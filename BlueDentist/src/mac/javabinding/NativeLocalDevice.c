#include <jniutil.h>
#include <jbluetooth.h>
#include <stdlib.h>
#include <cfutil.h>

#include "NativeLocalDevice.h"


#define BLUETOOTH_ADDRESS_LENGTH        13
#define BLUETOOTH_ADDRESS_UINT8_DIGITS  6


jboolean NativeLocalDevice_init() {
	return JNI_TRUE;
}


jboolean NativeLocalDevice_destroy() {
	return JNI_TRUE;
}


JNIEXPORT jboolean JNICALL Java_javax_bluetooth_jni_NativeLocalDevice_nativeGetLocalDevice(JNIEnv *env, jobject this) {
	int status = jbluetooth_getLocalDevice();
	return (status == JBLUETOOTH_TRUE)? JNI_TRUE: JNI_FALSE;
}


JNIEXPORT jstring JNICALL Java_javax_bluetooth_jni_NativeLocalDevice_nativeGetBluetoothAddress(JNIEnv *env, jobject this) {
	jboolean success = JNI_TRUE;
	int status = JBLUETOOTH_TRUE;
	int i;
	BluetoothDeviceAddress address;
	jstring javaAddressString;
	char cAddressString[BLUETOOTH_ADDRESS_LENGTH];

	if(success == JNI_TRUE) {
		status = jbluetooth_getBluetoothAddress(&address);
		if(status != JNI_TRUE) {
			fprintf(stderr, "Could not obtain bluetooth address!\n");
			success = JNI_FALSE;
		}
	}

	if(success == JNI_TRUE) {
		for(i = 0; i < BLUETOOTH_ADDRESS_UINT8_DIGITS; i++) {
			status = snprintf(cAddressString + i * 2, 3, "%.2hx", address.data[i]);
			if(status >= 3) {
				perror("bluetooth address convert");
				success = JNI_FALSE;
			}
		}
	}

	if(success == JNI_TRUE) {
		javaAddressString = (*env)->NewStringUTF(env, cAddressString);
		if(javaAddressString == NULL) {
			fprintf(stderr, "Could not construct java string!\n");
			success = JNI_FALSE;
		}
	}

	return (success == JNI_TRUE)? javaAddressString: NULL;
}


/*
JNIEXPORT jstring JNICALL Java_javax_bluetooth_jni_NativeLocalDevice_getFriendlyName(JNIEnv *env, jobject this, jboolean alwaysAsk, jlong deviceRef) {
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
*/
