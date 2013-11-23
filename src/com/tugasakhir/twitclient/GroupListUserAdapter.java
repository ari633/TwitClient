package com.tugasakhir.twitclient;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class GroupListUserAdapter extends SimpleCursorAdapter{
	
	
	private GroupDataModel groupModel;
	private Cursor cursor;
	static final String[] from = {"user_screen"};
	static final int[] to = {R.id.user_screen};
	
	public GroupListUserAdapter(Context context, Cursor c) {
		super(context, R.layout.group_list_user_row, c, from, to);
		groupModel = new GroupDataModel(context);
		groupModel.open();
	}

	
	public void bindView(View row, Context context, final Cursor cursor){
		super.bindView(row, context, cursor);
		
		//get the user ID
		long statusID = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
		//get the user name
		String statusName = cursor.getString(cursor.getColumnIndex("user_screen"));		
		//null
		String statusText = null;
		
		StatusData tag = new StatusData(statusID, statusName, statusText);
		
		row.findViewById(R.id.delete).setTag(tag);
		row.findViewById(R.id.delete).setOnClickListener(		
		new OnClickListener() {
			
			public void onClick(View v) {
				 
				StatusData tag = (StatusData)v.getTag();
				switch (v.getId()) {
				case R.id.delete:
		
					groupModel.deleteUserGroup(tag.getID());
					cursor.requery();
					
					
					Toast.makeText(v.getContext(), "Deleted "+tag.getID(), Toast.LENGTH_LONG).show();	
					
					break;

				default:
					break;
				}
				
			}
		});
	}
		

	/*
	private OnClickListener userListener = new OnClickListener() {

		
		
		public void onClick(View v) {
			
			 
			StatusData tag = (StatusData)v.getTag();
			switch (v.getId()) {
			case R.id.delete:
	
				groupModel.deleteUserGroup(tag.getID());
				cursor.requery();
				
				
				Toast.makeText(v.getContext(), "Deleted "+tag.getID(), Toast.LENGTH_LONG).show();	
				
				break;

			default:
				break;
			}
		}
		
	};
	
	*/

	
}
