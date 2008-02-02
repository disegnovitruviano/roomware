package test;


import javax.bluetooth.*;
import java.io.*;
import java.util.*;



public class Test implements DiscoveryListener {

	LocalDevice localDevice;
	DiscoveryAgent discoveryAgent;
	boolean discoveryCompleted = false;


	public Test() throws BluetoothStateException {
		localDevice = LocalDevice.getLocalDevice();
		discoveryAgent = localDevice.getDiscoveryAgent();
	}


	public void doDiscovery() {
		try {
			if(discoveryAgent.startInquiry(DiscoveryAgent.GIAC, this)) {
				discoveryCompleted = false;
				System.out.println("Inquiry started!");
			}
			else {
				System.out.println("inquiry failed to start!");
			}
		}
		catch(BluetoothStateException cause) {
			System.out.println(cause.getMessage());
		}

		synchronized(this) {
			try {
				wait(20000);
			}
			catch(InterruptedException cause) {
			}	
		}

		if(!discoveryCompleted) {
			System.out.println("discovery completed = " + discoveryCompleted);
			System.out.println("canceling inquiry...");
			System.out.println("cancel inquiry = " + discoveryAgent.cancelInquiry(this));
		}
	}


	public void start() {
		System.out.println("My device address = " + localDevice.getBluetoothAddress());

		doDiscovery();
	}


	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass clazz) {
		try {
			String address = btDevice.getBluetoothAddress();
				String name = btDevice.getFriendlyName(false);
 				System.out.println(address + " : " + name);
			}
			catch (IOException cause) {
				System.err.println(cause.getMessage());
			}
	}


	public void inquiryCompleted(int discType) {
		synchronized(this) {
			discoveryCompleted = true;
			notify();
		}

		System.out.println("Inquiry completed!");
	}


	public static void main(String args[]) throws Exception {
		Test test = new Test();
		test.start();
	}

}
