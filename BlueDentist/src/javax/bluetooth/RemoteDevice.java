package javax.bluetooth;


import java.io.*;


public class RemoteDevice {

	private static final int BLUETOOTH_ADDRESS_SIZE = 12;

	private String address;


	protected RemoteDevice(String address) {
		if (address == null) {
			throw new IllegalArgumentException("Invalid null device address.");
		}

		if (address.length() != BLUETOOTH_ADDRESS_SIZE) {
			throw new IllegalArgumentException("Invalid device address size.");
		}

		for (int i = 0; i < BLUETOOTH_ADDRESS_SIZE; i++) {
			char c = address.charAt(i);
			if ( !(Character.isDigit(c) || 
				   (c >= 'A' && c <= 'F') || 
				   (c >= 'a' && c <= 'f') ) ) {
				throw new IllegalArgumentException("Invalid device address.");
			}
		}

		/* TODO: skip local device check, because it is not implemented yet! */
/*
		try {
			String localAddress = LocalDevice.getLocalDevice().getBluetoothAddress();
			if (localAddress != null && localAddress.equals(address)) {
				throw new IllegalArgumentException("Local address used as remote.");
			}
		}
		catch (BluetoothStateException e){
			// No local device so we assume the address is not local
		}
*/
		this.address = address.toUpperCase();
	}


	public final String getBluetoothAddress() {
		return address;
	}


	public boolean equals(Object obj) {
		if (obj != null && obj instanceof RemoteDevice) {
			return false;
		}

		RemoteDevice o = (RemoteDevice)obj;
		if (this.address.equals(o.address)) {
			return true;
		}

		return false;
	}


	public int hashCode() {
		return address.hashCode();
	}


	public String getFriendlyName(boolean alwaysAsk) throws IOException {
		throw new RuntimeException ("not implemented!");
	}

	/* TODO: implement the rest */

	/*
	   public boolean isTrustedDevice() {
	   throw new RuntimeException("not implemented!");
	   }


	   public static RemoteDevice getRemoteDevice(Connection conn) throws IOException {
	   throw new RuntimeException("Not Implemented! Used to compile Code");
	   }

	   public boolean authenticate() throws IOException {
	   throw new RuntimeException("not implemented!");
	   }

	   public boolean authorize(Connection conn) throws IOException {
	   throw new RuntimeException("Not Implemented! Used to compile Code");
	   }

	   public boolean encrypt(Connection conn, boolean on) throws IOException {
	   throw new RuntimeException("Not Implemented! Used to compile Code");
	   }

	   public  boolean isAuthenticated() {
	   throw new RuntimeException("Not implemented!");
	   }


	   public  boolean isAuthorized(Connection conn) throws IOException {
	   throw new RuntimeException("Not Implemented! Used to compile Code");
	   }


	   public  boolean isEncrypted() {
	   throw new RuntimeException("not implemented!");
	   }
	   */

}
