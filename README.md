RadioEyeAndroid
===============
RadioEye client for android.
 
When launching the app, you will be asking to login with your Facebook account.
After login, visit [AirFunction.com](http://airfunction.com/) and sign in with the same Facebook account.
Upload some images, and publish them. If everything is OK, you will see them in the app.


- RadioEye - main project
- facebook - facebook library (reference library for RadioEye project)
- common   - contain some common jar's (reference library for RadioEye project)


Everything work well until we leave the app (onPause) and return back (onResume).
Then, the sliding panel stop working.



[The main sliding function](https://github.com/yakirp/RadioEyeAndroid/blob/master/RadioEye/src/com/radioeye/ui/SlidingUpPanelLayout.java#L963-977)

Note: the code is from [here](https://github.com/umano/AndroidSlidingUpPanel)
 
 

