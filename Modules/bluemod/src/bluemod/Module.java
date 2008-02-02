package bluemod;

import roomware.*;
import java.util.*;
import java.util.logging.*;
import java.io.*;
import java.beans.*;
import javax.bluetooth.*;


public class Module extends AbstractModule implements DiscoveryListener {

	protected DiscoveryAgent agent;
	protected Map<BluetoothAddress, Device> devices = new HashMap<BluetoothAddress, Device>();
	protected Map<Device, Date> times = new HashMap<Device, Date>();

	protected int scanInterval;
	protected int clearTime;
	protected boolean doLoop;
	protected boolean isScanning;
	protected boolean alwaysAsk;

	public Module(Properties properties) throws RoomWareException {
		super(properties);
		init();
	}

	public void servicesDiscovered(int b, ServiceRecord[] a) { }
	public void serviceSearchCompleted(int a, int b) { }

	protected void init() throws RoomWareException {
		try {
			scanInterval = 60;
			clearTime = 60;
			alwaysAsk = false;

			agent = LocalDevice.getLocalDevice().getDiscoveryAgent();

			String value = properties.getProperty("update-interval-time");
			if(value != null) scanInterval = Integer.parseInt(value);
			else properties.setProperty("update-interval-time", 
										scanInterval + "");

			value = properties.getProperty("device-clear-time");
			if(value != null) clearTime = Integer.parseInt(value);
			else properties.setProperty("device-clear-time",
										clearTime + "");

			value = properties.getProperty("always-ask-name");
			if(value != null) {
				if(value.equals("true")) {
					alwaysAsk = true;
				}
				else if(value.equals("false")) {
					alwaysAsk = false;
				}
				else {
					logger.severe("always-ask-name should be 'true' or 'false'!");
					throw new RoomWareException("Wrong always-ask-name value!");
				}	
			}
			else properties.setProperty("always-ask-name", "false");
		}
		catch (NumberFormatException cause) {
			logger.severe(cause.getMessage());
			throw new RoomWareException("wrong update-intervale-time or device-clear-time!");
		}
		catch (BluetoothStateException cause) {
			logger.severe(cause.getMessage());
			throw new RoomWareException("Could not init bluetooth!");
		}

		logger.info("update-time-interval = " + scanInterval);
		logger.info("device-clear-time = " + clearTime);
		logger.info("always-ask-name = " + alwaysAsk);

		doLoop = true;
		isScanning = false;
	}


	public synchronized void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass clazz) {
		String name = null;
		try {
	   		name = remoteDevice.getFriendlyName(alwaysAsk);
		} catch(IOException cause) {
			logger.warning("Could not obtain friendly name of device!");
		}

		BluetoothAddress address = new BluetoothAddress(remoteDevice.getBluetoothAddress());
		Device entry = devices.get(address);

		// We have a new device!
		if(entry == null) {
			Device device = new Device(address);
			device.setFriendlyName(name);

			devices.put(address, device);
			times.put(device, new Date());

			propertyChange(new PropertyChangeEvent(device, "in range", false, true));
		}
		
		// Check for new properties
		else {
			// update seen time
			times.put(entry, new Date());

			if(name != null) {
				String oldName = entry.getFriendlyName();
				if(!oldName.equals(name)) {
					entry.setFriendlyName(name);
					propertyChange(new PropertyChangeEvent(entry, "name", oldName, name));
				}
			}
		}
	}


	public synchronized void inquiryCompleted(int discType) {
		isScanning = false;

		switch(discType) {
			case (DiscoveryListener.INQUIRY_ERROR):
				logger.warning("inquiry has been stopped due to error!");
				break;

			case (DiscoveryListener.INQUIRY_TERMINATED):
				logger.warning("inquiry has been stopped by time out!");
				break;

			case (DiscoveryListener.INQUIRY_COMPLETED):
				break;
		}
	}


	protected synchronized void cleanDeviceList() {
		Date clearDate = new Date(new Date().getTime() - (clearTime * 1000));

		Iterator<Map.Entry<Device, Date>> iterator = times.entrySet().iterator();

		while(iterator.hasNext()) {
			Map.Entry<Device, Date> entry = iterator.next();
			Device device = entry.getKey();
			Date date = entry.getValue();

			if(date.before(clearDate)) {
				iterator.remove();
				devices.remove(device.getDeviceAddress());
				propertyChange(new PropertyChangeEvent(device, "in range", true, false));
			}
		}
	}


	public void run() {
		while(doLoop) {
			try {
				agent.startInquiry(DiscoveryAgent.GIAC, this);
				isScanning = true;
			}
			catch(BluetoothStateException cause) {
				logger.warning("Could not start bluetooth inquiry!");
			}

			try {
				synchronized(this) {
					wait(scanInterval * 1000);
				}
			}

			catch(InterruptedException cause) {
				if(doLoop) {
					logger.warning("we are waked for unknown reason!");
				}
			}

			if(isScanning) {
				agent.cancelInquiry(this);
			}

			cleanDeviceList();
		}
	}


	public synchronized void stop() {
		doLoop = false;
		notifyAll();
	}


	public synchronized Set<Device> getDevices() {
		return new HashSet<Device> (times.keySet());
	}


	public void sendMessage(Device device, Message message)
		throws RoomWareException {
			throw new RoomWareException("Operation not supported!");
		}
}
