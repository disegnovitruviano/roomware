package PROTO;

import roomware.*;
import java.util.*;
import java.util.logging.*;
import java.io.*;
import java.beans.*;


public class Module extends AbstractModule {


	protected boolean doLoop = true;


	public Module(Properties properties) throws RoomWareException {
		super(properties);
		init();
	}


	protected void init() throws RoomWareException {
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
					logger.warning("we are waked for unknown reason!");
				}
			}
		}
	}


	public synchronized void stop() {
		doLoop = false;
		notifyAll();
	}


	public synchronized Set<Device> getDevices() {
		return new HashSet<Device> ();
	}


	public void sendMessage(Device device, Message message)
		throws RoomWareException {
			throw new RoomWareException("Operation not supported!");
		}
}
