package org.roomwareproject.module.oscrfid;

import org.roomwareproject.server.*;
import org.roomwareproject.utils.*;
import java.util.*;
import java.util.logging.*;
import java.io.*;
import java.beans.*;
import de.sciss.net.*;
import java.nio.channels.*;
import java.net.*;


public class Module extends AbstractModule implements OSCListener {

	protected boolean doLoop = false;
	protected String zone;
	protected SocketAddress socketAddress;
	protected Set<Presence>presences = new HashSet<Presence>();
	OSCReceiver     rcv     = null;
	OSCTransmitter  trns;
	DatagramChannel dch     = null;

	public Module(Properties properties) throws RoomWareException {
		super(properties);
		init();
	}


	public void messageReceived(OSCMessage msg, SocketAddress sender, long time) {
		logger.info("message from " + zone + ": " + msg.getName());

		if (msg.getName().equals("/tag/enter")) {
			if (msg.getArgCount() < 2) {
				logger.warning("we got not enough arguments in OSCMessage!");
			}
			OSCTagDeviceAddress da = new OSCTagDeviceAddress(msg.getArg(0), msg.getArg(1));
			Presence p = new Presence (this, zone, new Device(da), new Date(time));
			presences.add(p);
			propertyChange(new PropertyChangeEvent(p.getDevice (), "in range", null, p.getZone ()));
		}

		if (msg.getName().equals("/tag/leave")) {
			if (msg.getArgCount() < 2) {
				logger.warning("we got not enough arguments in OSCMessage!");
			}
			OSCTagDeviceAddress da = new OSCTagDeviceAddress(msg.getArg(0), msg.getArg(1));
			Presence p = new Presence (this, zone, new Device(da), new Date(time));
			presences.remove(p);
			propertyChange(new PropertyChangeEvent(p.getDevice (), "in range", p.getZone(), null));
		}
	}


	protected void init() throws RoomWareException {
		try {
			String ip = properties.getProperty("ip", "127.0.0.1");
			int port = Integer.parseInt(properties.getProperty("port"));
			zone = properties.getProperty("zone", "osc").trim();

			socketAddress = new InetSocketAddress(ip, port);

			// OSC config
			dch     = DatagramChannel.open();
			dch.socket().bind(socketAddress); // assigns an automatic local socket address
			rcv     = OSCReceiver.newUsing(dch);
			trns    = OSCTransmitter.newUsing(dch);
			
			rcv.addOSCListener(this);
			logger.info("oscmod '" + zone + "' is listening on " + ip + ":" + port);
			doLoop = true;
		}
		catch (NumberFormatException cause) {
			logger.severe(cause.getMessage());
			throw new RoomWareException("unable to parse port number!");
		}
		catch (IOException cause) {
		  logger.warning(cause.getLocalizedMessage());
		}
	}


	public void run() {
		try {
			rcv.startListening();
		}
		catch (IOException cause) {
			logger.warning(cause.getLocalizedMessage());
			doLoop = false;
		}
		while(doLoop) {
			try {
				synchronized (this) {
					wait();
				}
			}
			catch (InterruptedException cause) {
				if (doLoop)
					logger.warning("We are waked for no reason!");
			}
		}
	}

	public synchronized void stop() {
		if (rcv != null) {
		  rcv.dispose();
		}
		else if( dch != null ) {
		  try {
			  dch.close();
		  }
		  catch(IOException cause) {
			  logger.warning(cause.getLocalizedMessage());
		  };
		}
		doLoop = false;
		notifyAll();
	}


	public synchronized Set<Presence> getPresences() {
		return presences;
	}


	public void sendMessage(Device device, Message message)
		throws RoomWareException {
			throw new RoomWareException("Operation not supported!");
		}

}
