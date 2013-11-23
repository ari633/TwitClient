package com.tugasakhir.twitclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import twitter4j.DirectMessage;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

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
		String userToken = twitPrefs.getString(Const.TOKEN, null);
		String userSecret = twitPrefs.getString(Const.TOKEN_SECRET, null);		
		
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

		
		ArrayList<HashMap<String, String>> messages = new ArrayList<HashMap<String,String>>();
		//TextView textview = new TextView(this);
		try {
			List<DirectMessage> directMessage =  dmTwitter.getDirectMessages();
			
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
					Toast.makeText(getApplicationContext(), "msg msg", Toast.LENGTH_SHORT).show();
				}
	        	
	        });			
			
			
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("LIST DM", "Exception: " + e);
		}
		
	}
}
