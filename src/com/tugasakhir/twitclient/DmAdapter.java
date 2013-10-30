package com.tugasakhir.twitclient;

import com.library.imageloader.ImageLoader;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class DmAdapter extends SimpleCursorAdapter{
	
    static final String[] from = {"update_text", "sender_name", "sender_img", "update_time"};
    static final int[] to = {R.id.messageText, R.id.userScreen, R.id.list_image, R.id.time};
    
	//private String LOG_TAG = "DmAdapter";
	
	public ImageLoader imageLoader;
	
	int loader = R.drawable.ic_launcher;    

	public DmAdapter(Context context, Cursor c) {
		super(context, R.layout.row_message, c, from, to);
		imageLoader=new ImageLoader(context.getApplicationContext());
	}
	
	public void bindView(View row, Context context, Cursor cursor){
		super.bindView(row, context, cursor);
		
		String urlImage = cursor.getString(cursor.getColumnIndex("sender_img"));
		ImageView profPic = (ImageView)row.findViewById(R.id.list_image);
		imageLoader.DisplayImage(urlImage, loader, profPic);
		
		long createdAt = cursor.getLong(cursor.getColumnIndex("update_time"));
		TextView textCreatedAt = (TextView) row.findViewById(R.id.time);
		
		textCreatedAt.setText(DateUtils.getRelativeTimeSpanString(createdAt)+" ");
		
		
		long senderID = cursor.getLong(cursor.getColumnIndex("sender_id"));
		String senderName = cursor.getString(cursor.getColumnIndex("sender_name"));
		DmData dmData = new DmData(senderID, senderName);
		
		//set the status data object as tag
		row.findViewById(R.id.messageText).setTag(dmData);
		row.findViewById(R.id.messageText).setTag(dmData);
		//setup onclick listeners
		row.findViewById(R.id.messageText).setOnClickListener(dmListener);
	}
	
	private OnClickListener dmListener = new OnClickListener(){

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.messageText:
				 
				 Intent conversationsIntent = new Intent(v.getContext(), DmConversationActivity.class);
				 DmData theData = (DmData)v.getTag();
				 conversationsIntent.putExtra("pengirimID", theData.getSenderId());
				 conversationsIntent.putExtra("namaPengirim", theData.getSenderName());
				 v.getContext().startActivity(conversationsIntent);
				 
				 Toast.makeText(v.getContext(), "Go To Conversations", Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		}
		
	};
	

}
