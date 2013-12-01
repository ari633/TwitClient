package com.tugasakhir.twitclient;



import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.ListView;


public class FavoriteActivity extends Activity{
	
	
	//for error logging 
	private String LOG_TAG = "FavoriteActivity";//Log
	
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
	

	//set gambar profile
	//ProfileImage.ImageSize imageSize = ProfileImage.NORMAL;		
	
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setupFavorite();
		
	}
	
	
	private void setupFavorite(){ 				
		Log.v(LOG_TAG, "settings up favorite");
		
		setContentView(R.layout.timelinegeneral);
					
		try {
			//get reference to the list view
		homeTimeline = (ListView)findViewById(R.id.homeList);
			//instantiate database helper
		timelineHelper = new TwitDataHelper(this);
			//get the database
		timelineDB = timelineHelper.getReadableDatabase();	
		
	    //query the database, most recent tweets first
		timelineCursor = timelineDB.query("favorite", null, null, null, null, null, "update_time DESC");
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
	    registerReceiver(twitStatusReceiver, new IntentFilter("FAVORITES_UPDATES"));		
		//start the service for updates now
		//this.getApplicationContext().startService(new Intent(this.getApplicationContext(), TimelineService.class));
		
		} catch (Exception te) {
			Log.e(LOG_TAG, "Failed to fetch timeline: "+te.getMessage());
		}
		
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
			int rowLimit = 50;
			if(DatabaseUtils.queryNumEntries(timelineDB, "favorite")>rowLimit) {
				String deleteQuery = "DELETE FROM favorite WHERE "+BaseColumns._ID+" NOT IN " +
						"(SELECT "+BaseColumns._ID+" FROM favorite ORDER BY "+"update_time DESC " +
								"limit "+rowLimit+")";	
				timelineDB.execSQL(deleteQuery);
			}	
			timelineCursor = timelineDB.query("favorite", null, null, null, null, null, "update_time DESC");
			startManagingCursor(timelineCursor);
			timelineAdapter = new UpdateAdapter(context, timelineCursor);
			homeTimeline.setAdapter(timelineAdapter);			
			
		}
		
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
