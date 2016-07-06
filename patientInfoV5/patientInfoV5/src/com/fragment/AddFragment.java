package com.fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.example.patientinfov5.R;
import com.thread.utils.AddThread;
import com.thread.utils.SelectCountThread;
import com.utils.BrInfo;
import com.utils.FanHuiCanShu;
import com.utils.GESHIHUA;

public class AddFragment extends Fragment implements OnClickListener {
	private View view;
	private BrInfo data;
	private ImageView btn_add, btn_reset, btn_bddq;
	private TextView tv_fwz, tv_Shz, tv_Shtime;
	private EditText ed_Name, ed_Age, ed_Lxfs, ed_Brzz, ed_Yz, ed_Zybch,
			ed_Tssm;
	private RadioGroup rg_Sex;
	private RadioButton rb_Sex;
	private RadioGroup rg_Hyzk;
	private RadioButton rb_Hyzk;
	private RadioGroup rg_Yxck;
	private RadioButton rb_Yxck;
	
	private LinearLayout ll_left, ll_middle, ll_right;

	private boolean UPDATE = false;
	private String ShTime_before;
	private String ShTime_current;
	private String Shz;

	private String file_path;
	private String[] file_list;
	private String fileName;

	private AlertDialog dialog;
	private AlertDialog.Builder fileDialog;
	private ArrayAdapter<String> adapter;

	private Config config;

	private JSONObject getJSON;
	private String path;
	private String path_count;
	private int count;

	private Thread addThread;
	private Thread countThread;

	private Handler mHandler = new Handler();
	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			if (FanHuiCanShu.state_ADD.equals("添加成功")) {
				Toast.makeText(getActivity().getApplicationContext(), "添加成功！",
						1).show();
			} else {
				Toast.makeText(getActivity().getApplicationContext(), "添加失败！",
						1).show();
			}
		}
	};

	/**
	 * 预请求获取病人总数
	 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);

	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = initView(inflater);
		initAdapter();
		initListener();
		
		return view;
	}

	/**
	 * 初始化适配器
	 */
	private void initAdapter() {
		adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.item_file);
		
		
	}

	// 初始化控件
	private View initView(LayoutInflater inflater) {
		view = inflater.inflate(R.layout.fragment_add, null);
		btn_add = (ImageView) view.findViewById(R.id.btn_ok);
		btn_reset = (ImageView) view.findViewById(R.id.btn_reset);
		btn_bddq = (ImageView) view.findViewById(R.id.btn_beifenxx);
		
		ll_left = (LinearLayout) view.findViewById(R.id.ll_left);
		ll_middle = (LinearLayout) view.findViewById(R.id.ll_middle);
		ll_right = (LinearLayout) view.findViewById(R.id.ll_right);

		tv_fwz = (TextView) view.findViewById(R.id.tv_current_shz);
		tv_Shz = (TextView) view.findViewById(R.id.tv_patient_shz);

		Shz = ChanelActivity.login_Name;
		tv_fwz.setText("当前操作医生 ：" + Shz);

		tv_Shtime = (TextView) view.findViewById(R.id.tv_patient_shtime);

		ed_Name = (EditText) view.findViewById(R.id.ed_patient_name);
		ed_Age = (EditText) view.findViewById(R.id.ed_patient_age);
		ed_Lxfs = (EditText) view.findViewById(R.id.ed_patient_lxfs);
		ed_Brzz = (EditText) view.findViewById(R.id.ed_patient_brzz);
		ed_Yz = (EditText) view.findViewById(R.id.ed_patient_yz);
		ed_Zybch = (EditText) view.findViewById(R.id.ed_patient_zybch);
		ed_Tssm = (EditText) view.findViewById(R.id.ed_patient_tssm);

		rg_Sex = (RadioGroup) view.findViewById(R.id.rg_Sex);
		rg_Hyzk = (RadioGroup) view.findViewById(R.id.rg_Hyzk);
		rg_Yxck = (RadioGroup) view.findViewById(R.id.rg_Yxck);

		initRadioButton();

		return view;
	}

	/**
	 * 初始化点击事件监听
	 */
	private void initListener() {
		ll_left.setOnClickListener(this);
		ll_middle.setOnClickListener(this);
		ll_right.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_right:
			initRadioButton();
			path_count = "http://" + ChanelActivity.url_IP + ":"
					+ ChanelActivity.PORT
					+ "/HttpServer/servlet/HttpServiceSelectALL";

			path = "http://" + ChanelActivity.url_IP + ":"
					+ ChanelActivity.PORT
					+ "/HttpServer/servlet/HttpServiceAdd";
			// 查询总病人数
			countThread = new Thread(new SelectCountThread(path_count));
			countThread.start();
			Date date = new Date();
			SimpleDateFormat f = new SimpleDateFormat(GESHIHUA.TIME_GS);
			ShTime_current = f.format(date);

			try {
				countThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			data = new BrInfo();
			data = initData(data);
			// 添加数据
			addThread = new Thread(new AddThread(data, path));
			addThread.start();

			try {
				addThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			mHandler.postDelayed(mRunnable, 1 * 1000);

			break;
		case R.id.ll_middle:
			getExistFile();
			break;
		case R.id.ll_left:
			resetData();
			break;
		}
	}

	/**
	 * 获取指定文件夹下的文件名
	 */
	private void getExistFile() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			file_path = sdcardDir.getPath() + GESHIHUA.SD_XML_PATH_CK;
			File file1 = new File(file_path);
			if (!file1.exists()) {
				// 若不存在则创建目录
				file1.mkdir();
			}
			file_list = file1.list();
			
			adapter.clear();
			adapter.addAll(file_list);
			adapter.notifyDataSetChanged();
			
			fileDialog = new AlertDialog.Builder(getActivity()).setTitle("请选择一个文件")
					.setAdapter(adapter, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							fileName = adapter.getItem(which);
							config = new Config();
							data = config.parserXmlFromLocal(fileName);
							setlayoutData(data);
						}
					});
			
			dialog = fileDialog.show();

		}

	}
	
	/**
	 * 加载选择文件的数据
	 */
	private void setlayoutData(BrInfo data) {
		ed_Name.setText(data.getName());
		ed_Age.setText(data.getAge());
		ed_Lxfs.setText(data.getLxfs());
		ed_Brzz.setText(data.getBrzz());
		ed_Yz.setText(data.getYz());
		ed_Zybch.setText(data.getZybch());
		ed_Tssm.setText(data.getTssm());
		
		tv_Shz.setText(data.getShz());
		tv_Shtime.setText(data.getShtime());
		
		if(data.getSex().equals("男")) {
			rg_Sex.check(R.id.rb_man);
		}else if(data.getSex().equals("女")) {
			rg_Sex.check(R.id.rb_woman);
		}else {
			rg_Sex.check(R.id.rb_other);
		}
		
		if (data.getHyzk().equals("已婚")) {
			rg_Hyzk.check(R.id.rb_Yh);
		} else {
			rg_Hyzk.check(R.id.rb_Wh);
		}
		
		if (data.getYxck().equals("是")) {
			rg_Yxck.check(R.id.rb_Yes);
		} else {
			rg_Yxck.check(R.id.rb_No);
		}
		
	}

	/**
	 * 清空当前页面数据
	 */
	private void resetData() {
		// TODO Auto-generated method stub
		ed_Name.setText("");
		ed_Age.setText("");
		ed_Lxfs.setText("");
		ed_Brzz.setText("");
		ed_Yz.setText("");
		ed_Zybch.setText("");
		ed_Tssm.setText("");
	}

	/**
	 * 封装数据
	 */
	private BrInfo initData(BrInfo data) {
		count = FanHuiCanShu.count;
		int add_Id = 10000000 + count;
		String Id = String.valueOf(add_Id);
		data.setId(Id);
		if (ed_Name.getText().toString().length() == 0) {
			data.setName("null");
		} else {
			data.setName(ed_Name.getText().toString());
		}
		if (ed_Age.getText().toString().length() == 0) {
			data.setAge("null");
		} else {
			data.setAge(ed_Age.getText().toString());
		}
		if (rb_Sex.getText().toString().length() == 0) {
			data.setSex("null");
		} else {
			data.setSex(rb_Sex.getText().toString());
		}
		if (ed_Lxfs.getText().toString().length() == 0) {
			data.setLxfs("null");
		} else {
			data.setLxfs(ed_Lxfs.getText().toString());
		}
		if (ed_Brzz.getText().toString().length() == 0) {
			data.setBrzz("null");
		} else {
			data.setBrzz(ed_Brzz.getText().toString());
		}
		if (ed_Yz.getText().toString().length() == 0) {
			data.setYz("null");
		} else {
			data.setYz(ed_Yz.getText().toString());
		}
		if (ed_Zybch.getText().toString().length() == 0) {
			data.setZybch("null");
		} else {
			data.setZybch(ed_Zybch.getText().toString());
		}
		if (rb_Hyzk.getText().toString().length() == 0) {
			data.setHyzk("null");
		} else {
			data.setHyzk(rb_Hyzk.getText().toString());
		}
		if (rb_Yxck.getText().toString().length() == 0) {
			data.setYxck("null");
		} else {
			data.setYxck(rb_Yxck.getText().toString());
		}
		if (ed_Tssm.getText().toString().length() == 0) {
			data.setTssm("null");
		} else {
			data.setTssm(ed_Tssm.getText().toString());
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

		tv_Shz.setText(Shz);
		tv_Shtime.setText(ShTime_current);
		return data;
	}

	/**
	 * 获取RadioButton的值
	 */
	private void initRadioButton() {
		rb_Sex = (RadioButton) view.findViewById(rg_Sex
				.getCheckedRadioButtonId());
		rb_Hyzk = (RadioButton) view.findViewById(rg_Hyzk
				.getCheckedRadioButtonId());
		rb_Yxck = (RadioButton) view.findViewById(rg_Yxck
				.getCheckedRadioButtonId());
	}
}
