package org.roomwareproject.communicator.httpd;

import org.roomwareproject.server.*;
import org.roomwareproject.utils.*;
import java.util.*;
import java.util.logging.*;
import java.io.*;
import java.beans.*;
import java.net.*;


public class Communicator extends AbstractCommunicator {


	public final static String
		DEFAULT_WEB_SERVER_PORT_PROPERTY = "4040";

	protected boolean doLoop = true;
	protected ServerSocket socket;


	public Communicator(Properties properties, RoomWareServer rws) throws RoomWareException {
		super(properties, rws);

		init();
	}


	public void messageReceived(MessageEvent messageEvent) { }

	public void propertyChange(PropertyChangeEvent event) { }


	protected void init() throws RoomWareException {
		try {
			int webPort = Integer.parseInt(
								  properties.getProperty("web-server-port",
								  DEFAULT_WEB_SERVER_PORT_PROPERTY));
			properties.setProperty("web-server-port", webPort + "");

			socket = new ServerSocket(webPort);
		}
		catch(IOException cause) {
			throw new RoomWareException("Can't start server port");
		}
		catch(NumberFormatException cause) {
			throw new RoomWareException("Could not parse property: "
										+ cause.getMessage());
		}
	}


	public void run() {
		while(doLoop) {
			try {
				Socket client = socket.accept();
				Thread t = new Thread(new WebService(roomwareServer, client, logger));
				t.start();
			}

			catch(IOException cause) {
				logger.warning("we have some problems: " + cause.getMessage());
			}
		}
	}


	public synchronized void stop() {
		doLoop = false;
		notifyAll();
	}

}
