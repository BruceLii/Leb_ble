package com.common.net;

import com.common.uitl.LogUtil;

public class NetException extends Exception {

	private int code = -1;
	private String msg = "";
	private String tag = NetException.class.getSimpleName();

	public NetException(int code, String msg) {
		this.code = code;
		this.msg = msg;
		LogUtil.i(tag, " "+msg);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "code :" + code + " msg:" + msg;
	}

}
