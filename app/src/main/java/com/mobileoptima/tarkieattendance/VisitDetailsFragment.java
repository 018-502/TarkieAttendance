package com.mobileoptima.tarkieattendance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepan.database.SQLiteAdapter;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.model.StoreObj;
import com.mobileoptima.model.TaskObj;

public class VisitDetailsFragment extends Fragment {

	private CodePanLabel tvStoreVisitDetails, tvAddressVisitDetails;
	private SQLiteAdapter db;
	private TaskObj task;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivity main = (MainActivity) getActivity();
		db = main.getDatabase();
		db.openConnection();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.visit_details_layout, container, false);
		tvStoreVisitDetails = (CodePanLabel) view.findViewById(R.id.tvStoreVisitDetails);
		tvAddressVisitDetails = (CodePanLabel) view.findViewById(R.id.tvAddressVisitDetails);
		if(task != null) {
			StoreObj store = task.store;
			if(store != null) {
				tvStoreVisitDetails.setText(store.name);
				tvAddressVisitDetails.setText(store.address);
			}
		}
		return view;
	}

	public void setTask(TaskObj task) {
		this.task = task;
	}
}
