package com.tugasakhir.twitclient;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Message extends Activity{
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		TextView textview = new TextView(this);
		textview.setText("Message test Lorem ipsum dolor sit amet");
		setContentView(textview);
	}
}
