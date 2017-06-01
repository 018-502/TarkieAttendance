package com.mobileoptima.tarkieattendance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.codepan.callback.Interface.OnRefreshCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.CodePanTextField;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.ContactObj;
import com.mobileoptima.model.StoreObj;

public class AddContactFragment extends Fragment implements OnClickListener {

	private CodePanTextField etDesignationAddContact, etNameAddContact, etMobileAddContact,
			etLandlineAddContact, etEmailAddContact, etRemarksAddContact;
	private CodePanButton btnBackContacts, btnAddContact;
	private OnRefreshCallback refreshCallback;
	private CodePanLabel tvNameAddContact;
	private FragmentManager manager;
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
		View view = inflater.inflate(R.layout.add_contact_layout, container, false);
		btnBackContacts = (CodePanButton) view.findViewById(R.id.btnBackStoreDetails);
		btnAddContact = (CodePanButton) view.findViewById(R.id.btnAddContactStoreDetails);
		tvNameAddContact = (CodePanLabel) view.findViewById(R.id.tvNameAddContact);
		etNameAddContact = (CodePanTextField) view.findViewById(R.id.etNameAddContact);
		etDesignationAddContact = (CodePanTextField) view.findViewById(R.id.etDesignationAddContact);
		etMobileAddContact = (CodePanTextField) view.findViewById(R.id.etMobileAddContact);
		etLandlineAddContact = (CodePanTextField) view.findViewById(R.id.etLandlineAddContact);
		etEmailAddContact = (CodePanTextField) view.findViewById(R.id.etEmailAddContact);
		etRemarksAddContact = (CodePanTextField) view.findViewById(R.id.etRemarksAddContact);
		btnBackContacts.setOnClickListener(this);
		btnAddContact.setOnClickListener(this);
		String name = getString(R.string.name);
		TarkieLib.requiredField(tvNameAddContact, name);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.btnBackStoreDetails:
				manager.popBackStack();
				break;
			case R.id.btnAddContactStoreDetails:
				ContactObj contact = new ContactObj();
				contact.store = store;
				contact.name = etNameAddContact.getText().toString();
				contact.position = etDesignationAddContact.getText().toString();
				contact.mobile = etMobileAddContact.getText().toString();
				contact.landline = etLandlineAddContact.getText().toString();
				contact.email = etEmailAddContact.getText().toString();
				contact.remarks = etRemarksAddContact.getText().toString();
				boolean result = TarkieLib.addContact(db, contact);
				if(result && refreshCallback != null) {
					refreshCallback.onRefresh();
				}
				break;
		}
	}

	public void setOnRefreshCallback(OnRefreshCallback refreshCallback) {
		this.refreshCallback = refreshCallback;
	}

	public void setStore(StoreObj store) {
		this.store = store;
	}
}
