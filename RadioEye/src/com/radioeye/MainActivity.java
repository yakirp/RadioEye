package com.radioeye;

import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.google.gson.Gson;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;
import com.radioeye.clients.CloudinaryClient;
import com.radioeye.clients.FacebookClinet;
import com.radioeye.clients.PubnubClient;
import com.radioeye.clients.PubnubClient.PubnubCallback;
import com.radioeye.clients.RadioEyeClient;
import com.radioeye.datastructure.NewImageMessageFromPublisher;
import com.radioeye.ui.SlidingUpPanelLayout;
import com.radioeye.utils.AppPreferences;
import com.radioeye.utils.Log;

/**
 * Main RadioEye activity
 * 
 * @author user
 * 
 */
public class MainActivity extends Activity {
	private SlidingUpPanelLayout slidingPanel;
	private String CurrentUserFacebookId = null;

	private Context context;
	private RadioEyeClient radioEyeClient;

	private MainActivity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);

		activity = this;

		setContext(this);

		setRadioEyeClient(new RadioEyeClient(activity));

		setSlidingPanel((SlidingUpPanelLayout) findViewById(R.id.sliding_layout));

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

	private Pubnub pubnub;

	@Override
	protected void onPause() {

		super.onPause();

		// unSubscribe from pubnub
		PubnubClient.getInstane().unSubscribe(getCurrentUserFacebookId());

		pubnub.unsubscribe(getCurrentUserFacebookId());

		// finish();

	}

	@Override
	protected void onResume() {

		super.onResume();

		// TODO:: check network connection

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// Show loading dialog -> The Dialog window will cancel only after web
		// content loading finish.
		// need to change this logic.

		setCurrentUserFacebookId(getRadioEyeClient().getAppPref()
				.getSomeString("facebookID"));

		getRadioEyeClient().showLoadingDialog();

		if (getCurrentUserFacebookId() == null
				|| getCurrentUserFacebookId() == "") {
			startFacebookLogin();
		} else {
			startRadioEye();
		}
		 

	}

	private void startFacebookLogin() {
		FacebookClinet.startFacebookLogin(this, new GraphUserCallback() {

			@Override
			public void onCompleted(GraphUser user, Response response) {

				if (user != null) {

					Bundle params = new Bundle();
					// make request to the /me API
					params.putBoolean("redirect", false);
					params.putString("height", "200");
					params.putString("type", "normal");
					params.putString("width", "200");
					new Request(Session.getActiveSession(), "/me/picture",
							params, HttpMethod.GET, new Request.Callback() {

								// callback after Graph API response with user
								// object
								@Override
								public void onCompleted(Response response) {
									if (response != null) {

										Map<String, Object> g = response
												.getGraphObject().asMap();

										JSONObject obj;
										try {
											obj = new JSONObject(g.get("data")
													.toString());

											new AppPreferences(getContext())
													.saveSomeString(
															"profile_image",
															obj.getString("url"));

											// radioClient.updateLoadingProfileImage(obj.getString("url"));
										} catch (JSONException e) {

											e.printStackTrace();
										}
									}
								}
							}).executeAsync();

					Log.i(user.getName());

					getRadioEyeClient().getAppPref().saveSomeString(
							"facebookId", user.getId());

					setCurrentUserFacebookId(user.getId());

					startRadioEye();
				} else {
					// TODO:: Handle login problem
				}

			}

		});

	}

	private void startRadioEye() {
		// Get the publisher images from server
		// and load them
		getRadioEyeClient().getAndLoadCurrentPublisherActiveImages(
				getCurrentUserFacebookId(), getSlidingPanel());

		// init pubnub clinet
		initPubnub(getCurrentUserFacebookId());
	}

	private void initPubnub(String channel) {

		pubnub = new Pubnub("demo", "demo", "", false);

		try {
			pubnub.subscribe(channel, new Callback() {

				@Override
				public void successCallback(String channel, Object msg) {
					Log.i(channel + ": " + msg.toString());

					final NewImageMessageFromPublisher incomingImage = new Gson()
							.fromJson(msg.toString(),
									NewImageMessageFromPublisher.class);
					final String url = CloudinaryClient.getInstance()
							.generateWebPUrl(incomingImage.getImageUrl());

					// We get only the image id from server , so we init the
					// full image url
					incomingImage.setImageUrl(url);

					getRadioEyeClient().handleNewIncomingImage(incomingImage,
							getSlidingPanel());

				}
			});
		} catch (PubnubException e1) {

			e1.printStackTrace();
		}

	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

	}

	public String getCurrentUserFacebookId() {
		return "695432614";
		// return CurrentUserFacebookId;
	}

	public void setCurrentUserFacebookId(String currentUserFacebookId) {
		CurrentUserFacebookId = currentUserFacebookId;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public void onBackPressed() {
		if (getSlidingPanel() != null && getSlidingPanel().isPanelExpanded()
				|| getSlidingPanel().isPanelAnchored()) {
			getSlidingPanel().collapsePanel(null);
		} else {
			super.onBackPressed();
		}
	}

	public RadioEyeClient getRadioEyeClient() {
		return radioEyeClient;
	}

	public void setRadioEyeClient(RadioEyeClient radioEyeClient) {
		this.radioEyeClient = radioEyeClient;
	}

	public SlidingUpPanelLayout getSlidingPanel() {
		return slidingPanel;
	}

	public void setSlidingPanel(SlidingUpPanelLayout slidingPanel) {
		this.slidingPanel = slidingPanel;
	}

}