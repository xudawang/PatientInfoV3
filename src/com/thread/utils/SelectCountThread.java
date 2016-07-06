package com.thread.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.utils.FanHuiCanShu;

public class SelectCountThread implements Runnable{
	
	private String path;
	private JSONObject getJSON;
	
	public SelectCountThread(){}
	
	public SelectCountThread(String path) {
		this.path = path;
	}

	public void getCount() {
		// TODO Auto-generated method stub
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
			json_send.put("SELECTALL", "SELECTALL");
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

				initCount();
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private void initCount() {
		try {
			FanHuiCanShu.count = getJSON.getInt("病人信息总数");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void run() {
		getCount();
	}

}
