package javax.bluetooth.jni;


import javax.bluetooth.*;
import java.io.*;



public class NativeRemoteDevice extends RemoteDevice {

	private long nativeDeviceRef;


	public NativeRemoteDevice(long nativeDeviceRef, String address) {
		super(address);
		this.nativeDeviceRef = nativeDeviceRef;
	}


	long getNativeDeviceRef() {
		return nativeDeviceRef;
	}


	public String getFriendlyName(boolean alwaysAsk) throws IOException {
		String name = nativeGetFriendlyName(alwaysAsk, nativeDeviceRef);

		if(name == null) {
			throw new IOException("Could not retrieve friendly name!");
		}

		return name;
	}


	protected native String nativeGetFriendlyName(boolean alwaysAsk, long nativeDeviceRef);

}
