package com.radioeye.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppPreferences {
	public static final String KEY_PREFS_SOME_STRING = "RadioEye";
	private static final String APP_SHARED_PREFS = AppPreferences.class
			.getSimpleName();
	private SharedPreferences _sharedPrefs;
	private Editor _prefsEditor;

	private static AppPreferences instance;

	public static AppPreferences getInstance() {

		return instance;
	}

	public static AppPreferences getInstance(Context context) {
		if (instance == null) {
			instance = new AppPreferences(context);
		}

		return instance;
	}

	private AppPreferences(Context context) {
		this._sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS,
				Activity.MODE_PRIVATE);
		this._prefsEditor = _sharedPrefs.edit();
	}

	public String getString(String key) {
		return _sharedPrefs.getString(key, "");
	}

	public void purString(String key, String text) {
		_prefsEditor.putString(key, text);
		_prefsEditor.commit();
	}

}
