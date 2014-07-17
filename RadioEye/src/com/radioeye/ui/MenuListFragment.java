package com.radioeye.ui;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.Response.Listener;
import com.android.volley.toolbox.NetworkImageView;
 
 
 
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.radioeye.MenuCallback;
import com.radioeye.R;
 
import com.radioeye.utils.Log;
import com.radioeye.volley.MyVolley;
import com.radioeye.volley.RequestManager;

public class MenuListFragment extends ListFragment {

	 private SlidingMenu slidingMenu;
private MenuCallback callback;
private SwipeRefreshLayout swipeLayout;
	 
	 
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
		
		 swipeLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipe_container);
		    swipeLayout.setOnRefreshListener(new OnRefreshListener() {
				
				@Override
				public void onRefresh() {
					RequestManager.getInstance().doRequest().getJson("http://airfunction.com/server/php/testrds.php", new Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {
							   Log.i(response.toString());
				                parseJSON(response);
				             
				                swipeLayout.setRefreshing(false);
						}
					});
					
				}
			});
		    swipeLayout.setColorScheme(android.R.color.holo_blue_bright, 
		            android.R.color.holo_green_light, 
		            android.R.color.holo_orange_light, 
		            android.R.color.holo_red_light);
 
		
		
		
		 
	}
	
	 private void parseJSON(JSONObject json){
	        try{
	        	SampleAdapter adapter = new SampleAdapter(getActivity());
	        	
	          //  JSONObject value = json.getJSONArray("publishers");
	            JSONArray items = json.getJSONArray("publishers");
	            for(int i=0;i<items.length();i++){

	                    JSONObject item = items.getJSONObject(i);
	                    Log.i(item.optString("username"));
	                  
	                    adapter.add(new publisherItems(item.optString("title"), item.optString("imageurl"),item.optString("userfacebookid") ,item.optString("description"),item.optString("online"),item.optString("offlineimageurl")));
	                   
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
		private String channel;
		private String imageUrl;
		private String offlineimageUrl;
		private String description;
		private String online;
	 
		public publisherItems(String tag, String imageUrl,String Channel , String description , String online,String offlineimageurl) {
			this.setPublisherName(tag); 
			this.setImageUrl(imageUrl);
			this.channel = Channel;
			this.setDescription(description);
			this.setOnline(online);
			this.setOfflineimageUrl(offlineimageurl);
			
		}
		public String getChannel() {
			return channel;
		}
		public void setChannel(String channel) {
			channel = channel;
		}
		public String getPublisherName() {
			return publisherName;
		}
		public void setPublisherName(String publisherName) {
			this.publisherName = publisherName;
		}
		public String getImageUrl() {
			return imageUrl;
		}
		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		 
		public String getOfflineimageUrl() {
			return offlineimageUrl;
		}
		public void setOfflineimageUrl(String offlineimageUrl) {
			this.offlineimageUrl = offlineimageUrl;
		}
		public String getOnline() {
			return online;
		}
		public void setOnline(String online) {
			this.online = online;
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
			
			 
			String url = getItem(position).getOnline().equalsIgnoreCase("t") ? getItem(position).getImageUrl() : getItem(position).getOfflineimageUrl();
			
			NetworkImageView imageUrl = (NetworkImageView) convertView.findViewById(R.id.image_item);
			 imageUrl.setImageUrl(url,MyVolley.getImageLoader());
			 
		 
			 
			TextView title = (TextView) convertView.findViewById(R.id.lbl_contact_name_item);
		 
			
			
			
			
			TextView description = (TextView) convertView.findViewById(R.id.lbl_contact_description_item);
			title.setText(getItem(position).getPublisherName());
			description.setText(getItem(position).getDescription());
			
			 if (getItem(position).getOnline().equalsIgnoreCase("f")) {
					title.setTextColor(Color.GRAY);
					description.setTextColor(Color.GRAY);
			 } 
			 
			 
			 
			
			
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
