package httpcomm;

import roomware.*;
import java.util.*;
import java.io.*;
import java.net.*;
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
		while(doLoop) {
			try {
				synchronized(this) {
					wait();
				}
			}
			catch(InterruptedException cause) {
				if(doLoop) {
					logger.warning("Unknown interrupt!");
				}
			}
		}
	}


	public synchronized void stop() {
		doLoop = false;
		notifyAll();
	}
	
	public void propertyChange(PropertyChangeEvent changeEvent) {
	}

	public void messageReceived(MessageEvent messageEvent) {
	}

}
