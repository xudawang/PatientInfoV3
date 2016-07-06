package com.thread.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Notification;
import android.app.NotificationManager;
import android.util.Log;

import com.utils.BrInfo;
import com.utils.FanHuiCanShu;
import com.utils.GETUIDatas;
import com.utils.GETUIInfo;

public class GETUIThread implements Runnable{
	private String path, time;
	private JSONObject getJSON;
	private int count;
	
	public GETUIThread(String path, String time) {
		this.path = path;
		this.time = time;
	}
	
	public GETUIThread(){}

	@Override
	public void run() {
		try {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(5 * 1000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-Type",
					"application/json; charset=utf-8");
			conn.setRequestProperty("Accept", "application/json");

			JSONObject json_send = new JSONObject();
			json_send.put("��ѯʱ��", time);
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
			if(conn.getResponseCode() == 200) {
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

				initJSONData();	//�����ӿ�����
				
			}
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	private void initJSONData() {
		try {
			GETUIDatas.datas.clear();
			count = getJSON.getInt("������Ϣ����");
			FanHuiCanShu.count = getJSON.getInt("������Ϣ����");
			for (int i = 0; i < count; i++) {
				GETUIDatas.data = new BrInfo();
				GETUIDatas.data.setId(getJSON.getJSONObject("������Ϣ" + i).getString("id"));
				GETUIDatas.data.setName(getJSON.getJSONObject("������Ϣ" + i)
						.getString("name"));
				GETUIDatas.data.setAge(getJSON.getJSONObject("������Ϣ" + i).getString("age"));
				GETUIDatas.data.setSex(getJSON.getJSONObject("������Ϣ" + i).getString("sex"));
				GETUIDatas.data.setLxfs(getJSON.getJSONObject("������Ϣ" + i).getString(
						"phone"));
				GETUIDatas.data.setBrzz(getJSON.getJSONObject("������Ϣ" + i)
						.getString("brzz"));
				GETUIDatas.data.setYz(getJSON.getJSONObject("������Ϣ" + i).getString("yz"));
				GETUIDatas.data.setZybch(getJSON.getJSONObject("������Ϣ" + i).getString(
						"zybch"));
				GETUIDatas.data.setHyzk(getJSON.getJSONObject("������Ϣ" + i)
						.getString("hyzk"));
				GETUIDatas.data.setYxck(getJSON.getJSONObject("������Ϣ" + i)
						.getString("yxck"));
				GETUIDatas.data.setTssm(getJSON.getJSONObject("������Ϣ" + i)
						.getString("tssm"));
				GETUIDatas.data.setShz(getJSON.getJSONObject("������Ϣ" + i).getString("shz"));
				GETUIDatas.data.setShtime(getJSON.getJSONObject("������Ϣ" + i).getString(
						"shtime"));

				GETUIDatas.datas.add(GETUIDatas.data);
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
