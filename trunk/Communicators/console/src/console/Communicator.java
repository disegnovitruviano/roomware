package console;

import roomware.*;
import java.util.*;
import java.io.*;
import java.util.logging.*;
import java.beans.*;

public class Communicator extends AbstractCommunicator {

	protected boolean doLoop;


	public Communicator(Properties properties, RoomWareServerInterface server) throws RoomWareException {
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
		System.out.println("list	- list devices");
		System.out.println("help	- print this message");
		System.out.println("stop	- stop the server");
	}


	protected void listCommand() {
		Set<Device> devices = roomwareServer.getDevices();
		for(Device d: devices) {
			DeviceAddress address = d.getDeviceAddress();
			String name = d.getFriendlyName();

			String line = "" + address;
			if(name != null) {
				line += " : " + name;
			}
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
