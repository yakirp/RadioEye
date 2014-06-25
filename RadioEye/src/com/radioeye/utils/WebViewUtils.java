package com.radioeye.utils;

import android.annotation.SuppressLint;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.radioeye.R;

 

public class WebViewUtils {

	// call this function and it will place the image at the center
		@SuppressLint("SetJavaScriptEnabled")
		public static void loadImageUrlToWebView(WebView mWebView, final String url,
				final WebViewClient callback) {

		//	WebView mWebView = (WebView) context.findViewById(webId);
			mWebView.getSettings().setJavaScriptEnabled(true);
			mWebView.getSettings().setBuiltInZoomControls(false);
			mWebView.getSettings().setDefaultZoom(ZoomDensity.FAR);
			mWebView.setWebViewClient(new WebViewClient() {

				public void onPageFinished(WebView view, String url) {

					if (callback != null) {
						callback.onPageFinished(view, url);
					}
				}
			});
			mWebView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (v.getId() == R.id.webview_top) {
						//menu.toggle();

					}

				}
			});
			mWebView.setOnTouchListener(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {

					return (event.getAction() == MotionEvent.ACTION_MOVE);
				}
			});

		 
			
			mWebView.loadDataWithBaseURL(url,
					"<html><center><img height=\"99%\" width=\"100%\" src=" + "'"
							+ url + "'" + "></html>", "text/html", "utf-8", "");

		}

}
