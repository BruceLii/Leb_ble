package com.common.uitl;

public class ProjectTool {

	/**
	 * 旅客量，航班量
	 * 
	 * @param dataType
	 * @return
	 */
	public static String getDataTypeText(String dataType) {
		int type = Integer.parseInt(dataType);
		switch (type) {
		case 0:
			return "旅客量";
		case 1:
			return "航班量";
		case 2:
			return "客座率";
		}
		return "";
	}

	public static String getFlightTextByType(String type) {
		if ("0".equals(type)) {
			return "进港";
		}
		if ("1".equals(type)) {
			return "出港";
		}
		return "进出港";
	}

}
