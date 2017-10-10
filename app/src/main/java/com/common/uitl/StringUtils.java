package com.common.uitl;

import java.io.UnsupportedEncodingException;

import android.text.TextUtils;

public class StringUtils {

	/**
	 * 计算字符串长度
	 * 
	 * @param str
	 * @return
	 */
	public static int computStrlen(String str) {
		if (StringUtils.isEmpty(str)) {
			return 0;
		} else {
			return str.length();
		}
	}

	/**
	 * 判断字符串是否为空 ps:此方法在android中有代替，TextUtils.isEmpty
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return str == null || "".equals(str.trim());
	}

	public static boolean isDajiePasswd(String passwd) {
		boolean isPasswd = false;
		if (!isEmpty(passwd)) {
			int len = passwd.length();
			if (len >= 4 && len <= 20) {
				isPasswd = true;
			}
		}
		return isPasswd;
	}

	/**
	 * 判断字符串是否为空或者null字符串
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmptyNull(String str) {
		return str == null || "".equals(str.trim()) || "null".equals(str);
	}

	/**
	 * 将数组转为字符串
	 * 
	 * @param array
	 * @param encode
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String convertByteArrayToString(byte[] array, String encode) throws UnsupportedEncodingException {
		if (array == null || array.length == 0) {
			return null;
		}
		return new String(array, encode);
	}

	/**
	 * 返回int型参数的string值
	 * 
	 * @param param
	 * @return
	 */
	public static String nvl(int param) {
		return String.valueOf(param);
	}
	
	public static String nvl(String param) {
		if (TextUtils.isEmpty(param)) {
			return "";
		} else {
			return param;
		}
	}
	public static int getCharCount(String soucestr, char dstchar) {
		int count = 0;
		if (!isEmpty(soucestr)) {
			char[] chary = soucestr.toCharArray();
			for (char cin : chary) {
				if (cin == dstchar) {
					count++;
				}
			}
		}
		return count;
	}

	// ---------------------下面的方法目前未使用----------------------------//

	public static String convertByteArrayToString(byte[] array) throws UnsupportedEncodingException {
		return convertByteArrayToString(array, "utf-8");
	}

}
