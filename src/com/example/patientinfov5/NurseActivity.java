package com.example.patientinfov5;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class NurseActivity extends FragmentActivity{
	private FragmentManager fragmentManager;
	
	private ImageView imageBack;
	private TextView centerTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nurse);
		
		imageBack = (ImageView) findViewById(R.id.toolbar_back);
		centerTitle = (TextView) findViewById(R.id.text_view_center_title);
		
		centerTitle.setText("护士界面");
		getBundle();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initListener();
	}
	private void initListener() {
		// TODO Auto-generated method stub
		imageBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				NurseActivity.this.finish();
			}
		});
	}
	private void getBundle() {
		Intent intent = getIntent();
		Bundle args = intent.getExtras();
		setTitle(args.getString("职业"));
	}

}
