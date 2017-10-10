package com.common.uitl;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class NumberHelper {

	public static String keepDecimal2(String input) {
		try {
			double i = Double.parseDouble(input);
			DecimalFormat df = new DecimalFormat("0.00");
			return df.format(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return input;
	}

	public static String div10000Str(String input) {
		try {
			double i = Double.parseDouble(input);
			if (i < 10000) {
				return input;
			} else {
				double ret = i / 10000;
				DecimalFormat df = new DecimalFormat("0.00");
				StringBuffer sb = new StringBuffer();
				sb.append(df.format(ret));
				sb.append("万");
				return sb.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return input;
	}

	public static String numberToCnMoney(String money) {
		try {
			StringBuffer sbf = new StringBuffer();
			// 1 1 1 1 1 1 1 1 1 1 1 1
			// 个，十，百，千，万，十万，百万，千万，亿，十亿，百亿，千亿
			long data = (long) Double.parseDouble(money);
			if (money.length() > 5 && money.length() < 9) {
				sbf.append(data / 10000).append("万");
			} else if (money.length() >= 9) {
				if ((data / 100000000) != 0) {
					sbf.append(data / 100000000).append("亿");
				}
				sbf.append(data % 100000000 / 10000).append("万");
			} else {
				return money;
			}
			return sbf.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return money;
		}
	}

	public static String floatToPercent(String floatBelow1) {
		double value = Double.parseDouble(floatBelow1);
		NumberFormat nt = NumberFormat.getPercentInstance();
		// 设置百分数精确度2即保留两位小数
		nt.setMinimumFractionDigits(2);
		// 最后格式化并输出
		return nt.format(value);
	}

	public static String floatToCurrency(String floatValue) {
		float value = Float.parseFloat(floatValue);
		NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.CHINA);
		nf.setMinimumFractionDigits(3);
		return nf.format(value);
	}

	public static String floatToCurrency2(String floatValue) {
		float value = Float.parseFloat(floatValue);
		NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.CHINA);
		nf.setMinimumFractionDigits(2);
		return nf.format(value);
	}

	public static String LeftPad_Tow_Zero(int str) {
		java.text.DecimalFormat format = new java.text.DecimalFormat("00");
		return format.format(str);
	}

	public static String formatNumber(double input) {
		return new DecimalFormat("0.00").format(input);
	}
}
