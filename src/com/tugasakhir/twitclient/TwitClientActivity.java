package com.tugasakhir.twitclient;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.util.Log;
import twitter4j.ProfileImage; 
import android.database.Cursor; 
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase; 
import android.widget.ListView; 
import android.widget.LinearLayout; 

public class TwitClientActivity extends Activity implements OnClickListener {
	

	/**app url*/
	public final static String TWIT_URL = "tclient-android:///"; 
	//for error logging 
	private String LOG_TAG = "TwitClientActivity";//alter for your Activity name 
	
	/**Twitter instance*/
	private Twitter twitClient; 
	/**request token for accessing user account*/
	private RequestToken twitRequestToken; 
	/**shared preferences to store user details*/
	private SharedPreferences twitPrefs; 	
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
	
	

	//set the profile image display
	ProfileImage.ImageSize imageSize = ProfileImage.NORMAL;	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        twitPrefs = getSharedPreferences("TwitClientPrefs", 0); 
        
        //find out if the user preferences are set 
        if(twitPrefs.getString("user_token", null)==null) { 
        	
        	//no user preferences so prompt to sign in 
        	setContentView(R.layout.main);
        	//get a twitter instance for authentication
        	twitClient = new TwitterFactory().getInstance();
        	
        	//pass developer key and scret
        	twitClient.setOAuthConsumer(Const.TWIT_KEY, Const.TWIT_SECRET);
        	
        	
            try 
            {
            		//get authentication request token
            	twitRequestToken = twitClient.getOAuthRequestToken(TWIT_URL);
            	
            }
            catch(TwitterException te) { Log.e(LOG_TAG, "TE "+te.getMessage()); }
        	//setup button for click listener
        	Button signIn = (Button)findViewById(R.id.signin);
        	signIn.setOnClickListener(this);
        	
        }else{
        	//user preferences are set - get timeline 
        	setupTimeline(); 
        }
        
    }

    //to handle sign in tweet button press
	public void onClick(View v) {
		
		//find view
		switch (v.getId()) {
		
		//jika button sign in id di tekan
		case R.id.signin: 
			//take user to twitter authentication web page to allow app access to their twitter account 
			String authURL = twitRequestToken.getAuthenticationURL();
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authURL)));
			break;

		default:
			break;
		}
		
	}
	
	/* 
	 * onNewIntent fires when user returns from Twitter authentication Web page 
	 */
	protected void onNewIntent(Intent intent) {
	    	
	        super.onNewIntent(intent);
	        //get the retrieved data
	    	Uri twitURI = intent.getData();
	    	//make sure the url is correct
	    	if(twitURI!=null && twitURI.toString().startsWith(TWIT_URL)) 
	    	{
	    		//is verification - get the returned data
		    	String oaVerifier = twitURI.getQueryParameter("oauth_verifier");
		    	//attempt to retrieve access token
		   	    try
		   	    {
		    	        //try to get an access token using the returned data from the verification page
		   	    	AccessToken accToken = twitClient.getOAuthAccessToken(twitRequestToken, oaVerifier);
		   	    		//add the token and secret to shared prefs for future reference
		   	        twitPrefs.edit()
		   	            .putString("user_token", accToken.getToken())
		   	            .putString("user_secret", accToken.getTokenSecret())
		   	            .commit();
		    	        //display the timeline
		   	        setupTimeline();
		    	        
		   	    }
		   	    catch (TwitterException te)
		   	    { Log.e(LOG_TAG, "Failed to get access token: "+te.getMessage()); }
	    	}
	    } 
	
	
	private void setupTimeline(){
		Log.v(LOG_TAG, "settings up timeline");
		
		setContentView(R.layout.timeline);
		
		try {
			//get reference to the list view
		homeTimeline = (ListView)findViewById(R.id.homeList);
			//instantiate database helper
		timelineHelper = new TwitDataHelper(this);
			//get the database
		timelineDB = timelineHelper.getReadableDatabase();	
		
	    //query the database, most recent tweets first
		timelineCursor = timelineDB.query("home", null, null, null, null, null, "update_time DESC");
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
		this.getApplicationContext().startService(new Intent(this.getApplicationContext(), TimelineService.class));
		
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
			if(DatabaseUtils.queryNumEntries(timelineDB, "home")>rowLimit) {
				String deleteQuery = "DELETE FROM home WHERE "+BaseColumns._ID+" NOT IN " +
						"(SELECT "+BaseColumns._ID+" FROM home ORDER BY "+"update_time DESC " +
								"limit "+rowLimit+")";	
				timelineDB.execSQL(deleteQuery);
			}	
			timelineCursor = timelineDB.query("home", null, null, null, null, null, "update_time DESC");
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
