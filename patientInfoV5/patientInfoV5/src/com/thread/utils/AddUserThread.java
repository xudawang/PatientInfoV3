package com.thread.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.utils.FanHuiCanShu;
import com.utils.UserInfo;

public class AddUserThread implements Runnable {
	private String path;
	private UserInfo user_data;
	private JSONObject json_send, json_userInfo;
	private JSONObject getJSON;

	public AddUserThread(String path, UserInfo user_data) {
		this.path = path;
		this.user_data = user_data;
	}

	public AddUserThread() {
	}

	@Override
	public void run() {
		try {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(5*1000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			
			conn.setRequestProperty("Content-Type",
					"application/json; charset=utf-8");
			conn.setRequestProperty("Accept", "application/json");

			json_send = new JSONObject();
			json_userInfo = new JSONObject();
			json_send.put("用户信息", initJOSNContent(json_userInfo));

			try {
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
						conn.getOutputStream(), "utf-8"));
				bw.write(json_send.toString());
				bw.flush();
				bw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (conn.getResponseCode() == 200) {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						conn.getInputStream(), "utf-8"));
				StringBuffer sb = new StringBuffer();
				String temp = "";
				while((temp = br.readLine()) != null) {
					sb.append(temp);
				}
				br.close();
				JSONTokener json_get = new JSONTokener(sb.toString());
				getJSON = (JSONObject) json_get.nextValue();
				FanHuiCanShu.state_user_Add = getJSON.getString("返回信息");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 生成发送的JSON字符串
	 * 
	 * @param json
	 * @return
	 */
	private JSONObject initJOSNContent(JSONObject json) {
		try {
			json.put("Name", user_data.getName());
			json.put("Sex", user_data.getSex());
			json.put("Career", user_data.getCreer());
			json.put("PassWord", user_data.getPassword());
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
