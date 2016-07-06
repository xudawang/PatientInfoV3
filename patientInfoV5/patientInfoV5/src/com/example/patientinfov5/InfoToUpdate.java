package com.example.patientinfov5;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.config.xml.Config;
import com.thread.utils.UpdateThread;
import com.utils.BrInfo;
import com.utils.FanHuiCanShu;
import com.utils.GESHIHUA;

public class InfoToUpdate extends Activity implements OnClickListener {
	private BrInfo data;
	private ImageView btn_ok, btn_reget, btn_bddq;
	private TextView tv_fwz, tv_Id, tv_Shz, tv_Shtime;
	private EditText ed_Name, ed_Age, ed_Lxfs, ed_Brzz, ed_Yz, ed_Zybch,
			ed_Tssm;
	private RadioGroup rg_Sex;
	private RadioButton rb_Sex;
	private RadioGroup rg_Hyzk;
	private RadioButton rb_Hyzk;
	private RadioGroup rg_Yxck;
	private RadioButton rb_Yxck;
	
	private ImageView imageBack;
	private TextView centerTitle;
	
	private LinearLayout ll_left, ll_middle, ll_right;

	private boolean UPDATE = false;
	private String ShTime_before;
	private String ShTime_current;
	private String Shz;

	private JSONObject getJSON;
	private Thread yzThread;
	private Thread updateThread;

	private AlertDialog dialog;

	private Config config;

	private Handler mHandler = new Handler();
	private Handler bfHandler = new Handler();

	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			if (FanHuiCanShu.state_TAG.equals("修改成功")) {
				initDataOfBrinfo(data);
				Toast.makeText(InfoToUpdate.this, "修改成功", 1).show();
				dialog.dismiss();
				initNotification();
			} else {
				Toast.makeText(InfoToUpdate.this, "修改失败", 1).show();
				dialog.dismiss();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.patientinfo_to_update);

		initView();
		initDate();
		initListener();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void initDate() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		data = (BrInfo) intent.getSerializableExtra("病人信息");

		initDataOfBrinfo(data);
	}

	/**
	 * 分发病人信息数据
	 */
	private void initDataOfBrinfo(BrInfo data) {
		if (!UPDATE) {
			Shz = ChanelActivity.login_Name.toString();
			tv_fwz.setText("当前操作医生：" + ChanelActivity.login_Name.toString());
			tv_Id.setText(data.getId());

			ed_Name.setText(data.getName());
			ed_Age.setText(data.getAge());
			ed_Lxfs.setText(data.getLxfs());
			ed_Brzz.setText(data.getBrzz());
			ed_Yz.setText(data.getYz());
			ed_Zybch.setText(data.getZybch());
			ed_Tssm.setText(data.getTssm());

			if (ChanelActivity.career.equals("护士")) {
				ed_Name.setEnabled(false);
				ed_Age.setEnabled(false);
				ed_Lxfs.setEnabled(false);
				ed_Brzz.setEnabled(false);
				ed_Yz.setEnabled(false);
				ed_Zybch.setEnabled(false);
			}

			tv_Shz.setText(data.getShz());
			tv_Shtime.setText(data.getShtime());
			ShTime_before = tv_Shtime.getText().toString();

			if (data.getSex().equals("男")) {
				rg_Sex.check(R.id.rb_man);
				if (ChanelActivity.career.equals("护士")) {
					findViewById(R.id.rb_woman).setVisibility(View.GONE);
					findViewById(R.id.rb_other).setVisibility(View.GONE);
				}
			} else if (data.getSex().equals("女")) {
				rg_Sex.check(R.id.rb_woman);
				if (ChanelActivity.career.equals("护士")) {
					findViewById(R.id.rb_man).setVisibility(View.GONE);
					findViewById(R.id.rb_other).setVisibility(View.GONE);
				}
			} else {
				rg_Sex.check(R.id.rb_other);
				if (ChanelActivity.career.equals("护士")) {
					findViewById(R.id.rb_woman).setVisibility(View.GONE);
					findViewById(R.id.rb_man).setVisibility(View.GONE);
				}
			}

			if (data.getHyzk().equals("已婚")) {
				rg_Hyzk.check(R.id.rb_yh);
				if (ChanelActivity.career.equals("护士")) {
					findViewById(R.id.rb_Wh).setVisibility(View.GONE);
				}
			} else {
				rg_Hyzk.check(R.id.rb_Wh);
				if (ChanelActivity.career.equals("护士")) {
					findViewById(R.id.rb_yh).setVisibility(View.GONE);
				}
			}

			if (data.getYxck().equals("是")) {
				rg_Yxck.check(R.id.rb_Yes);
				if (ChanelActivity.career.equals("护士")) {
					findViewById(R.id.rb_No).setVisibility(View.GONE);
				}
			} else {
				rg_Yxck.check(R.id.rb_No);
				if (ChanelActivity.career.equals("护士")) {
					findViewById(R.id.rb_Yes).setVisibility(View.GONE);
				}
			}

		}
	}

	/**
	 * 注册控件
	 */
	private void initView() {
		// TODO Auto-generated method stub
		tv_fwz = (TextView) findViewById(R.id.tv_current_shz);
		tv_Id = (TextView) findViewById(R.id.tv_patient_Id);
		tv_Shz = (TextView) findViewById(R.id.tv_patient_shz);
		tv_Shtime = (TextView) findViewById(R.id.tv_patient_shtime);
		
		ll_left = (LinearLayout) findViewById(R.id.ll_left);
		ll_middle = (LinearLayout) findViewById(R.id.ll_middle);
		ll_right = (LinearLayout) findViewById(R.id.ll_right);
		
		imageBack = (ImageView) findViewById(R.id.toolbar_back);
		centerTitle = (TextView) findViewById(R.id.text_view_center_title);

		btn_ok = (ImageView) findViewById(R.id.btn_ok);
		btn_bddq = (ImageView) findViewById(R.id.btn_beifenxx);
		btn_reget = (ImageView) findViewById(R.id.btn_reget);
		ll_left.setEnabled(false);
		
		centerTitle.setText("修改界面");
		
		imageBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InfoToUpdate.this.finish();
			}
		});

		if (ChanelActivity.career.equals("护士")) {
			ll_middle.setVisibility(View.GONE);
		}

		ed_Name = (EditText) findViewById(R.id.ed_patient_name);
		ed_Age = (EditText) findViewById(R.id.ed_patient_age);
		ed_Lxfs = (EditText) findViewById(R.id.ed_patient_lxfs);
		ed_Brzz = (EditText) findViewById(R.id.ed_patient_brzz);
		ed_Yz = (EditText) findViewById(R.id.ed_patient_yz);
		ed_Zybch = (EditText) findViewById(R.id.ed_patient_zybch);
		ed_Tssm = (EditText) findViewById(R.id.ed_patient_tssm);

		rg_Sex = (RadioGroup) findViewById(R.id.rg_Sex);
		rg_Hyzk = (RadioGroup) findViewById(R.id.rg_Hyzk);
		rg_Yxck = (RadioGroup) findViewById(R.id.rg_Yxck);

		initRadioButton();
	}

	/**
	 * 注册选择按钮
	 */
	private void initRadioButton() {
		// TODO Auto-generated method stub
		rb_Sex = (RadioButton) findViewById(rg_Sex.getCheckedRadioButtonId());
		rb_Hyzk = (RadioButton) findViewById(rg_Hyzk.getCheckedRadioButtonId());
		rb_Yxck = (RadioButton) findViewById(rg_Yxck.getCheckedRadioButtonId());
	}

	/**
	 * 注册监听事件
	 */
	private void initListener() {
		ll_right.setOnClickListener(this);
		ll_left.setOnClickListener(this);
		ll_middle.setOnClickListener(this);
	}

	/**
	 * 点击事件响应方法
	 * 
	 * @param v
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_right:
			btn_ok.setImageResource(R.mipmap.uploading);
			initRadioButton();
			Date date = new Date();
			yzThread = new Thread(new SelectThread());
			yzThread.start();
			SimpleDateFormat f = new SimpleDateFormat(GESHIHUA.TIME_GS);
			ShTime_current = f.format(date);
			if (data.getShtime().toString().equals(ShTime_before)) {
				AlertDialog.Builder qdDialog = new AlertDialog.Builder(this);
				qdDialog.setCancelable(false);
				qdDialog.setTitle("提示：")
						.setMessage(
								"将要修改" + data.getName() + "的信息!" + "\r\n"
										+ "确定吗？")
						.setPositiveButton(R.string.ad_ok,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								})
						.setNegativeButton(R.string.ad_cancel,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										btn_ok.setImageResource(R.mipmap.upload);
									}
								});
				dialog = qdDialog.create();
				dialog.show();

				dialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								String path = "http://"
										+ ChanelActivity.url_IP
										+ ":"
										+ ChanelActivity.PORT
										+ "/HttpServer/servlet/HttpServletUpdate";

								data = initUpdateData();
								// 开启本地备份
								config = new Config(data, Shz, ShTime_current);
								String msg = config.writeXmlToLocal();

								// 上传更新数据
								UpdateThread updateRun = new UpdateThread(data,
										path);
								updateThread = new Thread(updateRun);
								updateThread.start();

								btn_ok.setImageResource(R.mipmap.upload);
								mHandler.postDelayed(mRunnable, 1 * 1000);
							}
						});

				

			} else {
				btn_ok.setImageResource(R.mipmap.upload);
				Toast.makeText(InfoToUpdate.this, "该病人信息已发生改变，请重新获取", 5).show();
				btn_reget.setImageResource(R.mipmap.reget);
				ll_left.setEnabled(true);
			}

			break;
		case R.id.ll_left:
			btn_reget.setImageResource(R.mipmap.reget_unselect);
			initDataOfBrinfo(data);
			ll_left.setEnabled(false);
			break;
		case R.id.ll_middle:
			config = new Config(data.getId());
			data = config.parserXmlFromLocal();
			initDataOfBrinfo(data);
			Toast.makeText(InfoToUpdate.this, "读取成功！", 1).show();
			break;
		default:
			break;
		}

	}

	/**
	 * 产生通知
	 */
	private void initNotification() {
		Bitmap b = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);

		/*Intent intent_notification = new Intent(getApplicationContext(), DoctorJM.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, intent_notification, 0);*/
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

		mBuilder.setContentTitle("修改成功！")
				.setContentText(
						"姓名：" + data.getName() + "病案号：" + data.getId()
								+ "的信息修改成功！").setTicker("修改成功啦")
				.setWhen(System.currentTimeMillis())
				.setPriority(Notification.PRIORITY_DEFAULT).setOngoing(false)
				.setDefaults(Notification.DEFAULT_SOUND)
				.setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
				.setLargeIcon(b)
				.setContentIntent(null);
		
		mNotificationManager.notify(1, mBuilder.build());

	}

	class SelectThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				URL url = new URL("http://" + ChanelActivity.url_IP + ":"
						+ ChanelActivity.PORT
						+ "/HttpServer/servlet/HttpServiceSelectById");
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
				json_send.put("ID号", tv_Id.getText().toString());
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
					initJSONData();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 获取返回的病人信息数据
	 */
	public void initJSONData() {
		try {
			data.setId(getJSON.getJSONObject("病人信息").getString("id"));
			data.setName(getJSON.getJSONObject("病人信息").getString("name"));
			data.setAge(getJSON.getJSONObject("病人信息").getString("age"));
			data.setSex(getJSON.getJSONObject("病人信息").getString("sex"));
			data.setLxfs(getJSON.getJSONObject("病人信息").getString("phone"));
			data.setBrzz(getJSON.getJSONObject("病人信息").getString("brzz"));
			data.setYz(getJSON.getJSONObject("病人信息").getString("yz"));
			data.setZybch(getJSON.getJSONObject("病人信息").getString("zybch"));
			data.setHyzk(getJSON.getJSONObject("病人信息").getString("hyzk"));
			data.setYxck(getJSON.getJSONObject("病人信息").getString("yxck"));
			data.setTssm(getJSON.getJSONObject("病人信息").getString("tssm"));
			data.setShz(getJSON.getJSONObject("病人信息").getString("shz"));
			data.setShtime(getJSON.getJSONObject("病人信息").getString("shtime"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private BrInfo initUpdateData() {
		data.setId(tv_Id.getText().toString());
		if (ed_Name.getText().toString().length() == 0) {
			data.setName("null");
		} else {
			data.setName(ed_Name.getText().toString());
		}
		if (ed_Age.getText().toString().length() == 0) {
			data.setAge("null");
		} else {
			data.setAge(ed_Age.getText().toString());
		}
		if (rb_Sex.getText().toString().length() == 0) {
			data.setSex("null");
		} else {
			data.setSex(rb_Sex.getText().toString());
		}
		if (ed_Lxfs.getText().toString().length() == 0) {
			data.setLxfs("null");
		} else {
			data.setLxfs(ed_Lxfs.getText().toString());
		}
		if (ed_Brzz.getText().toString().length() == 0) {
			data.setBrzz("null");
		} else {
			data.setBrzz(ed_Brzz.getText().toString());
		}
		if (ed_Yz.getText().toString().length() == 0) {
			data.setYz("null");
		} else {
			data.setYz(ed_Yz.getText().toString());
		}
		if (ed_Zybch.getText().toString().length() == 0) {
			data.setZybch("null");
		} else {
			data.setZybch(ed_Zybch.getText().toString());
		}
		if (rb_Hyzk.getText().toString().length() == 0) {
			data.setHyzk("null");
		} else {
			data.setHyzk(rb_Hyzk.getText().toString());
		}
		if (rb_Yxck.getText().toString().length() == 0) {
			data.setYxck("null");
		} else {
			data.setYxck(rb_Yxck.getText().toString());
		}
		if (ed_Tssm.getText().toString().length() == 0) {
			data.setTssm("null");
		} else {
			data.setTssm(ed_Tssm.getText().toString());
		}
		if (Shz.length() == 0) {
			data.setShz("null");
		} else {
			data.setShz(Shz);
		}
		if (ShTime_current.length() == 0) {
			data.setShtime("0000-00-00 00:00:00");
		} else {
			data.setShtime(ShTime_current);
		}
		return data;
	}

}
