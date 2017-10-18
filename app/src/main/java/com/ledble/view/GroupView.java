package com.ledble.view;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ledble.R;
import com.ledble.base.LedBleApplication;
import com.ledble.db.GroupDevice;
import com.ledble.net.NetConnectBle;

public class GroupView {

	private String groupName;
	private View groupView;
	private Context context;
	private ToggleButton toggleButton;
	private ArrayList<GroupDevice> groupDevices;
	private TextView textViewTotal;

	private ImageView imageViewControll;
	private View viewTopLine;
	private View viewBottomLine;
	private SlideSwitch slideSwitch;
	private int connect;
	private boolean isAllon;
	
	public GroupView(Context context, String groupName,boolean isAllon) {
		this.context = context;
		this.isAllon=isAllon;
		this.groupName = groupName;
		this.groupView = View.inflate(context, R.layout.item_my_group, null);
		this.groupView.setTag(groupName);

		this.imageViewControll = (ImageView) groupView.findViewById(R.id.imageViewControll);
		this.viewTopLine = groupView.findViewById(R.id.viewTopLine);
		this.viewBottomLine = groupView.findViewById(R.id.viewBottomLine);

		TextView gnameTextView = (TextView) groupView.findViewById(R.id.textViewGroupName);
		textViewTotal = (TextView) groupView.findViewById(R.id.textViewTotal);
		toggleButton = (ToggleButton) groupView.findViewById(R.id.toggleButton);
		
		if(isAllon==false){
			setCloseDisable();
		}
		gnameTextView.setText(groupName);

		toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					NetConnectBle.getInstanceByGroup(GroupView.this.groupName).turnOn();
				} else {
					NetConnectBle.getInstanceByGroup(GroupView.this.groupName).turnOff();
				}
				juedg();
			}
		});
		
		slideSwitch = (SlideSwitch) groupView.findViewById(R.id.slideSwitch);
		slideSwitch.setState(false);
	}

	
	public ToggleButton getToggleButton() {
		return toggleButton;
	}


	public void hideTopLine() {
		viewTopLine.setVisibility(View.VISIBLE);
	}

	public void hideBottomLine() {
		viewBottomLine.setVisibility(View.VISIBLE);
	}

	public boolean isTurnOn() {
		return toggleButton.isChecked();
	}

	public int getConnect() {
		return connect;
	}

	public ImageView getImageViewControll() {
		return imageViewControll;
	}

	public void setImageViewControll(ImageView imageViewControll) {
		this.imageViewControll = imageViewControll;
	}

	public ArrayList<GroupDevice> getGroupDevices() {
		return groupDevices;
	}

	public void setGroupDevices(ArrayList<GroupDevice> groupDevices) {
		this.groupDevices = groupDevices;
		// String
		// tot=App.getInstance().getResources().getString(R.string.conenct_device,(ListUtiles.isEmpty(groupDevices)?0:this.groupDevices.size()),0);
		// setConnect(tot);
		setConnectCount(0);
	}

	public void setConnect(String connected) {
		String connectdDevice = LedBleApplication.getInstance().getResources().getString(R.string.connect_count, connected);
		this.textViewTotal.setText(connectdDevice);
	}

	public void setConnectCount(int count) {
		this.connect = count;
		String connectdDevice = LedBleApplication.getInstance().getResources().getString(R.string.connect_count, count);
		this.textViewTotal.setText(connectdDevice);
		juedg();
	}

	public void turnOn() {
		toggleButton.setChecked(true);
		NetConnectBle.getInstanceByGroup(groupName).turnOn();
	}

	public void juedg() {
		if (toggleButton.isChecked() && connect > 0) {
			this.imageViewControll.setVisibility(View.VISIBLE);
		} else {
			this.imageViewControll.setVisibility(View.INVISIBLE);
		}
	}

	public void setCloseDisable(){
		turnOff();
		toggleButton.setChecked(false);
	}
	public void turnOff() {
		toggleButton.setChecked(false);
		NetConnectBle.getInstanceByGroup(groupName).turnOff();
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public View getGroupView() {
		return groupView;
	}

	public void setGroupView(View groupView) {
		this.groupView = groupView;
	}


	public SlideSwitch getSlideSwitch() {
		return slideSwitch;
	}


	public void setSlideSwitch(SlideSwitch slideSwitch) {
		this.slideSwitch = slideSwitch;
	}

}
