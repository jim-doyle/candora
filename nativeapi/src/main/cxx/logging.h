// from https://gist.github.com/mgodave/1951986

#ifndef __LOGGING_H__
#define __LOGGING_H__

#include <jni.h>

typedef enum {
  SEVERE = 0,
  WARNING,
  INFO,
  CONFIG,
  FINE,
  FINER,
  FINEST
} log_level_t;

static jmethodID log_method_table[7];

#define JU_LOG(env, logger, level, msg) \
  env->CallVoidMethod(logger, log_method_table[level], env->NewStringUTF(msg))

#define LOG_SEVERE(env, logger, msg) JU_LOG(env, logger, SEVERE, msg)
#define LOG_WARNING(env, logger, msg) JU_LOG(env, logger, WARNING, msg)
#define LOG_INFO(env, logger, msg) JU_LOG(env, logger, INFO, msg)
#define LOG_CONFIG(env, logger, msg) JU_LOG(env, logger, CONFIG, msg)

#ifdef DEBUG

#define LOG_FINE(env, logger, msg) JU_LOG(env, logger, FINE, msg)
#define LOG_FINER(env, logger, msg) JU_LOG(env, logger, FINER, msg)
#define LOG_FINEST(env, logger, msg) JU_LOG(env, logger, FINEST, msg)

#else

#define LOG_FINE(env, logger, msg) fprintf(stdout,msg); fflush(stdout);
#define LOG_FINER(env, logger, msg) fprintf(stdout, msg); fflush(stdout);
#define LOG_FINEST(env, logger, msg) fprintf(stdout,msg); fflush(stdout);

#endif

/* configures the logger method dispatch table */
static void init_logging(JNIEnv *env, jclass logger)
{  
  log_method_table[SEVERE] = env->GetMethodID(logger, "severe", "(Ljava/lang/String;)V");
  log_method_table[WARNING] = env->GetMethodID(logger, "warning", "(Ljava/lang/String;)V");
  log_method_table[INFO] = env->GetMethodID(logger, "info", "(Ljava/lang/String;)V");
  log_method_table[CONFIG] = env->GetMethodID(logger, "config", "(Ljava/lang/String;)V");
  log_method_table[FINE] = env->GetMethodID(logger, "fine", "(Ljava/lang/String;)V");
  log_method_table[FINER] = env->GetMethodID(logger, "finer", "(Ljava/lang/String;)V");
  log_method_table[FINEST] = env->GetMethodID(logger, "finest", "(Ljava/lang/String;)V");
}

#endif
