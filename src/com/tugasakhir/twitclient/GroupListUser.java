package com.tugasakhir.twitclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GroupListUser extends Activity implements OnClickListener{

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
		startManagingCursor(cursor);
		adapter = new GroupListUserAdapter(this, cursor);
		
		list.setAdapter(adapter);
		
		Button add = (Button)findViewById(R.id.add);
		Button delete_group = (Button)findViewById(R.id.delete_group);
		add.setOnClickListener(this);
		delete_group.setOnClickListener(this);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.add:
				Intent i = new Intent(this, GroupUserForm.class);
				i.putExtra(GroupActivity.ID_EXTRA, group_ID);
				startActivity(i);
				//startActivity(new Intent(this, GroupUserForm.class));
			break;
			
			case R.id.delete_group:
				groupModel.deleteGroup(group_ID);
				Toast.makeText(this, "Delete Group", Toast.LENGTH_LONG).show();
				finish();
			break;			
			
		}
	}
	
	
	

}
