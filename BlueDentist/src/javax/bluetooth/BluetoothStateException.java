package javax.bluetooth;


import java.io.*;


public class BluetoothStateException extends IOException {

    static final long serialVersionUID = 1416755682950937086L;

	public BluetoothStateException() {
		super();
	}

	public BluetoothStateException(String msg) {
		super(msg);
	}
}
