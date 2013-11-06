package com.tugasakhir.twitclient;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class GroupUserForm extends Activity implements OnClickListener{
	
	private GroupDataModel groupModel;
	private EditText username;
	
	private long group_ID = 0;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_user_form);
		
		groupModel = new GroupDataModel(this);
		groupModel.open();
		
		Bundle extras = getIntent().getExtras();
		group_ID = extras.getLong("group_ID");
		
		username = (EditText)findViewById(R.id.username);
		Button save = (Button)findViewById(R.id.save);
		save.setOnClickListener(this);
	}
	
	  protected void onResume() {
		  super.onResume();
		  groupModel.open(); 		  		  
	  }		
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.save:
			
				Cursor c = groupModel.getUserByName(username.getText().toString());
				c.moveToFirst();
				
				if(c.getCount() == 0){
					
					groupModel.insertUser(username.getText().toString(), group_ID);				
					Toast.makeText(this, "Group Saved", Toast.LENGTH_LONG).show();	
					
					finish();
					
				}else{
					Toast.makeText(this, "Group Already Exist", Toast.LENGTH_LONG).show();	
				}

				
				
			break;
			
		default:
			break;
		}
		
	}

}
