package org.roomwareproject.module.rfid;

import org.roomwareproject.server.*;


public class RfidAddress extends DeviceAddress {

	protected String address;

	public RfidAddress(String addressString) {
		address = addressString;
	}

	public int hashCode() {
		return address.hashCode();
	}

	public boolean equals(Object object) {
		if(object instanceof RfidAddress) {
			RfidAddress other = (RfidAddress) object;
			return address.equals(other.address);
		}
		return false;
	}

	public static RfidAddress parseAddress(String s) throws NumberFormatException {
		return new RfidAddress(s);
	}

	public String toString() {
		return address;
	}
}
