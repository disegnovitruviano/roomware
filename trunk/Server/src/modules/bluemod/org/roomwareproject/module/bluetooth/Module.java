package org.roomwareproject.module.bluetooth;

import org.roomwareproject.server.*;
import org.roomwareproject.utils.*;
import java.util.*;
import java.util.logging.*;
import java.io.*;
import java.beans.*;
import javax.bluetooth.*;

public class Module extends AbstractModule implements DiscoveryListener {

	protected DiscoveryAgent agent; 

	protected LinkedList<BluetoothDevice> unknownDevices = new LinkedList<BluetoothDevice> ();
	protected LinkedList<BluetoothDevice> knownDevices = new LinkedList<BluetoothDevice> ();

	protected Map<BluetoothDevice, String> friendlyNameCache = new HashMap<BluetoothDevice, String> ();
	protected PresenceCacheList presenceCacheList;

	public final static int
		NAME_LOOKUP_POLICY_NEVER = 0,
	NAME_LOOKUP_POLICY_ONCE = 1,
	NAME_LOOKUP_POLICY_ALWAYS = 2;

	protected int haltOnNErrors = -1;
	protected int errorCount = 0;

	protected Date inquiryStartTime;
	protected int scanInterval;
	protected int clearTime;
	protected boolean doLoop;
	protected boolean isScanning;
	protected int nameLookupPolicy;
	protected String zone;

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
			nameLookupPolicy = NAME_LOOKUP_POLICY_ONCE;

			agent = LocalDevice.getLocalDevice().getDiscoveryAgent();

			zone = properties.getProperty("zone", "unknown zone");
			zone = zone.trim();
			String value = properties.getProperty("update-interval-time");
			if(value != null) scanInterval = Integer.parseInt(value);
			else properties.setProperty("update-interval-time", scanInterval + "");

			value = properties.getProperty("device-clear-time");
			if(value != null) clearTime = Integer.parseInt(value);
			else properties.setProperty("device-clear-time", clearTime + "");

			haltOnNErrors = Integer.parseInt(properties.getProperty("halt-on-nerrors", "-1"));

			value = properties.getProperty("name-lookup-policy");
			if(value != null) {
				if(value.equals("never")) {
					nameLookupPolicy = NAME_LOOKUP_POLICY_NEVER;
				}
				else if(value.equals("once")) {
					nameLookupPolicy = NAME_LOOKUP_POLICY_ONCE;
				}
				else if(value.equals("always")) {
					nameLookupPolicy = NAME_LOOKUP_POLICY_ALWAYS;
				}
				else {
					logger.severe("name-lookup-policy should be 'never', 'once' or 'always'!");
					throw new RoomWareException("Wrong always-ask-name value!");
				}	
			}
			else properties.setProperty("name-lookup-policy", "once");
		}
		catch (NumberFormatException cause) {
			logger.severe(cause.getMessage());
			throw new RoomWareException("wrong update-interval-time or device-clear-time!");
		}
		catch (BluetoothStateException cause) {
			logger.severe(cause.getMessage());
			throw new RoomWareException("Could not init bluetooth!");
		}

		logger.info("update-interval-time = " + scanInterval);
		logger.info("device-clear-time = " + clearTime);
		String policyString = "once";
		if(nameLookupPolicy == NAME_LOOKUP_POLICY_NEVER) {
			policyString = "never";
		}
		else if(nameLookupPolicy == NAME_LOOKUP_POLICY_ALWAYS) {
			policyString = "always";
		}
		logger.info("name-lookup-policy = " + policyString);

   		presenceCacheList = new PresenceCacheList (this, clearTime);
		doLoop = true;
		isScanning = false;
	}


	public synchronized void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass clazz) {
		BluetoothDevice foundDevice = new BluetoothDevice (remoteDevice);
		Presence p = new Presence (this, zone, foundDevice, new Date ());
		presenceCacheList.addPresence (p);

		if(!knownDevices.contains(foundDevice)) {
			if(unknownDevices.contains(foundDevice)) {
				unknownDevices.remove(foundDevice);
			}
			unknownDevices.addFirst(foundDevice);
		}
		if(friendlyNameCache.containsKey(foundDevice)) {
			foundDevice.setFriendlyName(friendlyNameCache.get(foundDevice));
		}
		logger.info("Device found: " + foundDevice);
	}


	protected List<BluetoothDevice> orderDevicesForNameLookup() {
		List<BluetoothDevice> order = new LinkedList<BluetoothDevice> ();
		order.addAll(unknownDevices);
		order.addAll(knownDevices);

		Set<Presence> ps = presenceCacheList.getPresences ();
		Set<Device> ds = new HashSet<Device> ();
		for (Presence p: ps) {
			ds.add (p.getDevice ());
		}

		Iterator <BluetoothDevice> it = order.iterator ();
		while (it.hasNext ()) {
			Device d = it.next ();
			if(!ds.contains(d)) {
				it.remove ();
			}
		}
		return order;
	}


	protected void handleNameLookupPolicy() {
		Date timeOutTime = new Date();
		timeOutTime.setTime(inquiryStartTime.getTime() + scanInterval * 1000);

		if(nameLookupPolicy == NAME_LOOKUP_POLICY_NEVER) return;

		List<BluetoothDevice> queryOrder = orderDevicesForNameLookup();

		for(BluetoothDevice device: queryOrder) {
			if(device == null) {
				logger.warning("we got null device!");
				continue;
			}

			if(new Date().after(timeOutTime)) {
				logger.info("name lookup sequence stopped by time out!");
				return;
			}

			String previousName = device.getFriendlyName();
			if(previousName == null || nameLookupPolicy == NAME_LOOKUP_POLICY_ALWAYS) {
				logger.info("name lookup started for device: " + device);
				try {
					if(unknownDevices.contains(device)) {
						unknownDevices.remove(device);
					}
					if(knownDevices.contains(device)) {
						knownDevices.remove(device);
					}
					knownDevices.addLast(device);

					String currentName = device.getRemoteDevice().getFriendlyName(true);

					if(currentName == null) {
						continue;
					}
					device.setFriendlyName(currentName);
					friendlyNameCache.put(device, currentName);
					logger.info("lookup answer: " + device.toString());

					if(!currentName.equals(previousName)) {
						device.setFriendlyName(currentName);
						friendlyNameCache.put(device, currentName);
						propertyChange(new PropertyChangeEvent(device, "name", previousName, currentName));
					}
				} catch(IOException cause) {
					logger.info("no lookup answer: " + cause.getMessage());
					continue;
				}
			}
			else {
				logger.info("got name from cache");
			}
		}
		logger.info("name lookup sequence finished");
	}


	public synchronized void inquiryCompleted(int discType) {
		isScanning = false;

		switch(discType) {
		case (DiscoveryListener.INQUIRY_ERROR):
			logger.warning("inquiry has been stopped due to error!");
			errorCount++;
			logger.info("errorCount is now: " + errorCount);
			if(haltOnNErrors > 0 && errorCount >= haltOnNErrors) {
				logger.severe("got more then n errors, exiting real hard!");
				System.exit(1);
			}
			break;

		case (DiscoveryListener.INQUIRY_TERMINATED):
			logger.warning("inquiry has been stopped by time out!");
			errorCount++;
			logger.info("errorCount is now: " + errorCount);
			if(haltOnNErrors > 0 && errorCount >= haltOnNErrors) {
				logger.severe("got more then n errors, exiting real hard!");
				System.exit(1);
			}
			break;

		case (DiscoveryListener.INQUIRY_COMPLETED):
			logger.info("inquiry completed succesfully");
			if(errorCount != 0) {
				errorCount = 0;
				logger.info("errorCount set to 0");
			}
			break;
		}

		this.notifyAll();
	}


	protected void startInquiry() {
		while(!isScanning && doLoop) {
			try {
				agent.startInquiry(DiscoveryAgent.GIAC, this);
				isScanning = true;
				inquiryStartTime = new Date();
				logger.info("inquiry started");
			}
			catch(BluetoothStateException cause) {
				logger.warning("Could not start bluetooth inquiry!");
				try {
					synchronized(this) {
						wait(1000);
					}
				}
				catch(InterruptedException ie) {
				}
			}                  
		}
	}


	protected void waitForInquiryCompletion() {
		while (isScanning && doLoop) {
			try {
				synchronized(this) {
					wait(1000);
				}
			}
			catch(InterruptedException cause) {
			}
		}
	}


	public void run() {
		while(doLoop) {
			startInquiry();
			waitForInquiryCompletion();
			presenceCacheList.updatePresences();
			handleNameLookupPolicy();
			waitForIntervalTime();
		}
	}


	protected void waitForIntervalTime() {
		Date inquiryEndTime = new Date();
		inquiryEndTime.setTime(inquiryStartTime.getTime() + scanInterval * 1000);
		Date now = new Date();
		long difference = inquiryEndTime.getTime() - now.getTime();
		if(difference < 0) return;

		try {
			logger.info("waiting for " + (difference / 1000) + " seconds to inquiry...");
			synchronized(this) {
				wait(difference);
			}
		}
		catch(InterruptedException cause) {
			if(doLoop) logger.warning("We are waked for no reason!");
		}
	}


	public synchronized void stop() {
		doLoop = false;
		notifyAll();
	}


	public synchronized Set<Presence> getPresences() {
		return presenceCacheList.getPresences ();
	}


	public void sendMessage(Device device, Message message)
		throws RoomWareException {
			throw new RoomWareException("Operation not supported!");
		}
}
