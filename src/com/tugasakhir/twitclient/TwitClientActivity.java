package com.tugasakhir.twitclient;


import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.View;
import android.database.Cursor; 
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase; 
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView; 
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class TwitClientActivity extends Activity{
	
	
	//for error logging 
	private String LOG_TAG = "TwitClientActivity";//Log
	
	/**main view for the home timeline*/
	private ListView homeTimeline;
	/**database helper for update data*/
	private TwitDataHelper timelineHelper;
	/**update database*/
	private SQLiteDatabase timelineDB;
	/**cursor for handling data*/
	private Cursor timelineCursor;
	/**adapter for mapping data*/
	private UpdateAdapter timelineAdapter;	

	/**broadcast receiver for when new updates are available*/
	private BroadcastReceiver twitStatusReceiver;
	
	private GroupDataModel groupModel;	
	private Spinner spinner;
	private String query;

	private List<String> labels;
	private ArrayAdapter<String> spinnerAdapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);     
        
		//instantiate database helper
		timelineHelper = new TwitDataHelper(this);
			//get the database
		timelineDB = timelineHelper.getReadableDatabase();
		
		groupModel = new GroupDataModel(this);
		groupModel.open();
		
        setupTimeline();  
        this.getApplicationContext().startService(new Intent(this.getApplicationContext(), TimelineService.class));
    }


	private void setupTimeline(){	
		
		Log.v(LOG_TAG, "settings up timeline");
		
		setContentView(R.layout.timeline);
		homeTimeline = (ListView)findViewById(R.id.homeList);
        
		spinner = (Spinner) findViewById(R.id.spinner);
		
		loadSpinnerData();
	    
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				String label = parent.getItemAtPosition(position).toString();
				
				if(label.equals("All Timeline")){					
					query = "SELECT * FROM home WHERE user_screen  NOT IN (SELECT user_screen FROM mute_users) ORDER BY update_time DESC";					
				}else{
					Cursor cGroup = groupModel.getBytitle(label);
					cGroup.moveToFirst();
					
					String idGroup = groupModel.getGroupId(cGroup);
					
					query = "SELECT * FROM home WHERE user_screen  IN (SELECT user_screen FROM group_users WHERE id_group = "+ idGroup +" ORDER BY _id DESC) ORDER BY update_time DESC";										
				}
				
				try {
					
				    //query the database, most recent tweets first except mute user
					
					timelineCursor = timelineDB.rawQuery(query, null);		
					//manage the updates using a cursor
					startManagingCursor(timelineCursor);
				    //instantiate adapter
					timelineAdapter = new UpdateAdapter(parent.getContext(), timelineCursor);
					
					//apply the adapter to the timeline view
					//this will make it populate the new update data in the view
					homeTimeline.setAdapter(timelineAdapter);
					//instantiate receiver class for finding out when new updates are available
					twitStatusReceiver = new TwitterUpdateReceiver();
					//register for updates
				    registerReceiver(twitStatusReceiver, new IntentFilter("TWITTER_UPDATES"));		
					//start the service for updates now
				    
					
					} catch (Exception te) {
						Log.e(LOG_TAG, "Failed to fetch timeline: "+te.getMessage());
					}				
				
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});     
		
        

		
	}
	
	
	private void loadSpinnerData(){
		
		labels = groupModel.getAllLabelGroups();
		
	    spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labels);
		spinnerAdapter.notifyDataSetChanged();
		
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
				
		
	    spinner.setAdapter(spinnerAdapter);
		
	}	


	
	/**
	 * Class to implement broadcast receipt for new updates
	 */
	class TwitterUpdateReceiver extends BroadcastReceiver 
	{
		
		
		/**
		 * When new updates are available, a broadcast is received here
		 */
		@Override
		public void onReceive(Context context, Intent intent) {
			//delete db rows
			int rowLimit = 150;
			if(DatabaseUtils.queryNumEntries(timelineDB, "home")>rowLimit) {
				String deleteQuery = "DELETE FROM home WHERE "+BaseColumns._ID+" NOT IN " +
						"(SELECT "+BaseColumns._ID+" FROM home ORDER BY "+"update_time DESC " +
								"limit "+rowLimit+")";	
				timelineDB.execSQL(deleteQuery);
			}	
			
			String query = "SELECT * FROM home WHERE user_screen NOT IN "+"(SELECT user_screen FROM mute_users) ORDER BY update_time DESC";
			timelineCursor = timelineDB.rawQuery(query, null);						
			
			startManagingCursor(timelineCursor);
			timelineAdapter = new UpdateAdapter(context, timelineCursor);
			homeTimeline.setAdapter(timelineAdapter);			
			
		}
		
	}
	
	
	public void onResume(){
		super.onResume();
		labels.clear();
		labels.addAll(groupModel.getAllLabelGroups());
		spinnerAdapter.notifyDataSetChanged();
		//this.getApplicationContext().startService(new Intent(this.getApplicationContext(), TimelineService.class));
	}
	
	public void onPause(){
		super.onPause();
		//stopService(new Intent(this, TimelineService.class));
	}
	/*
	 * When the class is destroyed, close database and service classes
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		try 
		{
			//stop the updater service
			//stopService(new Intent(this, TimelineService.class));
			//remove receiver register
			unregisterReceiver(twitStatusReceiver);
			//close the database
			timelineDB.close();
		}
		catch(Exception se) { Log.e(LOG_TAG, "unable to stop service or receiver"); }
	}


	
	
}