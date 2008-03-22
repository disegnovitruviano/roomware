package org.roomwareproject.communicator.admin.server;


import org.roomwareproject.server.*;
import java.io.*;
import java.net.*;
import java.util.logging.*;



public class AppletServer implements Runnable {

	private RoomWareServer rwsi;
	private ServerSocket socket;
	private boolean stop = false;
	private Logger logger;


	public AppletServer(RoomWareServer rwsi, int port, Logger logger)
		throws IOException {
		this.rwsi = rwsi;
		this.socket = new ServerSocket(port);
		this.logger = logger;
	}


	public void run() {
		try {
			while(!stop) {
				Socket client = socket.accept();
				AppletService service =
					new AppletService(client, rwsi, logger);
				new Thread(service).start();
			}
		}
		catch(IOException cause) {
			logger.warning(cause.getMessage());
		}
	}


	public void stop() {
		stop = true;
		try {
			socket.close();
		} catch(IOException cause) {
			logger.warning(cause.getMessage());
		}
	}

}
