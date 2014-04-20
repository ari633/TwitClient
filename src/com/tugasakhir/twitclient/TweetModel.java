package com.tugasakhir.twitclient;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class TweetModel {

	private SQLiteDatabase db;
	private TwitDataHelper dbHelper;
	
	public TweetModel(Context context) {
		dbHelper = new TwitDataHelper(context);
	}
	
	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}
	
	public void close(){
		dbHelper.close();
	}	
	
	public long insert(String tweetText, long time){
		ContentValues cv = new ContentValues();
		cv.put("text_tweet", tweetText);
		cv.put("update_time", time);	
		long id = db.insert("schedule_tweet", null, cv);
		return id;
	}
	
	//get all schedule
	public Cursor getAll(){
		return (db.query("schedule_tweet", null, null, null, null, null, "update_time ASC"));
	}
	
	public void deleteByTime(long time){
		db.delete("schedule_tweet", "update_time = "+time, null);
	}
	
	public void delete(long id){
		db.delete("schedule_tweet", "_id = "+id, null);
	}	
	
}
