package org.roomwareproject.module.master;

import org.roomwareproject.server.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.beans.*;
import java.util.logging.*;


public class WebService implements Runnable {

	protected org.roomwareproject.module.master.Module module;
	protected Socket clientSocket;
	protected Logger logger;
	protected ObjectInputStream in;

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
				PropertyChangeEvent event = (PropertyChangeEvent) in.readObject();
				Device d = (Device) in.readObject();
				event = new PropertyChangeEvent(d, event.getPropertyName(), event.getOldValue(), event.getNewValue());
				logger.info("got new event: " + event.getPropertyName());
				module.handleEvent(this, event);
			}
			catch (ClassNotFoundException cause) {
				logger.warning("Can't read event!");
			}
		}
	}


	@SuppressWarnings("unchecked")
	private void handlePresenceList() throws IOException {
		try {
			Set<Presence> presences = new HashSet<Presence>();
			int presenceCount = in.readInt();
			while (presenceCount-- > 0) {
				Device device = (Device)in.readObject();
				String zone = (String)in.readObject();
				presences.add(new Presence(module, zone, device, new Date()));
			}
			module.addSlave(this, presences);
		}
		catch (ClassNotFoundException cause) {
			throw new IOException ("cant read Set<Presence> Class!");
		}
	}


}
