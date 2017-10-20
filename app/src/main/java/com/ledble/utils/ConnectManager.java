package com.ledble.utils;

import android.bluetooth.BluetoothDevice;

import com.ledble.bean.BluetoothDeviceModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by liyon on 2017/10/18.
 */

public class ConnectManager {
    public static ConnectManager connectManager = new ConnectManager();

    /**
     * 扫描到的指定蓝牙设备
     */
    private static List<BluetoothDeviceModel> scanedDevice = Collections.synchronizedList(new ArrayList<BluetoothDeviceModel>());

    private ConnectManager() {
    }

    public static ConnectManager getInstance() {
        if (connectManager == null) {
            connectManager = new ConnectManager();
        }
        return connectManager;
    }

    public static List<BluetoothDeviceModel> getScanedDevice() {
        return scanedDevice;
    }

    public static void addDevice(BluetoothDeviceModel newFoundDevice) {
        boolean isContain = false;
        if (scanedDevice.size() == 0) {
            isContain = false;
        }

        for (BluetoothDeviceModel m :
                scanedDevice) {
            if (m.device.getAddress().equals(newFoundDevice.device.getAddress())) {
                isContain = true;
                break;
            }
        }
        if (!isContain) {
            scanedDevice.add(newFoundDevice);
        }

    }

    /**
     * 获取已连的蓝牙设备数量
     *
     * @return
     */
    public static int getConnectedCount() {
        int count = 0;
        for (BluetoothDeviceModel m :
                scanedDevice) {
            if (m.isConnected) {
                count++;
            }
        }

        return count;
    }

    /**
     * 数据中心是否已经扫描到改蓝牙设备
     *
     * @param device
     * @return
     */
    public boolean isContainTheDevice(BluetoothDevice device) {
        boolean flag = false;

        for (BluetoothDeviceModel m :
                scanedDevice) {
            if (m.device.getAddress().equals(device.getAddress())) {
                flag = true;
                break;
            }
        }

        return flag;
    }

    /**
     * 指定设备是否当前已连接，
     *
     * @param device
     * @return
     */
    public boolean isDeviceConnected(BluetoothDevice device) {
        boolean flag = false;

        for (BluetoothDeviceModel m :
                scanedDevice) {
            if (m.device.getAddress().equals(device.getAddress())) {
                flag = m.isConnected;
                break;
            }
        }
        return flag;
    }

    public void clear() {
        scanedDevice.clear();
    }

    public void updateConnectState(String address, boolean isConnected) {
        for (BluetoothDeviceModel m :
                scanedDevice) {
            if (m.device.getAddress().equals(address)) {
                m.isConnected = isConnected;
            }
        }

    }

    /**
     * 未连接的设备
     *
     * @return
     */
    public synchronized List<BluetoothDevice> getUnconnectedDevice() {
        List<BluetoothDevice> list = new ArrayList<>();
        for (BluetoothDeviceModel m :
                scanedDevice) {
            if (!m.isConnected) {
                list.add(m.device);
            }
        }

        return list;
    }
}
