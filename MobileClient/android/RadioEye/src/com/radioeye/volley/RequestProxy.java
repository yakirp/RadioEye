package com.radioeye.volley;

import org.json.JSONObject;

import android.content.Context;
  
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
 
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.radioeye.utils.Log;

public class RequestProxy {

	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;

	// package access constructor
	RequestProxy(Context context) {
		 
		setmRequestQueue(MyVolley.getRequestQueue());
		setmImageLoader(MyVolley.getImageLoader());
				 
		
		 
	}

	public void getJson(String url, final Listener<JSONObject> callback) {
  
		JsonObjectRequest req = new JsonObjectRequest(url, null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						callback.onResponse(response);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.i("Error: " + error.getMessage());
					}
				});

		getmRequestQueue().add(req);
	}

	public void login() {
		// login request

	}

	public void weather() {
		// weather request
	}

	public void loadImage(String url, final ImageListener callback) {

		getmImageLoader().get(url, new ImageListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if (error != null) {
					callback.onErrorResponse(error);
				}
			}

			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {

				callback.onResponse(response, isImmediate);

			}
		});

	}

	public ImageLoader getmImageLoader() {
		return mImageLoader;
	}

	public void setmImageLoader(ImageLoader mImageLoader) {
		this.mImageLoader = mImageLoader;
	}

	public RequestQueue getmRequestQueue() {
		return mRequestQueue;
	}

	public void setmRequestQueue(RequestQueue mRequestQueue) {
		this.mRequestQueue = mRequestQueue;
	}

}