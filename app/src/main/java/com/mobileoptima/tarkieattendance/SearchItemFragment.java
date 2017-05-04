package com.mobileoptima.tarkieattendance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.codepan.calendar.callback.Interface.OnPickDateCallback;
import com.codepan.calendar.view.CalendarView;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.constant.DateType;
import com.mobileoptima.constant.EntriesSearchType;

public class SearchItemFragment extends Fragment implements OnClickListener, OnPickDateCallback {

	private CodePanLabel tvStartDateSearchItem, tvEndDateSearchItem;
	private RelativeLayout rlDateSearchItem;
	private FragmentTransaction transaction;
	private FrameLayout flSearchItem;
	private String startDate, endDate;
	private FragmentManager manager;
	private int tabType, dateType;
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
		View view = inflater.inflate(R.layout.search_item_layout, container, false);
		tvStartDateSearchItem = (CodePanLabel) view.findViewById(R.id.tvStartDateSearchItem);
		tvEndDateSearchItem = (CodePanLabel) view.findViewById(R.id.tvEndDateSearchItem);
		rlDateSearchItem = (RelativeLayout) view.findViewById(R.id.rlDateSearchItem);
		flSearchItem = (FrameLayout) view.findViewById(R.id.flSearchItem);
		tvStartDateSearchItem.setOnClickListener(this);
		tvEndDateSearchItem.setOnClickListener(this);
		endDate = CodePanUtils.getDate();
		startDate = CodePanUtils.getDateAfter(endDate, -7);
		String start = CodePanUtils.getCalendarDate(startDate, true, true);
		String end = CodePanUtils.getCalendarDate(endDate, true, true);
		tvStartDateSearchItem.setText(start);
		tvEndDateSearchItem.setText(end);
		switch(tabType) {
			case EntriesSearchType.DATE:
				flSearchItem.setVisibility(View.GONE);
				rlDateSearchItem.setVisibility(View.VISIBLE);
				break;
			case EntriesSearchType.STORE:
				flSearchItem.setVisibility(View.VISIBLE);
				rlDateSearchItem.setVisibility(View.GONE);
				break;
			case EntriesSearchType.CATEGORY:
				flSearchItem.setVisibility(View.GONE);
				rlDateSearchItem.setVisibility(View.GONE);
				break;
			case EntriesSearchType.STATUS:
				flSearchItem.setVisibility(View.GONE);
				rlDateSearchItem.setVisibility(View.GONE);
				break;

		}
		return view;
	}

	public void setTabType(int tabType) {
		this.tabType = tabType;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.tvStartDateSearchItem:
				showCalendar(startDate);
				dateType = DateType.START;
				break;
			case R.id.tvEndDateSearchItem:
				showCalendar(endDate);
				dateType = DateType.END;
				break;
		}
	}

	public void showCalendar(String currentDate) {
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
		switch(dateType) {
			case DateType.START:
				if(date != null) {
					long startMillis = CodePanUtils.dateToMillis(date);
					long endMillis = CodePanUtils.dateToMillis(endDate);
					if(startMillis <= endMillis) {
						String start = CodePanUtils.getCalendarDate(date, true, true);
						tvStartDateSearchItem.setText(start);
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
					if(endMIllis >= startMillis) {
						String end = CodePanUtils.getCalendarDate(date, true, true);
						tvEndDateSearchItem.setText(end);
						endDate = date;
					}
					else {
						CodePanUtils.alertToast(getActivity(), "End date must be greater than start date.");
					}
				}
				break;
		}
	}
}
