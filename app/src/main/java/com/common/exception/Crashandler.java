package com.common.exception;

import java.io.File;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.common.uitl.Constant;
import com.common.uitl.LogUtil;
public class Crashandler implements UncaughtExceptionHandler {

	private static Crashandler INSTANCE;
	private Thread.UncaughtExceptionHandler mDeExceptionHandler;//系统默认的处理类
	private Context mContext;
	private String tag="Crashandler";
	private CrashCallback mCrashCallback;
	private Crashandler() {
		
	}
	
	public static Crashandler getInstance(){
		if(null==INSTANCE){
			INSTANCE=new Crashandler();
		}
		return INSTANCE;
	}

    public void init(Context ctx,CrashCallback mcback){
    	mContext=ctx;
    	this.mCrashCallback=mcback;
    	mDeExceptionHandler=Thread.getDefaultUncaughtExceptionHandler();
    	Thread.setDefaultUncaughtExceptionHandler(this);
    }
	
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		handleException(ex);
	}
	
	private void handleException(final Throwable ex){
		mCrashCallback.OnCrash(ex);
		
		
		 if(ex==null){
			 return ;
		 }
		 LogUtil.e(Constant.TAG, " crashhandler:"+ex.getLocalizedMessage());
		 ex.printStackTrace();
		 
		try {
			
			File logDir=new File("sdcard/log");
			if(logDir.exists()==false){
				logDir.mkdir();
			}
			PrintStream errs = new PrintStream("/sdcard/log/"+mContext.getPackageName()+System.currentTimeMillis()+".log");
			ex.printStackTrace(errs);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		 final String msg=ex.getLocalizedMessage();
		 Log.e(tag, "ex:"+msg);
		 new Thread(){
			 @Override
			public void run() {
				 Looper.prepare();
				 Toast.makeText(mContext, "不好意思错误了"+ex.getMessage(), Toast.LENGTH_SHORT).show();
				 Looper.loop();
			}
		 }.start();
		 
		 
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(10);
	}
	/**
	 * 回调接口
	 * @author wangzy
	 *
	 */
	public static interface CrashCallback{
		public void OnCrash(Throwable e);
	}
}
