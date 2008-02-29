/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.roomwareproject.module.fake;


import org.roomwareproject.server.*;
import java.util.*;
import java.util.logging.*;
import java.io.*;
import java.beans.*;

/**
 *
 * @author tvburger
 */
public class Module extends AbstractModule {



	protected boolean doLoop = true;
        protected Set<Device>devices = new HashSet<Device>();



	public Module(Properties properties) throws RoomWareException {
		super(properties);
		init();
	}


	protected void init() throws RoomWareException {
            String deviceNameList = properties.getProperty("devices");
            String[] deviceNames = deviceNameList.split(",");
            for(String deviceName: deviceNames) {
                Device d = new Device(new FooDeviceAddress(deviceName.trim()));
                d.setFriendlyName(deviceName.trim());
                devices.add(d);
            }
        }


	public void run() {
		while(doLoop) {
			try {
				synchronized(this) {
					wait();
				}
			}

			catch(InterruptedException cause) {
				if(doLoop) {
					logger.warning("we are waked for unknown reason!");
				}
			}
		}
	}


	public synchronized void stop() {
            doLoop = false;
		notifyAll();
	}


	public synchronized Set<Device> getDevices() {
		return new HashSet<Device>(devices);
	}


	public void sendMessage(Device device, Message message)
		throws RoomWareException {
			throw new RoomWareException("Operation not supported!");
		}

}
