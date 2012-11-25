package com.tugasakhir.twitclient;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;






public class ClientTweet extends Activity implements OnClickListener {
	
		/**shared preferences for user twitter details*/
	private SharedPreferences twitPrefs; 
		/**twitter object**/
	private Twitter twitClient; 

		/**the update ID for this tweet if it is a reply*/
	private long tweetID = 0;
		/**the username for the tweet if it is a reply*/
	private String tweetName = "";
	
	/*
	 * onCreate called when activity is created
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
	    super.onCreate(savedInstanceState);
	    	//set tweet layout
	    setContentView(R.layout.tweet);  
	}
	
	/*
	 * Call setup method when this activity starts
	 */
	@Override
	public void onResume() {
		super.onResume();
		setupTweet();
	}
	
	/**
	 * Method called whenever this Activity starts
	 * - get ready to tweet
	 * Sets up twitter and onClick listeners
	 * - also sets up for replies
	 */
	private void setupTweet() {
		//get preferences for user twitter details
		twitPrefs = getSharedPreferences("twitPrefs", 0);
	    
	    	//get user token and secret for authentication
	    String userToken = twitPrefs.getString("user_token", null);
		String userSecret = twitPrefs.getString("user_secret", null);
		
		Configuration twitConf = new ConfigurationBuilder()
		.setOAuthConsumerKey(Const.TWIT_KEY)
		.setOAuthConsumerSecret(Const.TWIT_SECRET)
		.setOAuthAccessToken(userToken)
		.setOAuthAccessTokenSecret(userSecret)
		.build();
		//instantiate new twitter
		twitClient = new TwitterFactory(twitConf).getInstance();
		
			//get any data passed to this intent for a reply
	    Bundle extras = getIntent().getExtras();
	    if(extras !=null)
	    {
	    		//if there are extras, they represent the tweet to reply to
	    	
	    		//get the ID of the tweet we are replying to
	    	tweetID = extras.getLong("tweetID");
	    		//get the user screen name for the tweet we are replying to
	    	tweetName = extras.getString("tweetUser");
	    		//get a reference to the text field for tweeting
	    	EditText theReply = (EditText)findViewById(R.id.tweettext);
	    		//start the tweet text for the reply @username
	    	theReply.setText("@"+tweetName+" ");
	    		//set the cursor to the end of the text for entry
	    	theReply.setSelection(theReply.getText().length());
	
	    }
	    else 
	    {
	    	EditText theReply = (EditText)findViewById(R.id.tweettext);
	    	theReply.setText("");
	    }
	    
	    	//set up listener for choosing home button to go to timeline
	    LinearLayout tweetClicker = (LinearLayout)findViewById(R.id.homebtn);
		tweetClicker.setOnClickListener(this);
	    
			//set up listener for send tweet button
		Button tweetButton = (Button)findViewById(R.id.dotweet);
		tweetButton.setOnClickListener(this);
		
	}
	
	/**
	 * Listener method for button clicks
	 * - for home button and send tweet button
	 */
	public void onClick(View v) {
		
		EditText tweetTxt = (EditText)findViewById(R.id.tweettext);
			//find out which view has been clicked
		switch(v.getId()) {
		case R.id.dotweet:
			//send tweet - get the text
	    	String toTweet = tweetTxt.getText().toString();
	    	try {
	    		//is a reply
	    		if(tweetName.length()>0) {
	    			twitClient.updateStatus(new StatusUpdate(toTweet).inReplyToStatusId(tweetID));
	    		}
	    		//is a normal tweet
	    		else {
	    			twitClient.updateStatus(toTweet);
	    		}
	    			//reset the edit text
	    		tweetTxt.setText("");
	    			//go back to the home timeline
	    	}
	    	catch(TwitterException te) { Log.e("NiceTweet", te.getMessage()); }
			break;
		case R.id.homebtn:
			//go to the home timeline, reset text first
			tweetTxt.setText("");
			break;
		default:
			break;
		}
		//finish to go back to home
		finish();
	}
		
}
