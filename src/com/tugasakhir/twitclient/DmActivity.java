package com.tugasakhir.twitclient;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class DmActivity extends Activity implements OnClickListener{

	private String LOG_TAG = "DmActivity";
	private Twitter dmTwitter;
	private SharedPreferences twitPrefs;
	private ListView listMessage;
	private TwitDataHelper dmHelper;
	private SQLiteDatabase dmDB;
	private Cursor dmCursor;
	private DmAdapter dmAdapter;	
	
	private BroadcastReceiver dmReceiver;
	
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.v(LOG_TAG, "settings up DM");
		setContentView(R.layout.message);
		
	    Button add = (Button)findViewById(R.id.compose);
	    add.setOnClickListener(this);		
		//get twitter prefs
		twitPrefs = getSharedPreferences("TwitClientPrefs", 0);
		//get user preferences
		String userToken = twitPrefs.getString(Const.TOKEN, "");
		String userSecret = twitPrefs.getString(Const.TOKEN_SECRET, "");		
		//create new configuration
		Configuration twitConf = new ConfigurationBuilder()
		.setOAuthConsumerKey(Const.TWIT_KEY)
		.setOAuthConsumerSecret(Const.TWIT_SECRET)
		.setOAuthAccessToken(userToken)
		.setOAuthAccessTokenSecret(userSecret)
		.build();
		//instantiate new twitter
		dmTwitter = new TwitterFactory(twitConf).getInstance();		
		
		try {
			User user = dmTwitter.verifyCredentials();
			String myName = user.getScreenName().toString();
			listMessage = (ListView)findViewById(R.id.messageList);
			
			dmHelper = new TwitDataHelper(this);
			dmDB = dmHelper.getReadableDatabase();
			dmCursor = dmDB.query("messages", null, "sender_name != '"+myName+"' ", null, "sender_id", null, "update_time DESC");
			
			startManagingCursor(dmCursor);
			
			dmAdapter = new DmAdapter(this, dmCursor);
			/*
			 * Apply DM Adapter to DM View
			 */
			listMessage.setAdapter(dmAdapter);
			
			dmReceiver = new DmUpdateReceiver();
			registerReceiver(dmReceiver, new IntentFilter("DM_UPDATES"));	
			
		} catch (Exception e) {
			Log.e(LOG_TAG, "Failed to fetch DM: "+e.getMessage());
		}
		
	}
	
	class DmUpdateReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {	
			Log.v(LOG_TAG, "broadcast DM");
			try {
				User user = dmTwitter.verifyCredentials();
				String myName = user.getScreenName().toString();			
				dmCursor = dmDB.query("messages", null, "sender_name != '"+myName+"' ", null, "sender_id", null, "update_time DESC");
				startManagingCursor(dmCursor);
				dmAdapter = new DmAdapter(context, dmCursor);
				listMessage.setAdapter(dmAdapter);				
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				Log.e(LOG_TAG, "Failed to fetch BroadcastDM: "+e.getMessage());
			}
			
		}
		
	}
	

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {	
		
		case R.id.compose:
			startActivity(new Intent(this, DmComposeActivity.class));			
		break;
		
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
			stopService(new Intent(this, TimelineService.class));
			unregisterReceiver(dmReceiver);
			dmDB.close();
		}
		catch(Exception se) { Log.e(LOG_TAG, "unable to stop service or receiver"); }
	}

	
}
