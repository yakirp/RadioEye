package com.radioeye.clients;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.radioeye.R;
import com.radioeye.utils.Log;

public class RequestProxy {

	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;

	// package access constructor
	RequestProxy(Context context) {
		mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
		setmImageLoader(new ImageLoader(this.mRequestQueue,
				new ImageLoader.ImageCache() {
					private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(
							10);

					public void putBitmap(String url, Bitmap bitmap) {
						mCache.put(url, bitmap);
					}

					public Bitmap getBitmap(String url) {
						return mCache.get(url);
					}
				}));

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

}