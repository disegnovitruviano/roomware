#ifndef _event_h_
#define _event_h_

#include "jbluetooth.h"

/************************************************************************

  This is the event driven (non blocking) bluetooth API for Mac OS X.

  This is implemented on top of the blocking API (block). These two
  together are the implemtation of the bluetooth API (jbluetooth).

 ************************************************************************/

int event_init(void);

int event_destroy(void);

int event_startInquiry(int accessCode, DiscoveryListener listener);

int event_cancelInquiry(DiscoveryListener listener);

CFStringRef event_getFoundRemoteDeviceName(IOBluetoothDeviceRef deviceRef);

CFStringRef event_queryRemoteDeviceName(IOBluetoothDeviceRef deviceRef);

#endif
