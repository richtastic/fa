package com.richtastic.structuresystem;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.annotation.SuppressLint;
import android.app.Fragment;


public class HomeMenuFragment extends Fragment {
	private static String TAG = "HomeMenuFragment";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		Log.d(TAG, "HomeMenuFragment");
		return inflater.inflate(R.layout.fragment_home_menu, container, false);
	}
	@Override
	public void onTrimMemory(int level){
		//if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH){
			super.onTrimMemory(level);
			Log.d(TAG, "memory level: "+level);
		//}
	}
}
