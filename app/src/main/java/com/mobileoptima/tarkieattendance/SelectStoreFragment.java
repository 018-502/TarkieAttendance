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
import com.codepan.model.GpsObj;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.callback.Interface.OnSelectStoreCallback;
import com.mobileoptima.constant.Convention;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.StoreObj;

public class SelectStoreFragment extends Fragment implements OnClickListener, OnSelectStoreCallback {

	private CodePanButton btnSelectStore, btnCancelStore, btnOkStore;
	private OnSelectStoreCallback selectStoreCallback;
	private FragmentTransaction transaction;
	private FragmentManager manager;
	private CodePanLabel tvNameForm;
	private String convention;
	private SQLiteAdapter db;
	private StoreObj store;
	private GpsObj gps;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivity main = (MainActivity) getActivity();
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
		db.openConnection();
		convention = TarkieLib.getConvention(db, Convention.STORES);
		if(convention != null) {
			convention = convention.toUpperCase();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.select_store_layout, container, false);
		tvNameForm = (CodePanLabel) view.findViewById(R.id.tvNameForm);
		btnCancelStore = (CodePanButton) view.findViewById(R.id.btnCancelStore);
		btnSelectStore = (CodePanButton) view.findViewById(R.id.btnSelectStore);
		btnOkStore = (CodePanButton) view.findViewById(R.id.btnOkStore);
		btnSelectStore.setOnClickListener(this);
		btnCancelStore.setOnClickListener(this);
		btnOkStore.setOnClickListener(this);
		StoreObj store = TarkieLib.getDefaultStore(db);
		if(store != null) {
			btnSelectStore.setText(store.name);
			this.store = store;
		}
		if(convention != null) {
			String title = "SELECT " + convention;
			tvNameForm.setText(title);
		}
		return view;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnSelectStore:
				StoresFragment stores = new StoresFragment();
				stores.setOnSelectStoreCallback(this);
				transaction = manager.beginTransaction();
				transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
						R.anim.slide_in_ltr, R.anim.slide_out_ltr);
				transaction.add(R.id.rlMain, stores);
				transaction.hide(this);
				transaction.addToBackStack(null);
				transaction.commit();
				break;
			case R.id.btnOkStore:
				if(store != null) {
					boolean result = TarkieLib.setDefaultStore(db, store.ID);
					if(result && selectStoreCallback != null) {
						selectStoreCallback.onSelectStore(store);
					}
				}
				else {
					CodePanUtils.alertToast(getActivity(), "Please select a store.");
				}
				break;
			case R.id.btnCancelStore:
				manager.popBackStack();
				break;
		}
	}

	public void setGps(GpsObj gps) {
		this.gps = gps;
	}

	@Override
	public void onSelectStore(StoreObj store) {
		manager.popBackStack();
		btnSelectStore.setText(store.name);
		this.store = store;
	}

	public void setOnSelectStoreCallback(OnSelectStoreCallback selectStoreCallback) {
		this.selectStoreCallback = selectStoreCallback;
	}
}
