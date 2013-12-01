package com.tugasakhir.twitclient;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class ScheduleReciever extends BroadcastReceiver{

	private SharedPreferences tweetPrefs;
	private Twitter tweetTwitter;
	private TweetModel tweetModel;
	

	
	@Override
	public void onReceive(Context context, Intent intent) {

		tweetPrefs = context.getSharedPreferences("TwitClientPrefs", 0);	
		Bundle extra = intent.getExtras();
		
	    String userToken = tweetPrefs.getString(Const.TOKEN, null);
		String userSecret = tweetPrefs.getString(Const.TOKEN_SECRET, null);
		
		Configuration twitConf = new ConfigurationBuilder()
		.setOAuthConsumerKey(Const.TWIT_KEY)
		.setOAuthConsumerSecret(Const.TWIT_SECRET)
		.setOAuthAccessToken(userToken)
		.setOAuthAccessTokenSecret(userSecret)
		.build();
		
		tweetTwitter = new TwitterFactory(twitConf).getInstance();
		
		tweetModel = new TweetModel(context);
		tweetModel.open();		
		
		long time = extra.getLong("TweetTime");
		String tweetText = extra.getString("TweetText");
		String tweetName = extra.getString("TweetName");
		long tweetID = extra.getLong("TweetID");
		

		try {
			
			//is a reply tweet
			if(tweetName.length()>0) {
				tweetTwitter.updateStatus(new StatusUpdate(tweetText).inReplyToStatusId(tweetID));
				
				Log.v("ScheduleTweet", "Schedule Reply Tweet Send "+tweetText);
				
			}else{
				//a normat tweet
				tweetTwitter.updateStatus(tweetText);
				Log.v("ScheduleTweet Reply", "Schedule Update Tweet Send "+tweetText);
			}
			
		} catch (Exception e) {
			Log.e("ScheduleReceiver", e.getMessage()); 
		}

		tweetModel.deleteByTime(time);
		
		Log.v("ScheduleTweet", "Schedule Tweet Send "+tweetText);
        Toast.makeText(context, "Schedule Tweet Send "+tweetText, Toast.LENGTH_LONG).show();	
        
        tweetModel.close();
	}


	
}

