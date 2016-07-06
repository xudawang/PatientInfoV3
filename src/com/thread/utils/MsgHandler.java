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
			showInfo("��¼�ɹ�");
			break;
		case 2:
			showInfo("�û������������");
			break;
		case 3:
			showInfo("���ӳ������������ò���");
			break;
		case 4:
			showInfo("�����Ŵ���,�޴˲��˼�¼");
			break;
		case 5:
			showInfo("�ò��˲�����鿴");
			break;
		default:
			break;
		}

		super.handleMessage(msg);
	}

	/**
	 * ��ʾ��ʾ��Ϣ
	 * 
	 * @param info
	 */
	public void showInfo(String info) {
		Toast.makeText(activity.getApplicationContext(), info,
				Toast.LENGTH_SHORT).show();
	}

}
