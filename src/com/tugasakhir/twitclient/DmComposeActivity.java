package com.tugasakhir.twitclient;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DmComposeActivity extends Activity implements OnClickListener{
	
	private TwitDataHelper twitDataHelper;
	private SQLiteDatabase db;
	
	private AutoCompleteTextView username;

	private Twitter twitter;
	private SharedPreferences twitPrefs;		
	
	private EditText textMessage;
	private String user_screen;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dm_compose);
		
		twitPrefs = getSharedPreferences("TwitClientPrefs", 0);
		//get user preferences
		String userToken = twitPrefs.getString(Const.TOKEN, null);
		String userSecret = twitPrefs.getString(Const.TOKEN_SECRET, null);
		
		Configuration twitConf = new ConfigurationBuilder()
		.setOAuthConsumerKey(Const.TWIT_KEY)
		.setOAuthConsumerSecret(Const.TWIT_SECRET)
		.setOAuthAccessToken(userToken)
		.setOAuthAccessTokenSecret(userSecret)
		.build();
		
		twitter = new TwitterFactory(twitConf).getInstance();		
		
		
		username = (AutoCompleteTextView)findViewById(R.id.username);
		
		String[] friends  = getAllFriends();
	      // Print out the values to the log
        for(int i = 0; i < friends.length; i++)
        {
            Log.v(this.toString(), friends[i]);
        }		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, friends);
		username.setAdapter(adapter);	
		
		textMessage = (EditText)findViewById(R.id.composedm);
	    	
		Button save = (Button)findViewById(R.id.send);
		save.setOnClickListener(this);
		
		
	}
	
	
	
	public void onClick(View v) {
		
		user_screen = username.getText().toString();
		switch (v.getId()) {
			case R.id.send:
			
		
				try {
					twitter.sendDirectMessage(user_screen, textMessage.getText().toString());
					
					Toast.makeText(v.getContext(), "Direct Message sent to "+user_screen , Toast.LENGTH_SHORT).show();
					textMessage.setText("");
					
					finish();
									
				} catch (TwitterException e) {
					Log.e("SEND_DM", "gagal ngirim DM: "+e.getMessage());
					
					if(user_screen.trim().equals("")){			
						username.setError("Username cannot be blank.");
					}
					
					if(textMessage.getText().toString().trim().equals("")){
						textMessage.setError("Text Message cannot blank");
					}
					
				}
				
			break;
		}
		
		
	}
	
	
	
	public String[] getAllFriends(){
		twitDataHelper = new TwitDataHelper(this); 
		db = twitDataHelper.getReadableDatabase();
		Cursor cursor = db.query("following", null, null, null, null, null, "_id DESC");
        if(cursor.getCount() >0)
        {
            String[] str = new String[cursor.getCount()];
            int i = 0;
 
            while (cursor.moveToNext())
            {
                 str[i] = cursor.getString(cursor.getColumnIndex("user_screen"));
                 i++;
             }
            return str;
        }else{
        	return new String[] {};
        }
	}	
	
}
