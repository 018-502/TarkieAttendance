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
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.mobileoptima.adapter.EntriesAdapter;
import com.mobileoptima.callback.Interface.OnHighlightEntriesCallback;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.constant.Tag;
import com.mobileoptima.core.Data;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.EntryObj;

import java.util.ArrayList;

public class EntriesFragment extends Fragment implements OnClickListener, OnFragmentCallback,
		OnBackPressedCallback {

	private OnHighlightEntriesCallback highlightEntriesCallback;
	private CodePanButton btnSelectEntries, btnBackEntries;
	private OnOverrideCallback overrideCallback;
	private FragmentTransaction transaction;
	private RelativeLayout rlHeaderEntries;
	private ArrayList<EntryObj> entryList;
	private boolean isHighlight, isSearch;
	private FragmentManager manager;
	private EntriesAdapter adapter;
	private ListView lvEntries;
	private SQLiteAdapter db;

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
						form.setOnOverrideCallback(overrideCallback);
						transaction = getActivity().getSupportFragmentManager().beginTransaction();
						transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
								R.anim.slide_in_ltr, R.anim.slide_out_ltr);
						transaction.add(R.id.rlMain, form, Tag.FORM);
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
						TarkieLib.alertDialog(getActivity(), R.string.incomplete_title,
								R.string.incomplete_message, EntriesFragment.this);
					}
				}
			}
		});
		if(isSearch) {
			rlHeaderEntries.setVisibility(View.VISIBLE);
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
					entryList = Data.loadEntries(db);
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

	public void setIsSearch(boolean isSearch) {
		this.isSearch = isSearch;
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
		}
		lvEntries.invalidate();
		adapter.notifyDataSetChanged();
	}

	public boolean hasSelected() {
		for(EntryObj obj : entryList) {
			if(obj.isCheck) {
				return true;
			}
		}
		return false;
	}

	public boolean isHighlight() {
		return this.isHighlight;
	}

	public void submit() {
		if(hasSelected()) {
			final AlertDialogFragment alert = new AlertDialogFragment();
			alert.setDialogTitle(R.string.submit_entries_title);
			alert.setDialogMessage(R.string.submit_entries_message);
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
		if(!status) {
			MainActivity main = (MainActivity) getActivity();
			main.setOnBackPressedCallback(this);
		}
	}

	@Override
	public void onBackPressed() {
		int count = manager.getBackStackEntryCount();
		if(count > 0) {
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
				if(isHighlight) {
					select(true);
				}
				else {
					submit();
				}
				break;
		}
	}
}
