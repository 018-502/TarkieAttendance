package com.mobileoptima.tarkieattendance;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.codepan.database.SQLiteAdapter;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanTextField;
import com.mobileoptima.adapter.AnnouncementsAdapter;
import com.mobileoptima.callback.Interface.OnDeleteAnnouncementCallback;
import com.mobileoptima.core.Data;
import com.mobileoptima.model.AnnouncementObj;

import java.util.ArrayList;

public class AnnouncementsFragment extends Fragment implements OnClickListener, OnDeleteAnnouncementCallback {
	private final long IDLE_TIME = 500;
	private ArrayList<AnnouncementObj> announcementsList;
	private AnnouncementsAdapter adapter;
	private RelativeLayout rlPlaceholderAnnouncements;
	private CodePanButton btnBackAnnouncements;
	private CodePanTextField etSearchAnnouncements;
	private FragmentTransaction transaction;
	private FragmentManager manager;
	private ListView lvAnnouncements;
	private Handler inputFinishHandler;
	private SQLiteAdapter db;
	private String search;
	private long lastEdit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inputFinishHandler = new Handler();
		MainActivity main = (MainActivity) getActivity();
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
		db.openConnection();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.announcements_layout, container, false);
		btnBackAnnouncements = (CodePanButton) view.findViewById(R.id.btnBackAnnouncements);
		etSearchAnnouncements = (CodePanTextField) view.findViewById(R.id.etSearchAnnouncements);
		lvAnnouncements = (ListView) view.findViewById(R.id.lvAnnouncements);
		rlPlaceholderAnnouncements = (RelativeLayout) view.findViewById(R.id.rlPlaceholderAnnouncements);
		btnBackAnnouncements.setOnClickListener(this);
		etSearchAnnouncements.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence cs, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence cs, int i, int i1, int i2) {
				search = cs.toString();
				lastEdit = System.currentTimeMillis();
				inputFinishHandler.removeCallbacks(inputFinishChecker);
				inputFinishHandler.postDelayed(inputFinishChecker, IDLE_TIME);
			}

			@Override
			public void afterTextChanged(Editable editable) {
			}
		});
		lvAnnouncements.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				AnnouncementDetailsFragment announcementDetails = new AnnouncementDetailsFragment();
				announcementDetails.setAnnouncement(announcementsList.get(i));
				announcementDetails.setOnDeleteAnnouncementCallback(AnnouncementsFragment.this);
				transaction = manager.beginTransaction();
				transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
						R.anim.slide_in_ltr, R.anim.slide_out_ltr);
				transaction.add(R.id.rlMain, announcementDetails);
				transaction.hide(AnnouncementsFragment.this);
				transaction.addToBackStack(null);
				transaction.commit();
			}
		});
		search = "";
		loadAnnouncements(db);
		return view;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnBackAnnouncements:
				manager.popBackStack();
				break;
		}
	}

	@Override
	public void onDeleteAnnouncement(AnnouncementObj obj) {
		announcementsList.remove(obj);
		adapter.notifyDataSetChanged();
		lvAnnouncements.invalidate();
//		handler.sendMessage(handler.obtainMessage());
	}

	public void loadAnnouncements(final SQLiteAdapter db) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					announcementsList = Data.loadAnnouncements(db, search);
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

	private Runnable inputFinishChecker = new Runnable() {
		@Override
		public void run() {
			if(System.currentTimeMillis() > lastEdit + IDLE_TIME - 500) {
				loadAnnouncements(db);
			}
		}
	};
}