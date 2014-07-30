package com.richtastic.code;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ImageCallbackView extends ImageView implements Callback{
	private static String TAG = "ImageCallbackView";
	public boolean showProgress = true;
	ProgressBar spinner;
	public ImageCallbackView(Context context){
		super(context);
	}
	public void loadImage(String url){
		Cache cache = Cache.sharedInstance();
		Object object = cache.getImmediate(url);
		if(object!=null){
			Log.d(TAG,"IMMEDIATE");
			Bitmap bitmap = (Bitmap)object;
			setImageBitmap(bitmap);
		}else{
			Log.d(TAG,"DELAYED");
			if(showProgress){
				ViewGroup parent = (ViewGroup)getParent();
				spinner = new ProgressBar(this.getContext());
				parent.addView(spinner);
				spinner.setVisibility(View.VISIBLE);
			}
			cache.getImage(url, this);
		}
	}
	@Override
	public void callback(Object... params) {
		Log.d(TAG,"GOT BACK PARAMS: "+params);
		Bitmap bitmap = (Bitmap)params[1];
		if(bitmap!=null){
			setImageBitmap(bitmap);
			if(spinner!=null){
				ViewGroup parent = (ViewGroup)getParent();
				parent.removeView(spinner);
			}
		}else{
			Log.d(TAG,"unable to get image from cache");
		}
	}
}
