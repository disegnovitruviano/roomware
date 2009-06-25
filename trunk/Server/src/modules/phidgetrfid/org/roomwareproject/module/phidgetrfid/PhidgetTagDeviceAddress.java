package org.roomwareproject.module.phidgetrfid;

import org.roomwareproject.server.*;


public class PhidgetTagDeviceAddress extends DeviceAddress {

	protected String address;
	protected String type = "PhidgetRfidTag";

	public PhidgetTagDeviceAddress(String addressString) {
		address = addressString;
	}

	public int hashCode() {
		return address.hashCode();
	}

	public boolean equals(Object object) {
		if(object instanceof PhidgetTagDeviceAddress) {
			PhidgetTagDeviceAddress other = (PhidgetTagDeviceAddress) object;
			return address.equals(other.address);
		}
		return false;
	}

	public static PhidgetTagDeviceAddress parseAddress(String s) throws NumberFormatException {
		return new PhidgetTagDeviceAddress(s);
	}

	public String getType() {
		return type;
	}

	public String toString() {
		return address;
	}
}
