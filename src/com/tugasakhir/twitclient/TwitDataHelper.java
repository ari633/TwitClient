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
	private static final int DATABASE_VERSION = 10;
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
	
	
		/**database creation string*/
	private static final String DATABASE_CREATE1 = "CREATE TABLE home (" + HOME_COL + " INTEGER NOT NULL " +
			"PRIMARY KEY, " + UPDATE_COL + " TEXT, " + USER_COL + " TEXT, " +
					TIME_COL + " INTEGER, " + USER_IMG + " TEXT);" ;
	
	private static final String DATABASE_CREATE2 = "CREATE TABLE mentions (" + HOME_COL + " INTEGER NOT NULL " +
			"PRIMARY KEY, " + UPDATE_COL + " TEXT, " + USER_COL + " TEXT, " +
			TIME_COL + " INTEGER, " + USER_IMG + " TEXT);" ;

	private static final String DATABASE_CREATE3 = "CREATE TABLE favorite (" + HOME_COL + " INTEGER NOT NULL " +
			"PRIMARY KEY, " + UPDATE_COL + " TEXT, " + USER_COL + " TEXT, " +
			TIME_COL + " INTEGER, " + USER_IMG + " TEXT);" ;

	
	
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
    	db.execSQL(DATABASE_CREATE1);
    	db.execSQL(DATABASE_CREATE2);
    	db.execSQL(DATABASE_CREATE3);
    	
	}
    
    /*
     * onUpgrade drops home table and executes creation string
     */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	    	
		db.execSQL("DROP TABLE IF EXISTS home");
		db.execSQL("DROP TABLE IF EXISTS mention");
		db.execSQL("DROP TABLE IF EXISTS favorite");		
		//db.execSQL("VACUUM");
		onCreate(db);
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
	
	
	public static ContentValues getValuesTimeline(Status status){
		Log.v("TwitDataHelper", "converting values home tl");
		
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
	
	
	public static ContentValues getValuesMention(Status status){
		Log.v("TwitDataHelper", "converting values mention");
		
		ContentValues mentionValues = new ContentValues();
		
        try {
    		//get each value for the tablesta
       
        	mentionValues.put(HOME_COL, status.getId());
        	mentionValues.put(UPDATE_COL, status.getText());
        	mentionValues.put(USER_COL, status.getUser().getScreenName());
        	mentionValues.put(TIME_COL, status.getCreatedAt().getTime());
        	mentionValues.put(USER_IMG, status.getUser().getProfileImageURL().toString());
	    }
	    catch(Exception te) { Log.e("TwitDataHelper", te.getMessage()); }		
        
		return mentionValues;
		
	}	

	public static ContentValues getValuesFavorite(Status status){
		Log.v("TwitDataHelper", "converting values favorite");
		
		ContentValues favoriteValues = new ContentValues();
		
        try {
    		//get each value for the tablesta
       
        	favoriteValues.put(HOME_COL, status.getId());
        	favoriteValues.put(UPDATE_COL, status.getText());
        	favoriteValues.put(USER_COL, status.getUser().getScreenName());
        	favoriteValues.put(TIME_COL, status.getCreatedAt().getTime());
        	favoriteValues.put(USER_IMG, status.getUser().getProfileImageURL().toString());
	    }
	    catch(Exception te) { Log.e("TwitDataHelper", te.getMessage()); }		
        
		return favoriteValues;
		
	}	
	
	
	
}
