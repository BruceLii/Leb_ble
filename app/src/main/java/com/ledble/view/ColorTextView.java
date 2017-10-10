package com.ledble.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class ColorTextView extends TextView {

	private int color=-1;

	public ColorTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

}
