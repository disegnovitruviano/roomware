#include <AppKit/AppKit.h>

#include "cfutil.h"


int cfutil_createPool(void **pool_p) {
	int success = CFUTIL_TRUE;
	void *pool;

	pool = [[NSAutoreleasePool alloc]init];
	if(pool == NULL) {
		fprintf(stderr, "Could not allocate auto release pool!\n");
		success = CFUTIL_FALSE;
	} else {
		*pool_p = pool;
	}
		
        return success;
}


int cfutil_destroyPool(void *pool) {
	return cfutil_release(pool);
}


int cfutil_retain(void *object) {
	int success = CFUTIL_TRUE;

	if(object == NULL) {
		fprintf(stderr, "Got NULL pointer!\n");
		success = CFUTIL_FALSE;
	} else {
		[(id)object retain];
	}

	return success;
}


int cfutil_release(void *object) {
	int success = CFUTIL_TRUE;

	if(object == NULL) {
		fprintf(stderr, "Got NULL pointer!\n");
		success = CFUTIL_FALSE;
	} else {
		[(id)object release];
	}

	return success;
}


int cfutil_unicode(CFStringRef string, void **outBytes, size_t *outChars) {
	int success = CFUTIL_TRUE;
	CFIndex stringLength;
	CFRange range;
	UniChar *stringChars;
	size_t bytesLength;

	if(string == NULL) {
		fprintf(stderr, "Can't convert NULL CFStringRef!\n");
		success = CFUTIL_FALSE;
	}

	if(success == CFUTIL_TRUE) {
		stringLength = CFStringGetLength(string);
		bytesLength = (size_t)(stringLength * 2 + 1);
		stringChars = (UniChar*)malloc(bytesLength);
		if(stringChars == NULL) {
			perror("malloc");
			success = CFUTIL_FALSE;
		}
	}

	if(success == CFUTIL_TRUE) {
		range.location = 0;
		range.length = stringLength;
		CFStringGetCharacters(string, range, stringChars);
	}
	
	if(success == CFUTIL_TRUE) {
		*outBytes = (void*)stringChars;
		*outChars = stringLength; 
	} else {
		*outBytes = NULL;
		*outChars = 0;
	}

	return success;
}
