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
	 * ͷ����ʾ���رռ��ؿ�
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
		data = (BrInfo) intent.getSerializableExtra("������Ϣ");
		tv_display.setText("ID�ţ�" + data.getId() + "\r\n" + "����������"
				+ data.getName() + "\r\n" + "�����Ա�" + data.getSex() + "\r\n"
				+ "���䣺" + data.getAge() + "\r\n" + "ҽ����" + data.getYz()
				+ "\r\n" + "��ϵ��ʽ��" + data.getLxfs() + "\r\n" + "����֢״��"
				+ data.getBrzz() + "\r\n" + "סԺ������:" + data.getZybch() + "\r\n"
				+ "����״����" + data.getHyzk() + "\r\n" + "�Ƿ�����鿴��"
				+ data.getYxck() + "\r\n" + "����˵����" + data.getTssm() + "\r\n"
				+ "���ҽ����" + data.getShz() + "\r\n" + "���ʱ�䣺" + data.getShtime()
				+ "\r\n");

		if(img == null) {
			if (data.getSex().equals("��")) {
				img_tx.setImageResource(R.drawable.man);
			} else if (data.getSex().equals("Ů")) {
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
		
		centerTitle.setText("���˽���");
		
		imageBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PLPatientInfo.this.finish();
			}
		});
	}
	
	/**
	 * ��ȡͷ��
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
					json_send.put("ͷ����", data.getId());
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
