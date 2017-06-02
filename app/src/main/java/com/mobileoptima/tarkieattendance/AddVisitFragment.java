package com.mobileoptima.tarkieattendance;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.codepan.calendar.callback.Interface.OnPickDateCallback;
import com.codepan.calendar.view.CalendarView;
import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.callback.Interface.OnRefreshCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.callback.Interface.OnSelectStoreCallback;
import com.mobileoptima.constant.Convention;
import com.mobileoptima.constant.DateType;
import com.mobileoptima.constant.Result;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.StoreObj;
import com.mobileoptima.model.TaskObj;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class AddVisitFragment extends Fragment implements OnClickListener, OnPickDateCallback,
		OnSelectStoreCallback, OnBackPressedCallback, OnFragmentCallback {

	private CodePanButton btnBackAddVisit, btnStoreAddVisit, btnStartAddVisit,
			btnEndAddVisit, btnAddAnotherVisit, btnSaveAddVisit;
	private CodePanLabel tvStoreAddVisit, tvScheduleAddVisit;
	private String startDate, endDate, convention;
	private OnOverrideCallback overrideCallback;
	private OnRefreshCallback refreshCallback;
	private FragmentTransaction transaction;
	private ArrayList<TaskObj> taskList;
	private FragmentManager manager;
	private LinearLayout llAddVisit;
	private boolean withChanges;
	private SQLiteAdapter db;
	private StoreObj store;
	private int dateType;

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
		taskList = new ArrayList<>();
		MainActivity main = (MainActivity) getActivity();
		manager = main.getSupportFragmentManager();
		main.setOnBackPressedCallback(this);
		db = main.getDatabase();
		db.openConnection();
		convention = TarkieLib.getConvention(db, Convention.STORES);
		if(convention != null) {
			convention = StringUtils.capitalize(convention);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.add_visit_layout, container, false);
		tvStoreAddVisit = (CodePanLabel) view.findViewById(R.id.tvStoreAddVisit);
		tvScheduleAddVisit = (CodePanLabel) view.findViewById(R.id.tvScheduleAddVisit);
		btnBackAddVisit = (CodePanButton) view.findViewById(R.id.btnBackAddVisit);
		btnStoreAddVisit = (CodePanButton) view.findViewById(R.id.btnStoreAddVisit);
		btnStartAddVisit = (CodePanButton) view.findViewById(R.id.btnStartAddVisit);
		btnEndAddVisit = (CodePanButton) view.findViewById(R.id.btnEndAddVisit);
		btnAddAnotherVisit = (CodePanButton) view.findViewById(R.id.btnAddAnotherVisit);
		btnSaveAddVisit = (CodePanButton) view.findViewById(R.id.btnSaveAddVisit);
		llAddVisit = (LinearLayout) view.findViewById(R.id.llAddVisit);
		btnBackAddVisit.setOnClickListener(this);
		btnStoreAddVisit.setOnClickListener(this);
		btnStartAddVisit.setOnClickListener(this);
		btnEndAddVisit.setOnClickListener(this);
		btnSaveAddVisit.setOnClickListener(this);
		btnAddAnotherVisit.setOnClickListener(this);
		String schedule = getString(R.string.schedule);
		String select = "Select " + convention;
		btnStoreAddVisit.setHint(select);
		tvStoreAddVisit.setText(convention);
		TarkieLib.requiredField(tvStoreAddVisit, convention);
		TarkieLib.requiredField(tvScheduleAddVisit, schedule);
		return view;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnBackAddVisit:
				onBackPressed();
				break;
			case R.id.btnStoreAddVisit:
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
			case R.id.btnStartAddVisit:
				showCalendar(startDate);
				dateType = DateType.START;
				break;
			case R.id.btnEndAddVisit:
				showCalendar(endDate);
				dateType = DateType.END;
				break;
			case R.id.btnAddAnotherVisit:
				if(isValid()) {
					TaskObj task = new TaskObj();
					task.store = store;
					task.startDate = startDate;
					task.endDate = endDate;
					taskList.add(task);
					addVisit(task);
				}
				break;
			case R.id.btnSaveAddVisit:
				if(!taskList.isEmpty()) {
					saveTasks(db);
				}
				else {
					if(isValid()) {
						TaskObj task = new TaskObj();
						task.store = store;
						task.startDate = startDate;
						task.endDate = endDate;
						taskList.add(task);
						saveTasks(db);
					}
				}
				break;
		}
	}

	public void saveTasks(final SQLiteAdapter db) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					boolean result = TarkieLib.addTasks(db, taskList);
					handler.sendMessage(handler.obtainMessage(result ?
							Result.SUCCESS : Result.FAILED));
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	Handler handler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what) {
				case Result.SUCCESS:
					MainActivity main = (MainActivity) getActivity();
					main.updateSyncCount();
					main.reloadSchedule();
					manager.popBackStack();
					CodePanUtils.alertToast(getActivity(), "Visit successfully added.");
					if(refreshCallback != null) {
						refreshCallback.onRefresh();
					}
					break;
				case Result.FAILED:
					CodePanUtils.alertToast(getActivity(), "Failed to save visit/s");
					break;
			}
			return true;
		}
	});

	public boolean isValid() {
		if(store != null) {
			if(startDate != null && endDate != null) {
				return true;
			}
			else {
				TarkieLib.alertDialog(getActivity(), "Schedule Required",
						"Please select start date and end date.", this);
			}
		}
		else {
			String title = convention + " Required";
			String message = "Please select a " + convention.toLowerCase();
			TarkieLib.alertDialog(getActivity(), title, message, this);
		}
		return false;
	}

	public void addVisit(TaskObj task) {
		View view = getView();
		if(view != null) {
			LayoutInflater inflater = getActivity().getLayoutInflater();
			ViewGroup container = (ViewGroup) view.getParent();
			View child = inflater.inflate(R.layout.add_visit_item, container, false);
			CodePanLabel tvNameAddVisit = (CodePanLabel) child.findViewById(R.id.tvNameAddVisit);
			CodePanLabel tvStartAddVisit = (CodePanLabel) child.findViewById(R.id.tvStartAddVisit);
			CodePanLabel tvEndAddVisit = (CodePanLabel) child.findViewById(R.id.tvEndAddVisit);
			StoreObj store = task.store;
			String start = CodePanUtils.getCalendarDate(task.startDate, true, false);
			String end = CodePanUtils.getCalendarDate(task.endDate, true, false);
			tvNameAddVisit.setText(store.name);
			tvStartAddVisit.setText(start);
			tvEndAddVisit.setText(end);
			llAddVisit.addView(child);
		}
	}

	public void showCalendar(String date) {
		String today = CodePanUtils.getDate();
		String currentDate = date != null ? date : today;
		CalendarView calendar = new CalendarView();
		calendar.setOnPickDateCallback(this);
		calendar.setCurrentDate(currentDate);
		transaction = manager.beginTransaction();
		transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
				R.anim.fade_in, R.anim.fade_out);
		transaction.add(R.id.rlMain, calendar);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public void onPickDate(String date) {
		this.withChanges = true;
		switch(dateType) {
			case DateType.START:
				if(date != null) {
					long startMillis = CodePanUtils.dateToMillis(date);
					long endMillis = CodePanUtils.dateToMillis(endDate);
					if(startMillis <= endMillis || endMillis == 0) {
						String start = CodePanUtils.getCalendarDate(date, true, true);
						btnStartAddVisit.setText(start);
						startDate = date;
					}
					else {
						CodePanUtils.alertToast(getActivity(), "Start date must be less than end date.");
					}
				}
				break;
			case DateType.END:
				if(date != null) {
					long startMillis = CodePanUtils.dateToMillis(startDate);
					long endMIllis = CodePanUtils.dateToMillis(date);
					if(endMIllis >= startMillis || startMillis == 0) {
						String end = CodePanUtils.getCalendarDate(date, true, true);
						btnEndAddVisit.setText(end);
						endDate = date;
					}
					else {
						CodePanUtils.alertToast(getActivity(), "End date must be greater than start date.");
					}
				}
				break;
		}
	}

	@Override
	public void onSelectStore(StoreObj store) {
		btnStoreAddVisit.setText(store.name);
		manager.popBackStack();
		this.withChanges = true;
		this.store = store;
	}

	public void setOnRefreshCallback(OnRefreshCallback refreshCallback) {
		this.refreshCallback = refreshCallback;
	}

	@Override
	public void onBackPressed() {
		if(withChanges) {
			AlertDialogFragment alert = new AlertDialogFragment();
			alert.setDialogTitle(R.string.discard_changes_title);
			alert.setDialogMessage(R.string.discard_changes_message);
			alert.setOnFragmentCallback(this);
			alert.setPositiveButton("Yes", new OnClickListener() {
				@Override
				public void onClick(View view) {
					manager.popBackStack();
					manager.popBackStack();
				}
			});
			alert.setNegativeButton("Cancel", new OnClickListener() {
				@Override
				public void onClick(View view) {
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
			overrideCallback.onOverride(!status);
		}
	}

	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}

	private void setOnBackStack(boolean isOnBackStack) {
		if(overrideCallback != null) {
			overrideCallback.onOverride(isOnBackStack);
		}
	}
}
