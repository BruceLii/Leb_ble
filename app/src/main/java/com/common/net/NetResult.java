package com.common.net;

import java.io.Serializable;

/**
 * 网络返回接口封装
 * 
 * @author:wangzhengyun 2012-8-14
 */
public class NetResult implements Serializable {

	public static final String CODE_OK = "0";
	public static final String CODE_ERROR = "10000";

	private boolean isSuccess;
	private String message;
	private String code;
	private Object[] data;
	private Object tag;

	public NetResult() {
	}

	public Object getTag() {
		return tag;
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Object[] getData() {
		return data;
	}

	public void setData(Object[] data) {
		this.data = data;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

}
