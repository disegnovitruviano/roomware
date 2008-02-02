package roomware;

public class Device {

	protected DeviceAddress address;
	protected String friendlyName;

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
		return "Device[address=" + address.toString() + 
		   ",friendlyName=" + friendlyName + "]";
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
