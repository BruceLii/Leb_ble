package com.ledble.bean;

import android.bluetooth.BluetoothDevice;

/**
 * Created by liyon on 2017/10/18.
 */

public class BluetoothDeviceModel {
    public BluetoothDevice device;
    public boolean isConnected = false;//default value is false .

    @Override
    public String toString() {
        return "BluetoothDeviceModel{" +
                "device=" + device.getAddress() +
                ", isConnected=" + isConnected +
                '}';
    }
}
