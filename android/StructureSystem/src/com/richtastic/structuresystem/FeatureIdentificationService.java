package com.richtastic.structuresystem;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.*;

public class FeatureIdentificationService extends Service{
	private static final String TAG = "FeatureIdentificationService";
	private Messenger mMessenger = new Messenger( new FeatureIdentificationHandler() );
	private boolean allowRebind = true;
	
	@Override
	public void onCreate(){
		Log.d(TAG,"FeatureIdentificationService - onCreate");
		super.onCreate();
	}
	@Override
	public void onStart(Intent intent, int startId){
		Log.d(TAG,"FeatureIdentificationService - onStart("+intent+","+startId+")");
		super.onStart(intent, startId);
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		Log.d(TAG,"FeatureIdentificationService - onStartCommand("+intent+","+flags+","+startId+")");
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG,"FeatureIdentificationService - onBind("+intent+")");
		return mMessenger.getBinder();
	}
	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG,"FeatureIdentificationService - onUnbind("+intent+")");
		return allowRebind; // return super.onUnbind(intent);
	}
	@Override
	public void onRebind(Intent intent) {
		Log.d(TAG,"FeatureIdentificationService - onRebind("+intent+")");
		super.onRebind(intent);
	}
	@Override
	public void onDestroy(){
		Log.d(TAG,"FeatureIdentificationService - onDestroy");
		// 
		super.onDestroy();
	}
	public static class FeatureIdentificationHandler extends Handler{
		@Override
		public void handleMessage(Message message){
			Log.d(TAG,"GOT MESSAGE "+message.what+" : "+message);
		}
	}
}
