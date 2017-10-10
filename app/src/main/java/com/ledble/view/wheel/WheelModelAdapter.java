package com.ledble.view.wheel;

import android.content.Context;

public class WheelModelAdapter extends ArrayWheelAdapter {

	private String[] model;

	public WheelModelAdapter(Context context, String[] model) {
		super(context, model);
		this.model = model;
	}

	@Override
	public CharSequence getItemText(int index) {
		return model[index];
	}

}
