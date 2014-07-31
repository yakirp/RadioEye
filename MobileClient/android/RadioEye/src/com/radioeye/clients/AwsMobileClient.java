package com.radioeye.clients;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.android.auth.CognitoCredentialsProvider;
import com.amazonaws.android.mobileanalytics.AmazonMobileAnalytics;
import com.amazonaws.android.mobileanalytics.AnalyticsEvent;
import com.amazonaws.android.mobileanalytics.InitializationException;
import com.facebook.Session;
import com.radioeye.utils.Log;

import android.content.Context;

public class AwsMobileClient {

	private static AwsMobileClient instance;
	private CognitoCredentialsProvider cognitoProvider;
	private Context context;
	private AmazonMobileAnalytics analytics;

	private AwsMobileClient(Context context) {
		this.context = context;
		InitObjects();
	}
	
	/**
	 * This method gets called when the player completes a level.
	 * 
	 * @param levelName
	 *            the name of the level
	 * @param difficulty
	 *            the difficulty setting
	 * @param timeToComplete
	 *            the time to complete the level in seconds
	 * @param playerState
	 *            the winning/losing state of the player
	 */
	public void AdView(String imageId, String publisher) {

		// Create a Level Complete event with some attributes and
		// metrics(measurements)
		// Attributes and metrics can be added using with statements
		AnalyticsEvent levelCompleteEvent = AwsMobileClient.getInstance().getAnalytics().getEventClient()
				.createEvent("AdView").withAttribute("ImageId", imageId)
				.withAttribute("PublishereId", publisher);

		// Record the Level Complete event
		AwsMobileClient.getInstance().getAnalytics().getEventClient().recordEvent(levelCompleteEvent);

		Log.i("Event created");
	}
	
	public void updateAwsWithFacebookSession() {
		Map<String, String> logins = new HashMap<String, String>();
		logins.put("graph.facebook.com", Session.getActiveSession()
				.getAccessToken());
		AwsMobileClient.getInstance().getCognitoProvider()
				.withLogins(logins);
	}
	
	public void updateAwsAboutResumeSession() {
		if (getAnalytics() != null) {
			getAnalytics().getSessionClient().resumeSession();
		}
	}
	
	public void updateAwsAboutPauseSession() {
		if (getAnalytics() != null) {
			getAnalytics().getSessionClient().pauseSession();
			// Attempt to send any events that have been recorded to the Mobile
			// Analytics service.
			new Thread(new Runnable() {

				@Override
				public void run() {
					getAnalytics().getEventClient().submitEvents();

				}
			}).start();

		}
	}
	

	private void InitObjects() {
		setCognitoProvider(new CognitoCredentialsProvider(
				context, // get the context for the current activity
				"961397298997",
				"us-east-1:835b8b9c-4983-4fd3-a7e5-8cfe6488736b",
				"arn:aws:iam::961397298997:role/Cognito_RadioEyePollUnauth_DefaultRole",
				"arn:aws:iam::961397298997:role/Cognito_RadioEyePollAuth_DefaultRole"));

		new Thread(new Runnable() {

			@Override
			public void run() {
				getCognitoProvider().getIdentityId();

			}
		}).start();

		try {
			setAnalytics(new AmazonMobileAnalytics(getCognitoProvider(),
					context.getApplicationContext(), "RadioEye"));
		} catch (InitializationException ex) {
			Log.e("Failed to initialize Amazon Mobile Analytics " + ex);
		}
	}

	public static AwsMobileClient getInstance(Context context) {
		if (instance == null) {
			instance = new AwsMobileClient(context);
		}
		return instance;
	}

	public static AwsMobileClient getInstance() {

		return instance;
	}

	public AmazonMobileAnalytics getAnalytics() {
		return analytics;
	}

	public void setAnalytics(AmazonMobileAnalytics analytics) {
		this.analytics = analytics;
	}

	public CognitoCredentialsProvider getCognitoProvider() {
		return cognitoProvider;
	}

	public void setCognitoProvider(CognitoCredentialsProvider cognitoProvider) {
		this.cognitoProvider = cognitoProvider;
	}

}
