package com.richtastic.structuresystem;
import java.util.List;

import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Activity;
//import android.support.v4.app.*;
import android.app.Fragment;
import android.content.Context;


public class SubmissionGalleryFragment extends Fragment {
	private String[] data = new String[]{"Amsterdam","Bendali","Couches","Damsel","Eiffle","Funky","Gears","Heckling","Insanity","Jeoff - did you mean Jeff?","Kamels","Lizzards","Mountains","And So On"};
// ------------------------------------------------------------------------------------------------------------------------------------
	private static String TAG = "SubmissionGalleryFragment";
	//private SimpleCursorAdapter mAdapter;
	private ListView mListView;
	private GalleryFragmentAdapter mAdapter; 
	@Override
	public void onAttach(Activity activity){
		Log.d(TAG, "SubmissionGalleryFragment - onAttach");
		super.onAttach(activity);
	}
	@Override
	public void onCreate(Bundle savedInstanceState){
		Log.d(TAG, "SubmissionGalleryFragment - onCreate ("+savedInstanceState+")");
		super.onCreate(savedInstanceState);
		if(savedInstanceState==null){
			// new
		}else{
			Log.d(TAG, "found: "+savedInstanceState.getInt("STATE_DATA"));
		}
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		Log.d(TAG, "SubmissionGalleryFragment - onCreateView");
		return inflater.inflate(R.layout.fragment_submission_gallery, container, false);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		Log.d(TAG, "SubmissionGalleryFragment - onActivityCreated ("+savedInstanceState+")");
		super.onActivityCreated(savedInstanceState);
	}
	@Override
	public void onStart(){
		Log.d(TAG, "SubmissionGalleryFragment - onStart");
		super.onStart();
		if(mListView==null){
			mListView = (ListView)getView().findViewById(R.id.main_list_view);
			mListView.setDividerHeight(0);
			Log.d(TAG,"list view: "+mListView);
			mAdapter = new GalleryFragmentAdapter(this.getActivity(), 0, null);
			Log.d(TAG,"adapter: "+mAdapter);
			mListView.setAdapter(mAdapter);
			mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						Log.d(TAG,"I duth clicked ?: "+arg0+" "+arg1+" "+arg2+" "+arg3);
				}
			});
		}
	}
	@Override
	public void onResume(){
		Log.d(TAG, "SubmissionGalleryFragment - onResume");
		super.onResume();
	}
	//
	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putInt("STATE_DATA",22);
		Log.d(TAG, "SubmissionGalleryFragment - onSaveInstanceState ("+outState+")");
	}
	//
	@Override
	public void onPause(){
		Log.d(TAG, "SubmissionGalleryFragment - onPause");
		super.onStop();
	}
	@Override
	public void onStop(){
		Log.d(TAG, "SubmissionGalleryFragment - onStop");
		super.onStop();
	}
	@Override
	public void onDestroyView(){
		Log.d(TAG, "SubmissionGalleryFragment - onDestroyView");
		super.onDestroyView();
	}
	@Override
	public void onDestroy(){
		Log.d(TAG, "SubmissionGalleryFragment - onDestroy");
		super.onDestroy();
	}
	@Override
	public void onDetach(){
		Log.d(TAG, "SubmissionGalleryFragment - onDetach");
		super.onDetach();
	}
	
	@Override
	public void onTrimMemory(int level){
		//if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH){
			super.onTrimMemory(level);
			Log.d(TAG, "memory level: "+level);
		//}
	}
	
	public void onClickFxn(View view){
		// reload data 
		data = new String[10];
		int i;
		for(i=0;i<data.length;++i){
			data[i] = ""+((int)Math.floor(Math.random()*1000));
		}
		//data = new String[]{"A","B","C","D","E","F","G","H","I"
		mAdapter.notifyDataSetChanged();
		mListView.invalidate(); // necessary?
		mListView.smoothScrollToPosition(0); // gradual
//		mListView.setSelection(0); //also immediate
//		mListView.setSelectionAfterHeaderView(); // immediate
		//mAdapter.clear()
	}
	
	
	
	private class GalleryFragmentAdapter extends ArrayAdapter<Object>{
		
		public GalleryFragmentAdapter(Context context, int resource, List<Object> objects) {
			super(context, resource, objects);
			Log.d(TAG,"created");
		}
		@Override
		public int getCount(){
			return SubmissionGalleryFragment.this.data.length;
		}
		@Override 
		public Object getItem(int position){
			Log.d(TAG,"getItem: "+position);
			return SubmissionGalleryFragment.this.data[position];
		}
		@Override
		public View getView(int position, View view, ViewGroup parent){
			Log.d(TAG,"getView: "+position);
			View row;
			Log.d(TAG,"checking: "+position);
			if(view!=null){
				Log.d(TAG,"-> RECYCLED: "+view);
				row = view;
			}else{
				Log.d(TAG,"-> NULL");
				LayoutInflater inflater = LayoutInflater.from(SubmissionGalleryFragment.this.getActivity());
				row = inflater.inflate(R.layout.item_gallery_submission, null);
			}
			TextView text = (TextView)row.findViewById(R.id.title);
			text.setText(SubmissionGalleryFragment.this.data[position]);
//			Button button = (Button)row.findViewById(R.id.fxn_button);
//			if(button!=null){
//				button.setOnClickListener(new View.OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						SubmissionGalleryFragment.this.onClickFxn(v);
//					}
//				});
//			}
			row.setBackgroundColor(0xFFFF0000);
			return row;
		}
		
	}
	
	
	
	
}



