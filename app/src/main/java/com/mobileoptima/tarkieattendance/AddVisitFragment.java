package com.mobileoptima.tarkieattendance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.codepan.database.SQLiteAdapter;
import com.codepan.widget.CodePanButton;

public class AddVisitFragment extends Fragment implements OnClickListener {

	private CodePanButton btnBackAddVisit;
	private FragmentManager manager;
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
		View view = inflater.inflate(R.layout.add_visit_layout, container, false);
		btnBackAddVisit = (CodePanButton) view.findViewById(R.id.btnBackAddVisit);
		btnBackAddVisit.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnBackAddVisit:
				manager.popBackStack();
				break;
		}
	}
}
