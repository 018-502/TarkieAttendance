package com.mobileoptima.tarkieattendance;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.adapter.VisitsDateAdapter;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.VisitsDateObj;

import java.util.ArrayList;

public class VisitsFragment extends Fragment {

	private final int PREVIOUS = 0;
	private final int CURRENT = 1;
	private final int NEXT = 2;
	private final int NO_OF_DAYS = 5;

	private View previous, next, current;
	private FragmentTransaction transaction;
	private CodePanLabel tvSelectedVisits;
	private VisitsDateAdapter dateAdapter;
	private String csDate, selectedDate;
	private int lastPosition = CURRENT;
	private ArrayList<View> viewList;
	private FragmentManager manager;
	private int green, blue, gray;
	private ViewGroup container;
	private ViewPager vpVisits;
	private SQLiteAdapter db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String today = CodePanUtils.getDate();
		csDate = CodePanUtils.getDateAfter(today, -1);
		selectedDate = today;
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
					onSelectDate(obj.date);
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

	private void onSelectDate(String selectedDate) {
		this.selectedDate = selectedDate;
		String date = CodePanUtils.getCalendarDate(selectedDate, true, true);
		tvSelectedVisits.setText(date);
	}
}
