package com.mobileoptima.tarkieattendance;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.codepan.callback.Interface.OnRefreshCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.CodePanTextField;
import com.mobileoptima.callback.Interface.OnCheckInCallback;
import com.mobileoptima.callback.Interface.OnCheckOutCallback;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.constant.ImageType;
import com.mobileoptima.constant.Result;
import com.mobileoptima.constant.Settings;
import com.mobileoptima.core.Data;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.CheckInObj;
import com.mobileoptima.model.CheckOutObj;
import com.mobileoptima.model.FormObj;
import com.mobileoptima.model.StoreObj;
import com.mobileoptima.model.VisitObj;

import java.util.ArrayList;

public class VisitDetailsFragment extends Fragment implements OnClickListener,
		OnCheckInCallback, OnCheckOutCallback {

	private CodePanButton btnCheckInVisitDetails, btnCheckOutVisitDetails, btnBackVisitDetails;
	private CodePanLabel tvStoreVisitDetails, tvAddressVisitDetails;
	private CodePanTextField etNotesVisitDetails;
	private OnOverrideCallback overrideCallback;
	private OnRefreshCallback refreshCallback;
	private LinearLayout llFormsVisitDetails;
	private FragmentTransaction transaction;
	private ArrayList<FormObj> formList;
	private FragmentManager manager;
	private ViewGroup container;
	private MainActivity main;
	private SQLiteAdapter db;
	private VisitObj visit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main = (MainActivity) getActivity();
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
		db.openConnection();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.visit_details_layout, container, false);
		tvStoreVisitDetails = (CodePanLabel) view.findViewById(R.id.tvStoreVisitDetails);
		tvAddressVisitDetails = (CodePanLabel) view.findViewById(R.id.tvAddressVisitDetails);
		etNotesVisitDetails = (CodePanTextField) view.findViewById(R.id.etNotesVisitDetails);
		llFormsVisitDetails = (LinearLayout) view.findViewById(R.id.llFormsVisitDetails);
		btnBackVisitDetails = (CodePanButton) view.findViewById(R.id.btnBackVisitDetails);
		btnCheckInVisitDetails = (CodePanButton) view.findViewById(R.id.btnCheckInVisitDetails);
		btnCheckOutVisitDetails = (CodePanButton) view.findViewById(R.id.btnCheckOutVisitDetails);
		btnBackVisitDetails.setOnClickListener(this);
		btnCheckInVisitDetails.setOnClickListener(this);
		btnCheckOutVisitDetails.setOnClickListener(this);
		this.container = container;
		if(visit != null) {
			StoreObj store = visit.store;
			if(store != null) {
				tvStoreVisitDetails.setText(store.name);
				tvAddressVisitDetails.setText(store.address);
			}
			if(visit.isCheckIn) {
				CheckInObj in = visit.in;
				setCheckInTime(in.dTime);
			}
			if(visit.isCheckOut) {
				CheckOutObj out = visit.out;
				setCheckOutTime(out.dTime);
			}
			else {
				btnCheckOutVisitDetails.setEnabled(visit.isCheckIn);
			}
			etNotesVisitDetails.setText(visit.notes);
			loadForms(db, visit.ID);
		}
		return view;
	}

	public void loadForms(final SQLiteAdapter db, final String taskID) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					formList = Data.loadForms(db, taskID);
					formsHandler.sendMessage(formsHandler.obtainMessage());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	Handler formsHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message message) {
			LayoutInflater inflater = getActivity().getLayoutInflater();
			for(FormObj form : formList) {
				View view = inflater.inflate(R.layout.visit_details_form_item, container, false);
				CodePanLabel tvVisitDetailsForm = (CodePanLabel) view.findViewById(R.id.tvVisitDetailsForm);
				CodePanButton btnVisitDetailsForm = (CodePanButton) view.findViewById(R.id.btnVisitDetailsForm);
				tvVisitDetailsForm.setText(form.name);
				btnVisitDetailsForm.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
					}
				});
				llFormsVisitDetails.addView(view);
			}
			return true;
		}
	});

	public void setVisit(VisitObj visit) {
		this.visit = visit;
	}

	public void setCheckInTime(String time) {
		String in = "IN - " + CodePanUtils.getNormalTime(time, false);
		btnCheckInVisitDetails.setText(in);
		btnCheckInVisitDetails.setEnabled(false);
	}

	public void setCheckOutTime(String time) {
		String out = "OUT - " + CodePanUtils.getNormalTime(time, false);
		btnCheckOutVisitDetails.setText(out);
		btnCheckOutVisitDetails.setEnabled(false);
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnBackVisitDetails:
				manager.popBackStack();
				break;
			case R.id.btnCheckInVisitDetails:
				if(TarkieLib.isTimeIn(db)) {
					if(!visit.isCheckIn) {
						String date = CodePanUtils.getDate();
						String time = CodePanUtils.getTime();
						if(TarkieLib.isSettingsEnabled(db, Settings.CHECK_IN_PHOTO)) {
							CameraFragment camera = new CameraFragment();
							camera.setDate(date);
							camera.setTime(time);
							camera.setTask(visit);
							camera.setGps(main.getGps());
							camera.setOnCheckInCallback(this);
							camera.setImageType(ImageType.CHECK_IN);
							camera.setOnOverrideCallback(overrideCallback);
							transaction = manager.beginTransaction();
							transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
									R.anim.slide_in_ltr, R.anim.slide_out_ltr);
							transaction.add(R.id.rlMain, camera);
							transaction.addToBackStack(null);
							transaction.commit();
						}
						else {
							CheckInObj in = new CheckInObj();
							in.gps = main.getGps();
							in.dDate = date;
							in.dTime = time;
							in.task = visit;
							onCheckIn(in);
						}
					}
				}
				else {
					TarkieLib.alertDialog(getActivity(), R.string.time_in_required_title,
							R.string.time_in_required_visit_message);
				}
				break;
			case R.id.btnCheckOutVisitDetails:
				if(!visit.isCheckOut) {
					String date = CodePanUtils.getDate();
					String time = CodePanUtils.getTime();
					if(TarkieLib.isSettingsEnabled(db, Settings.CHECK_OUT_PHOTO)) {
						CameraFragment camera = new CameraFragment();
						camera.setDate(date);
						camera.setTime(time);
						camera.setTask(visit);
						camera.setGps(main.getGps());
						camera.setOnCheckOutCallback(this);
						camera.setImageType(ImageType.CHECK_OUT);
						camera.setOnOverrideCallback(overrideCallback);
						transaction = manager.beginTransaction();
						transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
								R.anim.slide_in_ltr, R.anim.slide_out_ltr);
						transaction.add(R.id.rlMain, camera);
						transaction.addToBackStack(null);
						transaction.commit();
					}
					else {
						CheckOutObj out = new CheckOutObj();
						out.gps = main.getGps();
						out.dDate = date;
						out.dTime = time;
						CheckInObj in = new CheckInObj();
						in.ID = TarkieLib.getCheckInID(db);
						in.task = visit;
						out.checkIn = in;
						onCheckOut(out);
					}
				}
				break;
		}
	}

	@Override
	public void onCheckIn(final CheckInObj in) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					boolean result = TarkieLib.saveCheckIn(db, in.gps, in.task, in.dDate,
							in.dTime, in.photo);
					checkInHandler.sendMessage(checkInHandler
							.obtainMessage(result ? Result.SUCCESS : Result.FAILED, in));
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	Handler checkInHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what) {
				case Result.SUCCESS:
					CheckInObj in = (CheckInObj) msg.obj;
					visit.in = in;
					visit.isCheckIn = true;
					setCheckInTime(in.dTime);
					btnCheckOutVisitDetails.setEnabled(true);
					main.updateSyncCount();
					if(refreshCallback != null) {
						refreshCallback.onRefresh();
					}
					CodePanUtils.alertToast(getActivity(), "Check-in successful");
					break;
				case Result.FAILED:
					CodePanUtils.alertToast(getActivity(), "Failed to save check-in.");
					break;
			}
			return true;
		}
	});

	@Override
	public void onCheckOut(final CheckOutObj out) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					boolean result = TarkieLib.saveCheckOut(db, out.gps, out.checkIn, out.dDate,
							out.dTime, out.photo);
					checkOutHandler.sendMessage(checkOutHandler
							.obtainMessage(result ? Result.SUCCESS : Result.FAILED, out));
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	Handler checkOutHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what) {
				case Result.SUCCESS:
					CheckOutObj out = (CheckOutObj) msg.obj;
					visit.out = out;
					visit.isCheckOut = true;
					setCheckOutTime(out.dTime);
					main.updateSyncCount();
					if(refreshCallback != null) {
						refreshCallback.onRefresh();
					}
					CodePanUtils.alertToast(getActivity(), "Check-out successful");
					break;
				case Result.FAILED:
					CodePanUtils.alertToast(getActivity(), "Failed to save check-out.");
					break;
			}
			return true;
		}
	});

	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}

	public void setOnRefreshCallback(OnRefreshCallback refreshCallback) {
		this.refreshCallback = refreshCallback;
	}
}
