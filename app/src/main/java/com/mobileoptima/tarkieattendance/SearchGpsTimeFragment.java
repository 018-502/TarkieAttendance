package com.mobileoptima.tarkieattendance;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.mobileoptima.callback.Interface.OnTimeValidatedCallback;
import com.mobileoptima.core.TimeSecurity;

public class SearchGpsTimeFragment extends Fragment implements OnClickListener, LocationListener {

	private OnTimeValidatedCallback timeValidationCallback;
	private boolean runThread, isGpsFixed, isPause;
	private CodePanButton btnCancelSearchGpsTime;
	private ImageView ivLoadingSearchGpsTime;
	private FragmentTransaction transaction;
	private long lastLocationUpdate;
	private FragmentManager manager;
	private LocationManager lm;
	private SQLiteAdapter db;
	private Animation anim;
	private Thread bg;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivity main = (MainActivity) getActivity();
		lm = (LocationManager) main.getSystemService(Context.LOCATION_SERVICE);
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
		db.openConnection();
	}

	@Override
	public void onStart() {
		super.onStart();
		if(lm != null) {
			try {
				lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
			}
			catch(SecurityException se) {
				se.printStackTrace();
			}
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if(lm != null) {
			try {
				lm.removeUpdates(this);
			}
			catch(SecurityException se) {
				se.printStackTrace();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if(isPause && isGpsFixed) {
			showResult();
		}
		isPause = false;
	}

	@Override
	public void onPause() {
		super.onPause();
		isPause = true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.search_gps_time_layout, container, false);
		ivLoadingSearchGpsTime = (ImageView) view.findViewById(R.id.ivLoadingSearchGpsTime);
		btnCancelSearchGpsTime = (CodePanButton) view.findViewById(R.id.btnCancelSearchGpsTime);
		anim = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_clockwise);
		btnCancelSearchGpsTime.setOnClickListener(this);
		startSearch();
		return view;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.btnCancelSearchGpsTime:
				stopSearch();
				ivLoadingSearchGpsTime.clearAnimation();
				manager.popBackStack();
				break;
		}
	}

	public void startSearch() {
		ivLoadingSearchGpsTime.startAnimation(anim);
		isGpsFixed = false;
		runThread = true;
		bg = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					while(runThread) {
						Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						if(location != null) {
							if(location.getTime() != lastLocationUpdate) {
								isGpsFixed = true;
								TimeSecurity.updateServerTime(db, location.getTime());
							}
							lastLocationUpdate = location.getTime();
						}
						handler.sendMessage(handler.obtainMessage());
						Thread.sleep(1000);
					}
				}
				catch(SecurityException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			if(runThread) {
				checkGPSStatus();
			}
			return true;
		}
	});

	public void checkGPSStatus() {
		if(CodePanUtils.isGpsEnabled(getActivity())) {
			if(isGpsFixed) {
				stopSearch();
				ivLoadingSearchGpsTime.clearAnimation();
				if(!isPause) {
					showResult();
				}
			}
		}
		else {
			if(!isPause) {
				manager.popBackStack();
			}
		}
	}

	public void stopSearch() {
		runThread = false;
		if(bg.isAlive()) {
			bg.interrupt();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopSearch();
	}

	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	public void showResult() {
		manager.popBackStack();
		final AlertDialogFragment alert = new AlertDialogFragment();
		alert.setDialogTitle("Validation Success");
		alert.setDialogMessage("Tarkie time has been successfully updated");
		alert.setPositiveButton("Ok", new OnClickListener() {
			@Override
			public void onClick(View v) {
				manager.popBackStack();
				if(timeValidationCallback != null) {
					timeValidationCallback.onTimeValidated();
				}
			}
		});
		transaction = manager.beginTransaction();
		transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
				R.anim.fade_in, R.anim.fade_out);
		transaction.add(R.id.rlMain, alert);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void setOnTimeValidatedCallback(OnTimeValidatedCallback timeValidationCallback) {
		this.timeValidationCallback = timeValidationCallback;
	}
}
