package com.tugasakhir.twitclient;

import com.tugasakhir.twitclient.TimelineService.FriendsUpdater;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

public class FriendRrefresh extends Activity{

	/**Twitter objext*/
	private Twitter twitter; 
	/**Database helper object*/
	private TwitDataHelper twitHelper;
	/**Timeline database*/
	private SQLiteDatabase twitDB;
	/**shared preferences for user details*/
	private SharedPreferences twitPrefs;
	private Handler twitHandler;	
	
	private ProgressDialog  progressBar;
	private int progressBarStatus = 0;	
	private Handler progressBarHandler = new Handler();
	
	
	private Cursor cursor;
	
	private int totalFriends = 0;
	private int totalSavedFriends=0;
	private int totalLoadFriends = 0;
	private String myScreenName;
	
	private FriendsUpdater friends;
	
	public void onCreate(Bundle savedInstanceState){
		 super.onCreate(savedInstanceState);
		 
			//get prefs
			twitPrefs = getSharedPreferences("TwitClientPrefs", 0);
			//get user preferences
			String userToken = twitPrefs.getString(Const.TOKEN, "");
			String userSecret = twitPrefs.getString(Const.TOKEN_SECRET, "");		
			
			//create new configuration
			//create new configuration
			Configuration twitConf = new ConfigurationBuilder()
			.setOAuthConsumerKey(Const.TWIT_KEY)
			.setOAuthConsumerSecret(Const.TWIT_SECRET)
			.setOAuthAccessToken(userToken)
			.setOAuthAccessTokenSecret(userSecret)
			.build();
			//instantiate new twitter
			twitter = new TwitterFactory(twitConf).getInstance();	
			
			//get db helper
			twitHelper = new TwitDataHelper(this);
			//get the database
			twitDB = twitHelper.getWritableDatabase();
			
			setContentView(R.layout.friend_refresh);
	}
	

	
	public void startProgress(View v) {
		twitDB = twitHelper.getWritableDatabase();
		twitDB.delete("following", null, null);
		
		twitDB = twitHelper.getReadableDatabase();
		cursor = twitDB.query("following", null, null, null, null, null, null);
		totalSavedFriends = cursor.getCount();
		
		try {
			User myProfile = twitter.verifyCredentials();
			totalFriends = myProfile.getFriendsCount();
			myScreenName = myProfile.getScreenName().toString();
		} catch (TwitterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		totalLoadFriends = totalFriends - totalSavedFriends;
		
		// prepare for a progress bar dialog
		progressBar = new ProgressDialog(v.getContext());
		progressBar.setCancelable(true);
		progressBar.setMessage("Refresh list of friends ...");
		progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressBar.setProgress(0);
		progressBar.setMax(totalLoadFriends);
		progressBar.show();
		
		//reset progress bar status
		progressBarStatus = 0;
		
		Runnable runnable = new FriendsUpdater();
		    
		    
		new Thread(runnable).start();
		    
		    
	}
	
	class FriendsUpdater implements Runnable{

		public void run() {
			long cursor = -1;
			IDs ids;
			
			try {

				do {
					
					ids = twitter.getFriendsIDs(myScreenName, cursor);
					int i = 1;
					for(long id: ids.getIDs()){
						
						final int value = i;
						
						User user = twitter.showUser(id);
						ContentValues followingValues = TwitDataHelper.getValuesFollowing(user);
						twitDB.insertOrThrow("following", null, followingValues);

						  
				           
						progressBarHandler.post(new Runnable() {
							
							public void run() {
								// TODO Auto-generated method stub
								progressBar.setProgress(value);
							}					
				           });
				           
				           if (value >= totalLoadFriends) {
							progressBar.dismiss();
				           
				           }							
						
						i++;
						
					}
					
				} while ((cursor = ids.getNextCursor()) != 0);
				twitDB.close();
			} catch (Exception e) {
				Log.e("Refreshed Friends", "Exception: " + e);
			}
		}
		
	}
	

	@Override
	protected void onDestroy() {
		super.onDestroy();	
		twitDB.close();
		//Runnable friends = new FriendsUpdater();
		//new Thread(friends).stop();
		//twitHandler.removeCallbacks(friends);
			
	}
	

}
