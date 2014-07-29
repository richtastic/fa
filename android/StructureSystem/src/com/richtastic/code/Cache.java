package com.richtastic.code;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import com.richtastic.code.CacheMemory.MemoryEntry;

public class Cache {
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
		CacheDelayedRequest request = new CacheDelayedRequest(callback, this);
		// request from url
		return data;
	}
	public Object get(String url, int type, Callback callback){ // get from cache, else get from source
		Object data = memoryCache.get(url);
		if(data!=null){ // memory hit
			return data;
		} // memory miss
		
		/*
		if( diskCache.exists(url) ){ // disk hit
			//data = diskCache.get(url);
		}// disk miss
		*/
		// request from url
		return null;
	}
	
	public static class DelayedRequestList implements Callback{
		private HashMap<String,CacheDelayedRequest> requestList;
		private Cache cache;
		private ArrayList<Callback> callbackList;
		public DelayedRequestList(Cache cash){
			cache = cash;
			requestList = new HashMap<String,CacheDelayedRequest>();
			callbackList = new ArrayList<Callback>();
		}
		public void checkAddRequest(Networking network, String url, int type, Callback callback){
			CacheDelayedRequest request = requestList.get(url);
			if(request!=null){ // if request exists, add this
				request.addCallback(callback);
			}else{ // else create one
				callbackList.add(callback);
				request = new CacheDelayedRequest(this, cache);
				network.addRequest(url, type, request);
			}
		}
		private void checkRemoveRequest(){
			
			
		}
		@Override
		public void callback(Object... params) {
			// put in cache
			// delete entry from hashmap
			// alert concerned
		}
	}
// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public static class CacheDelayedRequest implements Callback{
		private WeakReference<Callback> callback;
		private WeakReference<Cache> cache;
		public CacheDelayedRequest(Callback cb, Cache cash){
			callback = new WeakReference<Callback>(cb);
			cache = new WeakReference<Cache>(cash);
		}
		@Override
		public void callback(Object... params) {
			
			
		} // either on disk, or on web, or not
		// disk hit
		// disk miss(error)
		// web hit
		// web miss
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
	
*/