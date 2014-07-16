package com.richtastic.code;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import com.richtastic.structuresystem.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
public class Networking {
	private static String TAG = "Networking";
	public static int REQUEST_PRIORITY_LOW = 2000;
	public static int REQUEST_PRIORITY_MEDIUM = 1000;
	public static int REQUEST_PRIORITY_HIGH = 0;
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
	// http://developer.android.com/reference/android/os/AsyncTask.html
	public static Object connectionResultToKnownType(URLConnection connection){
		Object response;
		InputStream inStream;
		try{
			inStream = connection.getInputStream();
			response = connection.getContent();
			if( response instanceof Bitmap ){
				return instreamToBitmap(inStream);
			}else{
				return instreamToString(inStream);
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}
	public static String instreamToString(InputStream inStream){
		return null;
	}
	public static Bitmap instreamToBitmap(InputStream inStream){
		return null;
	}
	public static Object instreamToJSON(InputStream inStream){
		return null;
	}
	public static class WebTask extends AsyncTask<Object,Object,Object>{ // <do-in-background-params, progress-params, post-execute-result>
		private WeakReference<Object> callback; // change this to Callback
		@Override
		protected Object doInBackground(Object... params){ // url, useCache, callback
			if(params.length>0){
				callback = new WeakReference<Object>(params[0]);
				Log.d(TAG,"set callback: "+callback);
			}
			Log.d(TAG,"doInBackground");
			// set url
			URL url;
			try{ url = new URL("http://static.adzerk.net/Advertisers/d893babe671c41118c1fece177e0a21a.jpg");
			}catch (MalformedURLException e){ e.printStackTrace(); return null; }
			// set connection
			URLConnection connection;
			try{ connection = url.openConnection();
			}catch(IOException e){ e.printStackTrace(); return null; }
			connection.setUseCaches(true);
			// get response
			Object response;
			try{ response = connection.getContent();
			}catch (IOException e) { e.printStackTrace(); return null; }
			// return value
			if(this.isCancelled()){ return null; }
			
			Log.d(TAG,"connection: "+connection);
			InputStream inStream = null;
			try{ inStream = connection.getInputStream();
			}catch(IOException e){ e.printStackTrace(); }
			Log.d(TAG,"instream: "+inStream);
			Bitmap bitmap = BitmapFactory.decodeStream(inStream);
			Log.d(TAG,"bitmap: "+bitmap);
			
			return bitmap;//connection;//response;
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
			Log.d(TAG,"onPostExecute "+result);
			if(callback!=null){
				Callback cb = (Callback)callback.get();
				if(cb!=null){
					cb.callback(result);
				}
			}
		}
	}
}
