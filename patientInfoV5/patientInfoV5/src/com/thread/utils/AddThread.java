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

import com.utils.BrInfo;
import com.utils.FanHuiCanShu;

public class AddThread implements Runnable{
	private BrInfo add_data;
	private String path;
	
	private JSONObject json_send, json_BrInfo;
	private JSONObject getJSON;
	
	public AddThread(){}
	
	public AddThread(BrInfo add_data, String path) {
		this.add_data = add_data;
		this.path = path;
	}

	@Override
	public void run() {
		add_patientInfo();
	}


	private synchronized void add_patientInfo() {
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
			json_BrInfo = new JSONObject();
			json_send.put("病人信息", initJSONContent(json_BrInfo));
			
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
				String result = "";
				while ((temp = br.readLine()) != null) {
					sb.append(temp.toString());
				}
				br.close();
				result = sb.toString();
				JSONTokener json_result = new JSONTokener(result);
				getJSON = (JSONObject) json_result.nextValue();
				FanHuiCanShu.state_ADD = getJSON.getString("添加标志");
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}		
	}

	//生成json格式数据
	private JSONObject initJSONContent(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			json.put("Id", add_data.getId());
			json.put("Name", add_data.getName());
			json.put("Age", add_data.getAge());
			json.put("Sex", add_data.getSex());
			json.put("Lxfs", add_data.getLxfs());
			json.put("Brzz", add_data.getBrzz());
			json.put("Yz", add_data.getYz());
			json.put("Zybch", add_data.getZybch());
			json.put("Hyzk", add_data.getHyzk());
			json.put("Yxck", add_data.getYxck());
			json.put("Tssm", add_data.getTssm());
			json.put("Shz", add_data.getShz());
			json.put("Shsj", add_data.getShtime());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}

}
