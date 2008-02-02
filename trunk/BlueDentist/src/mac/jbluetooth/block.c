#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>

#include "jbluetooth.h"
#include "block.h"


static IOBluetoothDeviceInquiryRef inquiryRef;

#define STATUS_NEEDS_INIT		0
#define STATUS_READY			2
#define STATUS_ERROR			3
#define STATUS_INQUIRY_SETUP		4
#define STATUS_INQUIRY_BUSY		5
#define STATUS_INQUIRY_COMPLETED	6


static int device_status = STATUS_NEEDS_INIT;
static int device_access_code;
static DiscoveryListener active_listener;



static int reset_device() {
	device_status = STATUS_READY;
	return JBLUETOOTH_TRUE;
}


static int clean_inquiry() {
	int success = JBLUETOOTH_TRUE;
	IOReturn ret;

	ret = IOBluetoothDeviceInquiryDelete(inquiryRef);
	if(ret != kIOReturnSuccess) {
		fprintf(stderr, "Could not delete inquiry reference!\n");
		success = JBLUETOOTH_FALSE;
	}

	return success;
}

int block_init() {
	int success = JBLUETOOTH_TRUE;

	if(device_status == STATUS_NEEDS_INIT) {
		device_status = STATUS_READY;
	}
	else {
		fprintf(stderr, "Bluetooth device in wrong state!\n");
		success = JBLUETOOTH_FALSE;
	}

	return success;
}


int block_destroy() {
	int success = JBLUETOOTH_TRUE;

	if(device_status != STATUS_READY) {
		fprintf(stderr, "Bluetooth device in wrong state!\n");
		success = JBLUETOOTH_FALSE;
	} else {
		device_status = STATUS_NEEDS_INIT;
	}
		
	return success;
}


static void myIOBluetoothDeviceInquiryDeviceFoundCallback(
	void *userRefCon,
	IOBluetoothDeviceInquiryRef inquiryRef,
	IOBluetoothDeviceRef deviceRef
) {
	DiscoveryListener listener = (DiscoveryListener) userRefCon;
	BluetoothClassOfDevice deviceClass = IOBluetoothDeviceGetClassOfDevice(deviceRef);
	listener->deviceDiscovered(deviceRef, deviceClass);
}


static void myIOBluetoothDeviceInquiryCompleteCallback(
	void *userRefCon,
	IOBluetoothDeviceInquiryRef inquiryRef,
	IOReturn error,
	Boolean aborted
) {
	DiscoveryListener listener = (DiscoveryListener) userRefCon;
	int discType = JBLUETOOTH_INQUIRY_COMPLETED;

	if(error != kIOReturnSuccess) {
		discType = JBLUETOOTH_INQUIRY_ERROR;
	}
	else {
		if(aborted == TRUE) {
			discType = JBLUETOOTH_INQUIRY_TERMINATED;
		}
	}

	listener->inquiryCompleted(discType);

	device_status = STATUS_INQUIRY_COMPLETED;

	CFRunLoopStop(CFRunLoopGetCurrent());
}


static void myIOBluetoothDeviceInquiryStartedCallback(
	void *userRefCon,
	IOBluetoothDeviceInquiryRef inquiryRef
) {
	device_status = STATUS_INQUIRY_BUSY;
}



static int inquiry_create_and_setup(DiscoveryListener listener) {
	int success = JBLUETOOTH_TRUE;
	IOReturn ret;

	inquiryRef = IOBluetoothDeviceInquiryCreateWithCallbackRefCon(listener);

	ret = IOBluetoothDeviceInquirySetDeviceFoundCallback(inquiryRef,
			myIOBluetoothDeviceInquiryDeviceFoundCallback);
	if(ret != kIOReturnSuccess) {
		fprintf(stderr, "Could not set device found callback!\n");
		success = JBLUETOOTH_FALSE;
	}

	ret = IOBluetoothDeviceInquiryClearFoundDevices(inquiryRef);
	if(ret != kIOReturnSuccess) {
		fprintf(stderr, "Could not clear found devices!\n");
		success = JBLUETOOTH_FALSE;
	}

	ret = IOBluetoothDeviceInquirySetInquiryLength(inquiryRef, 40);
	if(ret != kIOReturnSuccess) {
		fprintf(stderr, "Could not set inquiry length 40s!\n");
		success = JBLUETOOTH_FALSE;
	}
	
	ret = IOBluetoothDeviceInquirySetCompleteCallback(inquiryRef,
			myIOBluetoothDeviceInquiryCompleteCallback);
	if(ret != kIOReturnSuccess) {
		fprintf(stderr, "Could not set complete callback!\n");
		success = JBLUETOOTH_FALSE;
	}

	ret = IOBluetoothDeviceInquirySetStartedCallback(inquiryRef,
			myIOBluetoothDeviceInquiryStartedCallback);
	if(ret != kIOReturnSuccess) {
		fprintf(stderr, "Could not set started callback!\n");
		success = JBLUETOOTH_FALSE;
	}

	if(success != JBLUETOOTH_TRUE) {
		clean_inquiry();
		reset_device();
	}

	return success;
}


static int post_inquiry() {
	int success = JBLUETOOTH_TRUE;
	IOReturn ret;

	ret = IOBluetoothDeviceInquiryStart(inquiryRef);
	if(ret != kIOReturnSuccess) {
		fprintf(stderr, "Could not start device inquiry!\n");
		success = JBLUETOOTH_FALSE;
	}

	return success;
}


int block_doInquiry() {
	int success = JBLUETOOTH_TRUE;
	int status = JBLUETOOTH_TRUE;

	if(device_status != STATUS_INQUIRY_BUSY) {
		fprintf(stderr, "Inquiry not initialized!\n");
		success = JBLUETOOTH_FALSE;
	}

	if(success == JBLUETOOTH_TRUE) {
		CFRunLoopRun();
	}

	if(device_status == STATUS_INQUIRY_COMPLETED && success == JBLUETOOTH_TRUE) {
		device_status = STATUS_READY;
		status = clean_inquiry();
		if(status != JBLUETOOTH_TRUE) {
			fprintf(stderr, "Could not clean inquiry!\n");
			success = JBLUETOOTH_FALSE;
		}
	} else {
		fprintf(stderr, "Device is in wrong state!\n");
		success = JBLUETOOTH_FALSE;
	}

	return success;
}


int block_setupInquiry(int accessCode, DiscoveryListener listener) {
	int success = JBLUETOOTH_TRUE;
	int status = JBLUETOOTH_TRUE;

	/* TODO IMPELEMNT CHECK ACCESSCODE */
	device_access_code = accessCode;

	if(device_status != STATUS_READY) {
		fprintf(stderr, "Device in wrong state!\n");
		success = JBLUETOOTH_FALSE;
	} else {
		device_status = STATUS_INQUIRY_SETUP;
	}

	if(success == JBLUETOOTH_TRUE) {
		status = inquiry_create_and_setup(listener);
		if(status != JBLUETOOTH_TRUE) {
			fprintf(stderr, "Could not setup inquiry!\n");
			success = JBLUETOOTH_FALSE;
		}
	}

	if(success == JBLUETOOTH_TRUE) {
		status = post_inquiry();
		if(status != JBLUETOOTH_TRUE) {
			fprintf(stderr, "Could not post inquiry!\n");
			reset_device();
			success = JBLUETOOTH_FALSE;
		}
	}

	if(success == JBLUETOOTH_TRUE) {
		active_listener = listener;
	}

	return success;
}


int block_cancelInquiry(DiscoveryListener listener) {
	int success = JBLUETOOTH_TRUE;
	IOReturn ret;

	if(listener->id != active_listener->id) {
		fprintf(stderr, "Wrong instance of discovery listener!\n");
		success = JBLUETOOTH_FALSE;
	}

	if(device_status != STATUS_INQUIRY_BUSY && success == JBLUETOOTH_TRUE) {
		fprintf(stderr, "Device is in wrong state!\n");
		success = JBLUETOOTH_FALSE;
	}

	if(success == JBLUETOOTH_TRUE) {
		ret = IOBluetoothDeviceInquiryStop(inquiryRef);
		if(ret != kIOReturnSuccess) {
			fprintf(stderr, "Could not stop device inquiry!\n");
			success = JBLUETOOTH_FALSE;
		}
	}

	return success;
}


CFStringRef block_queryRemoteDeviceName(IOBluetoothDeviceRef deviceRef) {
	CFStringRef name = NULL;
	IOReturn status;
	BluetoothDeviceName outDeviceName; /* UInt8 [256] */

	status = IOBluetoothDeviceRemoteNameRequest(deviceRef, NULL, NULL, outDeviceName);
	if(status != kIOReturnSuccess) {
		fprintf(stderr, "Remote name request failed!\n");
	} else {
		name = IOBluetoothDeviceGetName(deviceRef);
	}

	return name;
}
