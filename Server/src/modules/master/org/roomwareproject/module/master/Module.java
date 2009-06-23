package org.roomwareproject.module.master;

import org.roomwareproject.server.*;
import org.roomwareproject.utils.*;
import java.util.*;
import java.util.logging.*;
import java.io.*;
import java.beans.*;

public class Module extends AbstractModule {

	protected ServerSocket serverSocket;
	protected Set<Presence> presences;
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

   		presences = new HashSet<Presence>();
		doLoop = true;
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
		return new HashSet<Presence>(presences);
	}


	public void sendMessage(Device device, Message message)
		throws RoomWareException {
		throw new RoomWareException("Operation not supported!");
	}

	void synchronized addPresence(Presence p) {
		logger.info("adding presence: " + p);
		presences.add(p);
		propertyChange(new PropertyChangeEvent(p.getDevice(), "in range", null, p.getZone()));
	}

	void synchronized removePresence(Presence p) {
		logger.info("removing presence: " + p);
		presences.remove(p);
		propertyChange(new PropertyChangeEvent(p.getDevice(), "in range", p.getZone(), null));
	}

	void synchronized updateName(Device device, String oldValue, String newValue) {
		logger.info("updating name: " + device);
		for(Presence lookupPresence: presences) {
			Device lookupDevice = lookupPresence.getDevice();
			if(lookupDevice.equals(device)) {
				lookupDevice.setFriendlyName(newValue);
				propertyChange(new PropertyChangeEvent(lookupDevice, "name", oldValue, newValue));
				return;
			}
		}
		logger.warning("device not known, cant update name!");
	}
}
