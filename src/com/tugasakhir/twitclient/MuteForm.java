package com.tugasakhir.twitclient;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MuteForm extends Activity implements OnClickListener{
	
	private MuteDataModel muteModel;
	private TwitDataHelper twitDataHelper;
	private SQLiteDatabase db;
	
	private AutoCompleteTextView username;

	private Twitter twitter;
	private SharedPreferences twitPrefs;	
	
	
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
		
		muteModel = new MuteDataModel(this);
		muteModel.open();
		
		username = (AutoCompleteTextView)findViewById(R.id.username);
		
		String[] friends  = getAllFriends();
        for(int i = 0; i < friends.length; i++)
        {
            Log.v(this.toString(), friends[i]);
        }		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, friends);
		username.setAdapter(adapter);
		
		Button save = (Button)findViewById(R.id.save);
		save.setOnClickListener(this);
		
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


	public void onClick(View v) {
		switch (v.getId()) {
			
		case R.id.save:
			
			String user_screen  = username.getText().toString();
			
			Cursor c = muteModel.getUserByName(user_screen);
			
			if(c.getCount() == 0){
				
				
				try {
					User user = twitter.showUser(user_screen);
					String usr_img = user.getProfileImageURL().toString();
					
					muteModel.insert(user_screen, usr_img);
					
				} catch (Exception e) {
					
				}
				
				finish();
			}else{
				Toast.makeText(this, "User Already Exist", Toast.LENGTH_LONG).show();	
			}
			
			
			break;
			
		default:
			break;			
		
		}
		
	}	
	

}
