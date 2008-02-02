#include <stdlib.h>
#include <stdio.h>

#include <jbluetooth.h>
#include "../native/jbluetooth/block.h"


void completed(int discType) {
	printf("inquiry completed!\n");
}


void discovered(IOBluetoothDeviceRef deviceRef, BluetoothClassOfDevice clazz) {
	printf("device found!\n");
}


int main(int argc, char **args) {
	DiscoveryListener_t listener;
	int status;

	listener.deviceDiscovered = discovered;
	listener.inquiryCompleted = completed;
	listener.id = 0;

	status = jbluetooth_init();
	printf("init = %d\n", status);

	status = jbluetooth_getLocalDevice();
	printf("get local device = %d\n", status);

	status = block_setupInquiry(0, (DiscoveryListener)(&listener));
	printf("setup inquiry = %d\n", status);

	status = block_doInquiry();
	printf("do inquiry = %d\n", status);

	status = jbluetooth_destroy();
	printf("destroy = %d\n", status);

	exit(EXIT_SUCCESS);
}
