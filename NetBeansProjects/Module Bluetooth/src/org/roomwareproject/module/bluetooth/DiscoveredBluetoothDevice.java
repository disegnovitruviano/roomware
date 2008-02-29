/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.roomwareproject.module.bluetooth;


import org.roomwareproject.server.*;
import java.util.*;
import javax.bluetooth.*;
/**
 *
 * @author tvburger
 */
public class DiscoveredBluetoothDevice extends BluetoothDevice implements Comparable<DiscoveredBluetoothDevice> {
    
    protected Date timeOfDiscovery;
    
    public DiscoveredBluetoothDevice(Date timeOfDiscovery, RemoteDevice remoteDevice) {
        super(remoteDevice);
        this.timeOfDiscovery = timeOfDiscovery;
    }
    
    public Date getTimeOfDiscovery() {
        return timeOfDiscovery;
    }

    public int compareTo(DiscoveredBluetoothDevice other) {
        if(this.timeOfDiscovery.before(other.timeOfDiscovery)) return -1;
        if(this.timeOfDiscovery.after(other.timeOfDiscovery)) return 1;
        else return 0;
    }

}
