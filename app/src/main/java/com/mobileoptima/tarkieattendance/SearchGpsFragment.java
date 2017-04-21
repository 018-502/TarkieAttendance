package com.mobileoptima.tarkieattendance;

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

import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.model.GpsObj;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.callback.Interface.OnCountdownFinishCallback;
import com.mobileoptima.callback.Interface.OnGpsFixedCallback;

public class SearchGpsFragment extends Fragment {

	private boolean runThread, isGpsFixed, isPause, withCounter, isPending;
	private OnCountdownFinishCallback countdownFinishCallback;
	private OnGpsFixedCallback gpsFixedCallback;
	private OnFragmentCallback fragmentCallback;
	private CodePanLabel tvCountdownSearchGps;
	private FragmentTransaction transaction;
	private ImageView ivLoadingSearchGps;
	private FragmentManager manager;
	private Animation anim;
	private int countdown;
	private GpsObj gps;
	private Thread bg;

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
	public void onResume() {
		super.onResume();
		if(isPause && isPending) {
			isPending = false;
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		manager = getActivity().getSupportFragmentManager();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.search_gps_layout, container, false);
		ivLoadingSearchGps = (ImageView) view.findViewById(R.id.ivLoadingSearchGps);
		tvCountdownSearchGps = (CodePanLabel) view.findViewById(R.id.tvCountdownSearchGps);
		anim = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_clockwise);
		startSearch();
		return view;
	}

	public void startSearch() {
		ivLoadingSearchGps.startAnimation(anim);
		runThread = true;
		isGpsFixed = false;
		bg = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					while(runThread) {
						GpsObj obj = ((MainActivity) getActivity()).getGps();
						if(obj.isValid) {
							isGpsFixed = true;
							gps = obj;
						}
						handler.sendMessage(handler.obtainMessage());
						Thread.sleep(1000);
						countdown = countdown != 0 ? countdown - 1 : countdown;
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
				checkGpsStatus();
			}
			return true;
		}
	});

	public void checkGpsStatus() {
		if(CodePanUtils.isGpsEnabled(getActivity())) {
			if(isGpsFixed) {
				stopSearch();
				if(!isPause) {
					showResult();
				}
				else {
					isPending = true;
				}
			}
			else {
				if(withCounter) {
					String timer = String.valueOf(countdown);
					tvCountdownSearchGps.setText(timer);
					if(countdown == 0) {
						if(!isPause) {
							showResult();
						}
						else {
							isPending = true;
						}
					}
				}
			}
		}
		else {
			if(!isPause) {
				manager.popBackStack();
			}
		}
	}

	public void showResult() {
		manager.popBackStack();
		if(isGpsFixed) {
			final AlertDialogFragment alert = new AlertDialogFragment();
			alert.setDialogTitle(R.string.gps_acquired_title);
			alert.setDialogMessage(R.string.gps_acquired_message);
			alert.setPositiveButton("Yes", new OnClickListener() {
				@Override
				public void onClick(View v) {
					manager.popBackStack();
					if(isGpsFixed && gpsFixedCallback != null) {
						gpsFixedCallback.onGpsFixed(gps);
					}
				}
			});
			alert.setNegativeButton("Cancel", new OnClickListener() {
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
			if(countdownFinishCallback != null) {
				countdownFinishCallback.onCountdownFinish();
			}
		}
	}

	public void setOnGpsFixedCallback(OnGpsFixedCallback gpsFixedCallback) {
		this.gpsFixedCallback = gpsFixedCallback;
	}

	public void setOnCountdownFinishCallback(OnCountdownFinishCallback countdownFinishCallback) {
		this.countdownFinishCallback = countdownFinishCallback;
	}

	public void setOnFragmentCallback(OnFragmentCallback fragmentCallback) {
		this.fragmentCallback = fragmentCallback;
	}

	public void setCountdown(int countdown) {
		this.countdown = countdown;
		this.withCounter = true;
	}

	public void stopSearch() {
		ivLoadingSearchGps.clearAnimation();
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

	public void setOnBackStack(boolean isOnBackStack) {
		if(fragmentCallback != null) {
			fragmentCallback.onFragment(isOnBackStack);
		}
	}
}
