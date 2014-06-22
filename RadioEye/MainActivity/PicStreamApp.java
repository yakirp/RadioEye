package com.pic;

 

import android.app.Activity;
import android.app.Application;
import android.content.Context;

public class PicStreamApp extends Application {
	public static final String PACKAGE_NAME = "com.novoda.locationdemo";
	public static final String LOCATION_UPDATE_ACTION = "com.novoda.locationdemo.action.ACTION_FRESH_LOCATION";

	// ==================================================
	// TODO  
	 
	public static final String LOG_TAG = "NovocationDemo";

	// ==================================================

	private static Context context;

	private static Activity activity;

	@Override
	public void onCreate() {
		super.onCreate();
		setContext(getApplicationContext());

	}  

	// ==================================================
	 
	public static Context getContext() {
		return context;
	}

	public static void setContext(Context context) {
		PicStreamApp.context = context;
	}

	public static Activity getActivity() {
		return activity;
	}

	public static void setActivity(Activity activity) {
		PicStreamApp.activity = activity;
	}
}
