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
			if (FanHuiCanShu.state_TAG.equals("�޸ĳɹ�")) {
				initDataOfBrinfo(data);
				Toast.makeText(InfoToUpdate.this, "�޸ĳɹ�", 1).show();
				dialog.dismiss();
				initNotification();
			} else {
				Toast.makeText(InfoToUpdate.this, "�޸�ʧ��", 1).show();
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
		data = (BrInfo) intent.getSerializableExtra("������Ϣ");

		initDataOfBrinfo(data);
	}

	/**
	 * �ַ�������Ϣ����
	 */
	private void initDataOfBrinfo(BrInfo data) {
		if (!UPDATE) {
			Shz = ChanelActivity.login_Name.toString();
			tv_fwz.setText("��ǰ����ҽ����" + ChanelActivity.login_Name.toString());
			tv_Id.setText(data.getId());

			ed_Name.setText(data.getName());
			ed_Age.setText(data.getAge());
			ed_Lxfs.setText(data.getLxfs());
			ed_Brzz.setText(data.getBrzz());
			ed_Yz.setText(data.getYz());
			ed_Zybch.setText(data.getZybch());
			ed_Tssm.setText(data.getTssm());

			if (ChanelActivity.career.equals("��ʿ")) {
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

			if (data.getSex().equals("��")) {
				rg_Sex.check(R.id.rb_man);
				if (ChanelActivity.career.equals("��ʿ")) {
					findViewById(R.id.rb_woman).setVisibility(View.GONE);
					findViewById(R.id.rb_other).setVisibility(View.GONE);
				}
			} else if (data.getSex().equals("Ů")) {
				rg_Sex.check(R.id.rb_woman);
				if (ChanelActivity.career.equals("��ʿ")) {
					findViewById(R.id.rb_man).setVisibility(View.GONE);
					findViewById(R.id.rb_other).setVisibility(View.GONE);
				}
			} else {
				rg_Sex.check(R.id.rb_other);
				if (ChanelActivity.career.equals("��ʿ")) {
					findViewById(R.id.rb_woman).setVisibility(View.GONE);
					findViewById(R.id.rb_man).setVisibility(View.GONE);
				}
			}

			if (data.getHyzk().equals("�ѻ�")) {
				rg_Hyzk.check(R.id.rb_yh);
				if (ChanelActivity.career.equals("��ʿ")) {
					findViewById(R.id.rb_Wh).setVisibility(View.GONE);
				}
			} else {
				rg_Hyzk.check(R.id.rb_Wh);
				if (ChanelActivity.career.equals("��ʿ")) {
					findViewById(R.id.rb_yh).setVisibility(View.GONE);
				}
			}

			if (data.getYxck().equals("��")) {
				rg_Yxck.check(R.id.rb_Yes);
				if (ChanelActivity.career.equals("��ʿ")) {
					findViewById(R.id.rb_No).setVisibility(View.GONE);
				}
			} else {
				rg_Yxck.check(R.id.rb_No);
				if (ChanelActivity.career.equals("��ʿ")) {
					findViewById(R.id.rb_Yes).setVisibility(View.GONE);
				}
			}

		}
	}

	/**
	 * ע��ؼ�
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
		
		centerTitle.setText("�޸Ľ���");
		
		imageBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InfoToUpdate.this.finish();
			}
		});

		if (ChanelActivity.career.equals("��ʿ")) {
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
	 * ע��ѡ��ť
	 */
	private void initRadioButton() {
		// TODO Auto-generated method stub
		rb_Sex = (RadioButton) findViewById(rg_Sex.getCheckedRadioButtonId());
		rb_Hyzk = (RadioButton) findViewById(rg_Hyzk.getCheckedRadioButtonId());
		rb_Yxck = (RadioButton) findViewById(rg_Yxck.getCheckedRadioButtonId());
	}

	/**
	 * ע������¼�
	 */
	private void initListener() {
		ll_right.setOnClickListener(this);
		ll_left.setOnClickListener(this);
		ll_middle.setOnClickListener(this);
	}

	/**
	 * ����¼���Ӧ����
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
				qdDialog.setTitle("��ʾ��")
						.setMessage(
								"��Ҫ�޸�" + data.getName() + "����Ϣ!" + "\r\n"
										+ "ȷ����")
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
								// �������ر���
								config = new Config(data, Shz, ShTime_current);
								String msg = config.writeXmlToLocal();

								// �ϴ���������
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
				Toast.makeText(InfoToUpdate.this, "�ò�����Ϣ�ѷ����ı䣬�����»�ȡ", 5).show();
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
			Toast.makeText(InfoToUpdate.this, "��ȡ�ɹ���", 1).show();
			break;
		default:
			break;
		}

	}

	/**
	 * ����֪ͨ
	 */
	private void initNotification() {
		Bitmap b = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);

		/*Intent intent_notification = new Intent(getApplicationContext(), DoctorJM.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, intent_notification, 0);*/
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

		mBuilder.setContentTitle("�޸ĳɹ���")
				.setContentText(
						"������" + data.getName() + "�����ţ�" + data.getId()
								+ "����Ϣ�޸ĳɹ���").setTicker("�޸ĳɹ���")
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
				json_send.put("ID��", tv_Id.getText().toString());
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
	 * ��ȡ���صĲ�����Ϣ����
	 */
	public void initJSONData() {
		try {
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
