package com.richtastic.structuresystem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

public class SubmissionGalleryActivity extends ActionBarActivity {
	private static String TAG = "SubmissionGalleryActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.d(TAG, "SubmissionGalleryActivity - onCreate ("+savedInstanceState+")");
		Intent intent = getIntent();
		Log.d(TAG,"intent: "+intent);
		setContentView(R.layout.activity_gallery_submission);
		
		// add main fragment
		if(savedInstanceState==null){
			View fragmentGalleryContainer = findViewById(R.id.fragment_container_gallery); 
			if(fragmentGalleryContainer != null){
				Log.d(TAG,"has gallery fragment container");
				SubmissionGalleryFragment galleryFragment = new SubmissionGalleryFragment();
				galleryFragment.setArguments(new Bundle());
				getFragmentManager().beginTransaction().add(R.id.fragment_container_gallery, galleryFragment).commit();
			}else{
				Log.d(TAG,"no fragment gallery container");
			}
		}
		
	}
	@Override
	public void finish(){
		Intent intent = new Intent();
		intent.putExtra("KEY_RESULT_HERE", "done");
		this.setResult(Activity.RESULT_OK, intent);
		super.finish();
	}
}
