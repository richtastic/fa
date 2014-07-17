package com.richtastic.structuresystem;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.HashMap;

import com.google.gson.JsonObject;
import com.richtastic.code.*;
import com.richtastic.code.Networking.WebTask;

import android.support.v7.app.ActionBarActivity;
//import android.support.v4.app.*;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.util.*;
import android.view.*;
import android.widget.ImageView;

public class StartupActivity extends ActionBarActivity {
	private static String TAG = "StartupActivity";
	private static String STATE_DATA = "STATE_DATA";
	// ------------------------------------------------------------------------------------------
	private int data = 0; 
	// ------------------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.d(TAG, "StartupActivity - onCreate ("+savedInstanceState+")");
		Intent intent = getIntent();
		Log.d(TAG,"intent: "+intent);
		
		// check saved state
		if(savedInstanceState != null){ // restore
			Log.d(TAG,"restoring state");
			Log.d(TAG,"found: "+savedInstanceState.getInt(STATE_DATA));
		}else{ // new
			Log.d(TAG,"new from scratch "+data);
//			Intent serviceIntent = new Intent(this, ReconstructionService.class);
//			startService(serviceIntent);
			
//stopService(serviceIntent);
		}
		//
		setContentView(R.layout.activity_startup);
		
		// home fragment
		View fragmentContainer = findViewById(R.id.fragment_container); 
		if(fragmentContainer != null){
			Log.d(TAG,"has fragment container");
			HomeMenuFragment homeFragment = new HomeMenuFragment();
			homeFragment.setArguments(new Bundle());
			getFragmentManager().beginTransaction().add(R.id.fragment_container, homeFragment).commit();
		}else{
			Log.d(TAG,"no fragment container");
		}
data = 1;
		
	}
	@Override
	protected void onStart(){
		super.onStart();
		Log.d(TAG, "StartupActivity - onStart");
	}
	@Override
	protected void onRestart(){
		super.onRestart();
		Log.d(TAG, "StartupActivity - onRestart "+data);
	}
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
		Log.d(TAG, "StartupActivity - onRestoreInstanceState ("+savedInstanceState+")");
	}
	@Override
	protected void onResume(){
		super.onResume();
		Log.d(TAG, "StartupActivity - onResume "+data);
	}
	@Override
	protected void onPause(){
		super.onPause();
		Log.d(TAG, "StartupActivity - onPause");
	}
	@Override
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		Log.d(TAG, "StartupActivity - onSaveInstanceState ("+outState+")");
		outState.putInt(STATE_DATA,data);
	}
	@Override
	protected void onStop(){
		super.onStop();
		Log.d(TAG, "StartupActivity - onStop "+data);
	}
	@Override
	protected void onDestroy(){
		super.onDestroy();
		Log.d(TAG, "StartupActivity - onDestroy");
	}
	// ...
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.startup, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	// ------------------------------------------------------------------------------------------
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		Log.d(TAG,"onActivityResult: "+requestCode+" | "+resultCode+" | "+data);
	}
	@Override
	public void finish(){
		Intent intent = new Intent();
		intent.putExtra("KEY_RESULT_HERE", "done");
		this.setResult(Activity.RESULT_OK, intent);
		super.finish();
	}
	
	public void onClickGalleryButton(View v){
		Log.d(TAG,"gallery...");
		Intent pushIntent = new Intent(this,SubmissionGalleryActivity.class);
		//pushIntent.putExtra("EXTRA","qwe");
		startActivityForResult(pushIntent, 99);
	}
	public void onClickFinishButton(View v){
		Log.d(TAG,"finish...");
		finish();
	}
	public void onClickLoadButton(View v){
		Log.d(TAG,"set content");
		View img = this.findViewById(R.id.display_image);
		if(img!=null){
			ImageView image = (ImageView)img;
			//Drawable draw = getResources().getDrawable(R.drawable.button_menu);
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.button_menu);
			image.setImageBitmap(bitmap);
			startSingleRequest();
		}
	}
	private void startSingleRequest(){
		// Networking.EVENT_WEB_CALL_COMPLETE
//		// load json
//		HashMap<String,Object> hash = new HashMap<String,Object>();
//		hash.put(Networking.PARAM_URL,"http://www.reddit.com/.json");
//		hash.put(Networking.PARAM_EXPECTED,Networking.TYPE_EXPECTED_JSON);
//		hash.put(Networking.PARAM_CALLBACK,new Callback(){
//			public void callback(Object... params){
//				if(params.length>1){
//					JsonObject response = (JsonObject)params[1];
//					Log.d(TAG,"response: "+response);
//					Log.d(TAG,"type: "+params[2]);
//				}
//			}
//		});
//		new WebTask().execute(hash);
		
//		// load string
//		HashMap<String,Object> hash = new HashMap<String,Object>();
//		hash.put(Networking.PARAM_URL,"http://www.w3.org/Graphics/GIF/spec-gif87.txt");
//		hash.put(Networking.PARAM_EXPECTED,Networking.TYPE_EXPECTED_STRING);
//		hash.put(Networking.PARAM_CALLBACK,new Callback(){
//			public void callback(Object... params){
//				if(params.length>1){
//					String response = (String)params[1];
//					Log.d(TAG,"response: "+response);
//					Log.d(TAG,"type: "+params[2]);
//				}
//			}
//		});
//		new WebTask().execute(hash);
		
		// load image
		HashMap<String,Object> hash = new HashMap<String,Object>();
		hash.put(Networking.PARAM_URL,"https://www.google.com/images/srpr/logo11w.png");
		hash.put(Networking.PARAM_EXPECTED,Networking.TYPE_EXPECTED_IMAGE);
		hash.put(Networking.PARAM_CALLBACK,new Callback(){
			public void callback(Object... params){
				Object response = params[1];
				if(response!=null){
					Bitmap bitmap = (Bitmap)response;
					Log.d(TAG,"bitmap: "+bitmap);
					ImageView image = (ImageView)findViewById(R.id.display_image);
					image.setImageBitmap(bitmap);
				}else{
					Log.d(TAG,"load error - status: "+params[2]);
				}
			}
		});
		WebTask task = new WebTask();
		task.execute(hash);
	}
	
}
