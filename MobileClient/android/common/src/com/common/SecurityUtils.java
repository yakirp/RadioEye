package com.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Base64;
import android.util.Log;

public class SecurityUtils {

	
	public static String md5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++)
				hexString.append(String.format("%02x", messageDigest[i]));
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
	public static void appSha(Context context , String PackageName) {
		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(PackageName,
					PackageManager.GET_SIGNATURES);
			for (android.content.pm.Signature signature : packageInfo.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				String key = new String(Base64.encode(md.digest(), 0));
				// String key = new String(Base64.encodeBytes(md.digest()));
				Log.e("Hash key", key);
			}
		} catch (NameNotFoundException e1) {
			Log.e("Name not found", e1.toString());
		}

		catch (NoSuchAlgorithmException e) {
			Log.e("No such an algorithm", e.toString());
		} catch (Exception e) {
			Log.e("Exception", e.toString());
		}
	}
	
}
