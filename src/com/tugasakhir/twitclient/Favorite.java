package com.tugasakhir.twitclient;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Favorite extends Activity{
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		TextView textview = new TextView(this);
		textview.setText("Favorite Lorem ipsum dolor sit amet");
		setContentView(textview);
	}
}
