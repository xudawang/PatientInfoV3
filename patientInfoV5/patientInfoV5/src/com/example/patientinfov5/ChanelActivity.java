package com.example.patientinfov5;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sqlite.DBCZ;
import com.thread.utils.MsgHandler;
import com.utils.LoginInfo;
import com.utils.szIP;

public class ChanelActivity extends Activity implements OnClickListener {
	private EditText ed_password;
	private AutoCompleteTextView ed_name;
	private Button btn_login, btn_register;
	private ImageView img_login, img_statechange;
	private TextView tv_cancel, tv_setting;

	public static String login_Name;
	public static String career;
	private String save_Name;
	private String save_Password;

	private List<String> datas;
	private ArrayAdapter<String> adapter;

	private String TAG = "false";
	private JSONObject getJSON;

	public static String url_IP;
	public static final String PORT = "8090";
	private DBCZ dbcz;

	private Animation anim;

	private Thread loginThread;

	private LoginInfo loginInfo = new LoginInfo();
	
	private Handler handler;
	private Message msg;
	
	private Runnable btndisplay = new Runnable() {
		
		@Override
		public void run() {
			btn_login.setEnabled(true);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		if (savedInstanceState != null) {
			save_Name = savedInstanceState.getString("LoginName");
			save_Password = savedInstanceState.getString("Password");
		}
		handler = new MsgHandler(ChanelActivity.this);
		// 注册控件事件
		loadDatas();
		initView();
	}

	/**
	 * 注册控件
	 */
	private void initView() {
		ed_name = (AutoCompleteTextView) findViewById(R.id.ed_name_login);
		ed_password = (EditText) findViewById(R.id.ed_password_login);

		btn_login = (Button) findViewById(R.id.btn_login);
		btn_register = (Button) findViewById(R.id.btn_register);

		tv_cancel = (TextView) findViewById(R.id.tv_cancel_Id);
		tv_setting = (TextView) findViewById(R.id.tv_setting_Id);

		if (save_Name != null && save_Password != null) {
			ed_name.setText(save_Name);
			ed_password.setText(save_Password);
		}

		img_login = (ImageView) findViewById(R.id.img_startImg);
		img_statechange = (ImageView) findViewById(R.id.img_state_change);
		// 注册监听事件
		initListener();
	}

	/**
	 * 注册EditText焦点变换监听
	 */
	private void initListener() {
		btn_login.setOnClickListener(this);
		btn_register.setOnClickListener(this);

		img_statechange.setOnClickListener(this);
		ed_password.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					img_login.setImageResource(R.drawable.password2);
				} else {
					img_login.setImageResource(R.drawable.password1);
				}
			}
		});

		/**
		 * 退出当前页面
		 */
		tv_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ChanelActivity.this.finish();
			}
		});

		/**
		 * 跳转到设置界面
		 */
		tv_setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ChanelActivity.this, Setting.class);
				startActivity(intent);
			}
		});
	}

	/**
	 * 点击事件监听
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:
			btn_login.setEnabled(false);
			login_Name = ed_name.getText().toString().trim();
			Pattern p = Pattern.compile("^[0-9]+$");
			Matcher m = p.matcher(ed_password.getText().toString());
			if(login_Name.length() == 0) {
				ed_name.setError("必须输入用户名");
				errorAnim();
				ed_name.startAnimation(anim);
			}
			if (!m.matches()) {
				ed_password.setError("密码必须为数字");
				errorAnim();
				ed_password.startAnimation(anim);
				btn_login.setEnabled(true);
			}else if(login_Name!=null && m.matches()) {
				loginThread = new Thread(new MyThread());
				loginThread.start();

				initAddLoginInfo();
			}
			break;
		case R.id.img_state_change:
			if (TAG == "false") {
				ed_password
						.setTransformationMethod(HideReturnsTransformationMethod
								.getInstance());
				img_statechange.setImageResource(R.drawable.kaisuo);
				TAG = "true";
			} else {
				ed_password
						.setTransformationMethod(PasswordTransformationMethod
								.getInstance());
				img_statechange.setImageResource(R.drawable.jiasuo);
				TAG = "false";
			}
			break;
		case R.id.btn_register:
			Intent intent = new Intent(ChanelActivity.this,
					RegisterActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	/**
	 * 本地添加登录信息
	 */
	private void initAddLoginInfo() {
		dbcz = new DBCZ(this);
		if (ed_name.getText().toString().length() != 0) {
			loginInfo.setName(ed_name.getText().toString());
			dbcz.addUser(loginInfo);
		}
	}

	private void loadDatas() {
		datas = new ArrayList<String>();
		datas.clear();
		dbcz = new DBCZ(this);
		for (szIP szip : dbcz.selectszIp()) {
			url_IP = szip.getIp();
		}
		for (LoginInfo dblogin : dbcz.selectUserName()) {
			datas.add(dblogin.getName());
		}
	}

	// EditText动画
	private void errorAnim() {
		// TODO Auto-generated method stub
		anim = new TranslateAnimation(0, 10, 0, 0); // 水平移动
		anim.setDuration(200);
		anim.setInterpolator(getApplicationContext(), R.anim.cycles7);
	}

	class MyThread implements Runnable {
		public void run() {
			Log.i("info111", "this is Thread");
			HttpURLConnection conn;
			try {
				String path = "http://" + url_IP + ":" + PORT
						+ "/HttpServer/servlet/HttpDNLoginServer";
				URL url = new URL(path);
				
				conn = (HttpURLConnection) url
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
				JSONObject json_login = new JSONObject();
				json_login.put("Name", ed_name.getText().toString());
				json_login.put("Password", ed_password.getText().toString());
				json_send.put("登录信息", json_login);

				try {
					BufferedWriter br = new BufferedWriter(
							new OutputStreamWriter(conn.getOutputStream(),
									"utf-8"));
					br.write(json_send.toString());
					br.flush();
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
					initMessage(3);
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

					String zy = getJSON.getString("职业").toString();
					career = zy;

					try {
						if (getJSON.getString("返回信息").toString().equals("登录成功")) {
							if (zy.equals("医生") || zy.equals("管理员")) {
								Intent intent_doctor = new Intent(
										ChanelActivity.this, DoctorJM.class);
								Bundle args = new Bundle();
								args.putString("职业", zy);
								intent_doctor.putExtras(args);
								startActivity(intent_doctor);
							} else if (zy.equals("护士")) {
								Intent intent_nurse = new Intent(
										ChanelActivity.this,
										NurseActivity.class);
								Bundle args = new Bundle();
								args.putString("职业", zy);
								intent_nurse.putExtras(args);
								startActivity(intent_nurse);
							}
							initMessage(1);
						} else {
							conn.disconnect();
							initMessage(2);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						conn.disconnect();
						initMessage(3);
						e.printStackTrace();
					} finally {
						conn.disconnect();
					}
				}else {
					conn.disconnect();
					initMessage(3);
				}
			} catch (Exception e) {
				initMessage(3);
				e.printStackTrace();
			}finally {
				ChanelActivity.this.runOnUiThread(btndisplay);
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putString("LoginName", ed_name.getText().toString());
		outState.putString("Password", ed_password.getText().toString());
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.i("info111", "onPause");

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.i("info111", "onStop");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadDatas();
		Log.i("infp111", "onResume");
		adapter = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.item_login_name, datas);
		ed_name.setAdapter(adapter);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i("info111", "destroy");
	}
	
	private void initMessage(int i) {
		msg = handler.obtainMessage();
		msg.arg1 = i;
		handler.sendMessage(msg);
	}
}
