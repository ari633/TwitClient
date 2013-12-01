package com.tugasakhir.twitclient;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.library.time.MyTime;




public class ClientTweet extends Activity implements OnClickListener {
	
	private TweetModel model;
	
	private Dialog picker;
	private Button selectTime;
	private Button set;
	private TimePicker timep;
	private DatePicker datep;
	private Integer hour, minute, month, day, year;
	private TextView time, date;
	private EditText unixTime;
		/**shared preferences for user twitter details*/
	private SharedPreferences tweetPrefs; 
		/**twitter object**/
	private Twitter tweetTwitter; 

		/**the update ID for this tweet if it is a reply*/
	private long tweetID = 0;
		/**the username for the tweet if it is a reply*/
	private String tweetName = "";
	
	private String tweetText = "";
	private Button tweetButton;
	
	
	
	
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
	    model = new TweetModel(this);
	    model.open();		
		setupTweet();
	}
	
	protected void onDestroy(){
		super.onDestroy();
		 model.close();	
	}	

	/**
	 * Method called whenever this Activity starts
	 * - get ready to tweet
	 * Sets up twitter and onClick listeners
	 * - also sets up for replies
	 */
	private void setupTweet() {
		//get preferences for user twitter details
		tweetPrefs = getSharedPreferences("TwitClientPrefs", 0);
	    
	    	//get user token and secret for authentication
	    String userToken = tweetPrefs.getString(Const.TOKEN, null);
		String userSecret = tweetPrefs.getString(Const.TOKEN_SECRET, null);
		
		Configuration twitConf = new ConfigurationBuilder()
		.setOAuthConsumerKey(Const.TWIT_KEY)
		.setOAuthConsumerSecret(Const.TWIT_SECRET)
		.setOAuthAccessToken(userToken)
		.setOAuthAccessTokenSecret(userSecret)
		.build();
		//instantiate new twitter
		tweetTwitter = new TwitterFactory(twitConf).getInstance();
		
			//get any data passed to this intent for a reply
	    Bundle extras = getIntent().getExtras();
	    if(extras !=null)
	    {
	    		//if there are extras, they represent the tweet to reply to
	    	
	    		//get the ID of the tweet we are replying to
	    	tweetID = extras.getLong("tweetID");
	    		//get the user screen name for the tweet we are replying to
	    	tweetName = extras.getString("tweetUser");
	    		//get Tweet Text
	    	tweetText = extras.getString("tweetText");
	    	
	    	if(tweetText == null && tweetName != null){
	    		//get a reference to the text field for tweeting
		    	EditText theReply = (EditText)findViewById(R.id.tweettext);
		    		//start the tweet text for the reply @username
		    	theReply.setText("@"+tweetName+"  ");
		    		//set the cursor to the end of the text for entry
		    	theReply.setSelection(theReply.getText().length());
	    	}else if(tweetText != null){
		    	EditText theReply = (EditText)findViewById(R.id.tweettext);
	    			//start the tweet text for the reply @username
		    	theReply.setText("@"+tweetName+" "+tweetText);
		    		//set the cursor to the end of the text for entry
		    	//theReply.setSelection(theReply.getText().length());
	    	}
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
	    tweetButton = (Button)findViewById(R.id.dotweet);
		tweetButton.setOnClickListener(this);
		
			//set up listener for select time
		selectTime = (Button)findViewById(R.id.btnSelectTime);
		selectTime.setOnClickListener(this);
		
		time = (TextView)findViewById(R.id.textTime);
		date = (TextView)findViewById(R.id.textDate);
		unixTime = (EditText)findViewById(R.id.unixTime);
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
	    	String toTime = unixTime.getText().toString();
	    	
	    	
	    	try {
	    		if(toTime.length()<=0)
	    		{
		    		//is a reply
		    		if(tweetName.length()>0) {
		    			tweetTwitter.updateStatus(new StatusUpdate(toTweet).inReplyToStatusId(tweetID));
		    			Log.v("ClienTweet", "Melakukan Reply tweet");
		    		}
		    		//is a normal tweet
		    		else {
		    			tweetTwitter.updateStatus(toTweet);
		    			Log.v("ClienTweet", "Melakukan normal tweet");
		    		}
	    		}else{
	    			long toEpoch = Long.parseLong(toTime);
	    			long insert = model.insert(toTweet, toEpoch);
	    			int requestCode = (int)insert;
	    			
	    			Intent intentSc  = new Intent(this, ScheduleReciever.class);
	    			intentSc.putExtra("TweetTime", toEpoch);
	    			intentSc.putExtra("TweetText", toTweet);
	    			
	    			intentSc.putExtra("TweetName", tweetName);
	    			intentSc.putExtra("TweetID", tweetID);
	    			
	    			PendingIntent pi = PendingIntent.getBroadcast(this, requestCode, intentSc, PendingIntent.FLAG_UPDATE_CURRENT);
	    			
	    			AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE); 
	    			alarmManager.set(AlarmManager.RTC_WAKEUP, toEpoch, pi);
	    			
	    			
	    			
	    			Toast.makeText(this, "Schedule Tweet "+ DateUtils.getRelativeTimeSpanString(toEpoch) , Toast.LENGTH_LONG).show();
	    		}
	    			//reset the edit text
	    		tweetTxt.setText("");
	    			//go back to the home timeline
	    	}
	    	catch(TwitterException te) {
	    		Log.e("ClienTweet", te.getMessage()); 
	    	}
	    	
	    	finish();
			break;
			
		case R.id.btnSelectTime:
			
			picker = new Dialog(ClientTweet.this);
			picker.setContentView(R.layout.picker_frag);
			picker.setTitle("Select Date and Time");
			
			datep = (DatePicker)picker.findViewById(R.id.datePicker);
			timep = (TimePicker)picker.findViewById(R.id.timePicker1);
			set = (Button)picker.findViewById(R.id.btnSet);
			
			set.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					month = datep.getMonth()+1;
					day = datep.getDayOfMonth();
					year = datep.getYear();
					hour = timep.getCurrentHour();
					minute = timep.getCurrentMinute();
					
					String times = ""+month+"/"+day+"/"+year+" "+hour+":"+minute+":01";
					
					time.setText(""+hour+":"+minute+"");
					
					date.setText(""+month+"/"+day+"/"+year+"");
					
					long epochTime = MyTime.dateToEpoch(times);
					
					
					
					unixTime.setText(String.valueOf(epochTime));
					
					picker.dismiss();
				}
			});
			
			picker.show();
			
			
			break;
			
		case R.id.homebtn:
			//go to the home timeline, reset text first
			tweetTxt.setText("");
			finish();
			break;
		default:
			finish();
			break;
		}
		//finish to go back to home
		//finish();
	}
		
}
