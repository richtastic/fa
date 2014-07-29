package com.richtastic.structuresystem;
import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

public class StructureSystemApplication extends Application{
	private static String TAG = "StructureSystemApplication";
	public static Application context;
	public static Application getContext(){
		return context;
	}
	@Override
	public void onCreate(){
		super.onCreate();
		Log.d(TAG,"app created");
//		context = this;
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		Log.d(TAG,"config changed");
	}
	@Override
	public void onLowMemory(){
		super.onLowMemory();
		Log.d(TAG,"Low Memory warning");
	}
}
