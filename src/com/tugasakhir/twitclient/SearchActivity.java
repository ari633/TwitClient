package com.tugasakhir.twitclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import twitter4j.DirectMessage;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class SearchActivity extends Activity implements OnClickListener{

	private Twitter twitter;
	private SharedPreferences twitPrefs;
	
	private Button btnsearch;
	private EditText getQuery;
	private ProgressDialog progressDialog;
	
	public static final String USER_SCREEN = "userScreen";
	public static final String TEXT = "text";
	public static final String TIME = "time";
	public static final String PROFILE_IMG = "profile_img";
	
	
	
	public void onCreate(Bundle savedInstanceState){
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchform);
		
		twitPrefs = getSharedPreferences("TwitClientPrefs", 0);
		String userToken = twitPrefs.getString(Const.TOKEN, null);
		String userSecret = twitPrefs.getString(Const.TOKEN_SECRET, null);	
		
		Configuration twitConf = new ConfigurationBuilder()
		.setOAuthConsumerKey(Const.TWIT_KEY)
		.setOAuthConsumerSecret(Const.TWIT_SECRET)
		.setOAuthAccessToken(userToken)
		.setOAuthAccessTokenSecret(userSecret)
		.build();
		
		twitter = new TwitterFactory(twitConf).getInstance();	
		
		
		btnsearch = (Button)findViewById(R.id.btnsearch);
		btnsearch.setOnClickListener(this);
		
	}

	public void onClick(View v) {
		
		
		switch (v.getId()) {
		
		case R.id.btnsearch:
			
			getQuery = (EditText)findViewById(R.id.query);
			String setQuery = getQuery.getText().toString();
			
			ArrayList<HashMap<String, String>> resultSearch = new ArrayList<HashMap<String,String>>();
			
			try {
				
				Query query = new Query(setQuery);
				QueryResult result;
				
				//do{
					result = twitter.search(query);
					List<Status> tweets = result.getTweets();
					
					for(Status tweet : tweets){
						
						String screenName = tweet.getUser().getScreenName();
						String text = tweet.getText();
						String profileImg = tweet.getUser().getProfileImageURL().toString();
						long createdAt = tweet.getCreatedAt().getTime();
						
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(USER_SCREEN, screenName);
						map.put(TEXT, text);
						map.put(PROFILE_IMG, profileImg);
						map.put(TIME, (String)DateUtils.getRelativeTimeSpanString(createdAt));
						
						resultSearch.add(map);						
						
						//Log.v("TweetResult", tweet.getUser().getScreenName() + " - " + tweet.getText());
					}
										
				//}while((query = result.nextQuery()) != null);
				
				
				ListView list = (ListView)findViewById(R.id.listSearch);
				SearchAdapter adapter = new SearchAdapter(this, resultSearch);
				list.setAdapter(adapter);
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("Search", "Exception: " + e);
			}
			
			break;

		default:
			break;
		}
	}
	
}
