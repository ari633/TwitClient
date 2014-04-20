package com.tugasakhir.twitclient;



import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DmConversationActivity extends Activity implements OnClickListener{
	
	private String LOG_TAG = "DmConversationActivity";
	private Twitter dmTwitter;
	private SharedPreferences twitPrefs;
	private ListView list;
	private TwitDataHelper dmHelper;
	private SQLiteDatabase dmDB;
	private Cursor dmCursor;
	private DmAdapter dmAdapter;	
	
	private String senderName = "";
	private long senderID = 0;
	
	private String myName = "";
	
	private BroadcastReceiver dmReceiver;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);		
		Log.v(LOG_TAG, "Show Conversations");		
		setContentView(R.layout.dm_conversation);
		list = (ListView)findViewById(R.id.listConversation);
		//get prefs
		twitPrefs = getSharedPreferences("TwitClientPrefs", 0);
		//get user preferences
		String userToken = twitPrefs.getString(Const.TOKEN, null);
		String userSecret = twitPrefs.getString(Const.TOKEN_SECRET, null);		
		//create new configuration
		Configuration twitConf = new ConfigurationBuilder()
		.setOAuthConsumerKey(Const.TWIT_KEY)
		.setOAuthConsumerSecret(Const.TWIT_SECRET)
		.setOAuthAccessToken(userToken)
		.setOAuthAccessTokenSecret(userSecret)
		.build();
		//instantiate new twitter
		dmTwitter = new TwitterFactory(twitConf).getInstance();		
		
		
		try {
			
			Bundle extras = getIntent().getExtras();
			//get pengirim id
			senderName = extras.getString("namaPengirim");		
			senderID = extras.getLong("pengirimID");
			//get my user details
			User user = dmTwitter.verifyCredentials();
			myName = user.getScreenName().toString();
			
			TextView screenName = (TextView)findViewById(R.id.userScreen);
			screenName.setText("@"+senderName);
			
			//Select DM 
			String selectQuery = "SELECT * FROM messages WHERE sender_name IN ('"+myName+"','"+senderName+"') AND recipient_name  IN ('"+myName+"','"+senderName+"') ORDER BY update_time DESC";
			dmHelper = new TwitDataHelper(this);
			dmDB = dmHelper.getReadableDatabase();
		
			dmCursor = dmDB.rawQuery(selectQuery, null);//dmDB.query("messages", null, null, null, "sender_id", null, "update_time DESC");			
			startManagingCursor(dmCursor);						
			
			dmAdapter = new DmAdapter(this, dmCursor);
			/*
			 * Apply DM Adapter to DM View
			 */
			list.setAdapter(dmAdapter);
			
			dmReceiver = new DmConversationReceiver();
			registerReceiver(dmReceiver, new IntentFilter("SEND_DM_UPDATES"));				
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	    LinearLayout homeClicker = (LinearLayout)findViewById(R.id.homebtn);
	    homeClicker.setOnClickListener(this);
	    
	    Button replyButton = (Button)findViewById(R.id.reply);
		replyButton.setOnClickListener(this);
		
	};
	
	public void onClick(View v) {
		
		EditText replyTxt = (EditText)findViewById(R.id.textReplyDM);
		
		switch (v.getId()) {
		
		case R.id.reply:
			
			String toReply = replyTxt.getText().toString();
			try {
				
				
				dmTwitter.sendDirectMessage(senderID, toReply);
				
				Toast.makeText(v.getContext(), "Sent", Toast.LENGTH_SHORT).show();
				replyTxt.setText("");				
				
			} catch (Exception e) {

			}
						
			break;
		
		case R.id.homebtn:
			
			replyTxt.setText("");
			
			break;

		default:
			break;
		}
		
		finish();
		
	}
	
	class DmConversationReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.v(LOG_TAG, "broadcast Conversation DM");
			try {
				Bundle extras = getIntent().getExtras();
				//get pengirim id
				senderName = extras.getString("namaPengirim");		
				senderID = extras.getLong("pengirimID");
				//get my user details
				User user = dmTwitter.verifyCredentials();
				myName = user.getScreenName().toString();
				
				String selectQuery = "SELECT * FROM messages WHERE sender_name IN ('"+myName+"','"+senderName+"') AND recipient_name  IN ('"+myName+"','"+senderName+"') ORDER BY update_time DESC";
				dmCursor = dmDB.rawQuery(selectQuery, null);		
				startManagingCursor(dmCursor);						
				
				dmAdapter = new DmAdapter(context, dmCursor);				
				list.setAdapter(dmAdapter);
				
			} catch (Exception e) {
				Log.e(LOG_TAG, "Failed to fetch Conversation BroadcastDM: "+e.getMessage());
			}
		}
		
	}
	
	public void onDestroy() {
		super.onDestroy();
		try 
		{
			stopService(new Intent(this, DmActivity.class));
			unregisterReceiver(dmReceiver);
			dmDB.close();
		}
		catch(Exception se) { Log.e(LOG_TAG, "unable to stop service or receiver"); }
	}	
	
}
