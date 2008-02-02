package javax.bluetooth.jni;


import javax.bluetooth.*;


public class NativeDiscoveryAgent extends DiscoveryAgent {

	protected DiscoveryListener listener = null;
	protected boolean inquiryBusy = false;
	private static NativeDiscoveryAgent nativeDiscoveryAgent = null;

	private NativeDiscoveryAgent() {
	}


	static DiscoveryAgent getDiscoveryAgent() {
		if(nativeDiscoveryAgent == null) {
			nativeDiscoveryAgent = new NativeDiscoveryAgent();
		}
		return nativeDiscoveryAgent;
	}


	public void deviceDiscovered(long deviceRef, int classOfDevice, String address) {
		RemoteDevice remoteDevice = new NativeRemoteDevice(deviceRef, address);
		DeviceClass deviceClass = new DeviceClass(classOfDevice);
		listener.deviceDiscovered(remoteDevice, deviceClass);
	}


	public synchronized void inquiryCompleted(int discType) {
		inquiryBusy = false;
		listener.inquiryCompleted(discType);
		listener = null;
	}


	public synchronized boolean startInquiry(int accessCode, DiscoveryListener listener) throws BluetoothStateException {

		if(inquiryBusy) {
			throw new BluetoothStateException("Device is currently busy!");
		}
		
		this.listener = listener;
		this.inquiryBusy = true;

		boolean setupOk = nativeStartInquiry(accessCode);
		if(!setupOk) {
			listener = null;
			inquiryBusy = false;
		}

		return setupOk;
	}

	
	public synchronized boolean cancelInquiry(DiscoveryListener listener) {
			if(!inquiryBusy || this.listener != listener) {
				return false;
			}

			return nativeCancelInquiry();
	}


	protected native boolean nativeStartInquiry(int accessCode);


	protected native boolean nativeCancelInquiry();

}
