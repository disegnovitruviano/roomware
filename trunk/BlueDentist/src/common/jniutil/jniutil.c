#include <stdio.h>
#include <pthread.h>
#include <hashtable.h>
#include "jniutil.h"


/* will point to the JNINativeInterface function table */
static JavaVM *javaVM;

/* we have to maintain a map of thread and env mappings */
static struct hashtable *threadEnvMapping;

/* This is the minimal Java version to work with */
#define MIN_REQUIRED_JAVA_VERSION	JNI_VERSION_1_4


#define HASH_SIZE 	16


static unsigned int myHash(void *key) {
	return (unsigned int)(*((pthread_t*)key));
}


static int myEquals(void *key1, void *key2) {
	return ((*(unsigned int*)key1) - (*(unsigned int*)key2));
}


static JNIEnv *createEnvForThisThread() {
	jint status;
	JNIEnv *env;

	/* should check this out more!!! */
	status = (*javaVM)->AttachCurrentThreadAsDaemon(javaVM, (void**)&env, NULL);
	if(status != 0) {
		fprintf(stderr, "Could not attach current thread!\n");
		env = NULL;
	}

	return env;
}


JNIEnv *jniutil_getThisThreadEnv() {
	int status;
	pthread_t self;
	JNIEnv* env;
	self = pthread_self();

	env = (JNIEnv*)hashtable_search(threadEnvMapping, (void*)&self);
	if(env == NULL) {
		env = createEnvForThisThread();
		if(env == NULL) {
			fprintf(stderr, "Could not create env!\n");
		}
		status = hashtable_insert(threadEnvMapping, &self, env);
		if(status == 0) {
			fprintf(stderr, "Could not store env!\n");
		}
	}

	return env;
}


int jniutil_init(JavaVM *jvm) {
	int success = JNIUTIL_TRUE;
	jint status;
	JNIEnv *env;
	jint jvmVersion;

	javaVM = jvm;

	if(success == JNIUTIL_TRUE) {
		status = (*jvm)->GetEnv(javaVM, (void**)&env, MIN_REQUIRED_JAVA_VERSION);
		switch(status) {
			case(JNI_OK):
				break;

			case(JNI_EDETACHED):
				fprintf(stderr, "thread deteched from the VM!\n");
				success = JNIUTIL_FALSE;
				break;

			case(JNI_EVERSION):
				fprintf(stderr, "got version error from VM!\n");
				success = JNIUTIL_FALSE;
				break;

			default:
				fprintf(stderr, "GetEnv gives wrong return value!\n");
				success = JNIUTIL_FALSE;
				break;
		}
	}

	if(success == JNIUTIL_TRUE) {
		jvmVersion = (*env)->GetVersion(env);

		switch(jvmVersion) {

			case(JNI_EDETACHED):
				fprintf(stderr, "thread deteched from the VM!\n");
				success = JNIUTIL_FALSE;
				break;

			case(JNI_EVERSION):
				fprintf(stderr, "got version error from VM!\n");
				success = JNIUTIL_FALSE;
				break;

			default:
				if(jvmVersion < MIN_REQUIRED_JAVA_VERSION) {
					fprintf(stderr, "wrong JVM version!\n");
					success = JNIUTIL_FALSE;
				}
				break;
		}
	}

	if(success == JNIUTIL_TRUE) {
		threadEnvMapping = create_hashtable(HASH_SIZE, myHash, myEquals);
		if(threadEnvMapping == NULL) {	
			fprintf(stderr, "could not create hashmap!\n");
			success = JNIUTIL_FALSE;
		}
	}

	return success;
}


int jniutil_destroy() {
	hashtable_destroy(threadEnvMapping, 0);
	return JNIUTIL_TRUE;
}


jint jniutil_getRequiredJavaVersion() {
	return MIN_REQUIRED_JAVA_VERSION;
}


int jniutil_invokeVoidMethod(JNIEnv *env, jobject object, char *className, char *methodName, char *parameters, ...) {
	int success = JNIUTIL_TRUE;
	jboolean exception;
	jclass clazz;
	jmethodID mid;
	va_list args;


	if(className == NULL || methodName == NULL || parameters == NULL ||
object == NULL) {
		fprintf(stderr, "InvokeMethod got NULL pointer!\n");
		success = JNIUTIL_FALSE;
	}

	if(env == NULL) {
		env = jniutil_getThisThreadEnv();
		if(env == NULL) {
			success = JNIUTIL_FALSE;
		}
	}

	if(success == JNIUTIL_TRUE) {
		clazz = (*env)->FindClass(env, className);
		if(clazz == NULL) {
			fprintf(stderr, "Could not find class!\n");
			success = JNIUTIL_FALSE;
		}
		else {
			exception = (*env)->ExceptionCheck(env);
			if(exception == JNI_TRUE) {
				(*env)->ExceptionClear(env);
				fprintf(stderr, "Exception occurred during class search!\n");
				success = JNI_FALSE;
			}
		}
	}

	if(success == JNIUTIL_TRUE) {
		mid = (*env)->GetMethodID(env, clazz, methodName, parameters);
		if(mid == NULL) {
			fprintf(stderr, "Could not find method!\n");
			success = JNIUTIL_FALSE;
		} else {
			exception = (*env)->ExceptionCheck(env);
			if(exception == JNI_TRUE) {
				(*env)->ExceptionClear(env);
				fprintf(stderr, "Exception occurred during method search!\n");
				success = JNIUTIL_FALSE;
			}
		}
	}

	if(success == JNIUTIL_TRUE) {
		va_start(args, parameters);
		(*env)->CallVoidMethodV(env, object, mid, args);
		exception = (*env)->ExceptionCheck(env);
		if(exception == JNI_TRUE) {
			(*env)->ExceptionDescribe(env);
			(*env)->ExceptionClear(env);
			fprintf(stderr, "Error while invoking void method!\n");
			success = JNIUTIL_FALSE;
		}
	}

	return success;
}


jstring jniutil_newUnicode(JNIEnv *env, void *bytes, size_t bytesLength) {
	jboolean success = JNI_TRUE;
	jboolean exception = JNI_FALSE;
	jstring string = NULL;
	
	if(success == JNI_TRUE) {
		string = (*env)->NewString(env, (jchar*)bytes, (jsize)bytesLength);
		exception = (*env)->ExceptionCheck(env);
		if(exception == JNI_TRUE) {
			(*env)->ExceptionDescribe(env);
			(*env)->ExceptionClear(env);
			fprintf(stderr, "Error while creating string form unicode bytes!\n");
			success = JNIUTIL_FALSE;
		}
		if(string == NULL) {
			fprintf(stderr, "Could not create string!\n");
			success = JNIUTIL_FALSE;
		}
	}

	return string;
}
