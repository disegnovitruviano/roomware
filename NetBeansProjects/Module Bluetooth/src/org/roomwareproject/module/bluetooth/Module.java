package org.roomwareproject.module.bluetooth;

import org.roomwareproject.server.*;
import java.util.*;
import java.util.logging.*;
import java.io.*;
import java.beans.*;
import javax.bluetooth.*;


public class Module extends AbstractModule implements DiscoveryListener {

        protected DiscoveryAgent agent; 
	protected Set<DiscoveredBluetoothDevice> discoveredDevices = new HashSet<DiscoveredBluetoothDevice>();
        protected Map<BluetoothDevice, String> friendlyNameCache = new HashMap<BluetoothDevice, String> ();
        
        public final static int
            NAME_LOOKUP_POLICY_NEVER = 0,
            NAME_LOOKUP_POLICY_ONCE = 1,
            NAME_LOOKUP_POLICY_ALWAYS = 2;
        
        protected Date inquiryStartTime;
	protected int scanInterval;
	protected int clearTime;
	protected boolean doLoop;
	protected boolean isScanning;
	protected int nameLookupPolicy;

        
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

			String value = properties.getProperty("update-interval-time");
			if(value != null) scanInterval = Integer.parseInt(value);
			else properties.setProperty("update-interval-time", scanInterval + "");

			value = properties.getProperty("device-clear-time");
			if(value != null) clearTime = Integer.parseInt(value);
			else properties.setProperty("device-clear-time", clearTime + "");

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

		doLoop = true;
		isScanning = false;
	}


    public synchronized void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass clazz) {
        DiscoveredBluetoothDevice foundDevice = new DiscoveredBluetoothDevice(new Date(), remoteDevice);
        if(!discoveredDevices.contains(foundDevice)) {
            discoveredDevices.add(foundDevice);
            propertyChange(new PropertyChangeEvent(foundDevice, "in range", false, true));
        }
        if(friendlyNameCache.containsKey(foundDevice)) {
            foundDevice.setFriendlyName(friendlyNameCache.get(foundDevice));
        }
    }
        
    
    protected BluetoothDevice[] orderDevicesForNameLookup() {
        BluetoothDevice[] queryOrder = new BluetoothDevice[discoveredDevices.size()];
        int slotNumber = 0;
        
        // get all unknown devices
        for(BluetoothDevice device: discoveredDevices) {
            if(device.getFriendlyName() == null) {
                queryOrder[slotNumber] = device;
                slotNumber++;
            }
        }
        
        // enter the rest
        BluetoothDevice[] sortedDevices = discoveredDevices.toArray(new BluetoothDevice[0]);
        Arrays.sort(sortedDevices);
        for(BluetoothDevice device: sortedDevices) {
            if(device.getFriendlyName() != null) {
                queryOrder[slotNumber] = device;
                slotNumber++;
            }
        }
        
        return queryOrder;
    }
    
    
    protected void handleNameLookupPolicy() {
        Date timeOutTime = new Date();
        timeOutTime.setTime(inquiryStartTime.getTime() + scanInterval * 1000);
        
        if(nameLookupPolicy == NAME_LOOKUP_POLICY_NEVER) return;
        
        BluetoothDevice[] queryOrder = orderDevicesForNameLookup();
        
        for(BluetoothDevice device: queryOrder) {
            if(device == null) {
                logger.warning("we got null device!");
                continue;
            }
            
            if(new Date().after(timeOutTime)) {
                logger.info("name lookup sequence stopped by time out!");
                return;
            }
            
            logger.info("name lookup started for device: " + device);
            
            String previousName = device.getFriendlyName();
            if(previousName == null || nameLookupPolicy == NAME_LOOKUP_POLICY_ALWAYS) {
                try {
	   		String currentName = device.getRemoteDevice().getFriendlyName(true);
                        if(currentName == null) {
                            logger.warning("getFriendlyName returned null!");
                            continue;
                        }
                        device.setFriendlyName(currentName);
                        friendlyNameCache.put(device, currentName);
                        logger.info("got name for: " + device.toString());
                        
                        if(!currentName.equals(previousName)) {
                            device.setFriendlyName(currentName);
                            friendlyNameCache.put(device, currentName);
                            propertyChange(new PropertyChangeEvent(device, "name", previousName, currentName));
                        }
		} catch(IOException cause) {
			logger.warning("Could not obtain friendly name of device!");
                        continue;
		}
            }
            logger.info("name lookup finished");
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
                            logger.info("inquiry completed succesfully");
                            break;
		}
                
                this.notifyAll();
	}


    protected synchronized void cleanDeviceList() {
        Date clearDate = new Date(new Date().getTime() - (clearTime * 1000));

	Iterator<DiscoveredBluetoothDevice> iterator = discoveredDevices.iterator();

	while(iterator.hasNext()) {
            DiscoveredBluetoothDevice discoveredDevice = iterator.next();
            Date date = discoveredDevice.getTimeOfDiscovery();

            if(date.before(clearDate)) {
                logger.info("we are cleaning device: " + discoveredDevice.toString());
                logger.info("it existed for " + ((new Date().getTime() - date.getTime()) / 1000) + " seconds"); 
		iterator.remove();
                propertyChange(new PropertyChangeEvent(discoveredDevice, "in range", true, false));
            }
	}
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
        try {
            logger.info("waiting for inquiry to complete...");
            synchronized(this) {
                wait(scanInterval * 1000);
            }
	}
        catch(InterruptedException cause) {
            if(doLoop && isScanning) logger.warning("we are waked for unknown reason!");
	}
	if(isScanning) agent.cancelInquiry(this);
    }
    
    
    public void run() {
        while(doLoop) {
            startInquiry();
            waitForInquiryCompletion();
            handleNameLookupPolicy();
            cleanDeviceList();
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


	public synchronized Set<Device> getDevices() {
		return new HashSet<Device> (discoveredDevices);
	}


	public void sendMessage(Device device, Message message)
		throws RoomWareException {
			throw new RoomWareException("Operation not supported!");
		}
}
