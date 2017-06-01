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
import android.widget.ListView;

import com.codepan.callback.Interface;
import com.codepan.database.SQLiteAdapter;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.adapter.StoreDetailsAdapter;
import com.mobileoptima.core.Data;
import com.mobileoptima.model.ContactObj;
import com.mobileoptima.model.StoreObj;

import java.util.ArrayList;

public class StoreDetailsFragment extends Fragment implements OnClickListener, Interface.OnRefreshCallback {

	private CodePanLabel tvPlaceholderStoreDetails, tvAddressStoreDetails, tvNameStoreDetails;
	private ArrayList<ContactObj> contactList;
	private FragmentTransaction transaction;
	private StoreDetailsAdapter adapter;
	private FragmentManager manager;
	private ListView lvContacts;
	private SQLiteAdapter db;
	private StoreObj store;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivity main = (MainActivity) getActivity();
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.store_details_layout, container, false);
		tvPlaceholderStoreDetails = (CodePanLabel) view.findViewById(R.id.tvPlaceholderStoreDetails);
		tvAddressStoreDetails = (CodePanLabel) view.findViewById(R.id.tvAddressStoreDetails);
		tvNameStoreDetails = (CodePanLabel) view.findViewById(R.id.tvNameStoreDetails);
		lvContacts = (ListView) view.findViewById(R.id.lvStoreDetails);
		view.findViewById(R.id.btnAddContactStoreDetails).setOnClickListener(this);
		view.findViewById(R.id.btnBackStoreDetails).setOnClickListener(this);
		if(store != null) {
			tvNameStoreDetails.setText(store.name);
			tvAddressStoreDetails.setText(store.address);
		}
		loadContacts(db);
		return view;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnAddContactStoreDetails:
				AddContactFragment addContact = new AddContactFragment();
				addContact.setStore(store);
				addContact.setOnRefreshCallback(this);
				transaction = manager.beginTransaction();
				transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
						R.anim.slide_in_ltr, R.anim.slide_out_ltr);
				transaction.add(R.id.rlMain, addContact);
				transaction.hide(this);
				transaction.addToBackStack(null);
				transaction.commit();
				break;
			case R.id.btnBackStoreDetails:
				manager.popBackStack();
				break;
		}
	}

	public void loadContacts(final SQLiteAdapter db) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					contactList = Data.loadContacts(db, store.ID);
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
		public boolean handleMessage(Message msg) {
			if(!contactList.isEmpty()) {
				tvPlaceholderStoreDetails.setVisibility(View.INVISIBLE);
				adapter = new StoreDetailsAdapter(getActivity(), contactList);
				lvContacts.setAdapter(adapter);
			}
			else {
				tvPlaceholderStoreDetails.setVisibility(View.VISIBLE);
			}
			return true;
		}
	});

	@Override
	public void onRefresh() {
		loadContacts(db);
	}

	public void setStore(StoreObj store) {
		this.store = store;
	}
}
