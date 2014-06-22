package com.radioeye.clients;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * 
 * @author user
 *   
 */  
public class HttpClient {

	/**
	 * Get content from web server
	 * @param requestUrl of the request
	 * @return
	 * @throws IOException
	 */
	public static String getContentFromServer(String  requestUrl) throws IOException {
		URLConnection conn = null;
		InputStream inputStream = null;
		URL url = new URL(requestUrl);
		conn = url.openConnection();
		HttpURLConnection httpConn = (HttpURLConnection) conn;
		httpConn.setRequestMethod("GET");
		httpConn.connect();
		if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			inputStream = httpConn.getInputStream();
		}
		return new String(readFully(inputStream));
	}

	private static byte[] readFully(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			byte[] buffer = new byte[1024];
			for (int count; (count = in.read(buffer)) != -1;) { // NOPMD
				out.write(buffer, 0, count);
			}
		} finally {
			in.close();
		}

		return out.toByteArray();
	}

}
