package com.tugasakhir.twitclient;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MuteDataModel {
	
	private SQLiteDatabase db;
	private TwitDataHelper dbHelper;
	
	public MuteDataModel(Context context){
		dbHelper = new TwitDataHelper(context);
	}
	
	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}
	
	public void close(){
		dbHelper.close();
	}	
	
	public void delete(long id){
		db.delete("mute_users", "_id = "+id, null);
	}
	
	public Cursor getUsers(){		
		return (db.query("mute_users", null, null, null, null, null, "_id DESC"));
	}
	
	public Cursor getUserByName(String username){
		String[] user_screen = {username};
		return (db.rawQuery("select _id, user_screen, user_img FROM mute_users WHERE user_screen=?", user_screen));
	}
	
	public void insert(String username, String user_img){
		ContentValues cv = new ContentValues();
		cv.put("user_screen", username);	
		cv.put("user_img", user_img);
		db.insert("mute_users", null, cv);
	}
	
	
	
}
