package com.tugasakhir.twitclient;

import android.app.ActivityGroup;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class GroupTimelineActivity extends ActivityGroup implements OnClickListener{
	
	private ListView groupTimeline;
	private TwitDataHelper timelineHelper;
	private SQLiteDatabase db;
	private Cursor cursor;
	private UpdateAdapter timelineAdapter;	
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);     
        
        setContentView(R.layout.timelinegroup);
        
        Button btn = (Button)findViewById(R.id.all_tl);
        btn.setOnClickListener(this);	        
  
        	
        groupTimeline = (ListView)findViewById(R.id.list);
        timelineHelper = new TwitDataHelper(this);
        db = timelineHelper.getReadableDatabase();
        String query = "SELECT * FROM home WHERE user_screen IN "+"(SELECT user_screen FROM group_users) ORDER BY update_time DESC"; 
        
        cursor = db.rawQuery(query, null);
        
        startManagingCursor(cursor);
        timelineAdapter = new UpdateAdapter(this, cursor);
        
        groupTimeline.setAdapter(timelineAdapter);

        
    }

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.all_tl:
			replaceContentView("TwitClientActivity", new Intent(v.getContext(), TwitClientActivity.class));	
			break;

		default:
			break;
		}
	}
	
	public void replaceContentView(String id, Intent newIntent) {
		
		View view = getLocalActivityManager().startActivity(id,newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)) .getDecorView(); this.setContentView(view);
		
	}		
	
}
