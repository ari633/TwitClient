package com.tugasakhir.twitclient;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import twitter4j.DirectMessage;
import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class MessageActivity extends Activity{
	
	private Twitter dmTwitter;

	private SharedPreferences twitPrefs;
	
	public static final String USER_SCREEN = "userScreen";
	public static final String MESSAGE_TEXT = "messageText";
	public static final String TIME = "time";
	public static final String PROFILE_IMG = "profile_img";
	
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
		Paging paging = new Paging(1, 100);
		
		ArrayList<HashMap<String, String>> messages = new ArrayList<HashMap<String,String>>();
		//TextView textview = new TextView(this);
		try {
			List<DirectMessage> directMessage =  dmTwitter.getDirectMessages(paging);
			
			for(DirectMessage dm:directMessage){
				String sender = dm.getSenderScreenName();
				String text = dm.getText();
				String profileImg = dm.getSender().getProfileImageURL().toString();
				long createdAt = dm.getCreatedAt().getTime();
				
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(USER_SCREEN, sender);
				map.put(MESSAGE_TEXT, text);
				map.put(PROFILE_IMG, profileImg);
				map.put(TIME, (String)DateUtils.getRelativeTimeSpanString(createdAt));
				
				messages.add(map);
				//textview.append(text+"  Sender: "+sender+"\n \n");
			}
			

			ListView list = (ListView)findViewById(R.id.messageList);
			MessageAdapter adapter = new MessageAdapter(this, messages);	
			list.setAdapter(adapter);
			
	        // Click event for single list row
	        list.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// TODO Auto-generated method stub
					
				}
	        	
	        });			
			
			
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//setContentView(textview);
		

		/*
		TextView textview = new TextView(this);
		textview.setText("Underconstruction");
		setContentView(textview);
		*/
	}
}
