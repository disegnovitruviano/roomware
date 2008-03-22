package org.roomwareproject.communicator.admin.server;


import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;



public class WebService implements Runnable {

	private Socket client;
	private Logger logger;

	private PrintStream out;
	private Scanner in;

	public WebService(Socket client, Logger logger) {
		this.client = client;
		this.logger = logger;
	}

	public void run() {
		try {
			out = new PrintStream(client.getOutputStream());
			in = new Scanner(client.getInputStream());

			String line = in.nextLine();
			if(line.contains("index")) handleIndex();
			else if(line.contains("jar")) handleJar();
			else handleError();
			out.close();
			client.close();

		}
		catch(IOException cause) {
			logger.warning(cause.getMessage());
		}
	}

	private void handleError() throws IOException {
		out.println("HTTP/0.9 400 Bad Request");
		out.println("");
	}


	private void handleJar() throws IOException {
		InputStream in = getClass().getResourceAsStream("/docs/applet.jar");
		if(in == null) {
			logger.warning("Can't load JAR!");
			return;
		}
		URLConnection urlconn = getClass().getResource("/docs/applet.jar").openConnection();


		out.println("HTTP/1.1 200 OK");
		out.println("Server: RoomWare Admin Communicator");
		out.println("ETag: \"" + urlconn.getLastModified() + "\"");
		out.println("Accept-Ranges: bytes");
		out.println("Content-Length: " + urlconn.getContentLength());
		out.println("Content-Type: text/plain");
		out.println("");

		ByteArrayOutputStream memory = new ByteArrayOutputStream();
		int nbytes = 0;
		byte[] buffer = new byte[1024 * 64];
		while(nbytes != -1) {
			nbytes = in.read(buffer);
			if(nbytes > 0) memory.write(buffer, 0, nbytes);
		}
		memory.close();
		in.close();

		byte[] jarbytes = memory.toByteArray();

		out.write(jarbytes, 0, jarbytes.length);
	}


	private void handleIndex() throws IOException {
		out.println("HTTP/0.9 200 OK");
		out.println("Server: RoomWare Admin Communicator");
		out.println("Content-Type: text/html");
		out.println("");

		out.println(
					"<html><head><title>RoomWare: admin communicator interface</title></head>" +
					"<body><applet height=400 width=600 name='Admin RWC'" +
					"archive='applet.jar' code='org.roomwareproject.communicator.admin.applet.Applet' /> " +
					"</body></html>");
	}
}

