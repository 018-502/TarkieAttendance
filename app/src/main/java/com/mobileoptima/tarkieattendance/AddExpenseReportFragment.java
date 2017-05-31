package com.mobileoptima.tarkieattendance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.codepan.database.SQLiteAdapter;
import com.codepan.widget.CodePanButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.text.NumberFormat;

public class AddExpenseReportFragment extends Fragment implements OnClickListener {
	private CodePanButton btnBackAddExpenseReport, btnSaveAddExpenseReport;
	private DisplayImageOptions options;
	private FragmentManager manager;
	private FragmentTransaction transaction;
	private ImageLoader imageLoader;
	private NumberFormat nf;
	private SQLiteAdapter db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivity main = (MainActivity) getActivity();
		manager = main.getSupportFragmentManager();
		nf = NumberFormat.getInstance();
		nf.setGroupingUsed(true);
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
		db = main.getDatabase();
		db.openConnection();
		this.imageLoader = ImageLoader.getInstance();
		if(!imageLoader.isInited()) {
			imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
		}
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_user_placeholder)
				.showImageForEmptyUri(R.drawable.ic_user_placeholder)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.build();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.add_expense_report_layout, container, false);
		btnBackAddExpenseReport = (CodePanButton) view.findViewById(R.id.btnBackAddExpenseReport);
		btnSaveAddExpenseReport = (CodePanButton) view.findViewById(R.id.btnSaveAddExpenseReport);
		btnBackAddExpenseReport.setOnClickListener(this);
		btnSaveAddExpenseReport.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnBackAddExpenseReport:
				manager.popBackStack();
				break;
			case R.id.btnSaveAddExpenseReport:
				break;
		}
	}
}