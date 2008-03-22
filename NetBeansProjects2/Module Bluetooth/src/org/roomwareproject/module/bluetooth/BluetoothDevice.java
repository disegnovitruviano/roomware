/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.roomwareproject.module.bluetooth;

import org.roomwareproject.server.*;
import javax.bluetooth.*;
/**
 *
 * @author tvburger
 */
public class BluetoothDevice extends Device {

    protected RemoteDevice remoteDevice;
    
    public BluetoothDevice(RemoteDevice remoteDevice) {
        super(new BluetoothAddress(remoteDevice.getBluetoothAddress()));
        this.remoteDevice = remoteDevice;
    }
    
    
    public RemoteDevice getRemoteDevice() {
        return remoteDevice;
    }
    
}
