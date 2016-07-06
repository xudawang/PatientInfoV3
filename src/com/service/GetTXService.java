package com.service;

import com.example.patientinfov5.ChanelActivity;
import com.thread.utils.GETUIThread;
import com.thread.utils.GetTxImage;
import com.utils.FanHuiCanShu;
import com.utils.GESHIHUA;
import com.utils.GETUIDatas;
import com.utils.GETUIInfo;
import com.utils.ImageTx;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class GetTXService extends Service {
	String path = "http://" + ChanelActivity.url_IP + ":"
			+ ChanelActivity.PORT
			+ "/HttpServer/servlet/HttpGeTuiService";
	
	private Thread txThread;
	private Handler mHandler = new Handler();
	private Runnable mRunnable = new Runnable() {
		
		@Override
		public void run() {
			txThread = new Thread(new GetTxImage(FanHuiCanShu.count));
			txThread.start();
			try {
				txThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mHandler.post(mRunnable);
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
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
