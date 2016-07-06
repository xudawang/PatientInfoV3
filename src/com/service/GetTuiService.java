package com.service;

import com.example.patientinfov5.ChanelActivity;
import com.thread.utils.GETUIThread;
import com.utils.GESHIHUA;
import com.utils.GETUIDatas;
import com.utils.GETUIInfo;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class GetTuiService extends Service{
	String path = "http://" + ChanelActivity.url_IP + ":"
			+ ChanelActivity.PORT
			+ "/HttpServer/servlet/HttpGeTuiService";
	
	private Thread GeTuiThread;
	private Handler mHandler = new Handler();
	private Handler bHandler = new Handler();
	private Runnable mRunnable = new Runnable() {
		
		@Override
		public void run() {
			GeTuiThread = new Thread(new GETUIThread(path, GETUIInfo.time));
			GeTuiThread.start();
			try {
				GeTuiThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mHandler.postDelayed(mRunnable, 5*1000);
		}
	};
	
	private Runnable bRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(GETUIDatas.datas.size() != 0) {
				Intent intent = new Intent();
				intent.putExtra("新消息", "有病人信息发生变化");
				intent.setAction(GESHIHUA.BROADCAST_SEND);
				sendBroadcast(intent);
			}
			bHandler.postDelayed(bRunnable, 5*1000);
		}
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		mHandler.post(mRunnable);
		bHandler.post(bRunnable);
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
