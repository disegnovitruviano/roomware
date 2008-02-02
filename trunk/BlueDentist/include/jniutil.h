#ifndef _jniutil_h_
#define _jniutil_h_

#include <jni.h>

#define JNIUTIL_TRUE	1
#define JNIUTIL_FALSE	0


int jniutil_init(JavaVM *jvm);

int jniutil_destroy();

int jniutil_invokeVoidMethod(JNIEnv *env, jobject object, char *className, char *methodName, char *parameters, ...);

jstring jniutil_newUnicode(JNIEnv *env, void *bytes, size_t chars);

JNIEnv *jniutil_getThisThreadEnv();

jint jniutil_getRequiredJavaVersion();

#endif
