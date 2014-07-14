package com.radioeye;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.android.volley.toolbox.ImageLoader;
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
import com.pubnub.api.PubnubException;
import com.radioeye.clients.CloudinaryClient;
import com.radioeye.clients.FacebookClinet;
import com.radioeye.clients.PubnubClient;
import com.radioeye.clients.RadioEyeClient;
import com.radioeye.clients.RequestManager;
import com.radioeye.datastructure.NewImageMessageFromPublisher;
import com.radioeye.ui.SampleListFragment;
import com.radioeye.ui.SlidingUpPanelLayout;
import com.radioeye.ui.SlidingUpPanelLayout.PanelSlideListener;
import com.radioeye.utils.AppPreferences;
import com.radioeye.utils.Log;
import com.amazonaws.android.mobileanalytics.*;
import com.amazonaws.android.auth.CognitoCredentialsProvider;

 
/**
 * Main RadioEye activity
 * 
 * @author user
 * 
 */
public class MainActivity extends FragmentActivity  implements MenuCallback {
	
	private AmazonMobileAnalytics analytics;
	private SlidingUpPanelLayout slidingPanel;
	private String CurrentUserFacebookId = null;
	public static final String SAVED_STATE_ACTION_BAR_HIDDEN = "saved_state_action_bar_hidden";
	private Context context;
	private RadioEyeClient radioEyeClient;

	private MainActivity activity;
	private SlidingMenu menu;
  
	 private ImageLoader mImageLoader;
	private SwipeRefreshLayout swipeLayout;
	   
	
	/**
	 * This method gets called when the player completes a level.
	 * @param levelName the name of the level
	 * @param difficulty the difficulty setting
	 * @param timeToComplete the time to complete the level in seconds
	 * @param playerState the winning/losing state of the player
	 */
	public void AdView(String imageId, String publisher ) {
	        
	    //Create a Level Complete event with some attributes and metrics(measurements)
	    //Attributes and metrics can be added using with statements
	    AnalyticsEvent levelCompleteEvent = analytics.getEventClient().createEvent("AdView")
	            .withAttribute("ImageId", imageId)
	            .withAttribute("PublishereId", publisher)
	            .withMetric("n", 1.0);
	   
	       
	 
	    //Record the Level Complete event
	    analytics.getEventClient().recordEvent(levelCompleteEvent);
	    
	    Log.i("Event created");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		setContentView(R.layout.activity_main);
		  AppPreferences.getInstance(getApplicationContext());
		
		
		activity = this;

		setContext(this);

		RadioEyeApp.setAppContext(getApplicationContext());
		
		pubnub = new Pubnub("demo", "demo", "", false);
		
		
	 
		final CognitoCredentialsProvider cognitoProvider = new CognitoCredentialsProvider(
				this, // get the context for the current activity
			    "961397298997",
			    "us-east-1:835b8b9c-4983-4fd3-a7e5-8cfe6488736b",
			    "arn:aws:iam::961397298997:role/Cognito_RadioEyePollUnauth_DefaultRole",
			    "arn:aws:iam::961397298997:role/Cognito_RadioEyePollAuth_DefaultRole"
			);
		 
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				cognitoProvider.getIdentityId();
			
				
			}
		}).start();
	 
		
		 try {
		        analytics = new AmazonMobileAnalytics(
		                cognitoProvider,
		                getApplicationContext(),
		                "RadioEye"
		        );
		    } catch(InitializationException ex) {
		            Log.e("Failed to initialize Amazon Mobile Analytics "+ex);
		    }
		 
		setRadioEyeClient(new RadioEyeClient(activity));

		setSlidingPanel((SlidingUpPanelLayout) findViewById(R.id.sliding_layout));
		  
		 getSlidingPanel().setPanelHeight(0);
		 
		 getSlidingPanel().setPanelSlideListener(new PanelSlideListener() {
	            @Override
	            public void onPanelSlide(View panel, float slideOffset) {
	                 
	                setActionBarTranslation(getSlidingPanel().getCurrentParalaxOffset());
	            }

	            @Override     
	            public void onPanelExpanded(View panel) {
	              //  Log.i(TAG, "onPanelExpanded");

	            }      

	            @Override
	            public void onPanelCollapsed(View panel) {
	             //   Log.i(TAG, "onPanelCollapsed");

	            }
    
	            @Override
	            public void onPanelAnchored(View panel) {
	             //   Log.i(TAG, "onPanelAnchored");
	            }

	            @Override
	            public void onPanelHidden(View panel) {
	             //   Log.i(TAG, "onPanelHidden");
	            }  
	        });  
		
		 boolean actionBarHidden = savedInstanceState != null && savedInstanceState.getBoolean(SAVED_STATE_ACTION_BAR_HIDDEN, false);
	        if (actionBarHidden) {
	            int actionBarHeight = getActionBarHeight();
	            setActionBarTranslation(-actionBarHeight);//will "hide" an ActionBar
	        }  
	        
	        SampleListFragment s = new SampleListFragment();	        
	      
	     // configure the SlidingMenu
			menu = new SlidingMenu(this);
			menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			menu.setShadowWidthRes(R.dimen.shadow_width);
			menu.setShadowDrawable(R.drawable.shadow);
		    menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
			menu.setFadeDegree(0.35f);
			menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
			menu.setMenu(R.layout.menu_frame);
			   
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.menu_frame, s)
			.commit();
		 
			  s.setSlidingMenu(menu);
			  s.setCallback(this);
			  
			  /*
			  swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
			    swipeLayout.setOnRefreshListener(new OnRefreshListener() {
					
					@Override
					public void onRefresh() {
					Log.i("Refreh finish");
						
					}
				});
			    swipeLayout.setColorScheme(android.R.color.holo_blue_bright, 
			            android.R.color.holo_green_light, 
			            android.R.color.holo_orange_light, 
			            android.R.color.holo_red_light);
    */
		     getActionBar().setDisplayHomeAsUpEnabled(true);

			 RequestManager.getInstance(getApplicationContext());
			 
			 
			 RequestManager.getInstance().doRequest().login();

			 
			 mImageLoader =  RequestManager.getInstance().doRequest().getmImageLoader();
			 
			   
			 
			 
			 
			
			 
	          
	}    
    
	  
	 private int getActionBarHeight(){
	        int actionBarHeight = 0;
	        TypedValue tv = new TypedValue();
	        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
	            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
	        }
	        return actionBarHeight;
	    }
	 
    public void setActionBarTranslation(float y) {
        // Figure out the actionbar height
        int actionBarHeight = getActionBarHeight();
        // A hack to add the translation to the action bar
        ViewGroup content = ((ViewGroup) findViewById(android.R.id.content).getParent());
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

	private Pubnub pubnub;

	@Override
	protected void onPause() {

		super.onPause();

		// unSubscribe from pubnub
		PubnubClient.getInstane().unSubscribe(getCurrentUserFacebookId());

		pubnub.unsubscribe(getCurrentUserFacebookId());

		 if(analytics != null) {
		        analytics.getSessionClient().pauseSession();
		            //Attempt to send any events that have been recorded to the Mobile Analytics service.
		        analytics.getEventClient().submitEvents();
		    }
		 
		// finish();

	}

	 
	 
	@Override
	protected void onResume() {

		super.onResume();

		// TODO:: check network connection

		if(analytics != null)  {
	        analytics.getSessionClient().resumeSession();
	    }
		
		
		// Show loading dialog -> The Dialog window will cancel only after web
		// content loading finish.
		// need to change this logic.

//		setCurrentUserFacebookId(getRadioEyeClient().getAppPref()
//				.getSomeString("facebookID"));

	// 	getRadioEyeClient().showLoadingDialog();

//		if (getCurrentUserFacebookId() == null
//				|| getCurrentUserFacebookId() == "") {
//			startFacebookLogin();
//		} else {
//			startRadioEye();
//		}
		 startRadioEye();

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

											AppPreferences.getInstance().saveSomeString("profile_image",
													obj.getString("url"));
											
											 
											// radioClient.updateLoadingProfileImage(obj.getString("url"));
										} catch (JSONException e) {

											e.printStackTrace();
										}
									}
								}
							}).executeAsync();

					Log.i(user.getName());

//					getRadioEyeClient().getAppPref().saveSomeString(
//							"facebookId", user.getId());

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
		
		 
		
		String localChannel = AppPreferences.getInstance().getSomeString("lastChannel");
		
		if (!localChannel.equals("")) {
			
			getRadioEyeClient().showLoadingDialog();
			
		getRadioEyeClient().getAndLoadCurrentPublisherActiveImages(
				localChannel, getSlidingPanel());

		// init pubnub clinet
		initPubnub(localChannel);
		}
	}

	private void initPubnub(String channel) {

		 

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
					
					if (incomingImage.getImageType().equalsIgnoreCase("center")) {
						AdView(incomingImage.getImageUrl(),AppPreferences.getInstance().getSomeString("lastChannel"));
					}
					
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

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

	    int itemId = item.getItemId();
	    switch (itemId) {
	    case android.R.id.home:
	      menu.toggle();
	        break;

	    }

	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(SAVED_STATE_ACTION_BAR_HIDDEN, getSlidingPanel().isPanelExpanded());
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

		menu.toggle();
		pubnub.unsubscribe(AppPreferences.getInstance().getSomeString("lastChannel"));
		 
		AppPreferences.getInstance().saveSomeString("lastChannel", Channel);
		
	 
		
		startRadioEye();
 
		
	}

}