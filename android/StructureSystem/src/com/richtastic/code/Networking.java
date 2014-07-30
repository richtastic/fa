package com.richtastic.code;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import com.richtastic.structuresystem.R;
import com.richtastic.structuresystem.StructureSystemApplication;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
public class Networking {
	private static String TAG = "Networking";
	public static int REQUEST_PRIORITY_LOW = 4096;
	public static int REQUEST_PRIORITY_MEDIUM = 2048;
	public static int REQUEST_PRIORITY_HIGH = 0;
	//
	public static String EVENT_WEB_CALL_COMPLETE = "EVENT_WEB_CALL_COMPLETE";
	//
	public static int TYPE_EXPECTED_UNKNOWN = 0;
	public static int TYPE_EXPECTED_STRING = 10;
	public static int TYPE_EXPECTED_JSON = 20;
	public static int TYPE_EXPECTED_IMAGE = 30;
	//
	public static String PARAM_URL = "PARAM_URL"; // String
	public static String PARAM_METHOD = "PARAM_METHOD"; // String 
	public static String PARAM_CACHE = "PARAM_CACHE"; // Boolean
	public static String PARAM_EXPECTED = "PARAM_EXPECTED"; // Integer
	public static String PARAM_PROPERTIES = "PARAM_PROPERTIES"; // HashMap<String,String>
	public static String PARAM_CALLBACK = "PARAM_CALLBACK"; // Callback
	//
	private static Networking instance;
	// class methods
	public static Networking sharedInstance(){
		if(instance==null){
			instance = new Networking();
		}
		return instance;
	}
// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private WebThrottle throttle;
	public Networking(){
		throttle = new WebThrottle();
	}
	public void addRequest(String url, int type, Callback callback){
		WebRequest request = new WebRequest(url, type);
		request.addCallback(callback);
		throttle.addRequest(request);
	}
	public void addRequest(WebRequest request){ // filled out by caller
		throttle.addRequest(request);
	}
// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public static class WebRequest implements Callback{
		public String url;
		private int priority;
		private WebThrottle throttle;
		private ArrayList<WeakReference<Callback>> concerned;
		private WebTask task;
		private HashMap<String,Object> hash;
		private HashMap<String,String> properties;
		public WebRequest(String url, int expected, int priority){
			this.url = url;
			this.priority = priority;
			concerned = new ArrayList<WeakReference<Callback>>();
			throttle = null;
			task = null;
			properties = new HashMap<String,String>();
			hash = new HashMap<String,Object>();
			hash.put(Networking.PARAM_CALLBACK,this);
			hash.put(Networking.PARAM_URL,url);
			hash.put(Networking.PARAM_EXPECTED,expected);
			hash.put(Networking.PARAM_PROPERTIES, properties);
		}
		public WebRequest(String url, int expected){
			this(url, expected, REQUEST_PRIORITY_MEDIUM);
		}
		public void setProperty(String key, String value){
			properties.put(key, value);
		}
		public void setPriority(int newPriority){
			priority = newPriority;
		}
		public int getPriority(){
			return priority;
		}
		public void setThrottle(WebThrottle newThrottle){
			throttle = newThrottle;
		}
		public void addCallback(Callback callback){
			concerned.add(new WeakReference<Callback>(callback));
		}
		private void alertConcerned(Object... params){
			Callback callback;
			for(WeakReference<Callback> cb : concerned){
				callback = cb.get();
				Log.d(TAG,"alerting: "+cb+" = "+callback);
				if(callback!=null){
					callback.callback(params);
				}
			}
			if(throttle != null){
				throttle.requestDidComplete(this);
			}
		}
		private void clear(){
			if(properties!=null){
				properties.clear();
				properties = null;
			}
			if(hash!=null){
				hash.clear();
				hash = null;
			}
			if(task!=null){
				task.cancel(true);
				task = null;
			}
			if(concerned!=null){
				concerned.clear();
				concerned = null;
			}
			url = null;
			priority = 0;
		}
		public void start(){
			if(task==null){
				task = new WebTask();
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, hash); // task.execute(hash);
			}
		}
		@Override
		public void callback(Object... params){
			alertConcerned(params);
			clear();
		}
	}
// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public static class WebThrottle{
		protected int maxRequests = 3;
		protected ArrayList<WebRequest> currentRequests;
		protected PriorityQueue<WebRequest> requestQueue;
		public WebThrottle(){
			WebRequestComparator compare = new WebRequestComparator();
			requestQueue = new PriorityQueue<WebRequest>(maxRequests,compare);
			currentRequests = new ArrayList<WebRequest>();
		}
		public void requestDidComplete(WebRequest request){
			Log.d(TAG,"requestDidComplete ......................................");
			currentRequests.remove(request);
			request.clear();
			checkNextRequest();
		}
		public void addRequest(WebRequest request){
			request.setThrottle(this); 
			requestQueue.add(request);
			checkNextRequest();
		}
		private void checkNextRequest(){
			Log.d(TAG,"check next: "+currentRequests.size());
			if( currentRequests.size()<maxRequests ){
				Log.d(TAG,"queue size: "+requestQueue.size());
				if(requestQueue.size()>0){
					WebRequest request = requestQueue.remove();
					currentRequests.add(request);
					request.start();
				}
			}
			
		}
	}
// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public static class WebRequestComparator implements Comparator<WebRequest>{
		@Override
		public int compare(WebRequest a, WebRequest b){
			return a.getPriority()-b.getPriority();
		}
	}
// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public static String readInputAsString(InputStream inStream){
		int bufferLen = 256;
		int read = 1;
		String returnString = "";
		try{
			Reader reader = new InputStreamReader(inStream, "UTF-8");
			char [] buffer = new char[bufferLen];
			while(read>0){
				read = reader.read(buffer);
				if(read==bufferLen){
					returnString += new String(buffer);
				}else if(read>0){
					returnString = returnString + (new String(buffer)).substring(0,read);
				}
			}
		}catch(Exception e){
			//
		}
		return returnString;
	}
	public static Object connectionResultToKnownType(URLConnection connection, int expectedType){
		InputStream inStream;
		try{
			inStream = connection.getInputStream();
			//String contentType = connection.getContentType();
			//Object response = connection.getContent();
			if(expectedType==TYPE_EXPECTED_IMAGE){
				Log.d(TAG,"isBitmap");
				return instreamToBitmap(inStream);
			}else if(expectedType==TYPE_EXPECTED_STRING){
				Log.d(TAG,"isString");
				return instreamToString(inStream);
			}else if(expectedType==TYPE_EXPECTED_JSON){
				Log.d(TAG,"isJSON");
				return instreamToJSON(inStream);
			}else{ // try to use MIMETypes from guava library
				return null;
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}
	public static String instreamToString(InputStream inStream){
		String str = readInputAsString(inStream);
		return str;
	}
	public static Bitmap instreamToBitmap(InputStream inStream){
		Bitmap bitmap = BitmapFactory.decodeStream(inStream);
		return bitmap;
	}
	public static Object instreamToJSON(InputStream inStream){
		Gson gson = new Gson();
		try{
			JsonReader reader = new JsonReader(new InputStreamReader(inStream,"UTF-8"));
			JsonObject json = gson.fromJson(reader, JsonObject.class);
			return json;
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
		return null;
	}
// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public static class WebTask extends AsyncTask<Object,Object,Object>{ // <do-in-background-params, progress-params, post-execute-result>
		private WeakReference<Callback> callback;
		private HttpURLConnection connection = null;
		private Object responseObject = null;
		private int responseCode = -1;
		private void clear(){
			if(connection != null){
				connection.disconnect();
				connection = null;
			}
			if(callback !=null){
				callback.clear();
				callback = null;
			}
			responseObject = null;
			responseCode = -1;
		}
		@Override
		protected void onCancelled(){
			super.onCancelled();
			clear();
		}
		@SuppressWarnings("unchecked") // HashMap<...,...>
		@Override
		protected Object doInBackground(Object... params){ // url, useCache, headers (auth,content-type), method, callback
			Log.d(TAG,"doInBackground");
			// default params
			int connectTimeout = 10000; // 10 seconds
			int readTimeout = 15000; // 15 seconds
			HashMap<String,Object> inParams;
			String requestMethod = "GET";
			int requestExpectedType = TYPE_EXPECTED_JSON;
			String requestURL = "http://www.reddit.com/.json";
			HashMap<String,String> requestProperties = null;//new HashMap<String,String>();
			Boolean requestCaching = true;
			Callback requestCallback = null;
			// get input params
			if(params.length==1){
				Object paramZero = params[0];
				if(paramZero instanceof Callback){
					requestCallback = (Callback)paramZero;
				}else if(paramZero instanceof HashMap){
					inParams = (HashMap<String,Object>)paramZero;
					if( inParams.get(PARAM_CALLBACK) != null ){
						requestCallback = (Callback)inParams.get(PARAM_CALLBACK);
					}
					if( inParams.get(PARAM_URL) != null ){
						requestURL = (String)inParams.get(PARAM_URL);
					}
					if( inParams.get(PARAM_METHOD) != null ){
						requestMethod = (String)inParams.get(PARAM_METHOD);
					}
					if( inParams.get(PARAM_CACHE) != null ){
						requestCaching = (Boolean)inParams.get(PARAM_CACHE);
					}
					if( inParams.get(PARAM_PROPERTIES) != null ){
						HashMap<String,String> props = (HashMap<String,String>)inParams.get(PARAM_PROPERTIES);
						requestProperties = props;
					}
					if( inParams.get(PARAM_EXPECTED) != null ){
						requestExpectedType = (Integer)inParams.get(PARAM_EXPECTED);
					}
				}
			}
			// set callback
			callback = new WeakReference<Callback>(requestCallback);
			// set url
			URL url;
			try{ url = new URL(requestURL);
			}catch(MalformedURLException e){ e.printStackTrace(); return null; }
			// set connection
			try{ connection = (HttpURLConnection)url.openConnection();
			}catch(IOException e){ e.printStackTrace(); return null; }
			// set cache
			connection.setUseCaches(requestCaching);
			// set properties
			if(requestProperties!=null){
				for(Entry<String,String> entry : requestProperties.entrySet()){
					connection.setRequestProperty(entry.getKey(),entry.getValue());
				}
			}
			// set timeouts
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(readTimeout);
			// set method
			try { connection.setRequestMethod(requestMethod);
			}catch(ProtocolException e){ e.printStackTrace(); }
			// get response code
			try{ responseCode = connection.getResponseCode();
			}catch (IOException e){ e.printStackTrace();}
			// get response
			try{ responseObject = connection.getContent();
			}catch(IOException e){ e.printStackTrace(); return null; }
			// return data
			if(this.isCancelled()){ return null; }
			Object object = Networking.connectionResultToKnownType(connection, requestExpectedType);
			try{ // check that stream is closed before returning
				InputStream inStream = connection.getInputStream();
				inStream.close();
			}catch(IOException e){
				e.printStackTrace();
			}
			return object;
		}
		@Override
		protected void onPreExecute(){
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
			Log.d(TAG,"threads: "+Thread.activeCount());
			super.onPreExecute();
			Log.d(TAG,"onPreExecute");
		}
		@Override
		protected void onProgressUpdate(Object... progress){
			Log.d(TAG,"onProgressUpdate");
		}
		@Override
		protected void onPostExecute(Object result){
			Log.d(TAG,"onPostExecute "+result+" | "+responseObject+" | "+responseCode);
			if(callback!=null){
				Callback cb = (Callback)callback.get();
				if(cb!=null){
					cb.callback(EVENT_WEB_CALL_COMPLETE, result, responseCode, responseObject); // HttpURLConnection.HTTP_OK
				}
			}
			clear();
		}
	}
}
