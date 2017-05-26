package com.mobileoptima.tarkieattendance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.CodePanTextField;
import com.mobileoptima.callback.Interface.OnSelectStatusCallback;
import com.mobileoptima.constant.Settings;
import com.mobileoptima.constant.TaskStatus;
import com.mobileoptima.core.Data;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.StoreObj;
import com.mobileoptima.model.TaskStatusObj;

import java.util.ArrayList;

public class VisitStatusFragment extends Fragment implements OnClickListener {

	private CodePanButton btnOkVisitStatus, btnCancelVisitStatus;
	private OnSelectStatusCallback selectStatusCallback;
	private CodePanTextField etNotesVisitStatus;
	private ArrayList<TaskStatusObj> statusList;
	private boolean hasNotes, isNotesRequired;
	private CodePanLabel tvStoreVisitStatus;
	private ArrayAdapter<String> adapter;
	private Spinner spinVisitStatus;
	private FragmentManager manager;
	private TaskStatusObj status;
	private SQLiteAdapter db;
	private StoreObj store;

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
		View view = inflater.inflate(R.layout.visit_status_layout, container, false);
		btnOkVisitStatus = (CodePanButton) view.findViewById(R.id.btnOkVisitStatus);
		etNotesVisitStatus = (CodePanTextField) view.findViewById(R.id.etNotesVisitStatus);
		btnCancelVisitStatus = (CodePanButton) view.findViewById(R.id.btnCancelVisitStatus);
		tvStoreVisitStatus = (CodePanLabel) view.findViewById(R.id.tvStoreVisitStatus);
		spinVisitStatus = (Spinner) view.findViewById(R.id.spinVisitStatus);
		btnOkVisitStatus.setOnClickListener(this);
		btnCancelVisitStatus.setOnClickListener(this);
		if(store != null) {
			tvStoreVisitStatus.setText(store.name);
		}
		statusList = Data.loadStatus(db);
		adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_selected_item);
		adapter.setDropDownViewResource(R.layout.spinner_selection_item);
		for(TaskStatusObj status : statusList) {
			adapter.add(status.name);
		}
		spinVisitStatus.setAdapter(adapter);
		spinVisitStatus.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				status = statusList.get(i);
				if(!hasNotes) {
					if(TarkieLib.isSettingsEnabled(db, Settings.NOTES_FOR_INCOMPLETE_VISITS)
							&& status.code.equals(TaskStatus.INCOMPLETE)) {
						if(etNotesVisitStatus.getVisibility() == View.GONE) {
							CodePanUtils.expandView(etNotesVisitStatus, true);
						}
						isNotesRequired = true;
					}
					else {
						etNotesVisitStatus.setText(null);
						if(etNotesVisitStatus.getVisibility() == View.VISIBLE) {
							CodePanUtils.collapseView(etNotesVisitStatus, true);
						}
						isNotesRequired = false;
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
			}
		});
		return view;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnCancelVisitStatus:
				manager.popBackStack();
				break;
			case R.id.btnOkVisitStatus:
				if(status != null) {
					String notes = etNotesVisitStatus.getText().toString().trim();
					notes = notes.replace("'", "''");
					if(hasNotes || !isNotesRequired || !notes.isEmpty()) {
						manager.popBackStack();
						status.notes = notes;
						if(selectStatusCallback != null) {
							selectStatusCallback.onSelectStatus(status);
						}
					}
					else {
						CodePanUtils.alertToast(getActivity(), "Notes is required.");
					}
				}
				else {
					CodePanUtils.alertToast(getActivity(), "Please select status.");
				}
				break;
		}
	}

	public void setStore(StoreObj store) {
		this.store = store;
	}

	public void setOnSelectStatusCallback(OnSelectStatusCallback selectStatusCallback) {
		this.selectStatusCallback = selectStatusCallback;
	}

	public void setHasNotes(boolean hasNotes) {
		this.hasNotes = hasNotes;
	}
}
