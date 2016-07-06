package com.fragment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.adapter.FragLvAdapter;
import com.example.patientinfov5.ChanelActivity;
import com.example.patientinfov5.PatientInfo;
import com.example.patientinfov5.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.service.GetTXService;
import com.service.GetTuiService;
import com.thread.utils.GetTxImage;
import com.utils.BrInfo;
import com.utils.FanHuiCanShu;
import com.utils.GESHIHUA;
import com.utils.GETUIInfo;
import com.utils.ImageTx;

public class CSFragment extends Fragment implements OnClickListener {
	private ListView lv_select;
	private PullToRefreshListView pullToRefreshListView;
	private EditText ed_cx_input;
	private RadioGroup rg_cxtj;
	private RadioButton rb_cxfs;
	private Button btn_ok;

	private Context context;
	private FragLvAdapter adapter;
	private LinkedList<BrInfo> datas = new LinkedList<BrInfo>();
	private LinkedList<BrInfo> datas_page = new LinkedList<BrInfo>();
	private LinkedList<BrInfo> datas_click = new LinkedList<BrInfo>();
	private BrInfo data;

	private Thread cxThread;
	private Thread cxAllThread;
	private Thread itemClickThread;

	private String select_fs;
	private String select_cs;
	private String path;
	private JSONObject getJSON;
	private View view;

	private ArrayList<Bitmap> images = new ArrayList<Bitmap>();
	private Bitmap img_man, img_woman, img_default;

	private boolean TAG = false;
	private boolean buttonOnClickTAG = false;

	private Dialog tDialog;

	private Intent intent;

	private Handler mHandler = new Handler();

	private Runnable loadingRunnable = new Runnable() {

		@Override
		public void run() {
			tDialog.dismiss();
			adapter.notifyDataSetChanged();
			pullToRefreshListView.onRefreshComplete();
			if (TAG == false) {
				Toast.makeText(getActivity(), "加载出错！", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(getActivity(), "加载成功！", Toast.LENGTH_SHORT)
						.show();
			}
		}
	};

	/**
	 * 在创建Fragment中初始化默认头像
	 */
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		intent = new Intent(getActivity(), GetTXService.class);

		img_man = BitmapFactory.decodeResource(getActivity().getResources(),
				R.drawable.man);
		img_woman = BitmapFactory.decodeResource(getActivity().getResources(),
				R.drawable.woman);
		img_default = BitmapFactory.decodeResource(
				getActivity().getResources(), R.drawable.ic_launcher);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("info222", "onResume");
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		Log.i("info222", "onCreateView");
		view = initView(inflater);
		return view;
	}

	/*
	 * @Override public void setUserVisibleHint(boolean isVisibleToUser) { //
	 * TODO Auto-generated method stub
	 * super.setUserVisibleHint(isVisibleToUser); Log.i("info222",
	 * "setUserVisible"); mHandler.post(new Runnable() {
	 * 
	 * @Override public void run() { // TODO Auto-generated method stub
	 * initDatas(); } }); }
	 */
	private View initView(LayoutInflater inflater) {
		Log.i("infoV", "initView");

		tDialog = new Dialog(getActivity());
		tDialog.setContentView(R.layout.dialog_loading);

		view = inflater.inflate(R.layout.fragment_item_cs, null);
		pullToRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.lv_contentId);
		ed_cx_input = (EditText) view.findViewById(R.id.ed_select_Id);
		rg_cxtj = (RadioGroup) view.findViewById(R.id.rg_cs);
		btn_ok = (Button) view.findViewById(R.id.btn_ok);

		initPullToRefresh(); // 注册刷新组件
		initListener(); // 注册监听

		return view;
	}

	// 初始化刷新事件
	private void initPullToRefresh() {
		initDatas();
		initAdapter();
		lv_select = pullToRefreshListView.getRefreshableView();
		lv_select.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		pullToRefreshListView.setMode(Mode.BOTH);

		// 设置头布局样式
		pullToRefreshListView.getLoadingLayoutProxy(true, false).setPullLabel(
				"拉我拉我拉我");
		pullToRefreshListView.getLoadingLayoutProxy(true, false)
				.setReleaseLabel("够啦！放开我！");
		pullToRefreshListView.getLoadingLayoutProxy(true, false)
				.setRefreshingLabel("正在拼命加载！");

		// 设置尾布局样式
		pullToRefreshListView.getLoadingLayoutProxy(false, true).setPullLabel(
				"向上拖我！");
		pullToRefreshListView.getLoadingLayoutProxy(false, true)
				.setReleaseLabel("放开我放开我");
		pullToRefreshListView.getLoadingLayoutProxy(false, true)
				.setRefreshingLabel("拼命加载中...");

		final Thread sxThread = new Thread(new HttpSelectAllThread());

		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						if (refreshView.isHeaderShown()) {
							if(ed_cx_input.getText().toString().length() == 0) {
								buttonOnClickTAG = false;
							}
							if (buttonOnClickTAG == true) {
								tDialog.show();
								Thread singleThread = new Thread(
										new HttpSelectThread());
								singleThread.start();
							} else {
								tDialog.show();
								path = "http://" + ChanelActivity.url_IP + ":" + ChanelActivity.PORT
										+ "/HttpServer/servlet/HttpServiceSelectALL";
								Thread slThread = new Thread(
										new HttpSelectAllThread());
								slThread.start();
								try {
									slThread.join();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						} else {
							if(ed_cx_input.getText().toString().length() == 0) {
								buttonOnClickTAG = false;
							}
							if (buttonOnClickTAG == true) {
								tDialog.show();
								Thread singleThread = new Thread(
										new HttpSelectThread());
								singleThread.start();
							} else {
								tDialog.show();
								path = "http://" + ChanelActivity.url_IP + ":" + ChanelActivity.PORT
										+ "/HttpServer/servlet/HttpServiceSelectALL";
								Thread xlThread = new Thread(
										new HttpSelectAllThread());
								xlThread.start();
								try {
									xlThread.join();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}
				});

	}

	public void initDatas() {

		tDialog.show();
		datas.clear();
		path = "http://" + ChanelActivity.url_IP + ":" + ChanelActivity.PORT
				+ "/HttpServer/servlet/HttpServiceSelectALL";
		cxAllThread = new Thread(new HttpSelectAllThread());
		cxAllThread.start();
		try {
			cxAllThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 初始化适配器
	 */
	private void initAdapter() {
		adapter = new FragLvAdapter(getActivity(), datas, images);
		/* adapter.notifyDataSetChanged(); */
	}

	/**
	 * 注册监听
	 */
	private void initListener() {
		// TODO Auto-generated method stub
		btn_ok.setOnClickListener(this);
		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String path_click = "http://" + ChanelActivity.url_IP + ":"
						+ ChanelActivity.PORT
						+ "/HttpServer/servlet/HttpServiceSelectById";
				itemClickThread = new Thread(new ItemHttpSelectThread(datas
						.get(position - 1).getId(), path_click));
				itemClickThread.start();
				try {
					itemClickThread.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_ok:
			buttonOnClickTAG = true;
			rb_cxfs = (RadioButton) view.findViewById(rg_cxtj
					.getCheckedRadioButtonId());
			select_fs = rb_cxfs.getText().toString();
			select_cs = ed_cx_input.getText().toString();
			if (select_fs.equals("ID号")) {
				path = "http://" + ChanelActivity.url_IP + ":"
						+ ChanelActivity.PORT
						+ "/HttpServer/servlet/HttpServiceSelectById";
				cxThread = new Thread(new HttpSelectThread());
				cxThread.start();
			} else if (select_fs.equals("姓名")) {
				path = "http://" + ChanelActivity.url_IP + ":"
						+ ChanelActivity.PORT
						+ "/HttpServer/servlet/HttpServiceSelectByName";
				cxThread = new Thread(new HttpSelectThread());
				cxThread.start();
			}

			break;

		default:
			break;
		}
	}

	/**
	 * 查询单条记录
	 * @author xudawang
	 *
	 */
	class HttpSelectThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				URL url = new URL(path);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setRequestMethod("POST");
				conn.setConnectTimeout(5 * 1000);
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setUseCaches(false);
				conn.setRequestProperty("Content-Type",
						"application/json; charset=utf-8");
				conn.setRequestProperty("Accept", "application/json");

				JSONObject json_send = new JSONObject();
				json_send.put(select_fs, select_cs);
				try {
					BufferedWriter br = new BufferedWriter(
							new OutputStreamWriter(conn.getOutputStream(),
									"utf-8"));
					br.write(json_send.toString());
					br.flush();
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (conn.getResponseCode() == 200) {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(conn.getInputStream(),
									"utf-8"));
					StringBuffer sb = new StringBuffer();
					String temp = "";
					while ((temp = br.readLine()) != null) {
						sb.append(temp);
					}
					br.close();
					JSONTokener json = new JSONTokener(sb.toString());
					getJSON = (JSONObject) json.nextValue();
					initJSONData();
					initImages();
				}
				TAG = true;
				getActivity().runOnUiThread(loadingRunnable);

			} catch (Exception e) {
				e.printStackTrace();
				TAG = false;
				getActivity().runOnUiThread(loadingRunnable);
			}

		}

	}

	/**
	 * 查询全部信息
	 * 
	 * @author xudawang
	 * 
	 */
	class HttpSelectAllThread implements Runnable {

		@Override
		public void run() {
			try {
				URL url = new URL(path);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setRequestMethod("POST");
				conn.setConnectTimeout(5 * 1000);
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setUseCaches(false);
				conn.setRequestProperty("Content-Type",
						"application/json; charset=utf-8");
				conn.setRequestProperty("Accept", "application/json");

				JSONObject json_send = new JSONObject();
				json_send.put("SELECTALL", "SELECTALL");
				try {
					BufferedWriter br = new BufferedWriter(
							new OutputStreamWriter(conn.getOutputStream(),
									"utf-8"));
					br.write(json_send.toString());
					br.flush();
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (conn.getResponseCode() == 200) {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(conn.getInputStream(),
									"utf-8"));
					StringBuffer sb = new StringBuffer();
					String temp = "";
					while ((temp = br.readLine()) != null) {
						sb.append(temp);
					}
					br.close();
					JSONTokener json = new JSONTokener(sb.toString());
					getJSON = (JSONObject) json.nextValue();

					initJSONALL();
					initImages();
					TAG = true;
					getActivity().runOnUiThread(loadingRunnable);
				}

			} catch (Exception e1) {
				e1.printStackTrace();
				TAG = false;
				getActivity().runOnUiThread(loadingRunnable);
			} finally {
				getActivity().runOnUiThread(loadingRunnable);
			}

		}
	}

	class ItemHttpSelectThread implements Runnable {

		String id;
		String path_item_click;

		public ItemHttpSelectThread(String id, String path_item_click) {
			this.id = id;
			this.path_item_click = path_item_click;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				URL url = new URL(path_item_click);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setRequestMethod("POST");
				conn.setConnectTimeout(5 * 1000);
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setUseCaches(false);
				conn.setRequestProperty("Content-Type",
						"application/json; charset=utf-8");
				conn.setRequestProperty("Accept", "application/json");

				JSONObject json_send = new JSONObject();
				json_send.put("ID号", id);
				try {
					BufferedWriter br = new BufferedWriter(
							new OutputStreamWriter(conn.getOutputStream(),
									"utf-8"));
					br.write(json_send.toString());
					br.flush();
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (conn.getResponseCode() == 200) {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(conn.getInputStream(),
									"utf-8"));
					StringBuffer sb = new StringBuffer();
					String temp = "";
					while ((temp = br.readLine()) != null) {
						sb.append(temp);
					}
					br.close();
					JSONTokener json = new JSONTokener(sb.toString());
					getJSON = (JSONObject) json.nextValue();
					initJSONData_Click();

					Intent intent = new Intent(getActivity()
							.getApplicationContext(), PatientInfo.class);
					intent.putExtra("病人信息", datas_click.get(0));
					intent.putExtra("登录者类别", "医护人员");
					startActivity(intent);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * 获取点击的病人信息数据
	 */
	public void initJSONData_Click() {
		try {
			datas_click.clear();
			data = new BrInfo();
			data.setId(getJSON.getJSONObject("病人信息").getString("id"));
			data.setName(getJSON.getJSONObject("病人信息").getString("name"));
			data.setAge(getJSON.getJSONObject("病人信息").getString("age"));
			data.setSex(getJSON.getJSONObject("病人信息").getString("sex"));
			data.setLxfs(getJSON.getJSONObject("病人信息").getString("phone"));
			data.setBrzz(getJSON.getJSONObject("病人信息").getString("brzz"));
			data.setYz(getJSON.getJSONObject("病人信息").getString("yz"));
			data.setZybch(getJSON.getJSONObject("病人信息").getString("zybch"));
			data.setHyzk(getJSON.getJSONObject("病人信息").getString("hyzk"));
			data.setYxck(getJSON.getJSONObject("病人信息").getString("yxck"));
			data.setTssm(getJSON.getJSONObject("病人信息").getString("tssm"));
			data.setShz(getJSON.getJSONObject("病人信息").getString("shz"));
			data.setShtime(getJSON.getJSONObject("病人信息").getString("shtime"));

			datas_click.add(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获取返回的病人信息数据
	 */
	public void initJSONData() {
		try {
			datas.clear();
			data = new BrInfo();
			data.setId(getJSON.getJSONObject("病人信息").getString("id"));
			data.setName(getJSON.getJSONObject("病人信息").getString("name"));
			data.setAge(getJSON.getJSONObject("病人信息").getString("age"));
			data.setSex(getJSON.getJSONObject("病人信息").getString("sex"));
			data.setLxfs(getJSON.getJSONObject("病人信息").getString("phone"));
			data.setBrzz(getJSON.getJSONObject("病人信息").getString("brzz"));
			data.setYz(getJSON.getJSONObject("病人信息").getString("yz"));
			data.setZybch(getJSON.getJSONObject("病人信息").getString("zybch"));
			data.setHyzk(getJSON.getJSONObject("病人信息").getString("hyzk"));
			data.setYxck(getJSON.getJSONObject("病人信息").getString("yxck"));
			data.setTssm(getJSON.getJSONObject("病人信息").getString("tssm"));
			data.setShz(getJSON.getJSONObject("病人信息").getString("shz"));
			data.setShtime(getJSON.getJSONObject("病人信息").getString("shtime"));

			datas.add(data);
			adapter.notifyDataSetChanged();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 初始化全部病人信息
	 */
	private void initJSONALL() {
		try {
			datas.clear();
			int count = getJSON.getInt("病人信息总数");
			FanHuiCanShu.count = getJSON.getInt("病人信息总数");
			getActivity().startService(intent); // 开启GetTxService
			String[] time = new String[count];
			for (int i = 0; i < count; i++) {
				data = new BrInfo();
				data.setId(getJSON.getJSONObject("病人信息" + i).getString("id"));
				data.setName(getJSON.getJSONObject("病人信息" + i)
						.getString("name"));
				data.setAge(getJSON.getJSONObject("病人信息" + i).getString("age"));
				data.setSex(getJSON.getJSONObject("病人信息" + i).getString("sex"));
				data.setLxfs(getJSON.getJSONObject("病人信息" + i).getString(
						"phone"));
				data.setBrzz(getJSON.getJSONObject("病人信息" + i)
						.getString("brzz"));
				data.setYz(getJSON.getJSONObject("病人信息" + i).getString("yz"));
				data.setZybch(getJSON.getJSONObject("病人信息" + i).getString(
						"zybch"));
				data.setHyzk(getJSON.getJSONObject("病人信息" + i)
						.getString("hyzk"));
				data.setYxck(getJSON.getJSONObject("病人信息" + i)
						.getString("yxck"));
				data.setTssm(getJSON.getJSONObject("病人信息" + i)
						.getString("tssm"));
				data.setShz(getJSON.getJSONObject("病人信息" + i).getString("shz"));
				data.setShtime(getJSON.getJSONObject("病人信息" + i).getString(
						"shtime"));

				time[i] = data.getShtime();
				datas.add(data);
			}
			for (int i = 0; i < 4; i++) {
				datas_page.add(datas.get(i));
			}

			String time1, time2;
			for (int i = 0; i < count; i++) {
				if (i < (count - 1)) {
					time1 = time[i];
					time2 = time[i + 1];
					if (time1.compareTo(time2) > 0) {
						time[i] = time2;
						time[i + 1] = time1;
					}
				}
				if (i == count - 1) {
					GETUIInfo.time = time[i];
					Log.i("infoGETUI", GETUIInfo.time);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 形成头像数组
	 */
	private void initImages() {
		images.clear();
		for (int i = 0; i < datas.size(); i++) {

			File f = new File(GESHIHUA.IMAGE_READ_DIR + "/"
					+ datas.get(i).getId() + ".jpg");
			if (!f.exists()) {
				if (datas.get(i).getSex().equals("男")) {
					images.add(img_man);
				} else if (datas.get(i).getSex().equals("女")) {
					images.add(img_woman);
				} else {
					images.add(img_default);
				}
			} else {
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(f);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Bitmap bitmap = BitmapFactory.decodeStream(fis);
				if (bitmap != null) {
					images.add(bitmap);
				}

				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		Log.i("info222", "destroyView");
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i("info222", "destroy");
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		Log.i("info222", "detach");
		getActivity().stopService(intent);
	}
}
