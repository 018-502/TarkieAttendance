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
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.CodePanTextField;
import com.mobileoptima.callback.Interface.OnAddStoreCallback;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.constant.Convention;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.StoreObj;

import org.apache.commons.lang3.StringUtils;

public class AddStoreFragment extends Fragment implements OnClickListener, TextWatcher,
		OnBackPressedCallback, OnFragmentCallback {

	private boolean isShareWithTeam, isShareWithMe, withChanges;
	private CodePanTextField etNameAddStore, etAddressAddStore;
	private OnOverrideCallback overrideCallback;
	private OnAddStoreCallback addStoreCallback;
	private FragmentTransaction transaction;
	private FragmentManager manager;
	private String convention;
	private MainActivity main;
	private SQLiteAdapter db;

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
		db = main.getDatabase();
		db.openConnection();
		convention = TarkieLib.getConvention(db, Convention.STORES);
		if(convention != null) {
			convention = StringUtils.capitalize(convention);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.add_store_layout, container, false);
		CodePanLabel tvNameAddStore = (CodePanLabel) view.findViewById(R.id.tvNameAddStore);
		CodePanLabel tvTitleAddStore = (CodePanLabel) view.findViewById(R.id.tvTitleAddStore);
		etAddressAddStore = (CodePanTextField) view.findViewById(R.id.etAddressAddStore);
		etNameAddStore = (CodePanTextField) view.findViewById(R.id.etNameAddStore);
		view.findViewById(R.id.btnSaveAddStore).setOnClickListener(this);
		view.findViewById(R.id.btnBackStoreDetails).setOnClickListener(this);
		view.findViewById(R.id.cbMeAddStore).setOnClickListener(this);
		view.findViewById(R.id.cbTeamAddStore).setOnClickListener(this);
		etNameAddStore.addTextChangedListener(this);
		etAddressAddStore.addTextChangedListener(this);
		String name = getString(R.string.company_name);
		TarkieLib.requiredField(tvNameAddStore, name);
		if(convention != null && !convention.isEmpty()) {
			String title = "Add " + convention;
			tvTitleAddStore.setText(title);
		}
		return view;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.btnSaveAddStore:
				if(!isValidated()) {
					TarkieLib.alertDialog(main, "Required Field",
							"Please complete required fields.", this);
				}
				else {
					showAlert(true);
				}
				break;
			case R.id.btnBackStoreDetails:
				onBackPressed();
				break;
			case R.id.cbMeAddStore:
				withChanges = true;
				break;
			case R.id.cbTeamAddStore:
				withChanges = true;
				break;
		}
	}

	public boolean isValidated() {
		return !etNameAddStore.getText().toString().isEmpty();
	}

	public void showAlert(boolean isSave) {
		final AlertDialogFragment alert = new AlertDialogFragment();
		alert.setOnFragmentCallback(this);
		if(isSave) {
			alert.setDialogTitle("Save " + convention);
			alert.setDialogMessage("Are you sure you want to add this " + convention + "?");
			alert.setPositiveButton("Yes", new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					StoreObj store = new StoreObj();
					store.name = etNameAddStore.getText().toString();
					store.address = etAddressAddStore.getText().toString();
					if(TarkieLib.addStore(db, store)) {
						manager.popBackStack();
						manager.popBackStack();
						CodePanUtils.alertToast(main, "You have successfully added\n" + store.name);
						if(addStoreCallback != null) {
							addStoreCallback.onAddStore(store);
						}
					}
				}
			});
		}
		else {
			alert.setDialogTitle(R.string.discard_changes_title);
			alert.setDialogMessage(R.string.discard_changes_message);
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

	public void setOnAddStoreCallback(OnAddStoreCallback addStoreCallback) {
		this.addStoreCallback = addStoreCallback;
	}
}
