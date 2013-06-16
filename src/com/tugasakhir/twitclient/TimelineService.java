package com.tugasakhir.twitclient;

import twitter4j.Status; 
import twitter4j.Twitter; 
import twitter4j.TwitterFactory; 
import twitter4j.conf.Configuration; 
import twitter4j.conf.ConfigurationBuilder; 
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log; 
import java.util.List;


public class TimelineService extends Service{
	
	/**Twitter objext*/
	private Twitter timelineTwitter; 
	/**Database helper object*/
	private TwitDataHelper twitHelper;
	/**Timeline database*/
	private SQLiteDatabase twitDB;
	/**shared preferences for user details*/
	private SharedPreferences twitPrefs;
	/**handler for updater*/
	private Handler twitHandler;	
	/**updater thread object*/
	private TimelineUpdater twitUpdater;	
	private MentionTimelineUpdater mentionUpdater;
	private FavoriteTimelineUpdater favoriteUdapter;
	
	
	/*delay between fethcing  new tweets*/
	private static int mins = 1;
	private static final long FETCH_DELAY = mins * (60*1000);
	//debugging tag
	private String LOG_TAG = "TimelineService";
	
	
	public void onCreate(){
		super.onCreate();
		//get prefs
		twitPrefs = getSharedPreferences("TwitClientPrefs", 0);
		//get db helper
		twitHelper = new TwitDataHelper(this);
		//get the database
		twitDB = twitHelper.getWritableDatabase();
		//get user preferences
		String userToken = twitPrefs.getString("user_token", null);
		String userSecret = twitPrefs.getString("user_secret", null);		
		
		//create new configuration
		//create new configuration
	Configuration twitConf = new ConfigurationBuilder()
		.setOAuthConsumerKey(Const.TWIT_KEY)
		.setOAuthConsumerSecret(Const.TWIT_SECRET)
		.setOAuthAccessToken(userToken)
		.setOAuthAccessTokenSecret(userSecret)
		.build();
		//instantiate new twitter
	timelineTwitter = new TwitterFactory(twitConf).getInstance();
		
	}
	
	/*
	 * Get ready for updating
	 */

	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStart(intent, startId);
		//get handler
		twitHandler = new Handler();
		//create an instance of the updater class
		twitUpdater = new TimelineUpdater();
		mentionUpdater = new MentionTimelineUpdater();
		favoriteUdapter = new FavoriteTimelineUpdater();
		//add to run queue
		twitHandler.post(twitUpdater);
		twitHandler.post(mentionUpdater);
		twitHandler.post(favoriteUdapter);
		//return sticky
		return START_STICKY;
	}

	/**
	 * Perform close tasks
	 */
	
	public void onDestroy() {
		super.onDestroy();
		//stop the updating
		twitHandler.removeCallbacks(twitUpdater);
		twitDB.close();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/**
	 * TimelineUpdater class implements the runnable interface
	 */
	class TimelineUpdater implements Runnable 
	{
		
		//run method
		public void run() {
			//check for updates - assume none
			boolean statusChanges = false;
			try {
				
				//retrieve the new home timeline tweets as a list
				List<Status> homeTimeline = timelineTwitter.getHomeTimeline();
				
				//iterate through new status updates
				for (Status statusUpdate : homeTimeline) 
				{
						//call the getValues method of the data helper class, passing the new updates
					ContentValues timelineValues = TwitDataHelper.getValuesTimeline(statusUpdate);
					twitDB.insertOrThrow("home", null, timelineValues);
						//confirm we have new updates
					statusChanges = true;
				}				
				
			} catch (Exception te) {
				Log.e(LOG_TAG, "Exception: " + te);
			}
			//if we have new updates, send a broadcast
			if (statusChanges) 
			{
					//this should be received in the main timeline class
				sendBroadcast(new Intent("TWITTER_UPDATES"));
			}
			//delay fetching new updates
			twitHandler.postDelayed(this, FETCH_DELAY);
		}
		
	}
	
	
	/**
	 * TimelineUpdater class implements the runnable interface
	 */
	class MentionTimelineUpdater implements Runnable 
	{
		
		//run method
		public void run() {
			//check for updates - assume none
			boolean statusChanges = false;
			try {
				
				//retrieve the new home timeline tweets as a list
				List<Status> mentionTimeline = timelineTwitter.getMentions();
				
				//iterate through new status updates
				for (Status statusUpdate : mentionTimeline) 
				{
						//call the getValues method of the data helper class, passing the new updates
					ContentValues mentionValues = TwitDataHelper.getValuesMention(statusUpdate);
					twitDB.insertOrThrow("mentions", null, mentionValues);
						//confirm we have new updates
					statusChanges = true;
				}				
				
			} catch (Exception te) {
				Log.e(LOG_TAG, "Exception: " + te);
			}
			//if we have new updates, send a broadcast
			if (statusChanges) 
			{
					//this should be received in the main timeline class
				sendBroadcast(new Intent("MENTIONS_UPDATES"));
			}
			//delay fetching new updates
			twitHandler.postDelayed(this, FETCH_DELAY);
		}
		
	}

	
	/**
	 * TimelineUpdater class implements the runnable interface
	 */
	class FavoriteTimelineUpdater implements Runnable 
	{
		
		//run method
		public void run() {
			//check for updates - assume none
			boolean statusChanges = false;
			try {
				
				//retrieve the new favorite timeline tweets as a list
				List<Status> favoriteTimeline = timelineTwitter.getFavorites();
				
				//iterate through new status updates
				for (Status statusUpdate : favoriteTimeline) 
				{
						//call the getValues method of the data helper class, passing the new updates
					ContentValues favoriteValues = TwitDataHelper.getValuesFavorite(statusUpdate);
					twitDB.insertOrThrow("favorite", null, favoriteValues);
						//confirm we have new updates
					statusChanges = true;
				}				
				
			} catch (Exception te) {
				Log.e(LOG_TAG, "Exception: " + te);
			}
			//if we have new updates, send a broadcast
			if (statusChanges) 
			{
					//this should be received in the main timeline class
				sendBroadcast(new Intent("FAVORITES_UPDATES"));
			}
			//delay fetching new updates
			twitHandler.postDelayed(this, FETCH_DELAY);
		}
	}	
	
}
