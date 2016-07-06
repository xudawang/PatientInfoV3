package com.my.imageview;

import com.example.patientinfov5.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ImageButton_define extends LinearLayout{
	private ImageView imageView;
	private TextView textView;
	

	public ImageButton_define(Context context, AttributeSet attrs) {
		super(context, attrs);
		imageView = new ImageView(context, attrs);
		textView = new TextView(context, attrs);
		
		imageView.setPadding(0, 0, 0, 0);
		textView.setGravity(android.view.Gravity.RIGHT);
		textView.setPadding(0, 0, 0, 0);
		textView.setClickable(true);
		textView.setFocusable(true);
		setBackgroundResource(R.drawable.ic_launcher);
		setOrientation(LinearLayout.HORIZONTAL);
		addView(imageView);
		addView(textView);
		
	}

}
