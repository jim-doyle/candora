/*
 *
 * Copyright
 */
#include "org_ab1rw_candora_core_adapter_NativeSocketCANAdapter.h"
#include <jni.h>
#include <jni_md.h>
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/ioctl.h>
#include <net/if.h>
#include <linux/can.h>
#include <linux/can/raw.h>
#include <linux/sockios.h>
#include "logging.h"

/*
 * Throw a Java CANAdapterException from here back to the JVM
 */
void throwCANAdapterException(JNIEnv * env, char * reason, int errcode) {
  char * exBuffer;
  sprintf(exBuffer, "%s %d %s", reason, errcode, strerror(errcode));

  (*env)->ThrowNew(env,
		  (*env)->FindClass(env, "org/ab1rw/candora/core/CANAdapterException"),
		  exBuffer);
  
}

void throwCANReceiveTimeoutException(JNIEnv * env) {
  char * exBuffer;
  sprintf(exBuffer, "%d", 1);

  (*env)->ThrowNew(env,
		  (*env)->FindClass(env, "org/ab1rw/candora/core/CANReceiveTimeoutException"),
		  exBuffer);

}



JNIEXPORT jstring JNICALL Java_org_ab1rw_candora_core_adapter_NativeSocketCANAdapter_getVersionInfo
(JNIEnv * env, jobject object) {
  return  NULL;
}


JNIEXPORT void JNICALL Java_org_ab1rw_candora_core_adapter_NativeSocketCANAdapter_init
(JNIEnv * env, jobject object) {

  init_logging(env);

  // are we already initialized, if so log warn and return gracefully.
  
  const int s = socket(PF_CAN, SOCK_RAW, CAN_RAW);
  if (s < 0) {
    throwCANAdapterException(env, "socket()", errno);
  }

  // enable CAN FD frames 
   int enable = 1;
   if (setsockopt(s, SOL_CAN_RAW, CAN_RAW_FD_FRAMES,
		  &enable, sizeof(enable)) < 0) {
     throwCANAdapterException(env, "setsockopt(CAN_RAW_FD_FRAMES)", errno);
   }

  // take all errors
  can_err_mask_t err_mask = CAN_ERR_MASK;
  if (setsockopt(s, SOL_CAN_RAW, CAN_RAW_ERR_FILTER,
                 &err_mask, sizeof(can_err_mask_t)) < 0) {
    //    log but continue....
  }
  printf("startup: set socket opt CAN_RAW_ERR_FILTER\n");
  
  // create the socket
  // set various sock opts
  // store the socket fd on the java class instance
  // mark the ready bit
  // LOG_INFO
}

JNIEXPORT void JNICALL Java_org_ab1rw_candora_core_adapter_NativeSocketCANAdapter_close
(JNIEnv * env, jobject object) {
  // unflip the ready bit, atomically
  // close the socket
  // LOG_INFO  
}

JNIEXPORT void JNICALL Java_org_ab1rw_candora_core_adapter_NativeSocketCANAdapter_send
(JNIEnv * env, jobject obj, jobject arg1) {
  // call sendto
  // LOG_FINE before and after
  // update counters
}

JNIEXPORT jobject JNICALL Java_org_ab1rw_candora_core_adapter_NativeSocketCANAdapter_receive
(JNIEnv * env, jobject object) {
  // block on recvfrom, LOG_FINE before and after
  // unpack the packet, is it an error or a payload? is it can2 or canfd?
  // update counters
  return NULL;
}


