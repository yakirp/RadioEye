package com.radioeye;

public class RadioEyeApp {

	private enum Mode {
		DEVELOPMENT, RELEASE
	}

	public final static Mode MODE = Mode.DEVELOPMENT;
  
	public static final boolean IN_RELEASE_MODE = MODE == Mode.RELEASE;
	public static final boolean IN_DEVELOPMENT_MODE = MODE == Mode.DEVELOPMENT;

	public static String getBaseUrl() {

		if (IN_RELEASE_MODE) {
			/* AWS HAProxy */
			return "http://54.83.192.113";

		} else {
			/* DigitalOcean Development Server */
			return "http://airfunction.com";
		}

	}

}
