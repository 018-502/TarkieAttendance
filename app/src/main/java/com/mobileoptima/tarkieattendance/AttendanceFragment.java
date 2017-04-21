package com.mobileoptima.tarkieattendance;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.mobileoptima.adapter.AttendanceAdapter;
import com.mobileoptima.core.Data;
import com.mobileoptima.model.AttendanceObj;
import com.mobileoptima.model.TimeInObj;

import java.util.ArrayList;

public class AttendanceFragment extends Fragment implements OnClickListener {

	private ArrayList<AttendanceObj> attendanceList;
	private CodePanButton btnBackAttendance;
	private FragmentTransaction transaction;
	private AttendanceAdapter adapter;
	private FragmentManager manager;
	private ListView lvAttendance;
	private SQLiteAdapter db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		manager = getActivity().getSupportFragmentManager();
		db = ((MainActivity) getActivity()).getDatabase();
		db.openConnection();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.attendance_layout, container, false);
		btnBackAttendance = (CodePanButton) view.findViewById(R.id.btnBackAttendance);
		lvAttendance = (ListView) view.findViewById(R.id.lvAttendance);
		btnBackAttendance.setOnClickListener(this);
		lvAttendance.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				AttendanceObj attendance = attendanceList.get(i);
				TimeInObj in = attendance.in;
				if(in != null && in.isTimeOut) {
					SummaryFragment summary = new SummaryFragment();
					summary.setAttendance(attendance);
					transaction = manager.beginTransaction();
					transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
							R.anim.slide_in_ltr, R.anim.slide_out_ltr);
					transaction.add(R.id.rlMain, summary);
					transaction.hide(AttendanceFragment.this);
					transaction.addToBackStack(null);
					transaction.commit();
				}
				else {
					CodePanUtils.alertToast(getActivity(), "Please time-out first.");
				}
			}
		});
		loadAttendance(db);
		return view;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnBackAttendance:
				manager.popBackStack();
				break;
		}
	}

	public void loadAttendance(final SQLiteAdapter db) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					attendanceList = Data.loadAttendance(db);
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
			adapter = new AttendanceAdapter(getActivity(), attendanceList);
			lvAttendance.setAdapter(adapter);
			return true;
		}
	});
}
