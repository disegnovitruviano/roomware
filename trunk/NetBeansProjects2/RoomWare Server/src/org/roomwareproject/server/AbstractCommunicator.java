package org.roomwareproject.server;

import java.util.logging.*;
import java.util.*;
import java.beans.*;

public abstract class AbstractCommunicator
	implements Communicator, PropertyChangeListener, MessageListener {

	protected RoomWareServer roomwareServer;
	protected Logger logger;
	protected Properties properties;

	public AbstractCommunicator(Properties properties, RoomWareServer roomwareServer) {
		this.properties = properties;
		this.roomwareServer = roomwareServer;
		String loggerName = properties.getProperty("logger", "Communicators");
		this.logger = Logger.getLogger(loggerName);
		roomwareServer.addPropertyChangeListener(this);
		roomwareServer.addMessageListener(this);
	}

	public Properties getProperties() {
		return properties;
	}

}
