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
import android.widget.ListView;

import com.codepan.database.SQLiteAdapter;
import com.codepan.model.GpsObj;
import com.codepan.utils.CodePanUtils;
import com.mobileoptima.adapter.BreakAdapter;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.constant.DialogTag;
import com.mobileoptima.core.Data;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.BreakObj;

import java.util.ArrayList;

public class BreakFragment extends Fragment {

	private final int SAVE = 1;
	private final int LOAD = 2;

	private OnOverrideCallback overrideCallback;
	private FragmentTransaction transaction;
	private ArrayList<BreakObj> breakList;
	private FragmentManager manager;
	private BreakAdapter adapter;
	private ListView lvBreak;
	private SQLiteAdapter db;
	private boolean result;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivity main = (MainActivity) getActivity();
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
		db.openConnection();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.break_layout, container, false);
		lvBreak = (ListView) view.findViewById(R.id.lvBreak);
		lvBreak.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				final BreakObj obj = breakList.get(i);
				if(!obj.isDone) {
					final AlertDialogFragment alert = new AlertDialogFragment();
					alert.setDialogTitle("Confirm Break");
					alert.setDialogMessage("Do you want to take your " + obj.name + " now?");
					alert.setPositiveButton("Yes", new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							GpsObj gps = ((MainActivity) getActivity()).getGps();
							saveBreakIn(db, gps, obj);
						}
					});
					alert.setNegativeButton("No", new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							manager.popBackStack();
						}
					});
					transaction = manager.beginTransaction();
					transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
							R.anim.fade_in, R.anim.fade_out);
					transaction.add(R.id.rlMain, alert);
					transaction.addToBackStack(null);
					transaction.commit();
				}
				else {
					CodePanUtils.alertToast(getActivity(), "You've already used " + obj.name);
				}
			}
		});
		loadBreaks(db);
		return view;
	}

	public void saveBreakIn(final SQLiteAdapter db, final GpsObj gps, final BreakObj obj) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					result = TarkieLib.saveBreakIn(db, gps, obj);
					handler.obtainMessage(SAVE, obj).sendToTarget();
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
		public boolean handleMessage(Message msg) {
			switch(msg.what) {
				case SAVE:
					if(result) {
						BreakObj obj = (BreakObj) msg.obj;
						manager.popBackStack();
						manager.popBackStack();
						BreakTimeFragment breakTime = new BreakTimeFragment();
						breakTime.setBreak(obj);
						breakTime.setOnOverrideCallback(overrideCallback);
						transaction = manager.beginTransaction();
						transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
								R.anim.fade_in, R.anim.fade_out);
						transaction.add(R.id.rlMain, breakTime, DialogTag.BREAK);
						transaction.addToBackStack(null);
						Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								transaction.commit();
							}
						}, 250);
						((MainActivity) getActivity()).updateSyncCount();
					}
					else {
						CodePanUtils.alertToast(getActivity(), "Failed to save break.");
					}
					break;
				case LOAD:
					adapter = new BreakAdapter(getActivity(), breakList);
					lvBreak.setAdapter(adapter);
					break;
			}
			return true;
		}
	});

	public void loadBreaks(final SQLiteAdapter db) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					breakList = Data.loadBreaks(db);
					handler.obtainMessage(LOAD).sendToTarget();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}
}
