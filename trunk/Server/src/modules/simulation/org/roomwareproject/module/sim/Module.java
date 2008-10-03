package org.roomwareproject.module.sim;

import org.roomwareproject.server.*;
import org.roomwareproject.utils.*;
import java.util.*;
import java.util.logging.*;
import java.io.*;
import java.beans.*;

class SimEvent implements Comparable<SimEvent> {
	static int ARRIVE	= 1;
	static int LEAVE	= 2;

	protected Presence presence;
	protected long time;
	protected int type;

	SimEvent (Presence p, long time, int type) {
		this.presence = p;
		this.time = time;
		this.type = type;
	}

	long getTime() { return time; }
	Presence getPresence () { return presence; }
	int getType () { return type; }

	public int compareTo (SimEvent event) {
		return (int)(time - event.time);
	}
}

class SimEventFactory {

	protected int intervaltime;
	protected int staytime;
	protected String[] availableZones;
	protected Device[] availableDevices;

	SimEventFactory (int intervaltime, int staytime,
					 String[] zones, Device[] devices) {
		this.intervaltime = intervaltime;
		this.staytime = staytime;
		this.availableZones = zones;
		this.availableDevices = devices;
	}

	SimEvent createArriveEvent () {
		Device chosenDevice = availableDevices[(int)(Math.random()
													 * availableDevices.length)];
		String chosenZone = availableZones[(int)(Math.random()
												 * availableZones.length)];
		Presence p = new Presence (this, chosenZone, chosenDevice, new Date ());
		long time = (long) (Math.random () * intervaltime * 1000);
		time += new Date().getTime ();
		return new SimEvent (p, time, SimEvent.ARRIVE);
	}

	SimEvent createLeaveEvent (Presence p) {
		long time = (long) (Math.random () * staytime * 1000);
		time += new Date().getTime ();
		return new SimEvent (p, time, SimEvent.LEAVE);
	}
}

public class Module extends AbstractModule {

	protected boolean doLoop = true;
	protected Set<Presence>presences = new HashSet<Presence>();
	protected PriorityQueue<SimEvent> queue = new PriorityQueue<SimEvent>();
	protected SimEventFactory fac;

	public Module(Properties properties) throws RoomWareException {
		super(properties);
		init();
	}

	protected void init() throws RoomWareException {
		String zoneNameList = properties.getProperty ("zones", "sim");
		String[] zones = zoneNameList.split (",");
		for (int i = 0; i < zones.length; i++)
		{
			zones[i] = zones[i].trim();
		}

		String deviceNameList = properties.getProperty("devices");
		String[] deviceAddressNames = deviceNameList.split(",");
		Device[] devices = new Device[deviceAddressNames.length];
		for(int i = 0; i < deviceAddressNames.length; i++) {
			String address = deviceAddressNames[i].trim();
			String type = properties.getProperty(address + "-type", "simulation").trim ();
			String name = properties.getProperty(address + "-name");
			Device d = new Device(new SimulationDeviceAddress(address, type));
			if(name != null) d.setFriendlyName(name.trim());
			devices[i] = d;
		}
		try {
			String staytimeStr = properties.getProperty ("stay-time", "60");
			String intervaltimeStr = properties.getProperty ("interval-time", "120");
			int staytime = Integer.parseInt (staytimeStr);
			int intervaltime = Integer.parseInt (intervaltimeStr);
			fac = new SimEventFactory (intervaltime, staytime, zones, devices);
		}
		catch (NumberFormatException cause) {
			throw new RoomWareException ("Can't read interval-time and/or stay-time values!");
		}
	}


	public void run() {
		queue.add (fac.createArriveEvent ());
		while(doLoop) {
			SimEvent event = queue.peek ();
			if (event == null) {
				logger.warning ("event queue is empty!");
				doLoop = false;
				continue;
			}	
			try {
				synchronized(this) {
					long waitTime = event.getTime () - new Date().getTime();
					if (waitTime > 0) wait(waitTime);
				}
			}
			catch(InterruptedException cause) {
				if(doLoop) {
					logger.warning("we are waked for unknown reason!");
				}
				else continue;
			}
			queue.remove (event);
			if (event.getType () == SimEvent.ARRIVE) {
				synchronized (this) {
					presences.add (event.getPresence ());
				}
				queue.add (fac.createLeaveEvent (event.getPresence()));
				queue.add (fac.createArriveEvent ());
			}
			if (event.getType () == SimEvent.LEAVE) {
				synchronized (this) {
					presences.remove (event.getPresence ());
				}
			}
		}
	}

	public synchronized void stop() {
		doLoop = false;
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
