package com.ledble.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
	
	private static final String dbname="group.db";
	private static final int version=1;
	
	public SQLiteHelper(Context context) {
		super(context, dbname, null, version);
	}
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		//组名
		db.execSQL("CREATE TABLE IF NOT EXISTS " + Group.GROUP_TAB + " ( " +
				Group.GROUP_NAME + " varchar primary key,"+
				Group.GROUP_ISON + " varchar"+
				" ) ");
		
		
		//组名和附属的组
		db.execSQL("CREATE TABLE IF NOT EXISTS " + GroupDevice.GROUP_CONTENT_TAB + " ( " +
				GroupDevice.ADDRESSNUM + " varchar,"+ 
				GroupDevice.GROUPNUM + " varchar" +" ) ");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}


}
