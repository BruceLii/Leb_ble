package com.ledble.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.common.BaseActivity;
import com.ledble.R;
import com.ledble.adapter.BleSelectDeviceAdapter;
import com.ledble.base.LedBleApplication;
import com.ledble.db.GroupDevice;

public class DeviceListActivity extends BaseActivity {

	private ListView listViewDevices;
	private BleSelectDeviceAdapter bleDeviceAdapter;
	private String groupName = "";
	private ArrayList<GroupDevice> groupDevices;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		initView();
	}

	@Override
	public void initView() {
		setContentView(R.layout.activity_device_list);
		groupName = getIntent().getStringExtra("group");
		groupDevices = (ArrayList<GroupDevice>) getIntent().getSerializableExtra("devices");

		listViewDevices = findListViewById(R.id.listViewDevices);
		bleDeviceAdapter = new BleSelectDeviceAdapter(this);
		bleDeviceAdapter.setSelected(groupDevices);
		listViewDevices.setAdapter(bleDeviceAdapter);
		
		listViewDevices.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				BluetoothDevice ble = bleDeviceAdapter.getDevice(position);
				bleDeviceAdapter.select(ble);
				bleDeviceAdapter.notifyDataSetChanged();
			}
		});

		findViewById(R.id.textViewBack).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		findButtonById(R.id.buttonFireBind).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LedBleApplication.getInstance().setTempDevices(bleDeviceAdapter.getSelectSet());
				Intent data = new Intent();
				data.putExtra("group", groupName);
				setResult(Activity.RESULT_OK, data);
				finish();
			}
		});

	}
}
