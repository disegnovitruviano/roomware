package org.roomwareproject.module.master;

import org.roomwareproject.server.*;
import org.roomwareproject.utils.*;
import java.util.*;
import java.util.logging.*;
import java.io.*;
import java.net.*;
import java.beans.*;

public class Module extends AbstractModule {

	protected ServerSocket serverSocket;
	protected Map<WebService, Set<Presence>> slavePresences;
	protected boolean doLoop;


	public Module(Properties properties) throws RoomWareException {
		super(properties);
		init();
	}


	protected void init() throws RoomWareException {
		try {
			int port = Integer.parseInt(
				properties.getProperty("port", "4003"));
			serverSocket = new ServerSocket(port);
		}
		catch (NumberFormatException cause) {
			logger.severe(cause.getMessage());
			throw new RoomWareException("unable to parse port number!");
		}
		catch (IOException cause) {
			logger.severe(cause.getMessage());
			throw new RoomWareException("unable to open server socket!");
		}

   		slavePresences = new HashMap<WebService, Set<Presence>>();
		doLoop = true;
	}


	protected synchronized void addSlave(WebService slave, Set<Presence> presences) {
		slavePresences.put(slave, presences);
		for (Presence p: presences) {
			propertyChange(new PropertyChangeEvent(p.getDevice(), "in range", null, p.getZone()));
		}
	}


	protected synchronized void removeSlave(WebService slave) {
		Set<Presence> presences = slavePresences.get(slave);
		slavePresences.remove(slave);
		for (Presence p: presences) {
			propertyChange(new PropertyChangeEvent(p.getDevice(), "in range", p.getZone(), null));
		}
	}


	public void run() {
		while(doLoop) {
			try {
				logger.info("waiting for new roomware server...");
				Socket clientSocket = serverSocket.accept();
				logger.info("got new roomware server");
				Thread t = new Thread(new WebService(this, clientSocket, logger));
				t.start();
			}
			catch(IOException cause) {
				logger.warning("we have some problems: "+ cause.getMessage());
			}

			try {
				synchronized(this) {
					wait(1000);
				}
			}
			catch (InterruptedException cause) {
			}
		}
	}


	public synchronized void stop() {
		doLoop = false;
		notifyAll();
	}


	public synchronized Set<Presence> getPresences() {
		Set<Presence> allPresences = new HashSet<Presence> ();
		for (Set<Presence> presences: slavePresences.values()) {
			allPresences.addAll(presences);
		}
		return allPresences;
	}


	public void sendMessage(Device device, Message message)
		throws RoomWareException {
		throw new RoomWareException("Operation not supported!");
	}


	synchronized void handleEvent(WebService slave, PropertyChangeEvent event) {
		String property = event.getPropertyName();
		if (property.equals("in range")) {
			handleInRangeEvent(slave, event);
		}
		else if (property.equals("name")) {
			handleNameEvent(slave, event);
		}
	}


	protected synchronized void handleInRangeEvent(WebService slave, PropertyChangeEvent event) {
		Set<Presence> presences = slavePresences.get(slave);
		if (!(event.getSource() instanceof Device)) {
			logger.warning("Got not a Device in event source but: " + event.getSource());
			return;
		}
		Device device = (Device)event.getSource();
		Presence p;

		if (event.getNewValue() == null) { // we are leaving zone
			p = new Presence(this, event.getOldValue().toString(), device, new Date());
			presences.remove(p);
			propertyChange(new PropertyChangeEvent(p.getDevice(), "in range", p.getZone(), null));
		}

		else if (event.getOldValue() == null) { // we are new in zone
			p = new Presence(this, event.getNewValue().toString(), device, new Date());
			presences.add (p);
			propertyChange(new PropertyChangeEvent(p.getDevice(), "in range", null, p.getZone()));
		}

		else { // we are changing zones
			p = new Presence(this, event.getOldValue().toString(), device, new Date());
			presences.remove(p);
			p = new Presence(this, event.getNewValue().toString(), device, new Date());
			presences.add(p);
			propertyChange(new PropertyChangeEvent(p.getDevice(), "in range", null, p.getZone()));
		}
	}


	protected synchronized void handleNameEvent(WebService slave, PropertyChangeEvent event) {
		if (!(event.getSource() instanceof Device)) {
			logger.warning("Got not a Device in event source but: " + event.getSource());
			return;
		}
		Device device = (Device)event.getSource();
		String currentName = (String)event.getOldValue();
		String newName = (String)event.getNewValue();
		boolean found = false;

		logger.info("updating name: " + device);
		for(Presence lookupPresence: slavePresences.get(slave)) {
			Device lookupDevice = lookupPresence.getDevice();
			if(lookupDevice.equals(device)) {
				lookupDevice.setFriendlyName(newName);
				found = true;
			}
		}

		if (found) {
			propertyChange(new PropertyChangeEvent(device, "name", currentName, newName));
		} else {
			logger.warning("device not known, can't update name!");
		}
	}

}
