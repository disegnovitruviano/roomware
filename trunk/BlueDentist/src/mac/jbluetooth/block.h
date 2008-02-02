#ifndef _block_h_
#define _block_h_

#include "jbluetooth.h"


/************************************************************************

  This is the blocking bluetooth api for Mac OS X.

  For now the accessCode is ignored by inquiring!

 ************************************************************************/

int block_init(void);

int block_destroy(void);

int block_setupInquiry(int accessCode, DiscoveryListener listener);

int block_doInquiry(void);

int block_cancelInquiry(DiscoveryListener listener);

CFStringRef block_queryRemoteDeviceName(IOBluetoothDeviceRef deviceRef);

#endif
