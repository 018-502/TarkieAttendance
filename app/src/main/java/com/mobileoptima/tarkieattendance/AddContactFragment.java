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

	private FragmentManager manager;
	private CodePanTextField tfName;
	private CodePanTextField tfDesignation;
	private CodePanTextField tfMobile;
	private CodePanTextField tfTel;
	private CodePanTextField tfEmail;
	private CodePanTextField tfBirthday;
	private CodePanTextField tfRemarks;
	private SQLiteAdapter db;
	private OnRefreshCallback refreshCallback;
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
		CodePanButton btnBackContacts = (CodePanButton) view.findViewById(R.id.btnBackContacts);
		CodePanButton btnAddContact = (CodePanButton) view.findViewById(R.id.btnAddContact);
		CodePanLabel lblName = (CodePanLabel) view.findViewById(R.id.lblName);
		btnBackContacts.setOnClickListener(this);
		btnAddContact.setOnClickListener(this);
		TarkieLib.requiredField(lblName, "Name");
		tfName = (CodePanTextField) view.findViewById(R.id.tfName);
		tfDesignation = (CodePanTextField) view.findViewById(R.id.tfDesignation);
		tfMobile = (CodePanTextField) view.findViewById(R.id.tfMobile);
		tfTel = (CodePanTextField) view.findViewById(R.id.tfTel);
		tfEmail = (CodePanTextField) view.findViewById(R.id.tfEmail);
		tfBirthday = (CodePanTextField) view.findViewById(R.id.tfBirthday);
		tfRemarks = (CodePanTextField) view.findViewById(R.id.tfRemarks);//
		return view;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.btnBackContacts:
				manager.popBackStack();
				break;
			case R.id.btnAddContact:
				ContactObj contact = new ContactObj();
				contact.store = store;
				contact.name = tfName.getText().toString();
				contact.position = tfDesignation.getText().toString();
				contact.mobile = tfMobile.getText().toString();
				contact.landline = tfTel.getText().toString();
				contact.email = tfEmail.getText().toString();
				contact.birthday = tfBirthday.getText().toString();
				contact.remarks = tfRemarks.getText().toString();
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
