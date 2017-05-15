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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.callback.Interface.OnSignCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.model.GpsObj;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.constant.App;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.AttendanceObj;
import com.mobileoptima.model.TimeInObj;
import com.mobileoptima.model.TimeOutObj;
import com.mobileoptima.service.MainService;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

public class SummaryFragment extends Fragment implements OnClickListener, OnBackPressedCallback,
		OnFragmentCallback, OnSignCallback {

	private CodePanButton btnBackSummary, btnSignatureSummary, btnCancelSummary,
			btnTimeOutSummary, btnEditSummary;
	private CodePanLabel tvTimeInSummary, tvTimeOutSummary, tvWorkHoursSummary,
			tvBreakSummary, tvNetSummary;
	private FrameLayout flSignatureSummary, flEditSummary;
	private OnOverrideCallback overrideCallback;
	private FragmentTransaction transaction;
	private ImageView ivSignatureSummary;
	private LinearLayout llFooterSummary;
	private boolean isTimeOut, result;
	private AttendanceObj attendance;
	private FragmentManager manager;
	private String photo, signature;
	private SQLiteAdapter db;
	private GpsObj gps;

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
		View view = inflater.inflate(R.layout.summary_layout, container, false);
		ivSignatureSummary = (ImageView) view.findViewById(R.id.ivSignatureSummary);
		tvTimeInSummary = (CodePanLabel) view.findViewById(R.id.tvTimeInSummary);
		tvTimeOutSummary = (CodePanLabel) view.findViewById(R.id.tvTimeOutSummary);
		tvWorkHoursSummary = (CodePanLabel) view.findViewById(R.id.tvWorkHoursSummary);
		tvBreakSummary = (CodePanLabel) view.findViewById(R.id.tvBreakSummary);
		tvNetSummary = (CodePanLabel) view.findViewById(R.id.tvNetSummary);
		btnBackSummary = (CodePanButton) view.findViewById(R.id.btnBackSummary);
		btnCancelSummary = (CodePanButton) view.findViewById(R.id.btnCancelSummary);
		btnTimeOutSummary = (CodePanButton) view.findViewById(R.id.btnTimeOutSummary);
		btnSignatureSummary = (CodePanButton) view.findViewById(R.id.btnSignatureSummary);
		btnEditSummary = (CodePanButton) view.findViewById(R.id.btnEditSummary);
		llFooterSummary = (LinearLayout) view.findViewById(R.id.llFooterSummary);
		flSignatureSummary = (FrameLayout) view.findViewById(R.id.flSignatureSummary);
		flEditSummary = (FrameLayout) view.findViewById(R.id.flEditSummary);
		btnEditSummary.setOnClickListener(this);
		btnBackSummary.setOnClickListener(this);
		btnCancelSummary.setOnClickListener(this);
		btnTimeOutSummary.setOnClickListener(this);
		btnSignatureSummary.setOnClickListener(this);
		if(!isTimeOut) {
			llFooterSummary.setVisibility(View.GONE);
			if(attendance != null && attendance.out != null) {
				TimeOutObj out = attendance.out;
				onSign(out.signature);
			}
		}
		computeSummary();
		return view;
	}

	public void computeSummary() {
		if(attendance != null) {
			TimeInObj in = attendance.in;
			TimeOutObj out = attendance.out;
			if(in != null && out != null) {
				long millisIn = CodePanUtils.dateTimeToMillis(in.dDate, in.dTime);
				long millisOut = CodePanUtils.dateTimeToMillis(out.dDate, out.dTime);
				long totalBreak = attendance.totalBreak;
				long millisWork = millisOut - millisIn;
				long millisNet = millisWork - totalBreak;
				String dateIn = CodePanUtils.getCalendarDate(in.dDate, false, true);
				String dateOut = CodePanUtils.getCalendarDate(out.dDate, false, true);
				String timeIn = CodePanUtils.getNormalTime(in.dTime, false);
				String timeOut = CodePanUtils.getNormalTime(out.dTime, false);
				String breakHours = CodePanUtils.millisToHours(totalBreak);
				String workHours = CodePanUtils.millisToHours(millisWork);
				String netHours = CodePanUtils.millisToHours(millisNet);
				tvTimeInSummary.setText(dateIn + ", " + timeIn);
				tvTimeOutSummary.setText(dateOut + ", " + timeOut);
				tvBreakSummary.setText(breakHours);
				tvWorkHoursSummary.setText(workHours);
				tvNetSummary.setText(netHours);
			}
		}
	}

	public void setAttendance(AttendanceObj attendance) {
		this.attendance = attendance;
	}

	public void setIsTimeOut(boolean isTimeOut) {
		this.isTimeOut = isTimeOut;
	}

	public void setImage(String photo) {
		this.photo = photo;
	}

	public void setGps(GpsObj gps) {
		this.gps = gps;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnBackSummary:
				manager.popBackStack();
				break;
			case R.id.btnSignatureSummary:
				SignatureFragment sign = new SignatureFragment();
				sign.setOnSignCallback(this);
				sign.setSignature(signature);
				sign.setAttendance(attendance);
				sign.setIsTimeOut(isTimeOut);
				transaction = manager.beginTransaction();
				transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
						R.anim.slide_in_ltr, R.anim.slide_out_ltr);
				transaction.add(R.id.rlMain, sign);
				transaction.hide(this);
				transaction.addToBackStack(null);
				transaction.commit();
				break;
			case R.id.btnCancelSummary:
				onBackPressed();
				break;
			case R.id.btnTimeOutSummary:
				if(attendance != null) {
					saveTimeOut();
				}
				break;
			case R.id.btnEditSummary:
				btnSignatureSummary.performClick();
				break;
		}
	}

	public void saveTimeOut() {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					TimeOutObj out = attendance.out;
					result = TarkieLib.saveTimeOut(db, out.dDate, out.dTime, photo, signature, gps);
					handler.sendMessage(handler.obtainMessage());
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

	Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message message) {
			CodePanUtils.alertToast(getActivity(), result ?
					"Time-out successful" : "Failed to save time-out.");
			manager.popBackStack(null, POP_BACK_STACK_INCLUSIVE);
			MainActivity main = (MainActivity) getActivity();
			main.checkTimeIn();
			main.updateSyncCount();
			MainService service = main.getService();
			service.syncData(db);
			return true;
		}
	});

	@Override
	public void onBackPressed() {
		final AlertDialogFragment alert = new AlertDialogFragment();
		alert.setOnFragmentCallback(this);
		alert.setDialogTitle("Cancel Time-out?");
		alert.setDialogMessage("Are you sure you want to cancel time-out?");
		alert.setPositiveButton("Yes", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(photo != null) {
					CodePanUtils.deleteFile(getActivity(), App.FOLDER, photo);
				}
				manager.popBackStack(null, POP_BACK_STACK_INCLUSIVE);
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

	public void setOnBackStack(boolean isOnBackStack) {
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

	@Override
	public void onSign(String signature) {
		if(signature != null) {
			this.signature = signature;
			Bitmap bitmap = CodePanUtils.getBitmapImage(getActivity(), App.FOLDER, signature);
			ivSignatureSummary.setImageBitmap(bitmap);
			flSignatureSummary.setVisibility(View.VISIBLE);
			btnSignatureSummary.setVisibility(View.GONE);
			if(!isTimeOut) {
				flEditSummary.setVisibility(View.GONE);
			}
		}
	}
}
