package com.mobileoptima.tarkieattendance;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.codepan.callback.Interface.OnRefreshCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.CodePanTextField;
import com.mobileoptima.callback.Interface;
import com.mobileoptima.callback.Interface.OnCameraDoneCallback;
import com.mobileoptima.callback.Interface.OnCheckInCallback;
import com.mobileoptima.callback.Interface.OnCheckOutCallback;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.callback.Interface.OnSelectStatusCallback;
import com.mobileoptima.constant.App;
import com.mobileoptima.constant.ImageType;
import com.mobileoptima.constant.Result;
import com.mobileoptima.constant.Settings;
import com.mobileoptima.core.Data;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.CheckInObj;
import com.mobileoptima.model.CheckOutObj;
import com.mobileoptima.model.FormObj;
import com.mobileoptima.model.ImageObj;
import com.mobileoptima.model.StoreObj;
import com.mobileoptima.model.TaskObj;
import com.mobileoptima.model.TaskStatusObj;
import com.mobileoptima.model.VisitObj;

import java.util.ArrayList;

public class VisitDetailsFragment extends Fragment implements OnClickListener,
		OnCheckInCallback, OnCheckOutCallback, OnSelectStatusCallback, OnCameraDoneCallback {

	private CodePanButton btnCheckInVisitDetails, btnCheckOutVisitDetails, btnBackVisitDetails,
			btnSaveVisitDetails, btnAddPhotoVisitDetails;
	private LinearLayout llFormsVisitDetails, llGridPhotoVisitDetails;
	private CodePanLabel tvStoreVisitDetails, tvAddressVisitDetails;
	private CodePanTextField etNotesVisitDetails;
	private OnOverrideCallback overrideCallback;
	private OnRefreshCallback refreshCallback;
	private FragmentTransaction transaction;
	private ArrayList<ImageObj> imageList;
	private ArrayList<FormObj> formList;
	private FragmentManager manager;
	private MainActivity main;
	private CheckOutObj out;
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
		llGridPhotoVisitDetails = (LinearLayout) view.findViewById(R.id.llGridPhotoVisitDetails);
		btnBackVisitDetails = (CodePanButton) view.findViewById(R.id.btnBackVisitDetails);
		btnCheckInVisitDetails = (CodePanButton) view.findViewById(R.id.btnCheckInVisitDetails);
		btnCheckOutVisitDetails = (CodePanButton) view.findViewById(R.id.btnCheckOutVisitDetails);
		btnAddPhotoVisitDetails = (CodePanButton) view.findViewById(R.id.btnAddPhotoVisitDetails);
		btnSaveVisitDetails = (CodePanButton) view.findViewById(R.id.btnSaveVisitDetails);
		btnSaveVisitDetails.setOnClickListener(this);
		btnBackVisitDetails.setOnClickListener(this);
		btnCheckInVisitDetails.setOnClickListener(this);
		btnCheckOutVisitDetails.setOnClickListener(this);
		btnAddPhotoVisitDetails.setOnClickListener(this);
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
			etNotesVisitDetails.addTextChangedListener(new TextWatcher() {
				String lastText = null;

				@Override
				public void beforeTextChanged(CharSequence cs, int start, int count, int after) {
					lastText = cs.toString();
				}

				@Override
				public void onTextChanged(CharSequence cs, int start, int before, int count) {
					if(visit.isFromWeb && visit.notes != null && !visit.notes.isEmpty()) {
						String text = cs.toString();
						String originalText = visit.notes.substring(0, visit.notesLimit);
						if(text.length() < visit.notesLimit) {
							etNotesVisitDetails.setText(visit.notes.substring(0, visit.notesLimit));
							etNotesVisitDetails.setSelection(visit.notesLimit);
						}
						else {
							if(!text.startsWith(originalText)) {
								etNotesVisitDetails.setText(lastText);
								etNotesVisitDetails.setSelection(visit.notesLimit);
							}
						}
					}
				}

				@Override
				public void afterTextChanged(Editable e) {
				}
			});
			loadForms(db, visit.ID);
			loadPhotos(db, visit.ID);
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

	Handler formsHandler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message message) {
			llFormsVisitDetails.removeAllViews();
			View view = getView();
			if(view != null) {
				LayoutInflater inflater = getActivity().getLayoutInflater();
				ViewGroup container = (ViewGroup) view.getParent();
				for(FormObj form : formList) {
					View child = inflater.inflate(R.layout.visit_details_form_item, container, false);
					CodePanLabel tvVisitDetailsForm = (CodePanLabel) child.findViewById(R.id.tvVisitDetailsForm);
					CodePanButton btnVisitDetailsForm = (CodePanButton) child.findViewById(R.id.btnVisitDetailsForm);
					tvVisitDetailsForm.setText(form.name);
					btnVisitDetailsForm.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
						}
					});
					llFormsVisitDetails.addView(child);
				}
			}
			return true;
		}
	});

	public void loadPhotos(final SQLiteAdapter db, final String taskID) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					imageList = Data.loadImages(db, taskID);
					photosHandler.sendMessage(photosHandler.obtainMessage());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	Handler photosHandler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message message) {
			updatePhotoGrid(llGridPhotoVisitDetails, imageList);
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
		btnCheckInVisitDetails.setBackgroundResource(R.drawable.state_rect_green_pri_gray_qui);
		btnCheckOutVisitDetails.setBackgroundResource(R.drawable.state_rect_green_pri_gray_oct);
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
							transaction.hide(this);
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
						transaction.hide(this);
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
			case R.id.btnAddPhotoVisitDetails:
				CameraMultiShotFragment camera = new CameraMultiShotFragment();
				camera.setOnOverrideCallback(overrideCallback);
				camera.setOnCameraDoneCallback(this);
				transaction = manager.beginTransaction();
				transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
						R.anim.slide_in_ltr, R.anim.slide_out_ltr);
				transaction.add(R.id.rlMain, camera);
				transaction.hide(this);
				transaction.addToBackStack(null);
				transaction.commit();
				break;
			case R.id.btnSaveVisitDetails:
				String notes = etNotesVisitDetails.getText().toString().trim();
				saveTask(db, notes);
				break;
		}
	}

	@Override
	public void onCheckIn(final CheckInObj in) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					TaskObj task = in.task;
					boolean result = TarkieLib.saveCheckIn(db, in.gps, task.ID, in.dDate,
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

	Handler checkInHandler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what) {
				case Result.SUCCESS:
					CheckInObj in = (CheckInObj) msg.obj;
					visit.in = in;
					visit.isCheckIn = true;
					setCheckInTime(in.dTime);
					btnCheckOutVisitDetails.setEnabled(true);
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
		this.out = out;
		VisitStatusFragment status = new VisitStatusFragment();
		status.setStore(visit.store);
		status.setHasNotes(hasNotes());
		status.setOnSelectStatusCallback(this);
		transaction = manager.beginTransaction();
		transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
				R.anim.fade_in, R.anim.fade_out);
		transaction.add(R.id.rlMain, status);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public boolean hasNotes() {
		if(visit.notes != null) {
			int length = visit.notes.length();
			return length > visit.notesLimit;
		}
		return false;
	}

	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}

	public void setOnRefreshCallback(OnRefreshCallback refreshCallback) {
		this.refreshCallback = refreshCallback;
	}

	@Override
	public void onSelectStatus(final TaskStatusObj status) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					boolean result = TarkieLib.saveCheckOut(db, out.gps, out.checkIn, out.dDate,
							out.dTime, out.photo, status.code, status.notes);
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

	Handler checkOutHandler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what) {
				case Result.SUCCESS:
					CheckOutObj out = (CheckOutObj) msg.obj;
					visit.out = out;
					visit.isCheckOut = true;
					setCheckOutTime(out.dTime);
					if(refreshCallback != null) {
						refreshCallback.onRefresh();
					}
					etNotesVisitDetails.setText(visit.notes);
					CodePanUtils.alertToast(getActivity(), "Check-out successful");
					break;
				case Result.FAILED:
					CodePanUtils.alertToast(getActivity(), "Failed to save check-out.");
					break;
			}
			return true;
		}
	});

	public void updatePhotoGrid(final LinearLayout llGridPhoto, final ArrayList<ImageObj> imageList) {
		View view = getView();
		if(view != null) {
			LayoutInflater inflater = getActivity().getLayoutInflater();
			ViewGroup container = (ViewGroup) view.getParent();
			llGridPhoto.removeAllViews();
			for(final ImageObj obj : imageList) {
				View child = inflater.inflate(R.layout.photo_item, container, false);
				CodePanButton btnPhoto = (CodePanButton) child.findViewById(R.id.btnPhoto);
				ImageView ivPhoto = (ImageView) child.findViewById(R.id.ivPhoto);
				int size = CodePanUtils.getWidth(child);
				Bitmap bitmap = CodePanUtils.getBitmapThumbnails(getActivity(), App.FOLDER, obj.fileName, size);
				ivPhoto.setImageBitmap(bitmap);
				obj.bitmap = bitmap;
				btnPhoto.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						int position = imageList.indexOf(obj);
						onPhotoGridItemClick(llGridPhoto, imageList, position);
					}
				});
				llGridPhoto.addView(child);
			}
			ViewParent parent = llGridPhoto.getParent();
			final HorizontalScrollView hsvPhoto = (HorizontalScrollView) parent.getParent();
			hsvPhoto.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
				@Override
				public void onLayoutChange(View view, int l, int t, int r, int b, int ol,
										   int ot, int or, int ob) {
					hsvPhoto.removeOnLayoutChangeListener(this);
					hsvPhoto.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
				}
			});
		}
	}

	public void onPhotoGridItemClick(final LinearLayout llGridPhoto, final ArrayList<ImageObj> imageList, int position) {
		ImagePreviewFragment preview = new ImagePreviewFragment();
		preview.setDeletable(true);
		preview.setImageList(imageList, position);
		preview.setOnDeletePhotoCallback(new Interface.OnDeletePhotoCallback() {
			@Override
			public void onDeletePhoto(int position) {
				imageList.remove(position);
				llGridPhoto.removeViewAt(position);
				if(imageList.size() > 0) {
					invalidateViews(llGridPhoto, imageList);
				}
			}
		});
		transaction = getActivity().getSupportFragmentManager().beginTransaction();
		transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
				R.anim.slide_in_ltr, R.anim.slide_out_ltr);
		transaction.add(R.id.rlMain, preview);
		transaction.hide(this);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void invalidateViews(final LinearLayout llGridPhoto, final ArrayList<ImageObj> imageList) {
		int count = llGridPhoto.getChildCount();
		for(int i = 0; i < count; i++) {
			final int position = i;
			View view = llGridPhoto.getChildAt(i);
			CodePanButton btnPhoto = (CodePanButton) view.findViewById(R.id.btnPhoto);
			btnPhoto.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onPhotoGridItemClick(llGridPhoto, imageList, position);
				}
			});
		}
	}

	public void saveTask(final SQLiteAdapter db, final String notes) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					StoreObj store = visit.store;
					boolean result = TarkieLib.editTask(db, store.ID, visit.ID, notes,
							formList, imageList);
					if(result) {
						visit.notes = notes;
						saveTaskHandler.sendMessage(saveTaskHandler.obtainMessage(Result.SUCCESS));
					}
					else {
						saveTaskHandler.sendMessage(saveTaskHandler.obtainMessage(Result.FAILED));
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	Handler saveTaskHandler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what) {
				case Result.SUCCESS:
					if(refreshCallback != null) {
						refreshCallback.onRefresh();
					}
					manager.popBackStack();
					CodePanUtils.alertToast(getActivity(), "Task has been successfully saved.");
					break;
				case Result.FAILED:
					CodePanUtils.alertToast(getActivity(), "Failed to update task.");
					break;
			}
			return false;
		}
	});

	@Override
	public void onCameraDone(ArrayList<ImageObj> imageList) {
		if(imageList != null) {
			this.imageList.addAll(0, imageList);
		}
		updatePhotoGrid(llGridPhotoVisitDetails, this.imageList);
	}
}
