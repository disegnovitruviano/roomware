#ifndef _jbluetooth_h_
#define _jbluetooth_h_


#define MAC_OS_X_VERSION_MIN_REQUIRED 1020
#define BLUETOOTH_VERSION_USE_CURRENT


#include <AvailabilityMacros.h>
#include <CoreFoundation/CoreFoundation.h>
#include <IOBluetooth/Bluetooth.h>
#include <IOBluetooth/IOBluetoothUserLib.h>

#include <cfutil.h>


/**********************************************************************
  This is the heart of the Mac native Bluetooth API.

  This API will provide the same functions and behaviour of the
  Java Bluetooth API. Only data types are not transformed. This
  should be done by the layer above.

 **********************************************************************/

#define JBLUETOOTH_TRUE						1
#define JBLUETOOTH_FALSE					0

#define JBLUETOOTH_GIAC						0x9E8B33
#define JBLUETOOTH_LIAC						0x9E8B00

#define JBLUETOOTH_INQUIRY_COMPLETED				0x00
#define JBLUETOOTH_INQUIRY_TERMINATED				0x05
#define JBLUETOOTH_INQUIRY_ERROR				0x07
#define JBLUETOOTH_SERVICE_SEARCH_COMPLETED			0x01
#define JBLUETOOTH_SERVICE_SEARCH_TERMINATED			0x02
#define JBLUETOOTH_SERVICE_SEARCH_ERROR				0x03
#define JBLUETOOTH_SERVICE_SEARCH_NO_RECORDS			0x04
#define JBLUETOOTH_SERVICE_SEARCH_DEVICE_NOT_REACHABLE		0x06


/*
  This struct mappes on to the Java DiscoveryListener.
  It only still uses the Macintosh Bluetooth types,
  those will be mapped by the functions pointed to.
*/
typedef struct DiscoveryLister_s {
	void (*deviceDiscovered)(IOBluetoothDeviceRef, BluetoothClassOfDevice);
	void (*inquiryCompleted)(int);
	int id;
} DiscoveryListener_t;

typedef DiscoveryListener_t* DiscoveryListener;



int jbluetooth_init(void);

int jbluetooth_destroy(void);

int jbluetooth_getLocalDevice(void);

int jbluetooth_getBluetoothAddress(BluetoothDeviceAddress *address);

int jbluetooth_startInquiry(int accessCode, DiscoveryListener listener);

int jbluetooth_cancelInquiry(DiscoveryListener listener);

CFStringRef jbluetooth_getFriendlyName(IOBluetoothDeviceRef deviceRef, int alwaysAsk);

#endif
