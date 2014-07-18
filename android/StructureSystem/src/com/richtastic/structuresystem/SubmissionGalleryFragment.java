package com.richtastic.structuresystem;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.annotation.SuppressLint;
import android.app.Activity;
//import android.support.v4.app.*;
import android.app.Fragment;


public class SubmissionGalleryFragment extends Fragment {
	private static String TAG = "SubmissionGalleryFragment";
	@Override
	public void onAttach(Activity activity){
		Log.d(TAG, "SubmissionGalleryFragment - onAttach");
		super.onAttach(activity);
	}
	@Override
	public void onCreate(Bundle savedInstanceState){
		Log.d(TAG, "SubmissionGalleryFragment - onCreate ("+savedInstanceState+")");
		super.onCreate(savedInstanceState);
		if(savedInstanceState!=null){
			Log.d(TAG, "found: "+savedInstanceState.getInt("STATE_DATA"));
		}
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		Log.d(TAG, "SubmissionGalleryFragment - onCreateView");
		return inflater.inflate(R.layout.fragment_submission_gallery, container, false);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		Log.d(TAG, "SubmissionGalleryFragment - onActivityCreated ("+savedInstanceState+")");
		super.onActivityCreated(savedInstanceState);
	}
	@Override
	public void onStart(){
		Log.d(TAG, "SubmissionGalleryFragment - onStart");
		super.onStart();
	}
	@Override
	public void onResume(){
		Log.d(TAG, "SubmissionGalleryFragment - onResume");
		super.onResume();
	}
	//
	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putInt("STATE_DATA",22);
		Log.d(TAG, "SubmissionGalleryFragment - onSaveInstanceState ("+outState+")");
	}
	//
	@Override
	public void onPause(){
		Log.d(TAG, "SubmissionGalleryFragment - onPause");
		super.onStop();
	}
	@Override
	public void onStop(){
		Log.d(TAG, "SubmissionGalleryFragment - onStop");
		super.onStop();
	}
	@Override
	public void onDestroyView(){
		Log.d(TAG, "SubmissionGalleryFragment - onDestroyView");
		super.onDestroyView();
	}
	@Override
	public void onDestroy(){
		Log.d(TAG, "SubmissionGalleryFragment - onDestroy");
		super.onDestroy();
	}
	@Override
	public void onDetach(){
		Log.d(TAG, "SubmissionGalleryFragment - onDetach");
		super.onDetach();
	}
	
	@Override
	public void onTrimMemory(int level){
		//if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH){
			super.onTrimMemory(level);
			Log.d(TAG, "memory level: "+level);
		//}
	}
}
