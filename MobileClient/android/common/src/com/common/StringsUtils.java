package com.common;

public class StringsUtils {

	public static boolean isNullOrEmpty(String str) {
		if (str != null && str != "") {
			return false;
		}
		return true;
	}

}
