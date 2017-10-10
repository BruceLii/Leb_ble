package com.ledble.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.common.uitl.LogUtil;
import com.ledble.base.LedBleApplication;

public class GroupDeviceDao {
	private SQLiteHelper sqLiteHelper;

	public GroupDeviceDao(Context context) {
		sqLiteHelper = new SQLiteHelper(context);
	}

	public ArrayList<Group> getAllgroup() {
		ArrayList<Group> groups = new ArrayList<Group>();
		SQLiteDatabase sdb = sqLiteHelper.getWritableDatabase();
		Cursor cursor = sdb.rawQuery("select * from " + Group.GROUP_TAB, new String[] {});
		// cursor.moveToFirst();
		while (cursor.moveToNext()) {
			Group group = new Group();
			group.setGroupName((cursor.getString(cursor.getColumnIndex(Group.GROUP_NAME))));
			group.setIsOn((cursor.getString(cursor.getColumnIndex(Group.GROUP_ISON))));
			groups.add(group);

			LogUtil.i(LedBleApplication.tag, "group:" + group.getGroupName() + " is on:" + group.getIsOn());
		}
		sdb.close();
		return groups;
	}

	/**
	 * 更新组信息
	 * @param groupList
	 */
	public void updateGroupStatus(ArrayList<Group> groupList) throws Exception {
		SQLiteDatabase sdb = sqLiteHelper.getWritableDatabase();
		for (int i = 0, isize = groupList.size(); i < isize; i++) {
			Group group = groupList.get(i);
			
			ContentValues values = new ContentValues();
			values.put(Group.GROUP_ISON, group.getIsOn());
			
			sdb.update(Group.GROUP_TAB, values, Group.GROUP_NAME + " =?", new String[] { group.getGroupName() });
		}
		sdb.close();
	}

	public ArrayList<GroupDevice> getAllGroupDevices() {
		ArrayList<GroupDevice> groupDevices = new ArrayList<GroupDevice>();
		SQLiteDatabase sdb = sqLiteHelper.getWritableDatabase();
		Cursor cursor = sdb.rawQuery("select * from " + GroupDevice.GROUP_CONTENT_TAB, new String[] {});
		// cursor.moveToFirst();
		while (cursor.moveToNext()) {
			GroupDevice groupDevice = new GroupDevice();
			groupDevice.setAddress(cursor.getString(cursor.getColumnIndex(GroupDevice.ADDRESSNUM)));
			groupDevice.setGroupName(cursor.getString(cursor.getColumnIndex(GroupDevice.GROUPNUM)));
			groupDevices.add(groupDevice);
		}
		sdb.close();
		return groupDevices;
	}

	public void delteByGroup(String groupName) {
		SQLiteDatabase sdb = sqLiteHelper.getWritableDatabase();
		sdb.execSQL("DELETE FROM " + GroupDevice.GROUP_CONTENT_TAB + " WHERE " + GroupDevice.GROUPNUM + " = ? ", new Object[] { groupName });
		sdb.close();
	}

	public ArrayList<GroupDevice> getDevicesByGroup(String groupName) {
		ArrayList<GroupDevice> groupDevices = new ArrayList<GroupDevice>();
		SQLiteDatabase sdb = sqLiteHelper.getWritableDatabase();
		Cursor cursor = sdb.rawQuery("select * from " + GroupDevice.GROUP_CONTENT_TAB + " where " + GroupDevice.GROUPNUM + " =?",
				new String[] { groupName });
		// cursor.moveToFirst();
		while (cursor.moveToNext()) {
			GroupDevice groupDevice = new GroupDevice();
			groupDevice.setAddress(cursor.getString(cursor.getColumnIndex(GroupDevice.ADDRESSNUM)));
			groupDevice.setGroupName(cursor.getString(cursor.getColumnIndex(GroupDevice.GROUPNUM)));
			groupDevices.add(groupDevice);
		}
		sdb.close();
		return groupDevices;
	}

	public void addGroup(String groupName) throws Exception {
		SQLiteDatabase sdb = sqLiteHelper.getWritableDatabase();
		if (null != sdb && sdb.isOpen()) {
			ContentValues values = new ContentValues();
			values.put(Group.GROUP_NAME, groupName);
			sdb.insert(Group.GROUP_TAB, null, values);
			sdb.close();
		}
	}

	public void save2Group(ArrayList<GroupDevice> groupDevice) throws Exception {
		SQLiteDatabase sdb = sqLiteHelper.getWritableDatabase();
		if (null != sdb && sdb.isOpen()) {
			for (GroupDevice gd : groupDevice) {
				ContentValues values = new ContentValues();
				values.put(GroupDevice.ADDRESSNUM, gd.getAddress());
				values.put(GroupDevice.GROUPNUM, gd.getGroupName());
				sdb.insert(GroupDevice.GROUP_CONTENT_TAB, null, values);
			}
			sdb.close();
		}
	}

	public void deleteGroup(String groupName) throws Exception {
		SQLiteDatabase sdb = sqLiteHelper.getWritableDatabase();
		if (null != sdb && sdb.isOpen()) {
			sdb.execSQL("DELETE FROM " + Group.GROUP_TAB + " WHERE " + Group.GROUP_NAME + " = ?", new Object[] { groupName });
			sdb.close();
		}
	}

	public void deleteGroupDevice(ArrayList<GroupDevice> groupDevice) throws Exception {
		SQLiteDatabase sdb = sqLiteHelper.getWritableDatabase();
		if (null != sdb && sdb.isOpen()) {
			for (GroupDevice gd : groupDevice) {
				sdb.execSQL("DELETE FROM " + GroupDevice.GROUP_CONTENT_TAB + " WHERE " + GroupDevice.ADDRESSNUM + " = ? & "
						+ GroupDevice.GROUPNUM + " = ? ", new Object[] { gd.getAddress(), gd.getGroupName() });
			}
			sdb.close();
		}
	}

}
