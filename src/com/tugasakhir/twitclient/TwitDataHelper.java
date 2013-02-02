package com.tugasakhir.twitclient;

import twitter4j.Status;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;


/**
 * Home Timeline database - the main db for the app
 * 
 * - stores updates for the user's home timeline
 * - each record has ID, user screen name, tweet text, time sent and profile image URL
 */

	public class TwitDataHelper extends SQLiteOpenHelper{
		/**db version*/
	private static final int DATABASE_VERSION = 2;
		/**database name*/
	private static final String DATABASE_NAME = "home.db";
		/**ID column*/
	private static final String HOME_COL = BaseColumns._ID;
		/**tweet text*/
	private static final String UPDATE_COL = "update_text";
		/**twitter screen name*/
	private static final String USER_COL = "user_screen";
		/**time tweeted*/
	private static final String TIME_COL = "update_time";
		/**user profile image*/
	private static final String USER_IMG = "user_img";
		/**Timeline Type e.g. Home timeline, Mention timeline*/
	private static final String TWEET_TYPE = "tweet_type";
	
	
		/**database creation string*/
	private static final String DATABASE_CREATE = "CREATE TABLE home (" + HOME_COL + " INTEGER NOT NULL " +
			"PRIMARY KEY, " + UPDATE_COL + " TEXT, " + USER_COL + " TEXT, " +
					TIME_COL + " INTEGER, " + USER_IMG + " TEXT, " + TWEET_TYPE + " TEXT);";

	
	
	/**
	 * Constructor method
	 * @param context
	 */
	public TwitDataHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}
	
    /*
     * onCreate executes database creation string
     */
	@Override
	public void onCreate(SQLiteDatabase db) {
    	Log.v("TwitDataHelper", "creating db");
        db.execSQL(DATABASE_CREATE);	
	}
    
    /*
     * onUpgrade drops home table and executes creation string
     */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	
    	if (oldVersion < 2) {
    		Log.v("TwitDataHelper", "upgrading db");
            final String ALTER_TBL = "ALTER TABLE home ADD COLUMN "+ TWEET_TYPE +" text ;";
            db.execSQL(ALTER_TBL);
        }    	
		//db.execSQL("DROP TABLE IF EXISTS home");
		//db.execSQL("VACUUM");
		//onCreate(db);
	}
	
    /**
     * getValues retrieves the relevant info from the passed Status update
     * to write to the database table, returning a set of ContentValues
     * - called from TimelineUpdater in TimelineService
     * - this is a static method that can be called without an instance of this class
     * The Service will attempt to write the returned set to the database
     * 
     * @param status
     * @return ContentValues result
     */
	
	
	public static ContentValues getValues(Status status){
		Log.v("TwitDataHelper", "converting values");
		
		ContentValues homeValues = new ContentValues();
		
        try {
    		//get each value for the table
        homeValues.put(HOME_COL, status.getId());
        homeValues.put(UPDATE_COL, status.getText());
        homeValues.put(USER_COL, status.getUser().getScreenName());
        homeValues.put(TIME_COL, status.getCreatedAt().getTime());
        homeValues.put(USER_IMG, status.getUser().getProfileImageURL().toString());
	    }
	    catch(Exception te) { Log.e("TwitDataHelper", te.getMessage()); }		
        
		return homeValues;
		
	}
	
}
