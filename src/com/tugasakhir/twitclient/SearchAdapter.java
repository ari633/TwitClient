package com.tugasakhir.twitclient;

import java.util.ArrayList;
import java.util.HashMap;

import com.library.imageloader.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchAdapter extends BaseAdapter {

	public Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater=null;
	
	public ImageLoader imageLoader;
	
	public SearchAdapter(Activity a, ArrayList<HashMap<String, String>> d){
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);   
        imageLoader=new ImageLoader(activity.getApplicationContext());
	}
	
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View vi = convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.update, null);
        
        TextView userScreen = (TextView)vi.findViewById(R.id.userScreen);
        TextView text = (TextView)vi.findViewById(R.id.updateText);
        TextView time = (TextView)vi.findViewById(R.id.updateTime);
        ImageView thumb_image = (ImageView)vi.findViewById(R.id.userImg);
        
        HashMap<String, String> src = new HashMap<String, String>();
        
        src = data.get(position);
        
        userScreen.setText(src.get(SearchActivity.USER_SCREEN));
        text.setText(src.get(SearchActivity.TEXT));
        time.setText(src.get(SearchActivity.TIME));
        
        int loader = R.drawable.ic_launcher;
        imageLoader.DisplayImage(src.get(MessageActivity.PROFILE_IMG), loader, thumb_image);
        
		return vi;
	}

}
