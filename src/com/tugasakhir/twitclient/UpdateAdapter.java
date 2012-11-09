package com.tugasakhir.twitclient;

import java.io.InputStream; 
import java.net.URL; 
import android.graphics.drawable.Drawable; 
import android.view.View; 
import android.widget.ImageView; 
import android.widget.TextView;
import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;
import android.text.format.DateUtils;
import android.util.Log;

/**
 * Apapter class binds the update data to the views in the user interface
 * - when new Twitter updates are received, they appear in the main timeline
 */
public class UpdateAdapter extends SimpleCursorAdapter{
	
	/**strings representing database column names to map to views*/
	static final String[] from = { "update_text", "user_screen", "update_time", "user_img" };
	/**view item IDs for mapping database record values to*/
	static final int[] to = { R.id.updateText, R.id.userScreen, R.id.updateTime, R.id.userImg };
	
	private String LOG_TAG = "UpdateAdapter";
	
	/**
	 * constructor sets up adapter, passing 'from' data and 'to' views
	 * @param context
	 * @param c
	 */
	public UpdateAdapter(Context context, Cursor c) {
		super(context, R.layout.update, c, from, to);
	}
	
	/*
	 * Bind the data to the visible views
	 */
	
	public void bindView(View row, Context context, Cursor cursor){
		super.bindView(row, context, cursor);
		/*
		 * In this method we add any additional requirements we have 
		 * for binding the data to the views
		 * - we set the image, add tags for data, format the time created and setup click listeners
		 */
		try {
			//get profile image
			URL profileURL = new URL(cursor.getString(cursor.getColumnIndex("user_img")));
			//set the image in the view for the current tweet
			ImageView profPic = (ImageView)row.findViewById(R.id.userImg);
			profPic.setImageDrawable(Drawable.createFromStream((InputStream)profileURL.getContent(), ""));			
		} catch (Exception te) {
			Log.e(LOG_TAG, te.getMessage()); 
		}
		//get the update time
		long createdAt = cursor.getLong(cursor.getColumnIndex("update_time"));
		//get the update time view
		TextView textCreatedAt = (TextView) row.findViewById(R.id.updateTime);
		//adjust the way the time is displayed to make it human-readable
		textCreatedAt.setText(DateUtils.getRelativeTimeSpanString(createdAt)+" ");
		
		
	}

}
