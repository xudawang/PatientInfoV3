package com.thread.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

/**
 * 尚未完成的图片回传下载
 * @author xudawang
 *
 */
public class ImageDownRoad implements Runnable{
	private String path;
	private URL url = null;
	private HttpURLConnection conn = null;
	
	public ImageDownRoad(String path) {
		this.path = path;
	}
	@Override
	public void run() {
		try {
			url = new URL(path);
			try {
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setConnectTimeout(5 * 1000);
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setUseCaches(false);

				conn.setRequestProperty("Content-Type",
						"multipart/form-data; boundary=------------------123456789");
				conn.setRequestProperty("Accept", "image/*, application/json");

				conn.connect();
				
				InputStream inputStream = conn.getInputStream();
				if(conn.getResponseCode() == 200) {
					int size = conn.getContentLength();
					byte[] buffer = new byte[size];
					byte[] result = new byte[size];
					
					int count = 0;
					int rbyte = 0;
					while(count < size) {
						rbyte = inputStream.read(buffer);
						for(int i=0; i<rbyte; i++) {
							result[count + i] = buffer[i];
						}
						count += rbyte;
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
	}

}
