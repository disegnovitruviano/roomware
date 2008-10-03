package org.roomwareproject.module.bluetooth;

import org.roomwareproject.server.*;

public class BluetoothAddress extends DeviceAddress {

	protected String address;
	final static public String TYPE = "bluetooth";

	public BluetoothAddress(String addressString) {
		address = addressString;
	}

	public int hashCode() {
		return address.hashCode();
	}

	public boolean equals(Object object) {
		if(object instanceof BluetoothAddress) {
			BluetoothAddress other = (BluetoothAddress) object;
			return address.equals(other.address);
		}
		return false;
	}

	public String toString() {
		return address;
	}

	public String getType () {
		return TYPE;
	}
}
