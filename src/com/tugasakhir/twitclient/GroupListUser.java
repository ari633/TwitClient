package com.tugasakhir.twitclient;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class GroupListUser extends Activity{

	private ListView list;
	private Cursor cursor;
	private GroupListUserAdapter adapter;		
	private GroupDataModel groupModel;
	private long group_ID = 0;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_list_user);
		
		Bundle extras = getIntent().getExtras();		
		group_ID = extras.getLong("group_ID");		
		
		list = (ListView)findViewById(R.id.list);
		
		groupModel = new GroupDataModel(this);
		groupModel.open();
		
		Cursor cGroup = groupModel.getById(group_ID);
		cGroup.moveToFirst();
		TextView heading = (TextView)findViewById(R.id.heading);
		heading.setText("Users "+ groupModel.getGroupTitle(cGroup)+" Group");
		
		cursor = groupModel.getUserByGroup(group_ID);
		adapter = new GroupListUserAdapter(this, cursor);
		
		list.setAdapter(adapter);
		
		
	}
	
	
	

}
