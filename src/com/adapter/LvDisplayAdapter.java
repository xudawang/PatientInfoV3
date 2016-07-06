package com.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.patientinfov5.R;
import com.utils.szIP;

public class LvDisplayAdapter extends BaseAdapter {
	private List<szIP> datas;
	private Context context;
	
	public LvDisplayAdapter(Context context, List<szIP> datas) {
		this.context = context;
		this.datas = datas;
		
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
		ViewHolder vHolder = null;
		if(convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.lv_ip_display, null);
			vHolder = new ViewHolder();
			
			vHolder.tv_title = (TextView) convertView.findViewById(R.id.lv_ip_title);
			vHolder.tv_context = (TextView) convertView.findViewById(R.id.lv_ip_context);
			
			convertView.setTag(vHolder);
		}else {
			vHolder = (ViewHolder) convertView.getTag();
		}
		
		vHolder.tv_title = (TextView) convertView.findViewById(R.id.lv_ip_title);
		vHolder.tv_context = (TextView) convertView.findViewById(R.id.lv_ip_context);
		
		vHolder.tv_context.setText(datas.get(position).getIp());
		
		return convertView;
	}
	
	class ViewHolder {
		TextView tv_title;
		TextView tv_context;
	}

}
