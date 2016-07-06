package com.example.patientinfov5;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.utils.BrInfo;

public class PLPatientInfo extends Activity {
	private TextView tv_display;
	private ImageView img_tx;
	
	private ImageView imageBack;
	private TextView centerTitle;
	
	private ArrayList<BrInfo> datas;
	private BrInfo data;
	
	private Dialog tDialog;
	private Bitmap img = null;
	
	/**
	 * 头像显示并关闭加载框
	 */
	private Runnable imageRunnable = new Runnable() {
		
		@Override
		public void run() {
			img_tx.setImageBitmap(img);
			tDialog.dismiss();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pl_patient_info);

		initView();
		initData();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		tDialog.show();
		super.onResume();
		Thread GetTxThread = new Thread(new GetTx());
		GetTxThread.start();
	}

	private void initData() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		data = (BrInfo) intent.getSerializableExtra("病人信息");
		tv_display.setText("ID号：" + data.getId() + "\r\n" + "病人姓名："
				+ data.getName() + "\r\n" + "病人性别：" + data.getSex() + "\r\n"
				+ "年龄：" + data.getAge() + "\r\n" + "医嘱：" + data.getYz()
				+ "\r\n" + "联系方式：" + data.getLxfs() + "\r\n" + "病人症状："
				+ data.getBrzz() + "\r\n" + "住院病床号:" + data.getZybch() + "\r\n"
				+ "婚姻状况：" + data.getHyzk() + "\r\n" + "是否允许查看："
				+ data.getYxck() + "\r\n" + "特殊说明：" + data.getTssm() + "\r\n"
				+ "审核医生：" + data.getShz() + "\r\n" + "审核时间：" + data.getShtime()
				+ "\r\n");

		if(img == null) {
			if (data.getSex().equals("男")) {
				img_tx.setImageResource(R.drawable.man);
			} else if (data.getSex().equals("女")) {
				img_tx.setImageResource(R.drawable.woman);
			} else {
				img_tx.setImageResource(R.drawable.ic_launcher);
			}
		}
	}

	private void initView() {
		// TODO Auto-generated method stub
		tDialog = new Dialog(this);
		tDialog.setContentView(R.layout.dialog_loading);
		img_tx = (ImageView) findViewById(R.id.img_tx);
		tv_display = (TextView) findViewById(R.id.tv_test);
		
		imageBack = (ImageView) findViewById(R.id.toolbar_back);
		centerTitle = (TextView) findViewById(R.id.text_view_center_title);
		
		centerTitle.setText("病人界面");
		
		imageBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PLPatientInfo.this.finish();
			}
		});
	}
	
	/**
	 * 获取头像
	 * @author xudawang
	 *
	 */
		class GetTx implements Runnable {

			@Override
			public void run() {
				String urlstring = "http://" + ChanelActivity.url_IP + ":"
						+ ChanelActivity.PORT + "/HttpServer/servlet/TestHttp";
				try {
					URL url = new URL(urlstring);

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

					conn.connect();

					JSONObject json_send = new JSONObject();
					json_send.put("头像名", data.getId());
					try {
						BufferedWriter bw = new BufferedWriter(
								new OutputStreamWriter(conn.getOutputStream(),
										"utf-8"));
						bw.write(json_send.toString());
						bw.flush();
						bw.close();
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (conn.getResponseCode() == 200) {
						InputStream is = conn.getInputStream();
						img = BitmapFactory.decodeStream(is);
						if (img != null) {
							PLPatientInfo.this.runOnUiThread(imageRunnable);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();

				}finally {
					tDialog.dismiss();
				}

			}
		}
}
