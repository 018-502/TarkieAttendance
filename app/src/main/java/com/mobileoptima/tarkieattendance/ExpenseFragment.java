package com.mobileoptima.tarkieattendance;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepan.cache.TypefaceCache;
import com.codepan.calendar.adapter.ViewPagerAdapter;
import com.codepan.database.SQLiteAdapter;
import com.codepan.widget.SlidingTabLayout;

import java.util.ArrayList;

public class ExpenseFragment extends Fragment {
	private ArrayList<Fragment> fragmentList;
	private FragmentManager manager;
	private FragmentTransaction transaction;
	private SQLiteAdapter db;
	private SlidingTabLayout stlExpense;
	private String[] tabItems;
	private Typeface bold;
	private ViewPager vpExpense;
	private ViewPagerAdapter adapter;
	private int green, gray;

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
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
		db.openConnection();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.expense_layout, container, false);
		stlExpense = (SlidingTabLayout) view.findViewById(R.id.stlExpense);
		vpExpense = (ViewPager) view.findViewById(R.id.vpExpense);
		ExpenseItemsFragment item = new ExpenseItemsFragment();
		ExpenseReportsFragment report = new ExpenseReportsFragment();
		fragmentList.add(item);
		fragmentList.add(report);
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

	public void loadExpense(SQLiteAdapter db) {
	}
}
