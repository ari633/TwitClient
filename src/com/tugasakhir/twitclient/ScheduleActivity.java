package com.tugasakhir.twitclient;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

public class ScheduleActivity extends Activity{
	
	private ListView list;
	private Cursor cursor;
	
	private TweetModel tweetModel;
	private ScheduleAdapter scAdapter;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.schedule_tweet);
		list = (ListView)findViewById(R.id.list);
		
		tweetModel = new TweetModel(this);
		tweetModel.open();
		
		cursor = tweetModel.getAll();
		startManagingCursor(cursor);
		
		scAdapter = new ScheduleAdapter(this, cursor);
		list.setAdapter(scAdapter);
		
	}
	
	
}
