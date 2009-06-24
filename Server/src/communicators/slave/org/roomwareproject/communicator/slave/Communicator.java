package org.roomwareproject.communicator.slave; 

import org.roomwareproject.server.*;
import org.roomwareproject.utils.*;
import java.util.*;
import java.util.logging.*;
import java.util.concurrent.*;
import java.io.*;
import java.beans.*;
import java.net.*;


public class Communicator extends AbstractCommunicator {

	protected boolean doLoop = true;
	protected String host;
	protected int port;
	protected InetAddress inetAddress;
	protected BlockingQueue<PropertyChangeEvent> eventQueue = 
		new LinkedBlockingQueue<PropertyChangeEvent>();


	public Communicator(Properties properties, RoomWareServer rws) throws RoomWareException {
		super(properties, rws);
		init();
	}

	public void messageReceived(MessageEvent messageEvent) {
		/* TODO
		 * built in message forwarding support
		 */
		logger.info ("Discarding message due to lack of support in this RWS version!");
	}

	public void propertyChange(PropertyChangeEvent event) {
		eventQueue.add(event);
	}

	protected void init() throws RoomWareException {
		try {
			host = properties.getProperty("host");
			port = Integer.parseInt(
				properties.getProperty("port", "4003"));
			inetAddress = InetAddress.getByName (host);
		}
		catch(IOException cause) {
			throw new RoomWareException("Can't resolve host name");
		}
		catch(NumberFormatException cause) {
			throw new RoomWareException("Could not parse property: "
										+ cause.getMessage());
		}
	}


	public void run() {
		while(doLoop) {
			try {
				Socket socket = new Socket (inetAddress, port);
				ObjectOutputStream out = new ObjectOutputStream (socket.getOutputStream());
				logger.info("socket opened");
				Set<Presence> presences = roomwareServer.getPresences();
				out.writeInt(presences.size());
				for (Presence p: presences) {
					out.writeObject(p.getZone());
					out.writeObject(p.getDevice());
					out.writeObject(p.getDetectTime());
				}
				out.flush();

				logger.info("device list written");

				while(doLoop) {
					try {
						PropertyChangeEvent event = eventQueue.take();
						out.writeObject(event);
						out.writeObject(event.getSource());
						out.flush();
						logger.info("event written to master");
					}
					catch (InterruptedException cause) { }
				}

				out.close();
				socket.close();
				logger.info("socket closed");
			}
			catch (IOException cause) {
				logger.warning(cause.getMessage());
			}

			try {
				synchronized(this) {
					wait(1000);
				}
			}
			catch (InterruptedException cause) { }
		}
	}


	public synchronized void stop() {
		doLoop = false;
		notifyAll();
	}

}
