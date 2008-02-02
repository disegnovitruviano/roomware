#ifndef _cfutil_h_
#define _cfutil_h_

#include <CoreFoundation/CoreFoundation.h>

#define CFUTIL_TRUE	1
#define CFUTIL_FALSE	0

int cfutil_unicode(CFStringRef string, void **bytes, size_t *chars);

int cfutil_createPool(void **pool);

int cfutil_destroyPool(void *pool);

int cfutil_retain(void* object);

int cfutil_release(void *object);

#endif
