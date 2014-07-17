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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import com.richtastic.structuresystem.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
	public static Networking getSharedInstance(){
		if(instance==null){
			instance = new Networking();
		}
		return instance;
	}
	// instance methods
	private WebThrottle throttle;
	public Networking(){
		throttle = new WebThrottle();
	}
	public void addRequest(String url){
		WebRequest request = new WebRequest(url);
		throttle.addRequest(request);
	}
// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public static class WebRequest implements Callback{
		public String url;
		public int priority;
		private ArrayList<WeakReference<Callback>> concerned;
		public WebRequest(String u, int p){
			url = u;
			priority = p;
			concerned = new ArrayList<WeakReference<Callback>>();
		}
		public WebRequest(String u){
			this(u, REQUEST_PRIORITY_MEDIUM);
		}
		public void addCallback(Callback callback){
			concerned.add(new WeakReference<Callback>(callback));
		}
		private void alertConcerned(Object... params){
			Callback callback;
			for(WeakReference<Callback> cb : concerned){
				callback = cb.get();
				if(callback!=null){
					callback.callback(params);
				}
			}
		}
		private void clear(){
			if(concerned!=null){
				concerned.clear();
				concerned = null;
			}
			url = null;
			priority = 0;
		}
		@Override
		public void callback(Object... params) {
			alertConcerned(params);
			clear();
		}
	}
// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public static class WebThrottle{
		protected int maxRequests = 3;
		protected ArrayList<WebRequest> currentRequests;
		protected PriorityQueue<WebRequest> requestQueue;
		protected WebRequest currentRequest;
//		protected Callback requestCallback;
		public WebThrottle(){
			requestQueue = new PriorityQueue<WebRequest>();
			currentRequest = null;
//			requestCallback = new Callback(){
//				@Override
//				public void callback(Object... params) {
//					// TODO Auto-generated method stub
//					
//				}
//			}
		}
		public void completeRequest(WebRequest request){
			currentRequests.remove(request);
			checkNextRequest();
		}
		public void addRequest(WebRequest request){
			requestQueue.add(request);
			checkNextRequest();
		}
		private void checkNextRequest(){
			if( currentRequests.size()>0 && currentRequests.size()<maxRequests ){
				WebRequest request = requestQueue.remove();
				if(currentRequest==null){
					// check
				}
			}
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
		try{
			InputStream inStream = connection.getInputStream();
			//String contentType = connection.getContentType();
			//Object response = connection.getContent();
			if(expectedType==TYPE_EXPECTED_IMAGE){
				Log.d(TAG,"isBitmap");
				return instreamToBitmap(inStream);
			}else if(expectedType==TYPE_EXPECTED_STRING){
				return instreamToString(inStream);
			}else if(expectedType==TYPE_EXPECTED_JSON){
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
		return readInputAsString(inStream);
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
			// 
			Log.d(TAG,"set callback: "+callback);
			Log.d(TAG,"set url: "+requestURL);
			Log.d(TAG,"set method: "+requestMethod);
			Log.d(TAG,"set cache: "+requestCaching);
			Log.d(TAG,"set props: "+requestProperties);
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
			return Networking.connectionResultToKnownType(connection, requestExpectedType);
		}
		@Override
		protected void onPreExecute(){
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
