package com.tugasakhir.twitclient;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class MuteActivity extends Activity implements OnClickListener{

	private ListView list;
	private TwitDataHelper dataHelper;
	private SQLiteDatabase db;
	private Cursor cursor;	
	
	private MuteDataModel muteModel;
	private MuteAdapter adapter;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.mute_user);
		list = (ListView)findViewById(R.id.list);
		
		muteModel = new MuteDataModel(this);
		muteModel.open();
		
		cursor = muteModel.getUsers();
		startManagingCursor(cursor);
		
		adapter = new MuteAdapter(this, cursor); 
		list.setAdapter(adapter);
		
	    Button add = (Button)findViewById(R.id.add);
	    add.setOnClickListener(this);		
		
		
	}

	public void onClick(View v) {
		
		switch (v.getId()) {	
		
		case R.id.add:
			startActivity(new Intent(this, MuteForm.class));
		break;
		
		}
		
	}
	
	
}
