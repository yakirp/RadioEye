package com.radioeye.ui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.WindowManager.BadTokenException;

import com.radioeye.R;
import com.radioeye.utils.AppPreferences;

public class LoadingDialog {

	private Activity activity;
	private ProgressDialog progDailog;
	private AppPreferences appPref;

	public LoadingDialog(Activity activity) {
		super();

		this.activity = activity;
		appPref = new AppPreferences(activity);

	}

	public void showLoadingDialog() {
		if (progDailog == null) {
			progDailog = createProgressDialog(activity);
			progDailog.show();
		} else {
			progDailog.show();
		}

	}

	private ProgressDialog createProgressDialog(Context mContext) {
		ProgressDialog dialog = new ProgressDialog(mContext);

		try {
			dialog.show();
		} catch (BadTokenException e) {

		}
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.progressdialog);

		String url = appPref.getSomeString("profile_image");
		if (url != "") {
			try {
				updateLoadingProfileImage(dialog, url);
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

		return dialog;
	}

	private Bitmap mIcon_val;

	public void close() {
		progDailog.dismiss();
	}

	public void updateLoadingProfileImage(final ProgressDialog dialog,
			final String url) throws IOException {

		new Thread(new Runnable() {

			@Override
			public void run() {

				final CircularImageView profileImageView = (CircularImageView) dialog
						.findViewById(R.id.profileImage);
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						profileImageView.setBorderColor(activity.getResources()
								.getColor(R.color.GrayLight));
						profileImageView.setBorderWidth(10);
						profileImageView.addShadow();

						Animation.tada(profileImageView).start();

					}
				});

				URL newurl;
				try {
					newurl = new URL(url);

					mIcon_val = BitmapFactory.decodeStream(newurl
							.openConnection().getInputStream());

					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							profileImageView.setImageBitmap(mIcon_val);

						}
					});

				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}).start();

	}

}