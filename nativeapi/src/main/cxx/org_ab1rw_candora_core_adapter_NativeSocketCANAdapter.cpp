/*
 *
 * Copyright 2017 James R. Doyle, AB1RW
 * 
 * Reading Recommendations:
 * https://www.ibm.com/developerworks/library/j-jni/index.html#notc
 * https://stackoverflow.com/questions/230689/best-way-to-throw-exceptions-in-jni-code
 * http://www.math.uni-hamburg.de/doc/java/tutorial/native1.1/implementing/array.html
 */
using namespace org.ab1rw.candora;

#include "org_ab1rw_candora_core_adapter_NativeSocketCANAdapter.h"
#include <jni.h>
#include <jni_md.h>
#include <assert.h>
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <stdlib.h>
#include <stdbool.h>
#include <unistd.h>
#include <sys/ioctl.h>
#include <net/if.h>
#include <linux/can.h>
#include <linux/can/raw.h>
#include <linux/sockios.h>

#define log_msg_bufsz 128
extern const char *gitversion;
static bool atMostOnceInit = false;

// cache JNI classes, fieldIDs, and method IDs after init() to avoid expensive "reach backs" into JVM
// The cached items are global to the adapter.  These items should
static struct {
  jclass clsNativeSocketCANAdapter;
  jclass clsCANAdapterException;
  jclass clsCANReceiveTimeoutException;
  jclass clsCANNativeFrame;
} jniClassCache;

static struct {
  jfieldID fldSocket;
  jfieldID fldReadyFlag;
  jfieldID fldUseAllInterfaces;
  jfieldID fldUseOnlyInterfaceId;
} jniNativeAdapterCache;
  
static struct {
  jfieldID can_id;
  jfieldID can_dlc;
  jfieldID can_fd_flags;
  jfieldID reserved0, reserved1;
  jfieldID can_data;
  jfieldID effFlag, errFlag;
  jfieldID timestamp;
  jfieldID canInterface;
} jniNativeFrameCache;

/* Throw a Java CANAdapterException from here back to the JVM */
void throwCANAdapterException(JNIEnv * env, const char * reason, int errcode) {
  char * arg1 = (char *)calloc(log_msg_bufsz, sizeof(char));
  snprintf(arg1,  log_msg_bufsz, "%s %d %s", reason, errcode, strerror(errcode));
  jclass exception =  env->FindClass("org/ab1rw/candora/core/CANAdapterException");
  assert (exception != NULL);
  env->ThrowNew(exception, arg1);
}


/* Throw a Java CANReceiveTimeoutException */
void throwCANReceiveTimeoutException(JNIEnv * env) {
  char * arg1 = (char *)calloc(log_msg_bufsz, sizeof(char));
  snprintf(arg1, log_msg_bufsz, "%s", "socket timeout while waiting on recvfrom()");
  jclass exception = env->FindClass("org/ab1rw/candora/core/CANReceiveTimeoutException");
  env->ThrowNew(exception, arg1);
}

/* getVersion Implements Java Native Method */
JNIEXPORT jstring JNICALL Java_org_ab1rw_candora_core_adapter_NativeSocketCANAdapter_getVersionInfo
(JNIEnv * env, jobject object) {
  return env->NewStringUTF(gitversion);
}


/* Implements Java Native Method */
JNIEXPORT void JNICALL Java_org_ab1rw_candora_core_adapter_NativeSocketCANAdapter_init
(JNIEnv * env, jobject object) {

  jclass cls = env->GetObjectClass(object);
 
  // first time through, capture and cache all the static JNI metadata. This not only makes
  // the code more efficient at runtime, but also much easier to read and maintain
  if (! atMostOnceInit) {
    // cache all the java classes used in the native adapter.
    jniClassCache.clsCANNativeFrame =  env->FindClass("org/ab1rw/candora/core/adapter/NativeCANFrame");
    jniClassCache.clsCANReceiveTimeoutException = env->FindClass("org/ab1rw/candora/core/CANReceiveTimeoutException");
    jniClassCache.clsCANAdapterException = env->FindClass("org/ab1rw/candora/core/CANAdapterException");


    // cache the inner part of the NativeAdapter
    jniClassCache.clsNativeSocketCANAdapter = cls;
    jniNativeAdapterCache.fldReadyFlag = env->GetFieldID(cls,"adapterReady","Z");
    jniNativeAdapterCache.fldSocket = env->GetFieldID(cls,"socket","I");
    jniNativeAdapterCache.fldUseAllInterfaces =  env->GetFieldID(cls,"useAllInterfaces","Z");
    jniNativeAdapterCache.fldUseOnlyInterfaceId = env->GetFieldID(cls,"useOnlyInterfaceId","Ljava/lang/String;");
    if (env->ExceptionOccurred()) return;
    
    // cache the inner parts of the CANNativeFrame value object
    jclass nf = jniClassCache.clsCANNativeFrame;
    jniNativeFrameCache.can_id = env->GetFieldID(nf,"can_id","I");
    jniNativeFrameCache.can_dlc = env->GetFieldID(nf,"can_dlc","B");
    jniNativeFrameCache.can_fd_flags = env->GetFieldID(nf,"can_fd_flags","B");
    jniNativeFrameCache.can_data = env->GetFieldID(nf,"can_data","[B");
    jniNativeFrameCache.effFlag = env->GetFieldID(nf,"effFlag","Z");
    jniNativeFrameCache.errFlag = env->GetFieldID(nf,"errFlag","Z");
    jniNativeFrameCache.timestamp = env->GetFieldID(nf,"timestamp","I");
    jniNativeFrameCache.canInterface = env->GetFieldID(nf,"canInterface","Ljava/lang/String;");
    if (env->ExceptionOccurred()) return;
    atMostOnceInit = true;
  }


  if (env->GetBooleanField(object, jniNativeAdapterCache.fldReadyFlag) == true) {
    return;  // already initialized, just return....
  }

  // new raw socket
  const int s = socket(PF_CAN, SOCK_RAW, CAN_RAW);
  if (s < 0) {
    return throwCANAdapterException(env, "socket()", errno);
  }
    
   // enable support for mutual CAN 2.0x and CAN FD frames
   int enable = 1;
   if (setsockopt(s, SOL_CAN_RAW, CAN_RAW_FD_FRAMES,
		  &enable, sizeof(enable)) < 0) {
     close(s);
     return throwCANAdapterException(env, "error when setsockopt(CAN_RAW_FD_FRAMES)", errno);
   }

  // enable receive of all error frames
  can_err_mask_t err_mask = CAN_ERR_MASK;
  if (setsockopt(s, SOL_CAN_RAW, CAN_RAW_ERR_FILTER,
                 &err_mask, sizeof(can_err_mask_t)) < 0) {
    close(s);
    return throwCANAdapterException(env, "error when setsockopt(CAN_RAW_ERR_FRAMES)", errno);
  }

  // setup the receive/poll timeout, so long as there is a non-zero value present.
  jfieldID f_to_s = env->GetFieldID(cls,"recvTimeoutSeconds","I");
  jfieldID f_to_us = env->GetFieldID(cls,"recvTimeoutMicroseconds","I");
  assert (f_to_us != NULL && f_to_us != NULL);
  jint to_s = env->GetIntField(object, f_to_s);
  jint to_us = env->GetIntField(object, f_to_us);
  if (to_s > 0 || to_us > 0) {  // blocking read with a timeout
    struct timeval recv_timeout;
    recv_timeout.tv_sec=to_s;
    recv_timeout.tv_usec=to_us;
    if (setsockopt(s, SOL_SOCKET, SO_RCVTIMEO, (const char *)&recv_timeout,
                 sizeof(struct timeval)) <0 ) {
      close(s);
      return throwCANAdapterException(env, "error when setsockopt(SO_RCVTIMEO)", errno);\
    }

  } else {
    // no timeout means a blocking read that may never return
  }

  // Interface bind
  jboolean useAllInterfaces = env->GetBooleanField(object, jniNativeAdapterCache.fldUseAllInterfaces);
  struct ifreq ifr;
  struct sockaddr_can addr;
  addr.can_family = AF_CAN;
  if (! useAllInterfaces) {

      // next 6 lines, ritual to get the interface name from the Adapter Java object so we can pass it to struct ifreq!
      jstring ifc = (jstring) env->GetObjectField(object, jniNativeAdapterCache.fldUseOnlyInterfaceId);
      jsize len = env->GetStringLength(ifc);
      jboolean iscopy = true;
      const char * foo = env->GetStringUTFChars(ifc, &iscopy);
      strncpy(ifr.ifr_name, foo , len );
      env->ReleaseStringUTFChars(ifc, foo); 

      if (ioctl(s, SIOCGIFINDEX, &ifr) < 0) {
            close(s);
            return throwCANAdapterException(env, "error when ioctl(SIOCGIFINDEX)", errno);
      }
      addr.can_ifindex = ifr.ifr_ifindex;
  } else {
    addr.can_ifindex = 0;  // To bind on all CAN interface, the interface index is set to zero.
  }
  
  if (bind(s, (struct sockaddr *)&addr, sizeof(addr)) < 0) {
        close(s);
        return throwCANAdapterException(env, "bind()", errno);    
  }

  // Store back into the native object the unix handles (socket) needed to service future method calls...
  env->SetIntField(object, jniNativeAdapterCache.fldSocket, s);
  env->SetBooleanField(object, jniNativeAdapterCache.fldReadyFlag, true);
}

/* close()  Implements Java Native Method */
JNIEXPORT void JNICALL Java_org_ab1rw_candora_core_adapter_NativeSocketCANAdapter_close
(JNIEnv * env, jobject object) {

  if (env->GetBooleanField(object, jniNativeAdapterCache.fldReadyFlag) == false) {
    return; // already closed, do no more.
  }
  // Mark as not ready, then close the socket.
  env->SetBooleanField(object, jniNativeAdapterCache.fldReadyFlag, false);
  jint s = env->GetIntField(object, jniNativeAdapterCache.fldSocket);
  if (close(s)<0) {
    fprintf(stderr,"Unexpected errno %d while closing socket %d : %s\n",errno,s,strerror(errno)); 
  }
}

/* send() Implements Java Native Method */
JNIEXPORT void JNICALL Java_org_ab1rw_candora_core_adapter_NativeSocketCANAdapter_send (JNIEnv * env, jobject object, jobject arg1) {

  if (env->GetBooleanField(object, jniNativeAdapterCache.fldReadyFlag) == false) {
        return throwCANAdapterException(env, "NativeAdapter not initialized or has been closed.", 0);
  }
  
  jint socket = env->GetIntField(object, jniNativeAdapterCache.fldSocket);
  
  struct canfd_frame txframe;
  txframe.can_id = env->GetIntField(arg1, jniNativeFrameCache.can_id);
  txframe.len = env->GetByteField(arg1, jniNativeFrameCache.can_dlc);
  txframe.flags = env->GetByteField(arg1, jniNativeFrameCache.can_fd_flags);  

  jobject tmp = env->GetObjectField(arg1, jniNativeFrameCache.can_data);
  jbyteArray * byteArray = reinterpret_cast<jbyteArray *>(&tmp);
  jbyte * candata = env->GetByteArrayElements(*byteArray,0);
  assert(txframe.len <= CANFD_MAX_DLEN);
  bcopy(candata, txframe.data, txframe.len);
  env->ReleaseByteArrayElements(*byteArray, candata, 0);


  // To write CAN frames on sockets bound to 'any' CAN interface the
  // outgoing interface has to be defined certainly.
 
  struct sockaddr_can addr;
  struct ifreq ifr;
  strcpy(ifr.ifr_name, "can0")   // XXX HACK - fix me! ;
  ioctl(socket, SIOCGIFINDEX, &ifr);
  addr.can_family = AF_CAN;
  addr.can_ifindex = ifr.ifr_ifindex;
  
  if (sendto(socket, &txframe, sizeof(struct can_frame), 0,
	     (struct sockaddr*)&addr, sizeof(addr)) < 0) {
    return throwCANAdapterException(env, "sendto()", errno);    
  }
  bzero(&txframe.data, txframe.len); 
}

/*  receive() Implements Java Native Method */
JNIEXPORT void JNICALL Java_org_ab1rw_candora_core_adapter_NativeSocketCANAdapter_receive
(JNIEnv * env, jobject object, jobject arg1) {

  if (env->GetBooleanField(object, jniNativeAdapterCache.fldReadyFlag) == false) {
        return throwCANAdapterException(env, "NativeAdapter not initialized or has been closed.", 0);
  }

  // Unlike above, we do not close the socket on receipt of a unix errno...

  jint socket = env->GetIntField(object, jniNativeAdapterCache.fldSocket);

  struct canfd_frame rxframe;
  bzero(&rxframe.data, 8);

  struct sockaddr_can addr;   // XXX - need to use the same sockaddr from bind? We'll find out.
  socklen_t len = sizeof(addr);
  int nbytes = recvfrom(socket, &rxframe, sizeof(struct canfd_frame),
                    0, (struct sockaddr*)&addr, &len);
  if (nbytes < 0) {
    int e = errno;
    if (e == EAGAIN) {
      return throwCANReceiveTimeoutException(env);
    }
    return throwCANAdapterException(env, "while doing system call recvfrom() on socket", errno);
  }

  // We've successfully received a packet ; extract the recv timestamp
  //assert(errno == 0);
  struct timeval recv_timestamp;
  if (ioctl(socket, SIOCGSTAMP, &recv_timestamp)<0) {

  }

  // Extract the interface that this request came in from
  struct ifreq ifr;
  ifr.ifr_ifindex = addr.can_ifindex;
  if (ioctl(socket, SIOCGIFNAME, &ifr)<0) {
    // what to do ?
  }


  jstring ifname = env->NewStringUTF(ifr.ifr_name);
  env->SetObjectField(arg1, jniNativeFrameCache.canInterface, ifname);
  env->SetIntField(arg1, jniNativeFrameCache.can_id, rxframe.can_id);
  env->SetByteField(arg1, jniNativeFrameCache.can_dlc, rxframe.len);
  env->SetByteField(arg1, jniNativeFrameCache.can_fd_flags, rxframe.flags);
  env->SetIntField(arg1, jniNativeFrameCache.timestamp, recv_timestamp.tv_usec);    

  // copy can payload from c struct > java array
  jbyteArray newBuffer = env->NewByteArray(rxframe.len);
  jbyte * bytes  = env->GetByteArrayElements(newBuffer,0);
  bcopy(rxframe.data, bytes, rxframe.len);
  env->SetObjectField(arg1,jniNativeFrameCache.can_data,newBuffer);
  env->ReleaseByteArrayElements(newBuffer, bytes, 0);
  env->DeleteLocalRef(newBuffer);
  bzero(&rxframe.data, rxframe.len);

}

