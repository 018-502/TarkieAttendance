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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.mobileoptima.adapter.EntriesAdapter;
import com.mobileoptima.callback.Interface.OnHighlightEntriesCallback;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.constant.Tag;
import com.mobileoptima.core.Data;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.EntryObj;

import java.util.ArrayList;

public class EntriesFragment extends Fragment implements OnFragmentCallback, OnBackPressedCallback {

	private OnHighlightEntriesCallback highlightEntriesCallback;
	private boolean isHighlight, inOtherFragment;
	private OnOverrideCallback overrideCallback;
	private FragmentTransaction transaction;
	private ArrayList<EntryObj> entryList;
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
		lvEntries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				EntryObj obj = entryList.get(i);
				if(!obj.isHighlight) {
					if(!obj.isSubmit) {
						FormFragment form = new FormFragment();
						form.setEntry(obj);
						form.setOnOverrideCallback(overrideCallback);
						transaction = getActivity().getSupportFragmentManager().beginTransaction();
						transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
								R.anim.slide_in_ltr, R.anim.slide_out_ltr);
						transaction.replace(R.id.rlMain, form, Tag.FORM);
						transaction.addToBackStack(null);
						transaction.commit();
					}
					else {
						CodePanUtils.alertToast(getActivity(), "This entry has already been submitted.");
					}
				}
				else {
					if(!TarkieLib.hasUnfilledUpFields(db, obj.ID)) {
						obj.isCheck = true;
						lvEntries.invalidate();
						adapter.notifyDataSetChanged();
					}
					else {
						TarkieLib.alertDialog(getActivity(), R.string.incomplete_title, R.string.incomplete_message);
					}
				}
			}
		});
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

	public void select(boolean isHighlight) {
		for(EntryObj obj : entryList) {
			obj.isHighlight = isHighlight;
			if(!isHighlight) {
				obj.isCheck = false;
			}
		}
		if(overrideCallback != null) {
			overrideCallback.onOverride(isHighlight);
		}
		if(highlightEntriesCallback != null) {
			highlightEntriesCallback.onHighlightEntries(isHighlight);
		}
		this.isHighlight = isHighlight;
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
			alert.setPositiveButton("Yes", new View.OnClickListener() {
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
						CodePanUtils.alertToast(getActivity(), "Entries has been successfully submitted.");
					}
					else {
						CodePanUtils.alertToast(getActivity(), "Error submitting entries.");
					}
				}
			});
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
		else {
			CodePanUtils.alertToast(getActivity(), "Please select an entry.");
		}
	}

	@Override
	public void onFragment(boolean status) {
		this.inOtherFragment = status;
	}

	@Override
	public void onBackPressed() {
		if(inOtherFragment) {
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
}
