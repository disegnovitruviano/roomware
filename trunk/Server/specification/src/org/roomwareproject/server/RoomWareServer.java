package org.roomwareproject.server;

import java.beans.*;
import java.util.*;

public interface RoomWareServer {

	public void addPropertyChangeListener(PropertyChangeListener listener);
	public void removePropertyChangeListener(PropertyChangeListener listener);

	public void addMessageListener(MessageListener listener);
	public void removeMessageListener(MessageListener listener);
	
	public Properties getProperties();

	public Set<Device> getDevices();
	public void sendMessage(Device destination, Message message) throws RoomWareException;

	public List<Module> getModules();
	public Set<String> getModuleNames();
	public Communicator getCommunicator(String name);
	public void loadModule(Properties properties) throws RoomWareException;
	public void attachModule(Module module) throws RoomWareException;

	public List<Communicator> getCommunicators();
	public Set<String> getCommunicatorNames();
	public Module getModule(String name);
	public void loadCommunicator(Properties properties) throws RoomWareException;
	public void attachCommunicator(Communicator communicator) throws RoomWareException;

	public void stopServer();

}
