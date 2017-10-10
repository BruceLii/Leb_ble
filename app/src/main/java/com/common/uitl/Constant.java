package com.common.uitl;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * 常量集合
 * 
 * @author:wangzhengyun 2012-9-11
 */
public class Constant {

	public final static String STRING_AES_KEY = "1234567890abcDEF";// 必须是16位
	public final static String STRING_IMEI = "imei";
	// forceUpdate,reason,desc,downurl
	public final static String STRING_KEY_FOCEUPDATE = "update_force";
	public final static String STRING_KEY_RESON = "update_reson";
	public final static String STRING_KEY_DESC = "update_desc";

	public final static String STRING_KEY_PHONE = "phone_number";
	public final static String STRING_KEY_PWD = "passwd";
	public final static String STRING_KEY_AUTO_LOGIN = "auto_login";
	public final static String STRING_FILE_CITY_AREA_DIR = "city_area";
	public final static String STRING_FILE_CITY_FILE_PREFIX = "city_area_";
	public final static String STRING_CITY_DATA_FILE_NAME = "city_area_0.xml";
	public final static String STRING_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
	public final static String STRING_DAY_FORMAT = "yyyy/MM/dd";
	public final static String STRING_DAY_FORMAT2 = "yyyy-MM-dd";
	public final static String STRING_DAY_FORMAT3 = "MM-dd";
	public final static String STRING_DAY_FORMAT4 = "yyyy-MM-dd HH:mm:ss";
	public final static String STRING_DAY_FORMAT5 = "yyyy-MM-dd HH:mm";
	public final static String STRING_HOTEL_FILTER_BOOK_ONLY = "book_only";
	public final static String STRING_CACHE_DIR= "/sdcard/imgcache/";
	public final static String STRING_FILE_CACHE= "fileCache";
	public final static String STRING_FILE_CACHE_TEMP= "fileCacheTemp";
	//下载apk后保存的路径
	public final static String STRING_SAVE_APK_DIR = "sdcard" + File.separator + "travelsky"+ File.separator + "com.travelsky.apk";

	public final static SimpleDateFormat SDF = new SimpleDateFormat(
			STRING_DATE_FORMAT);
	public final static SimpleDateFormat SDFD = new SimpleDateFormat(
			STRING_DAY_FORMAT);
	public final static SimpleDateFormat SDFD2 = new SimpleDateFormat(
			STRING_DAY_FORMAT2);
	public final static SimpleDateFormat SDFD3 = new SimpleDateFormat(
            STRING_DAY_FORMAT3);
	public final static SimpleDateFormat SDFD4 = new SimpleDateFormat(
            STRING_DAY_FORMAT4);
	public final static SimpleDateFormat SDFD5 = new SimpleDateFormat(
            STRING_DAY_FORMAT5);
	public final static String TAG = "skyworth";
	public final static String TAG_TRACETRIP="tracetrip";
	public final static String TAG_SKYWORTH_AIRPORT="skyairport";
}
