package com.tugasakhir.twitclient;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class GroupForm extends Activity implements OnClickListener{
	
	
	private GroupDataModel groupModel;
	
	private EditText title;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_form);
		
		groupModel = new GroupDataModel(this);
		groupModel.open();
		
		
		title = (EditText)findViewById(R.id.title);
		Button save = (Button)findViewById(R.id.save);
		save.setOnClickListener(this);
	}
	
	  protected void onResume() {
			
		  groupModel.open(); 
		  super.onResume();
		  
	  }	
	
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.save:
				
				groupModel.insert(title.getText().toString());
				
				Toast.makeText(this, "Group Saved", Toast.LENGTH_LONG).show();
				finish();
			break;
			
		default:
			break;
		}
		
	}

	
	
}
