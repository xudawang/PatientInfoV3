package com.example.patientinfov5;

import com.thread.utils.SelectThread;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class DialogActivity extends Activity {
	private Thread selectThread;
	private String select_cs, select_fs, path;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_loading);
		
		Intent intent = getIntent();
		select_cs = intent.getStringExtra("select_cs");
		select_fs = intent.getStringExtra("select_fs");
		path = intent.getStringExtra("path");
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				selectThread = new Thread(new SelectThread(path, select_fs, select_cs));
				selectThread.start();
				try {
					selectThread.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				DialogActivity.this.finish();
				Toast.makeText(getApplicationContext(), "º”‘ÿ≥…π¶", 1).show();
				
			}
		}, 3*1000);
		
	}

}
