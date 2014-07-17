package com.richtastic.structuresystem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

public class SubmissionGalleryActivity extends ActionBarActivity {
	private static String TAG = "SubmissionGalleryActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.d(TAG, "SubmissionGalleryActivity - onCreate ("+savedInstanceState+")");
		Intent intent = getIntent();
		Log.d(TAG,"intent: "+intent);
		setContentView(R.layout.activity_gallery_submission);
	}
	@Override
	public void finish(){
		Intent intent = new Intent();
		intent.putExtra("KEY_RESULT_HERE", "done");
		this.setResult(Activity.RESULT_OK, intent);
		super.finish();
	}
}
