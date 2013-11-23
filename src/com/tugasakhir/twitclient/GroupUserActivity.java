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
import android.widget.Toast;

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
		String userToken = twitPrefs.getString(Const.TOKEN, "");
		String userSecret = twitPrefs.getString(Const.TOKEN_SECRET, "");		
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
		title.setText("Timeline "+ groupModel.getGroupTitle(cGroup)+" Group");
		
	    LinearLayout homeClicker = (LinearLayout)findViewById(R.id.homebtn);
	    homeClicker.setOnClickListener(this);		
		
	    LinearLayout tweetClicker = (LinearLayout)findViewById(R.id.tweetbtn);
	    tweetClicker.setOnClickListener(this);	  
	    
	    Button add = (Button)findViewById(R.id.add);
	    Button delete_group = (Button)findViewById(R.id.delete_group);
	    Button list_user = (Button)findViewById(R.id.list_user);
	    
	    add.setOnClickListener(this);
	    delete_group.setOnClickListener(this);
	    list_user.setOnClickListener(this);
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
		
		case R.id.delete_group:
			groupModel.deleteGroup(group_ID);
			Toast.makeText(this, "Delete Group", Toast.LENGTH_LONG).show();
			finish();
		break;
		
		case R.id.list_user:
			Intent intent = new Intent(this, GroupListUser.class);
			intent.putExtra(GroupActivity.ID_EXTRA, group_ID);
			startActivity(intent);
			Toast.makeText(this, "List User", Toast.LENGTH_LONG).show();
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
