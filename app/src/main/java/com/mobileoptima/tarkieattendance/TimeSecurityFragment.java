package com.mobileoptima.tarkieattendance;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.codepan.database.SQLiteAdapter;
import com.codepan.model.TimeObj;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.callback.Interface.OnTimeValidatedCallback;
import com.mobileoptima.constant.Module;
import com.mobileoptima.core.TimeSecurity;

public class TimeSecurityFragment extends Fragment implements OnClickListener, OnTimeValidatedCallback {

	private CodePanLabel tvDateTimeSecurity, tvTimeTimeSecurity;
	private OnTimeValidatedCallback timeValidationCallback;
	private FragmentTransaction transaction;
	private FragmentManager manager;
	private SQLiteAdapter db;

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
		View view = inflater.inflate(R.layout.time_security_layout, container, false);
		tvDateTimeSecurity = (CodePanLabel) view.findViewById(R.id.tvDateTimeSecurity);
		tvTimeTimeSecurity = (CodePanLabel) view.findViewById(R.id.tvTimeTimeSecurity);
		view.findViewById(R.id.btnCancelTimeSecurity).setOnClickListener(this);
		view.findViewById(R.id.btnSettingsTimeSecurity).setOnClickListener(this);
		view.findViewById(R.id.tvValidateTimeSecurity).setOnClickListener(this);
		setTime();
		updateTime();
		return view;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnCancelTimeSecurity:
				getActivity().finish();
				break;
			case R.id.btnSettingsTimeSecurity:
				manager.popBackStack();
				Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);
				startActivity(intent);
				break;
			case R.id.tvValidateTimeSecurity:
				if(CodePanUtils.hasInternet(getActivity())) {
					LoadingDialogFragment loading = new LoadingDialogFragment();
					loading.setAction(Module.Action.VALIDATE_SERVER_TIME);
					loading.setOnTimeValidatedCallback(timeValidationCallback);
					transaction = manager.beginTransaction();
					transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
							R.anim.fade_in, R.anim.fade_out);
					transaction.add(R.id.rlMain, loading);
					transaction.addToBackStack(null);
					transaction.commit();
				}
				else {
					if(((MainActivity) getActivity()).isGpsSecured()) {
						SearchGpsTimeFragment search = new SearchGpsTimeFragment();
						search.setOnTimeValidatedCallback(timeValidationCallback);
						transaction = manager.beginTransaction();
						transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
								R.anim.fade_in, R.anim.fade_out);
						transaction.add(R.id.rlMain, search);
						transaction.addToBackStack(null);
						transaction.commit();
					}
				}
				break;
		}
	}

	public void updateTime() {
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setTime();
				handler.postDelayed(this, 1000);
			}
		}, 1000);
	}

	public void setTime() {
		TimeObj obj = TimeSecurity.getServerTime(db);
		String date = CodePanUtils.getCalendarDate(obj.date, false, true);
		String time = CodePanUtils.getNormalTime(obj.time, true);
		tvDateTimeSecurity.setText(date);
		tvTimeTimeSecurity.setText(time);
	}

	public void setOnTimeValidatedCallback(OnTimeValidatedCallback timeValidationCallback) {
		this.timeValidationCallback = timeValidationCallback;
	}

	@Override
	public void onTimeValidated() {
		if(timeValidationCallback != null) {
			timeValidationCallback.onTimeValidated();
		}
	}
}
