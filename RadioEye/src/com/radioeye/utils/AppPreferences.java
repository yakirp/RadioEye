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

	public AppPreferences(Context context) {
		this._sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS,
				Activity.MODE_PRIVATE);
		this._prefsEditor = _sharedPrefs.edit();
	}

	public String getSomeString(String key) {
		return _sharedPrefs.getString(key, "");
	}

	public void saveSomeString(String key, String text) {
		_prefsEditor.putString(key, text);
		_prefsEditor.commit();
	}

}
