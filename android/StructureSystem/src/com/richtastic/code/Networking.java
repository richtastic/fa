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
import java.util.Set;
import com.google.gson.*;
import com.google.gson.JsonObject;
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
	public static int TYPE_EXPECTED_UNKNOWN = 0;
	public static int TYPE_EXPECTED_STRING = 10;
	public static int TYPE_EXPECTED_JSON = 20;
	public static int TYPE_EXPECTED_IMAGE = 30;
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
	//
	public class WebRequest{
		public String url;
		public int priority;
		public WebRequest(String u, int p){
			url = u;
			priority = p;
		}
		public WebRequest(String u){
			this(u, REQUEST_PRIORITY_MEDIUM);
		}
	}
	public class WebThrottle{
		protected ArrayList<WebRequest> requestList;
		protected WebRequest currentRequest;
		public WebThrottle(){
			requestList = new ArrayList<WebRequest>();
			currentRequest = null;
		}
		public void addRequest(WebRequest request){
			requestList.add(request);
			checkNextRequest();
		}
		private void checkNextRequest(){
			if(currentRequest==null){
				// check
			}
		}
	}
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
			//Log.d(TAG,"fail read");
		}finally{
			//Log.d(TAG,"finally read");
		}
		return returnString;
	}
	// http://developer.android.com/reference/android/os/AsyncTask.html
	public static Object connectionResultToKnownType(URLConnection connection, int expectedType){
		Object response;
		InputStream inStream;
		try{
			inStream = connection.getInputStream();
			String contentType = connection.getContentType();
			Log.d(TAG,"type: "+contentType);
			response = connection.getContent();
			Log.d(TAG,"response: "+response);
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
		Log.d(TAG,"to JSON");
		try{
			JsonReader reader = new JsonReader(new InputStreamReader(inStream,"UTF-8"));
			JsonObject json = gson.fromJson(reader, JsonObject.class);
			Log.d(TAG,"JSON: "+json);
			JsonElement kind = json.get("kind");
			Log.d(TAG,"kind: "+kind);
			String kindString = kind.getAsString();
			Log.d(TAG,"kind: "+kindString);
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
		return null;
	}
	public static class WebTask extends AsyncTask<Object,Object,Object>{ // <do-in-background-params, progress-params, post-execute-result>
		public static String EVENT_CALL_COMPLETE = "EVENT_CALL_COMPLETE";
		public static String PARAM_URL = "PARAM_URL"; // String
		public static String PARAM_METHOD = "PARAM_METHOD"; // String 
		public static String PARAM_CACHE = "PARAM_CACHE"; // Boolean
		public static String PARAM_EXPECTED = "PARAM_EXPECTED"; // Integer
		public static String PARAM_PROPERTIES = "PARAM_PROPERTIES"; // HashMap<String,String>
		public static String PARAM_CALLBACK = "PARAM_CALLBACK"; // Callback
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
//			int requestExpectedType = TYPE_EXPECTED_IMAGE;
//			String requestURL = "https://www.google.com/images/srpr/logo11w.png";
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
				/*Set<String> properties = requestProperties.keySet();
				Iterator<String> iter = properties.iterator();
				String key, value;
				while( iter.hasNext()){
					key = iter.next();
					value = requestProperties.get(key);
					connection.setRequestProperty(key,value);
				}*/
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
					cb.callback(EVENT_CALL_COMPLETE, result, responseCode, responseObject); // HttpURLConnection.HTTP_OK
				}
			}
			clear();
		}
	}
}
