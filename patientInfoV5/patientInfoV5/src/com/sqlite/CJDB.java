package com.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CJDB extends SQLiteOpenHelper{
	private static final String DB_NAME = "ipxx.db";
	private static final String TABLE_NAME = "szIP";
	private static final String TABLE_NAME_LOGIN = "user";

	public CJDB(Context context) {
		super(context, DB_NAME, null, 1);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 第一次创建数据库执行，只有调用getReaderabledatabase()或者getWriteabledatabase()执行
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "create table " +
				TABLE_NAME +
				"(id INTEGER PRIMARY KEY AUTOINCREMENT,IP varchar(50) UNIQUE)";
		String sql_user = "create table " +
				TABLE_NAME_LOGIN +
				"(id INTEGER PRIMARY KEY AUTOINCREMENT, Name varchar(50) UNIQUE)";
		db.execSQL(sql);
		db.execSQL(sql_user);
		
		Log.i("info", "数据库创建成功");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String sql = "DROP TABLE IF EXISTS" + TABLE_NAME;
		String sql_user = "DROP TABLE IF EXISTS" + TABLE_NAME_LOGIN;
		db.execSQL(sql);
		db.execSQL(sql_user);
		this.onCreate(db);
		
		Log.i("info", "数据库已经更新");
	}

}
