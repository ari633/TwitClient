package com.tugasakhir.twitclient;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import com.library.imageloader.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageAdapter extends BaseAdapter{

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    
    public ImageLoader imageLoader;
    
    public MessageAdapter(Activity a, ArrayList<HashMap<String, String>> d){
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
            vi = inflater.inflate(R.layout.row_message, null);
        TextView userScreen = (TextView)vi.findViewById(R.id.userScreen);
        TextView messageText = (TextView)vi.findViewById(R.id.messageText);
        TextView time = (TextView)vi.findViewById(R.id.time);
        ImageView thumb_image = (ImageView)vi.findViewById(R.id.list_image);
        
        HashMap<String, String> msg = new HashMap<String, String>();
        
        msg = data.get(position);
        
        //setting all value to list view
        userScreen.setText(msg.get(MessageActivity.USER_SCREEN));
        messageText.setText(msg.get(MessageActivity.MESSAGE_TEXT));
        time.setText(msg.get(MessageActivity.TIME));
                
        /*
        try {
			URL thumbURL = new URL(msg.get(MessageActivity.PROFILE_IMG));
			thumb_image.setImageDrawable(Drawable.createFromStream((InputStream)thumbURL.getContent(), ""));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
        int loader = R.drawable.ic_launcher;
        imageLoader.DisplayImage(msg.get(MessageActivity.PROFILE_IMG), loader, thumb_image);
        
		return vi;
	}
	
}
