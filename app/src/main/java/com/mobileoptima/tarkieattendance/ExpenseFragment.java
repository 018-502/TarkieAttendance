package com.mobileoptima.tarkieattendance;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepan.cache.TypefaceCache;
import com.codepan.calendar.adapter.ViewPagerAdapter;
import com.codepan.database.SQLiteAdapter;
import com.codepan.widget.SlidingTabLayout;
import com.mobileoptima.callback.Interface;
import com.mobileoptima.callback.Interface.OnOverrideCallback;

import java.util.ArrayList;

public class ExpenseFragment extends Fragment {

	private ArrayList<Fragment> fragmentList;
	private ExpenseReportsFragment reports;
	private ExpenseItemsFragment items;
	private OnOverrideCallback overrideCallback;
	private SlidingTabLayout stlExpense;
	private ViewPagerAdapter adapter;
	private ViewPager vpExpense;
	private String[] tabItems;
	private SQLiteAdapter db;
	private int green, gray;
	private Typeface bold;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fragmentList = new ArrayList<>();
		Resources res = getResources();
		tabItems = res.getStringArray(R.array.tab_expense);
		green = res.getColor(R.color.green_pri);
		gray = res.getColor(R.color.gray_pri);
		MainActivity main = (MainActivity) getActivity();
		bold = TypefaceCache.get(main.getAssets(), getString(R.string.proxima_nova_bold));
		db = main.getDatabase();
		db.openConnection();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.expense_layout, container, false);
		stlExpense = (SlidingTabLayout) view.findViewById(R.id.stlExpense);
		vpExpense = (ViewPager) view.findViewById(R.id.vpExpense);
		items = new ExpenseItemsFragment();
		items.setOnOverrideCallback(overrideCallback);
		reports = new ExpenseReportsFragment();
		reports.setOnOverrideCallback(overrideCallback);
		fragmentList.add(items);
		fragmentList.add(reports);
		adapter = new ViewPagerAdapter(getChildFragmentManager(), fragmentList, tabItems);
		vpExpense.setOffscreenPageLimit(2);
		vpExpense.setAdapter(adapter);
		stlExpense.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
			@Override
			public int getIndicatorColor(int position) {
				return green;
			}
		});
		stlExpense.setCustomTabView(R.layout.tab_item_layout, R.id.tvTab);
		stlExpense.setDistributeEvenly(true);
		stlExpense.setViewPager(vpExpense);
		stlExpense.setSelectedTypeface(bold);
		stlExpense.setSelectedColor(gray);
		return view;
	}

	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}

	public void addExpenseItem(String dDate, String dTime, String ID) {
		items.addExpenseItem(dDate, dTime, ID);
	}

	public void loadExpenseItems(SQLiteAdapter db) {
		items.loadExpenseItems(db);
	}

	public void addExpenseReport() {
		reports.addExpenseReport();
	}

	public void loadExpenseReports(SQLiteAdapter db) {
		reports.loadExpenseReports(db);
	}
}
