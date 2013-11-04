package com.tugasakhir.twitclient;

import java.io.InputStream;
import java.net.URL;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.library.imageloader.*;

/**
 * Apapter class binds the update data to the views in the user interface
 * - when new Twitter updates are received, they appear in the main timeline
 */
public class UpdateAdapter extends SimpleCursorAdapter{
	
	/**strings representing database column names to map to views*/
	static final String[] from = { "update_text", "user_screen", "update_time", "user_img" };
	/**view item IDs for mapping database record values to*/
	static final int[] to = { R.id.updateText, R.id.userScreen, R.id.updateTime, R.id.userImg };
	
	private String LOG_TAG = "UpdateAdapter";
	
	public ImageLoader imageLoader;
	
	int loader = R.drawable.ic_launcher;
	/**
	 * constructor sets up adapter, passing 'from' data and 'to' views
	 * @param context @
	 * @param c
	 */
	public UpdateAdapter(Context context, Cursor c) {
		super(context, R.layout.update, c, from, to);
		imageLoader=new ImageLoader(context.getApplicationContext());
	}
	
	/*
	 * Bind the data to the visible views
	 */
	
	public void bindView(View row, Context context, Cursor cursor){
		super.bindView(row, context, cursor);

		/*
		 * In this method we add any additional requirements we have 
		 * for binding the data to the views
		 * - we set the image, add tags for data, format the time created and setup click listeners
		 */
		
		/*
		try 
		{
			//get profile image
			URL profileURL = new URL(cursor.getString(cursor.getColumnIndex("user_img")));
			//set the image in the view for the current tweet
			ImageView profPic = (ImageView)row.findViewById(R.id.userImg);
			profPic.setImageDrawable(Drawable.createFromStream((InputStream)profileURL.getContent(), ""));
		}
		catch(Exception te) { Log.e(LOG_TAG, te.getMessage()); }
		*/
		String urlImage = cursor.getString(cursor.getColumnIndex("user_img"));
		ImageView profPic = (ImageView)row.findViewById(R.id.userImg);
		imageLoader.DisplayImage(urlImage, loader, profPic);

		//get the update time
		long createdAt = cursor.getLong(cursor.getColumnIndex("update_time"));
		//get the update time view
		TextView textCreatedAt = (TextView) row.findViewById(R.id.updateTime);
		//adjust the way the time is displayed to make it human-readable
		textCreatedAt.setText(DateUtils.getRelativeTimeSpanString(createdAt)+" ");

		/*
		 * For retweets and replies, we need to store the tweet ID and user screen name in the view
		 */

		//get the status ID
		long statusID = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
		//get the user name
		String statusName = cursor.getString(cursor.getColumnIndex("user_screen"));
		//get tweetText
		String statusText = cursor.getString(cursor.getColumnIndex("update_text"));
		//create a StatusData object to store these
		StatusData tweetData = new StatusData(statusID, statusName, statusText);

		//set the status data object as tag for both retweet and reply buttons in this view
		row.findViewById(R.id.retweet).setTag(tweetData);
		row.findViewById(R.id.reply).setTag(tweetData);
		row.findViewById(R.id.quote).setTag(tweetData);
		//setup onclick listeners for the retweet and reply buttons
		row.findViewById(R.id.retweet).setOnClickListener(tweetListener);
		row.findViewById(R.id.reply).setOnClickListener(tweetListener);
		row.findViewById(R.id.quote).setOnClickListener(tweetListener);
		//setup  onclick for the user screen name within the tweet
		row.findViewById(R.id.userScreen).setOnClickListener(tweetListener);		
		
	}
	
	

	/**
	 * tweetListener handles clicks of reply and retweet buttons
	 * - also handles clicking the user name within a tweet
	 */
	private OnClickListener tweetListener = new OnClickListener() {
		//onClick method
		public void onClick(View v) {
			
			//get the data from the tag within the button view
			StatusData theData = (StatusData)v.getTag();
			
			//which view
			switch(v.getId()) {
			//reply button pressed
			case R.id.reply:
				//create an intent for sending a new tweet
				Intent replyIntent = new Intent(v.getContext(), ClientTweet.class);

				//pass the status ID
				replyIntent.putExtra("tweetID", theData.getID());
				//pass the user name
				replyIntent.putExtra("tweetUser", theData.getUser());
				//go to the tweet screen
				v.getContext().startActivity(replyIntent);
				break;
			
			case R.id.quote:
				Intent quoteIntent = new Intent(v.getContext(), ClientTweet.class);	
				quoteIntent.putExtra("tweetUser", theData.getUser());
				quoteIntent.putExtra("tweetText", theData.getText());
				v.getContext().startActivity(quoteIntent);
				break;
				
				//retweet button pressed
			case R.id.retweet:
				//get context
				Context appCont = v.getContext();
				//get preferences for user access
				SharedPreferences tweetPrefs = appCont.getSharedPreferences("TwitClientPrefs", 0);
				String userToken = tweetPrefs.getString("user_token", null);
				String userSecret = tweetPrefs.getString("user_secret", null);
				//create new Twitter configuration
				Configuration twitConf = new ConfigurationBuilder()
				.setOAuthConsumerKey(Const.TWIT_KEY)
				.setOAuthConsumerSecret(Const.TWIT_SECRET)
				.setOAuthAccessToken(userToken)
				.setOAuthAccessTokenSecret(userSecret)
				.build();
				//create Twitter instance for retweeting
				Twitter retweetTwitter = new TwitterFactory(twitConf).getInstance();
				//get tweet data from view tag
				StatusData tweetData = (StatusData)v.getTag();
				try 
				{
					//retweet, passing the status ID from the tag
					retweetTwitter.retweetStatus(tweetData.getID());
					//confirm to use
					CharSequence text = "Retweeted!";
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(appCont, text, duration);
					toast.show();
				}
				catch(TwitterException te) {Log.e(LOG_TAG, te.getMessage());}
				break;
				//user has pressed tweet user name
			case R.id.userScreen:
				/*
				 * When the user presses the user name within a tweet they are taken 
				 * to the user's profile page in the browser.
				 */
				//get the user screen name
				TextView tv = (TextView)v.findViewById(R.id.userScreen);
				String userScreenName = tv.getText().toString();
				//open the user's profile page in the browser
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, 
						Uri.parse("http://twitter.com/"+userScreenName));
				v.getContext().startActivity(browserIntent);
				break;
			default:
				break;
			}
		}
	};

}
