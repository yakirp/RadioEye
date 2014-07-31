package com.radioeye.utils;

 

public class Log {

	private static final String TAG = "RadioEye";

	public static void i(String msg) {
		android.util.Log.i(TAG, msg);
	}
	public static void e(String msg) {
		android.util.Log.e(TAG, msg);
	}
	

}
