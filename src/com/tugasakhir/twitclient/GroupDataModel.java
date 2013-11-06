package com.tugasakhir.twitclient;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class GroupDataModel{
	
	private SQLiteDatabase db;
	private TwitDataHelper dbHelper;
	
	public GroupDataModel(Context context){
		dbHelper = new TwitDataHelper(context);
	}

	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}
	
	public void close(){
		dbHelper.close();
	}	
	
	public void insert(String title){
		ContentValues cv = new ContentValues();
		
		cv.put("title", title);		
		db.insert("groups", null, cv);		
		
	}
	public Cursor getById(long id){
		
		return (db.rawQuery("select _id, title FROM groups WHERE _id="+id, null));
	}

	
	public void deleteGroup(long id){
		db.delete("groups", "_id = "+id, null);
	}
		
	public String getGroupTitle(Cursor c){
		return(c.getString(1));
	}
	
	/*
	 * Group Users
	 */
	public Cursor getUserByName(String username){
		String[] user_screen = {username};
		return (db.rawQuery("select _id, id_group, user_screen FROM group_users WHERE user_screen=?", user_screen));
	}	
	
	public void insertUser(String usrname, long group_id){
		ContentValues cv = new ContentValues();
		
		cv.put("user_screen", usrname);	
		cv.put("id_group", group_id);
		db.insert("group_users", null, cv);		
		
	}	
	
	public String getUserName(Cursor c){
		return(c.getString(2));
	}	
	
}
