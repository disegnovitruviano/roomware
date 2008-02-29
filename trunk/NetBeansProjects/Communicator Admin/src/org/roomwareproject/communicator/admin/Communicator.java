package org.roomwareproject.communicator.admin;

import org.roomwareproject.communicator.admin.server.*;
import org.roomwareproject.server.*;
import java.util.*;
import java.io.*;
import java.util.logging.*;
import java.beans.*;



public class Communicator extends AbstractCommunicator {

	public final static String
		DEFAULT_APPLET_SERVER_PORT_PROPERTY = "4101",
		DEFAULT_WEB_SERVER_PORT_PROPERTY = "4080";

	protected boolean doLoop;
	private AppletServer appletServer;
	private WebServer webServer;

	public Communicator(Properties properties, RoomWareServer server) throws RoomWareException {
		super(properties, server);
		init();
	}


	protected void init() throws RoomWareException {
		doLoop = true;
		try {
			int appletPort = Integer.parseInt(
						properties.getProperty("applet-server-port", 
						 DEFAULT_APPLET_SERVER_PORT_PROPERTY));
			properties.setProperty("applet-server-port", appletPort + "");

			int webPort = Integer.parseInt(
						properties.getProperty("web-server-port",
						 DEFAULT_WEB_SERVER_PORT_PROPERTY));
			properties.setProperty("web-server-port", webPort + "");

			appletServer = new AppletServer(roomwareServer, appletPort, logger);
			webServer = new WebServer(webPort, logger);
		}
		catch(IOException cause) {
			throw new RoomWareException("Could not start servers: "
										+ cause.getMessage());
		}
		catch(NumberFormatException cause) {
			throw new RoomWareException("Could not parse property: "
										+ cause.getMessage());
		}
	}


	public void run() {
		ThreadGroup appletServers = new ThreadGroup("AppletServers");
		appletServers.setDaemon(true);
		new Thread(appletServers, appletServer).start();

		ThreadGroup webServers = new ThreadGroup("WebServers");
		webServers.setDaemon(true);
		new Thread(webServers, webServer).start();
	}


	public void stop() {
		doLoop = false;
		appletServer.stop();
		webServer.stop();
	}
	

	public void propertyChange(PropertyChangeEvent changeEvent) {
	}


	public void messageReceived(MessageEvent messageEvent) {
	}

}
