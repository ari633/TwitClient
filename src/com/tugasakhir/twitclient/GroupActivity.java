package com.tugasakhir.twitclient;


import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

public class GroupActivity extends Activity{
	
	private ListView list;
	private TwitDataHelper groupHelper;
	private SQLiteDatabase db;
	private Cursor cursor;
	private GroupAdapter groupAdapter;		
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group);
		
		list = (ListView)findViewById(R.id.list);
		
		groupHelper = new TwitDataHelper(this); 
		db = groupHelper.getReadableDatabase();
		cursor = db.query("groups", null, null, null, null, null, "_id DESC");
		
		startManagingCursor(cursor);
		
		groupAdapter = new GroupAdapter(this, cursor);
		
		list.setAdapter(groupAdapter);
	}
	
	
	
}
