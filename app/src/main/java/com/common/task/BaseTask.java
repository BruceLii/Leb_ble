package com.common.task;

import java.util.HashMap;

import android.os.AsyncTask;

import com.common.net.NetResult;

/**
 * loginTask
 * 
 * @author wangzy
 * 
 */
public class BaseTask extends  AsyncTask<HashMap<String, String>, Void, NetResult> {

	NetCallBack mCallBack;

	public BaseTask(NetCallBack callBack) {
		this.mCallBack = callBack;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (null != mCallBack) {
			mCallBack.onPreCall();
		}
	}

	@Override
	protected NetResult doInBackground(HashMap<String, String>... params) {
		if (null != mCallBack) {
			if(null==params){
				return	mCallBack.onDoInBack(null);
			}else{
				HashMap<String,String> paramMap=params[0];
				NetResult result = mCallBack.onDoInBack(paramMap);
				return	result;
			}
		}
		return null;
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		if (null != mCallBack) {
			mCallBack.onCanCell();
		}
	}

	@Override
	protected void onPostExecute(NetResult result) {
		super.onPostExecute(result);
		if (null != mCallBack) {
			mCallBack.onFinish(result);
		}
	}
}
