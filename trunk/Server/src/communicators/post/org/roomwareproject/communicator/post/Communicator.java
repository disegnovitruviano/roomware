package org.roomwareproject.communicator.post; 

import org.roomwareproject.server.*;
import org.roomwareproject.utils.*;
import java.util.*;
import java.util.logging.*;
import java.io.*;
import java.beans.*;
import java.net.*;
import java.util.concurrent.*;


public class Communicator extends AbstractCommunicator {


	protected boolean doLoop = false;
	protected String ip;
	protected String host;
	protected int port;
	protected String path;
	protected InetAddress inetAddress;
	protected BlockingQueue<Map<String, String>> postings = 
		new LinkedBlockingQueue<Map<String, String>> ();


	public Communicator(Properties properties, RoomWareServer rws) throws RoomWareException {
		super(properties, rws);
		init();
	}


	public void messageReceived(MessageEvent messageEvent) {
		Message message = messageEvent.getMessage();
		Date date = messageEvent.getDate();
		Device source = (Device) messageEvent.getSource();
		String name = source.getFriendlyName();
		if(name == null) name = "(null)";
		Map <String, String> values = new HashMap <String, String> ();
		values.put("event", "messageReceived");
		values.put("message", message.toString());
		values.put("date", date.getTime() + "");
		values.put("deviceAddress", source.getDeviceAddress().toString());
		values.put("deviceName", name);
		postings.add(values);
	}

	public void propertyChange(PropertyChangeEvent event) {
		Object oldValue = event.getOldValue();
		if(oldValue == null) oldValue = "(null)";
		Object newValue = event.getNewValue();
		if(newValue == null) newValue = "(null)";
		Device source = (Device) event.getSource();
		String propertyName = event.getPropertyName();
		String name = source.getFriendlyName();
		if(name == null) name = "(null)";
		Map <String, String> values = new HashMap <String, String> ();
		values.put("event", "propertyChange");
		values.put("oldValue", oldValue.toString());
		values.put("newValue", newValue.toString());
		values.put("deviceAddress", source.getDeviceAddress().toString());
		values.put("deviceName", name);
		values.put("propertyName", propertyName);
		postings.add(values);
	}


	protected void postEvent(Map <String, String> values) {
		try {
			String data = "";
			for(String key: values.keySet()) {
				if(data.length() > 0) data += "&";
				data += URLEncoder.encode(key, "UTF-8") + "=";
				data += URLEncoder.encode(values.get(key), "UTF-8");
			}
			writePost(data);
		}
		catch (UnsupportedEncodingException cause) {
			logger.warning("Unsupported encoding!");
		}
	}


	protected void writePost(String data) {
		try {
			Socket socket = new Socket (inetAddress, port);
			BufferedWriter wr = new BufferedWriter (
				new OutputStreamWriter (socket.getOutputStream (), "UTF-8"));
			wr.write("POST " + path + " HTTP/1.0\r\n");
			if (host != null) {
				wr.write("Host: " + host + "\r\n");
			}
			wr.write("Content-Length: " + data.length() + "\r\n");
			wr.write("Content-Type: application/x-www-form-urlencoded\r\n");
			wr.write("\r\n");
			wr.write(data);
			wr.flush();
			wr.close();
			socket.close();
			logger.info ("event posted");
		}
		catch(IOException cause) {
			logger.warning (cause.getMessage());
		}
	}

	protected void init() throws RoomWareException {
		try {
			host = properties.getProperty("host");
			ip = properties.getProperty("ip");
			if(ip == null && host == null) {
				throw new RoomWareException("no ip or host defined!");
			}
			port = Integer.parseInt(properties.getProperty("port", "no-port-defined"));
			path = properties.getProperty("path", "/");
			inetAddress = InetAddress.getByName(ip != null ? ip : host);
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
		doLoop = true;
		while(doLoop) {
			try {
				logger.info("waiting for event...");
				postEvent(postings.take());
			}
			catch (InterruptedException cause) { }
		}
	}


	public synchronized void stop() {
		doLoop = false;
		notifyAll();
	}

}
