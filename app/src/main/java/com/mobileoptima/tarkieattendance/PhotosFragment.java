package com.mobileoptima.tarkieattendance;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.mobileoptima.adapter.PhotosAdapter;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.core.Data;
import com.mobileoptima.model.ImageObj;
import com.mobileoptima.model.PhotoObj;

import java.util.ArrayList;

public class PhotosFragment extends Fragment {

	private OnOverrideCallback overrideCallback;
	private RelativeLayout rlPlaceholderPhotos;
	private FragmentTransaction transaction;
	private ArrayList<ImageObj> imageList;
	private FragmentManager manager;
	private PhotosAdapter adapter;
	private int spacing, numCol;
	private GridView gvPhotos;
	private SQLiteAdapter db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivity main = (MainActivity) getActivity();
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
		db.openConnection();
		numCol = CodePanUtils.getSupportedNoOfCol(main);
		spacing = CodePanUtils.pxToDp(getActivity(), numCol);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.photos_layout, container, false);
		rlPlaceholderPhotos = (RelativeLayout) view.findViewById(R.id.rlPlaceholderPhotos);
		gvPhotos = (GridView) view.findViewById(R.id.gvPhotos);
		gvPhotos.setNumColumns(numCol);
		gvPhotos.setVerticalSpacing(spacing);
		gvPhotos.setHorizontalSpacing(spacing);
		gvPhotos.setPadding(spacing, spacing, spacing, spacing);
		gvPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				ArrayList<PhotoObj> photoList = new ArrayList<>();
				photoList.addAll(imageList);
				ImagePreviewFragment imagePreview = new ImagePreviewFragment();
				imagePreview.setPhotoList(photoList, position);
				imagePreview.setDeletable(false);
				transaction = manager.beginTransaction();
				transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
						R.anim.slide_in_ltr, R.anim.slide_out_ltr);
				transaction.add(R.id.rlMain, imagePreview);
				transaction.addToBackStack(null);
				transaction.commit();
			}
		});
		loadPhotos(db);
		return view;
	}

	public void loadPhotos(final SQLiteAdapter db) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					imageList = Data.loadImages(db);
					handler.sendMessage(handler.obtainMessage());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message message) {
			if(imageList == null || imageList.isEmpty()) {
				rlPlaceholderPhotos.setVisibility(View.VISIBLE);
			}
			else {
				rlPlaceholderPhotos.setVisibility(View.GONE);
			}
			adapter = new PhotosAdapter(getActivity(), imageList);
			gvPhotos.setAdapter(adapter);
			return true;
		}
	});

	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}
}