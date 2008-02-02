CC = gcc
CFLAGS = -g -Wall -Iinclude
JAVAC = javac
JFLAGS = -cp src

all: setup cfutil jbluetooth hashtable jniutil javabinding sharedlib java-bluetooth javalib

c: cfutil jbluetooth hashtable jniutil javabinding sharedlib

java: java-bluetooth javalib

test: javatest ctest

javatest:
	$(JAVAC) $(JFLAGS) src/test/Test.java

ctest:
	$(CC) $(CFLAGS) src/test/Test.c -o bin/ctest -Iinclude build/jbluetooth/jbluetooth.o build/jbluetooth/block.o build/jbluetooth/event.o -framework CoreFoundation -framework IOBluetooth

setup:
	mkdir -p build/jbluetooth
	mkdir -p build/hashtable
	mkdir -p build/cfutil
	mkdir -p build/jniutil
	mkdir -p build/javabinding
	mkdir include
	mkdir bin
	mkdir lib

javalib:
	jar cvf lib/bluedentist.jar -C src javax

java-bluetooth:
	$(JAVAC) $(JFLAGS) src/javax/bluetooth/jni/NativeRemoteDevice.java
	$(JAVAC) $(JFLAGS) src/javax/bluetooth/jni/NativeLocalDevice.java
	$(JAVAC) $(JFLAGS) src/javax/bluetooth/jni/NativeDiscoveryAgent.java

sharedlib:
	$(CC) $(CFLAGS) -dynamiclib build/javabinding/Native.o build/javabinding/NativeDiscoveryAgent.o build/javabinding/NativeRemoteDevice.o build/javabinding/NativeLocalDevice.o build/jbluetooth/jbluetooth.o build/jbluetooth/event.o build/jbluetooth/block.o build/cfutil/cfutil.o build/jniutil/jniutil.o build/hashtable/hashtable.o build/hashtable/hashtable_itr.o -o lib/libbluedentist.jnilib -framework IOBluetooth -framework AppKit -framework CoreFoundation -framework JavaVM

javabinding:
	$(CC) $(CFLAGS) -fPIC -c src/native/javabinding/NativeDiscoveryAgent.c -o build/javabinding/NativeDiscoveryAgent.o -I/System/Library/Frameworks/JavaVM.framework/Headers/
	$(CC) $(CFLAGS) -fPIC -c src/native/javabinding/NativeLocalDevice.c -o build/javabinding/NativeLocalDevice.o -I/System/Library/Frameworks/JavaVM.framework/Headers/
	$(CC) $(CFLAGS) -fPIC -c src/native/javabinding/NativeRemoteDevice.c -o build/javabinding/NativeRemoteDevice.o -I/System/Library/Frameworks/JavaVM.framework/Headers/
	$(CC) $(CFLAGS) -fPIC -c src/native/javabinding/Native.c -o build/javabinding/Native.o -I/System/Library/Frameworks/JavaVM.framework/Headers/

cfutil:
	$(CC) $(CFLAGS) -fPIC -c src/native/cfutil/cfutil.m -o build/cfutil/cfutil.o -Iinclude/ -I/System/Library/Frameworks/JavaVM.framework/Headers/
	install src/native/cfutil/cfutil.h include/cfutil.h
	
jniutil:
	$(CC) $(CFLAGS) -fPIC -c src/native/jniutil/jniutil.c -o build/jniutil/jniutil.o -I/System/Library/Frameworks/JavaVM.framework/Headers/
	install src/native/jniutil/jniutil.h include/jniutil.h
	
hashtable:
	$(CC) $(CFLAGS) -fPIC -c src/native/hashtable/hashtable.c -o build/hashtable/hashtable.o
	$(CC) $(CFLAGS) -fPIC -c src/native/hashtable/hashtable_itr.c -o build/hashtable/hashtable_itr.o
	install src/native/hashtable/hashtable.h include/hashtable.h
	install src/native/hashtable/hashtable_itr.h include/hashtable_itr.h
	install src/native/hashtable/hashtable_private.h include/hashtable_private.h

jbluetooth:
	$(CC) $(CFLAGS) -fPIC -c src/native/jbluetooth/block.c -o build/jbluetooth/block.o
	$(CC) $(CFLAGS) -fPIC -c src/native/jbluetooth/event.c -o build/jbluetooth/event.o
	$(CC) $(CFLAGS) -fPIC -c src/native/jbluetooth/jbluetooth.c -o build/jbluetooth/jbluetooth.o
	install src/native/jbluetooth/jbluetooth.h include/jbluetooth.h

.PHONY: clean setup jbluetooth hashtable jniutil jni

clean:
	rm -r build
	rm -r include
	rm -r bin
	rm -r lib
