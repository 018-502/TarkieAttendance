package com.mobileoptima.tarkieattendance;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
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
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.codepan.database.SQLiteAdapter;
import com.codepan.widget.CodePanTextField;
import com.mobileoptima.adapter.ExpenseReportsAdapter;
import com.mobileoptima.core.Data;
import com.mobileoptima.model.ExpenseReportsObj;

import java.util.ArrayList;

public class ExpenseReportsFragment extends Fragment implements OnClickListener {
	private final long IDLE_TIME = 500;
	private ArrayList<ExpenseReportsObj> expenseReportsList;
	private CodePanTextField etSearchExpenseReports;
	private ExpenseReportsAdapter adapter;
	private Handler inputFinishHandler;
	private FragmentManager manager;
	private FragmentTransaction transaction;
	private ListView lvExpenseReports;
	private RelativeLayout rlPlaceholderExpenseReports;
	private SQLiteAdapter db;
	private String search;
	private long lastEdit;

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
		View view = inflater.inflate(R.layout.expense_reports_layout, container, false);
		etSearchExpenseReports = (CodePanTextField) view.findViewById(R.id.etSearchExpenseReports);
		lvExpenseReports = (ListView) view.findViewById(R.id.lvExpenseReports);
		rlPlaceholderExpenseReports = (RelativeLayout) view.findViewById(R.id.rlPlaceholderExpenseReports);
		etSearchExpenseReports.addTextChangedListener(new TextWatcher() {
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
		lvExpenseReports.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				ExpenseReportsDetailsFragment expenseReportsDetails = new ExpenseReportsDetailsFragment();
				transaction = manager.beginTransaction();
				transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
						R.anim.slide_in_ltr, R.anim.slide_out_ltr);
				transaction.add(R.id.rlMain, expenseReportsDetails);
				transaction.addToBackStack(null);
				transaction.commit();
			}
		});
		search = "";
		loadExpenseReports(db);
		return view;
	}

	@Override
	public void onClick(View v) {
	}

	private Runnable inputFinishChecker = new Runnable() {
		@Override
		public void run() {
			if(System.currentTimeMillis() > lastEdit + IDLE_TIME - 500) {
				loadExpenseReports(db);
			}
		}
	};

	public void addExpenseReport() {
		AddExpenseReportFragment addExpenseReport = new AddExpenseReportFragment();
		transaction = manager.beginTransaction();
		transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
				R.anim.slide_in_ltr, R.anim.slide_out_ltr);
		transaction.add(R.id.rlMain, addExpenseReport);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void loadExpenseReports(final SQLiteAdapter db) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					expenseReportsList = Data.loadExpenseReports(db, search);
					handler.sendMessage(handler.obtainMessage());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	Handler handler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message message) {
			if(expenseReportsList.size() == 0) {
				rlPlaceholderExpenseReports.setVisibility(View.VISIBLE);
			}
			else {
				rlPlaceholderExpenseReports.setVisibility(View.GONE);
			}
			adapter = new ExpenseReportsAdapter(getActivity(), expenseReportsList);
			lvExpenseReports.setAdapter(adapter);
			return true;
		}
	});
}