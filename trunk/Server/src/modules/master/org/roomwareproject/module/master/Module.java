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

	synchronized void updatePresence(WebService slave, Presence pOld, Presence pNew) {
		logger.info("updating presence: " + pNew);
		slavePresences.get(slave).remove(pOld);
		slavePresences.get(slave).add(pNew);
		propertyChange(new PropertyChangeEvent(pNew.getDevice(), "in range", pOld.getZone(), pNew.getZone()));
	}

	synchronized void addPresence(WebService slave, Presence p) {
		logger.info("adding presence: " + p + ", " + p.getZone());
		slavePresences.get(slave).add(p);
		propertyChange(new PropertyChangeEvent(p.getDevice(), "in range", null, p.getZone()));
	}

	synchronized void removePresence(WebService slave, Presence p) {
		logger.info("removing presence: " + p + ", " + p.getZone());
		slavePresences.get(slave).remove(p);
		propertyChange(new PropertyChangeEvent(p.getDevice(), "in range", p.getZone(), null));
	}

	synchronized void updateFriendlyName(WebService slave, Device device, String oldValue, String newValue) {
		logger.info("updating name: " + device);
		for(Presence lookupPresence: slavePresences.get(slave)) {
			Device lookupDevice = lookupPresence.getDevice();
			if(lookupDevice.equals(device)) {
				lookupDevice.setFriendlyName(newValue);
				propertyChange(new PropertyChangeEvent(lookupDevice, "name", oldValue, newValue));
				return;
			}
		}
		logger.warning("device not known, can't update name!");
	}

}
