package org.roomwareproject.server.impl;

import org.roomwareproject.server.*;
import java.io.*;
import java.util.*;


public class RoomWareServerProperties extends java.util.Properties {

	private static final long serialVersionUID = 9119119237651L;

	public RoomWareServerProperties() {
		super();
	}

	public RoomWareServerProperties(Properties defaultProperties) {
		super(defaultProperties);
	}

	public Properties getModuleProperties(String moduleName) {
		Properties properties = getPropertiesStartWith(moduleName);
		properties.setProperty("name", moduleName);
		return properties;
	}


	public Properties getCommunicatorProperties(String communicatorName) {
		Properties properties = getPropertiesStartWith(communicatorName);
		properties.setProperty("name", communicatorName);
		return properties;
	}


	protected Properties getPropertiesStartWith(String prefix) {
		Properties properties = new Properties();
		for(
			Enumeration<?> keys = propertyNames();
			keys.hasMoreElements();
		) {
			String key = (String) keys.nextElement();

			if(key.startsWith(prefix + "-")) {
				String newKey = key.substring(key.indexOf('-')
							+ 1);
				properties.setProperty(newKey,
						getProperty(key));
			}
		}

		return properties;	
	}


	public String[] getModules() {
		return getList("modules");
	}


	public String[] getCommunicators() {
		return getList("communicators");
	}


	public boolean getStopOnError() throws RoomWareException {
		String value = getProperty("stop-on-error");
		if(value == null) return true;

		if(value.equals("true")) return true;
		if(value.equals("false")) return false;

		throw new RoomWareException("Could not understand stop-on-error value! '" + value + "'");
	}


	protected String[] getList(String property) {
		String list = getProperty(property);
		if(list == null) return new String[0];

		Scanner names = new Scanner(list);
		names.useDelimiter(",");
		Set<String> entries = new HashSet<String>();

		while(names.hasNext()) {
			entries.add(names.next().trim());
		}

		return entries.toArray(new String[0]);
	}
	
}
