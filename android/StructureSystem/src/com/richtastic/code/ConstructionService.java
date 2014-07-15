package com.richtastic.code;

import android.util.Log;

public class ConstructionService {
	private static String TAG = "ConstructionService";
	private static ConstructionService instance;
	public static String DEFAULT_PROTOCOL = "http://";
	public static String DEFAULT_HOST = "192.168.1.4";
	public static String DEFAULT_path = "android_server";
	public static ConstructionService sharedInstance(){
		return instance;
	}
	// ---------------------------------------------------------------------------------------
	private String domain;
	private String protocol;
	private String path;
	public ConstructionService(String proto, String server){
		protocol = proto;
		domain = server;
	}
	private String getServerURL(){
		return protocol+domain+"/"+path+"/";
	}
	public void getSharedEntryListingWithOffset(int offset){
		String url = getServerURL()+"data.json";
		Log.d(TAG,url);
	}
}
