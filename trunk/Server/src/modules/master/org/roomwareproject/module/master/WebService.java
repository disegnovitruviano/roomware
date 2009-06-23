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

	protected HashSet<Device> devices = new HashSet<Device> ();
	protected ObjectInputStream in;
	protected org.roomwareproject.module.roomware.Module module;

	WebService(org.roomwareproject.module.roomware.Module module, Socket clientSocket, Logger logger) {
		this.module = module;
		this.clientSocket = clientSocket;
		this.logger = logger;
	}


	public void run() {
		try {
			in = new ObjectInputStream(
				clientSocket.getInputStream());

			logger.info("going to handle device list");
			handleDeviceList();
			logger.info("device list handled");

			handleEvents();

			logger.info("we are clossing...");

			in.close();
			clientSocket.close();
		}
		catch(IOException cause) {
			logger.warning(cause.getMessage());
		}
		for(Device d: devices) {
			module.removeDevice(d);
		}
	}

	@SuppressWarnings("unchecked")
	private void handleEvents() throws IOException {
		boolean doLoop = true;

		while(doLoop) {
			try {
				PropertyChangeEvent event =
					(PropertyChangeEvent) in.readObject();
				Device d = (Device) in.readObject();
				logger.info("got new event");

				if(event.getPropertyName().equals("in range")) {
					if(event.getNewValue() == null) {
						module.removeDevice(d);
						devices.remove(d);
					}
					else if(event.getOldValue() == null) {
						module.addDevice(d);
						devices.add(d);
					}
				}
				else if(event.getPropertyName().equals("name")) {
					module.updateName(d, (String)event.getOldValue(), (String)event.getNewValue());
				}
			}
			catch (ClassNotFoundException cause) {
				logger.warning("Can't read event!");
			}
		}
	}


	@SuppressWarnings("unchecked")
	private void handleDeviceList() throws IOException {
		devices.clear();
		try {
			Set<Device> newDevices = (Set<Device>)in.readObject();
			Set<Device> knownDevices = module.getDevices();
			for(Device d: newDevices) {
				devices.add(d);
				if(!knownDevices.contains(d)) {
					module.addDevice(d);
				}
			}
		}
		catch (ClassNotFoundException cause) {
			throw new IOException ("cant read Set<Device> Class!");
		}
	}


}
