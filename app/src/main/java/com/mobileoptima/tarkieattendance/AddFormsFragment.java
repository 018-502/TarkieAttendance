package com.mobileoptima.tarkieattendance;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.widget.CodePanButton;
import com.mobileoptima.adapter.AddFormsAdapter;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.callback.Interface.OnTagFormsCallback;
import com.mobileoptima.core.Data;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.FormObj;
import com.mobileoptima.model.VisitObj;

import java.util.ArrayList;

public class AddFormsFragment extends Fragment implements OnClickListener, OnFragmentCallback,
		OnBackPressedCallback {

	private CodePanButton btnSaveAddForms, btnBackAddForms;
	private OnTagFormsCallback tagFormsCallback;
	private OnOverrideCallback overrideCallback;
	private OnFragmentCallback fragmentCallback;
	private FragmentTransaction transaction;
	private ArrayList<FormObj> taggedList;
	private ArrayList<FormObj> formList;
	private FragmentManager manager;
	private AddFormsAdapter adapter;
	private boolean withChanges;
	private ListView lvAddForms;
	private SQLiteAdapter db;
	private VisitObj visit;

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
		MainActivity main = (MainActivity) getActivity();
		main.setOnBackPressedCallback(this);
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
		db.openConnection();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.add_forms_layout, container, false);
		btnSaveAddForms = (CodePanButton) view.findViewById(R.id.btnSaveAddForms);
		btnBackAddForms = (CodePanButton) view.findViewById(R.id.btnBackAddForms);
		lvAddForms = (ListView) view.findViewById(R.id.lvAddForms);
		btnSaveAddForms.setOnClickListener(this);
		btnBackAddForms.setOnClickListener(this);
		lvAddForms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				FormObj obj = formList.get(i);
				CheckBox cbAddForms = (CheckBox) view.findViewById(R.id.cbAddForms);
				if(!obj.isTagged) {
					obj.isChecked = !obj.isChecked;
					obj.hasChanged = !obj.hasChanged;
					cbAddForms.setChecked(obj.isChecked);
					if(!withChanges) {
						withChanges = true;
						if(overrideCallback != null) {
							overrideCallback.onOverride(true);
						}
					}
				}
				else {
					TarkieLib.alertDialog(getActivity(), R.string.untag_forms_title,
							R.string.untag_forms_message, AddFormsFragment.this);
				}
			}
		});
		loadForms(db);
		return view;
	}

	public void loadForms(final SQLiteAdapter db) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					formList = Data.loadForms(db, visit.ID);
					for(FormObj form : formList) {
						for(FormObj tagged : taggedList) {
							if(form.ID.equals(tagged.ID)) {
								form.isChecked = tagged.isChecked;
								break;
							}
						}
					}
					handler.sendMessage(handler.obtainMessage());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	Handler handler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message message) {
			adapter = new AddFormsAdapter(getActivity(), formList);
			lvAddForms.setAdapter(adapter);
			return false;
		}
	});

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnBackAddForms:
				onBackPressed();
				break;
			case R.id.btnSaveAddForms:
				manager.popBackStack();
				save();
				break;
		}
	}

	public void setVisit(VisitObj visit) {
		this.visit = visit;
	}

	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}

	@Override
	public void onFragment(boolean status) {
		if(overrideCallback != null) {
			overrideCallback.onOverride(!status);
		}
	}

	@Override
	public void onBackPressed() {
		if(withChanges) {
			AlertDialogFragment alert = new AlertDialogFragment();
			alert.setDialogTitle(R.string.save_changes_title);
			alert.setDialogMessage(R.string.save_changes_message);
			alert.setOnFragmentCallback(this);
			alert.setPositiveButton("Yes", new OnClickListener() {
				@Override
				public void onClick(View view) {
					manager.popBackStack();
					manager.popBackStack();
					save();
				}
			});
			alert.setNegativeButton("Discard", new OnClickListener() {
				@Override
				public void onClick(View view) {
					manager.popBackStack();
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
		else {
			manager.popBackStack();
		}
	}

	public void save() {
		ArrayList<FormObj> taggedList = new ArrayList<>();
		for(FormObj form : formList) {
			if(form.hasChanged) {
				taggedList.add(form);
			}
		}
		if(tagFormsCallback != null) {
			tagFormsCallback.onTagForms(taggedList);
		}
	}

	public void setOnTagFormsCallback(OnTagFormsCallback tagFormsCallback) {
		this.tagFormsCallback = tagFormsCallback;
	}

	public void setTaggedList(ArrayList<FormObj> taggedList) {
		this.taggedList = taggedList;
	}

	private void setOnBackStack(boolean isOnBackStack) {
		if(fragmentCallback != null) {
			fragmentCallback.onFragment(isOnBackStack);
		}
	}

	public void setOnFragmentCallback(OnFragmentCallback fragmentCallback) {
		this.fragmentCallback = fragmentCallback;
	}
}
