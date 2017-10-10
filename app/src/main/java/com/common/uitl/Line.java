package com.common.uitl;

public class Line {

	private float k;
	private float b;

	public Line() {
	}

	public float getK() {
		return k;
	}

	public void setK(float k) {
		this.k = k;
	}

	public float getB() {
		return b;
	}

	public void setB(float b) {
		this.b = b;
	}

	@Override
	public String toString() {
	
		return "k:"+k+"  b:"+b;
	}
}
