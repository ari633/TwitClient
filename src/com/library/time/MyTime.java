package com.library.time;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyTime {
	
	@SuppressLint("SimpleDateFormat")
	public static long dateToEpoch(String time){
		try {
			
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Date date = df.parse(time);
			long epoch = date.getTime();
			return epoch;
		} catch (Exception e) {
			
			return 0;
		}
	}
	
}
