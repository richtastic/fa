package com.richtastic.structuresystem;

//import android.app.Service;
import android.app.IntentService;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ReconstructionService extends IntentService{
	private static String TAG = "ReconstructionService"; 
//	public ReconstructionService(String name){
//		//String name = "ReconstructionService";
//		super(name);
//		Log.d(TAG," constructor");
//	}
	public ReconstructionService(){
		super("ReconstructionService");
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG," onHandleIntent");
		int id = 1234567;
		Intent request = new Intent(this, StartupActivity.class);
		request.setFlags(Notification.FLAG_ONGOING_EVENT); // request.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pi = PendingIntent.getActivity(this, 0, request, Notification.FLAG_ONGOING_EVENT);
		// getApplicationContext()
		Notification notification = new Notification.Builder(this).setContentTitle("TITLE GOES HERE").setContentText("LONG DESC HERE").setContentIntent(pi).build();
		//new Notification(R.drawable.ic_launcher,"Reconstruction Background",System.currentTimeMillis());
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		
		startForeground(id, notification);
		
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE); 
		
		// do work
		int i;
		for(i=0;i<=100;++i){
			Log.d(TAG," iteration "+i);
			if(i%10==0 && i>0){
				notification = new Notification(R.drawable.ic_launcher,"BG "+i,System.currentTimeMillis());
				notification.setLatestEventInfo(this, "ITERATION", "it: "+i, pi);
				notification.flags = Notification.FLAG_FOREGROUND_SERVICE;
				mNotificationManager.notify(id, notification);
			}
			try{
				Thread.sleep(1000);
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
		Log.d(TAG," done ");
	}
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG," onBind");
		return super.onBind(intent);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		Log.d(TAG," onStartCommand "+intent+" "+flags+" "+startId);
		return super.onStartCommand(intent, flags, startId);
//		stopSelf();
//		stopService(intent);
		// start new thread to run independently of parent activity process/thread
	}
	
	@Override
	public void onCreate(){
		Log.d(TAG," onCreate");
		super.onCreate();
	}
	@Override
	public void onDestroy(){
		Log.d(TAG," onDestroy");
		stopForeground(true);
		super.onDestroy();
	}
}
