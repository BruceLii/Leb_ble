package com.ledble.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;


import com.ledble.bean.Mp3;

public class LedBleApplication extends Application {
	
	private ArrayList<BluetoothDevice> bleDevices;
	private Set<BluetoothDevice> tempDevices;
	private HashMap<String, BluetoothGatt> hashMapGatt;
	public static LedBleApplication app;
	public static final String tag = "ble";
	private ArrayList<Mp3> mp3s;
	
	@Override
	public void onCreate() {
		super.onCreate();
		//CrashHandler.getInstance().init(this);
		bleDevices = new ArrayList<BluetoothDevice>();
		hashMapGatt = new HashMap<String, BluetoothGatt>();
		mp3s = new ArrayList<Mp3>();
		app = this;
		
	}
	
	public static LedBleApplication getApp() {
		return app;
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

	public ArrayList<BluetoothDevice> getBleDevices() {
		return bleDevices;
	}

	/**
	 * 删除断开的Device
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
