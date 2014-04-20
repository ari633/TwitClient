package com.tugasakhir.twitclient;

import com.library.imageloader.ImageLoader;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

public class GroupListUserAdapter extends SimpleCursorAdapter{
	
	public ImageLoader imageLoader;
	int loader = R.drawable.ic_launcher;
	
	private GroupDataModel groupModel;
	static final String[] from = {"user_screen"};
	static final int[] to = {R.id.user_screen};
	
	public GroupListUserAdapter(Context context, Cursor c) {
		super(context, R.layout.group_list_user_row, c, from, to);
		imageLoader=new ImageLoader(context.getApplicationContext());
		groupModel = new GroupDataModel(context);
		groupModel.open();
				
		
	}

	
	public void bindView(View row, Context context, final Cursor cursor){
		super.bindView(row, context, cursor);
		
		//get the user ID
		long statusID = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
		//get the user screen_name
		String statusName = cursor.getString(cursor.getColumnIndex("user_screen"));		
		String userImage = cursor.getString(cursor.getColumnIndex("user_img"));
		//null
		String statusText = null;
		
		StatusData tag = new StatusData(statusID, statusName, statusText);
		
		ImageView profPic = (ImageView)row.findViewById(R.id.avatar);
		imageLoader.DisplayImage(userImage, loader, profPic);
		
		row.findViewById(R.id.delete).setTag(tag);
		row.findViewById(R.id.delete).setOnClickListener(		
		new OnClickListener() {
			
			public void onClick(View v) {
				 
				StatusData tag = (StatusData)v.getTag();
				switch (v.getId()) {
				case R.id.delete:		
					groupModel.deleteUserGroup(tag.getID());
					cursor.requery();
					break;

				default:
					break;
				}
				
			}
		});
	}
		


	
}
