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
import android.view.Window;
import android.view.WindowManager;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.google.gson.Gson;
import com.pubnub.api.PubnubError;
import com.radioeye.clients.CloudinaryClient;
import com.radioeye.clients.FacebookClinet;
import com.radioeye.clients.PubnubClient;
import com.radioeye.clients.PubnubClient.PubnubCallback;
import com.radioeye.clients.RadioEyeClient;
import com.radioeye.datastructure.NewImageMessageFromPublisher;
import com.radioeye.utils.AppPreferences;
import com.radioeye.utils.Log;

/**
 * Main RadioEye activity
 * 
 * @author user
 * 
 */  
public class MainActivity extends Activity {

	private String CurrentUserFacebookId = null;
	private RadioEyeClient radioClient;
	private Context context;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);

	 

		setContext(this);
		
		setRadioClient(new RadioEyeClient(this));

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

	@Override
	protected void onPause() {

		super.onPause();

		// unSubscribe from pubnub
		PubnubClient.getInstane().unSubscribe(getCurrentUserFacebookId());

	}

	@Override
	protected void onResume() {

		super.onResume();

	 
		
		 
		
		
		//TODO:: check network connection
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// Show loading dialog -> The Dialog window will cancel only after web content loading finish. 
		// need to change this logic.
		 

		setCurrentUserFacebookId(getRadioClient().getAppPref().getSomeString("facebookID"));
		
		getRadioClient().showLoadingDialog();
		
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

					getRadioClient().getAppPref().saveSomeString("facebookId", user.getId());
					
					setCurrentUserFacebookId(user.getId());

					startRadioEye();
				} else {
					//TODO:: Handle login problem
				}

			}

		});

	}

	private void startRadioEye() {
		// Get the publisher images from server
		// and load them
		getRadioClient().getAndLoadCurrentPublisherActiveImages(
				getCurrentUserFacebookId());

		// init pubnub clinet
		initPubnub(getCurrentUserFacebookId());
	}

	private void initPubnub(String channel) {
		PubnubClient.getInstane().subscribe(channel, new PubnubCallback() {

			@Override
			public void onHistory(JSONArray msg) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(PubnubError msg) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onMessage(String channel, String msg) {

				Log.i(channel + ": " + msg);

				final NewImageMessageFromPublisher incomingImage = new Gson()
						.fromJson(msg, NewImageMessageFromPublisher.class);
				final String url = CloudinaryClient.getInstance()
						.generateWebPUrl(incomingImage.getImageUrl());

				// We get only the image id from server , so we init the
				// full image url
				incomingImage.setImageUrl(url);

				 getRadioClient().handleNewIncomingImage(incomingImage);
				
				

			}

			@Override
			public void onSubscribe() {

			}

			@Override
			public void disconnectCallback() {

			}

			@Override
			public void reconnectCallback() {

			}
		});
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
		return CurrentUserFacebookId;
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

	public RadioEyeClient getRadioClient() {
		return radioClient;
	}

	public void setRadioClient(RadioEyeClient radioClient) {
		this.radioClient = radioClient;
	}

	@Override
    public void onBackPressed() {
        if (getRadioClient().getSlidingPanel() != null && getRadioClient().getSlidingPanel().isPanelExpanded() || getRadioClient().getSlidingPanel().isPanelAnchored()) {
        	getRadioClient().getSlidingPanel().collapsePanel(null);
        } else {
            super.onBackPressed();
        }
    }
	
	 
	 
	
}