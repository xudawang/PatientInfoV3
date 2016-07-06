package com.example.patientinfov5;

import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts.Data;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.adapter.MyFragmentPagerAdapter;
import com.fragment.AddFragment;
import com.fragment.CSFragment;
import com.fragment.UpdateFragment;
import com.service.GetTuiService;
import com.utils.BrInfo;
import com.utils.GESHIHUA;
import com.utils.GETUIDatas;
import com.utils.GETUIInfo;
import com.utils.ImageTx;

public class DoctorJM extends FragmentActivity {
	private LinearLayout labelCS, labelAdd, labelUpdate;
	private ImageView imgSelect, imgAdd, imgUpdate;
	
	private ImageView imageBack;
	private TextView centerTitle;
	
	private Toolbar toolbar;

	private Context context = null;
	private ViewPager pager = null;
	private TextView title1, title2, title3;
	private ImageView cursor; // 导航底下动画图片

	private int offset = 0; // 动画图片偏移量
	private int bmpW; // 动画图片宽度
	private int currIndex = 0; // 当前页卡编号

	private ArrayList<Fragment> fragmentsList;

	private BrInfo p;

	private Fragment csFragment;

	private Intent intent;

	private NotifyReceiver notifyReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.doctor_main_new);

		ImageTx.img_man = BitmapFactory.decodeResource(getResources(),
				R.drawable.man);
		ImageTx.img_woman = BitmapFactory.decodeResource(getResources(),
				R.drawable.woman);
		ImageTx.img_default = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);

		initView();
		initImageView();
		initViewPager();
		getBundleData();

		intent = new Intent(getApplicationContext(), GetTuiService.class);
		startService(intent);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		notifyReceiver = new NotifyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(GESHIHUA.BROADCAST_SEND);
		registerReceiver(notifyReceiver, filter);

	}

	/**
	 * 自定义广播接收器
	 * 
	 * @author xudawang
	 * 
	 */
	public class NotifyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			String message = bundle.getString("新消息");

			Intent intent_notification = new Intent(getApplicationContext(),
					DoctorJM.class);
			if (message.equals("有病人信息发生变化")) {
				intent_notification.putExtra("病人信息", GETUIDatas.datas.get(0));
				intent_notification.putExtra("登录者类别", "医护人员");
				PendingIntent pendingIntent = PendingIntent.getActivity(
						getApplicationContext(), 0, intent_notification,
						PendingIntent.FLAG_CANCEL_CURRENT);
				Bitmap b = BitmapFactory.decodeResource(getResources(),
						R.drawable.ic_launcher);
				NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(
						getApplicationContext());
				nBuilder.setContentTitle("新消息")
						.setContentText(
								"病案中有：" + GETUIDatas.datas.size() + "条信息发生变化")
						.setTicker("通知！").setWhen(System.currentTimeMillis())
						.setPriority(Notification.PRIORITY_DEFAULT)
						.setOngoing(false)
						.setDefaults(Notification.DEFAULT_SOUND)
						.setSmallIcon(R.drawable.notifys).setLargeIcon(b)
						.setContentIntent(pendingIntent).setAutoCancel(true);

				mNotificationManager.notify(1, nBuilder.build());
				String[] time = new String[GETUIDatas.datas.size()];
				String time1, time2;
				/*
				 * for(int i=0; i<GETUIDatas.datas.size(); i++) { time[i] =
				 * GETUIDatas.datas.get(i).getShtime(); } for(int i=0;
				 * i<GETUIDatas.datas.size(); i++) { time1 = time[i]; time2 =
				 * time[i+1]; if(time1.compareTo(time2)>0) { time[i] = time2;
				 * time[i+1] = time1; } } GETUIInfo.time =
				 * time[GETUIDatas.datas.size()];
				 */

			}
		}

	}

	private void getBundleData() {
		Intent intent = getIntent();
		Bundle args_getInfo = new Bundle();
		args_getInfo = intent.getExtras();
		String career = args_getInfo.getString("职业");
		centerTitle.setText(career + "界面");
	}

	private void initViewPager() {
		pager = (ViewPager) findViewById(R.id.viewPagerId);
		fragmentsList = new ArrayList<Fragment>();
		csFragment = new CSFragment();
		Fragment xgFragment = new UpdateFragment();
		Fragment AddFragment = new AddFragment();

		fragmentsList.add(csFragment);
		fragmentsList.add(AddFragment);
		fragmentsList.add(xgFragment);

		pager.setAdapter(new MyFragmentPagerAdapter(
				getSupportFragmentManager(), fragmentsList));
		pager.setCurrentItem(0);
		pager.addOnPageChangeListener(new MyOnPageChangeListener());

	}

	private void initView() {
		title1 = (TextView) findViewById(R.id.title_csId);
		title2 = (TextView) findViewById(R.id.title_addId);
		title3 = (TextView) findViewById(R.id.title_updateId);

		labelCS = (LinearLayout) findViewById(R.id.label_csId);
		labelAdd = (LinearLayout) findViewById(R.id.label_addId);
		labelUpdate = (LinearLayout) findViewById(R.id.label_updateId);

		imgSelect = (ImageView) findViewById(R.id.img_select);
		imgAdd = (ImageView) findViewById(R.id.img_add);
		imgUpdate = (ImageView) findViewById(R.id.img_update);
		
		imageBack = (ImageView) findViewById(R.id.toolbar_back);
		centerTitle = (TextView) findViewById(R.id.text_view_center_title);

		labelCS.setOnClickListener(new MyOnClickListener(0));
		labelAdd.setOnClickListener(new MyOnClickListener(1));
		labelUpdate.setOnClickListener(new MyOnClickListener(2));
		imageBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DoctorJM.this.finish();
			}
		});

	}

	private void initImageView() {
		cursor = (ImageView) findViewById(R.id.navbarId);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.roller)
				.getWidth(); // 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		int screenW = dm.widthPixels; // 获取分辨率宽度
		offset = (screenW / 3 - bmpW) / 2; // 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix); // 设置导航底下初始位置
	}

	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			pager.setCurrentItem(index);
		}
	}

	public class MyOnPageChangeListener implements
			ViewPager.OnPageChangeListener {
		int one = offset * 2 + bmpW; // 页卡1-->页卡2偏移量
		int two = one * 2; // 页卡1-->页卡3偏移量

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {

		}

		@Override
		public void onPageSelected(int position) {
			Animation animation = null;
			switch (position) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				}
				imgSelect.setImageResource(R.mipmap.search_select);
				imgAdd.setImageResource(R.mipmap.add);
				imgUpdate.setImageResource(R.mipmap.update);
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
				}
				imgSelect.setImageResource(R.mipmap.search);
				imgAdd.setImageResource(R.mipmap.add_select);
				imgUpdate.setImageResource(R.mipmap.update);
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				}
				imgSelect.setImageResource(R.mipmap.search);
				imgAdd.setImageResource(R.mipmap.add);
				imgUpdate.setImageResource(R.mipmap.update_select);
				break;
			}
			currIndex = position;
			animation.setFillAfter(true); // 图片停在动画结束位置
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}

		@Override
		public void onPageScrollStateChanged(int state) {

		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stopService(intent);
		unregisterReceiver(notifyReceiver);
	}

}
