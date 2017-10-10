package com.common.task;

import java.util.HashMap;

import com.common.net.NetResult;

public abstract class NetCallBack {
	
	public  void onPreCall(){}
	public abstract NetResult onDoInBack(HashMap<String, String> paramMap);
	public void onCanCell(){};
	public abstract void onFinish(NetResult result);

}
