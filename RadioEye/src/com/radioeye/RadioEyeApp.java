package com.radioeye;

import android.app.Application;
import android.content.Context;

import com.radioeye.clients.RequestManager;

public class RadioEyeApp extends Application {

	private enum Mode {
		DEVELOPMENT, RELEASE
	}

	public final static Mode MODE = Mode.DEVELOPMENT;
  
	public static final boolean IN_RELEASE_MODE = MODE == Mode.RELEASE;
	public static final boolean IN_DEVELOPMENT_MODE = MODE == Mode.DEVELOPMENT;

	 
	
	private static Context appContext;
	
	public static String getBaseUrl() {

		if (IN_RELEASE_MODE) {
			/* AWS HAProxy */
			return "http://54.83.192.113";

		} else {
			/* DigitalOcean Development Server */
			return "http://airfunction.com";
		}

	}

	@Override
	public void onCreate() {
		// RequestManager initialization
		System.err.println("----------------------------------");
        RequestManager.getInstance(getApplicationContext());
		super.onCreate();
	}

	public static Context getAppContext() {
		return appContext;
	}

	public static void setAppContext(Context appContext) {
		RadioEyeApp.appContext = appContext;
	}

	 

}
