package org.roomwareproject.module.bluetooth;

import org.roomwareproject.server.*;
import javax.bluetooth.*;

public class BluetoothDevice extends Device {

    transient protected RemoteDevice remoteDevice;
    
    public BluetoothDevice(RemoteDevice remoteDevice) {
        super(new BluetoothAddress(remoteDevice.getBluetoothAddress()));
        this.remoteDevice = remoteDevice;
    }
    
    
    public RemoteDevice getRemoteDevice() {
        return remoteDevice;
    }
    
}
