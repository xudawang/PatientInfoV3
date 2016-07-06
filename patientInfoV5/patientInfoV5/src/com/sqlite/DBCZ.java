package com.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.utils.LoginInfo;
import com.utils.szIP;

public class DBCZ {
	private CJDB cjdb = null;
	
	public DBCZ(Context context) {
		cjdb = new CJDB(context);
	}
	
	/**
	 * 添加IP数据
	 * @param szip
	 * @return
	 */
	public String addszIP(szIP szip) {
		SQLiteDatabase db = cjdb.getWritableDatabase();
		String sql = "replace into szIP(IP)values(?)";
		db.execSQL(sql, new Object[]{szip.getIp()});
		db.close();
		return "添加成功";
	}
	
	/**
	 * 添加登录者信息
	 * @param loginInfo
	 * @return
	 */
	public String addUser(LoginInfo loginInfo) {
		SQLiteDatabase db = cjdb.getWritableDatabase();
		String sql = "replace into user(Name)values(?)";
		db.execSQL(sql, new Object[]{loginInfo.getName()});
		db.close();
		return "添加成功";
	}
	
	/**
	 * 删除IP数据
	 * @param szip
	 * @return
	 */
	public String deleteszIP(szIP szip) {
		SQLiteDatabase db = cjdb.getWritableDatabase();
		String sql = "delete from szIP where ID = ?";
		db.execSQL(sql, new Object[]{szip.getId()});
		db.close();
		return "删除成功";
	}
	
	/**
	 * 更新IP数据
	 * @param szip
	 * @return
	 */
	public String updateszIP(szIP szip) {
		SQLiteDatabase db = cjdb.getWritableDatabase();
		String sql = "update szIP set IP = ? where ID = ?";
		db.execSQL(sql, new Object[]{szip.getId(), szip.getIp()});
		db.close();
		return "更新成功";
	}
	
	/**
	 * 查询IP数据
	 * @return
	 */
	public List<szIP> selectszIp() {
		List<szIP> datas = new ArrayList<szIP>();
		SQLiteDatabase db = cjdb.getReadableDatabase();
		String sql = "select * from szIP";
		Cursor cursor = null;
		cursor = db.rawQuery(sql, null);
		while(cursor.moveToNext()) {
			String ID = String.valueOf(cursor.getInt(cursor.getColumnIndex("id")));
			String IP = cursor.getString(cursor.getColumnIndex("IP"));
			szIP szip = new szIP(ID, IP);
			datas.add(szip);
		}
		cursor.close();
		db.close();
		return datas;
	}
	
	public List<LoginInfo> selectUserName() {
		List<LoginInfo> datas = new ArrayList<LoginInfo>();
		SQLiteDatabase db = cjdb.getReadableDatabase();
		String sql = "select * from user";
		Cursor cursor = null;
		cursor = db.rawQuery(sql, null);
		while(cursor.moveToNext()) {
			String Name = cursor.getString(cursor.getColumnIndex("Name"));
			LoginInfo loginInfo = new LoginInfo();
			loginInfo.setName(Name);
			datas.add(loginInfo);
		}
		cursor.close();
		db.close();
		return datas;
	}

}
