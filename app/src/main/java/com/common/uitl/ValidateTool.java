package com.common.uitl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateTool {

	/**
	 * 检测中文姓名
	 * 
	 * @param name
	 * @return
	 */
	public static boolean checkChineseName(String name) {
		if (StringUtils.isEmpty(name) || !name.matches("[\u4e00-\u9fa5]{2,4}")) {
			return false;
		} else
			return true;
	}

	public static boolean checkEmail(String emailStr) {
		if(StringUtils.isEmpty(emailStr)){
			return false;
		}
		String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(emailStr);
		boolean isMatched = matcher.matches();
		if (isMatched) {
			return true;
		}
		return false;
	}

}
