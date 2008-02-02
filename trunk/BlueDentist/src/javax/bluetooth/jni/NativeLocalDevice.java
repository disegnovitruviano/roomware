package javax.bluetooth.jni;


import javax.bluetooth.*;
import java.io.*;


public class NativeLocalDevice extends LocalDevice {

	static {
		System.loadLibrary("bluedentist");
	}

	private static NativeLocalDevice nativeLocalDevice = null;


	private NativeLocalDevice() throws BluetoothStateException {
		if(!nativeGetLocalDevice()) throw new BluetoothStateException("No bluetooth device available!");
	}


	protected native boolean nativeGetLocalDevice();


	public static LocalDevice getLocalDevice() throws BluetoothStateException {
		if(nativeLocalDevice == null) {
			nativeLocalDevice = new NativeLocalDevice();
		}

		return nativeLocalDevice;
	}


	public DiscoveryAgent getDiscoveryAgent() {
		return NativeDiscoveryAgent.getDiscoveryAgent();
	}


	public String getBluetoothAddress() {
		String address = nativeGetBluetoothAddress();
		if(address == null) {
			address = "000000000000";
		}
		return address;
	}


	protected native String nativeGetBluetoothAddress();

}
