package org.roomwareproject.module.phidgetrfid;

import org.roomwareproject.server.*;
import org.roomwareproject.utils.*;
import com.phidgets.*;
import com.phidgets.event.*;

import java.util.*;
import java.util.logging.*;
import java.io.*;
import java.beans.*;


public class Module extends AbstractModule implements TagGainListener, TagLossListener {


	protected String zone;
	protected RFIDPhidget reader;
	protected boolean doLoop;
	protected Set<Presence> presences;


	public void tagGained(TagGainEvent event) {
		if(reader != event.getSource()) return;

		String addressString = event.getValue();
		try {
			PhidgetTagDeviceAddress address = PhidgetTagDeviceAddress.parseAddress(addressString);
			Device device = new Device(address);
			Presence p = new Presence (this, zone, device, new Date());
			presences.add(p);
			propertyChange(new PropertyChangeEvent(device, "in range", false, p.getZone()));
		}
		catch(NumberFormatException cause) {
			logger.info("device with invalid address: " + addressString);
		}
	}


	public void tagLost(TagLossEvent event) {
		if(reader != event.getSource()) return;

		String addressString = event.getValue();
		try {
			PhidgetTagDeviceAddress address = PhidgetTagDeviceAddress.parseAddress(addressString);
			Device device = new Device(address);
			Presence p = new Presence (this, zone, device, new Date());
			presences.remove(p);
			propertyChange(new PropertyChangeEvent(device, "in range", p.getZone(), null));
		}
		catch(NumberFormatException cause) {
			logger.info("device with invalid address: " + addressString);
		}
	}


	public Module(Properties properties) throws RoomWareException {
		super(properties);
		init();
	}


	protected void init() throws RoomWareException {
		zone = getProperties().getProperty ("zone", "<unknown>").trim();
		presences = new HashSet<Presence>();
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
		doLoop = true;
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


	public synchronized Set<Presence> getPresences() {
		return presences;
	}


	public void sendMessage(Device device, Message message)
		throws RoomWareException {
			throw new RoomWareException("Operation not supported!");
		}
}
