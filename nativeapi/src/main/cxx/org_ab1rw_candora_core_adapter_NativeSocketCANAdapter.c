/*
 *
 * Copyright
 * https://www.ibm.com/developerworks/library/j-jni/index.html#notc
 * https://stackoverflow.com/questions/230689/best-way-to-throw-exceptions-in-jni-code

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

#define log_msg_bufsz 128
static jobject logger;

/* Throw a Java CANAdapterException from here back to the JVM */
void throwCANAdapterException(JNIEnv * env, char * reason, int errcode) {
  char * arg1 = calloc(log_msg_bufsz);
  snprintf(arg1,  _exc_message_sz, "%s %d %s", reason, errcode, strerror(errcode));
  jclass exception =  (*env)->FindClass(env, "org/ab1rw/candora/core/CANAdapterException");
  (*env)->ThrowNew(env, exception, arg1);
}

/* Throw a Java CANReceiveTimeoutException */
void throwCANReceiveTimeoutException(JNIEnv * env) {
  char * arg1 = calloc(log_msg_bufsz);
  snprintf(arg1, log_msg_bufsz, "%s", "socket timeout while waiting on recvfrom()");
  jclass exception = (*env)->FindClass(env, "org/ab1rw/candora/core/CANReceiveTimeoutException");
  (*env)->ThrowNew(env,exception,arg1);
}

JNIEXPORT jstring JNICALL Java_org_ab1rw_candora_core_adapter_NativeSocketCANAdapter_getVersionInfo
(JNIEnv * env, jobject object) {
  return  NULL;
}


JNIEXPORT void JNICALL Java_org_ab1rw_candora_core_adapter_NativeSocketCANAdapter_init
(JNIEnv * env, jobject object) {

  init_logging(env);
  LOG_INFO(env, logger, "Initialized!");
  return;

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


