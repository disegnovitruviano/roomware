package org.roomwareproject.communicator.admin.server;


import org.roomwareproject.server.*;
import java.io.*;
import java.net.*;
import java.util.logging.*;
import java.util.*;



public class AppletService implements Runnable {

	private RoomWareServer rwsi;
	private Socket client;
	private Logger logger;
	private boolean stop = false;

	private ObjectInputStream in;
	private ObjectOutputStream out;

	public AppletService(Socket client, RoomWareServer rwsi, Logger logger) {
		this.rwsi = rwsi;
		this.client = client;
		this.logger = logger;
	}

	public void run() {
		try {
			out = new ObjectOutputStream(client.getOutputStream());
			in = new ObjectInputStream(client.getInputStream());

			do {
				String command = in.readUTF();
				handleCommand(command);
			} while(!stop);
		}
		catch(IOException cause) {
			logger.warning(cause.getMessage());
		}
	}


	void handleCommand(String command) throws IOException {
		if(command.equals("close")) {
			out.writeUTF("ok");
			out.flush();
			stop = true;
			client.close();
			logger.fine("received stop command");
		}
		else if (command.equals("list modules")) {
			out.writeUTF("ok");
			out.writeObject(rwsi.getModuleNames());
			out.flush();
		}
		else if (command.equals("list communicators")) {
			out.writeUTF("ok");
			out.writeObject(rwsi.getCommunicatorNames());
			out.flush();
		}
		else if(command.equals("module properties")) {
			String name = in.readUTF();
			Module m = rwsi.getModule(name);
			if(m == null) {
				out.writeUTF("error");
			} else {
				out.writeUTF("ok");
				out.writeObject(m.getProperties());
			}
			out.flush();
		}
		else if(command.equals("communicator properties")) {
			String name = in.readUTF();
			Communicator c = rwsi.getCommunicator(name);
			if(c == null) {
				out.writeUTF("error");
			} else {
				out.writeUTF("ok");
				out.writeObject(c.getProperties());
			}
			out.flush();
		}
		else if(command.equals("get devices")) {
			out.writeUTF("ok");
			out.flush();
			Set<Device> devices = rwsi.getDevices();
			List<String> str = new ArrayList<String>();
			for(Device d: devices) {
				str.add(d.toString());
			}
			out.writeObject(str);
		}
		else {
			logger.warning("received unknown command: " + command);
			out.writeUTF("error");
			out.flush();
		}
	}

}
