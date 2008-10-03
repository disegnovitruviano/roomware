package org.roomwareproject.utils;

import java.util.logging.*;
import java.util.*;
import java.beans.*;

import org.roomwareproject.server.*;


public abstract class AbstractModule implements Module {

	protected Logger logger;
	protected Properties properties;
	protected Set<PropertyChangeListener> propertyChangeListeners;
	protected Set<MessageListener> messageListeners;

	public AbstractModule(Properties properties) {
		this.properties = properties;
		String loggerName = properties.getProperty("logger", "Modules");
		this.logger = Logger.getLogger(loggerName);
		propertyChangeListeners = new HashSet<PropertyChangeListener>();
		messageListeners = new HashSet<MessageListener>();
	}

	public Properties getProperties() {
		return properties;
	}


	protected void propertyChange(PropertyChangeEvent event) {
		for(PropertyChangeListener l: propertyChangeListeners) {
			l.propertyChange(event);
		}
	}


	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeListeners.add(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeListeners.remove(listener);
	}

	public void addMessageListener(MessageListener listener) {
		messageListeners.add(listener);
	}

	public void removeMessageListener(MessageListener listener) {
		messageListeners.remove(listener);
	}

}
