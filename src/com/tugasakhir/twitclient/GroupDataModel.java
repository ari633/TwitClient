package com.tugasakhir.twitclient;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.CursorAdapter;

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

	public Cursor getBytitle(String title){
		String[] group_title = {title};
		return (db.rawQuery("select * FROM groups WHERE title=?", group_title));		
	}
	
	public void deleteGroup(long id){
		db.delete("groups", "_id = "+id, null);
		db.delete("group_users", "id_group = "+id, null);
	}
		
	public String getGroupTitle(Cursor c){
		return(c.getString(1));
	}
	
	public String getGroupId(Cursor c){
		return(c.getString(0));
	}	
	
	/*List All groups*/
	public List<String> getAllLabelGroups(){
		List<String> labels = new ArrayList<String>();
		
		labels.add("All Timeline");
		String selectQuery = "SELECT * FROM groups";
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        
        return labels;
	}
	
	
	/*
	 * Group Users
	 */
	
	public void deleteUserGroup(long id){
		db.delete("group_users", "_id = "+id, null);
	}
	
	public Cursor getUserByGroup(long id_group){
		String query = "select _id, id_group, user_screen, user_img FROM group_users WHERE id_group ="+id_group;
		return (db.rawQuery(query, null));
	}
	
	public Cursor getUserJoinGroup(String username){
		String[] user_screen = {username};
		final String query = "SELECT id_group, user_screen, user_img title AS group_name FROM group_users JOIN groups ON group_users.id_group = groups._id where group_users.user_screen = ?";
		return (db.rawQuery(query, user_screen));
	}
	
	public Cursor getUserByName(String username){
		String[] user_screen = {username};
		return (db.rawQuery("select _id, id_group, user_screen, user_img FROM group_users WHERE user_screen=?", user_screen));
	}	
	
	public void insertUser(String usrname, long group_id, String user_img){
		ContentValues cv = new ContentValues();
		
		cv.put("user_screen", usrname);	
		cv.put("id_group", group_id);
		cv.put("user_img", user_img);
		db.insert("group_users", null, cv);		
		
	}	
	
	public String getUserName(Cursor c){
		return(c.getString(2));
	}	
	
	public String getTlGroupName(Cursor c){
		return(c.getString(2));
	}
}
