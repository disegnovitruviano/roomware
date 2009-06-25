package org.roomwareproject.module.oscrfid;

import org.roomwareproject.server.*;

public class OSCTagDeviceAddress extends DeviceAddress {
	protected String address;
	protected String type = "OSCRfidTag";

	public OSCTagDeviceAddress(Object arg1, Object arg2) {
		address = arg1 + "-" + arg2;
	}

	public int hashCode() {
		return address.hashCode();
	}

	public boolean equals(Object object) {
		if(object instanceof OSCTagDeviceAddress) {
			OSCTagDeviceAddress other = (OSCTagDeviceAddress) object;
			return address.equals(other.address);
		}
		return false;
	}

	public String toString() {
		return address;
	}

	public String getType () {
		return type;
	}
}
