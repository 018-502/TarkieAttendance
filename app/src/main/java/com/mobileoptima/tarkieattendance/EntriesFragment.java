package com.mobileoptima.tarkieattendance;

import android.graphics.Color;
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
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.utils.SpannableMap;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.adapter.EntriesAdapter;
import com.mobileoptima.callback.Interface.OnHighlightEntriesCallback;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.callback.Interface.OnSaveEntryCallback;
import com.mobileoptima.constant.Tag;
import com.mobileoptima.core.Data;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.EntryObj;
import com.mobileoptima.model.SearchObj;

import java.util.ArrayList;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

public class EntriesFragment extends Fragment implements OnClickListener, OnFragmentCallback,
		OnBackPressedCallback, OnSaveEntryCallback {

	private OnHighlightEntriesCallback highlightEntriesCallback;
	private boolean isHighlight, inOtherFragment, isMultiple;
	private CodePanButton btnSelectEntries, btnBackEntries;
	private OnOverrideCallback overrideCallback;
	private FragmentTransaction transaction;
	private RelativeLayout rlHeaderEntries;
	private ArrayList<EntryObj> entryList;
	private CodePanLabel tvTitleEntries;
	private FragmentManager manager;
	private EntriesAdapter adapter;
	private ListView lvEntries;
	private SearchObj search;
	private SQLiteAdapter db;
	private int type;

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
		View view = inflater.inflate(R.layout.entries_layout, container, false);
		lvEntries = (ListView) view.findViewById(R.id.lvEntries);
		tvTitleEntries = (CodePanLabel) view.findViewById(R.id.tvTitleEntries);
		btnBackEntries = (CodePanButton) view.findViewById(R.id.btnBackEntries);
		btnSelectEntries = (CodePanButton) view.findViewById(R.id.btnSelectEntries);
		rlHeaderEntries = (RelativeLayout) view.findViewById(R.id.rlHeaderEntries);
		btnBackEntries.setOnClickListener(this);
		btnSelectEntries.setOnClickListener(this);
		lvEntries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				EntryObj obj = entryList.get(i);
				if(!obj.isHighlight) {
					if(!obj.isSubmit) {
						FormFragment form = new FormFragment();
						form.setEntry(obj);
						form.setOnFragmentCallback(EntriesFragment.this);
						form.setOnSaveEntryCallback(EntriesFragment.this);
						form.setOnOverrideCallback(overrideCallback);
						transaction = manager.beginTransaction();
						transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
								R.anim.slide_in_ltr, R.anim.slide_out_ltr);
						transaction.add(R.id.rlMain, form, Tag.FORM);
						transaction.hide(EntriesFragment.this);
						transaction.addToBackStack(null);
						transaction.commit();
					}
					else {
						CodePanUtils.alertToast(getActivity(), "This entry has already been submitted.");
					}
				}
				else {
					if(!TarkieLib.hasUnfilledUpFields(db, obj.ID)) {
						obj.isCheck = !obj.isCheck;
						lvEntries.invalidate();
						adapter.notifyDataSetChanged();
					}
					else {
						ArrayList<SpannableMap> spannableList = new ArrayList<>();
						String message = getString(R.string.incomplete_message);
						int index = message.indexOf("*");
						spannableList.add(new SpannableMap(index, index + 1, Color.RED));
						final AlertDialogFragment alert = new AlertDialogFragment();
						alert.setDialogTitle(R.string.incomplete_title);
						alert.setDialogMessage(R.string.incomplete_message);
						alert.setSpannableList(spannableList);
						alert.setOnFragmentCallback(EntriesFragment.this);
						alert.setPositiveButton("OK", new View.OnClickListener() {
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
				}
			}
		});
		if(search != null) {
			rlHeaderEntries.setVisibility(View.VISIBLE);
			tvTitleEntries.setText(search.name);
		}
		else {
			rlHeaderEntries.setVisibility(View.GONE);
		}
		loadEntries(db);
		return view;
	}

	public void loadEntries(final SQLiteAdapter db) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					entryList = Data.loadEntries(db, search, type);
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
			adapter = new EntriesAdapter(getActivity(), entryList);
			lvEntries.setAdapter(adapter);
			return true;
		}
	});

	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}

	public void setOnHighlightEntriesCallback(OnHighlightEntriesCallback highlightEntriesCallback) {
		this.highlightEntriesCallback = highlightEntriesCallback;
	}

	public void setSearch(SearchObj search) {
		this.search = search;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void select(boolean isHighlight) {
		boolean hasDraft = false;
		for(EntryObj obj : entryList) {
			if(!hasDraft) hasDraft = !obj.isSubmit;
			obj.isHighlight = isHighlight;
			if(!isHighlight) {
				obj.isCheck = false;
			}
		}
		if(!hasDraft && isHighlight) {
			for(EntryObj obj : entryList) {
				obj.isHighlight = false;
				obj.isCheck = false;
			}
			CodePanUtils.alertToast(getActivity(), "No available entries.");
		}
		else {
			if(overrideCallback != null) {
				overrideCallback.onOverride(isHighlight);
			}
			this.isHighlight = isHighlight;
			if(highlightEntriesCallback != null) {
				highlightEntriesCallback.onHighlightEntries(isHighlight);
			}
			if(search != null) {
				if(isHighlight) {
					btnSelectEntries.setText(R.string.submit);
				}
				else {
					btnSelectEntries.setText(R.string.select);
				}
			}
		}
		lvEntries.invalidate();
		adapter.notifyDataSetChanged();
	}

	public boolean hasSelected() {
		this.isMultiple = false;
		boolean hasSelected = false;
		int count = 0;
		for(EntryObj obj : entryList) {
			if(obj.isCheck) {
				hasSelected = true;
				count++;
				if(count > 1) {
					isMultiple = true;
					break;
				}
			}
		}
		return hasSelected;
	}

	public boolean isHighlight() {
		return this.isHighlight;
	}

	public void submit() {
		if(hasSelected()) {
			final AlertDialogFragment alert = new AlertDialogFragment();
			String title = "Submit " + (isMultiple ? "Entries" : "Entry");
			String message = "Once you submit " + (isMultiple ? "these entries" : "this entry") + " " +
					"you can no longer edit " + (isMultiple ? "them" : "it") + ".\n" +
					"Are you sure you want to submit?";
			alert.setDialogTitle(title);
			alert.setDialogMessage(message);
			alert.setOnFragmentCallback(this);
			alert.setPositiveButton("Yes", new OnClickListener() {
				@Override
				public void onClick(View view) {
					manager.popBackStack();
					boolean result = false;
					for(EntryObj obj : entryList) {
						if(obj.isCheck) {
							result = TarkieLib.submitEntry(db, obj.ID);
							if(result) {
								obj.isSubmit = true;
							}
							else {
								break;
							}
						}
					}
					if(result) {
						select(false);
						MainActivity main = (MainActivity) getActivity();
						main.updateSyncCount();
						if(search != null) {
							main.reloadEntries();
						}
						CodePanUtils.alertToast(getActivity(), "Entries has been successfully submitted.");
					}
					else {
						CodePanUtils.alertToast(getActivity(), "Error submitting entries.");
					}
				}
			});
			alert.setNegativeButton("Cancel", new OnClickListener() {
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
		else {
			CodePanUtils.alertToast(getActivity(), "Please select an entry.");
		}
	}

	@Override
	public void onFragment(boolean status) {
		this.inOtherFragment = status;
		if(!status) {
			MainActivity main = (MainActivity) getActivity();
			main.setOnBackPressedCallback(this);
		}
	}

	@Override
	public void onBackPressed() {
		int count = manager.getBackStackEntryCount();
		if((count > 0 && search == null) || inOtherFragment) {
			manager.popBackStack();
		}
		else {
			if(isHighlight) {
				select(false);
			}
			else {
				getActivity().onBackPressed();
			}
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(!hidden) {
			MainActivity main = (MainActivity) getActivity();
			main.setOnBackPressedCallback(this);
		}
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnBackEntries:
				onBackPressed();
				break;
			case R.id.btnSelectEntries:
				if(!isHighlight) {
					select(true);
				}
				else {
					submit();
				}
				break;
		}
	}

	@Override
	public void onSaveEntry() {
		MainActivity main = (MainActivity) getActivity();
		main.updateSyncCount();
		main.reloadEntries();
		main.reloadPhotos();
		if(search != null) {
			manager.popBackStack();
		}
		else {
			manager.popBackStack(null, POP_BACK_STACK_INCLUSIVE);
		}
	}
}
