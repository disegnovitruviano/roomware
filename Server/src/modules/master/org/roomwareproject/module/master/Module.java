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
	protected Map<String, Set<Presence>> slavePresences;
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

   		slavePresences = new HashMap<String, Set<Presence>>();
		doLoop = true;
	}


	protected synchronized void initSlave(String roomwareServerId, Set<Presence> presences) {
		if (slavePresences.containsKey(roomwareServerId)) {
			Set<Presence> savedPresences = slavePresences.get(roomwareServerId);
			for (Presence p: savedPresences) {
				if (!presences.contains(p))
					removePresence(roomwareServerId, p);

				/* TODO
				else
					we should check the device properties...
					if we don't we end up with the wrong settings
					for each property. changing each property seperatly
					will update the proper info.
				*/
			}
		}
		slavePresences.put(roomwareServerId, presences);
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

	synchronized void addPresence(String roomwareServerId, Presence p) {
		logger.info("adding presence: " + p);
		slavePresences.get(roomwareServerId).add(p);
		propertyChange(new PropertyChangeEvent(p.getDevice(), "in range", null, p.getZone()));
	}

	synchronized void removePresence(String roomwareServerId, Presence p) {
		logger.info("removing presence: " + p);
		slavePresences.get(roomwareServerId).remove(p);
		propertyChange(new PropertyChangeEvent(p.getDevice(), "in range", p.getZone(), null));
	}

	synchronized void updateFriendlyName(String roomwareServerId, Device device, String oldValue, String newValue) {
		logger.info("updating name: " + device);
		for(Presence lookupPresence: slavePresences.get(roomwareServerId)) {
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
