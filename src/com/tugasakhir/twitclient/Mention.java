package com.tugasakhir.twitclient;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

public class Mention extends Activity{
	
	private Twitter timelineTwitter; 
	private SharedPreferences twitPrefs;
	
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		/**
		 * Config
		 */
		//get preferences for user twitter details
		twitPrefs = getSharedPreferences("TwitClientPrefs", 0);
		//get user preferences
		String userToken = twitPrefs.getString("user_token", null);
		String userSecret = twitPrefs.getString("user_secret", null);		
		
		//create new configuration
		Configuration twitConf = new ConfigurationBuilder()
		.setOAuthConsumerKey(Const.TWIT_KEY)
		.setOAuthConsumerSecret(Const.TWIT_SECRET)
		.setOAuthAccessToken(userToken)
		.setOAuthAccessTokenSecret(userSecret)
		.build();
		//instantiate new twitter
		timelineTwitter = new TwitterFactory(twitConf).getInstance();

		TextView textview = new TextView(this);
		
		
		textview.setText("Mentions test, Lorem ipsum dolor sit amet");
		setContentView(textview);
		
	}
	
}
