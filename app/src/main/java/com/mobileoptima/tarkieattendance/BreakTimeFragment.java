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
import com.codepan.utils.BlurBuilder;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.BreakInObj;
import com.mobileoptima.model.BreakObj;

public class BreakTimeFragment extends Fragment implements OnClickListener,
		OnBackPressedCallback, OnFragmentCallback {

	private CodePanLabel tvTitleBreakTime, tvDurationBreakTime, tvCurrentBreakTime;
	private OnOverrideCallback overrideCallback;
	private FragmentTransaction transaction;
	private CodePanButton btnDoneBreakTime;
	private FragmentManager manager;
	private ImageView ivBreakTime;
	private SQLiteAdapter db;
	private BreakInObj in;
	private BreakObj obj;

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
		in = TarkieLib.getBreakIn(db);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.break_time_layout, container, false);
		ivBreakTime = (ImageView) view.findViewById(R.id.ivBreakTime);
		btnDoneBreakTime = (CodePanButton) view.findViewById(R.id.btnDoneBreakTime);
		tvTitleBreakTime = (CodePanLabel) view.findViewById(R.id.tvTitleBreakTime);
		tvDurationBreakTime = (CodePanLabel) view.findViewById(R.id.tvDurationBreakTime);
		tvCurrentBreakTime = (CodePanLabel) view.findViewById(R.id.tvCurrentBreakTime);
		btnDoneBreakTime.setOnClickListener(this);
		if(obj != null) {
			String mins = obj.duration > 1 ? obj.duration + " mins" : "1 min";
			String status = "You are currently on\n" + obj.name + ". (" + mins + ")";
			tvTitleBreakTime.setText(obj.name);
			tvDurationBreakTime.setText(status);
		}
		setBackground();
		updateTimeLeft();
		startTimer();
		return view;
	}

	public void startTimer() {
		final long delay = 1000L;
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				updateTimeLeft();
				handler.postDelayed(this, delay);
			}
		}, delay);
	}

	public void updateTimeLeft() {
		long timeLeft = getTimeLeft();
		timeLeft = timeLeft < 0 ? 0 : timeLeft;
		String time = CodePanUtils.millisToHours(timeLeft) + " left";
		if(tvCurrentBreakTime != null) {
			tvCurrentBreakTime.setText(time);
		}
	}

	public void setBackground() {
		View view = ((MainActivity) getActivity()).getMainParent();
		Bitmap bitmap = BlurBuilder.blur(view);
		if(bitmap != null) {
			ivBreakTime.setImageBitmap(bitmap);
		}
	}

	public long getTimeLeft() {
		long timeLeft = 0L;
		if(in != null && obj != null) {
			long currentTime = System.currentTimeMillis();
			long breakIn = CodePanUtils.dateTimeToMillis(in.dDate, in.dTime);
			long duration = obj.duration * 60 * 1000;
			long actual = currentTime - breakIn;
			actual = actual < 0 ? actual + 86400 : actual;
			timeLeft = duration - actual;
		}
		return timeLeft;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnDoneBreakTime:
				final AlertDialogFragment alert = new AlertDialogFragment();
				alert.setDialogTitle("End Break");
				alert.setDialogMessage("Do you really want to end your break?");
				alert.setOnFragmentCallback(this);
				alert.setPositiveButton("Yes", new OnClickListener() {
					@Override
					public void onClick(View v) {
						manager.popBackStack();
						GpsObj gps = ((MainActivity) getActivity()).getGps();
						saveBreakOut(db, gps, in);
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
				break;
		}
	}

	public void saveBreakOut(final SQLiteAdapter db, final GpsObj gps, final BreakInObj in) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					TarkieLib.saveBreakOut(db, gps, in);
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
			manager.popBackStack();
			((MainActivity) getActivity()).updateSyncCount();
			return true;
		}
	});

	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}

	public void setBreak(BreakObj obj) {
		this.obj = obj;
	}

	public void setOnBackStack(boolean isOnBackStack) {
		if(overrideCallback != null) {
			overrideCallback.onOverride(isOnBackStack);
		}
	}

	@Override
	public void onBackPressed() {
		getActivity().finish();
	}

	@Override
	public void onFragment(boolean status) {
		if(overrideCallback != null) {
			overrideCallback.onOverride(!status);
		}
	}
}
