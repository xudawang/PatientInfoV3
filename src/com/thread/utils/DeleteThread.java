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

public class DeleteThread implements Runnable{
	private String path;
	private BrInfo data;
	
	private JSONObject json_send, json_BrInfo;
	private JSONObject getJSON;
	
	public DeleteThread(String path, BrInfo data) {
		this.path = path;
		this.data = data;
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
				FanHuiCanShu.state_DELETE = getJSON.getString("删除标志");
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}


	//生成json格式数据
	private JSONObject initJSONContent(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			json.put("Id", data.getId());
			json.put("Name", data.getName());
			json.put("Age", data.getAge());
			json.put("Sex", data.getSex());
			json.put("Lxfs", data.getLxfs());
			json.put("Brzz", data.getBrzz());
			json.put("Yz", data.getYz());
			json.put("Zybch", data.getZybch());
			json.put("Hyzk", data.getHyzk());
			json.put("Yxck", data.getYxck());
			json.put("Tssm", data.getTssm());
			json.put("Shz", data.getShz());
			json.put("Shsj", data.getShtime());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}

}
