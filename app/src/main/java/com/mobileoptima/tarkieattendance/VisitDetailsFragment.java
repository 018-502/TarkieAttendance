package com.mobileoptima.tarkieattendance;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.codepan.database.SQLiteAdapter;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.CodePanTextField;
import com.mobileoptima.core.Data;
import com.mobileoptima.model.FormObj;
import com.mobileoptima.model.StoreObj;
import com.mobileoptima.model.TaskObj;

import java.util.ArrayList;

public class VisitDetailsFragment extends Fragment {

	private CodePanLabel tvStoreVisitDetails, tvAddressVisitDetails;
	private CodePanTextField etNotesVisitDetails;
	private LinearLayout llFormsVisitDetails;
	private ArrayList<FormObj> formList;
	private ViewGroup container;
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
		etNotesVisitDetails = (CodePanTextField) view.findViewById(R.id.etNotesVisitDetails);
		llFormsVisitDetails = (LinearLayout) view.findViewById(R.id.llFormsVisitDetails);
		this.container = container;
		if(task != null) {
			StoreObj store = task.store;
			if(store != null) {
				tvStoreVisitDetails.setText(store.name);
				tvAddressVisitDetails.setText(store.address);
			}
			etNotesVisitDetails.setText(task.notes);
			loadForms(db, task.ID);
		}
		return view;
	}

	public void loadForms(final SQLiteAdapter db, final String taskID) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					formList = Data.loadForms(db, taskID);
					formsHandler.sendMessage(formsHandler.obtainMessage());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	Handler formsHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message message) {
			LayoutInflater inflater = getActivity().getLayoutInflater();
			for(FormObj form: formList) {
				View view = inflater.inflate(R.layout.visit_details_form_item, container, false);
				CodePanLabel tvVisitDetailsForm = (CodePanLabel) view.findViewById(R.id.tvVisitDetailsForm);
				CodePanButton btnVisitDetailsForm = (CodePanButton) view.findViewById(R.id.btnVisitDetailsForm);
				tvVisitDetailsForm.setText(form.name);
				btnVisitDetailsForm.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
					}
				});
				llFormsVisitDetails.addView(view);
			}
			return true;
		}
	});

	public void setTask(TaskObj task) {
		this.task = task;
	}
}
