package com.richtastic.code;

import com.richtastic.code.CacheMemory.MemoryEntry;

public class Cache {
	//
// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private CacheDisk diskCache;
	private CacheMemory memoryCache;
	public Cache(){
		diskCache = new CacheDisk();
		memoryCache = new CacheMemory();
	}
	public Object getImmediate(String url){ // get from memory if exists 
		return memoryCache.get(url);
	}
	public Object getSource(String url, int type, Callback callback){ // get whatever currently exists immediately, but also force direct from source
		Object data = memoryCache.get(url);
		CacheDelayedRequest request = new CacheDelayedRequest();
		// request from url
		return data;
	}
	public Object get(String url, int type, Callback callback){ // get from cache, else get from source
		Object data = memoryCache.get(url);
		if(data!=null){ // memory hit
			return data;
		} // memory miss
		CacheDelayedRequest request = new CacheDelayedRequest();
		if( diskCache.exists(url) ){ // disk hit
			//data = diskCache.get(url);
		}// disk miss
		// request from url
		return null;
	}
// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public static class CacheDelayedRequest{ // either on disk, or on web, or not
		// disk hit
		// disk miss(error)
		// web hit
		// web miss
	}
	//
}
