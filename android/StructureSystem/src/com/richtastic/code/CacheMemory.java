package com.richtastic.code;

import java.lang.ref.SoftReference;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.text.SimpleDateFormat;


import android.graphics.Bitmap;
import android.util.Log;

public class CacheMemory {
	private static String TAG = "CacheMemory";
	public static String EVENT_ = "EVENT_";
	public static final String DEFAULT_DIRECTORY_PATH = "/";
	public static final long DEFAULT_SIZE_BYTES = 10*1024*1024; // 10 MB
	public static final long DEFAULT_STALE_SECONDS = 1*60*60; // 1 hour
// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private static CacheMemory _cache;
	public static CacheMemory sharedCache(){
		if(_cache==null){
			_cache = new CacheMemory();
		}
		return _cache;
	}
// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private long bytesUsed;
	private long bytesTotal;
	protected HashMap<String,MemoryEntry> memoryHash;
	public CacheMemory(){
		memoryHash = new HashMap<String,MemoryEntry>();
		bytesTotal = DEFAULT_SIZE_BYTES;
	}
	public Object get(String url){
		MemoryEntry entry = memoryHash.get(url);
		if(entry!=null){
			SoftReference<Object>reference = entry.data;
			Object object = reference.get();
			if(object!=null){ // hit
				return object;
			}else{ // miss - explicit delete
				memoryHash.remove(url);
			}
		}
		return null;
	}
	public Object set(String url, Object obj){
		long sizeInBytes = CacheMemory.getSizeOf(obj);
		// check if will fit into memory
		// need to check if too much memory will be used, then add
		MemoryEntry entry = new MemoryEntry(obj, sizeInBytes);
		memoryHash.put(url,entry);
		return entry;
	}
	public void clearOldEntries(Date date){
		// remove all entries before date
		clearNullEntries();
	}
	public void clearNewEntries(Date date){
		// remove all entries after date
		clearNullEntries();
	}
	public void clearNullEntries(){ // remove any hash entry with null-softreferences
		for(Entry<String,MemoryEntry> hash : memoryHash.entrySet()){
			MemoryEntry entry = hash.getValue();
			if(entry.data.get()==null){
				clearEntry( hash.getKey() );
			}
		}
	}
	public void clearEntry(String url){
		MemoryEntry entry = memoryHash.get(url);
		if(entry!=null){
			bytesUsed -= entry.size;
			entry.clear();
			memoryHash.remove(url);
		}
	}
	public void clearAllEntries(){ // remove all
		for(Entry<String,MemoryEntry> hash : memoryHash.entrySet()){
			clearEntry( hash.getKey() );
		}
		memoryHash.clear(); // necessary?
		bytesUsed = 0;
	}
	public void lowMemoryAlert(){
		clearAllEntries();
	}
	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public static long getSizeOf(Object obj){
		if(obj instanceof Bitmap){
			return ((Bitmap)obj).getByteCount();
		}else if(obj instanceof String){
			return ((((String)obj).length()*2+45)/8)*8; // 32 + 2*length rounded up to multiple of 8
		}
		return 0;
	}
	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public static class MemoryEntry{
		public Date timestamp;
		public long size;
		public SoftReference<Object> data;
		public MemoryEntry(Object d, long bytes){
			timestamp = new Date();
			size = bytes;
			data = new SoftReference<Object>(d);
			String disp = new SimpleDateFormat("yyyy-MM-dd'T' HH:mm:ss").format(timestamp);
			Log.d(TAG,"timestamp: "+disp);
		}
		public void clear(){
			timestamp = null;
			size = 0;
			if(data!=null){
				data.clear();
				data = null;
			}
		}
	}
	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
