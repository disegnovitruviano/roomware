package org.roomwareproject.server;

public class Device {

	protected DeviceAddress address = null;
	protected String friendlyName = null;

	public Device(DeviceAddress address) {
		this.address = address;
	}

	public void setFriendlyName(String newFriendlyName) {
		friendlyName = newFriendlyName;
	}

	public DeviceAddress getDeviceAddress() {
		return address;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public String toString() {
            String namePart = "";
            if(friendlyName != null) {
                namePart = ",friendlyName=" + friendlyName;
            }
            
            return "Device[address=" + address.toString() + namePart + "]";
	}

	public int hashCode() {
		return address.hashCode();
	}

	public boolean equals(Object object) {
		if(!(object instanceof Device)) return false;

		Device otherDevice = (Device) object;
		return address.equals(otherDevice.address);
	}
}
