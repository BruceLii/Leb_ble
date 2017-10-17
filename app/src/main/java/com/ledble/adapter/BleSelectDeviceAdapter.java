package com.ledble.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.uitl.ListUtiles;
import com.ledble.R;
import com.ledble.base.LedBleApplication;
import com.ledble.db.GroupDevice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BleSelectDeviceAdapter extends BaseAdapter {

	private Context context;
	private Set<BluetoothDevice> hashSet;
	private ArrayList<GroupDevice> groupDevices;

	public BleSelectDeviceAdapter(Context context) {
		this.context = context;
		this.hashSet = new HashSet<BluetoothDevice>();
	}

	public Set<BluetoothDevice> getSelectSet() {
		return hashSet;
	}

	public void select(BluetoothDevice bledevice) {
		if (hashSet.contains(bledevice)) {
			hashSet.remove(bledevice);
		} else {
			hashSet.add(bledevice);
		}
		notifyDataSetChanged();
	}

	public void setSelected(ArrayList<GroupDevice> groupDevices) {
		this.groupDevices = groupDevices;
		if (!ListUtiles.isEmpty(groupDevices)) {
			List<BluetoothDevice> blelist = LedBleApplication.getApp().getBleDevices();
			for (GroupDevice groupDevice : groupDevices) {
				for (BluetoothDevice bDevice : blelist) {
					if (groupDevice.getAddress().equals(bDevice.getAddress())) {
						hashSet.add(bDevice);
					}
				}
			}

		}
		notifyDataSetChanged();
	}

	public void addDevice(BluetoothDevice device) {
		if (!LedBleApplication.getApp().getBleDevices().contains(device)) {
			LedBleApplication.getApp().getBleDevices().add(device);
		}
		notifyDataSetChanged();
	}

	public ArrayList<GroupDevice> getGroupDevices() {
		return groupDevices;
	}

	public void setGroupDevices(ArrayList<GroupDevice> groupDevices) {
		this.groupDevices = groupDevices;
	}

	private List<BluetoothDevice> getAllDevice() {
		return LedBleApplication.getApp().getBleDevices();
	}

	public BluetoothDevice getDevice(int position) {
		return LedBleApplication.getApp().getBleDevices().get(position);
	}

	public void clear() {
		LedBleApplication.getApp().getBleDevices().clear();
	}

	@Override
	public int getCount() {
		return LedBleApplication.getApp().getBleDevices().size();
	}

	@Override
	public Object getItem(int i) {
		return LedBleApplication.getApp().getBleDevices().get(i);
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
			view = View.inflate(context, R.layout.layout_listitem_device_select, null);
			viewHolder = new ViewHolder();
			viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
			viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
			viewHolder.imageViewHook = (ImageView) view.findViewById(R.id.imageViewHook);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		BluetoothDevice device = LedBleApplication.getApp().getBleDevices().get(i);
		final String deviceName = device.getName();
		if (deviceName != null && deviceName.length() > 0) {
			viewHolder.deviceName.setText(deviceName);
		} else {
			viewHolder.deviceName.setText("unknown device");
		}
		viewHolder.deviceAddress.setText(device.getAddress());

		if (hashSet.contains(device)) {
			viewHolder.imageViewHook.setVisibility(View.VISIBLE);
		} else {
			viewHolder.imageViewHook.setVisibility(View.INVISIBLE);
		}
		return view;
	}

	class ViewHolder {
		TextView deviceName;
		TextView deviceAddress;
		ImageView imageViewHook;
	}

}
