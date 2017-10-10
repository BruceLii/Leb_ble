package com.common.uitl;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 * 
 * @author:wangzhengyun 2012-9-11
 */
public class DateTool {


	public static String getTimeStamp(long then) {
		Calendar ca = Calendar.getInstance();
		ca.setTimeInMillis(then);
		Date nowDate = new Date();
		long now = nowDate.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String thenStr = sdf.format(ca.getTime());
		String d2 = sdf.format(nowDate);
		String time = new String();
		if (thenStr.equals(d2)) {
			// 当天的（以24小时自然时来算），就按：刚刚、1小时前、2小时前….等来设定；
			// ⑵如果是昨日的，就写昨日；
			// ⑶如果超过昨日，就按照xxx-xx-xx的格式来写，如：2013-11-22；
			long time_d = Math.abs(now - then);
			long muniute = time_d / 1000 / 60;// 相差的分钟数
			if (muniute < 60) {
				time = "刚刚";
			} else {
				time = (muniute / 60) + "小时前";
			}
		} else {
			Date thenDate = ca.getTime();
			if ((nowDate.getYear() == thenDate.getYear()) && (nowDate.getMonth() == thenDate.getMonth())) {
				if (nowDate.getDay() == (thenDate.getDay() + 1)) {
					time = "一天前";
				} else {
					time = sdf.format(thenDate);
				}
			}else{
				time = sdf.format(thenDate);
			}
		}
		return time;
	}
	public static String geLongTimeMonthDay(long time) {
		Calendar instance = Calendar.getInstance();
		instance.setTimeInMillis(time);
		Date d = instance.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
		return sdf.format(d);
	}

	public static String getLongTime4Ymdhms(long time) {
		Calendar instance = Calendar.getInstance();
		instance.setTimeInMillis(time);
		Date d = instance.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		return sdf.format(d);
	}

	/**
	 * 格式化日期
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String format(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String datestr = sdf.format(date);
		return datestr;
	}

	/**
	 * 获取当前月份
	 * 
	 * @return
	 */
	public static String getNowMonth() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			Date d = new Date();
			return sdf.format(d);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取年份中的月份
	 * 
	 * @param yyyymm
	 * @return
	 */
	public static String getMonthFrom(String yyyymm) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			Date date = sdf.parse(yyyymm);
			Calendar ca = Calendar.getInstance();
			ca.setTime(date);
			return String.valueOf(ca.get(Calendar.MONTH) + 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return yyyymm;
	}

	/**
	 * 返回年eg：2013
	 * 
	 * @return
	 */
	public static int getThisYear() {
		Calendar ca = Calendar.getInstance();
		ca.setTime(new Date());
		return ca.get(Calendar.YEAR);
	}

	/**
	 * 格式化年月，返回 eg：2012年10月
	 * 
	 * @param yyyymm
	 * @return
	 */
	public static String formatYearMonth(String yyyymm) {
		String format = "";
		try {
			String year = yyyymm.substring(0, 4) + "年";
			String month = String.valueOf(Integer.parseInt(yyyymm.substring(4, yyyymm.length()))) + "月";
			format = year + month;
		} catch (Exception e) {
			e.printStackTrace();
			return yyyymm;
		}
		return format;
	}

	/**
	 * 月份加0
	 * 
	 * @param monthIn
	 * @return
	 */
	public static String monthFormat(String monthIn) {
		if (StringUtils.isEmpty(monthIn)) {
			return "01";
		} else {
			if (monthIn.length() == 1) {
				return "0" + monthIn;
			} else {
				return monthIn;
			}
		}
	}

	/**
	 * 获取某年某月的天数
	 * 
	 * @param yyyyMM
	 * @return
	 * @throws ParseException
	 */
	public static int getDayCountforMoneth(String yyyyMM) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			Date date = sdf.parse(yyyyMM);
			Calendar ca = Calendar.getInstance();
			ca.setTime(date);
			int days = ca.getActualMaximum(Calendar.DAY_OF_MONTH);
			return days;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 28;
	}

	/**
	 * 返回日期的天
	 * 
	 * @param yyyyMMdd
	 * @return
	 */
	public static String getDayForDate(String yyyyMMdd) {
		if (!StringUtils.isEmpty(yyyyMMdd) && yyyyMMdd.length() == 8) {
			return Integer.parseInt(yyyyMMdd.substring(yyyyMMdd.length() - 2, yyyyMMdd.length())) + "";
		}
		return "";
	}

	/**
	 * 返回日期中的天数
	 * 
	 * @param yyyyMM
	 * @return
	 */
	public static String getYearMonth(String yyyyMM) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			Date date = sdf.parse(yyyyMM.trim());
			Calendar ca = Calendar.getInstance();
			ca.setTime(date);
			int month = ca.get(Calendar.MONTH) + 1;
			return String.valueOf(month);
		} catch (Exception e) {
		}
		return yyyyMM;
	}

	/**
	 * 在输入的日期上加deltaDate天，返回之后的Date
	 * 
	 * @param date
	 * @param deltaDate
	 * @return
	 */
	public static Date addDayOnDate(Date date, int deltaDate) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.set(Calendar.DATE, (ca.get(Calendar.DATE) + deltaDate));
		return ca.getTime();
	}

	/**
	 * 判断起飞事件是否在某个事件段,输入格式 2012-10-04 12:11,5:00-12:00
	 * 
	 * @param inputs
	 * @param time_betwwen
	 * @return
	 */
	public static boolean isTimeBetwwen(String inputs, String time_betwwen) {
		if (!time_betwwen.contains("-")) {
			return true;
		} else {
			String[] times = time_betwwen.split("-");
			int startHour = Integer.parseInt(times[0].split(":")[0]);
			int endHour = Integer.parseInt(times[1].split(":")[0]);
			try {
				Date d = Constant.SDFD5.parse(inputs);
				Calendar ca = Calendar.getInstance();
				ca.setTime(d);
				int endMinus = ca.get(Calendar.MINUTE);

				int departHour = ca.get(Calendar.HOUR_OF_DAY);
				if (departHour >= startHour /* && departHour <= endHour */) {
					if (departHour < endHour) {// 包含整点
						return true;
					} else if ((departHour == endHour) && (endMinus == 0)) {
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		}
	}

	/**
	 * 获取时间的字符串 yyyy-MM-dd HH:mm
	 * 
	 * @param inputs
	 * @return
	 * @throws ParseException
	 */
	public static Date getDateFormStr(String inputs) throws ParseException {
		Date d = Constant.SDFD5.parse(inputs);
		return d;
	}

	/**
	 * 将分钟变成小时
	 * 
	 * @param inputMinues
	 * @return
	 */
	public static String formatMinutes(int inputMinues) {
		String time = "";
		int hour = inputMinues / 60;
		if (hour != 0) {
			time += hour + "小时";
		}
		if (inputMinues % 60 != 0) {
			time += String.valueOf(inputMinues % 60) + "分钟";
		}
		return time;
	}

	/**
	 * 返回时间字符串小时分钟 10:44,仅限于yyyy-MM-dd HH:mm格式
	 * 
	 * @param inputs
	 * @return
	 */
	public static String getTimeHourAndMinute(String inputs) {
		String returnStr = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date time = sdf.parse(inputs);
			Calendar ca = Calendar.getInstance();
			ca.setTime(time);
			int hour = ca.get(Calendar.HOUR_OF_DAY);
			int minute = ca.get(Calendar.MINUTE);
			returnStr = String.valueOf(formatNumber(hour) + ":" + formatNumber(minute));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnStr;
	}

	public static String formatNumber(int inputs) {
		DecimalFormat dcf = new DecimalFormat("00");
		String ret = dcf.format(inputs);
		return ret;
	}

	/**
	 * 获取月和日
	 * 
	 * @param d
	 * @return
	 */
	public static String getDateMonthAndDay(Date d) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(d);
		int month = ca.get(Calendar.MONTH);
		int day = ca.get(Calendar.DATE);
		String m = String.valueOf((month + 1) + "月" + day);
		return m;
	}

	/**
	 * 返回 对应日期所在星期，比如2011/11/15返回"四"
	 * 
	 * @param d
	 * @return
	 */
	public static String getDateInWeekDay(Date d) {
		String weekday[] = { "日", "一", "二", "三", "四", "五", "六" };
		Calendar ca = Calendar.getInstance();
		ca.setTime(d);
		int dweek = ca.get(Calendar.DAY_OF_WEEK);
		return weekday[dweek - 1];
	}

	/**
	 * 获取小时和分钟仅仅限于 "yyyy-MM-dd HH:mm";格式
	 * 
	 * @param input
	 * @return
	 */
	public static String parseDateForHAndM(String input) {
		String result = "";
		try {
			String pattern = "yyyy-MM-dd HH:mm";
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			Date d = sdf.parse(input);
			SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
			result = sdf2.format(d);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 把201210变成2012年10月
	public static String spliteYearMonth4Number(String inputyyyyMM) {
		StringBuffer sbf = new StringBuffer();
		sbf.append(inputyyyyMM.substring(0, 4) + "年");
		sbf.append(Integer.parseInt(inputyyyyMM.substring(4, inputyyyyMM.length())) + "月");
		return sbf.toString();
	}

	// 把20120101变成2012年1月日
	public static String spliteYearMonthDay4Number(String inputyyyyMMdd) {
		StringBuffer sbf = new StringBuffer();
		sbf.append(inputyyyyMMdd.substring(0, 4) + "年");
		sbf.append(Integer.parseInt(inputyyyyMMdd.substring(4, 6)) + "月");
		sbf.append(Integer.parseInt(inputyyyyMMdd.substring(6, 8)) + "日");
		return sbf.toString();
	}

	public static String getDayByDate(Date d) {
		if (null == d) {
			return "";
		}
		String daystr = "";
		int day = d.getDay();
		switch (day) {
		case 0:
			daystr = "星期日";
			break;
		case 1:
			daystr = "星期一";
			break;
		case 2:
			daystr = "星期二";
			break;
		case 3:
			daystr = "星期三";
			break;
		case 4:
			daystr = "星期四";
			break;
		case 5:
			daystr = "星期五";
			break;
		case 6:
			daystr = "星期六";
			break;

		default:
			break;
		}
		return daystr;
	}
}
