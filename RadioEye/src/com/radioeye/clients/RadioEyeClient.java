package com.radioeye.clients;

import java.io.IOException;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.common.TaskCallback;
import com.google.gson.Gson;

import com.radioeye.R;
import com.radioeye.RadioEyeApp;
import com.radioeye.datastructure.CurrentPublisherImagesFromServer;
import com.radioeye.datastructure.NewImageMessageFromPublisher;
import com.radioeye.ui.LoadingDialog;
import com.radioeye.ui.SlidingUpPanelLayout;
 
import com.radioeye.ui.SlidingUpPanelLayout.Panelcallback;
import com.radioeye.utils.AppPreferences;
import com.radioeye.utils.Log;
import com.radioeye.utils.WebViewUtils;

public class RadioEyeClient {

	private static final int AD_MINIMUM_SHOWING_TIME_IN_MILISECIME = 2500;
	private static final String BASE_URL = RadioEyeApp.getBaseUrl();
	private static final String GET_PUBLISHER_IMAGES_BASE_URL = BASE_URL
			+ "/server/php/getUserCurrentImages.php?userId=";
	private SlidingUpPanelLayout slidingPanel;
	private Handler mainHandler;

	private Activity activity;

	private AppPreferences appPref;
	private LoadingDialog loadingDialog;

	private static RadioEyeClient instance;
	
	public static RadioEyeClient with(Activity activity) {
		if (instance == null) {
			instance = new RadioEyeClient(activity);
		}
		return instance;
	}
	
	
	private RadioEyeClient(Activity activity) {

		super();
		this.activity = activity;
		// set UI thread
		setMainHandler(new Handler(Looper.getMainLooper()));

		setSlidingPanel((SlidingUpPanelLayout) activity
				.findViewById(R.id.sliding_layout));

		/* Sliding menu : Not is use at this moment */
		// initSlidingMenu();

		setAppPref(new AppPreferences(activity));

		setLoadingDialog(new LoadingDialog(activity));

	}

	 
	public void showLoadingDialog() {

		getLoadingDialog().showLoadingDialog();

	}
	
	
	/**
	 * handle new image
	 * 
	 * @param image
	 */
	public void handleNewIncomingImage(final NewImageMessageFromPublisher image) {
		postToUiThread(new Runnable() {

			@Override
			public void run() {

				if (image.getImageType().equals("top")) {

					loadImage(image.getImageUrl(), R.id.webview_top, false,
							null);

				}

				if (image.getImageType().equals("center")) {

					loadImage(image.getImageUrl(), R.id.webview_center, true,
							null);
					
			

				}

				if (image.getImageType().equals("ad")) {

					loadImage(image.getImageUrl(), R.id.webview_ad, false, null);

				}
			}
		});
	}

	/**
	 * Get and load to the webViews the images according to the publisherId (at
	 * this moment the publisher is the current facebook user id)
	 * 
	 * @param publisherId
	 *            that we want to load his images
	 */
	public void getAndLoadCurrentPublisherActiveImages(final String publisherId) {
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					final String json = HttpClient
							.getContentFromServer(GET_PUBLISHER_IMAGES_BASE_URL
									+ publisherId);

					postToUiThread(new Runnable() {

						@Override
						public void run() {

							handleCurrnetublisherImages(json);

						}
					});

				} catch (IOException e) { 
					Log.e(e.getMessage());
				}

			}

		}).start();
	}

	/**
	 * Init the sliding menu
	 */
	//private void initSlidingMenu() {
		// configure the SlidingMenu
		// menu = new SlidingMenu(this.activity);
		// menu.setMode(SlidingMenu.LEFT);
		// menu.setTouchModeAbove(SlidingMenu.LEFT);
		//
		// menu.setShadowWidthRes(R.dimen.shadow_width);
		// menu.setShadowDrawable(R.drawable.shadow);
		// menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// menu.setFadeDegree(0.35f);
		// menu.attachToActivity(this.activity, SlidingMenu.SLIDING_CONTENT);
		// menu.setMenu(R.layout.gallery);

		// menu = new SlidingMenu(this.activity);
		// menu.setMode(SlidingMenu.LEFT);
		// menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		// menu.setShadowWidthRes(R.dimen.shadow_width);
		// menu.setShadowDrawable(R.drawable.shadow);
		// menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// menu.setFadeDegree(0.35f);
		// menu.attachToActivity(this.activity, SlidingMenu.SLIDING_CONTENT);
		// menu.setMenu(R.layout.gallery);
//	}

	/**
	 * Load to webview images from json
	 * 
	 * @param json
	 *            contain the publisher images key
	 */
	private void handleCurrnetublisherImages(String json) {

		final CurrentPublisherImagesFromServer allImages = new Gson().fromJson(
				json, CurrentPublisherImagesFromServer.class);

		// Load Top image
		String url = CloudinaryClient.getInstance().generateWebPUrl(
				allImages.getTop());

		final boolean[] loadedImages = { false, false, false };

		// Load the top image
		loadImage(url, R.id.webview_top, false, new TaskCallback() {

			@Override
			public void onSuccess() {

				loadedImages[0] = true;
				checkIfLoadingFInish(loadedImages);

			}

			@Override
			public void onFailure() {
				// TODO Auto-generated method stub

			}
		});

		// Load Ad image
		url = CloudinaryClient.getInstance().generateWebPUrl(allImages.getAd());

		loadImage(url, R.id.webview_ad, false, new TaskCallback() {

			@Override
			public void onSuccess() {
				loadedImages[1] = true;

				checkIfLoadingFInish(loadedImages);

			}

			@Override
			public void onFailure() {
				// TODO Auto-generated method stub

			}
		});

		// Load center image
		url = CloudinaryClient.getInstance().generateWebPUrl(
				allImages.getCenter());

		loadImage(url, R.id.webview_center, false, new TaskCallback() {

			@Override
			public void onSuccess() {
				loadedImages[2] = true;

				checkIfLoadingFInish(loadedImages);

			}

			@Override
			public void onFailure() {
				// TODO Auto-generated method stub

			}
		});

	}

	/**
	 * Check if all images loaded
	 * 
	 * @param loadedImages array of
	 */
	private void checkIfLoadingFInish(final boolean[] loadedImages) {

		for (boolean image : loadedImages) {
			if (!image)
				return;

		}
		// if all images loaded , close the dialog
		getLoadingDialog().close();

	}

	/**
	 * Load image to webview from url
	 * 
	 * @param url to load
	 * @param webId to put in
	 * @param isShowAd is show ad in the downloadin process
	 * @param callback to client
	 */
	private void loadImage(final String url, final int webId,
			final Boolean isShowAd, final TaskCallback callback) {
		
		 
		
		postToUiThread(new Runnable() {

			@Override
			public void run() {
				System.err.println("==loadImage==1=====");
				// if we need to show ad before loading image
				if (isShowAd) {
					System.err.println("==loadImage==2=====");
					// expand the ad panel
					
					getSlidingPanel().expandPanel(new Panelcallback() {
						
						 
					 
  
						@Override
						public void onCollapsFinish() {
							System.err.println("==loadImage==3=====");
							final long startTime = System.currentTimeMillis();
							// load the url to the webview
							WebViewUtils.loadImageUrlToWebView(
									(WebView) activity.findViewById(webId),
									url, new WebViewClient() {

										public void onPageFinished(
												WebView view, String url) {

											// After loading, we wait for some
											// milisec
											// before closing the ad panel
											new Thread(new Runnable() {

												@Override
												public void run() {
													// we wait the ad minimum
													// time
													while (System
															.currentTimeMillis()
															- startTime <= AD_MINIMUM_SHOWING_TIME_IN_MILISECIME) {

													}
													// now we close the ad panel
													postToUiThread(new Runnable() {

														@Override
														public void run() {
															getSlidingPanel().collapsePanel(new Panelcallback() {
																
																 

																				@Override
																				public void onCollapsFinish() {
																					if (callback != null) {
																						callback.onSuccess();
																					}

																				};

																			});

														}
													});

												}

											}).start();
										}
									});

						} // after sliding panel with the ad is up

					}); //expandPane

				} // if (isShowAd)
				else {
					// load the url to the webview with no ad
					WebViewUtils.loadImageUrlToWebView(
							(WebView) activity.findViewById(webId), url,
							new WebViewClient() {

								public void onPageFinished(WebView view,
										String url) {
									if (callback != null) {
										callback.onSuccess();
									}
								}
							});
				} 
			
			}
		});
  
	}    

	private void postToUiThread(Runnable runnable) {

		
		//Method 1 :
		//getMainHandler().post(runnable);
		
		//Method 2:
		this.activity.runOnUiThread(runnable);
  
	}

	public AppPreferences getAppPref() {
		return appPref;
	}

	public void setAppPref(AppPreferences appPref) {
		this.appPref = appPref;
	}

	public LoadingDialog getLoadingDialog() {
		return loadingDialog;
	}

	public void setLoadingDialog(LoadingDialog loadingDialog) {
		this.loadingDialog = loadingDialog;
	}

	public SlidingUpPanelLayout getSlidingPanel() {
		return slidingPanel;
	}

	public void setSlidingPanel(SlidingUpPanelLayout slidingPanel) {
		this.slidingPanel = slidingPanel;
		
		//slidingPanel.setSlidingEnabled(false);
		
	}

	public Handler getMainHandler() {
		return mainHandler;
	}

	public void setMainHandler(Handler mainHandler) {
		this.mainHandler = mainHandler;
	}


	 


	 

}
