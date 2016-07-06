package com.thread.utils;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class MsgHandler extends Handler {
	private Activity activity;

	public MsgHandler(Activity activity) {
		this.activity = new WeakReference<Activity>(activity).get();
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.arg1) {
		case 1:
			showInfo("登录成功");
			break;
		case 2:
			showInfo("用户名或密码错误");
			break;
		case 3:
			showInfo("连接出错，请重新配置参数");
			break;
		case 4:
			showInfo("病案号错误,无此病人记录");
			break;
		case 5:
			showInfo("该病人不允许查看");
			break;
		default:
			break;
		}

		super.handleMessage(msg);
	}

	/**
	 * 显示提示信息
	 * 
	 * @param info
	 */
	public void showInfo(String info) {
		Toast.makeText(activity.getApplicationContext(), info,
				Toast.LENGTH_SHORT).show();
	}

}
