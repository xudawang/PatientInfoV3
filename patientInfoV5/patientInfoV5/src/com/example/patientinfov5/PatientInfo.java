package com.example.patientinfov5;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.config.xml.Config;
import com.thread.utils.DeleteThread;
import com.utils.BrInfo;
import com.utils.FanHuiCanShu;
import com.utils.GESHIHUA;

public class PatientInfo extends Activity {
	private TextView tv_display;
	private ImageView img_tx;
	private ImageView btn_update;
	private ImageView btn_delete;
	
	private ImageView imageBack;
	private TextView centerTitle;

	private String path; // 选择的图片路径
	private Bitmap bitmap = null; // 选出的图像转为Bitmap

	private Bitmap img = null; // 从服务端获取的头像

	private LinearLayout ll_left, ll_right;

	private ArrayList<BrInfo> datas;
	private BrInfo data;

	private AlertDialog dialog;

	private Config config;
	private String ShTime_current;

	private Thread deleteThread;
	private String dlzlb;

	private Dialog tDialog;

	/**
	 * 打开图片库的配置
	 */
	private String IMAGE_TYPE = "image/jpg";
	private final int IMAGE_CODE = 0;

	private Handler mHandler = new Handler();

	/**
	 * 头像显示并关闭加载框
	 */
	private Runnable imageRunnable = new Runnable() {

		@Override
		public void run() {
			img_tx.setImageBitmap(img);
			tDialog.dismiss();
		}
	};
	
	/**
	 * 获取头像加载框关闭
	 */
	private Runnable dialogRunnable = new Runnable() {
		
		@Override
		public void run() {
			tDialog.dismiss();
		}
	}; 

	/**
	 * 显示Toast线程
	 */
	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			if (FanHuiCanShu.state_DELETE.equals("删除成功")) {
				Toast.makeText(PatientInfo.this, "删除成功！", 1).show();
				dialog.dismiss();
			} else {
				Toast.makeText(PatientInfo.this, "删除失败！", 1).show();
				dialog.dismiss();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.patient_info);

		initView();
		initData();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		tDialog.show();
		super.onResume();
		Thread GetTxThread = new Thread(new GetTx());
		GetTxThread.start();
	}

	private void initData() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		data = (BrInfo) intent.getSerializableExtra("病人信息");
		dlzlb = intent.getStringExtra("登录者类别");
		tv_display.setText("ID号：" + data.getId() + "\r\n" + "病人姓名："
				+ data.getName() + "\r\n" + "病人性别：" + data.getSex() + "\r\n"
				+ "年龄：" + data.getAge() + "\r\n" + "医嘱：" + data.getYz()
				+ "\r\n" + "联系方式：" + data.getLxfs() + "\r\n" + "病人症状："
				+ data.getBrzz() + "\r\n" + "住院病床号:" + data.getZybch() + "\r\n"
				+ "婚姻状况：" + data.getHyzk() + "\r\n" + "是否允许查看："
				+ data.getYxck() + "\r\n" + "特殊说明：" + data.getTssm() + "\r\n"
				+ "审核医生：" + data.getShz() + "\r\n" + "审核时间：" + data.getShtime()
				+ "\r\n");

		/* int ceshi = Integer.parseInt(data.getShtime()); */

		if (data.getSex().equals("男")) {
			img_tx.setImageResource(R.drawable.man);
		} else if (data.getSex().equals("女")) {
			img_tx.setImageResource(R.drawable.woman);
		} else {
			img_tx.setImageResource(R.drawable.ic_launcher);
		}

		if (dlzlb.equals("病人")) {
			btn_delete.setVisibility(View.GONE);
			btn_update.setVisibility(View.GONE);
		}
	}

	private void initView() {
		// TODO Auto-generated method stub
		tDialog = new Dialog(this);
		tDialog.setContentView(R.layout.dialog_loading);

		img_tx = (ImageView) findViewById(R.id.img_tx);
		tv_display = (TextView) findViewById(R.id.tv_test);
		btn_update = (ImageView) findViewById(R.id.btn_update);
		btn_delete = (ImageView) findViewById(R.id.btn_delete);
		
		imageBack = (ImageView) findViewById(R.id.toolbar_back);
		centerTitle = (TextView) findViewById(R.id.text_view_center_title);
		
		centerTitle.setText("病人信息");
		
		imageBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PatientInfo.this.finish();
			}
		});

		ll_left = (LinearLayout) findViewById(R.id.ll_left);
		ll_right = (LinearLayout) findViewById(R.id.ll_right);

		if (ChanelActivity.career.equals("护士")) {
			ll_left.setVisibility(View.GONE);
		} else if (ChanelActivity.career.equals("医生")
				|| ChanelActivity.career.equals("管理员")) {
			ll_left.setVisibility(View.VISIBLE);
		}

		initListener();
	}

	private void initListener() {
		// TODO Auto-generated method stub
		ll_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btn_delete.setImageResource(R.mipmap.delete_select);
				Date date = new Date();
				SimpleDateFormat f = new SimpleDateFormat(GESHIHUA.TIME_GS);
				ShTime_current = f.format(date);
				AlertDialog.Builder qdDialog = new AlertDialog.Builder(
						PatientInfo.this);
				qdDialog.setCancelable(false);
				qdDialog.setTitle("警告！")
						.setMessage(
								"接下来将要彻底删除姓名为：" + data.getName() + ", 病案号为："
										+ data.getId() + "的病人所有信息！" + "\r\n"
										+ "确定吗？")
						.setPositiveButton(R.string.ad_ok,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								})
						.setNegativeButton(R.string.ad_cancel,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										btn_delete
												.setImageResource(R.mipmap.delete);
									}
								});

				dialog = qdDialog.create();
				dialog.show();
				// 覆盖AlertDialog的按钮点击事件
				dialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								String path = "http://"
										+ ChanelActivity.url_IP
										+ ":"
										+ ChanelActivity.PORT
										+ "/HttpServer/servlet/HttpServerDelete";

								// 删除操作前为了安全在本地备份数据
								config = new Config(data,
										ChanelActivity.login_Name,
										ShTime_current);
								String mag = config.writeXmlToLocal();

								// 刪除操作
								deleteThread = new Thread(new DeleteThread(
										path, data));
								deleteThread.start();

								mHandler.postDelayed(mRunnable, 1 * 1000);
								btn_delete.setImageResource(R.mipmap.delete);
							}
						});
			}
		});
		ll_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent_toupdate = new Intent(getApplicationContext(),
						InfoToUpdate.class);
				intent_toupdate.putExtra("病人信息", data);
				startActivity(intent_toupdate);
				PatientInfo.this.finish();
			}
		});

		/**
		 * 选择上传的头像
		 */
		img_tx.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType(IMAGE_TYPE);
				startActivityForResult(intent, IMAGE_CODE);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		Bitmap bm = null;
		ContentResolver resolver = getContentResolver();
		if (requestCode == IMAGE_CODE) {
			Uri originalUri = data.getData(); // 获取图片的uri
			try {
				bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);
				String[] proj = { MediaStore.Images.Media.DATA };
				/* Cursor c = managedQuery(originalUri, proj, null, null, null); */// 已过时
				Cursor c = getContentResolver().query(originalUri, proj, null,
						null, null);
				if (c.moveToFirst()) {
					int column_index = c
							.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					path = c.getString(column_index);
				}
				c.close();
				/* c.moveToFirst(); */
				if (path != null) {
					tDialog.show();
					bitmap = BitmapFactory.decodeFile(path);
					img_tx.setImageBitmap(bitmap);
					Thread iT = new Thread(new imageThread());
					iT.start();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 上传头像
	 * 
	 * @author xudawang
	 * 
	 */
	class imageThread implements Runnable {
		@Override
		public void run() {

			try {
				URL url = new URL("http://" + ChanelActivity.url_IP + ":"
						+ ChanelActivity.PORT
						+ "/HttpServer/servlet/TestHttpSave");
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setRequestMethod("POST");
				conn.setConnectTimeout(5 * 1000);
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setUseCaches(false);

				conn.setRequestProperty("Content-Type",
						"multipart/form-data; boundary=------------------123456789");
				conn.setRequestProperty("Accept", "image/*, application/json");

				conn.connect();

				File f = new File(path);
				int size = (int) f.length();
				FileInputStream inputStream = new FileInputStream(f);
				FileInputStream inputStreamtoSD = new FileInputStream(f);
				byte[] buffer = new byte[size];
				int len = -1;

				inputStream.read(buffer, 0, size);
				String finalName = f.getName().trim();
				String name = data.getId()
						+ finalName.substring(finalName.lastIndexOf("."));

				saveImageToSD(size, inputStreamtoSD, name);

				OutputStream os = conn.getOutputStream();
				os.write(name.getBytes());
				os.write('|');
				os.write(buffer);
				os.flush();
				os.close();
				inputStream.close();
				if (conn.getResponseCode() == 200) {
					PatientInfo.this.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							tDialog.show();
						}
					});
					new Thread(new GetTx()).start();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	/**
	 * 存储到指定的本地文件夹
	 * @param size
	 * @param inputStreamtoSD
	 * @param name
	 */
	private void saveImageToSD(int size, FileInputStream inputStreamtoSD,
			String name) {
		try {
			Options options = new Options();
			options.inSampleSize = 10;
			Bitmap image = BitmapFactory.decodeStream(inputStreamtoSD, null,
					options);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			int per = 100;
			while (baos.toByteArray().length / 1024 > 100) {
				int test = baos.size();
				baos.reset();
				image.compress(Bitmap.CompressFormat.JPEG, per, baos);
				per -= 10; // 每次减少10
			}
			if (image != null && !image.isRecycled()) {
				image.recycle();
				image = null;
				System.gc();
			}
			byte[] bufferSd = new byte[baos.toByteArray().length];
			ByteArrayInputStream bis = new ByteArrayInputStream(
					baos.toByteArray()); // 将压缩后的baos存放到bis中

			baos.close();
			FileOutputStream fos = new FileOutputStream(GESHIHUA.IMAGE_READ_DIR
					+ "/" + name);

			bis.read(bufferSd);

			fos.write(bufferSd, 0, bufferSd.length);
			fos.flush();
			fos.close();
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取头像
	 * 
	 * @author xudawang
	 * 
	 */
	class GetTx implements Runnable {

		@Override
		public void run() {
			String urlstring = "http://" + ChanelActivity.url_IP + ":"
					+ ChanelActivity.PORT + "/HttpServer/servlet/TestHttp";
			try {
				URL url = new URL(urlstring);

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

				conn.connect();

				JSONObject json_send = new JSONObject();
				json_send.put("头像名", data.getId());
				try {
					BufferedWriter bw = new BufferedWriter(
							new OutputStreamWriter(conn.getOutputStream(),
									"utf-8"));
					bw.write(json_send.toString());
					bw.flush();
					bw.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (conn.getResponseCode() == 200) {
					InputStream is = conn.getInputStream();
					img = BitmapFactory.decodeStream(is);
					if (img != null) {
						PatientInfo.this.runOnUiThread(imageRunnable);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();

			} finally {
				PatientInfo.this.runOnUiThread(dialogRunnable);
			}

		}
	}
}
