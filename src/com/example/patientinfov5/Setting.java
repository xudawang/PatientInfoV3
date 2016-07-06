package com.example.patientinfov5;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.adapter.LvDisplayAdapter;
import com.sqlite.DBCZ;
import com.utils.szIP;

public class Setting extends Activity implements OnClickListener {
	private DBCZ db_operate = null;
	private List<szIP> datas = null;
	private szIP data = new szIP();

	private Button btn_ok;
	private EditText ed_ip_input;
	private ListView lv_ip_display;
	private LvDisplayAdapter adapter;
	
	private ImageView imageBack;
	private TextView centerTitle;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);

		// 实例化数据库操作类
		db_operate = new DBCZ(this);
		data.setIp("0.0.0.0");
		datas = new ArrayList<szIP>();
		datas.add(data);
		initView();
		
		dbOperation_select();
	}

	/**
	 * 注册控件
	 */
	private void initView() {
		// TODO Auto-generated method stub
		ed_ip_input = (EditText) findViewById(R.id.ed_ip_input);
		btn_ok = (Button) findViewById(R.id.btn_input);
		lv_ip_display = (ListView) findViewById(R.id.lv_ip_display);
		
		imageBack = (ImageView) findViewById(R.id.toolbar_back);
		centerTitle = (TextView) findViewById(R.id.text_view_center_title);
		
		centerTitle.setText("配置界面");
		imageBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Setting.this.finish();
			}
		});
		
		adapter = new LvDisplayAdapter(getApplicationContext(), datas);
		btn_ok.setOnClickListener(this);
		lv_ip_display.setAdapter(adapter);
		
		lv_ip_display.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ed_ip_input.setText(datas.get(position).getIp());
				
			}
		});
	}

	/**
	 * 点击事件监听
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_input:
			String ip_input = ed_ip_input.getText().toString();
			dbOperation_add(ip_input);
			dbOperation_select();

			break;

		default:
			break;
		}
	}

	//查询SQLite中的ip信息
	private void dbOperation_select() {
		// TODO Auto-generated method stub
		datas.clear();
		for(szIP szip : db_operate.selectszIp()) {
			datas.add(szip);
		}
		adapter.notifyDataSetChanged();
	}

	// IP记录增加
	private void dbOperation_add(String ip_input) {
		data.setIp(ip_input);
		String message = db_operate.addszIP(data);
	}

}
