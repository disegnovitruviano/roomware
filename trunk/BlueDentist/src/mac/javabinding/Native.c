#include <jniutil.h>
#include <jbluetooth.h>
#include <jni.h>

#include "NativeLocalDevice.h"
#include "NativeRemoteDevice.h"
#include "NativeDiscoveryAgent.h"


JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved) {
	jboolean success = JNI_TRUE;
	int status;

	if(success == JNI_TRUE) {
		status = jniutil_init(jvm);
		if(status != JNIUTIL_TRUE) {
			fprintf(stderr, "Failed to init the jniutil lib!\n");
			success = JNI_FALSE;
		}
	}

	if(success == JNI_TRUE) {
		status = jbluetooth_init();
		if(status != JBLUETOOTH_TRUE) {
			fprintf(stderr, "Failed to init the jbluetooth lib!\n");
			success = JNI_FALSE;
		}
	}

	if(success == JNI_TRUE) {
		status = NativeLocalDevice_init();
		if(status != JNI_TRUE) {
			fprintf(stderr, "Could not init NativeLocalDevice binding!\n");
			success = JNI_FALSE;
		}
	}

	if(success == JNI_TRUE) {
		status = NativeRemoteDevice_init();
		if(status != JNI_TRUE) {
			fprintf(stderr, "Could not init NativeRemoteDevice binding!\n");
			success = JNI_FALSE;
		}
	}

	if(success == JNI_TRUE) {
		status = NativeDiscoveryAgent_init();
		if(status != JNI_TRUE) {
			fprintf(stderr, "Could not init NativeDiscoveryAgent binding!\n");
			success = JNI_FALSE;
		}
	}

	return (success == JNI_TRUE)? jniutil_getRequiredJavaVersion(): -1;
}


JNIEXPORT void JNI_OnUnload(JavaVM *jvm, void *reserved) {
	jboolean jstatus;
	int status;

	jstatus = NativeDiscoveryAgent_destroy();
	if(jstatus != JNI_TRUE) {
		fprintf(stderr, "Could not destroy NativeDiscoveryAgent binding!\n");
	}

	jstatus = NativeRemoteDevice_destroy();
	if(jstatus != JNI_TRUE) {
		fprintf(stderr, "Could not destroy NativeRemoteDevice binding!\n");
	}

	jstatus = NativeLocalDevice_destroy();
	if(jstatus != JNI_TRUE) {
		fprintf(stderr, "Could not destroy NativeLocalDevice binding!\n");
	}

	status = jbluetooth_destroy();
	if(status != JBLUETOOTH_TRUE) {
		fprintf(stderr, "Could not destroy jbluetooth lib!\n");
	}

	status = jniutil_destroy();
	if(status != JNIUTIL_TRUE) {
		fprintf(stderr, "Could not destroy jniutil lib!\n");
	}
}
