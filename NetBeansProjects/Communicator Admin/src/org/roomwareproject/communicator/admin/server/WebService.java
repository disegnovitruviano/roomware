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
		URL url = getClass().getResource("/applet.jar");
		URLConnection urlconn = url.openConnection();
		InputStream in = urlconn.getInputStream();
		long length = urlconn.getContentLength();

		out.println("HTTP/1.1 200 OK");
		out.println("Server: RoomWare Admin Communicator");
		out.println("ETag: \" + urlconn.getDate() + \"");
		out.println("Accept-Ranges: bytes");
		out.println("Content-Length: " + length);
		out.println("Content-Type: text/plain");
		out.println("");

		long bytes_read = 0;
		int chunk = 1024 * 16;
		byte[] buffer = new byte[chunk];

		while(bytes_read < length) {
			int nbytes = in.read(buffer);
			out.write(buffer, 0, nbytes);
			bytes_read += nbytes;
		}

		in.close();
	}


	private void handleIndex() throws IOException {
		out.println("HTTP/0.9 200 OK");
		out.println("Server: RoomWare Admin Communicator");
		out.println("Content-Type: text/html");
		out.println("");

		out.println(
					"<html><head><title>RoomWare: admin communicator interface</title></head>" +
					"<body><applet height=400 width=600 name='Admin RWC'" +
					"archive='admin.jar' code='admin.applet.Applet' /> " +
					"</body></html>");
	}
}

