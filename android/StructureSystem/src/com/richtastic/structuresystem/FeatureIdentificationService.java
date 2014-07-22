package com.richtastic.structuresystem;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.util.*;

public class FeatureIdentificationService extends Service{
	public static final String INTENT_HELLO = "FeatureIdentificationService.INTENT_HELLO";
	private static final String TAG = "FeatureIdentificationService";
	private Messenger mMessenger = new Messenger( new FeatureIdentificationHandler() );
	private boolean allowRebind = true;
	private AsyncTask task = null;
	
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
		int val = super.onStartCommand(intent, flags, startId);
		return val;
	}
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG,"FeatureIdentificationService - onBind("+intent+")");
		
		AsyncTask<Object, Object, Object> task = new AsyncTask<Object, Object, Object>(){
			private int iteration = 100; 
			@Override
			protected Object doInBackground(Object... arg0) {
				//doWork();
				int i;
				for(i=0;i<=iteration;++i){
					if(this.isCancelled()){
						break;
					}
					Log.d(TAG," iteration "+i);
					if(i%10==0 && i>0){
						Intent intent = new Intent(INTENT_HELLO);
						FeatureIdentificationService.this.sendBroadcast(intent);
					}
					try{
						Thread.sleep(1000);
					}catch(InterruptedException e){
						e.printStackTrace();
					}
				}
				Log.d(TAG," done ");
				return null;
			}
			@Override
			protected void onCancelled(){
				Log.d(TAG,"FeatureIdentificationService - onCancelled");
				iteration = 0;
				super.onCancelled();
			}
		};
		this.task = task;
		task.execute("bla");
		
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
		if(this.task!=null){
			Log.d(TAG,"CANCEL THE TASK");
			this.task.cancel(true);
			this.task = null;
		}
		super.onDestroy();
	}
	private class FeatureIdentificationHandler extends Handler{
		// get weakreference to FeatureIdentificationService.this
		@Override
		public void handleMessage(Message message){
			Log.d(TAG,"GOT MESSAGE "+message.what+" : "+message);
			Intent intent = new Intent(INTENT_HELLO);
			FeatureIdentificationService.this.sendBroadcast(intent);
			Message reply = Message.obtain(null, 987654321);
			try {
				message.replyTo.send( reply );
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private int count = 0;
	/*private void doWork(){
		int i;
		for(i=0;i<=100;++i){
			Log.d(TAG," iteration "+i);
			if(i%10==0 && i>0){
				Intent intent = new Intent(INTENT_HELLO);
				this.sendBroadcast(intent);
			}
			try{
				Thread.sleep(1000);
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
		Log.d(TAG," done ");
	}*/
}
