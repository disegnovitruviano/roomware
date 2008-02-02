#include <jniutil.h>
#include <jbluetooth.h>
#include <stdlib.h>
#include <cfutil.h>

#include "NativeRemoteDevice.h"


jboolean NativeRemoteDevice_init() {
	return JNI_TRUE;
}


jboolean NativeRemoteDevice_destroy() {
	return JNI_TRUE;
}


JNIEXPORT jstring JNICALL Java_javax_bluetooth_jni_NativeRemoteDevice_nativeGetFriendlyName(JNIEnv *env, jobject this, jboolean alwaysAsk, jlong deviceRef) {
	jboolean success = JNI_TRUE;
	int status;
	CFStringRef cfName = NULL;
	void *bytes = NULL;
	size_t bytesLength = 0;
	jstring jName = NULL;

	if(success == JNI_TRUE) {
		cfName = jbluetooth_getFriendlyName((IOBluetoothDeviceRef)((int)deviceRef), (alwaysAsk == JNI_TRUE)? JBLUETOOTH_TRUE: JBLUETOOTH_FALSE);
		if(cfName == NULL) {
			fprintf(stderr, "Could not get friendly name!\n");
			success = JNI_FALSE;
		}
	}

	if(success == JNI_TRUE) {
		status = cfutil_unicode(cfName, &bytes, &bytesLength);
		if(status != CFUTIL_TRUE) {
			fprintf(stderr, "Could not convert CFString!\n");
			success = JNI_FALSE;
		}
	}

	if(success == JNI_TRUE) {
		jName = jniutil_newUnicode(env, bytes, bytesLength);
		if(jName == NULL) {
			fprintf(stderr, "Could not create String!\n");
			success = JNI_FALSE;
		}
	}
	
	if(bytes != NULL) {
		free(bytes);
	}

	return jName;
}
