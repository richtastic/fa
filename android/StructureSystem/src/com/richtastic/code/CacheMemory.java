package com.richtastic.code;

import java.lang.ref.SoftReference;
import java.util.Date;
import java.util.HashMap;
import java.text.SimpleDateFormat;

import android.util.Log;

public class CacheMemory {
	private static String TAG = "CacheMemory";
	public static String EVENT_ = "EVENT_";
// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	protected HashMap<String,MemoryEntry> memoryHash;
	public CacheMemory(){
		memoryHash = new HashMap<String,MemoryEntry>();
		
	}
	public Object get(String url){
		MemoryEntry entry = memoryHash.get(url);
		if(entry!=null){
			return entry.data;
		}
		return null;
	}
	public Object set(String url, Object obj){
		// 
		// need to check if too much memory will be used, then add
		MemoryEntry entry = new MemoryEntry(obj);
		memoryHash.put(url,entry);
		return entry;
	}
	public void clearOldEntries(Date date){
		// remove all entries before date
	}
	public void clearNewEntries(Date date){
		// remove all entries after date
	}
	public void clearNullEntries(){
		// go thru hash and remove any with softreferences to null
	}
	public void clearAllEntries(){
		// remove all
	}
	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public static class MemoryEntry{
		public Date timestamp;
		public SoftReference<Object> data;
		public MemoryEntry(Object d){
			timestamp = new Date();
			data = new SoftReference<Object>(d);
			String disp = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(timestamp);
			Log.d(TAG,"timestamp: "+disp);
		}
	}
	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
