# Android Wiki

## Activity

### Lifecycle
- onCreate - init essential components, setContentView (called with intent)
- onRestart - 
- onStart - (about to be visible)
- onRestoreInstanceState - not typically used, see onCreate
- onResume - 
- onPause - commit persistant changes beyond session
- onSaveInstanceState - 
- onStop - (not visible)
- onDestroy - 

### Lifecycle in action
```
# [open]
Activity - onCreate (null)
Activity - onStart
Activity - onResume
# [close]
Activity - onPause
Activity - onSaveInstanceState (Bundle)
Activity - onStop

# [reopen]
Activity - onRestart
Activity - onStart
Activity - onResume

# [rotate]
Activity - onPause
Activity - onSaveInstanceState (Bundle)
Activity - onStop
Activity - onDestroy
(---)
Activity - onCreate (Bundle)
Activity - onStart
Activity - onRestoreInstanceState (Bundle)
Activity - onResume
```

- Tell if app is returning from a rotation, and not fresh : onCreate.Bundle != null
- Tell if app is pushed/popped?




view States are saved automatically (text fields)



### Start activity from another app:
```JAVA
// inside requesting Activity class
// manifest
<intent-filter>
	<action android:name="android.intent.action.SEND" />
	<category android:name="android.intent.category.DEFAULT" />
</intent-filter>
// blank intent
String packageName = "com.example.packagename";
String className = "MainActivity";
Intent intent = new Intent();
intent.setComponent( new ComponentName(packageName, packageName+"."+className) );
// add arguments
intent.putExtra("KEY_NAME_HERE","value here");
// call
int resultID = 123456789;
startActivityForResult(intent, resultID);
// ...
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data){
	Log.d(TAG,"onActivityResult: "+requestCode+" | "+resultCode+" | "+data);
}

// inside receiving Activity class
Intent intent = getIntent();
String data = intent.getStringExtra("KEY_NAME_HERE");
// ...
Intent intent = new Intent();
intent.putExtra("KEY_RESULT_HERE", "done");
this.setResult(Activity.RESULT_OK, intent);
finish();
```



http://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
InputMethodManager keyboard = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);




## Fragment






intent






## Service


run forever (independent of creator): Activity.startService() + Service.onStartCommand()
	 + onBind(return null;)
run as bound service: Activity. + Service. onBind()
run in foreground


entirety of the activity OR application lifetime?


registerReceiver
start
bind
unbind
stop



## Various Classes

TextView -- text label

EditText -- input text field

Button --

ScrollView -- 
	android:layout_width="fill_parent"
    android:layout_height="fill_parent"
	android:fillViewport="true"

FastScroller

ListView


checkbox 
dadio
toggle
spinner
picker

tabs:activity




TableLayout --

RelativeLayout --






DOIN SUM PLANNIN







