package com.richtastic.structuresystem;
import com.richtastic.code.Networking;
import com.richtastic.code.Networking.WebTask;

import android.support.v7.app.ActionBarActivity;
//import android.app.*;
//import android.support.v4.app.Fragment;
import android.app.Fragment;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.util.*;
import android.view.*;

public class StartupActivity extends ActionBarActivity {
	private static String TAG = "StartupActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "StartupActivity");
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
		
		// load image
		WebTask task = new WebTask();
		task.execute();
	}
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
}
