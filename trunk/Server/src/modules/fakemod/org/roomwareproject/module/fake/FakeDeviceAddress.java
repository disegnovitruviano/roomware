package org.roomwareproject.module.fake;

import org.roomwareproject.server.*;

public class FakeDeviceAddress extends DeviceAddress {
	protected String address;
	protected String type;

	public FakeDeviceAddress(String addressString, String type) {
		address = addressString;
		this.type = type;
	}

	public int hashCode() {
		return address.hashCode();
	}

	public boolean equals(Object object) {
		if(object instanceof FakeDeviceAddress) {
			FakeDeviceAddress other = (FakeDeviceAddress) object;
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
