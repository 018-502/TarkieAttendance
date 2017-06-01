package com.mobileoptima.tarkieattendance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.codepan.callback.Interface.OnRefreshCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.CodePanTextField;
import com.mobileoptima.constant.Convention;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.StoreObj;

import org.apache.commons.lang3.StringUtils;

public class AddStoreFragment extends Fragment implements OnClickListener {

	private CodePanTextField tfCompName, tfAddress;
	private MainActivity main;
	private FragmentManager manager;
	private FragmentTransaction transaction;
	private SQLiteAdapter db;
	private OnRefreshCallback refreshCallback;
	private boolean isShareWithMe;
	private boolean isShareWithTeam;
	private String conventionStore;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main = (MainActivity) getActivity();
		manager = main.getSupportFragmentManager();
		isShareWithTeam = false;
		isShareWithMe = false;
		db = main.getDatabase();
		db.openConnection();
		conventionStore = TarkieLib.getConvention(db, Convention.STORES);
		if(conventionStore != null) {
			conventionStore = StringUtils.capitalize(conventionStore);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.add_store_layout, container, false);
		CodePanLabel tvCompName = (CodePanLabel) view.findViewById(R.id.tvCompName);
		CodePanLabel tvTitle = (CodePanLabel) view.findViewById(R.id.tvTitle);
		tfCompName = (CodePanTextField) view.findViewById(R.id.tfCompName);
		tfAddress = (CodePanTextField) view.findViewById(R.id.tfAddress);
		view.findViewById(R.id.btnHeaderSave).setOnClickListener(this);
		view.findViewById(R.id.btnSave).setOnClickListener(this);
		view.findViewById(R.id.btnCancel).setOnClickListener(this);
		TarkieLib.requiredField(tvCompName, getResources().getString(R.string.company_name));
		if(conventionStore != null && !conventionStore.isEmpty()) {
			String title = "Add " + conventionStore;
			tvTitle.setText(title);
		}
		return view;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.btnSave:
				if(!isValidated()) {
					TarkieLib.alertDialog(main, "Required Field", "Please complete required fields.");
				}
				else {
					showAlert(true);
				}
				break;
			case R.id.btnCancel:
				showAlert(false);
				break;
		}
	}

	public boolean isValidated() {
		if(tfCompName.getText().toString().isEmpty()) {
			return false;
		}
		return true;
	}

	public void showAlert(boolean isSave) {
		final AlertDialogFragment alert = new AlertDialogFragment();
		if(isSave) {
			alert.setDialogTitle("Save client");
			alert.setDialogMessage("Are you sure you want to add this Client?");
			alert.setPositiveButton("Yes", new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					StoreObj store = new StoreObj();
					store.name = tfCompName.getText().toString();
					store.address = tfAddress.getText().toString();
					if(TarkieLib.addStore(db, store)) {
						manager.popBackStack();
						manager.popBackStack();
						CodePanUtils.alertToast(main, "You have successfully added " + store.name);
						if(refreshCallback != null) {
							refreshCallback.onRefresh();
						}
					}
				}
			});
		}
		else {
			alert.setDialogTitle("Cancel");
			alert.setDialogMessage("All Details will be lost. Are you sure you want to cancel?");
			alert.setPositiveButton("Yes", new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					manager.popBackStack();
					manager.popBackStack();
				}
			});
		}
		alert.setNegativeButton("No", new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				manager.popBackStack();
			}
		});
		transaction = manager.beginTransaction();
		transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
				R.anim.fade_in, R.anim.fade_out);
		transaction.add(R.id.rlMain, alert);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void setOnRefreshCallback(OnRefreshCallback refreshCallback) {
		this.refreshCallback = refreshCallback;
	}
}
