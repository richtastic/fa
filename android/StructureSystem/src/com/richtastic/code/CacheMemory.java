package com.richtastic.code;

import java.lang.ref.SoftReference;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
//import java.util.PriorityQueue;
//import java.util.Queue;
import java.text.SimpleDateFormat;

import com.google.common.collect.MinMaxPriorityQueue;

import com.richtastic.code.Networking.WebRequest;
import com.richtastic.code.Networking.WebRequestComparator;


import android.graphics.Bitmap;
import android.util.Log;

public class CacheMemory {
	private static String TAG = "CacheMemory";
	public static String EVENT_ = "EVENT_";
	public static final String DEFAULT_DIRECTORY_PATH = "/";
	public static final long DEFAULT_SIZE_BYTES = 15*1024*1024; // 10 MB
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
	//protected PriorityQueue<MemoryEntry> lruQueue;
	protected MinMaxPriorityQueue<MemoryEntry> lruQueue;
	public CacheMemory(){
		memoryHash = new HashMap<String,MemoryEntry>();
		bytesTotal = DEFAULT_SIZE_BYTES;
		MemoryEntryLRUComparator compare = new MemoryEntryLRUComparator();
		//lruQueue = new PriorityQueue<MemoryEntry>(1,compare);
		lruQueue = MinMaxPriorityQueue.orderedBy(compare).create();
	}
	public Object get(String url){
		MemoryEntry entry = memoryHash.get(url);
		if(entry!=null){
			SoftReference<Object>reference = entry.data;
			Object object = reference.get();
			if(object!=null){ // hit
				lruQueue.remove(entry);
				entry.updateAccessed();
				lruQueue.add(entry);
				return object;
			}else{ // miss - explicit delete
				synchronized(this){
					entry = memoryHash.remove(url);
					lruQueue.remove(entry);
					bytesUsed -= entry.size;
					entry.clear();
				}
			}
		}
		return null;
	}
	synchronized public Object set(String url, Object obj){
		long sizeInBytes = CacheMemory.getSizeOf(obj);
		MemoryEntry entry;
		// remove entry if currently in queue/map
		clearEntry(url);
		// check if will fit into memory
		long willBytesUsed = bytesUsed+sizeInBytes;
		Log.d(TAG,"MEMORY USAGE: "+bytesUsed+" / "+bytesTotal+" => "+willBytesUsed);
		if(sizeInBytes<=bytesTotal && willBytesUsed>bytesTotal){ // don't add if too big, make room if necessary 
			Log.d(TAG,"need to remove items...");
			while(lruQueue.size()>0 && willBytesUsed>bytesTotal){
				entry = lruQueue.remove();
				willBytesUsed -= entry.size;
				bytesUsed -= entry.size;
				Log.d(TAG,"made space from: "+entry);
			}
		}
		if(willBytesUsed<=bytesTotal){
			bytesUsed += sizeInBytes; // willBytesUsed
			entry = new MemoryEntry(obj, url, sizeInBytes);
			lruQueue.add(entry);
			return entry;
		}
		return null;
	}
	synchronized public void clearOldEntries(Date date){ // remove all entries before date
		clearNullEntries();
		MemoryEntry entry = lruQueue.peek();
		while(entry!=null && entry.timestamp.compareTo(date)<0){
			clearEntry(entry.url);
			entry = lruQueue.peek(); // check next
		}
	}
	synchronized public void clearNewEntries(Date date){ // remove all entries after date
		clearNullEntries();
		MemoryEntry entry = lruQueue.peekLast();
		while(entry!=null && entry.timestamp.compareTo(date)>0){
			clearEntry(entry.url);
			entry = lruQueue.peekLast(); // check next
		}
		/*
		MemoryEntry[] entries = (MemoryEntry[])lruQueue.toArray(); // MinMaxQueue
		MemoryEntry entry;
		if(entries.length>0){
			int i = entries.length-1;
			entry = entries[i];
			while(i>=0 && entry!=null && entry.timestamp.compareTo(date)<0){
				lruQueue.remove(entry); // pop off back
				memoryHash.remove(entry.url);
				entry.clear();
				--i;
				if(i>=0){
					entry = entries[i];					
				}
			}
		}
		*/
	}
	synchronized public void clearNullEntries(){ // remove any hash entry with null-softreferences
		for(Entry<String,MemoryEntry> hash : memoryHash.entrySet()){
			MemoryEntry entry = hash.getValue();
			if(entry.data.get()==null){
				clearEntry( hash.getKey() );
			}
		}
	}
	synchronized public void clearEntry(String url){
		MemoryEntry entry = memoryHash.get(url);
		if(entry!=null){
			lruQueue.remove(entry);
			memoryHash.remove(url);
			Log.d(TAG,"removed current entry: "+url);
			entry.clear();
			bytesUsed -= entry.size;
		}
	}
	synchronized public void clearAllEntries(){ // remove all
		for(Entry<String,MemoryEntry> hash : memoryHash.entrySet()){
			clearEntry( hash.getKey() );
		}
		memoryHash.clear(); // necessary?
		lruQueue.clear();
		bytesUsed = 0;
	}
	public void lowMemoryAlert(){
		clearAllEntries();
	}
	public String toString(){
		Object[] queued = lruQueue.toArray();
		int i, len = queued.length;
		String str = "[CacheMemory: ("+lruQueue.size()+")\n";
		for(i=0; i<len; ++i){
			MemoryEntry entry = (MemoryEntry)queued[i];
			str += i+": "+entry+"\n";
		}
		str += "]";
		return str;
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
		public String url;
		public MemoryEntry(Object d, String u, long bytes){
			timestamp = new Date();
			data = new SoftReference<Object>(d);
			url = u;
			size = bytes;
			String disp = new SimpleDateFormat("yyyy-MM-dd'T' HH:mm:ss").format(timestamp);
			Log.d(TAG,"new memory entry: "+this.toString());
		}
		public void updateAccessed(){ // update LRU priority
			timestamp = new Date();
		}
		public void clear(){
			timestamp = null;
			size = 0;
			if(data!=null){
				data.clear();
				data = null;
			}
		}
		public String toString(){
			String disp = new SimpleDateFormat("yyyy-MM-dd'T' HH:mm:ss").format(timestamp);
			return "[MemoryEntry: "+url+" | "+size+" | "+disp+"]";
		}
	}
	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public static class MemoryEntryLRUComparator implements Comparator<MemoryEntry>{
		@Override
		public int compare(MemoryEntry a, MemoryEntry b){
			int timeDiff = b.timestamp.compareTo(a.timestamp);
			if(a==b){
				return 0;
			}else if(timeDiff<0){
				return -1;
			}
			return 1;
		}
	}
	// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
