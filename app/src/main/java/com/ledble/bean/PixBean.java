package com.ledble.bean;

import com.common.bean.IBeanInterface;

public class PixBean implements IBeanInterface{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int rgbIndex;
	private int bannerType;
	private int pixNumber;
	public int getRgbIndex() {
		return rgbIndex;
	}
	public void setRgbIndex(int rgbIndex) {
		this.rgbIndex = rgbIndex;
	}
	public int getBannerType() {
		return bannerType;
	}
	public void setBannerType(int bannerType) {
		this.bannerType = bannerType;
	}
	public int getPixNumber() {
		return pixNumber;
	}
	public void setPixNumber(int pixNumber) {
		this.pixNumber = pixNumber;
	}
	
}
