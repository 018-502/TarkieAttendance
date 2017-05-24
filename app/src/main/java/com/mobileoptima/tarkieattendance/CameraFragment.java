package com.mobileoptima.tarkieattendance;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.codepan.callback.Interface.OnCameraErrorCallback;
import com.codepan.callback.Interface.OnCaptureCallback;
import com.codepan.camera.CameraSurfaceView;
import com.codepan.model.GpsObj;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.FocusIndicatorView;
import com.mobileoptima.callback.Interface.OnCheckInCallback;
import com.mobileoptima.callback.Interface.OnCheckOutCallback;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.callback.Interface.OnRetakeCameraCallback;
import com.mobileoptima.callback.Interface.OnTimeInCallback;
import com.mobileoptima.callback.Interface.OnTimeOutCallback;
import com.mobileoptima.constant.App;
import com.mobileoptima.constant.Tag;
import com.mobileoptima.model.StoreObj;
import com.mobileoptima.model.TaskObj;

public class CameraFragment extends Fragment implements OnClickListener, OnCaptureCallback,
		OnRetakeCameraCallback, OnCameraErrorCallback {

	private final String flashMode = Camera.Parameters.FLASH_MODE_OFF;
	private final long TRANS_DELAY = 300;
	private final long FADE_DELAY = 750;

	private CodePanButton btnCaptureCamera, btnSwitchCamera, btnBackCamera;
	private int cameraSelection, maxWidth, maxHeight;
	private OnOverrideCallback overrideCallback;
	private OnCheckOutCallback checkOutCallback;
	private OnCheckInCallback checkInCallback;
	private OnTimeOutCallback timeOutCallback;
	private OnTimeInCallback timeInCallback;
	private FragmentTransaction transaction;
	private CameraSurfaceView surfaceView;
	private FocusIndicatorView dvCamera;
	private FragmentManager manager;
	private FrameLayout flCamera;
	private String date, time;
	private StoreObj store;
	private TaskObj task;
	private View vCamera;
	private GpsObj gps;
	private int type;

	@Override
	public void onResume() {
		super.onResume();
		if(surfaceView != null && surfaceView.getCamera() == null) {
			resetCamera(0);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		maxWidth = CodePanUtils.getMaxWidth(getActivity());
		maxHeight = CodePanUtils.getMaxHeight(getActivity());
		manager = getActivity().getSupportFragmentManager();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.camera_layout, container, false);
		dvCamera = (FocusIndicatorView) view.findViewById(R.id.dvCamera);
		flCamera = (FrameLayout) view.findViewById(R.id.flCamera);
		btnCaptureCamera = (CodePanButton) view.findViewById(R.id.btnCaptureCamera);
		btnSwitchCamera = (CodePanButton) view.findViewById(R.id.btnSwitchCamera);
		btnBackCamera = (CodePanButton) view.findViewById(R.id.btnBackCamera);
		vCamera = view.findViewById(R.id.vCamera);
		btnCaptureCamera.setOnClickListener(this);
		btnSwitchCamera.setOnClickListener(this);
		btnBackCamera.setOnClickListener(this);
		resetCamera(TRANS_DELAY);
		return view;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnCaptureCamera:
				if(surfaceView != null && !surfaceView.isCaptured()) {
					surfaceView.takePicture();
				}
				break;
			case R.id.btnSwitchCamera:
				if(cameraSelection == CameraInfo.CAMERA_FACING_FRONT) {
					cameraSelection = CameraInfo.CAMERA_FACING_BACK;
				}
				else {
					cameraSelection = CameraInfo.CAMERA_FACING_FRONT;
				}
				resetCamera(0);
				break;
			case R.id.btnBackCamera:
				manager.popBackStack();
				break;
		}
	}

	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}

	@Override
	public void onCapture(String fileName) {
		CapturedFragment captured = new CapturedFragment();
		captured.setGps(gps);
		captured.setDate(date);
		captured.setTime(time);
		captured.setTask(task);
		captured.setStore(store);
		captured.setImageType(type);
		captured.setOnTimeInCallback(timeInCallback);
		captured.setOnTimeOutCallback(timeOutCallback);
		captured.setOnCheckInCallback(checkInCallback);
		captured.setOnOverrideCallback(overrideCallback);
		captured.setOnRetakeCameraCallback(this);
		captured.setImage(fileName);
		transaction = manager.beginTransaction();
		transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
				R.anim.slide_in_ltr, R.anim.slide_out_ltr);
		transaction.add(R.id.rlMain, captured, Tag.CAPTURED);
		transaction.hide(this);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public void onRetakeCamera() {
		resetCamera(TRANS_DELAY);
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

	public void setTask(TaskObj task) {
		this.task = task;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void resetCamera(long delay) {
		if(vCamera != null) vCamera.setVisibility(View.VISIBLE);
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(surfaceView != null) surfaceView.stopCamera();
				surfaceView = new CameraSurfaceView(getActivity(), CameraFragment.this,
						cameraSelection, flashMode, App.FOLDER, maxWidth, maxHeight);
				surfaceView.setOnCaptureCallback(CameraFragment.this);
				surfaceView.setFocusIndicatorView(dvCamera);
				surfaceView.fullScreenToContainer(flCamera);
				if(flCamera.getChildCount() > 1) {
					flCamera.removeViewAt(0);
				}
				flCamera.addView(surfaceView, 0);
				CodePanUtils.fadeOut(vCamera, FADE_DELAY);
			}
		}, delay);
	}

	@Override
	public void onCameraError() {
		final AlertDialogFragment alert = new AlertDialogFragment();
		alert.setDialogTitle(R.string.camera_error_title);
		alert.setDialogMessage(R.string.camera_error_message);
		alert.setPositiveButton("OK", new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				manager.popBackStack();
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

	public void setOnTimeInCallback(OnTimeInCallback timeInCallback) {
		this.timeInCallback = timeInCallback;
	}

	public void setOnTimeOutCallback(OnTimeOutCallback timeOutCallback) {
		this.timeOutCallback = timeOutCallback;
	}

	public void setOnCheckInCallback(OnCheckInCallback checkInCallback) {
		this.checkInCallback = checkInCallback;
	}

	public void setOnCheckOutCallback(OnCheckOutCallback checkOutCallback) {
		this.checkOutCallback = checkOutCallback;
	}
}
