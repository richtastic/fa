package com.richtastic.code;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;

import com.richtastic.code.CacheMemory.MemoryEntry;

public class Cache {
	private static String TAG = "Cache";
	private static Cache _cache;
	public static Cache sharedInstance(){
		if(_cache==null){
			_cache = new Cache();
			_cache.setNetwork(Networking.sharedInstance());
		}
		return _cache;
	}
// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private Networking network;
	private CacheDisk diskCache;
	private CacheMemory memoryCache;
	private DelayedRequestList requestList;
	public Cache(){
		diskCache = new CacheDisk();
		memoryCache = new CacheMemory();
		requestList = new DelayedRequestList(this);
	}
	public void setNetwork(Networking nw){
		network = nw;
	}
	public Object getImmediate(String url){ // get from memory if exists 
		return memoryCache.get(url);
	}
	public Object getSource(String url, int type, Callback callback){ // get whatever currently exists immediately, but also force direct from source
		Object data = memoryCache.get(url);
		requestList.checkAddRequest(network, url, type, callback);
		return data;
	}
	public Object getImage(String url, Callback callback){
		return get(url, Networking.TYPE_EXPECTED_IMAGE, callback);
	}
	public Object get(String url, int type, Callback callback){ // get from cache, else get from source
		Object data = memoryCache.get(url);
		if(data!=null){ // memory hit
			return data;
		} // memory miss
		if( diskCache.exists(url) ){ // disk hit
			// data = diskCache.get(url);
		}else{ // disk miss
			// request from url
			requestList.checkAddRequest(network, url, type, callback);
		}
		return null;
	}
	public void delayedRequestResult(String url, int responseCode, Object result, Object responseObject){
		Log.d(TAG,"resultA: "+url);
		Log.d(TAG,"resultB: "+responseCode);
		Log.d(TAG,"resultC: "+result);
		Log.d(TAG,"resultD: "+responseObject);
		memoryCache.set(url, result);
		diskCache.set(url,result);
	}
	public String toString(){
		String str = "[Cache:\n"+memoryCache.toString()+"\n]";
		return str;
	}
	
	public static class DelayedRequestList implements Callback{
		private static String TAG = "DelayedRequestList";
		private HashMap<String,CallbackConduit> requestList;
		private Cache cache;
		public DelayedRequestList(Cache cash){
			cache = cash;
			requestList = new HashMap<String,CallbackConduit>();
		}
		public void checkAddRequest(Networking network, String url, int type, Callback callback){
			CallbackConduit request = requestList.get(url);
			if(request!=null){
				Log.d(TAG,"check add - append");
				request.addCallback(callback);
			}else{
				Log.d(TAG,"check add - create");
				request = new CallbackConduit(this);
				requestList.put(url,request);
				request.setTag(url);
				request.addCallback(callback);
				network.addRequest(url, type, request);
			}
		}
		public void removeRequest(String url){
			CallbackConduit request = requestList.remove(url);
			request.clearCallbacks();
		}
		@Override
		public void callback(Object... params){
			Log.d(TAG,"ALERTED: "+((params!=null)?params.length:"null"));
			CallbackConduit request = (CallbackConduit)params[params.length-1];
			Log.d(TAG,"conduit: "+request);
			String url = (String)request.getTag();
			Log.d(TAG,"url: "+url);
			Log.d(TAG,"DelayedRequestList calling back cache: "+cache);
			// put in cache
			cache.delayedRequestResult(url,(Integer)params[2],params[1],params[3]); // EVENT_WEB_CALL_COMPLETE, result, responseCode, responseObject
			// delete entry from hashmap
			requestList.remove(url);
			// alert concerned
			request.alertCallbacks(params);
			request.clearCallbacks();
		}
	}
// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public static class CallbackConduit implements Callback{
		private static String TAG = "cacheDelayedRequest";
		private WeakReference<Callback> callback;
		private ArrayList<WeakReference<Callback>> callbackList;
		private Object tag;
		public CallbackConduit(Callback cb){
			callback = new WeakReference<Callback>(cb);
			callbackList = new ArrayList<WeakReference<Callback>>();
		}
		public Object getTag(){
			return tag;
		}
		public Object setTag(Object t){
			Object was = tag;
			tag = t;
			return was;
		}
		public void addCallback(Callback cb){
			callbackList.add(new WeakReference<Callback>(cb));
		}
		public void alertCallbacks(Object... params){
			for(WeakReference<Callback> cb : callbackList){
				Callback call = cb.get();
				Log.d(TAG,"ALERTING: "+cb+" "+call);
				call.callback(params);
			}
		}
		public void clearCallbacks(){
			for(WeakReference<Callback> cb : callbackList){
				cb.clear();
			}
			callbackList.clear();
		}
		@Override
		public void callback(Object... params) {
			Log.d(TAG,"conduit callback: "+params);
			Callback cb = callback.get();
			if(cb!=null){ // append this to parameters
				int i, len = params.length;
				Object[] p2 = new Object[len+1];
				for(i=0;i<len;++i){
					p2[i] = params[i];
				}
				p2[len] = this;
				cb.callback(p2);
			}
			
		}
	}
	//
}
/*
Cache
	RequestList: (HARD REFERENCE to each in list of requests)
		Request: (conglomerate for concerned callbacks and cache)
			- CALLBACK
			- cache (put resource once loaded)
			- callback list (call back once loaded)
			Networking
				-> onComplete call CALLBACK  
	// 
	on request:
	cache -> requestlist -> conduit -> request -> network
	...
	on complete:
	network -> request -> conduit -> requestlist -> cache + concerned
*/