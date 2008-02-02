package roomware;

import java.beans.*;
import java.util.logging.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;


public class RoomWareServer implements RoomWareServerInterface {

	public synchronized List<Module> getModules() {
		return new ArrayList<Module>(modules.values());
	}


	public synchronized List<Communicator> getCommunicators() {
		return new ArrayList<Communicator>(communicators.values());
	}


	public void removeMessageListener(MessageListener listener) {
		List<Module> modules = getModules();
		for(Module module: modules) {
			module.removeMessageListener(listener);
		}
	}


	public void addMessageListener(MessageListener listener) {
		List<Module> modules = getModules();
		for(Module module: modules) {
			module.addMessageListener(listener);
		}
	}


	public void removePropertyChangeListener(PropertyChangeListener listener) {
		List<Module> modules = getModules();
		for(Module module: modules) {
			module.removePropertyChangeListener(listener);
		}
	}


	public void addPropertyChangeListener(PropertyChangeListener listener) {
		List<Module> modules = getModules();
		for(Module module: modules) {
			module.addPropertyChangeListener(listener);
		}
	}


	public synchronized Module getModule(String name) {
		return modules.get(name);
	}


	public synchronized Communicator getCommunicator(String name) {
		return communicators.get(name);
	}


	public synchronized Set<String> getModuleNames() {
		return new HashSet<String>(modules.keySet());
	}


	public synchronized Set<String> getCommunicatorNames() {
		return new HashSet<String>(communicators.keySet());
	}


	public void sendMessage(Device device, Message message)
		throws RoomWareException {
		boolean messageIsSent = false;
		Exception finalException = null;

		List<Module> modules = getModules();
		for(Module module: modules) {
			if(!messageIsSent) {
				try {
					module.sendMessage(device, message);
					messageIsSent = true;
				} catch(RoomWareException cause) {
				}
			}
		}

		if(!messageIsSent) {
			throw new RoomWareException("Could not send message!");
		}
	}


	public Set<Device> getDevices() {
		Set<Device> devices = new HashSet<Device>();

		List<Module> modules = getModules();
		for (Module module: modules) {
			devices.addAll(module.getDevices());
		}

		return devices;
	}


	public synchronized void stopServer() {
		stopServer = true;
		notify();
	}


	public final static String CONFIG_PROPERTY = "RoomWareConfig";

	protected Logger logger;
	protected RoomWareServerProperties properties;
	protected Map<String, Module> modules = new HashMap<String, Module>();
	protected Map<String, Communicator> communicators =
		new HashMap<String, Communicator>();

	protected boolean stopServer = false;
	protected boolean stopOnError = true;


	public RoomWareServer(RoomWareServerProperties properties) {
		this.logger = Logger.getLogger("roomware");
		this.properties = properties;
		parseProperties();
	}


	protected void parseProperties() {
		try {
			stopOnError = properties.getStopOnError();
		}
		catch(RoomWareException cause) {
			logger.severe(cause.getMessage());
			stopWithError();
		}

		loadModulesFromRoomWareServerProperties();
		loadCommunicatorsFromRoomWareServerProperties();
		logger.info("Properties parsed.");
	}


	protected void loadModulesFromRoomWareServerProperties() {
		String[] moduleNames = properties.getModules();
		for(String moduleName: moduleNames) {
			try {
				Properties moduleProperties =
					properties.getModuleProperties(moduleName);

				loadModule(moduleProperties);
			}

			catch(RoomWareException cause) {
				logger.warning(cause.getMessage());
				if(stopOnError) stopWithError();
			}
				
		}
	}


	protected void loadCommunicatorsFromRoomWareServerProperties() {
		try {
			String[] commNames = properties.getCommunicators();
			for(String commName: commNames) {
				Properties commProp = 
					properties.getCommunicatorProperties(commName);

				loadCommunicator(commProp);
			}
		}

		catch (RoomWareException cause) {
			logger.warning(cause.getMessage());
			if(stopOnError) stopWithError();
		}
	}


	public void loadCommunicator(Properties properties)
		throws RoomWareException {
		Class[] parameters = {Properties.class, RoomWareServerInterface.class};
		Object[] arguments = {properties, this};
		Communicator communicator = (Communicator)
			loadPlugin(parameters, arguments);
		attachCommunicator(communicator);
	}


	public void loadModule(Properties properties) throws RoomWareException {
		Class[] parameters = {Properties.class};
		Object[] arguments = {properties};
		Module module = (Module) loadPlugin(parameters, arguments);
		attachModule(module);
	}


	public synchronized void attachModule(Module module) throws RoomWareException {
		String name = module.getProperties().getProperty("name");
		if(name == null) name = module.toString();
		if(modules.containsKey(name)) {
			throw new RoomWareException("Duplicate module name: " + name);
		}
		modules.put(name, module);
	}


	public synchronized void attachCommunicator(Communicator communicator) throws RoomWareException {
		String name = communicator.getProperties().getProperty("name");
		if(name == null) name = communicator.toString();
		if(communicators.containsKey(name)) {
			throw new RoomWareException("Duplicate communicator name: " + name);
		}
		communicators.put(name, communicator);
	}


	@SuppressWarnings("unchecked")
	protected Plugin loadPlugin(Class[] parameters, Object[] arguments)
		throws RoomWareException {
		if(!(arguments[0] instanceof Properties)) {
			throw new RoomWareException("First argument should always be properties!");
		}
		Properties properties = (Properties) arguments[0];

		String name = properties.getProperty("name");
		if (name == null) {
			throw new RoomWareException("Properties has no name defined. Could not load plugin.");
		}

		String className = properties.getProperty("class");
		if (className == null) {
			throw new RoomWareException("Class definition not found for plugin " + name);
		}
		
		try {
			Class unknownClass = Class.forName(className);
			Class<Plugin> pluginClass = (Class<Plugin>)unknownClass;
			Constructor<Plugin> constructor = pluginClass.getConstructor (parameters);
			Plugin plugin = (Plugin) constructor.newInstance(arguments);
			Thread t = new Thread(plugin);
			t.setDaemon(true);
			t.setName(name);
			t.start();
			return plugin;
		} catch (NoSuchMethodException cause) {
			throw new RoomWareException(
				"Plugin " + name + " doesn't have the required constructor", cause);
		} catch(ClassNotFoundException cause) {
			throw new RoomWareException("Plugin " + name + " couldn't find class '" + className + "'", cause);
		} catch(IllegalAccessException cause) {
			throw new RoomWareException("Plugin " + name + " has constructor with wrong access level", cause);
		} catch(InvocationTargetException cause) {
			throw new RoomWareException("Plugin " + name + " has thrown exception while construction new instance: " + cause.getTargetException(), cause);
		} catch(InstantiationException cause) {
			throw new RoomWareException("Plugin " + name + " is a abstract class", cause);
		}
	}


	protected void start() {
		logger.info("RoomWare server has been started.");
		while(!stopServer) {
			synchronized(this) {
				try {
					logger.fine("RoomWareServer waits for exit.");
					wait();
				}

				catch (InterruptedException e) {
					logger.fine("RoomWareServer has been signaled.");
				}
			}
		}

		stop();
	}



	protected void stopWithError() {
		stop();
		logger.severe("RoomWare server stopped by error!");
		System.exit(1);
	}

	protected synchronized void stop() {
		List<Module> modules = getModules();
		for(Module mod: modules) {
			mod.stop();
		}

		List<Communicator> communicators = getCommunicators();
		for(Communicator comm: communicators) {
			comm.stop();
		}

		logger.info("RoomWare server has been stopped.");
	}


	public Properties getProperties() {
		return properties;
	}


	public static void main(String[] args) {
		String propertiesFile =
			System.getProperty(CONFIG_PROPERTY);

		try {
			RoomWareServerProperties properties =
				new RoomWareServerProperties();

			properties.load(new FileInputStream(propertiesFile));
			new RoomWareServer(properties).start();
		}

		catch(Exception e) {
			System.err.println("Could not open properties file: "
				+ propertiesFile);
			System.err.println(e.getMessage());
		}
	}
}
