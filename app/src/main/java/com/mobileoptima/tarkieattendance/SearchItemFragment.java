package com.mobileoptima.tarkieattendance;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.codepan.calendar.callback.Interface.OnPickDateCallback;
import com.codepan.calendar.view.CalendarView;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.CodePanTextField;
import com.mobileoptima.adapter.SearchItemAdapter;
import com.mobileoptima.callback.Interface.OnSearchItemCallback;
import com.mobileoptima.constant.DateType;
import com.mobileoptima.constant.EntriesSearchType;
import com.mobileoptima.core.Data;
import com.mobileoptima.model.SearchObj;

import java.util.ArrayList;

public class SearchItemFragment extends Fragment implements OnClickListener, OnPickDateCallback {

	private final int LIMIT = 30;
	private final long IDLE_TIME = 500;

	private CodePanLabel tvStartDateSearchItem, tvEndDateSearchItem;
	private OnSearchItemCallback searchItemCallback;
	private String startDate, endDate, search;
	private ArrayList<SearchObj> searchList;
	private RelativeLayout rlDateSearchItem;
	private FragmentTransaction transaction;
	private CodePanTextField etSearchItem;
	private Handler inputFinishHandler;
	private SearchItemAdapter adapter;
	private FrameLayout flSearchItem;
	private FragmentManager manager;
	private ListView lvSearchItem;
	private int tabType, dateType;
	private SQLiteAdapter db;
	private long lastEdit;
	private View vDivider;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inputFinishHandler = new Handler();
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
		etSearchItem = (CodePanTextField) view.findViewById(R.id.etSearchItem);
		flSearchItem = (FrameLayout) view.findViewById(R.id.flSearchItem);
		lvSearchItem = (ListView) view.findViewById(R.id.lvSearchItem);
		vDivider = view.findViewById(R.id.vDivider);
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
				vDivider.setVisibility(View.VISIBLE);
				break;
			//case EntriesSearchType.STORE:
			case EntriesSearchType.FORM:
				flSearchItem.setVisibility(View.VISIBLE);
				rlDateSearchItem.setVisibility(View.GONE);
				vDivider.setVisibility(View.VISIBLE);
				break;
			case EntriesSearchType.CATEGORY:
			case EntriesSearchType.STATUS:
				flSearchItem.setVisibility(View.GONE);
				rlDateSearchItem.setVisibility(View.GONE);
				vDivider.setVisibility(View.GONE);
				break;
		}
		lvSearchItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				if(searchItemCallback != null) {
					SearchObj search = searchList.get(i);
					searchItemCallback.onSearchItem(search, tabType);
				}
			}
		});
		etSearchItem.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence cs, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence cs, int i, int i1, int i2) {
				search = cs.toString();
				lastEdit = System.currentTimeMillis();
				inputFinishHandler.removeCallbacks(inputFinishChecker);
				inputFinishHandler.postDelayed(inputFinishChecker, IDLE_TIME);
			}

			@Override
			public void afterTextChanged(Editable editable) {
			}
		});
		loadItems(db);
		return view;
	}

	public void setTabType(int tabType) {
		this.tabType = tabType;
	}

	public void setOnSearchItemCallback(OnSearchItemCallback searchItemCallback) {
		this.searchItemCallback = searchItemCallback;
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

	public void loadItems(final SQLiteAdapter db) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					switch(tabType) {
						case EntriesSearchType.DATE:
							searchList = Data.searchEntriesByDate(db, startDate, endDate);
							break;
//						case EntriesSearchType.STORE:
//							searchList = new ArrayList<>();
//							break;
						case EntriesSearchType.CATEGORY:
							searchList = Data.searchEntriesByCategory(db);
							break;
						case EntriesSearchType.FORM:
							searchList = Data.searchEntriesByForm(db, search);
							break;
						case EntriesSearchType.STATUS:
							searchList = Data.searchEntriesByStatus(db);
							break;
					}
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
			adapter = new SearchItemAdapter(getActivity(), searchList);
			lvSearchItem.setAdapter(adapter);
			return true;
		}
	});

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
		loadItems(db);
	}

	private Runnable inputFinishChecker = new Runnable() {
		@Override
		public void run() {
			if(System.currentTimeMillis() > lastEdit + IDLE_TIME - 500) {
				loadItems(db);
			}
		}
	};
}
