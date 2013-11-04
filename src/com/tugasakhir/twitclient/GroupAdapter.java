package com.tugasakhir.twitclient;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class GroupAdapter extends SimpleCursorAdapter{

	
	static final String[] from = {"title"};
	static final int[] to = {R.id.title};
	

	public GroupAdapter(Context context, Cursor c) {
		super(context, R.layout.group_row, c, from, to);
	}

	
	public void bindView(View row, Context context, Cursor cursor){
		TextView total = (TextView)row.findViewById(R.id.total);
		total.setText("Total User");
	}
	
	
}
