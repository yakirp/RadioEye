package com.radioeye.volley;

import com.android.volley.RequestQueue;

import android.content.Context;

public class RequestManager {

	private static RequestManager instance;
	private RequestProxy mRequestProxy;

	private RequestManager(Context context) {
		mRequestProxy = new RequestProxy(context);
	}

	public void cancelAllTraffic() {
		mRequestProxy.getmRequestQueue().cancelAll(
				new RequestQueue.RequestFilter() {

					@Override
					public boolean apply(com.android.volley.Request<?> request) {
						// TODO Auto-generated method stub
						return true;
					}
				});
	}

	public RequestProxy doRequest() {
		return mRequestProxy;
	}

	// This method should be called first to do singleton initialization
	public static synchronized RequestManager getInstance(Context context) {
		if (instance == null) {

			instance = new RequestManager(context);
		}
		return instance;
	}

	public static synchronized RequestManager getInstance() {
		if (instance == null) {
			throw new IllegalStateException(
					RequestManager.class.getSimpleName()
							+ " is not initialized, call getInstance(..) method first.");
		}
		return instance;
	}
}
