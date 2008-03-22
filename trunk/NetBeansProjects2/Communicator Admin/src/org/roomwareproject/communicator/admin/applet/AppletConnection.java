package org.roomwareproject.communicator.admin.applet;


import java.io.*;
import java.net.*;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.*;


public class AppletConnection implements Runnable {


	protected final static int
		ACTION_CONNECT = 1,
		ACTION_DISCONNECT = 2,
		ACTION_STOP = 3,
		ACTION_LIST_MODULES = 4,
		ACTION_LIST_COMMUNICATORS = 5,
		ACTION_COMMUNICATOR_PROPERTIES = 6,
		ACTION_MODULE_PROPERTIES = 7,
		ACTION_GET_DEVICES = 8;

	protected Socket socket;
	protected Properties properties;
	protected String name;
	protected ObjectInputStream in;
	protected ObjectOutputStream out;
	protected boolean doLoop;
	protected LinkedBlockingQueue<Integer> queue;
	protected String hostname;
	protected int port;
	protected Set<String> moduleNames;
	protected Set<String> commNames;
	protected Set<String> devices;

	protected Applet applet;

	protected Set<ActionListener> listeners;

	public AppletConnection(Applet applet) {
		doLoop = true;
		queue = new LinkedBlockingQueue<Integer>();
		listeners = new HashSet<ActionListener>();
		this.applet = applet;
	}


	public synchronized void addActionListener(ActionListener listener) {
		listeners.add(listener);
	}


	public synchronized void removeActionListener(ActionListener listener) {
		listeners.remove(listener);
	}


	public void run() {
		while(doLoop) {
			try {
				int action = queue.take();

				if(action == ACTION_CONNECT) {
					doConnect();
				} else if(action == ACTION_DISCONNECT) {
					doDisconnect();
				} else if(action == ACTION_STOP) {
					doStop();
				} else if(action == ACTION_LIST_MODULES) {
					doListModules();
				} else if(action == ACTION_LIST_COMMUNICATORS) {
					doListCommunicators();
				} else if(action == ACTION_COMMUNICATOR_PROPERTIES) {
					doCommunicatorProperties();
				} else if(action == ACTION_MODULE_PROPERTIES) {
					doModuleProperties();
				} else if(action == ACTION_GET_DEVICES) {
					doGetDevices();
				}
			}
			catch(InterruptedException cause) {
			}
		}
	}


	protected synchronized void postEvent(ActionEvent action) {
		for(ActionListener listener: listeners) {
			listener.actionPerformed(action);
		}
	}


	public void doConnect() {
		try {
			URL url = applet.getCodeBase();
			if(url == null) hostname = "localhost";
			else hostname = url.getHost();
			if(hostname == null) hostname = "localhost";

			socket = new Socket(hostname, port);
			socket.setSoTimeout(2000);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			postEvent(new ActionEvent(this, ACTION_CONNECT, "connected"));
		}
		catch(IOException cause) {
			postEvent(new ActionEvent(this, ACTION_DISCONNECT, "error: " + cause.getMessage()));
		}
	}


	public void doDisconnect() {
		String message = "disconnected";

		try {
			out.writeUTF("close");
			out.flush();

			String response = in.readUTF();
			if(!response.equals("ok")) {
				throw new IOException("wrong repsonse: " + response);
			}
		} catch(IOException cause) {
			message = "disconnected, warning: " + cause.getMessage();
		}

		postEvent(new ActionEvent(this, ACTION_DISCONNECT, message));
	}


	public void doStop() {
		doLoop = false;
		postEvent(new ActionEvent(this, ACTION_STOP, "stop"));
	}

	public synchronized void connect(int port) {
		this.port = port;
		queue.offer(ACTION_CONNECT);
	}	


	public synchronized void disconnect() {
		queue.offer(ACTION_DISCONNECT);
	}


	public synchronized void stop() {
		queue.offer(ACTION_STOP);
	}

	
	public void doGetDevices() {
		try {
			out.writeUTF("get devices");
			out.flush();

			String response = in.readUTF();
			if(!response.equals("ok")) throw new IOException();

			synchronized(devices) {
				devices.clear();
				devices.addAll((List<String>)in.readObject());
			}

			postEvent(new ActionEvent(this, ACTION_GET_DEVICES, "done"));
		}
		catch(IOException cause) {
		}
		catch(ClassNotFoundException cause) {
		}
	}


	public void doListModules() {
		try {
			out.writeUTF("list modules");
			out.flush();

			String response = in.readUTF();
			if(!response.equals("ok")) throw new IOException();

			synchronized(moduleNames) {
				moduleNames.clear();
				moduleNames.addAll((Set<String>)in.readObject());
			}

			postEvent(new ActionEvent(this, ACTION_LIST_MODULES, "done"));
		}
		catch(IOException cause) {
		}
		catch(ClassNotFoundException cause) {
		}
	}


	public void doListCommunicators() {
		try {
			out.writeUTF("list communicators");
			out.flush();

			String response = in.readUTF();
			if(!response.equals("ok")) throw new IOException();

			synchronized(commNames) {
				commNames.clear();
				commNames.addAll((Set<String>)in.readObject());
			}

			postEvent(new ActionEvent(this, ACTION_LIST_COMMUNICATORS, "done"));
		}
		catch(IOException cause) {
		}
		catch(ClassNotFoundException cause) {
		}
	}


	public void doCommunicatorProperties() {
		try {
			out.writeUTF("communicator properties");
			out.writeUTF(name);
			out.flush();

			String response = in.readUTF();
			if(!response.equals("ok")) throw new IOException();

			synchronized(properties) {
				properties.clear();
				Properties pin = (Properties)in.readObject();
				for(Enumeration<?> keys = pin.propertyNames();
					keys.hasMoreElements();
				) {
					String key = (String) keys.nextElement();
					String value = pin.getProperty(key);
					properties.setProperty(key, value);
				}
			}

			postEvent(new ActionEvent(this, ACTION_COMMUNICATOR_PROPERTIES, "done"));
		}
		catch(IOException cause) {
		}
		catch(ClassNotFoundException cause) {
		}
	}


	public void doModuleProperties() {
		try {
			out.writeUTF("module properties");
			out.writeUTF(name);
			out.flush();

			String response = in.readUTF();
			if(!response.equals("ok")) throw new IOException();

			synchronized(properties) {
				properties.clear();
				Properties pin = (Properties)in.readObject();
				for(Enumeration<?> keys = pin.propertyNames();
					keys.hasMoreElements();
				) {
					String key = (String)keys.nextElement();
					String value = pin.getProperty(key);
					properties.setProperty(key, value);
				}
			}

			postEvent(new ActionEvent(this, ACTION_MODULE_PROPERTIES, "done"));
		}
		catch(IOException cause) {
		}
		catch(ClassNotFoundException cause) {
		}
	}


	public synchronized void communicatorProperties(String name, Properties properties) {
		this.name = name;
		this.properties = properties;
		queue.offer(ACTION_COMMUNICATOR_PROPERTIES);
	}


	public synchronized void moduleProperties(String name, Properties properties) {
		this.name = name;
		this.properties = properties;
		queue.offer(ACTION_MODULE_PROPERTIES);
	}


	public synchronized void listCommunicators(Set<String> commNames) {
		this.commNames = commNames;
		queue.offer(ACTION_LIST_COMMUNICATORS);
	}


	public synchronized void listModules(Set<String> moduleNames) {
		this.moduleNames = moduleNames;
		queue.offer(ACTION_LIST_MODULES);
	}

	public synchronized void getDevices(Set<String> devices) {
		this.devices = devices;
		queue.offer(ACTION_GET_DEVICES);
	}
}
