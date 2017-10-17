package com.ledble.base;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

import com.ledble.bean.Mp3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class LedBleApplication extends Application {

    public static final String tag = "ble";
    public static LedBleApplication app;
    private List<BluetoothDevice> bleDevices = Collections.synchronizedList(new ArrayList<BluetoothDevice>());
    private Set<BluetoothDevice> tempDevices;
    private HashMap<String, BluetoothGatt> hashMapGatt;
    private ArrayList<Mp3> mp3s;

    public static LedBleApplication getApp() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        hashMapGatt = new HashMap<String, BluetoothGatt>();
        mp3s = new ArrayList<Mp3>();
        app = this;

    }

    public ArrayList<Mp3> getMp3s() {
        return mp3s;
    }

    public void setMp3s(ArrayList<Mp3> mp3s) {
        this.mp3s = mp3s;
    }

    public HashMap<String, BluetoothGatt> getBleGattMap() {
        return hashMapGatt;
    }

    public void clearBleGatMap() {
        hashMapGatt.clear();
    }

    public List<BluetoothDevice> getBleDevices() {
        return bleDevices;
    }

    /**
     * 删除断开的Device
     *
     * @param address
     */
    public void removeDisconnectDevice(String address) {

        for (int i = 0, isize = bleDevices.size(); i < isize; i++) {
            BluetoothDevice tempDev = bleDevices.get(i);
            if (address.equalsIgnoreCase(tempDev.getAddress())) {
                bleDevices.remove(i);
                break;
            }
        }

    }

    public Set<BluetoothDevice> getTempDevices() {
        return tempDevices;
    }

    public void setTempDevices(Set<BluetoothDevice> tempDevices) {
        this.tempDevices = tempDevices;
    }

    public void exit() {

    }

}
