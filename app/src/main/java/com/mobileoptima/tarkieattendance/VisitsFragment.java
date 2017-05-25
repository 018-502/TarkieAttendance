package com.mobileoptima.tarkieattendance;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.codepan.calendar.callback.Interface.OnPickDateCallback;
import com.codepan.calendar.view.CalendarView;
import com.codepan.callback.Interface.OnRefreshCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.adapter.VisitsAdapter;
import com.mobileoptima.adapter.VisitsDateAdapter;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.core.Data;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.VisitObj;
import com.mobileoptima.model.VisitsDateObj;

import java.util.ArrayList;

public class VisitsFragment extends Fragment implements OnPickDateCallback, OnRefreshCallback {

	private final int PREVIOUS = 0;
	private final int CURRENT = 1;
	private final int NEXT = 2;
	private final int NO_OF_DAYS = 5;

	private OnOverrideCallback overrideCallback;
	private String csDate, selectedDate, initDate;
	private FragmentTransaction transaction;
	private CodePanLabel tvSelectedVisits;
	private VisitsDateAdapter dateAdapter;
	private ArrayList<VisitObj> visitList;
	private View previous, next, current;
	private int lastPosition = CURRENT;
	private ArrayList<View> viewList;
	private FragmentManager manager;
	private VisitsAdapter adapter;
	private int green, blue, gray;
	private ViewGroup container;
	private ViewPager vpVisits;
	private ListView lvVisits;
	private SQLiteAdapter db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String date = CodePanUtils.getDate();
		initDate = initDate != null ? initDate : date;
		csDate = CodePanUtils.getDateAfter(initDate, -1);
		selectedDate = initDate;
		Resources res = getResources();
		green = res.getColor(R.color.green_pri);
		blue = res.getColor(R.color.blue_pri);
		gray = res.getColor(R.color.gray_qua);
		MainActivity main = (MainActivity) getActivity();
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
		db.openConnection();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.container = container;
		View view = inflater.inflate(R.layout.visits_layout, container, false);
		tvSelectedVisits = (CodePanLabel) view.findViewById(R.id.tvSelectedVisits);
		vpVisits = (ViewPager) view.findViewById(R.id.vpVisits);
		lvVisits = (ListView) view.findViewById(R.id.lvVisits);
		viewList = getViewList(lastPosition);
		dateAdapter = new VisitsDateAdapter(getActivity(), viewList);
		vpVisits.setAdapter(dateAdapter);
		vpVisits.setCurrentItem(CURRENT, false);
		vpVisits.addOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				lastPosition = position;
				switch(position) {
					case PREVIOUS:
						csDate = CodePanUtils.getDateAfter(csDate, -NO_OF_DAYS);
						break;
					case NEXT:
						csDate = CodePanUtils.getDateAfter(csDate, NO_OF_DAYS);
						break;
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				if(state == ViewPager.SCROLL_STATE_IDLE) {
					switchItem(lastPosition);
				}
			}
		});
		lvVisits.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				VisitObj visit = visitList.get(i);
				VisitDetailsFragment details = new VisitDetailsFragment();
				details.setVisit(visit);
				details.setOnOverrideCallback(overrideCallback);
				details.setOnRefreshCallback(VisitsFragment.this);
				transaction = manager.beginTransaction();
				transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
						R.anim.slide_in_ltr, R.anim.slide_out_ltr);
				transaction.add(R.id.rlMain, details);
				transaction.addToBackStack(null);
				transaction.commit();
			}
		});
		return view;
	}

	public View getDateView(String startDate) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		LinearLayout llVisits = (LinearLayout) inflater.inflate(R.layout.visits_calendar_layout,
				container, false);
		int count = llVisits.getChildCount();
		ArrayList<VisitsDateObj> dateList = TarkieLib.getVisitsDate(startDate, count);
		for(int i = 0; i < count; i++) {
			final VisitsDateObj obj = dateList.get(i);
			View view = llVisits.getChildAt(i);
			CodePanButton btnDateVisits = (CodePanButton) view.findViewById(R.id.btnDateVisits);
			final LinearLayout llDateVisits = (LinearLayout) view.findViewById(R.id.llDateVisits);
			final CodePanLabel tvDateVisits = (CodePanLabel) view.findViewById(R.id.tvDateVisits);
			final CodePanLabel tvDayVisits = (CodePanLabel) view.findViewById(R.id.tvDayVisits);
			btnDateVisits.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					invalidate();
					tvDateVisits.setTextColor(Color.WHITE);
					tvDayVisits.setTextColor(Color.WHITE);
					llDateVisits.setBackgroundColor(green);
					obj.isSelect = true;
					view.setTag(obj);
					updateSelected(obj.date);
				}
			});
			if(selectedDate.equals(obj.date)) {
				btnDateVisits.performClick();
			}
			String date = CodePanUtils.getCalendarDate(obj.date, true, false);
			String day = obj.day.substring(0, 3);
			tvDateVisits.setText(date);
			tvDayVisits.setText(day);
			view.setTag(obj);
		}
		return llVisits;
	}

	public void invalidate() {
		if(viewList != null) {
			for(View item : viewList) {
				LinearLayout llVisits = (LinearLayout) item;
				int count = llVisits.getChildCount();
				for(int i = 0; i < count; i++) {
					View view = llVisits.getChildAt(i);
					VisitsDateObj obj = (VisitsDateObj) view.getTag();
					if(obj.isSelect) {
						obj.isSelect = false;
						view.setTag(obj);
						LinearLayout llDateVisits = (LinearLayout) view.findViewById(R.id.llDateVisits);
						CodePanLabel tvDateVisits = (CodePanLabel) view.findViewById(R.id.tvDateVisits);
						CodePanLabel tvDayVisits = (CodePanLabel) view.findViewById(R.id.tvDayVisits);
						llDateVisits.setBackgroundColor(Color.WHITE);
						tvDateVisits.setTextColor(blue);
						tvDayVisits.setTextColor(gray);
					}
				}
			}
		}
	}

	public ArrayList<View> getViewList(final int position) {
		ArrayList<View> viewList = new ArrayList<>();
		String psDate = CodePanUtils.getDateAfter(csDate, -NO_OF_DAYS);
		String nsDate = CodePanUtils.getDateAfter(csDate, NO_OF_DAYS);
		switch(position) {
			case PREVIOUS:
				next = current;
				current = previous;
				previous = getDateView(psDate);
				break;
			case CURRENT:
				previous = getDateView(psDate);
				current = getDateView(csDate);
				next = getDateView(nsDate);
				break;
			case NEXT:
				previous = current;
				current = next;
				next = getDateView(nsDate);
				break;
		}
		viewList.add(previous);
		viewList.add(current);
		viewList.add(next);
		return viewList;
	}

	public void switchItem(final int position) {
		viewList = getViewList(position);
		dateAdapter = new VisitsDateAdapter(getActivity(), viewList);
		vpVisits.setAdapter(dateAdapter);
		vpVisits.setCurrentItem(CURRENT, false);
	}

	public void showCalendar() {
		CalendarView calendar = new CalendarView();
		calendar.setOnPickDateCallback(this);
		calendar.setCurrentDate(selectedDate);
		transaction = manager.beginTransaction();
		transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
				R.anim.fade_in, R.anim.fade_out);
		transaction.add(R.id.rlMain, calendar);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	private void updateSelected(String selectedDate) {
		String date = CodePanUtils.getCalendarDate(selectedDate, true, true);
		this.selectedDate = selectedDate;
		tvSelectedVisits.setText(date);
		loadVisits(db, selectedDate);
	}

	@Override
	public void onPickDate(String selectedDate) {
		updateSelected(selectedDate);
		csDate = CodePanUtils.getDateAfter(selectedDate, -2);
		viewList = getViewList(CURRENT);
		dateAdapter = new VisitsDateAdapter(getActivity(), viewList);
		vpVisits.setAdapter(dateAdapter);
		vpVisits.setCurrentItem(CURRENT, false);
	}

	public String getSelectedDate() {
		return this.selectedDate;
	}

	public void addVisit() {
		AddVisitFragment addVisit = new AddVisitFragment();
		transaction = manager.beginTransaction();
		transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
				R.anim.slide_in_ltr, R.anim.slide_out_ltr);
		transaction.add(R.id.rlMain, addVisit);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void loadVisits(final SQLiteAdapter db, final String date) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					visitList = Data.loadVisits(db, date);
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
			adapter = new VisitsAdapter(getActivity(), visitList);
			lvVisits.setAdapter(adapter);
			return true;
		}
	});

	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}

	@Override
	public void onRefresh() {
		lvVisits.invalidate();
		adapter.notifyDataSetChanged();
	}

	public void setInitDate(String date) {
		this.initDate = date;
	}
}
