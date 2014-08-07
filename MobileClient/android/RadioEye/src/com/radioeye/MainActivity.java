package com.radioeye;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.Listener;
import com.android.volley.toolbox.NetworkImageView;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nineoldandroids.view.animation.AnimatorProxy;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;
import com.pubnub.api.PubnubSyncedObject;
import com.radioeye.clients.AwsMobileClient;
import com.radioeye.clients.FacebookClinet;
import com.radioeye.clients.RadioEyeClient;
import com.radioeye.datastructure.NewImageMessageFromPublisher;
import com.radioeye.ui.MenuListFragment;
import com.radioeye.ui.SlidingUpPanelLayout;
import com.radioeye.utils.AppPreferences;
import com.radioeye.utils.Log;
import com.radioeye.volley.MyVolley;
import com.radioeye.volley.RequestManager;

/**
 * Main RadioEye activity
 * 
 * @author yakirp
 * 
 */
public class MainActivity extends Activity implements MenuCallback {

	private SlidingUpPanelLayout slidingPanel;
	private String CurrentUserFacebookId = null;
	public static final String SAVED_STATE_ACTION_BAR_HIDDEN = "saved_state_action_bar_hidden";
	private Context context;
	private RadioEyeClient radioEyeClient;
	private MainActivity activity;
	private SlidingMenu menu;
	private Pubnub pubnub;
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mDrawerLayout;
	private SwipeRefreshLayout swipeLayout;
	private ListView mDrawerList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.main);

		activity = this;

		setContext(this);
		RadioEyeApp.setAppContext(getApplicationContext());

		pubnub = new Pubnub("pub-69159aa7-3bcf-4d09-ae25-3269f14acb6a",
				"sub-4d81bf51-1eb6-11e1-82b2-3d61f7276a67", "", false);
	//	pubnub.setCacheBusting(false);
		//pubnub.setOrigin("pubsub-beta");

		final PubnubSyncedObject myData = pubnub.createSyncObject("table",
				"users.yakir");
  
		try {
			myData.initSync(new Callback() {

				// Called when the initialization process connects the ObjectID
				// to PubNub
				@Override
				public void connectCallback(String channel, Object message) {
				 
				 

					
					
				 
					try {

						System.err.println(myData.toString());
						System.out.println(myData.toString(2));
					} catch (org.json.JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				// Called every time ObjectID is changed, starting with the
				// initialization process
				// that retrieves current state of the object
				@Override
				public void successCallback(String channel, Object message) {

					System.err.println("initSync()-> successCallback -> print message: "
							+ message.toString() + "</br>");
				 

				}

				// Called whenever any error occurs
				@Override
				public void errorCallback(String channel, PubnubError error) {
					System.err.println(System.currentTimeMillis() / 1000
							+ " : "

							+ error);

					 
				}

			});
		} catch (PubnubException e) {
			e.printStackTrace();
		}
		
		
		setRadioEyeClient(new RadioEyeClient(activity));

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.above_shadow,
				GravityCompat.START);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_navigation_drawer, /*
										 * nav drawer image to replace 'Up'
										 * caret
										 */
		R.string.action_contact, /* "open drawer" description for accessibility */
		R.string.action_contact /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle("s");
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle("d");
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};

		mDrawerToggle.syncState();

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		initialSlidingPanel();

		list();
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view

		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	private void initialSlidingMenu() {
		MenuListFragment s = new MenuListFragment();

		// configure the SlidingMenu
		menu = new SlidingMenu(this);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.menu_frame);

		// getSupportFragmentManager().beginTransaction()
		// .replace(R.id.menu_frame, s).commit();

		s.setSlidingMenu(menu);
		s.setCallback(this);

		// getActionBar().setDisplayHomeAsUpEnabled(true);

		getActionBar().setIcon(R.drawable.ic_navigation_drawer);

	}

	private void initialSlidingPanel() {
		setSlidingPanel((SlidingUpPanelLayout) findViewById(R.id.sliding_layout));

		getSlidingPanel().setPanelHeight(0);

	}

	public void list() {

		RequestManager
				.getInstance()
				.doRequest()
				.getJson("http://airfunction.com/server/php/testrds.php",
						new Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject response) {
								Log.i(response.toString());
								parseJSON(response);

							}
						});

		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				RequestManager
						.getInstance()
						.doRequest()
						.getJson(
								"http://airfunction.com/server/php/testrds.php",
								new Listener<JSONObject>() {

									@Override
									public void onResponse(JSONObject response) {
										Log.i(response.toString());
										parseJSON(response);

										swipeLayout.setRefreshing(false);
									}
								});

			}
		});
		swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);

	}

	private void parseJSON(JSONObject json) {
		try {
			SampleAdapter adapter = new SampleAdapter(this);

			// JSONObject value = json.getJSONArray("publishers");
			JSONArray items = json.getJSONArray("publishers");
			for (int i = 0; i < items.length(); i++) {

				JSONObject item = items.getJSONObject(i);
				Log.i(item.optString("username"));

				adapter.add(new publisherItems(item.optString("title"), item
						.optString("imageurl"), item
						.optString("userfacebookid"), item
						.optString("description"), item.optString("online"),
						item.optString("offlineimageurl")));

			}

			mDrawerList.setAdapter(adapter);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private int getActionBarHeight() {
		int actionBarHeight = 0;
		TypedValue tv = new TypedValue();
		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
					getResources().getDisplayMetrics());
		}
		return actionBarHeight;
	}

	public void setActionBarTranslation(float y) {
		// Figure out the actionbar height
		int actionBarHeight = getActionBarHeight();
		// A hack to add the translation to the action bar
		ViewGroup content = ((ViewGroup) findViewById(android.R.id.content)
				.getParent());
		int children = content.getChildCount();
		for (int i = 0; i < children; i++) {
			View child = content.getChildAt(i);
			if (child.getId() != android.R.id.content) {
				if (y <= -actionBarHeight) {
					child.setVisibility(View.GONE);
				} else {
					child.setVisibility(View.VISIBLE);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						child.setTranslationY(y);
					} else {
						AnimatorProxy.wrap(child).setTranslationY(y);
					}
				}
			}
		}
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

		pubnub.unsubscribe(getCurrentUserFacebookId());

		AwsMobileClient.getInstance().updateAwsAboutPauseSession();

		getRadioEyeClient().getLoadingDialog().close();

		RequestManager.getInstance().cancelAllTraffic();
	}

	@Override
	protected void onResume() {

		super.onResume();

		// TODO:: check network connection

		AwsMobileClient.getInstance().updateAwsAboutResumeSession();

		// Show loading dialog -> The Dialog window will cancel only after web
		// content loading finish.
		// need to change this logic.

		// setCurrentUserFacebookId(getRadioEyeClient().getAppPref()
		// .getSomeString("facebookID"));

		// getRadioEyeClient().showLoadingDialog();

		// if (getCurrentUserFacebookId() == null
		// || getCurrentUserFacebookId() == "") {
		// startFacebookLogin();
		// } else {
		// startRadioEye();
		// }

		if (Session.getActiveSession() != null) {
			System.err.println("============session==========");
		}

		startRadioEye();

	}

	private void startFacebookLogin() {

		FacebookClinet.startFacebookLogin(this, new GraphUserCallback() {

			@Override
			public void onCompleted(GraphUser user, Response response) {

				if (user != null) {

					AwsMobileClient.getInstance()
							.updateAwsWithFacebookSession();

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

											AppPreferences
													.getInstance()
													.purString(
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

					// getRadioEyeClient().getAppPref().saveSomeString(
					// "facebookId", user.getId());

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

		String localChannel = AppPreferences.getInstance().getString(
				"lastChannel");

		if (!localChannel.equals("")) {

			getRadioEyeClient().showLoadingDialog();

			// getRadioEyeClient().getAndLoadCurrentPublisherActiveImages(
			// localChannel, getSlidingPanel());

			// init pubnub clinet
			initPubnub(localChannel);
		}
	}

	private void initPubnub(String channel) {

		try {
			pubnub.subscribe(channel, new Callback() {

				@Override
				public void connectCallback(String channel, Object arg1) {

					Log.i("cc= "
							+ AppPreferences.getInstance().getString(
									"lastChannel"));

					pubnub.getState(
							AppPreferences.getInstance().getString(
									"lastChannel"), "my_uuid", new Callback() {

								@Override
								public void errorCallback(final String channel,
										PubnubError arg1) {
									Log.i("===========");
									Log.i(arg1.toString());

									getRadioEyeClient().postToUiThread(
											new Runnable() {

												@Override
												public void run() {
													Toast.makeText(
															context,
															"Network "
																	+ channel,
															Toast.LENGTH_LONG)
															.show();
													getRadioEyeClient()
															.getAndLoadCurrentPublisherActiveImages(
																	AppPreferences
																			.getInstance()
																			.getString(
																					"lastChannel"),
																	getSlidingPanel());

												}
											});

								}

								@Override
								public void successCallback(String channel,
										final Object response) {
									Log.i("==========================");
									Log.i(channel + " " + response.toString());
									if (response.toString().trim()
											.equalsIgnoreCase("{}")) {

										getRadioEyeClient().postToUiThread(
												new Runnable() {

													@Override
													public void run() {

														getRadioEyeClient()
																.getAndLoadCurrentPublisherActiveImages(
																		AppPreferences
																				.getInstance()
																				.getString(
																						"lastChannel"),
																		getSlidingPanel());

													}
												});

									} else {

										getRadioEyeClient().postToUiThread(
												new Runnable() {

													@Override
													public void run() {

														Toast.makeText(
																context,
																"Pubnub",
																Toast.LENGTH_LONG)
																.show();
														getRadioEyeClient()
																.handleCurrnetublisherImages(
																		response.toString(),
																		getSlidingPanel());

													}
												});
									}

								}
							});

				}          

				@Override
				public void successCallback(String channel, Object msg) {
					Log.i(channel + ": " + msg.toString());

					final NewImageMessageFromPublisher incomingImage = new Gson()
							.fromJson(msg.toString(),
									NewImageMessageFromPublisher.class);

					// We get only the image id from server , so we init the
					// full image url

					// incomingImage.setImage(url);

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

		return super.onCreateOptionsMenu(menu);
	}
    
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		int itemId = item.getItemId();
		switch (itemId) {
		case android.R.id.home:
			// menu.toggle();
			if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
				// drawer is open
				mDrawerLayout.closeDrawers();
			} else {
				mDrawerLayout.openDrawer(GravityCompat.START);
			}
			break;

		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle your other action bar items...

		return super.onOptionsItemSelected(item);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(SAVED_STATE_ACTION_BAR_HIDDEN, getSlidingPanel()
				.isPanelExpanded());
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

		if (menu.isMenuShowing()) {
			menu.showContent();
		}

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

	@Override
	public void onItemSelect(String Channel) {

		pubnub.unsubscribe(AppPreferences.getInstance()
				.getString("lastChannel"));

		AppPreferences.getInstance().purString("lastChannel", Channel);

		startRadioEye();

		mDrawerLayout.closeDrawers();

	}

	public class publisherItems {
		private String publisherName;
		private String channel;
		private String imageUrl;
		private String offlineimageUrl;
		private String description;
		private String online;

		public publisherItems(String tag, String imageUrl, String Channel,
				String description, String online, String offlineimageurl) {
			this.setPublisherName(tag);
			this.setImageUrl(imageUrl);
			this.channel = Channel;
			this.setDescription(description);
			this.setOnline(online);
			this.setOfflineimageUrl(offlineimageurl);

		}

		public String getChannel() {
			return channel;
		}

		public void setChannel(String channel) {
			channel = channel;
		}

		public String getPublisherName() {
			return publisherName;
		}

		public void setPublisherName(String publisherName) {
			this.publisherName = publisherName;
		}

		public String getImageUrl() {
			return imageUrl;
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getOfflineimageUrl() {
			return offlineimageUrl;
		}

		public void setOfflineimageUrl(String offlineimageUrl) {
			this.offlineimageUrl = offlineimageUrl;
		}

		public String getOnline() {
			return online;
		}

		public void setOnline(String online) {
			this.online = online;
		}
	}

	public class SampleAdapter extends ArrayAdapter<publisherItems> {

		public SampleAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.row, null);
			}

			String url = getItem(position).getOnline().equalsIgnoreCase("t") ? getItem(
					position).getImageUrl()
					: getItem(position).getOfflineimageUrl();

			NetworkImageView imageUrl = (NetworkImageView) convertView
					.findViewById(R.id.image_item);
			imageUrl.setImageUrl(url, MyVolley.getImageLoader());

			TextView title = (TextView) convertView
					.findViewById(R.id.lbl_contact_name_item);

			TextView description = (TextView) convertView
					.findViewById(R.id.lbl_contact_description_item);
			title.setText(getItem(position).getPublisherName());
			description.setText(getItem(position).getDescription());

			if (getItem(position).getOnline().equalsIgnoreCase("f")) {
				title.setTextColor(Color.GRAY);
				description.setTextColor(Color.GRAY);
			}

			final String channel = getItem(position).getChannel();

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					onItemSelect(channel);

				}

			});

			return convertView;
		}

	}

}