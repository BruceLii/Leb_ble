package com.ledble.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ledble.R;
import com.ledble.base.LedBleApplication;

import java.util.ArrayList;
import java.util.List;

public class BleDeviceAdapter extends BaseAdapter {

	private Context context;

	public BleDeviceAdapter(Context context) {
		this.context = context;
	}

	public void addDevice(BluetoothDevice device) {
		if (!LedBleApplication.getInstance().getBleDevices().contains(device)) {
			LedBleApplication.getInstance().getBleDevices().add(device);
		}
		notifyDataSetChanged();
	}

	public void removeDevice(String address) {
		List<BluetoothDevice> devices = LedBleApplication.getInstance().getBleDevices();
		ArrayList<BluetoothDevice> deletes=new ArrayList<BluetoothDevice>();
		for (BluetoothDevice dev : devices) {
			if (dev.getAddress().equalsIgnoreCase(address)) {
				deletes.add(dev);
			}
		}
		LedBleApplication.getInstance().getBleDevices().removeAll(deletes);
		notifyDataSetChanged();
	}

	private List<BluetoothDevice> getAllDevice() {
		return LedBleApplication.getInstance().getBleDevices();
	}

	public BluetoothDevice getDevice(int position) {
		return LedBleApplication.getInstance().getBleDevices().get(position);
	}

	public void clear() {
		LedBleApplication.getInstance().getBleDevices().clear();
	}

	@Override
	public int getCount() {
		return LedBleApplication.getInstance().getBleDevices().size();
	}

	@Override
	public Object getItem(int i) {
		return LedBleApplication.getInstance().getBleDevices().get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		ViewHolder viewHolder;
		// General ListView optimization code.
		if (view == null) {
			view = View.inflate(context, R.layout.layout_listitem_device, null);
			viewHolder = new ViewHolder();
			viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
			viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		BluetoothDevice device = LedBleApplication.getInstance().getBleDevices().get(i);
		final String deviceName = device.getName();
		if (deviceName != null && deviceName.length() > 0) {
			viewHolder.deviceName.setText(deviceName);
		} else {
			viewHolder.deviceName.setText("unknown device");
		}
		viewHolder.deviceAddress.setText(device.getAddress());
		return view;
	}

	class ViewHolder {
		TextView deviceName;
		TextView deviceAddress;
	}

}
