package org.roomwareproject.module.sim;

import org.roomwareproject.server.*;

public class SimulationDeviceAddress extends DeviceAddress {
	protected String address;
	protected String type;

	public SimulationDeviceAddress(String addressString, String type) {
		address = addressString;
		this.type = type;
	}

	public int hashCode() {
		return address.hashCode();
	}

	public boolean equals(Object object) {
		if(object instanceof SimulationDeviceAddress) {
			SimulationDeviceAddress other = (SimulationDeviceAddress) object;
			return address.equals(other.address);
		}
		return false;
	}

	public String getType () {
		return type;
	}

	public String toString() {
		return address;
	}
}
