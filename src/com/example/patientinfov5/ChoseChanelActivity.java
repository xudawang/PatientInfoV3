package com.example.patientinfov5;

import java.io.File;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.Button;

import com.utils.GESHIHUA;

public class ChoseChanelActivity extends Activity implements OnClickListener {
	private Button btn_left, btn_right;
	private boolean isUse = false;
	private Intent sIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chose_chanel);
		initView();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		animSetR();
		animSetL();
		isUse = isNetworkOpen();
		if (!isUse) {
			AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
			mBuilder.setCancelable(false);
			mBuilder.setTitle("提示信息！")
					.setMessage("当前网络不可用，请重新设置！")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									startActivity(sIntent);
								}
							});
			mBuilder.create().show();
		}
		
		//创建头像文件目录
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + GESHIHUA.IMAGE_DIR;
			File path_image = new File(path);
			if(!path_image.exists()) {
				path_image.mkdir();
			}
		}
	}

	/**
	 * 检测网络状态
	 */
	private boolean isNetworkOpen() {
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connManager.getActiveNetworkInfo() != null) {
			return connManager.getActiveNetworkInfo().isAvailable();
		} else {
			return false;
		}

	}

	private void initView() {
		btn_left = (Button) findViewById(R.id.doc_left);
		btn_right = (Button) findViewById(R.id.p_right);
		initListener();
	}

	private void initListener() {
		// TODO Auto-generated method stub
		btn_left.setOnClickListener(this);
		btn_right.setOnClickListener(this);

		btn_left.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					btn_left.setBackgroundResource(R.drawable.leftdown);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					btn_left.setBackgroundResource(R.drawable.left);
				}
				return false;
			}
		});

		btn_right.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					btn_right.setBackgroundResource(R.drawable.rightdown);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					btn_right.setBackgroundResource(R.drawable.right);
				}
				return false;
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.doc_left:
			Intent intent = new Intent(ChoseChanelActivity.this,
					ChanelActivity.class);
			startActivity(intent);
			break;
		case R.id.p_right:
			Intent intent_patient = new Intent(ChoseChanelActivity.this,
					PatientLoginActivity.class);
			startActivity(intent_patient);
			break;

		default:
			break;
		}
	}

	public void animSetR() {
		AnimatorSet aSet = new AnimatorSet();
		ValueAnimator a1 = ObjectAnimator.ofFloat(btn_right, "translationX", 0,
				100);
		a1.setRepeatCount(Animation.INFINITE);
		/* a1.setRepeatCount(Animation.REVERSE); */
		a1.setDuration(4 * 1000);
		PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", 1,
				0.5f);
		PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", 1,
				0.5f);
		ValueAnimator a2 = ObjectAnimator.ofPropertyValuesHolder(btn_right,
				pvhX, pvhY);
		a2.setDuration(4 * 1000);
		a2.setRepeatCount(Animation.INFINITE);
		/* a2.setRepeatMode(Animation.REVERSE); */

		aSet.play(a1).with(a2);
		aSet.start();
	}

	public void animSetL() {
		AnimatorSet aSet = new AnimatorSet();
		ValueAnimator a1 = ObjectAnimator.ofFloat(btn_left, "translationX", 0,
				-100);
		a1.setRepeatCount(Animation.INFINITE);
		/* a1.setRepeatMode(Animation.REVERSE); */
		a1.setDuration(4 * 1000);
		PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", 1,
				0.5f);
		PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", 1,
				0.5f);
		ValueAnimator a2 = ObjectAnimator.ofPropertyValuesHolder(btn_left,
				pvhX, pvhY);
		a2.setDuration(4 * 1000);
		a2.setRepeatCount(Animation.INFINITE);
		/* a2.setRepeatMode(Animation.REVERSE); */

		aSet.play(a1).with(a2);
		aSet.start();
	}
}
