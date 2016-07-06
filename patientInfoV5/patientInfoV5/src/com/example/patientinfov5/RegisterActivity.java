package com.example.patientinfov5;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.thread.utils.AddUserThread;
import com.utils.FanHuiCanShu;
import com.utils.UserInfo;

public class RegisterActivity extends Activity implements OnClickListener {
	private EditText ed_Name, ed_Career, ed_Pass, ed_reputPass;
	private RadioGroup rg_Sex;
	private RadioButton rb_Sex;
	private Button btn_ok;
	private ImageView imageBack;
	private TextView centerTitle;

	private UserInfo user_data;
	private String path;
	private Thread addThread;

	private Animation anim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_page);

		initView();
	}

	/**
	 * 注册控件
	 */
	private void initView() {
		ed_Name = (EditText) findViewById(R.id.ed_register_Name);
		ed_Career = (EditText) findViewById(R.id.ed_register_Career);
		ed_Pass = (EditText) findViewById(R.id.ed_register_Pass);
		ed_reputPass = (EditText) findViewById(R.id.ed_reinput_Pass);
		rg_Sex = (RadioGroup) findViewById(R.id.rg_Sex);
		btn_ok = (Button) findViewById(R.id.btn_ok);
		
		imageBack = (ImageView) findViewById(R.id.toolbar_back);
		centerTitle = (TextView) findViewById(R.id.text_view_center_title);
		
		centerTitle.setText("注册界面");
		initRadioButton();
		initListener();
	}

	/**
	 * 注册监听
	 */
	private void initListener() {
		btn_ok.setOnClickListener(this);
		
		imageBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ok:
			btn_ok.setEnabled(false);
			initRadioButton();
			initData();
			btn_ok.setEnabled(true);
			break;
		case R.id.toolbar_back:
			RegisterActivity.this.finish();
			break;
		}

	}

	/**
	 * 初始化上传的数据
	 */
	private void initData() {
		String name = ed_Name.getText().toString();
		String career = ed_Career.getText().toString();
		String password = ed_Pass.getText().toString();
		String rePutPass = ed_reputPass.getText().toString();
		String sex = rb_Sex.getText().toString();
		errorAnim();

		for (int i = 0; i < 4; i++) {
			switch (i) {
			case 0:
				if (name.length() == 0) {
					ed_Name.setError("用户名不能为空！");
					ed_Name.startAnimation(anim);
				}
				break;
			case 1:
				if (career.length() == 0) {
					ed_Career.setError("职业不能为空！");
					ed_Career.startAnimation(anim);
				}
				break;
			case 2:
				if (password.length() == 0) {
					ed_Pass.setError("密码不能为空！");
					ed_Pass.startAnimation(anim);
				}
				break;
			case 3:
				if (rePutPass.length() == 0) {
					ed_reputPass.setError("验证密码不能为空！");
					ed_reputPass.startAnimation(anim);
				}
				break;
			}
		}

		if (name.length() != 0 && career.length() != 0
				&& password.length() != 0 && rePutPass.length() != 0) {
			if(password.equals(rePutPass)) {
				user_data = new UserInfo();
				user_data.setName(name);
				user_data.setCreer(career);
				user_data.setPassword(password);
				user_data.setSex(sex);
				
				path = "http://" + ChanelActivity.url_IP + ":" + ChanelActivity.PORT
						+ "/HttpServer/servlet/HttpDNRegisterServer";
				initThread(path, user_data);
			}else {
				ed_Pass.setError("两次密码输入有误！");
				ed_Pass.startAnimation(anim);
				ed_reputPass.setError("两次密码输入有误！");
				ed_reputPass.startAnimation(anim);
			}
		}

	}

	/**
	 * 开启注册操作线程
	 * @param path_add
	 * @param data
	 */
	private void initThread(String path_add, UserInfo data) {
		addThread = new Thread(new AddUserThread(path_add, data));
		addThread.start();
		try {
			addThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Toast.makeText(getApplicationContext(), FanHuiCanShu.state_user_Add, 1).show();
		
	}

	private void initRadioButton() {
		rb_Sex = (RadioButton) findViewById(rg_Sex.getCheckedRadioButtonId());
	}

	// EditText动画
	private void errorAnim() {
		// TODO Auto-generated method stub
		anim = new TranslateAnimation(0, 10, 0, 0); // 水平移动
		anim.setDuration(200);
		anim.setInterpolator(getApplicationContext(), R.anim.cycles7);
	}

}
