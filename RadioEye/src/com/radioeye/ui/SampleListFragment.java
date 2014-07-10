package com.radioeye.ui;

import org.json.JSONArray;
import org.json.JSONObject;

import com.android.volley.Response.Listener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.radioeye.MenuCallback;
import com.radioeye.R;
import com.radioeye.clients.RequestManager;
import com.radioeye.utils.AppPreferences;
import com.radioeye.utils.Log;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SampleListFragment extends ListFragment {

	 private SlidingMenu slidingMenu;
private MenuCallback callback;
	 
	 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		
		RequestManager.getInstance().doRequest().getJson("http://airfunction.com/server/php/testrds.php", new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				   Log.i(response.toString());
	                parseJSON(response);
	             
				
			}
		});
		
		
		
		 
	}
	
	 private void parseJSON(JSONObject json){
	        try{
	        	SampleAdapter adapter = new SampleAdapter(getActivity());
	        	
	          //  JSONObject value = json.getJSONArray("publishers");
	            JSONArray items = json.getJSONArray("publishers");
	            for(int i=0;i<items.length();i++){

	                    JSONObject item = items.getJSONObject(i);
	                    Log.i(item.optString("username"));
	                   
	                    adapter.add(new publisherItems(item.optString("title"), android.R.drawable.ic_menu_search,item.optString("userfacebookid") ));
	            }
	            
	               
	    		 
	    		setListAdapter(adapter);
	    		
	        }
	        catch(Exception e){
	            e.printStackTrace();
	        }


	    }
	 

	public SlidingMenu getSlidingMenu() {
		return slidingMenu;
	}

	public void setSlidingMenu(SlidingMenu slidingMenu) {
		this.slidingMenu = slidingMenu;
	}


	public MenuCallback getCallback() {
		return callback;
	}

	public void setCallback(MenuCallback callback) {
		this.callback = callback;
	}


	public class publisherItems {
		private String publisherName;
		private String Channel;
		public int iconRes;
		public publisherItems(String tag, int iconRes,String Channel) {
			this.setPublisherName(tag); 
			this.iconRes = iconRes;
			this.Channel = Channel;
		}
		public String getChannel() {
			return Channel;
		}
		public void setChannel(String channel) {
			Channel = channel;
		}
		public String getPublisherName() {
			return publisherName;
		}
		public void setPublisherName(String publisherName) {
			this.publisherName = publisherName;
		}
	}

	public class SampleAdapter extends ArrayAdapter<publisherItems> {

		public SampleAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, null);
			}
			
			
			ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
			icon.setImageResource(getItem(position).iconRes);
			TextView title = (TextView) convertView.findViewById(R.id.row_title);
			title.setText(getItem(position).getPublisherName());
			
			final String channel =getItem(position).getChannel();
			
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					 
					getCallback().onItemSelect(channel);
					 
				}
				
			});

			return convertView;
		}

	}
}
