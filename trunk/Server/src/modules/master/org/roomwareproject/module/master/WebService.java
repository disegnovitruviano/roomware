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
		logger.info("begin slave connection: " + this);
		try {
			in = new ObjectInputStream(clientSocket.getInputStream());
			handlePresenceList();
			handleEvents();
			in.close();
			clientSocket.close();
		}
		catch(IOException cause) {
			logger.warning(cause.getMessage());
		}
		logger.info("end slave connection: " + this);
		module.removeSlave(this);
	}

	@SuppressWarnings("unchecked")
	private void handleEvents() throws IOException {
		boolean doLoop = true;

		while(doLoop) {
			try {
				PropertyChangeEvent event =
					(PropertyChangeEvent) in.readObject();
				Device device = (Device) in.readObject();
				logger.info("got new event: " + event.getPropertyName());

				if(event.getPropertyName().equals("in range")) {
					Presence pOld = null, pNew = null;
					
					if (event.getOldValue() != null) {
						pOld = new Presence (module, event.getOldValue().toString(), device, new Date());
					}
					else {
						logger.info("we are new detected");
					}
					if (event.getNewValue() != null) {
						logger.info("we have new value: " + event.getNewValue());
						pNew = new Presence (module, event.getNewValue().toString(), device, new Date());
					}
					else {
						logger.info("we have disappeared");
					}
					if(event.getOldValue() == null) {
						presences.add(pNew);
						module.addPresence(this, pNew);
					}
					else if (event.getNewValue() == null) {
						presences.remove(pOld);
						module.removePresence(this, pOld);
					}
					else {
						presences.remove(pOld);
						presences.add(pNew);
						module.updatePresence(this, pOld, pNew);
					}
				}
				else if(event.getPropertyName().equals("name")) {
					module.updateFriendlyName(this, device, (String)event.getOldValue(), (String)event.getNewValue());
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
			Set<Presence> presences = new HashSet<Presence>();
			int presenceCount = in.readInt();
			while (presenceCount-- > 0) {
				String zone = (String)in.readObject();
				Device device = (Device)in.readObject();
				Date time = (Date)in.readObject();
				presences.add(new Presence(module, zone, device, time));
			}
			module.addSlave(this, presences);
		}
		catch (ClassNotFoundException cause) {
			throw new IOException ("cant read Set<Presence> Class!");
		}
	}


}
