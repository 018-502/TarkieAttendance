package com.mobileoptima.tarkieattendance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.callback.Interface.OnRefreshCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.CodePanTextField;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.constant.Convention;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.StoreObj;

import org.apache.commons.lang3.StringUtils;

public class AddStoreFragment extends Fragment implements OnClickListener, TextWatcher, OnBackPressedCallback, OnFragmentCallback {

	private CodePanTextField etCompName, etAddress;
	private MainActivity main;
	private FragmentManager manager;
	private FragmentTransaction transaction;
	private SQLiteAdapter db;
	private OnRefreshCallback refreshCallback;
	private boolean isShareWithMe;
	private boolean isShareWithTeam;
	private String conventionStore;
	private boolean withChanges;
	private OnOverrideCallback overrideCallback;

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
		main = (MainActivity) getActivity();
		main.setOnBackPressedCallback(this);
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
		etCompName = (CodePanTextField) view.findViewById(R.id.etCompNameAddStore);
		etAddress = (CodePanTextField) view.findViewById(R.id.etAddressAddStore);
		view.findViewById(R.id.btnHeaderSave).setOnClickListener(this);
		view.findViewById(R.id.btnBackStoreDetails).setOnClickListener(this);
		view.findViewById(R.id.cbMe).setOnClickListener(this);
		view.findViewById(R.id.cbTeam).setOnClickListener(this);
		TarkieLib.requiredField(tvCompName, getResources().getString(R.string.company_name));
		if(conventionStore != null && !conventionStore.isEmpty()) {
			String title = "Add " + conventionStore;
			tvTitle.setText(title);
		}
		etCompName.addTextChangedListener(this);
		etAddress.addTextChangedListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.btnHeaderSave:
				if(!isValidated()) {
					TarkieLib.alertDialog(main, "Required Field", "Please complete required fields.");
					return;
				}
				showAlert(true);
				break;
			case R.id.btnBackStoreDetails:
				onBackPressed();
				break;
			case R.id.cbMe:
				withChanges = true;
				break;
			case R.id.cbTeam:
				withChanges = true;
				break;
		}
	}

	public boolean isValidated() {
		if(etCompName.getText().toString().isEmpty()) {
			return false;
		}
		return true;
	}

	public void showAlert(boolean isSave) {
		final AlertDialogFragment alert = new AlertDialogFragment();
		alert.setOnFragmentCallback(this);
		if(isSave) {
			alert.setDialogTitle("Save client");
			alert.setDialogMessage("Are you sure you want to add this Client?");
			alert.setPositiveButton("Yes", new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					StoreObj store = new StoreObj();
					store.name = etCompName.getText().toString();
					store.address = etAddress.getText().toString();
					if(TarkieLib.addStore(db, store)) {
						manager.popBackStack();
						manager.popBackStack();
						CodePanUtils.alertToast(main, "You have successfully added\n" + store.name);
						if(refreshCallback != null) {
							refreshCallback.onRefresh();
						}
					}
				}
			});
		}
		else {
			alert.setDialogTitle("Discard Changes");
			alert.setDialogMessage("All Details will be lost. Are you sure you want to cancel?");
			alert.setPositiveButton("Yes", new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					manager.popBackStack();
					manager.popBackStack();
				}
			});
		}
		alert.setNegativeButton("Cancel", new View.OnClickListener() {
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

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void afterTextChanged(Editable s) {
		withChanges = true;
	}

	@Override
	public void onBackPressed() {
		if(!withChanges) {
			manager.popBackStack();
		}
		else {
			showAlert(false);
		}
	}

	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}

	private void setOnBackStack(boolean isOnBackStack) {
		if(overrideCallback != null) {
			overrideCallback.onOverride(isOnBackStack);
		}
	}

	@Override
	public void onFragment(boolean status) {
		if(overrideCallback != null) {
			overrideCallback.onOverride(!status);
		}
	}
}
