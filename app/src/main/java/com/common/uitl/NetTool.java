package com.common.uitl;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

/**
 * 网络工具
 * 
 * @author:wangzhengyun 2012-9-11
 */
public class NetTool {

	public static String tag = NetTool.class.getSimpleName();


	
	private static HashMap<String,String> paramMap=null;
	/**
	 * 获取通用公共请求参数
	 * @return
	 */
	public static HashMap<String,String> getCommonParam(Context context){
     if(null==paramMap){
    	 paramMap=new HashMap<String, String>();
 		paramMap.put("deviceId", Tool.getImei(context));
 		paramMap.put("imei", Tool.getImei(context));
 		paramMap.put("clientVersion", Tool.getVersionName(context));
 		paramMap.put("client_agent", Tool.getVersionName(context));
 		paramMap.put("market", Tool.getChannel(context));
 		paramMap.put("netType", NetTool.getNetType(context));
 		paramMap.put("clientType","1");
 		paramMap.put("clientPhone",android.os.Build.MODEL);//手机型号
 		paramMap.put("phoneVersion",Build.VERSION.RELEASE);//操作系统版本
 		Point p = Tool.getDisplayMetrics(context);
 		paramMap.put("screen", "("+p.x+","+p.y+")");
     }
    
		
//		if(null!=App.getInstance().getCurrentUser()){
//			paramMap.put("type", "1");
//			paramMap.put("uid", App.getInstance().getCurrentUser().getUid());
//		}else{
//			paramMap.put("type", "2");
//			paramMap.put("uid", Tool.getImei(context));
//		}
		
//		clientType
//		clientPhone
//		netType
//		market
//		type

		
		return paramMap;
	}

	/**
	 * 获取网络类型，返回cmwap,cmnet,wifi,没有查找到的话返回null
	 * 
	 * @param context
	 * @return
	 */
	public static String getNetType(Context context) {
		String networkType = null;
		try {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = cm.getActiveNetworkInfo();// NULL
			if (networkInfo != null && networkInfo.isAvailable()) {

				String typeName = networkInfo.getTypeName(); // MOBILE/WIFI
				if (!"MOBILE".equalsIgnoreCase(typeName)) {
					networkType = typeName;
				} else {
					networkType = networkInfo.getExtraInfo(); // cmwap/cmnet/wifi/uniwap/uninet
					if (networkType == null) {
						networkType = typeName + "#[]";
					}
				}
			}
		} catch (Exception e) {
			Log.e(tag, "getNetType", e);
		}

		return networkType;
	}

	/**
	 * 判断是否有可用网络
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connec = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean available = false;
		// NetworkInfo[] networkInfos = connec.getAllNetworkInfo();
		// connec.getActiveNetworkInfo();
		// connec.getAllNetworkInfo();
		// NetworkInfo
		// info=connec.getNetworkInfo(ConnectivityManager.DEFAULT_NETWORK_PREFERENCE);
		NetworkInfo[] infos = connec.getAllNetworkInfo();
		if (infos != null) {
			for (NetworkInfo info : infos) {
				if (info.isAvailable()) {
					available = true;
					break;
				}
			}
		}
		return available;
		// return false;
	}
}
