package com.ledble.db;

public class Group {

	public final static String GROUP_TAB = "group_s";
	public final static String GROUP_NAME = "group_name";
	public final static String GROUP_ISON = "is_on";
	private String groupName;
	private String isOn;

	public Group() {
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getIsOn() {
		return isOn;
	}

	public void setIsOn(String isOn) {
		this.isOn = isOn;
	}

}
