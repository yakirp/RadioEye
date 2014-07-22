package com.radioeye.clients;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
 
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
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
import com.radioeye.volley.RequestManager;

public class RadioEyeClient {

	private static final int AD_MINIMUM_SHOWING_TIME_IN_MILISECIME = 2500;
	private static final String BASE_URL = RadioEyeApp.getBaseUrl();
	private static final String GET_PUBLISHER_IMAGES_BASE_URL = BASE_URL
			+ "/server/php/getUserCurrentImages.php?userId=";

	private Handler mainHandler;

	private Activity activity;

	private LoadingDialog loadingDialog;

	public RadioEyeClient(Activity activity) {

		super();
		this.activity = activity;
		// set UI thread
		setMainHandler(new Handler(Looper.getMainLooper()));

		setLoadingDialog(new LoadingDialog(activity));

	}

	public void showLoadingDialog() {

		getLoadingDialog().showLoadingDialog();

	}

	private String currentAdImageId;

	/**
	 * handle new image
	 * 
	 * @param image
	 */
	public void handleNewIncomingImage(
			final NewImageMessageFromPublisher image,
			final SlidingUpPanelLayout panel) {
		postToUiThread(new Runnable() {

			@Override
			public void run() {

				String url = CloudinaryClient.getInstance().generateWebPUrl(
						image.getImage());

				if (image.getImageType().equals("top")) {

					loadImage(url, R.id.webview_top, false, panel, null);

				}

				if (image.getImageType().equals("center")) {

					loadImage(url, R.id.webview_center, true, panel, null);

				}

				if (image.getImageType().equals("ad")) {

					currentAdImageId = image.getImage();

					loadImage(url, R.id.webview_ad, false, panel, null);

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
	public void getAndLoadCurrentPublisherActiveImages(
			final String publisherId, final SlidingUpPanelLayout panel) {

		RequestManager
				.getInstance()
				.doRequest()
				.getJson(GET_PUBLISHER_IMAGES_BASE_URL + publisherId,
						new Listener<JSONObject>() {

							@Override
							public void onResponse(final JSONObject response) {
								Log.i(response.toString());

								postToUiThread(new Runnable() {

									@Override
									public void run() {

										handleCurrnetublisherImages(
												response.toString(), panel);

									}
								});

							}
						});

	}

	/**
	 * Load to webview images from json
	 * 
	 * @param json
	 *            contain the publisher images key
	 */
	public void handleCurrnetublisherImages(String json,
			final SlidingUpPanelLayout panel) {

		final CurrentPublisherImagesFromServer allImages = new Gson().fromJson(
				json, CurrentPublisherImagesFromServer.class);

		// Load Top image
		String url = CloudinaryClient.getInstance().generateWebPUrl(
				allImages.getTop());

		final boolean[] loadedImages = { false, false, false };

		// Load the top image
		loadImage(url, R.id.webview_top, false, panel, new TaskCallback() {

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

		loadImage(url, R.id.webview_ad, false, panel, new TaskCallback() {

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

		loadImage(url, R.id.webview_center, false, panel, new TaskCallback() {

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
	 * @param loadedImages
	 *            array of
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
	 * @param url
	 *            to load
	 * @param webId
	 *            to put in
	 * @param isShowAd
	 *            is show ad in the downloadin process
	 * @param callback
	 *            to client
	 */
	private void loadImage(final String url, final int webId,
			final Boolean isShowAd, final SlidingUpPanelLayout panel,
			final TaskCallback callback) {

		postToUiThread(new Runnable() {

			@Override
			public void run() {

				// if we need to show ad before loading image
				if (isShowAd) {

					// expand the ad panel

					panel.expandPanel(new Panelcallback() {

						@Override
						public void onCollapsFinish() {

							AwsMobileClient.getInstance().AdView(
									currentAdImageId,
									AppPreferences.getInstance().getString(
											"lastChannel"));

							final long startTime = System.currentTimeMillis();

							RequestManager.getInstance().doRequest()
									.loadImage(url, new ImageListener() {

										@Override
										public void onErrorResponse(
												VolleyError error) {
											Log.e("Image Load Error: "
													+ error.getMessage());
											Log.e(url);
											Log.e(String
													.valueOf(error.networkResponse.statusCode));
											error.printStackTrace();
										}

										@Override
										public void onResponse(
												ImageContainer response,
												boolean isImmediate) {
											ImageView avatar = (ImageView) activity
													.findViewById(webId);
 
											avatar.setImageBitmap(response
													.getBitmap());

									 
											
											while (avatar.getDrawable()  == null) {

											}

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
															panel.collapsePanel(new Panelcallback() {

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

					}); // expandPane

				} // if (isShowAd)
				else {
					ImageView avatar = (ImageView) activity.findViewById(webId);

					RequestManager.getInstance().doRequest()
							.loadImage(url, new ImageListener() {

								@Override
								public void onErrorResponse(
										 VolleyError error) {
									// TODO Auto-generated method stub

								}

								@Override
								public void onResponse(ImageContainer response,
										boolean isImmediate) {
									ImageView avatar = (ImageView) activity
											.findViewById(webId);

									avatar.setImageBitmap(response.getBitmap());

									if (callback != null) {
										callback.onSuccess();
									}

								}
							});

				}

			}
		});

	}

	public void postToUiThread(Runnable runnable) {

		// Method 1 :
		// getMainHandler().post(runnable);

		// Method 2:
		this.activity.runOnUiThread(runnable);

	}

	public LoadingDialog getLoadingDialog() {
		return loadingDialog;
	}

	public void setLoadingDialog(LoadingDialog loadingDialog) {
		this.loadingDialog = loadingDialog;
	}

	public Handler getMainHandler() {
		return mainHandler;
	}

	public void setMainHandler(Handler mainHandler) {
		this.mainHandler = mainHandler;
	}

}
