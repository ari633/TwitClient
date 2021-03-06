package com.tugasakhir.twitclient;

import com.newrelic.agent.android.NewRelic;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;

public class Main extends TabActivity implements OnClickListener {

	/**app url*/
	public final static String TWIT_URL = "tclient-android:///"; 
	//for error logging 
	private String LOG_TAG = "TwitClientActivity";//Log
	
	/**Twitter instance*/
	private Twitter twitClient; 
	/**request token for accessing user account*/
	private RequestToken twitRequestToken; 
	/**shared preferences to store user details*/
	private SharedPreferences twitPrefs; 	
	
	/**database helper*/
	private TwitDataHelper timelineHelper;
	
	private ProgressDialog progressDialog;
	
	protected void onCreate(Bundle savedInstanceState) {
				
		super.onCreate(savedInstanceState);
		
		/*NewRelic.withApplicationToken(
				"AA00039a769c2cc1596b3e3f99907e4f0fcd7fcd8d"
				).start(this.getApplication());*/		
		
        twitPrefs = getSharedPreferences("TwitClientPrefs", 0); 
        
        //mengetahui apakah preferensi pengguna ditetapkan
        if(twitPrefs.getString(Const.TOKEN, null)==null) { 
        	
        	//Full Screen Activity
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        	
        	//tidak ada preferensi pengguna sehingga meminta untuk masuk 
        	setContentView(R.layout.loginform);
        	//mendapatkan twitter untuk otentikasi
        	twitClient = new TwitterFactory().getInstance();
        	
        	//pass developer key and scret
        	twitClient.setOAuthConsumer(Const.TWIT_KEY, Const.TWIT_SECRET);
        	
        	
            try 
            {
            		//mendapatkan token otentikasi
            	twitRequestToken = twitClient.getOAuthRequestToken(TWIT_URL);
            	
            }
            catch(TwitterException te) { Log.e(LOG_TAG, "TE "+te.getMessage()); }
        	//setup button for click listener
        	Button signIn = (Button)findViewById(R.id.signin);
        	signIn.setOnClickListener(this);

        	
        }else{
        
        	startTimeLine();
        	
        }
		
		
	}
	

	public void onClick(View v) {
		// TODO Auto-generated method stub
		//find view
		switch (v.getId()) {		
			//jika button sign in id di tekan
			case R.id.signin: 
			//take user to twitter authentication web page to allow app access to their twitter account 
			String authURL = twitRequestToken.getAuthorizationURL();
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authURL)));
			break;
			
    		case R.id.tweetbtn:
    			//launch tweet activity
    		startActivity(new Intent(this, ClientTweet.class));
    			//Log.v(LOG_TAG, "Tweet Button di pencet");
    		break;
		default:
			break;
		}
	}
	
	
	 public boolean onCreateOptionsMenu(Menu menu) {
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.main, menu);
	        return true;
	 }
	 
	 
 
	 
	
	/* 
	 * onNewIntent fires when user returns from Twitter authentication Web page 
	 */
	protected void onNewIntent(Intent intent) {
	    	
	        super.onNewIntent(intent);
	        //get the retrieved data
	    	Uri twitURI = intent.getData();
	    	//make sure the url is correct
	    	if(twitURI!=null && twitURI.toString().startsWith(TWIT_URL)) 
	    	{
	    		//is verification - get the returned data
		    	String oaVerifier = twitURI.getQueryParameter("oauth_verifier");
		    	//attempt to retrieve access token
		   	    try
		   	    {
		    	        //try to get an access token using the returned data from the verification page
		   	    	AccessToken accToken = twitClient.getOAuthAccessToken(twitRequestToken, oaVerifier);
		   	    		//add the token and secret to shared prefs for future reference
		   	        twitPrefs.edit()
		   	        	.putString(Const.TOKEN, accToken.getToken())
		   	            .putString(Const.TOKEN_SECRET, accToken.getTokenSecret())
		   	            .commit();
		    	        //display the timeline
		   	        	Log.e(LOG_TAG, "Starting Timeline");
		   	        	startTimeLine();
		    	
		   	    }
		   	    catch (TwitterException te)
		   	    { Log.e(LOG_TAG, "Failed to get access token: "+te.getMessage()); }
	    	}
	    } 

	
	
	
	public void startTimeLine(){
		progressDialog = ProgressDialog.show(this, "", "Loading...");
		
		new Thread() {
			public void run() {
			try{
			sleep(1000);
			} catch (Exception e) {
			Log.e("tag", e.getMessage());
			}
			// dismiss the progress dialog
			progressDialog.dismiss();

			}
		}.start();	
		
		
    	setContentView(R.layout.main);
		LinearLayout tweetClicker = (LinearLayout)findViewById(R.id.tweetbtn);
		tweetClicker.setOnClickListener(this);
    	
    	Resources res = getResources();
    	TabHost tabHost = getTabHost();
    	TabHost.TabSpec spec;
    	Intent intent;

    	intent = new Intent().setClass(this, TwitClientActivity.class);
		spec = tabHost.newTabSpec("home").setIndicator("Home",res.getDrawable(R.drawable.ic_tab_home)).setContent(intent);
		tabHost.addTab(spec);  
    	
    	
    	intent = new Intent().setClass(this, MentionActivity.class);
		spec = tabHost.newTabSpec("mention").setIndicator("Mention",res.getDrawable(R.drawable.ic_tab_interaksi)).setContent(intent);
		tabHost.addTab(spec);  

    	
    	intent = new Intent().setClass(this, DmActivity.class);
		spec = tabHost.newTabSpec("message").setIndicator("Dm",res.getDrawable(R.drawable.ic_tab_message)).setContent(intent);
		tabHost.addTab(spec);  
		
		
    	intent = new Intent().setClass(this, GroupActivity.class);
		spec = tabHost.newTabSpec("group").setIndicator("Group",res.getDrawable(R.drawable.user_group)).setContent(intent);
		tabHost.addTab(spec);  
		
	}
	
	 
	 public boolean onOptionsItemSelected(MenuItem item) {
	    	
	        switch (item.getItemId()) {	       
	        
	        case R.id.favorite:
	        	startActivity(new Intent(this, FavoriteActivity.class));    	
	        return true;
	        
	        case R.id.groups:
	        	Intent i = new Intent(this, GroupActivity.class);
	        	startActivity(i);
	        	Toast.makeText(this, "Groups", Toast.LENGTH_LONG).show();	      
	        return true;
	        
	        case R.id.mute:	        		        	
	        	startActivity(new Intent(this, MuteActivity.class));	        	
	        return true;
	        
	        
	        case R.id.search:	        		        	
	        	startActivity(new Intent(this, SearchActivity.class));	        	
	        return true;	        
	        
	        case R.id.logout:
	        	  Logout();
	        return true;
	        
	        case R.id.refresh:
	    		progressDialog = ProgressDialog.show(Main.this, "", "Loading...");
	    		
	    		new Thread() {
	    			public void run() {
	    			try{
	    			sleep(1000);
	    			} catch (Exception e) {
	    			Log.e("tag", e.getMessage());
	    			}
	    			// dismiss the progress dialog
	    			progressDialog.dismiss();

	    			}
	    		}.start();
	    		
	        	this.getApplicationContext().startService(new Intent(this.getApplicationContext(), TimelineService.class));
	        	
	        	Toast.makeText(this, "Refreshed", Toast.LENGTH_LONG).show();	
	        return true;
	        
	        case R.id.schedule_tweet:	        		        	
	        	startActivity(new Intent(this, ScheduleActivity.class));	        	
	        return true;	        
	        
	        case R.id.friend_refresh:
	        	startActivity(new Intent(this, FriendRrefresh.class));
	        return true;
	        	
	        default:
	        return super.onOptionsItemSelected(item);
	        }
	    }		
	
	public void Logout(){
		twitPrefs = getSharedPreferences("TwitClientPrefs", 0); 
		SharedPreferences.Editor editor = twitPrefs.edit();
		editor.remove(Const.TOKEN);
		editor.remove(Const.TOKEN_SECRET);
		editor.commit();
		
		
		timelineHelper = new TwitDataHelper(this);
		timelineHelper.removeAll();
		
		finish();
		
	}
	
	public void onResume(){
		super.onResume();
		//this.getApplicationContext().startService(new Intent(this.getApplicationContext(), TimelineService.class));
	}
	
	public void onDestroy(){
		super.onPause();
		//stopService(new Intent(this, TimelineService.class));
	}
	
	
	
	
	
}
