package javax.bluetooth;


import java.util.*;
import javax.bluetooth.jni.*;


public class LocalDevice {


	protected LocalDevice() {
	}


	public static LocalDevice getLocalDevice() throws BluetoothStateException {
		return NativeLocalDevice.getLocalDevice();
	} 


	public DiscoveryAgent getDiscoveryAgent() {
		throw new RuntimeException("Not implemented!");
	}	

	public String getFriendlyName() {
		throw new RuntimeException("Not implemented!");
	}

	public DeviceClass getDeviceClass() {
		throw new RuntimeException("Not Implemented!");
	}

	public boolean setDiscoverable(int mode) throws BluetoothStateException {
		throw new RuntimeException("Not Implemented!");
	}

	public static String getProperty(String property) {
		throw new RuntimeException("Not Implemented!");
	}

	public int getDiscoverable() {
		throw new RuntimeException("Not implemented!");
	}

	public String getBluetoothAddress() {
		throw new RuntimeException("not implemented!");
	}

	/* TODO: enable service records */
	/*
	   public ServiceRecord getRecord(Connection notifier) {
	   throw new RuntimeException("Not Implemented!");
	   }

	   public void updateRecord(ServiceRecord srvRecord) throws ServiceRegistrationException {
	   throw new RuntimeException("Not Implemented!");
	   }
	   */

}
