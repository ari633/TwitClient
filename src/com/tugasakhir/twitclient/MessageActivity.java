package com.tugasakhir.twitclient;

import java.util.List;

import twitter4j.DirectMessage;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

public class MessageActivity extends Activity{
	
	private Twitter dmTwitter;

	private SharedPreferences twitPrefs;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message);
		//get prefs
		twitPrefs = getSharedPreferences("TwitClientPrefs", 0);

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
		dmTwitter = new TwitterFactory(twitConf).getInstance();		
		
		TextView textview = new TextView(this);
		try {
			List<DirectMessage> directMessage =  dmTwitter.getDirectMessages();
			
			for(DirectMessage dm:directMessage){
				String sender = dm.getSenderScreenName();
				String text = dm.getText();
				
				//textview.append(text+"  Sender: "+sender);
			}
			
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setContentView(textview);
		

		/*
		TextView textview = new TextView(this);
		textview.setText("Underconstruction");
		setContentView(textview);
		*/
	}
}
