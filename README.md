RadioEyeAndroid
===============
RadioEye client for android.
 
When launching the app, you will be asking to login with your Facebook account.
After login, visit [AirFunction.com](http://airfunction.com/) and sign in with the same Facebook account.
Upload some images, and publish them. If everything is OK, you will see them in the app.


- **RadioEye** - main project
- **facebook** - facebook library (reference library for RadioEye project)
- **common**   - contain some common jar's (reference library for RadioEye project)


**The app flow:**
When upload and publish image from the web site, the image ID is publish via Pubnub to the [subcribers](https://github.com/yakirp/RadioEyeAndroid/blob/master/RadioEye/src/com/radioeye/MainActivity.java#L170-216).
Each message from pubnub contain the image id and the image type (top,center,ad).
the image is loaded to WebView.
If the image is cneter, we slide up the panel for 2.5 sec, and silde it down.


Everything work well until we leave the app (onPause) and return back (onResume).
Then, the sliding panel stop working.


Please take a look at [the main sliding function](https://github.com/yakirp/RadioEyeAndroid/blob/master/RadioEye/src/com/radioeye/ui/SlidingUpPanelLayout.java#L949-979) where [this](https://github.com/yakirp/RadioEyeAndroid/blob/master/RadioEye/src/com/radioeye/ui/SlidingUpPanelLayout.java#L963) code appear to slide the panel but nothing happen

**Note:** The SlidingUpPanelLayout code was taken from [this project](https://github.com/umano/AndroidSlidingUpPanel)
 
 
 
**one more thing:**

I saw that when [this](https://github.com/yakirp/RadioEyeAndroid/blob/master/RadioEye/src/com/radioeye/ui/SlidingUpPanelLayout.java#L195) variable is False , the sliding is ok, but after onPause() this variable turn to True, and the sliding dont work.
I don't understand the exact purpose of this variable.

