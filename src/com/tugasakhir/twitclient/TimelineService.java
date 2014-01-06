package com.tugasakhir.twitclient;

import twitter4j.DirectMessage;
import twitter4j.IDs;
import twitter4j.Paging;
import twitter4j.Status; 
import twitter4j.Twitter; 
import twitter4j.TwitterFactory; 
import twitter4j.User;
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
import android.widget.Toast;

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
	private FriendsUpdater Friends;
	private TimelineUpdater twitUpdater;	
	private MentionTimelineUpdater mentionUpdater;
	private FavoriteTimelineUpdater favoriteUdapter;
	private MessageUpdapter messageUpdater;
	private SendMessageUpdapter sendMessageUpdater;
	/*delay between fethcing  new tweets*/
	private static int mins = 2;
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
		String userToken = twitPrefs.getString(Const.TOKEN, null);
		String userSecret = twitPrefs.getString(Const.TOKEN_SECRET, null);		
		
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
		messageUpdater = new MessageUpdapter();
		favoriteUdapter = new FavoriteTimelineUpdater();
		sendMessageUpdater = new SendMessageUpdapter();
		//add to run queue
		
		twitHandler.post(twitUpdater);
		twitHandler.post(mentionUpdater);
		twitHandler.post(favoriteUdapter);
		twitHandler.post(messageUpdater);
		twitHandler.post(sendMessageUpdater);
	
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
		twitHandler.removeCallbacks(mentionUpdater);
		twitHandler.removeCallbacks(favoriteUdapter);
		twitHandler.removeCallbacks(messageUpdater);
		twitHandler.removeCallbacks(sendMessageUpdater);
		
		twitDB.close();
		
		 Toast.makeText(this, "service tl done", Toast.LENGTH_SHORT).show(); 
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public void refreshFriends(){
		twitHandler = new Handler();
		Friends = new FriendsUpdater();
		twitHandler.post(Friends);
	}
	
	/*
	 * Get Friends Following List
	 */
	class FriendsUpdater implements Runnable
	{
		public void run(){
			long cursor = -1;
			String myUser = "pratomoss";
			IDs ids;
			try {

				do {
					
					ids = timelineTwitter.getFriendsIDs(myUser, cursor);
					for(long id: ids.getIDs()){
						User user = timelineTwitter.showUser(id);
						ContentValues followingValues = TwitDataHelper.getValuesFollowing(user);
						twitDB.insertOrThrow("following", null, followingValues);
					}
					
				} while ((cursor = ids.getNextCursor()) != 0);
				
			} catch (Exception e) {
				Log.e(LOG_TAG, "Exception: " + e);
			}
			
			//sendBroadcast(new Intent("TWITTER_UPDATES"));
			twitHandler.postDelayed(this, FETCH_DELAY);
		}
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
				sendBroadcast(new Intent("USER_GROUP_UPDATES"));
			}
			//delay fetching new updates
			twitHandler.postDelayed(this, FETCH_DELAY);
			
		}
		
	}
	
	
	/**
	 * MentionTimelineUpdater class implements the runnable interface
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
	 * FavoriteTimelineUpdater class implements the runnable interface
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
	
	/**
	 * MessageUpdapter class implements the runnable interface
	 */	
	class MessageUpdapter implements Runnable
	{

		public void run() {

			boolean statusChanges = false;
			try {
				//list DM masuk
				List<DirectMessage> directMessage =  timelineTwitter.getDirectMessages();
				for(DirectMessage dm:directMessage){
					
					ContentValues messagesValues = TwitDataHelper.getValuesMessages(dm);
					twitDB.insertOrThrow("messages", null, messagesValues);
						//confirm we have new updates
					statusChanges = true;					
				}
				
			} catch (Exception e) {
				Log.e("DM_UPDATES", "Exception: " + e);
			}
			//if we have new updates, send a broadcast
			if (statusChanges) 
			{
					//this should be received in the main DM class
				sendBroadcast(new Intent("DM_UPDATES"));
			}			
			twitHandler.postDelayed(this, FETCH_DELAY);
		}
		
	}
	
	
	class SendMessageUpdapter implements Runnable
	{

		public void run() {

			boolean statusChanges = false;
			try {
				
				//list DM terkirim
				List<DirectMessage> sentDirectMessage = timelineTwitter.getSentDirectMessages();
				for (DirectMessage sentDM:sentDirectMessage) {
					
					ContentValues messagesValues = TwitDataHelper.getValuesMessages(sentDM);
					twitDB.insertOrThrow("messages", null, messagesValues);
					statusChanges = true;
					
				}
				
			} catch (Exception e) {
				Log.e("SEND_DM_UPDATES", "Exception: " + e);
			}
			//if we have new updates, send a broadcast
			if (statusChanges) 
			{
					//this should be received in the main DM class
				sendBroadcast(new Intent("SEND_DM_UPDATES"));
			}			
			twitHandler.postDelayed(this, FETCH_DELAY);
		}
		
	}	
	
	
}
