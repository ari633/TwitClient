package com.tugasakhir.twitclient;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ScheduleAdapter extends SimpleCursorAdapter{
	
	private TweetModel tweetModel;
	
	static final String[] from = {"text_tweet", "update_time"};
	static final int[] to = {R.id.text_tweet, R.id.time};	
	
	
	public ScheduleAdapter(Context context, Cursor c) {
		super(context, R.layout.schedule_list, c, from, to);
		
		tweetModel = new TweetModel(context);
		tweetModel.open();
	}
	
	public void bindView(View row, Context context, final Cursor cursor){
		super.bindView(row, context, cursor);
		
		long updateAt = cursor.getLong(cursor.getColumnIndex("update_time"));
		TextView textUpdateAt = (TextView) row.findViewById(R.id.time);
		
		textUpdateAt.setText(DateUtils.getRelativeTimeSpanString(updateAt)+" ");		
		
		long statusID = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
		String tweetText = cursor.getString(cursor.getColumnIndex("text_tweet"));
		
		
		StatusData tag = new StatusData(statusID, tweetText, null);
		
		row.findViewById(R.id.delete).setTag(tag);
		row.findViewById(R.id.delete).setOnClickListener(		
		new OnClickListener() {
			
			public void onClick(View v) {
				 
				StatusData tag = (StatusData)v.getTag();
				switch (v.getId()) {
				case R.id.delete:
					
					tweetModel.delete(tag.getID());
					cursor.requery();					
				    Toast.makeText(v.getContext(), "Deleted", Toast.LENGTH_LONG).show();	
					
					break;

				default:
					break;
				}				
				
				
			}
		});		
		
		
	}

}
