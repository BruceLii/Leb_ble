package com.ledble.bean;

import com.common.bean.IBeanInterface;

public class SPIBean implements IBeanInterface{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int mode=-2;
	private int speed=-2;
	private int brightness=-2;

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getBrightness() {
		return brightness;
	}

	public void setBrightness(int brightness) {
		this.brightness = brightness;
	}

}
