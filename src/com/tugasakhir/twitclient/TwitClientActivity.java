package com.tugasakhir.twitclient;


import android.app.ActivityGroup;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
//import twitter4j.ProfileImage; 
import android.database.Cursor; 
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase; 
import android.widget.Button;
import android.widget.ListView; 

public class TwitClientActivity extends ActivityGroup implements OnClickListener{
	
	
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
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);     
        
        
        setupTimeline();         
    }


	private void setupTimeline(){	
		
		Log.v(LOG_TAG, "settings up timeline");
		
		setContentView(R.layout.timeline);
		
        Button btn_group = (Button)findViewById(R.id.group_tl);
        btn_group.setOnClickListener(this);	
		
		try {
			//get reference to the list view
		homeTimeline = (ListView)findViewById(R.id.homeList);
			//instantiate database helper
		timelineHelper = new TwitDataHelper(this);
			//get the database
		timelineDB = timelineHelper.getReadableDatabase();	
		
	    //query the database, most recent tweets first except mute user
		String query = "SELECT * FROM home WHERE user_screen NOT IN "+"(SELECT user_screen FROM mute_users) ORDER BY update_time DESC";
		timelineCursor = timelineDB.rawQuery(query, null);
		//manage the updates using a cursor
		startManagingCursor(timelineCursor);
	    //instantiate adapter
		timelineAdapter = new UpdateAdapter(this, timelineCursor);
		
		//apply the adapter to the timeline view
		//this will make it populate the new update data in the view
		homeTimeline.setAdapter(timelineAdapter);
		//instantiate receiver class for finding out when new updates are available
		twitStatusReceiver = new TwitterUpdateReceiver();
		//register for updates
	    registerReceiver(twitStatusReceiver, new IntentFilter("TWITTER_UPDATES"));		
		//start the service for updates now
		//this.getApplicationContext().startService(new Intent(this.getApplicationContext(), TimelineService.class));
		
		} catch (Exception te) {
			Log.e(LOG_TAG, "Failed to fetch timeline: "+te.getMessage());
		}
		
	}
	
	

	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.group_tl:
			replaceContentView("TimelineGroup", new Intent(v.getContext(), GroupTimelineActivity.class));	
			break;

		default:
			break;
		}
		
	}	
	
	public void replaceContentView(String id, Intent newIntent) {
		
		View view = getLocalActivityManager().startActivity(id,newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)) .getDecorView(); this.setContentView(view);
		
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
			stopService(new Intent(this, TimelineService.class));
			//remove receiver register
			unregisterReceiver(twitStatusReceiver);
			//close the database
			timelineDB.close();
		}
		catch(Exception se) { Log.e(LOG_TAG, "unable to stop service or receiver"); }
	}


	
	
}