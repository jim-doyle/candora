/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_ab1rw_candora_core_adapter_NativeSocketCANAdapter */

#ifndef _Included_org_ab1rw_candora_core_adapter_NativeSocketCANAdapter
#define _Included_org_ab1rw_candora_core_adapter_NativeSocketCANAdapter
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_ab1rw_candora_core_adapter_NativeSocketCANAdapter
 * Method:    getVersionInfo
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_ab1rw_candora_core_adapter_NativeSocketCANAdapter_getVersionInfo
  (JNIEnv *, jobject);

/*
 * Class:     org_ab1rw_candora_core_adapter_NativeSocketCANAdapter
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_ab1rw_candora_core_adapter_NativeSocketCANAdapter_init
  (JNIEnv *, jobject);

/*
 * Class:     org_ab1rw_candora_core_adapter_NativeSocketCANAdapter
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_ab1rw_candora_core_adapter_NativeSocketCANAdapter_close
  (JNIEnv *, jobject);

/*
 * Class:     org_ab1rw_candora_core_adapter_NativeSocketCANAdapter
 * Method:    send
 * Signature: (Lorg/ab1rw/candora/core/adapter/NativeCANFrame;)V
 */
JNIEXPORT void JNICALL Java_org_ab1rw_candora_core_adapter_NativeSocketCANAdapter_send
  (JNIEnv *, jobject, jobject);

/*
 * Class:     org_ab1rw_candora_core_adapter_NativeSocketCANAdapter
 * Method:    receive
 * Signature: ()Lorg/ab1rw/candora/core/adapter/NativeCANFrame;
 */
JNIEXPORT jobject JNICALL Java_org_ab1rw_candora_core_adapter_NativeSocketCANAdapter_receive
  (JNIEnv *, jobject);

/*
 * Class:     org_ab1rw_candora_core_adapter_NativeSocketCANAdapter
 * Method:    poll
 * Signature: ()Lorg/ab1rw/candora/core/adapter/NativeCANFrame;
 */
JNIEXPORT jobject JNICALL Java_org_ab1rw_candora_core_adapter_NativeSocketCANAdapter_poll
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif
