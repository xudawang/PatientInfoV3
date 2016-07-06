package com.thread.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.patientinfov5.R;
import com.utils.GESHIHUA;
import com.utils.GETUIDatas;
import com.utils.ImageTx;

/**
 * 从TxImage文件夹中获取图像文件的线程
 * @author xudawang
 *
 */
public class GetTxImage implements Runnable{
	private Bitmap img_man, img_woman, img_default;
	private int count;
	
	public GetTxImage(int count){
		img_man = ImageTx.img_man;
		img_woman = ImageTx.img_woman;
		img_default = ImageTx.img_default;
		this.count = count;
	}
	

	@Override
	public void run() {
		initImages();
	}

	/**
	 * 形成头像数组
	 */
	private void initImages() {
		ImageTx.brTx.clear();
		for (int i = 0; i < count; i++) {

			File f = new File(GESHIHUA.IMAGE_READ_DIR + "/"
					+ GETUIDatas.datas.get(i).getId() + ".jpg");
			if (!f.exists()) {
				if (GETUIDatas.datas.get(i).getSex().equals("男")) {
					ImageTx.brTx.add(img_man);
				} else if (GETUIDatas.datas.get(i).getSex().equals("女")) {
					ImageTx.brTx.add(img_woman);
				} else {
					ImageTx.brTx.add(img_default);
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
					ImageTx.brTx.add(bitmap);
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

}
