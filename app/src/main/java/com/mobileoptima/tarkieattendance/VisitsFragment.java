package com.mobileoptima.tarkieattendance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepan.database.SQLiteAdapter;

public class VisitsFragment extends Fragment {

	private FragmentTransaction transaction;
	private FragmentManager manager;
	private ViewPager vpVisits;
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
		View view = inflater.inflate(R.layout.visits_layout, container, false);
		vpVisits = (ViewPager) view.findViewById(R.id.vpVisits);
		return view;
	}
}
