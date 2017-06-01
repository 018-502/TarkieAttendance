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

import com.codepan.calendar.callback.Interface.OnPickDateCallback;
import com.codepan.calendar.view.CalendarView;
import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.callback.Interface.OnRefreshCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.CodePanTextField;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.ContactObj;
import com.mobileoptima.model.StoreObj;

public class AddContactFragment extends Fragment implements OnClickListener, OnPickDateCallback,
		TextWatcher, OnBackPressedCallback, OnFragmentCallback {

	private CodePanTextField etDesignationAddContact, etNameAddContact, etMobileAddContact,
			etLandlineAddContact, etEmailAddContact, etRemarksAddContact;
	private CodePanButton btnBackContacts, btnAddContact, btnBirthdateAddContact;
	private OnOverrideCallback overrideCallback;
	private OnRefreshCallback refreshCallback;
	private CodePanLabel tvNameAddContact;
	private FragmentManager manager;
	private FragmentTransaction transaction;
	private MainActivity main;
	private SQLiteAdapter db;
	private StoreObj store;
	private String birthday, email;
	private boolean withChanges;

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
		btnBirthdateAddContact = (CodePanButton) view.findViewById(R.id.btnBirthdateAddContact);
		btnBackContacts.setOnClickListener(this);
		btnAddContact.setOnClickListener(this);
		btnBirthdateAddContact.setOnClickListener(this);
		etNameAddContact.addTextChangedListener(this);
		etDesignationAddContact.addTextChangedListener(this);
		etMobileAddContact.addTextChangedListener(this);
		etLandlineAddContact.addTextChangedListener(this);
		etEmailAddContact.addTextChangedListener(this);
		etRemarksAddContact.addTextChangedListener(this);
		String name = getString(R.string.name);
		TarkieLib.requiredField(tvNameAddContact, name);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.btnBackStoreDetails:
				onBackPressed();
				break;
			case R.id.btnAddContactStoreDetails:
				if(isNameEmpty()) {
					TarkieLib.alertDialog(main, "Required Field", "Please fill in required field.");
					return;
				}
				if(!isValidEmail()) {
					TarkieLib.alertDialog(main, "Invalid Email", "Please enter a valid email.");
					return;
				}
				showAlert(true);
				break;
			case R.id.btnBirthdateAddContact:
				showCalendar();
				break;
		}
	}

	public void setOnRefreshCallback(OnRefreshCallback refreshCallback) {
		this.refreshCallback = refreshCallback;
	}

	public void setStore(StoreObj store) {
		this.store = store;
	}

	public void showCalendar() {
		CalendarView calendar = new CalendarView();
		calendar.setOnFragmentCallback(this);
		calendar.setOnPickDateCallback(this);
		transaction = manager.beginTransaction();
		transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
				R.anim.fade_in, R.anim.fade_out);
		transaction.add(R.id.rlMain, calendar);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public void onPickDate(String date) {
		withChanges = true;
		birthday = date;
		btnBirthdateAddContact.setText(birthday);
	}

	public boolean isNameEmpty() {
		if(etNameAddContact.getText().toString().isEmpty()) {
			return true;
		}
		return false;
	}

	public boolean isValidEmail() {
		email = etEmailAddContact.getText().toString();
		if(email.isEmpty()) {
			return true;
		}
		if(!CodePanUtils.isValidEmail(email)) {
			return false;
		}
		return true;
	}

	public void saveContact() {
		String name = etNameAddContact.getText().toString();
		ContactObj contact = new ContactObj();
		contact.store = store;
		contact.name = name;
		contact.designation = etDesignationAddContact.getText().toString();
		contact.mobile = etMobileAddContact.getText().toString();
		contact.landline = etLandlineAddContact.getText().toString();
		contact.email = email;
		contact.birthday = birthday;
		contact.remarks = etRemarksAddContact.getText().toString();
		boolean result = TarkieLib.addContact(db, contact);
		manager.popBackStack();
		CodePanUtils.alertToast(main, "You have successfully added\n" + name);
		if(result && refreshCallback != null) {
			refreshCallback.onRefresh();
		}
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

	public void showAlert(boolean isSave) {
		final AlertDialogFragment alert = new AlertDialogFragment();
		alert.setOnFragmentCallback(this);
		if(isSave) {
			alert.setDialogTitle("Save Client");
			alert.setDialogMessage("Are you sure you want to add this contact?");
			alert.setPositiveButton("Yes", new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					saveContact();
				}
			});
		}
		else {
			alert.setDialogTitle("Discard Changes");
			alert.setDialogMessage("All changes will be lost. Are you sure you want to cancel?");
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
