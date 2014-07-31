//package com.radioeye.clients;
//
//import java.util.HashMap;
//import java.util.Hashtable;
//import java.util.Map;
//import org.json.JSONArray;
//import org.json.JSONObject;
//
// 
//import com.pubnub.api.Callback;
//import com.pubnub.api.Pubnub;
//import com.pubnub.api.PubnubError;
//import com.pubnub.api.PubnubException;
//import com.radioeye.utils.Log;
//
//public class PubnubClient {
//  
//	public interface PubnubCallback {
//
//		public void onMessage(String channel, String msg);
//
//		public void onSubscribe();
//
//		public void onHistory(JSONArray msg);
//
//		public void onError(PubnubError msg);
//
//		public void disconnectCallback();
//
//		public void reconnectCallback();
//
//	}
//	
//	// private Hashtable<String, String> args = new Hashtable<String,
//	// String>(1);
//	private Pubnub pubnub;
//	private static PubnubClient instance;
//	private Map<String, PubnubCallback> listeners = new HashMap<String, PubnubCallback>();
// 
//
//	public static PubnubClient getInstane() {
//		if (instance == null) {
//			instance = new PubnubClient();
//		}
//		return instance;
//	}
//
//	private PubnubClient() {
//		super();
//
//		pubnub = new Pubnub("pub-79a72133-96ad-4cd2-84bb-c0769949cd64",
//				"sub-d5f18cdc-7402-11e1-9c68-75e14bafa916", "", false);
//
//		pubnub = new Pubnub("demo", "demo", "", false);
//
//		// pubnub.setUUID(UserData.getInstance().getUserId());
//	 
//
//	}
//
//	public Pubnub getPubnub() {
//		return pubnub;
//	}
//
//	public void publish(String channel, JSONObject msg) {
//		Hashtable<String, Object> args = new Hashtable<String, Object>(2);
//
//		args.put("channel", channel); // Channel Name
//
//		args.put("message", msg);
//
//		pubnub.publish(args, new Callback() {
//			@Override
//			public void successCallback(String channel, Object message) {
//
//				Log.i("PUBLISH : " + message);
//
//			}
//
//			@Override
//			public void errorCallback(String channel, PubnubError error) {
//
//				Log.i("PUBLISH : " + error);
//			}
//		});
//
//	}
//
//	public void subscribe(final String[] channels, final PubnubCallback callback) {
//
//		try {
//			pubnub.subscribe(channels, new Callback() {
//
//				@Override
//				public void disconnectCallback(String arg0, Object arg1) {
//					callback.disconnectCallback();
//					super.disconnectCallback(arg0, arg1);
//				}
//
//				@SuppressWarnings("deprecation")
//				@Override
//				public void errorCallback(String arg0, Object arg1) {
//					// TODO Auto-generated method stub
//					super.errorCallback(arg0, arg1);
//				}
//
//				@Override
//				public void reconnectCallback(String arg0, Object arg1) {
//					callback.reconnectCallback();
//					super.reconnectCallback(arg0, arg1);
//				}
//
//				@Override
//				public void connectCallback(String arg0, Object arg1) {
//					Log.i("connectCallback " + arg0);
//
//					super.connectCallback(arg0, arg1);
//				}
//
//				@Override
//				public void errorCallback(String arg0, PubnubError error) {
//					callback.onError(error);
//					super.errorCallback(arg0, error);
//				}
//
//				@Override
//				public void successCallback(String arg0, Object arg1) {
//
//					callback.onMessage(arg0, arg1.toString());
//
//				}
//			});
//		} catch (PubnubException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//	public void subscribe(final String channel, final PubnubCallback callback) {
//
//		try {
//
//			pubnub.subscribe(channel, new Callback() {
//
//				@Override
//				public void connectCallback(String arg0, Object arg1) {
//					Log.i("connectCallback " + arg0);
//					addListiner(channel, callback);
//					super.connectCallback(arg0, arg1);
//				}
//
//				@Override
//				public void errorCallback(String arg0, PubnubError error) {
//					Log.i("errorCallback " + error.getErrorString());
//					if (getListeners() != null) {
//						if (getListeners().get(channel) != null) {
//							getListeners().get(channel).onError(error);
//						}
//					}
//					super.errorCallback(arg0, error);
//				}
//
//				@Override
//				public void successCallback(String arg0, Object arg1) {
//					Log.i(arg1.toString());
//					getListeners().get(arg0).onMessage(arg0, arg1.toString());
//					// Channels.getCard(0).update(arg1.toString());
//
//				}
//			});
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	public void unSubscribe(String channel) {
//
//		
//		try {
//			pubnub.unsubscribe(channel);
//			removeListiner(channel);
//		} catch (Throwable t) {
//			
//		}
//		
//		 
//
//	}
//
//	public void unSubscribe(String[] channels) {
//
//		pubnub.unsubscribe(channels);
//		// removeListiner(channels);
//
//	}
//
//	public void presence(String channel, final Callback callback) {
//		try {
//			pubnub.presence("picstream0", new Callback() {
//
//				@Override
//				public void successCallback(String arg0, Object arg1) {
//
//					callback.successCallback(arg0, arg1);
//
//				}
//			});
//		} catch (PubnubException e) {
//
//		}
//	}
//
//	public void getHistory(String channel, int limit, PubnubCallback callback) {
//		addListiner(channel, callback);
//		pubnub.detailedHistory(channel, limit, new Callback() {
//			@Override
//			public void successCallback(String channel, Object message) {
//
//				getListeners().get(channel).onHistory((JSONArray) message);
//
//				// Channels.getCard(0).handleHistory(message);
//			}
//
//			@Override
//			public void errorCallback(String channel, PubnubError error) {
//				Log.i("DETAILED HISTORY : " + error);
//
//				getListeners().get(channel).onError(error);
//
//			}
//		});
//	}
//
//	public void addListiner(String channel, PubnubCallback callback) {
//
//		if (!getListeners().containsKey(channel)) {
//			if (callback != null) {
//				getListeners().put(channel, callback);
//			}
//		}
//
//	}
//
//	private void removeListiner(String channel) {
//
//		if (!getListeners().containsKey(channel)) {
//
//			getListeners().remove(channel);
//		}
//	}
//
//	public Map<String, PubnubCallback> getListeners() {
//		return listeners;
//	}
//
//	public void setListeners(Map<String, PubnubCallback> listeners) {
//		this.listeners = listeners;
//	}
//
// 
//
//}
