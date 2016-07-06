package com.fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.config.xml.Config;
import com.example.patientinfov5.ChanelActivity;
import com.example.patientinfov5.DialogActivity;
import com.example.patientinfov5.R;
import com.thread.utils.SelectThread;
import com.thread.utils.UpdateThread;
import com.utils.BrInfo;
import com.utils.FanHuiCanShu;
import com.utils.GESHIHUA;
import com.utils.SJJXUtils;

public class UpdateFragment extends Fragment implements OnClickListener {
	private BrInfo data;
	private View view;
	private Button btn_ok;
	private ImageView btn_reget, btn_beifenxx, btn_update;
	private EditText ed_select_Id, ed_patient_name, ed_patient_age,
			ed_patient_lxfs, ed_patient_brzz, ed_patient_yz, ed_patient_zybch, ed_patient_tssm;
	private RadioGroup rg_cs, rg_Sex, rg_Hyzk, rg_Yxck;
	private RadioButton rb_fs, rb_sex, rb_hyzk, rb_yxck;
	private TextView tv_patient_Id, tv_patient_shz, tv_patient_shtime;
	
	private LinearLayout ll_left, ll_middle, ll_right;

	private Thread selectThread;
	private Thread updateThread;

	private Animation anim;

	private String path;
	private Config config;

	private ArrayAdapter<String> adapter;
	private String[] file_list;
	private AlertDialog dialog;
	private AlertDialog.Builder file_Dialog;

	private String Shz;
	private String ShTime_current;
	private String shTime_before;

	private Handler mHandler = new Handler();
	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			if (FanHuiCanShu.state_TAG.equals("修改成功")) {
				Toast.makeText(getActivity().getApplicationContext(), "修改成功", 1)
						.show();
				dialog.dismiss();
				initNotification();
			} else {
				Toast.makeText(getActivity().getApplicationContext(), "修改失败", 1)
						.show();
				dialog.dismiss();
			}
		}

		
	};

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = initView(inflater);
		initListener();
		initAdapter();
		Shz = ChanelActivity.login_Name;
		return view;
	}

	/**
	 * 初始化适配器
	 */
	private void initAdapter() {
		adapter = new ArrayAdapter<String>(getActivity()
				.getApplicationContext(), R.layout.item_file);
	}

	private void initListener() {
		btn_ok.setOnClickListener(this);
		ll_left.setOnClickListener(this);
		ll_right.setOnClickListener(this);
		ll_middle.setOnClickListener(this);
	}

	/**
	 * 初始化试图
	 */
	private View initView(LayoutInflater inflater) {
		view = inflater.inflate(R.layout.fragment_update, null);

		btn_ok = (Button) view.findViewById(R.id.btn_ok);
		btn_reget = (ImageView) view.findViewById(R.id.btn_reget);
		btn_beifenxx = (ImageView) view.findViewById(R.id.btn_beifenxx);
		btn_update = (ImageView) view.findViewById(R.id.btn_update);
		
		ll_left = (LinearLayout) view.findViewById(R.id.ll_left);
		ll_middle = (LinearLayout) view.findViewById(R.id.ll_middle);
		ll_right = (LinearLayout) view.findViewById(R.id.ll_right);

		ed_select_Id = (EditText) view.findViewById(R.id.ed_select_Id);
		ed_patient_name = (EditText) view.findViewById(R.id.ed_patient_name);
		ed_patient_age = (EditText) view.findViewById(R.id.ed_patient_age);
		ed_patient_lxfs = (EditText) view.findViewById(R.id.ed_patient_lxfs);
		ed_patient_brzz = (EditText) view.findViewById(R.id.ed_patient_brzz);
		ed_patient_yz = (EditText) view.findViewById(R.id.ed_patient_yz);
		ed_patient_zybch = (EditText) view.findViewById(R.id.ed_patient_zybch);
		ed_patient_tssm =  (EditText) view.findViewById(R.id.ed_patient_tssm);

		rg_cs = (RadioGroup) view.findViewById(R.id.rg_cs);
		rg_Sex = (RadioGroup) view.findViewById(R.id.rg_Sex);
		rg_Hyzk = (RadioGroup) view.findViewById(R.id.rg_Hyzk);
		rg_Yxck = (RadioGroup) view.findViewById(R.id.rg_Yxck);

		tv_patient_Id = (TextView) view.findViewById(R.id.tv_patient_Id);
		tv_patient_shz = (TextView) view.findViewById(R.id.tv_patient_shz);
		tv_patient_shtime = (TextView) view
				.findViewById(R.id.tv_patient_shtime);

		initRadioButton();

		String Shz = ChanelActivity.login_Name;
		tv_patient_shz.setText("当前操作者:" + Shz);

		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ok:
			initRadioButton();
			initThread();
			break;
		case R.id.ll_left:
			initRegetThread(tv_patient_Id.getText().toString());
			break;
		case R.id.ll_middle:
			initReadXml();
			break;
		case R.id.ll_right:
			btn_update.setImageResource(R.mipmap.uploading);
			initRadioButton();
			initUpdateThread();
			break;
		}

	}

	/**
	 * 开启上传更新操作线程
	 */
	private void initUpdateThread() {
		data = new BrInfo();
		Date date = new Date();
		SimpleDateFormat f = new SimpleDateFormat(GESHIHUA.TIME_GS);
		ShTime_current = f.format(date);
		shTime_before = tv_patient_shtime.getText().toString();

		String path_sbu = "http://" + ChanelActivity.url_IP + ":"
				+ ChanelActivity.PORT
				+ "/HttpServer/servlet/HttpServiceSelectById";
		selectThread = new Thread(new SelectThread(path_sbu, "ID号",
				tv_patient_Id.getText().toString()));
		selectThread.start();
		try {
			selectThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (shTime_before.equals(SJJXUtils.data.getShtime())) {
			initUpdateData();
			AlertDialog.Builder qdDialog = new AlertDialog.Builder(
					getActivity());
			qdDialog.setTitle("提示：")
					.setMessage(
							"将要修改病案号为" + data.getId() + "\r\n" + "病人姓名为"
									+ data.getName() + "\r\n" + "病人的信息"
									+ "\r\n" + "确定吗？")
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
									// TODO Auto-generated method stub
									btn_update.setImageResource(R.mipmap.upload);
								}
							});

			dialog = qdDialog.create();
			dialog.show();
			dialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							path = "http://" + ChanelActivity.url_IP + ":"
									+ ChanelActivity.PORT
									+ "/HttpServer/servlet/HttpServletUpdate";

							// 开启本地备份
							config = new Config(data, Shz, ShTime_current);
							String msg = config.writeXmlToLocal();

							// 上传更新数据
							UpdateThread updateRun = new UpdateThread(data,
									path);
							updateThread = new Thread(updateRun);
							updateThread.start();
							try {
								updateThread.join();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							btn_update.setImageResource(R.mipmap.upload);
							mHandler.post(mRunnable);
						}
					});

		}else {
			btn_update.setImageResource(R.mipmap.upload);
			Toast.makeText(getActivity().getApplicationContext(), "当前病人信息已变化，请重新获取", 1).show();
		}

	}

	/**
	 * 加载本地备份信息
	 */
	private void initReadXml() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File sdcard = Environment.getExternalStorageDirectory();
			String file_path = sdcard.getPath() + GESHIHUA.SD_XML_PATH_CK;
			File f1 = new File(file_path);
			if (!f1.exists()) {
				f1.mkdir();
			}
			file_list = f1.list();
			adapter.clear();
			adapter.addAll(file_list);
			adapter.notifyDataSetChanged();

			file_Dialog = new AlertDialog.Builder(getActivity()).setTitle(
					"请选择一个文件").setAdapter(adapter,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							String fileName = adapter.getItem(which);
							config = new Config();
							data = config.parserXmlFromLocal(fileName);
							initData();
							Toast.makeText(
									getActivity().getApplicationContext(),
									"读取成功！", 1).show();
						}
					});
			dialog = file_Dialog.show();

		}
	}

	private void initRegetThread(String Id) {
		if (Id.length() != 0) {
			Intent intent = new Intent(getActivity().getApplicationContext(),
					DialogActivity.class);

			String select_cs = Id;
			String select_fs = "ID号";
			path = "http://" + ChanelActivity.url_IP + ":"
					+ ChanelActivity.PORT
					+ "/HttpServer/servlet/HttpServiceSelectById";
			intent.putExtra("select_cs", select_cs);
			intent.putExtra("select_fs", select_fs);
			intent.putExtra("path", path);

			startActivity(intent);

		} else {
			Toast.makeText(getActivity().getApplicationContext(), "请先查询病人信息", 1)
					.show();
		}

	}

	/**
	 * 开始查询操作
	 */
	private void initThread() {
		String select_cs = ed_select_Id.getText().toString().trim();
		String select_fs = rb_fs.getText().toString();
		if (select_cs.length() != 0) {
			if (select_fs.equals("ID号")) {
				Pattern p = Pattern.compile("^[0-9]+$");
				Matcher m = p.matcher(select_cs);
				if (!m.matches()) {
					ed_select_Id.setError("ID查询下参数必须为数字！");
					errorAnim();
					ed_select_Id.setAnimation(anim);
				} else {
					path = "http://" + ChanelActivity.url_IP + ":"
							+ ChanelActivity.PORT
							+ "/HttpServer/servlet/HttpServiceSelectById";
					selectThread = new Thread(new SelectThread(path, select_fs,
							select_cs));
					selectThread.start();
					try {
						selectThread.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					data = SJJXUtils.data;
					initData();
				}
			} else {
				Pattern p = Pattern.compile("^\\d+");
				Matcher m = p.matcher(select_cs);
				if (m.matches()) {
					ed_select_Id.setError("姓名查询下不允许有数字！");
					errorAnim();
					ed_select_Id.startAnimation(anim);
				} else {
					path = "http://" + ChanelActivity.url_IP + ":"
							+ ChanelActivity.PORT
							+ "/HttpServer/servlet/HttpServiceSelectByName";
					selectThread = new Thread(new SelectThread(path, select_fs,
							select_cs));
					selectThread.start();
					try {
						selectThread.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					data = SJJXUtils.data;
					initData();

				}
			}
		} else {
			ed_select_Id.setError("查询参数不能为空！");
			errorAnim();
			ed_select_Id.startAnimation(anim);
		}

	}

	/**
	 * 分发数据
	 */
	private void initData() {
		tv_patient_Id.setText(data.getId());
		tv_patient_shz.setText(data.getShz());
		tv_patient_shtime.setText(data.getShtime());

		ed_patient_name.setText(data.getName());
		ed_patient_age.setText(data.getAge());
		ed_patient_lxfs.setText(data.getLxfs());
		ed_patient_brzz.setText(data.getBrzz());
		ed_patient_yz.setText(data.getYz());
		ed_patient_zybch.setText(data.getZybch());
		ed_patient_tssm.setText(data.getTssm());

		// RadioButton部分设置
		if (data.getSex().equals("男")) {
			rg_Sex.check(R.id.rb_man);
		} else if (data.getSex().equals("女")) {
			rg_Sex.check(R.id.rb_woman);
		} else {
			rg_Sex.check(R.id.rb_other);
		}
		if (data.getHyzk().equals("已婚")) {
			rg_Hyzk.check(R.id.rb_yh);
		} else {
			rg_Hyzk.check(R.id.rb_Wh);
		}
		if (data.getYxck().equals("是")) {
			rg_Yxck.check(R.id.rb_Yes);
		} else {
			rg_Yxck.check(R.id.rb_No);
		}

	}

	private BrInfo initUpdateData() {
		data.setId(tv_patient_Id.getText().toString());
		if (ed_patient_name.getText().toString().length() == 0) {
			data.setName("null");
		} else {
			data.setName(ed_patient_name.getText().toString());
		}
		if (ed_patient_age.getText().toString().length() == 0) {
			data.setAge("null");
		} else {
			data.setAge(ed_patient_age.getText().toString());
		}
		if (rb_sex.getText().toString().length() == 0) {
			data.setSex("null");
		} else {
			data.setSex(rb_sex.getText().toString());
		}
		if (ed_patient_lxfs.getText().toString().length() == 0) {
			data.setLxfs("null");
		} else {
			data.setLxfs(ed_patient_lxfs.getText().toString());
		}
		if (ed_patient_brzz.getText().toString().length() == 0) {
			data.setBrzz("null");
		} else {
			data.setBrzz(ed_patient_brzz.getText().toString());
		}
		if (ed_patient_yz.getText().toString().length() == 0) {
			data.setYz("null");
		} else {
			data.setYz(ed_patient_yz.getText().toString());
		}
		if (ed_patient_zybch.getText().toString().length() == 0) {
			data.setZybch("null");
		} else {
			data.setZybch(ed_patient_zybch.getText().toString());
		}
		if (rb_hyzk.getText().toString().length() == 0) {
			data.setHyzk("null");
		} else {
			data.setHyzk(rb_hyzk.getText().toString());
		}
		if (rb_yxck.getText().toString().length() == 0) {
			data.setYxck("null");
		} else {
			data.setYxck(rb_yxck.getText().toString());
		}
		if (ed_patient_tssm.getText().toString().length() == 0) {
			data.setTssm("null");
		} else {
			data.setTssm(ed_patient_tssm.getText().toString());
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

	private void initRadioButton() {
		rb_fs = (RadioButton) view
				.findViewById(rg_cs.getCheckedRadioButtonId());
		rb_sex = (RadioButton) view.findViewById(rg_Sex
				.getCheckedRadioButtonId());
		rb_hyzk = (RadioButton) view.findViewById(rg_Hyzk
				.getCheckedRadioButtonId());
		rb_yxck = (RadioButton) view.findViewById(rg_Yxck
				.getCheckedRadioButtonId());
	}

	// EditText动画
	private void errorAnim() {
		// TODO Auto-generated method stub
		anim = new TranslateAnimation(0, 10, 0, 0); // 水平移动
		anim.setDuration(200);
		anim.setInterpolator(getActivity().getApplicationContext(),
				R.anim.cycles7);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		data = SJJXUtils.data;
		initData();
	}

	/**
	 * 通知消息
	 */
	private void initNotification() {
		Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity().getApplicationContext());

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
}
