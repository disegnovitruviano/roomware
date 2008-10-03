package org.roomwareproject.communicator.console;

import org.roomwareproject.server.*;
import org.roomwareproject.utils.*;
import java.util.*;
import java.io.*;
import java.util.logging.*;
import java.beans.*;

public class Communicator extends AbstractCommunicator {

	protected boolean doLoop;


	public Communicator(Properties properties, RoomWareServer server) throws RoomWareException {
		super(properties, server);
		init();
	}


	protected void init() throws RoomWareException {
		doLoop = true;
	}


	public void run() {
		Scanner stdin = new Scanner(System.in);

		while(doLoop) {
			try {
				System.out.print("console: ");
				String command = stdin.next();
				command.trim();
				if(command.equals("stop")) {
					stopCommand();
				}
				else if(command.equals("list")) {
					listCommand();
				}
				else if(command.equals("help")) {
					helpCommand();
				}
			}
			catch(Exception e) {
				logger.warning(e.getMessage());
			}
		}
	}


	protected void helpCommand() {
		System.out.println("Console Communicator help");
		System.out.println("list	- list device presences");
		System.out.println("help	- print this message");
		System.out.println("stop	- stop the server");
	}


	protected void listCommand() {
		Set<Presence> presences = roomwareServer.getPresences();
		for(Presence p: presences) {
			Device d = p.getDevice ();
			DeviceAddress address = d.getDeviceAddress();
			String name = d.getFriendlyName();

			String line = "" + address;
			if(name != null) {
				line += " : " + name;
			}
			line += " at " + p.getDetectTime () + ", zone = " + p.getZone ();
			System.out.println(line);
		}
	}


	protected void stopCommand() {
		roomwareServer.stopServer();
	}


	public void stop() {
		doLoop = false;
	}
	
	public void propertyChange(PropertyChangeEvent changeEvent) {
	}

	public void messageReceived(MessageEvent messageEvent) {
	}

}
