package com.tugasakhir.twitclient;

import com.tugasakhir.twitclient.FavoriteActivity.TwitterUpdateReceiver;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class GroupUserActivity extends Activity implements OnClickListener{
	
	private Twitter twitter;
	private SharedPreferences twitPrefs;
	private GroupDataModel groupModel;
	private TwitDataHelper timelineHelper;
	private SQLiteDatabase timelineDB;
	private UpdateAdapter timelineAdapter;
	
	private BroadcastReceiver twitStatusReceiver;
	
	private long group_ID = 0;
	
	private ListView listTimeline;
	private TextView title;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_user);
		
		//get twitter prefs
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
		
		
		
		Bundle extras = getIntent().getExtras();
		
		group_ID = extras.getLong("group_ID");
		
		//instantiate new twitter
		twitter = new TwitterFactory(twitConf).getInstance();	
		
		listTimeline = (ListView)findViewById(R.id.list);
		timelineHelper = new TwitDataHelper(this);
		timelineDB = timelineHelper.getReadableDatabase();	
		
		String tlQuery = "SELECT * FROM home WHERE user_screen IN "+"(SELECT user_screen FROM group_users WHERE id_group = "+ group_ID +" ORDER BY _id DESC) ORDER BY update_time DESC"; 
		
		Cursor timelineCursor = timelineDB.rawQuery(tlQuery, null);
		startManagingCursor(timelineCursor);
		timelineAdapter = new UpdateAdapter(this, timelineCursor);
		listTimeline.setAdapter(timelineAdapter);
		
		
		twitStatusReceiver = new TwitterUpdateReceiver();
		//register for updates
	    registerReceiver(twitStatusReceiver, new IntentFilter("USER_GROUP_UPDATES"));		

		
		groupModel = new GroupDataModel(this);
		groupModel.open();
		
		Cursor cGroup = groupModel.getById(group_ID);
		cGroup.moveToFirst();
		
		title = (TextView)findViewById(R.id.title);
		title.setText(groupModel.getGroupTitle(cGroup)+" Group");
		
	    LinearLayout homeClicker = (LinearLayout)findViewById(R.id.homebtn);
	    homeClicker.setOnClickListener(this);		
		
	    LinearLayout tweetClicker = (LinearLayout)findViewById(R.id.tweetbtn);
	    tweetClicker.setOnClickListener(this);	  
	    
	    Button add = (Button)findViewById(R.id.add);
	    
	    add.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {	
		case R.id.homebtn:
				finish();
		break;
		
		case R.id.tweetbtn:
			startActivity(new Intent(this, ClientTweet.class));
		break;
		
		case R.id.add:
			Intent i = new Intent(this, GroupUserForm.class);
			i.putExtra(GroupActivity.ID_EXTRA, group_ID);
			startActivity(i);
			//startActivity(new Intent(this, GroupUserForm.class));
		break;
		
	}
	}
	
	protected void onResume() {
		super.onResume();
		groupModel.open(); 
		  
	}		
	
	/**
	 * Class to implement broadcast receipt for new updates
	 */
	class TwitterUpdateReceiver extends BroadcastReceiver 
	{

		@Override
		public void onReceive(Context context, Intent intent) {			
			String tlQuery = "SELECT * FROM home WHERE user_screen IN "+"(SELECT user_screen FROM group_users ORDER BY _id DESC) ORDER BY update_time DESC"; 			
			Cursor timelineCursor = timelineDB.rawQuery(tlQuery, null);
			
			timelineAdapter = new UpdateAdapter(context, timelineCursor);
			listTimeline.setAdapter(timelineAdapter);	
		}
		
	}
	
}
