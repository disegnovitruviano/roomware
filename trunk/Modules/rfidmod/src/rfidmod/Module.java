package rfidmod;

import com.phidgets.*;
import com.phidgets.event.*;

import roomware.*;
import java.util.*;
import java.util.logging.*;
import java.io.*;
import java.beans.*;


public class Module extends AbstractModule implements TagGainListener, TagLossListener {


	protected RFIDPhidget reader;
	protected boolean doLoop = true;
	protected Device device;


	public void tagGained(TagGainEvent event) {
		if(reader != event.getSource()) return;

		String addressString = event.getValue();
		try {
			RfidAddress address = RfidAddress.parseAddress(addressString);
			device = new Device(address);

			propertyChange(new PropertyChangeEvent(device, "in range", false, true));
		}
		catch(NumberFormatException cause) {
			logger.info("device with invalid address: " + addressString);
		}
	}


	public void tagLost(TagLossEvent event) {
		if(reader != event.getSource()) return;

		if(device == null) return;

		Device tempDevice = device;
		device = null;

		propertyChange(new PropertyChangeEvent(tempDevice, "in range", true, false));
	}


	public Module(Properties properties) throws RoomWareException {
		super(properties);
		init();
	}


	protected void init() throws RoomWareException {
		try {
			reader = new RFIDPhidget();
			reader.openAny();
			reader.waitForAttachment();
			reader.setAntennaOn(true);
		}
		catch(PhidgetException cause) {
			throw new RoomWareException("Could not init rfid hardware!");
		}
	}


	public void run() {
		reader.addTagGainListener(this);
		reader.addTagLossListener(this);

		while(doLoop) {
			try {
				synchronized(this) {
					wait();
				}
			}

			catch(InterruptedException cause) {
				if(doLoop) {
					logger.warning("we are waked for unknown reason!");
				}
			}
		}
	}


	public synchronized void stop() {
		doLoop = false;
		reader.removeTagGainListener(this);
		reader.removeTagLossListener(this);
		notifyAll();
	}


	public synchronized Set<Device> getDevices() {
		Set<Device> devices = new HashSet<Device> ();
		if(device != null) {
			devices.add(device);
		}
		return devices;
	}


	public void sendMessage(Device device, Message message)
		throws RoomWareException {
			throw new RoomWareException("Operation not supported!");
		}
}
