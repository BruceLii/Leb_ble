package com.ledble.bean;

import com.common.bean.IBeanInterface;

public class MyColor implements IBeanInterface {

	public int r;
	public int g;
	public int b;

	public MyColor(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

}
