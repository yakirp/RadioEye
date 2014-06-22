package com.radioeye.clients;

import android.app.Activity;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

public class FacebookClinet {

	public static void startFacebookLogin(Activity activity,
			final Request.GraphUserCallback callback) {

		// start Facebook Login
		Session.openActiveSession(activity, true, new Session.StatusCallback() {

			// callback when session changes state
			@Override
			public void call(Session session, SessionState state,
					Exception exception) {

				if (session.isOpened()) {

					// make request to the /me API
					Request.newMeRequest(session,
							new Request.GraphUserCallback() {

								// callback after Graph API response
								// with user
								// object
								@Override
								public void onCompleted(GraphUser user,
										Response response) {
									if (user != null) {

										if (callback != null) {
											callback.onCompleted(user, response);
										}

									}
								}
							}).executeAsync();

				}
			}
		});

	}

}
