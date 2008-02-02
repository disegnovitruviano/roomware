package bluemod;

import roomware.*;

public class BluetoothAddress extends DeviceAddress {

	protected String address;

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
}
