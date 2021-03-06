package com.tugasakhir.twitclient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class GroupUserForm extends Activity implements OnClickListener{
	
	private GroupDataModel groupModel;

	private AutoCompleteTextView username;
	private TwitDataHelper twitDataHelper;
	private SQLiteDatabase db;
	
	private Twitter twitter;
	private SharedPreferences twitPrefs;
	
	private long group_ID = 0;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_user_form);
		
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
		
		
		groupModel = new GroupDataModel(this);
		groupModel.open();
		
		Bundle extras = getIntent().getExtras();
		group_ID = extras.getLong("group_ID");
		
		username = (AutoCompleteTextView)findViewById(R.id.username);
		
		String[] friends  = getAllFriends();
	      // Print out the values to the log
        for(int i = 0; i < friends.length; i++)
        {
            Log.v(this.toString(), friends[i]);
        }		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, friends);
		username.setAdapter(adapter);
		
		Button save = (Button)findViewById(R.id.save);
		save.setOnClickListener(this);
	}

	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.save:
			
				String user_screen = username.getText().toString();
				Cursor c = groupModel.getUserByName(user_screen);
				c.moveToFirst();
				
				if(c.getCount() == 0){
					
					try {
						User user = twitter.showUser(user_screen);
						String usr_img = user.getProfileImageURL().toString();
						
						groupModel.insertUser(username.getText().toString(), group_ID, usr_img);				
						Toast.makeText(this, "Group Saved", Toast.LENGTH_LONG).show();	
						
					} catch (TwitterException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						
					}

					groupModel.close();
					finish();
					
				}else{
					Toast.makeText(this, "Group Already Exist", Toast.LENGTH_LONG).show();	
				}

				
				
			break;
			
		default:
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
            db.close();
            return str;
        }else{
        	 db.close();
        	return new String[] {};
        }
	}
	
	
	 public boolean onCreateOptionsMenu(Menu menu) {
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.adduser, menu);
	        return true;
	 }	
	
	 public boolean onOptionsItemSelected(MenuItem item) {
		 
	        switch (item.getItemId()) {	       
	        
	        case R.id.refresh:
	        	startActivity(new Intent(this, FriendRrefresh.class));
	        return true;
	        
	        default:
	        return super.onOptionsItemSelected(item);
	        
	        }
	 }	
	
	protected void onResume() {
		  super.onResume();		  		  
	}		
	
	protected void onDestroy(){
		super.onDestroy();
		groupModel.close();
	}	

}
