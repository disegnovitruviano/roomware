package org.roomwareproject.module.master;

import org.roomwareproject.server.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.beans.*;
import java.util.logging.*;


public class WebService implements Runnable {

	protected Socket clientSocket;
	protected Logger logger;

	protected String roomwareServerId;
	protected Set<Presence> presences = new HashSet<Presence>();
	protected ObjectInputStream in;
	protected org.roomwareproject.module.master.Module module;

	WebService(org.roomwareproject.module.master.Module module, Socket clientSocket, Logger logger) {
		this.module = module;
		this.clientSocket = clientSocket;
		this.logger = logger;
	}


	public void run() {
		try {
			in = new ObjectInputStream(
				clientSocket.getInputStream());

			handleRoomwareServerId();
			logger.info("we've got server '" + roomwareServerId + "' here!");
			logger.info("going to handle device list");
			handlePresenceList();
			logger.info("device list handled");

			handleEvents();

			logger.info("we are closing a connection...");

			in.close();
			clientSocket.close();
		}
		catch(IOException cause) {
			logger.warning(cause.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private void handleEvents() throws IOException {
		boolean doLoop = true;

		while(doLoop) {
			try {
				PropertyChangeEvent event =
					(PropertyChangeEvent) in.readObject();
				Presence p = (Presence) in.readObject();
				logger.info("got new event");

				if(event.getPropertyName().equals("in range")) {
					if(event.getNewValue() == null) {
						module.removePresence(roomwareServerId, p);
						presences.remove(p);
					}
					else if(event.getOldValue() == null) {
						module.addPresence(roomwareServerId, p);
						presences.add(p);
					}
				}
				else if(event.getPropertyName().equals("name")) {
					module.updateFriendlyName(roomwareServerId, p.getDevice(), (String)event.getOldValue(), (String)event.getNewValue());
				}
			}
			catch (ClassNotFoundException cause) {
				logger.warning("Can't read event!");
			}
		}
	}


	@SuppressWarnings("unchecked")
	private void handleRoomwareServerId() throws IOException {
		try {
			roomwareServerId = (String)in.readObject();
		}
		catch (ClassNotFoundException cause) {
			throw new IOException ("cant read String Class!");
		}
	}


	@SuppressWarnings("unchecked")
	private void handlePresenceList() throws IOException {
		try {
			Set<Presence> newPresences = (Set<Presence>)in.readObject();
			presences = newPresences;
			module.initSlave(roomwareServerId, newPresences);
		}
		catch (ClassNotFoundException cause) {
			throw new IOException ("cant read Set<Presence> Class!");
		}
	}


}
