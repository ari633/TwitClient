package com.tugasakhir.twitclient;

import twitter4j.DirectMessage;
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
		
	private SQLiteDatabase db;
		/**db version*/
	private static final int DATABASE_VERSION = 12;
		/**database name*/
	private static final String DATABASE_NAME = "twitter.db";
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
	
	/**Tambahan Colom untuk messages**/
	private static final String MSG_RECIPIENT_COL = "recipient";
	private static final String MSG_RECIPIENT_ID_COL = "recipient_id";
	private static final String MSG_RECIPIENT_NAME_COL = "recipient_name";
	private static final String MSG_RECIPIENT_IMG_COL = "recipient_img";
	private static final String MSG_SENDER_COL = "sender";
	private static final String MSG_SENDER_ID_COL = "sender_id";
	private static final String MSG_SENDER_NAME_COL = "sender_name";
	private static final String MSG_SENDER_IMG_COL = "sender_img";
	
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
	
	/*Create messages table*/
	private static final String DATABASE_CREATE4 = "CREATE TABLE messages ("+ HOME_COL + " INTEGER NOT NULL " + 
			" PRIMARY KEY, "+ MSG_RECIPIENT_COL +" TEXT, "+ MSG_RECIPIENT_ID_COL +" INTEGER, "+MSG_RECIPIENT_NAME_COL+" TEXT, "+ MSG_RECIPIENT_IMG_COL + " TEXT, "+
			MSG_SENDER_COL + " TEXT, "+ MSG_SENDER_ID_COL +" INTEGER, "+ MSG_SENDER_NAME_COL +" TEXT, "+ MSG_SENDER_IMG_COL+ " TEXT, "+ TIME_COL +" INTEGER, "+ UPDATE_COL + " TEXT )";
	
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
    	db.execSQL(DATABASE_CREATE4);
	}
    
    /*
     * onUpgrade drops home table and executes creation string
     */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	    	
		db.execSQL("DROP TABLE IF EXISTS home");
		db.execSQL("DROP TABLE IF EXISTS mention");
		db.execSQL("DROP TABLE IF EXISTS favorite");	
		db.execSQL("DROP TABLE IF EXISTS messages");	
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
	
	public static ContentValues getValuesMessages(DirectMessage dm){
		Log.v("TwitDataHelper", "converting values messages");
		ContentValues messagesValues = new ContentValues();
		try {
			messagesValues.put(HOME_COL, dm.getId());
			messagesValues.put(MSG_RECIPIENT_ID_COL, dm.getRecipientId());
			messagesValues.put(MSG_RECIPIENT_NAME_COL, dm.getRecipientScreenName());
			messagesValues.put(MSG_RECIPIENT_IMG_COL, dm.getRecipient().getProfileImageURL().toString());
			messagesValues.put(MSG_SENDER_ID_COL, dm.getSenderId());
			messagesValues.put(MSG_SENDER_NAME_COL, dm.getSenderScreenName());
			messagesValues.put(MSG_SENDER_IMG_COL, dm.getSender().getProfileImageURL().toString());
			messagesValues.put(UPDATE_COL, dm.getText());
			messagesValues.put(TIME_COL, dm.getCreatedAt().getTime());
			
		} catch (Exception e) {
			Log.e("TwitDataHelper", e.getMessage());
		}
		return messagesValues;
	}
	
	
	public void removeAll(){	
		Log.v("TwitDataHelper", "delete tweet data");
		db = getWritableDatabase();
		db.delete("home", null, null);
		db.delete("mentions", null, null);
		db.delete("favorite", null, null);
		db.delete("messages", null, null);		
	}
	
}
