package com.tugasakhir.twitclient;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class GroupActivity extends Activity implements OnClickListener{
	
	private ListView list;
	private TwitDataHelper groupHelper;
	private SQLiteDatabase db;
	private Cursor cursor;
	private GroupAdapter groupAdapter;		
	private GroupDataModel groupModel;
	
	public final static String ID_EXTRA = "group_ID";
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group);
		
		
		
		list = (ListView)findViewById(R.id.list);
		
		groupHelper = new TwitDataHelper(this); 
		db = groupHelper.getReadableDatabase();
		cursor = db.query("groups", null, null, null, null, null, "_id DESC");
		
		startManagingCursor(cursor);
		
		groupAdapter = new GroupAdapter(this, cursor);
		
		groupModel = new GroupDataModel(this);
		groupModel.open();
		
		list.setAdapter(groupAdapter);
		
        // Click event for single list row
        list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {			
				Intent i = new Intent(view.getContext(), GroupUserActivity.class);
				i.putExtra(ID_EXTRA, id);
				view.getContext().startActivity(i);
				//Toast.makeText(getApplicationContext(), "Click ListItem "+ id, Toast.LENGTH_LONG).show();
				//groupModel.deleteGroup(id);
			}
        	
        });

	    LinearLayout homeClicker = (LinearLayout)findViewById(R.id.homebtn);
	    homeClicker.setOnClickListener(this);		
		
	    LinearLayout tweetClicker = (LinearLayout)findViewById(R.id.tweetbtn);
	    tweetClicker.setOnClickListener(this);	  
	    
	    Button add = (Button)findViewById(R.id.add);
	    add.setOnClickListener(this);
		
	}

	  
	protected void onResume() {
		  groupModel.open(); 
		  super.onResume();
	  }		
	
	public void onClick(View v) {
		switch (v.getId()) {	
			case R.id.homebtn:
					finish();
			break;
			
			case R.id.tweetbtn:
				startActivity(new Intent(this, ClientTweet.class));
			break;
			
			case R.id.add:
				startActivity(new Intent(this, GroupForm.class));
			break;
			
		}
	}	
	
	 public boolean onCreateOptionsMenu(Menu menu) {
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.group, menu);
	        return true;
	 }	
	
	 public boolean onOptionsItemSelected(MenuItem item) {
		 
	        switch (item.getItemId()) {	       
	        
	        case R.id.add:
	        	startActivity(new Intent(this, GroupForm.class));
	        	Toast.makeText(this, "Add", Toast.LENGTH_LONG).show();	        	
	        return true;
	        
	        default:
	        return super.onOptionsItemSelected(item);
	        
	        }
	 }


	
	
	
}
