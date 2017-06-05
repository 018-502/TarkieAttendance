package com.mobileoptima.tarkieattendance;

import android.content.Context;
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
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.callback.Interface.OnFragmentCallback;
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
import com.mobileoptima.callback.Interface.OnSaveEntryCallback;
import com.mobileoptima.callback.Interface.OnSaveVisitCallback;
import com.mobileoptima.callback.Interface.OnSelectStatusCallback;
import com.mobileoptima.callback.Interface.OnSelectStoreCallback;
import com.mobileoptima.callback.Interface.OnTagFormsCallback;
import com.mobileoptima.constant.App;
import com.mobileoptima.constant.Convention;
import com.mobileoptima.constant.ImageType;
import com.mobileoptima.constant.Result;
import com.mobileoptima.constant.Settings;
import com.mobileoptima.constant.Tag;
import com.mobileoptima.core.Data;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.CheckInObj;
import com.mobileoptima.model.CheckOutObj;
import com.mobileoptima.model.EntryObj;
import com.mobileoptima.model.FormObj;
import com.mobileoptima.model.PhotoObj;
import com.mobileoptima.model.StoreObj;
import com.mobileoptima.model.TaskObj;
import com.mobileoptima.model.TaskStatusObj;
import com.mobileoptima.model.VisitObj;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class VisitDetailsFragment extends Fragment implements OnClickListener,
		OnCheckInCallback, OnCheckOutCallback, OnSelectStatusCallback, OnCameraDoneCallback,
		OnBackPressedCallback, OnFragmentCallback, OnSelectStoreCallback, OnTagFormsCallback {

	private CodePanButton btnCheckInVisitDetails, btnCheckOutVisitDetails, btnBackVisitDetails,
			btnSaveVisitDetails, btnAddPhotoVisitDetails, btnStoreVisitDetails,
			btnAddFormVisitDetails;
	private CodePanLabel tvStoreVisitDetails, tvAddressVisitDetails, tvTitleVisitDetails;
	private LinearLayout llFormsVisitDetails, llGridPhotoVisitDetails;
	private boolean hasPhotosAdded, hasEntriesSaved, withChanges;
	private OnSaveVisitCallback saveVisitCallback;
	private CodePanTextField etNotesVisitDetails;
	private OnOverrideCallback overrideCallback;
	private FrameLayout flStoreVisitDetails;
	private FragmentTransaction transaction;
	private FragmentManager manager;
	private String convention;
	private MainActivity main;
	private SQLiteAdapter db;
	private CheckOutObj out;
	private VisitObj visit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main = (MainActivity) getActivity();
		main.setOnBackPressedCallback(this);
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
		db.openConnection();
		convention = TarkieLib.getConvention(db, Convention.STORES);
		if(convention != null) {
			convention = StringUtils.capitalize(convention);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.visit_details_layout, container, false);
		tvStoreVisitDetails = (CodePanLabel) view.findViewById(R.id.tvStoreVisitDetails);
		tvAddressVisitDetails = (CodePanLabel) view.findViewById(R.id.tvAddressVisitDetails);
		tvTitleVisitDetails = (CodePanLabel) view.findViewById(R.id.tvTitleVisitDetails);
		etNotesVisitDetails = (CodePanTextField) view.findViewById(R.id.etNotesVisitDetails);
		llFormsVisitDetails = (LinearLayout) view.findViewById(R.id.llFormsVisitDetails);
		llGridPhotoVisitDetails = (LinearLayout) view.findViewById(R.id.llGridPhotoVisitDetails);
		flStoreVisitDetails = (FrameLayout) view.findViewById(R.id.flStoreVisitDetails);
		btnBackVisitDetails = (CodePanButton) view.findViewById(R.id.btnBackVisitDetails);
		btnCheckInVisitDetails = (CodePanButton) view.findViewById(R.id.btnCheckInVisitDetails);
		btnCheckOutVisitDetails = (CodePanButton) view.findViewById(R.id.btnCheckOutVisitDetails);
		btnAddPhotoVisitDetails = (CodePanButton) view.findViewById(R.id.btnAddPhotoVisitDetails);
		btnSaveVisitDetails = (CodePanButton) view.findViewById(R.id.btnSaveVisitDetails);
		btnStoreVisitDetails = (CodePanButton) view.findViewById(R.id.btnStoreVisitDetails);
		btnAddFormVisitDetails = (CodePanButton) view.findViewById(R.id.btnAddFormVisitDetails);
		btnStoreVisitDetails.setOnClickListener(this);
		btnSaveVisitDetails.setOnClickListener(this);
		btnBackVisitDetails.setOnClickListener(this);
		btnCheckInVisitDetails.setOnClickListener(this);
		btnCheckOutVisitDetails.setOnClickListener(this);
		btnAddPhotoVisitDetails.setOnClickListener(this);
		btnAddFormVisitDetails.setOnClickListener(this);
		if(visit != null) {
			StoreObj store = visit.store;
			if(store != null) {
				tvStoreVisitDetails.setText(store.name);
				tvAddressVisitDetails.setText(store.address);
			}
			else {
				String name = convention + " Name";
				tvStoreVisitDetails.setText(name);
				tvAddressVisitDetails.setText(R.string.address);
				tvTitleVisitDetails.setText(visit.name);
			}
			if(!visit.isFromWeb) {
				flStoreVisitDetails.setVisibility(View.VISIBLE);
			}
			else {
				flStoreVisitDetails.setVisibility(View.GONE);
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
					visit.entryList = Data.loadEntries(db, taskID);
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
				for(final EntryObj entry : visit.entryList) {
					final int index = visit.entryList.indexOf(entry);
					FormObj form = entry.form;
					if(form.isChecked) {
						View child = inflater.inflate(R.layout.visit_details_form_item, container, false);
						CodePanLabel tvVisitDetailsForm = (CodePanLabel) child.findViewById(R.id.tvVisitDetailsForm);
						CodePanButton btnVisitDetailsForm = (CodePanButton) child.findViewById(R.id.btnVisitDetailsForm);
						tvVisitDetailsForm.setText(form.name);
						btnVisitDetailsForm.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								if(visit.isCheckIn) {
									FormFragment form = new FormFragment();
									form.setEntry(entry);
									form.setOverridden(true);
									form.setOnOverrideCallback(overrideCallback);
									form.setOnFragmentCallback(VisitDetailsFragment.this);
									form.setOnSaveEntryCallback(new OnSaveEntryCallback() {
										@Override
										public void onSaveEntry(EntryObj entry) {
											visit.entryList.set(index, entry);
											hasEntriesSaved = true;
											setWithChanges(true);
										}
									});
									transaction = manager.beginTransaction();
									transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
											R.anim.slide_in_ltr, R.anim.slide_out_ltr);
									transaction.add(R.id.rlMain, form, Tag.FORM);
									transaction.hide(VisitDetailsFragment.this);
									transaction.addToBackStack(null);
									transaction.commit();
								}
								else {
									TarkieLib.alertDialog(getActivity(), R.string.check_in_required_title,
											R.string.check_in_required_message);
								}
							}
						});
						llFormsVisitDetails.addView(child);
					}
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
					visit.photoList = Data.loadPhotos(db, taskID);
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
			updatePhotoGrid(llGridPhotoVisitDetails, visit.photoList);
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
				onBackPressed();
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
				camera.setOnFragmentCallback(this);
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
				StoreObj store = visit.store;
				if(store != null) {
					saveTask(db);
				}
				else {
					String title = convention + " Required";
					String message = "Please select a " + convention.toLowerCase();
					TarkieLib.alertDialog(getActivity(), title, message);
				}
				break;
			case R.id.btnStoreVisitDetails:
				StoresFragment stores = new StoresFragment();
				stores.setOnSelectStoreCallback(this);
				transaction = manager.beginTransaction();
				transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
						R.anim.slide_in_ltr, R.anim.slide_out_ltr);
				transaction.add(R.id.rlMain, stores);
				transaction.hide(this);
				transaction.addToBackStack(null);
				transaction.commit();
				break;
			case R.id.btnAddFormVisitDetails:
				ArrayList<FormObj> taggedList = new ArrayList<>();
				for(EntryObj entry : visit.entryList) {
					FormObj form = entry.form;
					taggedList.add(form);
				}
				AddFormsFragment addForms = new AddFormsFragment();
				addForms.setVisit(visit);
				addForms.setOverridden(true);
				addForms.setTaggedList(taggedList);
				addForms.setOnTagFormsCallback(this);
				addForms.setOnFragmentCallback(this);
				addForms.setOnOverrideCallback(overrideCallback);
				transaction = manager.beginTransaction();
				transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
						R.anim.slide_in_ltr, R.anim.slide_out_ltr);
				transaction.add(R.id.rlMain, addForms);
				transaction.hide(this);
				transaction.addToBackStack(null);
				transaction.commit();
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
					if(saveVisitCallback != null) {
						saveVisitCallback.onSaveVisit(visit);
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
					if(saveVisitCallback != null) {
						saveVisitCallback.onSaveVisit(visit);
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

	public void updatePhotoGrid(final LinearLayout llGridPhoto, final ArrayList<PhotoObj> photoList) {
		View view = getView();
		if(view != null) {
			LayoutInflater inflater = getActivity().getLayoutInflater();
			ViewGroup container = (ViewGroup) view.getParent();
			llGridPhoto.removeAllViews();
			for(final PhotoObj obj : photoList) {
				String uri = "file://" + getActivity().getDir(App.FOLDER, Context.MODE_PRIVATE)
						.getPath() + "/" + obj.fileName;
				View child = inflater.inflate(R.layout.photo_item, container, false);
				CodePanButton btnPhoto = (CodePanButton) child.findViewById(R.id.btnPhoto);
				ImageView ivPhoto = (ImageView) child.findViewById(R.id.ivPhoto);
				CodePanUtils.displayImage(ivPhoto, uri, R.color.gray_ter);
				btnPhoto.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						int position = photoList.indexOf(obj);
						onPhotoGridItemClick(llGridPhoto, photoList, position);
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

	public void onPhotoGridItemClick(final LinearLayout llGridPhoto, final ArrayList<PhotoObj> photoList, int position) {
		ImagePreviewFragment preview = new ImagePreviewFragment();
		preview.setDeletable(true);
		preview.setPhotoList(photoList, position);
		preview.setOnFragmentCallback(this);
		preview.setOnDeletePhotoCallback(new Interface.OnDeletePhotoCallback() {
			@Override
			public void onDeletePhoto(int position) {
				photoList.remove(position);
				llGridPhoto.removeViewAt(position);
				if(photoList.size() > 0) {
					invalidateViews(llGridPhoto, photoList);
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

	public void invalidateViews(final LinearLayout llGridPhoto, final ArrayList<PhotoObj> photoList) {
		int count = llGridPhoto.getChildCount();
		for(int i = 0; i < count; i++) {
			final int position = i;
			View view = llGridPhoto.getChildAt(i);
			CodePanButton btnPhoto = (CodePanButton) view.findViewById(R.id.btnPhoto);
			btnPhoto.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onPhotoGridItemClick(llGridPhoto, photoList, position);
				}
			});
		}
	}

	public void saveTask(final SQLiteAdapter db) {
		final String notes = etNotesVisitDetails.getText().toString().trim();
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					StoreObj store = visit.store;
					boolean result = TarkieLib.editTask(db, visit.store, visit.ID, notes,
							visit.entryList, visit.photoList);
					if(result) {
						visit.notes = notes;
						visit.name = store.name;
						saveTaskHandler.sendMessage(saveTaskHandler.obtainMessage(Result.SUCCESS, visit));
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
					VisitObj visit = (VisitObj) msg.obj;
					if(hasPhotosAdded) {
						main.reloadPhotos();
					}
					if(hasEntriesSaved) {
						main.reloadEntries();
					}
					if(saveVisitCallback != null) {
						saveVisitCallback.onSaveVisit(visit);
					}
					manager.popBackStack();
					CodePanUtils.alertToast(getActivity(), "Task has been successfully saved.");
					break;
				case Result.FAILED:
					CodePanUtils.alertToast(getActivity(), "Failed to updateDetails task.");
					break;
			}
			return false;
		}
	});

	@Override
	public void onCameraDone(ArrayList<PhotoObj> photoList) {
		this.hasPhotosAdded = true;
		if(photoList != null) {
			photoList.addAll(0, visit.photoList);
		}
		updatePhotoGrid(llGridPhotoVisitDetails, photoList);
		visit.photoList = photoList;
		setWithChanges(true);
	}

	@Override
	public void onBackPressed() {
		if(withChanges) {
			final AlertDialogFragment alert = new AlertDialogFragment();
			alert.setDialogTitle(R.string.save_changes_title);
			alert.setDialogMessage(R.string.save_changes_message);
			alert.setOnFragmentCallback(this);
			alert.setPositiveButton("Save", new OnClickListener() {
				@Override
				public void onClick(View view) {
					manager.popBackStack();
					manager.popBackStack();
					saveTask(db);
				}
			});
			alert.setNegativeButton("Discard", new OnClickListener() {
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
		else {
			manager.popBackStack();
		}
	}

	@Override
	public void onFragment(boolean status) {
		if(overrideCallback != null) {
			overrideCallback.onOverride(!status && withChanges);
		}
		if(!status) {
			MainActivity main = (MainActivity) getActivity();
			main.setOnBackPressedCallback(this);
		}
	}

	@Override
	public void onSelectStore(StoreObj store) {
		visit.store = store;
		tvStoreVisitDetails.setText(store.name);
		tvAddressVisitDetails.setText(store.address);
		manager.popBackStack();
		setWithChanges(true);
	}

	public void setOnSaveVisitCallback(OnSaveVisitCallback saveVisitCallback) {
		this.saveVisitCallback = saveVisitCallback;
	}

	@Override
	public void onTagForms(ArrayList<FormObj> formList) {
		for(FormObj form : formList) {
			boolean exists = false;
			for(EntryObj entry : visit.entryList) {
				if(form.ID.equals(entry.form.ID)) {
					entry.form = form;
					exists = true;
					break;
				}
			}
			if(!exists) {
				EntryObj entry = new EntryObj();
				entry.form = form;
				visit.entryList.add(entry);
			}
		}
		setWithChanges(true);
		formsHandler.sendMessage(formsHandler.obtainMessage());
	}

	public void setWithChanges(boolean withChanges) {
		this.withChanges = withChanges;
		if(overrideCallback != null) {
			overrideCallback.onOverride(withChanges);
		}
	}
}
