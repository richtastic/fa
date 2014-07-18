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
# (...)
Activity - onCreate (Bundle)
Activity - onStart
Activity - onRestoreInstanceState (Bundle)
Activity - onResume

# [startActivityForResult+intent for new activity (push)]
Activity - onPause
NewActivity - onCreate (null)
NewActivity - onStart
NewActivity - onResume
Activity - onSaveInstanceState (Bundle)
Activity - onStop

# [finish new activity (pop)]
NewActivity - onPause
Activity - onActivityResult
Activity - onRestart
Activity - onStart
Activity - onResume
NewActivity - onStop
NewActivity - onDestroy

```


- Tell if app is returning from a rotation, and not fresh : onCreate.Bundle != null
- Tell if app is pushed/popped? - onActivityResult




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

### Lifecycle
- onAttach
- onCreate - initialize essentials
- onCreateView - return a (root) view (first draw)
- onActivityCreated
- onStart
- onResume
- onPause - commit persistant changes beyond session
- onStop
- onDestroyView
- onDestroy
- onDetach


### Lifecycle in action
```
# [open/add]
Activity - onCreate (null)
Fragment - onAttach
Fragment - onCreate (null)
Fragment - onCreateView
Fragment - onActivityCreated (null)
Activity - onStart
Fragment - onStart
Activity - onResume
Fragment - onResume

# [close]
Fragment - onPause
Activity - onPause
Fragment - onSaveInstanceState (Bundle)
Activity - onSaveInstanceState (Bundle)
Fragment - onStop
Activity - onStop

# [reopen]
Activity - onRestart
Activity - onStart
Fragment - onStart
Activity - onResume
Fragment - onResume

# [remove]
Fragment - onPause
Fragment - onStop
Fragment - onDestroyView
Fragment - onDestroy
Fragment - onDetach

# [readd]
Fragment - onAttach
Fragment - onCreate (null)
Fragment - onCreateView
Fragment - onActivityCreated (null)
Fragment - onStart
Fragment - onResume
Fragment - onPause

# [rotate]
Fragment - onPause
Activity - onPause
Fragment - onSaveInstanceState (Bundle)
Activity - onSaveInstanceState (Bundle)
Fragment - onStop
Activity - onStop
Fragment - onDestroyView
Fragment - onDestroy
Fragment - onDetach
Activity - onDestroy
Fragment - onAttach
Fragment - onCreate (Bundle/null)
Activity - onCreate (Bundle)
Fragment - onCreateView
Fragment - onActivityCreated (Bundle/null)
Activity - onStart
Fragment - onStart
Activity - onRestoreInstanceState (Bundle)
Activity - onResume
Fragment - onResume

# [push]
Fragment - onPause
Activity - onPause
Fragment - onSaveInstanceState (Bundle)
Activity - onSaveInstanceState (Bundle)
Fragment - onStop
Activity - onStop

# [pop]
Activity - onRestart
Activity - onStart
Fragment - onStart
Activity - onResume
Fragment - onResume

```





intent






## Service
### Lifecycle
- onCreate
- onBind
- onHandleIntent
- onStartCommand
- onDestroy


### Lifecycle in action
```
# [start]

# []
```

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



## Size/DPI/Res

### Size
```
small
normal
large
xlarge
```

### DPI
```
  ldpi ~120 (0.75x)
  mdpi ~160 (1.0x)
  hdpi ~240 (1.5x)
 xhdpi ~320 (2.0x)
xxdhpi ~480 (4.0x)
```






## Layouts


### Custom

https://gist.github.com/martyglaubitz/c6e6cc8248bc9978274c



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






# DOIN SUM PLANNIN

### TODO:

* dynamic positioning
- saving fragment state
* service (disneyid) across activities 
- optimized lists (re-using rows/cells whatnot) - ListView?
- mem caching
- disk caching
- auto-loading/notifying image (receiver) on bitmap ready

- opening/playing video (from file)



CACHING OPTIONS
get an outdated image if available, but get fresh source copy as well
memory caching optional
disk caching optional

url-specific:
http/https
- allow for header (Authorization: Bearer bla, etc)
- test (GET),POST,PUT,DELETE
- getting back response code

image-specific:
Bitmap config (565)
show loading icon (image or system)
fade-in animation















## Libraries:

### JSON: GSON
```
# http://code.google.com/p/google-gson/downloads/detail?name=google-gson-2.2.4-release.zip
wget http://google-gson.googlecode.com/files/google-gson-2.2.4-release.zip
unzip google-gson-2.2.4-release.zip
cp ./google-gson-2.2.4-release/gson-2.2.4.jar ...
```










## JAVA
```JAVA
HashMap<String,String> requestProperties;
for(Entry<String,String> entry : requestProperties.entrySet()){
	connection.setRequestProperty(entry.getKey(),entry.getValue());
}

Set<String> properties = requestProperties.keySet();
Iterator<String> iter = properties.iterator();
String key, value;
while( iter.hasNext()){
	key = iter.next();
	value = requestProperties.get(key);
	connection.setRequestProperty(key,value);
}

-------------
ArrayList<Type> list;
for(Type obj : list){
	...
}
```
http://developer.android.com/reference/android/os/AsyncTask.html





