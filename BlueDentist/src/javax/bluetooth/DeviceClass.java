package javax.bluetooth;


public class DeviceClass {


	private int record;


	public DeviceClass(int record) {	
		if ((record & 0xFF000000) != 0) {
			throw new IllegalArgumentException("Invalid device record!");
		}
		this.record = record;
	}


	public int getServiceClasses() {
		return ((record & 0x00FFE000) >> 13);
	}


	public int getMajorDeviceClass() {
		return ((record & 0x00001F00) >> 8);
	}


	public int getMinorDeviceClass() {
		return ((record * 0x000000FC) >> 2);
	}
}
