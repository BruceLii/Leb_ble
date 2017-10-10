package com.ledble.service;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.common.uitl.LogUtil;
import com.ledble.base.LedBleApplication;

@SuppressLint("NewApi")
public class BluetoothLeServiceAll extends Service {

	private final static String TAG = LedBleApplication.tag;
	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;

	private static final int STATE_DISCONNECTED = 0;
	private static final int STATE_CONNECTING = 1;
	private static final int STATE_CONNECTED = 2;

	public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
	public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";
	public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);

	private static HashMap<String, BluetoothGatt> connectionMap = new HashMap<String, BluetoothGatt>();

	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			String intentAction;
			if (newState == BluetoothProfile.STATE_CONNECTED) {// 连接
				intentAction = ACTION_GATT_CONNECTED;
				// broadcastUpdate(intentAction);
				Log.i(LedBleApplication.tag, "Connected to GATT server.");
				broadcastUpdateWithAddress(intentAction, gatt.getDevice().getAddress());
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {// 断开
				intentAction = ACTION_GATT_DISCONNECTED;
				Log.i(LedBleApplication.tag, "Disconnected from GATT server.");
				// broadcastUpdate(intentAction);
				broadcastUpdateWithAddress(intentAction, gatt.getDevice().getAddress());
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			Log.i(LedBleApplication.tag, "onServicesDiscovered=====");
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Log.i(LedBleApplication.tag, "onServicesDiscovered");
				broadcastUpdateWithAddress(ACTION_GATT_SERVICES_DISCOVERED, gatt.getDevice().getAddress());// 发现service
			} else {
				Log.w(TAG, "onServicesDiscovered received: " + status);
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
		}
	};

	private void broadcastUpdateWithAddress(final String action, final String address) {
		final Intent intent = new Intent(action);
		intent.putExtra("address", address);
		sendBroadcast(intent);
	}

	private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		sendBroadcast(intent);
	}

	private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
		final Intent intent = new Intent(action);
		if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
			int flag = characteristic.getProperties();
			int format = -1;
			if ((flag & 0x01) != 0) {
				format = BluetoothGattCharacteristic.FORMAT_UINT16;
				Log.d(TAG, "Heart rate format UINT16.");
			} else {
				format = BluetoothGattCharacteristic.FORMAT_UINT8;
				Log.d(TAG, "Heart rate format UINT8.");
			}
			final int heartRate = characteristic.getIntValue(format, 1);
			Log.d(TAG, String.format("Received heart rate: %d", heartRate));
			intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
		} else {
			// For all other profiles, writes the data formatted in HEX.
			final byte[] data = characteristic.getValue();
			if (data != null && data.length > 0) {
				final StringBuilder stringBuilder = new StringBuilder(data.length);
				for (byte byteChar : data)
					stringBuilder.append(String.format("%02X ", byteChar));
				intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
			}
		}
		sendBroadcast(intent);
	}

	public class LocalBinder extends Binder {

		public BluetoothLeServiceAll getService() {
			return BluetoothLeServiceAll.this;
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		closeAll();
		return super.onUnbind(intent);
	}

	private final IBinder mBinder = new LocalBinder();

	public boolean initialize() {
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}
		return true;
	}

	public boolean connect(final String address) {
		if (connectionMap.containsKey(address)) {
			connectionMap.get(address).connect();
			return true;
		}
		final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		if (device == null) {
			Log.w(LedBleApplication.tag, "Device not found.  Unable to connect.");
			return false;
		}
		
		BluetoothGatt mBluetoothGatt = device.connectGatt(this, false, new BluetoothGattCallback() {
			@Override
			public void onServicesDiscovered(BluetoothGatt gatt, int status) {
				LogUtil.i(LedBleApplication.tag, "service discovered");
			}
		});
		
		
		LogUtil.i(LedBleApplication.tag, "have service:" + mBluetoothGatt.getServices().size());
		connectionMap.put(mBluetoothGatt.getDevice().getAddress(), mBluetoothGatt);
		return true;
	}

	public HashMap<String, BluetoothGatt> getConnectMap() {
		return connectionMap;
	}

	public void disconnect(String address) {
		BluetoothGatt bgatt = connectionMap.get(address);
		if (null != bgatt) {
			bgatt.disconnect();
		}
	}

	public void close(String address) {
		BluetoothGatt bgatt = connectionMap.get(address);
		if (null != bgatt) {
			bgatt.close();
		}
	}

	public void closeAll() {
		for (Entry<String, BluetoothGatt> bgat : connectionMap.entrySet()) {
			try {
				bgat.getValue().close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public BluetoothGatt getBluetoothGattByAddress(String address) {
		return connectionMap.get(address);
	}

	// public void readCharacteristic(BluetoothGattCharacteristic
	// characteristic) {
	// if (mBluetoothAdapter == null || mBluetoothGatt == null) {
	// Log.w(TAG, "BluetoothAdapter not initialized");
	// return;
	// }
	// mBluetoothGatt.readCharacteristic(characteristic);
	// }

	// public void setCharacteristicNotification(BluetoothGattCharacteristic
	// characteristic, boolean enabled) {
	// if (mBluetoothAdapter == null || mBluetoothGatt == null) {
	// Log.w(TAG, "BluetoothAdapter not initialized");
	// return;
	// }
	// mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
	//
	// // This is specific to Heart Rate Measurement.
	// if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
	// BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID
	// .fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
	// descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
	// mBluetoothGatt.writeDescriptor(descriptor);
	// }
	// }

}
