package com.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.patientinfov5.R;
import com.utils.BrInfo;
import com.utils.GESHIHUA;

public class FragLvAdapter extends BaseAdapter {
	private LinkedList<BrInfo> datas;
	private Activity activity;
	private Thread getTxThread;

	private ViewHolder vHolder;

	private Bitmap img = null;
	private ArrayList<Bitmap> images = new ArrayList<Bitmap>();
	private Bitmap img_man, img_woman, img_default;
	

	public FragLvAdapter(Activity activity, LinkedList<BrInfo> datas, ArrayList<Bitmap> images) {
		this.activity = activity;
		this.datas = datas;
		this.images = images;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		vHolder = new ViewHolder();
		if (convertView == null) {
			vHolder = new ViewHolder();
			convertView = LayoutInflater.from(activity).inflate(
					R.layout.item_doctor_cs, null);
			vHolder.img_tx = (ImageView) convertView
					.findViewById(R.id.img_tx_br);
			vHolder.tv_br_name = (TextView) convertView
					.findViewById(R.id.tv_br_name);
			vHolder.tv_br_sex = (TextView) convertView
					.findViewById(R.id.tv_br_sex);
			vHolder.tv_yz = (TextView) convertView.findViewById(R.id.tv_yz);

			convertView.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) convertView.getTag();
		}

		vHolder.img_tx = (ImageView) convertView.findViewById(R.id.img_tx_br);
		vHolder.tv_br_name = (TextView) convertView
				.findViewById(R.id.tv_br_name);
		vHolder.tv_br_sex = (TextView) convertView.findViewById(R.id.tv_br_sex);
		vHolder.tv_yz = (TextView) convertView.findViewById(R.id.tv_yz);

		vHolder.img_tx.setImageBitmap(images.get(position));

		vHolder.tv_br_name.setText(datas.get(position).getName());
		vHolder.tv_br_sex.setText(datas.get(position).getSex());
		vHolder.tv_yz.setText("Ò½Öö£º" + "\r\n" + "    "
				+ datas.get(position).getYz());

		return convertView;
	}

	class ViewHolder {
		ImageView img_tx;
		TextView tv_br_name;
		TextView tv_br_sex;
		TextView tv_yz;
	}
	
	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
	}
}
