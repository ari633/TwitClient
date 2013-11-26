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
		db.delete("group_users", "id_group = "+id, null);
	}
		
	public String getGroupTitle(Cursor c){
		return(c.getString(1));
	}
	
	/*
	 * Group Users
	 */
	
	public void deleteUserGroup(long id){
		db.delete("group_users", "_id = "+id, null);
	}
	
	public Cursor getUserByGroup(long id_group){
		String query = "select _id, id_group, user_screen FROM group_users WHERE id_group ="+id_group;
		return (db.rawQuery(query, null));
	}
	
	public Cursor getUserJoinGroup(String username){
		String[] user_screen = {username};
		final String query = "SELECT id_group, user_screen, title AS group_name FROM group_users JOIN groups ON group_users.id_group = groups._id where group_users.user_screen = ?";
		return (db.rawQuery(query, user_screen));
	}
	
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
	
	public String getTlGroupName(Cursor c){
		return(c.getString(2));
	}
}
