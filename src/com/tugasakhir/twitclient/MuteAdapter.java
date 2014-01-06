package com.tugasakhir.twitclient;

import com.library.imageloader.ImageLoader;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MuteAdapter extends SimpleCursorAdapter{

	
	private MuteDataModel muteModel;

	public ImageLoader imageLoader;
	int loader = R.drawable.ic_launcher;	
	
	static final String[] from = {"user_screen"};
	static final int[] to = {R.id.user_screen};	
	
	public MuteAdapter(Context context, Cursor c) {
		
		super(context, R.layout.group_list_user_row, c, from, to);
		imageLoader=new ImageLoader(context.getApplicationContext());
		muteModel = new MuteDataModel(context);
		muteModel.open();
		
	}
	
	public void bindView(View row, Context context, final Cursor cursor){
		super.bindView(row, context, cursor);
		//get user id
		long statusID = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
		
		String username = cursor.getString(cursor.getColumnIndex("user_screen"));	
		
		String statusText = null;
		
		StatusData tag = new StatusData(statusID, username, statusText);
		
		String userImage = cursor.getString(cursor.getColumnIndex("user_img"));
		
		ImageView profPic = (ImageView)row.findViewById(R.id.avatar);
		imageLoader.DisplayImage(userImage, loader, profPic);
		
		row.findViewById(R.id.delete).setTag(tag);
		row.findViewById(R.id.delete).setOnClickListener(		
		new OnClickListener() {
			
			public void onClick(View v) {
				 
				StatusData tag = (StatusData)v.getTag();
				switch (v.getId()) {
				case R.id.delete:
					
					muteModel.delete(tag.getID());
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
