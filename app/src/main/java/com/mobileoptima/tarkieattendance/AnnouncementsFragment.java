package com.mobileoptima.tarkieattendance;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.widget.CodePanButton;
import com.mobileoptima.adapter.AnnouncementsAdapter;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.constant.Tag;
import com.mobileoptima.core.Data;
import com.mobileoptima.model.AnnouncementObj;

import java.util.ArrayList;

public class AnnouncementsFragment extends Fragment implements OnClickListener, OnBackPressedCallback {
	private ArrayList<AnnouncementObj> announcementsList;
	private AnnouncementsAdapter adapter;
	private RelativeLayout rlPlaceholderAnnouncements;
	private CodePanButton btnBackAnnouncements, btnSearchAnnouncements;
	private OnOverrideCallback overrideCallback;
	private FragmentTransaction transaction;
	private FragmentManager manager;
	private ListView lvAnnouncements;
	private SQLiteAdapter db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivity main = (MainActivity) getActivity();
		main.setOnBackPressedCallback(this);
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
		db.openConnection();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.announcements_layout, container, false);
		btnBackAnnouncements = (CodePanButton) view.findViewById(R.id.btnBackAnnouncements);
		btnSearchAnnouncements = (CodePanButton) view.findViewById(R.id.btnSearchAnnouncements);
		lvAnnouncements = (ListView) view.findViewById(R.id.lvAnnouncements);
		rlPlaceholderAnnouncements = (RelativeLayout) view.findViewById(R.id.rlPlaceholderAnnouncements);
		btnBackAnnouncements.setOnClickListener(this);
		btnSearchAnnouncements.setOnClickListener(this);
		lvAnnouncements.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				Bundle bundle = new Bundle();
				bundle.putSerializable("Announcement", announcementsList.get(i));
				AnnouncementDetailsFragment announcementDetails = new AnnouncementDetailsFragment();
				announcementDetails.setArguments(bundle);
				announcementDetails.setOnOverrideCallback(overrideCallback);
				transaction = manager.beginTransaction();
				transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
						R.anim.slide_in_ltr, R.anim.slide_out_ltr);
				transaction.add(R.id.rlMain, announcementDetails, Tag.FORM);
				transaction.hide(AnnouncementsFragment.this);
				transaction.addToBackStack(null);
				transaction.commit();
			}
		});
		loadAnnouncements(db);
		return view;
	}

	public void loadAnnouncements(final SQLiteAdapter db) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					announcementsList = Data.loadAnnouncements(db);
					handler.sendMessage(handler.obtainMessage());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	Handler handler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message message) {
			if(announcementsList == null || announcementsList.isEmpty()) {
				rlPlaceholderAnnouncements.setVisibility(View.VISIBLE);
			}
			else {
				rlPlaceholderAnnouncements.setVisibility(View.GONE);
			}
			adapter = new AnnouncementsAdapter(getActivity(), announcementsList);
			lvAnnouncements.setAdapter(adapter);
			return true;
		}
	});

	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}

	@Override
	public void onBackPressed() {
		int count = manager.getBackStackEntryCount();
		if(count > 0) {
			manager.popBackStack();
		}
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnBackAnnouncements:
				onBackPressed();
				break;
			case R.id.btnSearchAnnouncements:
				break;
		}
	}
}