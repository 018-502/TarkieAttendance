package com.mobileoptima.tarkieattendance;

import android.graphics.Bitmap;
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
import android.widget.ImageView;

import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.model.GpsObj;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.callback.Interface.OnRetakeCameraCallback;
import com.mobileoptima.constant.App;
import com.mobileoptima.constant.ImageType;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.AttendanceObj;
import com.mobileoptima.model.StoreObj;
import com.mobileoptima.service.MainService;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

public class CapturedFragment extends Fragment implements OnClickListener,
		OnBackPressedCallback, OnFragmentCallback {

	private CodePanButton btnBackCaptured, btnRetakeCaptured, btnUsePhotoCaptured;
	private OnRetakeCameraCallback retakeCameraCallback;
	private OnOverrideCallback overrideCallback;
	private FragmentTransaction transaction;
	private CodePanLabel tvTitleCaptured;
	private String date, time, photo;
	private FragmentManager manager;
	private ImageView ivCaptured;
	private SQLiteAdapter db;
	private boolean result;
	private StoreObj store;
	private GpsObj gps;
	private int type;

	@Override
	public void onStart() {
		super.onStart();
		setOnBackStack(true);
	}

	@Override
	public void onStop() {
		super.onStop();
		setOnBackStack(false);
	}

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
		View view = inflater.inflate(R.layout.captured_layout, container, false);
		ivCaptured = (ImageView) view.findViewById(R.id.ivCaptured);
		btnBackCaptured = (CodePanButton) view.findViewById(R.id.btnBackCaptured);
		btnRetakeCaptured = (CodePanButton) view.findViewById(R.id.btnRetakeCaptured);
		btnUsePhotoCaptured = (CodePanButton) view.findViewById(R.id.btnUsePhotoCaptured);
		tvTitleCaptured = (CodePanLabel) view.findViewById(R.id.tvTitleCaptured);
		btnBackCaptured.setOnClickListener(this);
		btnRetakeCaptured.setOnClickListener(this);
		btnUsePhotoCaptured.setOnClickListener(this);
		if(photo != null) {
			Bitmap bitmap = CodePanUtils.getBitmapImage(getActivity(), App.FOLDER, photo);
			ivCaptured.setImageBitmap(bitmap);
		}
		switch(type) {
			case ImageType.TIME_IN:
				tvTitleCaptured.setText(R.string.time_in);
				break;
			case ImageType.TIME_OUT:
				tvTitleCaptured.setText(R.string.time_out);
				break;
		}
		return view;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnBackCaptured:
				onBackPressed();
				break;
			case R.id.btnRetakeCaptured:
				if(photo != null) {
					CodePanUtils.deleteFile(getActivity(), App.FOLDER, photo);
				}
				if(retakeCameraCallback != null) {
					retakeCameraCallback.onRetakeCamera();
				}
				manager.popBackStack();
				break;
			case R.id.btnUsePhotoCaptured:
				switch(type) {
					case ImageType.TIME_IN:
						saveTimeIn();
						break;
					case ImageType.TIME_OUT:
						String timeInID = TarkieLib.getTimeInID(db);
						AttendanceObj attendance = TarkieLib.getAttendance(db, timeInID);
						attendance.out.dDate = date;
						attendance.out.dTime = time;
						SummaryFragment summary = new SummaryFragment();
						summary.setAttendance(attendance);
						summary.setGps(gps);
						summary.setImage(photo);
						summary.setIsTimeOut(true);
						summary.setOnOverrideCallback(overrideCallback);
						transaction = manager.beginTransaction();
						transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
								R.anim.slide_in_ltr, R.anim.slide_out_ltr);
						transaction.add(R.id.rlMain, summary);
						transaction.hide(this);
						transaction.addToBackStack(null);
						transaction.commit();
						break;
				}
				break;
		}
	}

	public void saveTimeIn() {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					result = TarkieLib.saveTimeIn(db, photo, gps, store);
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
			CodePanUtils.alertToast(getActivity(), result ?
					"Time-in successful" : "Failed to save time-in.");
			manager.popBackStack(null, POP_BACK_STACK_INCLUSIVE);
			MainActivity main = (MainActivity) getActivity();
			main.checkTimeIn();
			main.updateSyncCount();
			MainService service = main.getService();
			service.syncData(db);
			return true;
		}
	});

	public void setImage(String fileName) {
		this.photo = fileName;
	}

	public void setImageType(int type) {
		this.type = type;
	}

	public void setStore(StoreObj store) {
		this.store = store;
	}

	public void setGps(GpsObj gps) {
		this.gps = gps;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}

	public void setOnRetakeCameraCallback(OnRetakeCameraCallback retakeCameraCallback) {
		this.retakeCameraCallback = retakeCameraCallback;
	}

	@Override
	public void onBackPressed() {
		final AlertDialogFragment alert = new AlertDialogFragment();
		alert.setOnFragmentCallback(this);
		alert.setDialogTitle("Discard Photo?");
		alert.setDialogMessage("Are you sure you want to discard this photo?");
		alert.setPositiveButton("Yes", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnRetakeCaptured.performClick();
				manager.popBackStack();
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

	private void setOnBackStack(boolean isOnBackStack) {
		if(overrideCallback != null) {
			overrideCallback.onOverride(isOnBackStack);
		}
	}

	@Override
	public void onFragment(boolean status) {
		if(overrideCallback != null) {
			overrideCallback.onOverride(!status);
		}
	}
}
