package com.common.uitl;

import android.util.Log;

/**
 * 日志工具，所有的日志用此工具输出
 * @author:wangzhengyun
 * 2012-9-11
 */
public class LogUtil {
	private static final boolean DEBUG = true;

	public static void v(String tag, String msg) {
		if (DEBUG) {
			Log.v(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		d(tag, msg, null);
	}

	public static void d(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			Log.d(tag, msg, tr);
		}
	}

	public static void i(String tag, String msg) {
		if (DEBUG) {
			Log.i(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (DEBUG) {
			Log.w(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		e(tag, msg, null);
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			Log.e(tag, msg, tr);
		}
	}

	public static void println(String str) {
		if (DEBUG) {
			System.out.println(str);
		}
	}
}
