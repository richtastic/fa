package com.richtastic.code;

import java.io.File;
import java.util.HashMap;


public class CacheDisk {
	public static String removeBadCharsFromString(String str){
		return str.replaceAll("(:|\\/|\\+|\\*|\\?|\\\"|<|>| )","_");
	}
	private static String HDD_PREFIX = "cache_";
	private static String HDD_SUFFIX = ".cache.tmp";
// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	protected HashMap<String,DiskRequest> diskRequestHash;
	protected String diskDirectoryPath;
	public CacheDisk(){
		diskRequestHash = new HashMap<String,DiskRequest>();
		diskDirectoryPath = "/";
	}
	private String getDiskDirectoryPath(){
		return diskDirectoryPath;
	}
	protected String getDiskFileNameFromURL(String url){
		return HDD_PREFIX+removeBadCharsFromString(url)+HDD_SUFFIX;
	}
	private String getFullAbsoluteFileName(String url){
		return getDiskDirectoryPath() + getDiskFileNameFromURL(url);
	}
	public boolean exists(String url){
		return (new File( getFullAbsoluteFileName(url) )).exists();
	}
	public Object get(String url, int type){
		// file exists on hdd ?
			// type for decompressing: Bitmap, JSON, String
		return null;
	}
	public Object set(String url, Object obj){
		// write to hdd ?
			// type for compressing: Bitmap, JSON, String ?
		return null;
	}
// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public static class DiskEntry{
		// 
	}
// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public static class DiskRequest{
		//
	}
}
