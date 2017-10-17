package com.ledble.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Note: 服务连接监听
 * Created by liyonglin  at 2017/10/11 11:33
 */
public class MyServiceConenction implements ServiceConnection {

    private BluetoothLeServiceSingle bluetoothLeService;
    private ServiceConnectListener serviceConnectListener;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        bluetoothLeService = ((BluetoothLeServiceSingle.LocalBinder) service).getService();

        if (null != serviceConnectListener) {
            serviceConnectListener.onConnect(name, service, bluetoothLeService);
        }
        // mBluetoothLeService.connect(mDeviceAddress);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        bluetoothLeService = null;
        if (null != serviceConnectListener) {
            serviceConnectListener.onDisConnect(name);
        }
    }

    public BluetoothLeServiceSingle getBluetoothLeService() {
        return bluetoothLeService;
    }

    public void setBluetoothLeService(BluetoothLeServiceSingle bluetoothLeService) {
        this.bluetoothLeService = bluetoothLeService;
    }


    public ServiceConnectListener getServiceConnectListener() {
        return serviceConnectListener;
    }

    public void setServiceConnectListener(ServiceConnectListener serviceConnectListener) {
        this.serviceConnectListener = serviceConnectListener;
    }

    public static interface ServiceConnectListener {
        public void onConnect(ComponentName name, IBinder service, BluetoothLeServiceSingle bLeService);

        public void onDisConnect(ComponentName name);
    }

}
