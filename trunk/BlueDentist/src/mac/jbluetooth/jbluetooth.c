#include "jbluetooth.h"
#include "event.h"

static void *pool;

int jbluetooth_init() {
	int success = JBLUETOOTH_TRUE;
	int status;

	if(success == JBLUETOOTH_TRUE) {
		status = cfutil_createPool(&pool);
		if(status != CFUTIL_TRUE) {
			fprintf(stderr, "Could not create autopool!\n");
			success = JBLUETOOTH_FALSE;
		}
	}

	if(success == JBLUETOOTH_TRUE) {
		status = event_init();
		if(status != JBLUETOOTH_TRUE) {
			fprintf(stderr, "Could not init event lib!\n");
			success = JBLUETOOTH_FALSE;
		}	
	}

	return success;
}


int jbluetooth_destroy() {
	int success = JBLUETOOTH_TRUE;
	int status;

	status = cfutil_destroyPool(pool);
	if(status != CFUTIL_TRUE) {
		fprintf(stderr, "Could not destroy autopool!\n");
		success = JBLUETOOTH_FALSE;
	}

	return event_destroy();
}


int jbluetooth_getLocalDevice() {
	int success = JBLUETOOTH_TRUE;
	IOReturn status;
	BluetoothHCIPowerState powerState;
/* BUG: This makes other threads crash if they access the Bluetooth API! */
/*
	Boolean value;
	value = IOBluetoothLocalDeviceAvailable();
	if(value != TRUE) {
		success = JBLUETOOTH_FALSE;
		fprintf(stderr, "No local device available!\n");
	}
*/
	if(success == JBLUETOOTH_TRUE) {
		status = IOBluetoothLocalDeviceGetPowerState(&powerState);
		if(status != kIOReturnSuccess) {
			fprintf(stderr, "Failed to get power state of bluetooth device!\n");
			success = JBLUETOOTH_FALSE;
		}
		else if(powerState != kBluetoothHCIPowerStateON) {
			fprintf(stderr, "Bluetooth device not powered on!\n");
			success = JBLUETOOTH_FALSE;
		}
	}

	return success;
}


int jbluetooth_getBluetoothAddress(BluetoothDeviceAddress *address) {
	int success = JBLUETOOTH_TRUE;
	IOReturn status;

	if(address == NULL) {
		fprintf(stderr, "Passed NULL pointer!!!\n");
		success = JBLUETOOTH_FALSE;
	}

	if(success == JBLUETOOTH_TRUE) {
		status = IOBluetoothLocalDeviceReadAddress(address, NULL, NULL, NULL);
		if(status != kIOReturnSuccess) {
			fprintf(stderr, "Could not read local device address!\n");
			success = JBLUETOOTH_FALSE;	
		}
	}

	return success;
}


int jbluetooth_startInquiry(int accessCode, DiscoveryListener listener) {
	int success = JBLUETOOTH_TRUE;
	int status = JBLUETOOTH_TRUE;

	if(success == JBLUETOOTH_TRUE) {
		status = event_startInquiry(accessCode, listener);
		if(status != JBLUETOOTH_TRUE) {
			fprintf(stderr, "could not start inquiry!\n");
			success = JBLUETOOTH_FALSE;
		}
	}

	return success;
}


int jbluetooth_cancelInquiry(DiscoveryListener listener) {
	return event_cancelInquiry(listener);
}


CFStringRef jbluetooth_getFriendlyName(IOBluetoothDeviceRef deviceRef, int alwaysAsk) {
	CFStringRef remoteDeviceName = NULL;

	if(alwaysAsk != JBLUETOOTH_TRUE) {
		remoteDeviceName = event_getFoundRemoteDeviceName(deviceRef);
	}

	if(remoteDeviceName == NULL) {
		remoteDeviceName = event_queryRemoteDeviceName(deviceRef);
	}

	return remoteDeviceName;
}
