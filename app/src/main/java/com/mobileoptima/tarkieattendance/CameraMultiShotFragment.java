package com.mobileoptima.tarkieattendance;

import android.animation.LayoutTransition;
import android.animation.LayoutTransition.TransitionListener;
import android.content.Context;
import android.hardware.Camera;
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
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.callback.Interface.OnCameraErrorCallback;
import com.codepan.callback.Interface.OnCaptureCallback;
import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.camera.CameraSurfaceView;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.FocusIndicatorView;
import com.mobileoptima.callback.Interface.OnCameraDoneCallback;
import com.mobileoptima.callback.Interface.OnDeletePhotoCallback;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.constant.App;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.PhotoObj;

import java.util.ArrayList;

public class CameraMultiShotFragment extends Fragment implements OnClickListener, OnCaptureCallback,
		OnBackPressedCallback, OnFragmentCallback, OnDeletePhotoCallback,
		OnCameraErrorCallback {

	private final String flashMode = Camera.Parameters.FLASH_MODE_OFF;
	private final long TRANS_DELAY = 300;
	private final long FADE_DELAY = 750;

	private CodePanButton btnBackCameraMultiShot, btnOptionsCameraMultiShot, btnCaptureCameraMultiShot,
			btnDoneCameraMultiShot, btnSwitchCameraMultiShot, btnClearCameraMultiShot;
	private int maxWidth, maxHeight, cameraSelection, position;
	private RelativeLayout rlPhotoGridCameraMultiShot, rlOptionsCameraMultiShot;
	private LinearLayout llPhotoGridCameraMultiShot, llSwitchCameraMultiShot;
	private HorizontalScrollView hsvPhotoGridCameraMultiShot;
	private CodePanLabel tvPhotosTakenCameraMultiShot;
	private OnCameraDoneCallback cameraDoneCallback;
	private OnOverrideCallback overrideCallback;
	private OnFragmentCallback fragmentCallback;
	private FocusIndicatorView dvCameraMultiShot;
	private FragmentTransaction transaction;
	private FrameLayout flCameraMultiShot;
	private ArrayList<PhotoObj> photoList;
	private CameraSurfaceView surfaceView;
	private LayoutTransition transition;
	private FragmentManager manager;
	private boolean inOtherFragment;
	private View vCameraMultiShot;
	private SQLiteAdapter db;
	private String fileName;

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
		if(surfaceView != null && surfaceView.getCamera() == null) {
			resetCamera(0);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		photoList = new ArrayList<>();
		manager = getActivity().getSupportFragmentManager();
		maxWidth = CodePanUtils.getMaxWidth(getActivity());
		maxHeight = CodePanUtils.getMaxHeight(getActivity());
		MainActivity main = (MainActivity) getActivity();
		main.setOnBackPressedCallback(this);
		db = main.getDatabase();
		db.openConnection();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.camera_multi_shot_layout, container, false);
		rlOptionsCameraMultiShot = (RelativeLayout) view.findViewById(R.id.rlOptionsCameraMultiShot);
		rlPhotoGridCameraMultiShot = (RelativeLayout) view.findViewById(R.id.rlPhotoGridCameraMultiShot);
		hsvPhotoGridCameraMultiShot = (HorizontalScrollView) view.findViewById(R.id.hsvPhotoGridCameraMultiShot);
		llPhotoGridCameraMultiShot = (LinearLayout) view.findViewById(R.id.llPhotoGridCameraMultiShot);
		llSwitchCameraMultiShot = (LinearLayout) view.findViewById(R.id.llSwitchCameraMultiShot);
		tvPhotosTakenCameraMultiShot = (CodePanLabel) view.findViewById(R.id.tvPhotosTakenCameraMultiShot);
		btnDoneCameraMultiShot = (CodePanButton) view.findViewById(R.id.btnDoneCameraMultiShot);
		btnSwitchCameraMultiShot = (CodePanButton) view.findViewById(R.id.btnSwitchCameraMultiShot);
		btnClearCameraMultiShot = (CodePanButton) view.findViewById(R.id.btnClearCameraMultiShot);
		btnBackCameraMultiShot = (CodePanButton) view.findViewById(R.id.btnBackCameraMultiShot);
		btnOptionsCameraMultiShot = (CodePanButton) view.findViewById(R.id.btnOptionsCameraMultiShot);
		btnCaptureCameraMultiShot = (CodePanButton) view.findViewById(R.id.btnCaptureCameraMultiShot);
		dvCameraMultiShot = (FocusIndicatorView) view.findViewById(R.id.dvCameraMultiShot);
		flCameraMultiShot = (FrameLayout) view.findViewById(R.id.flCameraMultiShot);
		vCameraMultiShot = view.findViewById(R.id.vCameraMultiShot);
		btnBackCameraMultiShot.setOnClickListener(this);
		btnOptionsCameraMultiShot.setOnClickListener(this);
		btnCaptureCameraMultiShot.setOnClickListener(this);
		btnDoneCameraMultiShot.setOnClickListener(this);
		btnSwitchCameraMultiShot.setOnClickListener(this);
		btnClearCameraMultiShot.setOnClickListener(this);
		rlOptionsCameraMultiShot.setOnClickListener(this);
		transition = llPhotoGridCameraMultiShot.getLayoutTransition();
		transition.addTransitionListener(new TransitionListener() {
			@Override
			public void startTransition(LayoutTransition transition, ViewGroup container, View view,
										int transitionType) {
			}

			@Override
			public void endTransition(LayoutTransition transition, ViewGroup container, View view,
									  int transitionType) {
				hsvPhotoGridCameraMultiShot.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
			}
		});
		resetCamera(TRANS_DELAY);
		return view;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnBackCameraMultiShot:
				onBackPressed();
				break;
			case R.id.btnCaptureCameraMultiShot:
				if(surfaceView != null && !surfaceView.isCaptured()) {
					surfaceView.takePicture();
				}
				break;
			case R.id.btnOptionsCameraMultiShot:
				if(rlOptionsCameraMultiShot.getVisibility() == View.GONE) {
					CodePanUtils.fadeIn(rlOptionsCameraMultiShot);
				}
				else {
					CodePanUtils.fadeOut(rlOptionsCameraMultiShot);
				}
				break;
			case R.id.btnDoneCameraMultiShot:
				if(rlOptionsCameraMultiShot.getVisibility() == View.VISIBLE) {
					CodePanUtils.fadeOut(rlOptionsCameraMultiShot);
				}
				if(cameraDoneCallback != null) {
					cameraDoneCallback.onCameraDone(photoList);
				}
				getActivity().getSupportFragmentManager().popBackStack();
				break;
			case R.id.btnSwitchCameraMultiShot:
				if(rlOptionsCameraMultiShot.getVisibility() == View.VISIBLE) {
					CodePanUtils.fadeOut(rlOptionsCameraMultiShot);
				}
				if(cameraSelection == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					cameraSelection = Camera.CameraInfo.CAMERA_FACING_BACK;
				}
				else {
					cameraSelection = Camera.CameraInfo.CAMERA_FACING_FRONT;
				}
				resetCamera(0);
				break;
			case R.id.btnClearCameraMultiShot:
				rlOptionsCameraMultiShot.performClick();
				if(!photoList.isEmpty()) {
					final AlertDialogFragment alert = new AlertDialogFragment();
					alert.setOnFragmentCallback(this);
					alert.setDialogTitle("Delete Photos");
					alert.setDialogMessage("Are you sure you want to clear all taken photos?");
					alert.setPositiveButton("Yes", new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							alert.getDialogActivity().getSupportFragmentManager().popBackStack();
							clearPhotos(db, photoList);
							if(overrideCallback != null) {
								overrideCallback.onOverride(false);
							}
						}
					});
					alert.setNegativeButton("No", new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							alert.getDialogActivity().getSupportFragmentManager().popBackStack();
						}
					});
					transaction = getActivity().getSupportFragmentManager().beginTransaction();
					transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
							R.anim.fade_in, R.anim.fade_out);
					transaction.add(R.id.rlMain, alert);
					transaction.addToBackStack(null);
					transaction.commit();
				}
				else {
					CodePanUtils.alertToast(getActivity(), "No photos to be cleared.");
				}
				break;
			case R.id.rlOptionsCameraMultiShot:
				if(rlOptionsCameraMultiShot.getVisibility() == View.VISIBLE) {
					CodePanUtils.fadeOut(rlOptionsCameraMultiShot);
				}
				break;
		}
	}

	public void setOnFragmentCallback(OnFragmentCallback fragmentCallback) {
		this.fragmentCallback = fragmentCallback;
	}

	public void setOnBackStack(boolean isOnBackStack) {
		if(fragmentCallback != null) {
			fragmentCallback.onFragment(isOnBackStack);
		}
	}

	@Override
	public void onCapture(String fileName) {
		this.fileName = fileName;
		PhotoObj obj = new PhotoObj();
		obj.dDate = CodePanUtils.getDate();
		obj.dTime = CodePanUtils.getTime();
		obj.fileName = fileName;
		obj.ID = TarkieLib.savePhoto(db, obj);
		photoList.add(obj);
		if(surfaceView != null && surfaceView.isCaptured()) {
			surfaceView.reset();
		}
		if(overrideCallback != null) {
			overrideCallback.onOverride(true);
		}
		updatePhotoGrid(position);
		position++;
	}

	public void updatePhotoGrid(final int position) {
		View view = getView();
		if(view != null) {
			final String uri = "file://" + getActivity().getDir(App.FOLDER, Context.MODE_PRIVATE)
					.getPath() + "/" + fileName;
			ViewGroup container = (ViewGroup) view.getParent();
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View child = inflater.inflate(R.layout.photo_grid_item, container, false);
			CodePanButton btnPhotoGrid = (CodePanButton) child.findViewById(R.id.btnPhotoGrid);
			final ImageView ivPhotoGrid = (ImageView) child.findViewById(R.id.ivPhotoGrid);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					CodePanUtils.displayImage(ivPhotoGrid, uri, R.color.gray_ter);
				}
			}, 250);
			btnPhotoGrid.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onPhotoGridItemClick(position);
				}
			});
			if(position == 0) {
				llPhotoGridCameraMultiShot.removeAllViews();
				llPhotoGridCameraMultiShot.setLayoutTransition(null);
			}
			else {
				llPhotoGridCameraMultiShot.setLayoutTransition(transition);
			}
			llPhotoGridCameraMultiShot.addView(child);
			if(rlPhotoGridCameraMultiShot.getVisibility() == View.GONE) {
				CodePanUtils.expandView(rlPhotoGridCameraMultiShot, true);
			}
			String taken = String.valueOf(photoList.size());
			tvPhotosTakenCameraMultiShot.setText(taken);
		}
	}

	@Override
	public void onBackPressed() {
		if(!photoList.isEmpty() && !inOtherFragment) {
			final AlertDialogFragment alert = new AlertDialogFragment();
			alert.setOnFragmentCallback(this);
			alert.setDialogTitle(R.string.save_photos_title);
			alert.setDialogMessage(R.string.save_photos_message);
			alert.setPositiveButton("Yes", new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					alert.getDialogActivity().getSupportFragmentManager().popBackStack();
					getActivity().getSupportFragmentManager().popBackStack();
					if(cameraDoneCallback != null) {
						cameraDoneCallback.onCameraDone(photoList);
					}
					if(overrideCallback != null) {
						overrideCallback.onOverride(false);
					}
				}
			});
			alert.setNegativeButton("No", new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(!photoList.isEmpty()) {
						clearPhotos(db, photoList);
					}
					if(overrideCallback != null) {
						overrideCallback.onOverride(false);
					}
					alert.getDialogActivity().getSupportFragmentManager().popBackStack();
					getActivity().getSupportFragmentManager().popBackStack();
				}
			});
			transaction = getActivity().getSupportFragmentManager().beginTransaction();
			transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
					R.anim.fade_in, R.anim.fade_out);
			transaction.add(R.id.rlMain, alert);
			transaction.addToBackStack(null);
			transaction.commit();
		}
		else {
			getActivity().getSupportFragmentManager().popBackStack();
		}
	}

	@Override
	public void onDeletePhoto(int position) {
		photoList.remove(position);
		llPhotoGridCameraMultiShot.removeViewAt(position);
		this.position = photoList.size();
		if(photoList.size() == 0) {
			CodePanUtils.collapseView(rlPhotoGridCameraMultiShot, true);
		}
		else {
			invalidateViews();
		}
		String taken = String.valueOf(photoList.size());
		tvPhotosTakenCameraMultiShot.setText(taken);
	}

	public void invalidateViews() {
		int count = llPhotoGridCameraMultiShot.getChildCount();
		for(int i = 0; i < count; i++) {
			final int position = i;
			View view = llPhotoGridCameraMultiShot.getChildAt(i);
			CodePanButton btnPhotoGrid = (CodePanButton) view.findViewById(R.id.btnPhotoGrid);
			btnPhotoGrid.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onPhotoGridItemClick(position);
				}
			});
		}
	}

	public void clearPhotos(final SQLiteAdapter db, final ArrayList<PhotoObj> deleteList) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					boolean result = TarkieLib.deletePhotos(getActivity(), db, deleteList);
					if(result) {
						photoList.clear();
						clearPhotosHandler.sendMessage(clearPhotosHandler.obtainMessage());
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	Handler clearPhotosHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			CodePanUtils.alertToast(getActivity(), "Photos cleared.");
			llPhotoGridCameraMultiShot.removeAllViews();
			CodePanUtils.collapseView(rlPhotoGridCameraMultiShot, true);
			tvPhotosTakenCameraMultiShot.setText("0");
			position = 0;
			return true;
		}
	});

	public void onPhotoGridItemClick(int position) {
		ImagePreviewFragment imagePreview = new ImagePreviewFragment();
		imagePreview.setPhotoList(photoList, position);
		imagePreview.setOnDeletePhotoCallback(this);
		imagePreview.setOnFragmentCallback(this);
		transaction = getActivity().getSupportFragmentManager().beginTransaction();
		transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
				R.anim.slide_in_ltr, R.anim.slide_out_ltr);
		transaction.add(R.id.rlMain, imagePreview);
		transaction.hide(this);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public void onFragment(boolean status) {
		this.inOtherFragment = status;
		if(!status) {
			MainActivity main = (MainActivity) getActivity();
			main.setOnBackPressedCallback(this);
			if(!photoList.isEmpty()) {
				if(overrideCallback != null) {
					overrideCallback.onOverride(true);
				}
			}
		}
	}

	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}

	public void setOnCameraDoneCallback(OnCameraDoneCallback cameraDoneCallback) {
		this.cameraDoneCallback = cameraDoneCallback;
	}

	public void resetCamera(long delay) {
		if(vCameraMultiShot != null) vCameraMultiShot.setVisibility(View.VISIBLE);
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(surfaceView != null) surfaceView.stopCamera();
				surfaceView = new CameraSurfaceView(getActivity(), CameraMultiShotFragment.this,
						cameraSelection, flashMode, App.FOLDER, maxWidth, maxHeight);
				surfaceView.setOnCaptureCallback(CameraMultiShotFragment.this);
				surfaceView.setFocusIndicatorView(dvCameraMultiShot);
				surfaceView.fullScreenToContainer(flCameraMultiShot);
				if(flCameraMultiShot.getChildCount() > 1) {
					flCameraMultiShot.removeViewAt(0);
				}
				flCameraMultiShot.addView(surfaceView, 0);
				CodePanUtils.fadeOut(vCameraMultiShot, FADE_DELAY);
				if(surfaceView.getNoOfCamera() == 1) {
					llSwitchCameraMultiShot.setVisibility(View.GONE);
				}
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
}
