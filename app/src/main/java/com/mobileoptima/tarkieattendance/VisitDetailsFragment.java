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

import com.codepan.database.SQLiteAdapter;
import com.codepan.model.GpsObj;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.CodePanTextField;
import com.mobileoptima.callback.Interface.OnCheckInCallback;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.constant.ImageType;
import com.mobileoptima.constant.Result;
import com.mobileoptima.constant.Settings;
import com.mobileoptima.core.Data;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.FormObj;
import com.mobileoptima.model.StoreObj;
import com.mobileoptima.model.TaskObj;

import java.util.ArrayList;

public class VisitDetailsFragment extends Fragment implements OnClickListener,
		OnCheckInCallback {

	private CodePanButton btnCheckInVisitDetails, btnCheckOutVisitDetails, btnBackVisitDetails;
	private CodePanLabel tvStoreVisitDetails, tvAddressVisitDetails;
	private CodePanTextField etNotesVisitDetails;
	private OnOverrideCallback overrideCallback;
	private LinearLayout llFormsVisitDetails;
	private FragmentTransaction transaction;
	private ArrayList<FormObj> formList;
	private FragmentManager manager;
	private ViewGroup container;
	private SQLiteAdapter db;
	private TaskObj task;

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
		if(task != null) {
			StoreObj store = task.store;
			if(store != null) {
				tvStoreVisitDetails.setText(store.name);
				tvAddressVisitDetails.setText(store.address);
			}
			etNotesVisitDetails.setText(task.notes);
			loadForms(db, task.ID);
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

	public void setTask(TaskObj task) {
		this.task = task;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnCheckInVisitDetails:
				MainActivity main = (MainActivity) getActivity();
				GpsObj gps = main.getGps();
				if(TarkieLib.isSettingsEnabled(db, Settings.CHECK_IN_PHOTO)) {
					CameraFragment camera = new CameraFragment();
					camera.setGps(gps);
					camera.setTask(task);
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
					onCheckIn(gps, task, null);
				}
				break;
			case R.id.btnCheckOutVisitDetails:
				break;
		}
	}

	@Override
	public void onCheckIn(final GpsObj gps, final TaskObj task, final String photo) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					boolean result = TarkieLib.saveCheckIn(db, gps, task, photo);
					checkInHandler.sendMessage(checkInHandler.
							obtainMessage(result ? Result.SUCCESS : Result.FAILED));
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
					CodePanUtils.alertToast(getActivity(), "Check-in successful");
					MainActivity main = (MainActivity) getActivity();
					main.updateSyncCount();
					break;
				case Result.FAILED:
					CodePanUtils.alertToast(getActivity(), "Failed to save check-in.");
					break;
			}
			return true;
		}
	});

	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}
}
