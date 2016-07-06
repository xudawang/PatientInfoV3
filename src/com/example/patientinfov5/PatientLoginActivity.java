package com.example.patientinfov5;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sqlite.DBCZ;
import com.thread.utils.MsgHandler;
import com.utils.BrInfo;
import com.utils.szIP;

public class PatientLoginActivity extends Activity {
	private Button btn_ok;
	private EditText ed_number;
	private JSONObject getJSON;
	private BrInfo data;
	private ArrayList<BrInfo> datas_click = new ArrayList<BrInfo>();

	private Thread selectThread;
	private DBCZ dbcz;
	private String p_url_IP;
	private String p_PORT = "8090";
	
	private Animation anim;
	
	private Handler handler;
	private Message msg;
	
	private Runnable btnOKdisplay = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			btn_ok.setEnabled(true);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.p_login);
		handler = new MsgHandler(PatientLoginActivity.this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initView();
		initListener();
		loadData();
	}

	private void loadData() {
		// TODO Auto-generated method stub
		dbcz = new DBCZ(this);
		for (szIP szip : dbcz.selectszIp()) {
			p_url_IP = szip.getIp();
		}
	}

	private void initListener() {
		// TODO Auto-generated method stub
		btn_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Pattern p = Pattern.compile("^[0-9]+$");
				Matcher m = p.matcher(ed_number.getText().toString());
				if(!m.matches()){
					ed_number.setError("��������ȷ�Ĳ����ţ�");
					errorAnim();
					ed_number.startAnimation(anim);
				}else {
					btn_ok.setEnabled(false);
					String path = "http://" + p_url_IP + ":"
							+ p_PORT
							+ "/HttpServer/servlet/HttpServiceSelectById";
					selectThread = new Thread(new patientHttpSelectThread(ed_number
							.getText().toString(), path));
					selectThread.start();
				}
								
			}
		});

	}

	private void initView() {
		btn_ok = (Button) findViewById(R.id.btn_login);
		ed_number = (EditText) findViewById(R.id.ed_number_login);
	}

	class patientHttpSelectThread implements Runnable {

		String id;
		String path_item_click;

		public patientHttpSelectThread(String id, String path_item_click) {
			this.id = id;
			this.path_item_click = path_item_click;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				URL url = new URL(path_item_click);
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
				json_send.put("ID��", id);
				try {
					BufferedWriter br = new BufferedWriter(
							new OutputStreamWriter(conn.getOutputStream(),
									"utf-8"));
					br.write(json_send.toString());
					br.flush();
					br.close();
				} catch (Exception e) {
					conn.disconnect();
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
					if(getJSON.getString("������Ϣ").equals("�޼�¼")) {
						initmsg(4);
					}else {
						initJSONData_Click();
						
						if (datas_click.get(0).getYxck().equals("��")) {
							Intent intent = new Intent(PatientLoginActivity.this,
									PLPatientInfo.class);
							intent.putExtra("������Ϣ", datas_click.get(0));
							startActivity(intent);
							initmsg(1);
						} else if(datas_click.get(0).getYxck().equals("��")){
							initmsg(5);
						}
					}
				}else {
					conn.disconnect();
					initmsg(3);
				}

			} catch (Exception e) {
				initmsg(3);
				e.printStackTrace();
			}finally {
				PatientLoginActivity.this.runOnUiThread(btnOKdisplay);
			}

		}
	}

	/**
	 * ��ȡ����Ĳ�����Ϣ����
	 */
	public void initJSONData_Click() {
		try {
			datas_click.clear();
			data = new BrInfo();
			data.setId(getJSON.getJSONObject("������Ϣ").getString("id"));
			data.setName(getJSON.getJSONObject("������Ϣ").getString("name"));
			data.setAge(getJSON.getJSONObject("������Ϣ").getString("age"));
			data.setSex(getJSON.getJSONObject("������Ϣ").getString("sex"));
			data.setLxfs(getJSON.getJSONObject("������Ϣ").getString("phone"));
			data.setBrzz(getJSON.getJSONObject("������Ϣ").getString("brzz"));
			data.setYz(getJSON.getJSONObject("������Ϣ").getString("yz"));
			data.setZybch(getJSON.getJSONObject("������Ϣ").getString("zybch"));
			data.setHyzk(getJSON.getJSONObject("������Ϣ").getString("hyzk"));
			data.setYxck(getJSON.getJSONObject("������Ϣ").getString("yxck"));
			data.setTssm(getJSON.getJSONObject("������Ϣ").getString("tssm"));
			data.setShz(getJSON.getJSONObject("������Ϣ").getString("shz"));
			data.setShtime(getJSON.getJSONObject("������Ϣ").getString("shtime"));

			datas_click.add(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initmsg(int i) {
		msg = handler.obtainMessage();
		msg.arg1 = i;
		handler.sendMessage(msg);
	}
	
	private void errorAnim() {
		anim = new TranslateAnimation(0, 10, 0, 0);
		anim.setDuration(200);
		anim.setInterpolator(getApplicationContext(), R.anim.cycles7);
	}

}
