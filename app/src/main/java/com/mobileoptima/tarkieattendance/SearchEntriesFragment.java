package com.mobileoptima.tarkieattendance;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.codepan.cache.TypefaceCache;
import com.codepan.calendar.adapter.ViewPagerAdapter;
import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.SlidingTabLayout;
import com.mobileoptima.constant.EntriesSearchType;

import java.util.ArrayList;

public class SearchEntriesFragment extends Fragment implements OnClickListener {

	private OnFragmentCallback fragmentCallback;
	private CodePanButton btnBackSearchEntries;
	private SlidingTabLayout stlSearchEntries;
	private ArrayList<Fragment> fragmentList;
	private ViewPager vpSearchEntries;
	private ViewPagerAdapter adapter;
	private FragmentManager manager;
	private String[] tabItems;
	private SQLiteAdapter db;
	private int green, gray;
	private Typeface bold;

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
		fragmentList = new ArrayList<>();
		Resources res = getResources();
		tabItems = res.getStringArray(R.array.tab_search_entries);
		green = res.getColor(R.color.green_pri);
		gray = res.getColor(R.color.gray_pri);
		MainActivity main = (MainActivity) getActivity();
		bold = TypefaceCache.get(main.getAssets(), getString(R.string.proxima_nova_bold));
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
		db.openConnection();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.search_entries_layout, container, false);
		btnBackSearchEntries = (CodePanButton) view.findViewById(R.id.btnBackSearchEntries);
		stlSearchEntries = (SlidingTabLayout) view.findViewById(R.id.stlSearchEntries);
		vpSearchEntries = (ViewPager) view.findViewById(R.id.vpSearchEntries);
		btnBackSearchEntries.setOnClickListener(this);
		SearchItemFragment date = new SearchItemFragment();
		SearchItemFragment store = new SearchItemFragment();
		SearchItemFragment category = new SearchItemFragment();
		SearchItemFragment status = new SearchItemFragment();
		date.setTabType(EntriesSearchType.DATE);
		store.setTabType(EntriesSearchType.STORE);
		category.setTabType(EntriesSearchType.CATEGORY);
		status.setTabType(EntriesSearchType.STATUS);
		fragmentList.add(date);
		fragmentList.add(store);
		fragmentList.add(category);
		fragmentList.add(status);
		adapter = new ViewPagerAdapter(getChildFragmentManager(), fragmentList, tabItems);
		vpSearchEntries.setOffscreenPageLimit(3);
		vpSearchEntries.setAdapter(adapter);
		stlSearchEntries.setCustomTabColorizer(new SlidingTabLayout.TabColorizer(){
			@Override
			public int getIndicatorColor(int position){
				return green;
			}
		});
		stlSearchEntries.setCustomTabView(R.layout.tab_item_layout, R.id.tvTab);
		stlSearchEntries.setDistributeEvenly(true);
		stlSearchEntries.setViewPager(vpSearchEntries);
		stlSearchEntries.setSelectedTypeface(bold);
		stlSearchEntries.setSelectedColor(gray);
		return view;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnBackSearchEntries:
				manager.popBackStack();
				break;
		}
	}

	public void setOnFragmentCallback(OnFragmentCallback fragmentCallback) {
		this.fragmentCallback = fragmentCallback;
	}

	private void setOnBackStack(boolean isOnBackStack) {
		if(fragmentCallback != null) {
			fragmentCallback.onFragment(isOnBackStack);
		}
	}
}
