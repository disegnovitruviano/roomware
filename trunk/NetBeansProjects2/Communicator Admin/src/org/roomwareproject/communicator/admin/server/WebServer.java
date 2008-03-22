package org.roomwareproject.communicator.admin.server;


import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;



public class WebServer implements Runnable {


	private ServerSocket serverSocket;
	private boolean stop = false;
	private Logger logger;


	public WebServer(int port, Logger logger) throws IOException {
		serverSocket = new ServerSocket(port);
		this.logger = logger;
	}


	public void run() {
		while(!stop) {
			try {
				Socket client = serverSocket.accept();
				WebService service = new WebService(client, logger);
				new Thread(service).start();
			}
			catch(IOException cause) {
				logger.warning("WebServer says: " + cause.getMessage());
			}
		}
	}


	public void stop() {
		stop = true;
		try {
			serverSocket.close();
		} catch(IOException cause) {
			logger.warning(cause.getMessage());
		}
	}

}
