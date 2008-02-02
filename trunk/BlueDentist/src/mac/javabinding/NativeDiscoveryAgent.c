#include <jniutil.h>
#include <jbluetooth.h>

#include "NativeDiscoveryAgent.h"

#define BLUETOOTH_ADDRESS_LENGTH	13
#define BLUETOOTH_ADDRESS_UINT8_DIGITS	6

static DiscoveryListener discoveryListener;
static DiscoveryListener_t discoveryListenerRaw;
static jobject myJavaObjectReference;


static void myDeviceDiscovered(IOBluetoothDeviceRef deviceRef, BluetoothClassOfDevice classOfDevice) {
	jboolean success = JNI_TRUE;
	int status;
	const BluetoothDeviceAddress *address;
	char cAddressString[BLUETOOTH_ADDRESS_LENGTH];
	jstring javaAddressString;
	JNIEnv *env;
	int i;

	if(myJavaObjectReference == NULL) {
		fprintf(stderr, "I don't have a pointer to my Java object!\n");
		success = JNI_FALSE;
	}
	
	if(success == JNI_TRUE) {
		address = IOBluetoothDeviceGetAddress(deviceRef);

		for(i = 0; i < BLUETOOTH_ADDRESS_UINT8_DIGITS; i++) {
			status = snprintf(cAddressString + i * 2, 3, "%.2hx", address->data[i]);
			if(status >= 3) {
				perror("bluetooth address convert");
				success = JNI_FALSE;
			}
		}
	}

	if(success == JNI_TRUE) {
		env = jniutil_getThisThreadEnv();
		if(env == NULL) {
			fprintf(stderr, "Could not get this thread env!\n");
			success = JNI_FALSE;
		}
	}

	if(success == JNI_TRUE) {
		javaAddressString = (*env)->NewStringUTF(env, cAddressString);
		if(javaAddressString == NULL) {
			fprintf(stderr, "Could not construct java string!\n");
			success = JNI_FALSE;
		}
	}

	if(success == JNI_TRUE) {
		status = jniutil_invokeVoidMethod(env, myJavaObjectReference,
			"javax/bluetooth/jni/NativeDiscoveryAgent",
			"deviceDiscovered",
			"(JILjava/lang/String;)V",
			(jlong)((int)deviceRef), (jint) classOfDevice,
			javaAddressString);
		if(status != JNIUTIL_TRUE) {
			fprintf(stderr, "Could not invoke deviceDiscovered method!\n");
			success = JNI_FALSE;
		}
	}
}


static void myInquiryCompleted(int discType) {
	jboolean success = JNI_TRUE;
	int status;
	JNIEnv *env;

	if(myJavaObjectReference == NULL) {
		fprintf(stderr, "I don't have a pointer to my Java object!\n");
		success = JNI_FALSE;
	}

	if(success == JNI_TRUE) {
		env = jniutil_getThisThreadEnv();
		if(env == NULL) {
			fprintf(stderr, "I could not fetch my JNIEnv!\n");
			success = JNI_FALSE;
		}
	}

	if(success == JNI_TRUE) {
		status = jniutil_invokeVoidMethod(env, myJavaObjectReference,
			"javax/bluetooth/jni/NativeDiscoveryAgent",
			"inquiryCompleted", "(I)V",
			(jint) discType);
		if(status != JNIUTIL_TRUE) {
			fprintf(stderr, "Could not invoke inquiryCompleted method!\n");
			success = JNI_FALSE;
		}
	}

	if(success == JNI_TRUE) {
		(*env)->DeleteGlobalRef(env, myJavaObjectReference);
	}
}


jboolean NativeDiscoveryAgent_init() {
	discoveryListenerRaw.deviceDiscovered = myDeviceDiscovered;
	discoveryListenerRaw.inquiryCompleted = myInquiryCompleted;
	discoveryListenerRaw.id = (int)&discoveryListenerRaw;
	discoveryListener = (DiscoveryListener)&discoveryListenerRaw;
	return JNI_TRUE;
}


jboolean NativeDiscoveryAgent_destroy() {
	return JNI_TRUE;
}


JNIEXPORT jboolean JNICALL Java_javax_bluetooth_jni_NativeDiscoveryAgent_nativeStartInquiry(JNIEnv *env, jobject this, jint accessCode) {
	jboolean success = JNI_TRUE;
	int status;

	if(success == JNI_TRUE) {
		myJavaObjectReference = (*env)->NewGlobalRef(env, this);
		if(myJavaObjectReference == NULL) {
			fprintf(stderr, "Could not create new global reference!\n");
			success = JNI_FALSE;
		}
	}

	if(success == JNI_TRUE) {
		status = jbluetooth_startInquiry((int)accessCode, discoveryListener);
		if(status != JBLUETOOTH_TRUE) {
			fprintf(stderr, "Could not start inquiry!\n");
			success = JNI_FALSE;
		}
	}

	return success;
}


JNIEXPORT jboolean JNICALL Java_javax_bluetooth_jni_NativeDiscoveryAgent_nativeCancelInquiry(JNIEnv *env, jobject this) {
	jboolean success = JNI_TRUE;
	int status;

	if(success == JNI_TRUE) {
		status = jbluetooth_cancelInquiry(discoveryListener);

		if(status != JBLUETOOTH_TRUE) {
			fprintf(stderr, "Failed to cancel inquiry!\n");
			success = JNI_FALSE;
		}
	}
	
	return success;
}
