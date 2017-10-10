package com.ledble.db;

import com.common.bean.IBeanInterface;

public class GroupDevice implements IBeanInterface{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//==========
	public final static String GROUP_CONTENT_TAB="group_content_name";//组
	public final static String ADDRESSNUM= "address";//地址
	public final static String GROUPNUM = "groupName";//所在组名

	private String address;
	private String groupName;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}
