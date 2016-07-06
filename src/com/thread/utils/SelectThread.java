package com.thread.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.utils.BrInfo;
import com.utils.SJJXUtils;

public class SelectThread implements Runnable{
	private String path;
	private String Id;
	private String select_fs;
	private String select_cs;
	
	private JSONObject getJSON;
	
	public SelectThread(String path, String select_fs, String select_cs) {
		this.path = path;
		this.Id = Id;
		this.select_fs = select_fs;
		this.select_cs = select_cs;
	}
	
	public SelectThread(){}

	/**
	 * 执行查询操作
	 */
	@Override
	public void run() {
		try {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url
					.openConnection();
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(5 * 1000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-Type",
					"application/json; charset=utf-8");
			conn.setRequestProperty("Accept", "application/json");

			JSONObject json_send = new JSONObject();
			json_send.put(select_fs, select_cs);
			try {
				BufferedWriter br = new BufferedWriter(
						new OutputStreamWriter(conn.getOutputStream(),
								"utf-8"));
				br.write(json_send.toString());
				br.flush();
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (conn.getResponseCode() == 200) {
				BufferedReader br = new BufferedReader(
						new InputStreamReader(conn.getInputStream(),
								"utf-8"));
				StringBuffer sb = new StringBuffer();
				String temp = "";
				while ((temp = br.readLine()) != null) {
					sb.append(temp);
				}
				br.close();
				JSONTokener json = new JSONTokener(sb.toString());
				getJSON = (JSONObject) json.nextValue();
				initJSONData();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取返回的病人信息数据
	 */
	public void initJSONData() {
		try {
			SJJXUtils.data.setId(getJSON.getJSONObject("病人信息").getString("id"));
			SJJXUtils.data.setName(getJSON.getJSONObject("病人信息").getString("name"));
			SJJXUtils.data.setAge(getJSON.getJSONObject("病人信息").getString("age"));
			SJJXUtils.data.setSex(getJSON.getJSONObject("病人信息").getString("sex"));
			SJJXUtils.data.setLxfs(getJSON.getJSONObject("病人信息").getString("phone"));
			SJJXUtils.data.setBrzz(getJSON.getJSONObject("病人信息").getString("brzz"));
			SJJXUtils.data.setYz(getJSON.getJSONObject("病人信息").getString("yz"));
			SJJXUtils.data.setZybch(getJSON.getJSONObject("病人信息").getString("zybch"));
			SJJXUtils.data.setHyzk(getJSON.getJSONObject("病人信息").getString("hyzk"));
			SJJXUtils.data.setYxck(getJSON.getJSONObject("病人信息").getString("yxck"));
			SJJXUtils.data.setTssm(getJSON.getJSONObject("病人信息").getString("tssm"));
			SJJXUtils.data.setShz(getJSON.getJSONObject("病人信息").getString("shz"));
			SJJXUtils.data.setShtime(getJSON.getJSONObject("病人信息").getString("shtime"));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
