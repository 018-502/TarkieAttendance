package com.mobileoptima.tarkieattendance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.utils.SpannableMap;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.callback.Interface.OnDeleteEntryCallback;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.callback.Interface.OnSaveEntryCallback;
import com.mobileoptima.core.Data;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.EntryObj;
import com.mobileoptima.model.FieldObj;
import com.mobileoptima.model.FormObj;
import com.mobileoptima.model.PageObj;

import java.util.ArrayList;

public class FormFragment extends Fragment implements OnClickListener, OnBackPressedCallback,
		OnFragmentCallback {

	private CodePanButton btnNextForm, btnBackForm, btnSaveForm, btnCancelForm,
			btnDeleteForm, btnOptionsForm;
	private LinearLayout llPageForm, llDeleteForm;
	private OnDeleteEntryCallback deleteEntryCallback;
	private OnSaveEntryCallback saveEntryCallback;
	private OnFragmentCallback fragmentCallback;
	private OnOverrideCallback overrideCallback;
	private FragmentTransaction transaction;
	private RelativeLayout rlOptionsForm;
	private ArrayList<PageObj> pageList;
	private FrameLayout flOptionsForm;
	private FragmentManager manager;
	private CodePanLabel tvForm;
	private ViewGroup container;
	private SQLiteAdapter db;
	private EntryObj entry;
	private FormObj form;
	private String tag;
	private int index;

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
		pageList = Data.loadPages(db, form.ID);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.form_layout, container, false);
		rlOptionsForm = (RelativeLayout) view.findViewById(R.id.rlOptionsForm);
		btnCancelForm = (CodePanButton) view.findViewById(R.id.btnCancelForm);
		btnDeleteForm = (CodePanButton) view.findViewById(R.id.btnDeleteForm);
		btnOptionsForm = (CodePanButton) view.findViewById(R.id.btnOptionsForm);
		flOptionsForm = (FrameLayout) view.findViewById(R.id.flOptionsForm);
		btnNextForm = (CodePanButton) view.findViewById(R.id.btnNextForm);
		btnBackForm = (CodePanButton) view.findViewById(R.id.btnBackForm);
		btnSaveForm = (CodePanButton) view.findViewById(R.id.btnSaveForm);
		llDeleteForm = (LinearLayout) view.findViewById(R.id.llDeleteForm);
		llPageForm = (LinearLayout) view.findViewById(R.id.llPageForm);
		tvForm = (CodePanLabel) view.findViewById(R.id.tvNameForm);
		btnNextForm.setOnClickListener(this);
		btnBackForm.setOnClickListener(this);
		btnSaveForm.setOnClickListener(this);
		btnCancelForm.setOnClickListener(this);
		btnDeleteForm.setOnClickListener(this);
		btnOptionsForm.setOnClickListener(this);
		rlOptionsForm.setOnClickListener(this);
		tvForm.setText(form.name);
		if(!pageList.isEmpty()) {
			PageObj page = pageList.get(index);
			this.tag = page.tag;
			PageFragment first = new PageFragment();
			first.setPage(page);
			first.setForm(form);
			first.setEntry(entry);
			first.setOnOverrideCallback(overrideCallback);
			transaction = manager.beginTransaction();
			transaction.setCustomAnimations(0, R.anim.slide_out_rtl,
					R.anim.slide_in_ltr, R.anim.slide_out_ltr);
			transaction.add(R.id.flForm, first, page.tag);
			transaction.commit();
			if(pageList.size() == 1) {
				btnNextForm.setText(R.string.finish);
			}
		}
		if(entry != null) {
			if(entry.isSubmit) {
				flOptionsForm.setVisibility(View.INVISIBLE);
			}
		}
		else {
			llDeleteForm.setVisibility(View.GONE);
		}
		this.container = container;
		setProgress();
		return view;
	}

	public void setForm(FormObj form) {
		this.form = form;
	}

	public void setEntry(EntryObj entry) {
		this.form = entry.form;
		this.entry = entry;
	}

	public void setProgress() {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		for(PageObj obj : pageList) {
			View view = inflater.inflate(R.layout.progress_item, container, false);
			View vProgress = view.findViewById(R.id.vProgress);
			if(pageList.indexOf(obj) <= index) {
				vProgress.setEnabled(true);
			}
			else {
				vProgress.setEnabled(false);
			}
			llPageForm.addView(view);
		}
	}

	public void updateProgress() {
		int count = llPageForm.getChildCount();
		if(count > 0) {
			for(int i = 0; i < count; i++) {
				View view = llPageForm.getChildAt(i);
				if(view != null) {
					View vProgress = view.findViewById(R.id.vProgress);
					if(i <= index) {
						vProgress.setEnabled(true);
					}
					else {
						vProgress.setEnabled(false);
					}
				}
			}
		}
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnBackForm:
				onBackPressed();
				break;
			case R.id.btnNextForm:
				PageFragment current = (PageFragment) manager.findFragmentByTag(tag);
				if(current != null) {
					PageObj obj = getPage(tag);
					obj.fieldList = current.getFieldList();
					if(incrementPage()) {
						PageObj page = getPage(tag);
						PageFragment next = new PageFragment();
						next.setPage(page);
						next.setForm(form);
						next.setEntry(entry);
						next.setFieldList(page.fieldList);
						next.setOnOverrideCallback(overrideCallback);
						transaction = manager.beginTransaction();
						transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
								R.anim.slide_in_ltr, R.anim.slide_out_ltr);
						transaction.add(R.id.flForm, next, tag);
						transaction.hide(current);
						transaction.addToBackStack(null);
						transaction.commit();
					}
					else {
						if(entry == null || !entry.isSubmit) {
							boolean hasRequired = false;
							for(PageObj page : pageList) {
								final int position = pageList.indexOf(page);
								final int p = position + 1;
								if(position <= index) {
									FieldObj field = TarkieLib.getUnfilledUpField(page.fieldList);
									if(field != null) {
										final AlertDialogFragment alert = new AlertDialogFragment();
										alert.setDialogTitle("Required Field");
										if(field.name != null && !field.name.isEmpty()) {
											String message = "\"" + field.name + "\" is required on page " + p + ".";
											alert.setDialogMessage(message);
											String font = getActivity().getResources().getString(R.string.proxima_nova_bold);
											ArrayList<SpannableMap> list = new ArrayList<>();
											int length = field.name.length() + 2;
											list.add(new SpannableMap(getActivity(), font, 0, length));
											alert.setSpannableList(list);
										}
										alert.setOnFragmentCallback(this);
										alert.setPositiveButton("Page " + p, new OnClickListener() {
											@Override
											public void onClick(View view) {
												manager.popBackStack();
												int x = pageList.size() - position - 1;
												for(int i = 0; i < x; i++) {
													decrementPage();
													manager.popBackStack();
												}
											}
										});
										transaction = manager.beginTransaction();
										transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
												R.anim.fade_in, R.anim.fade_out);
										transaction.add(R.id.rlMain, alert);
										transaction.addToBackStack(null);
										transaction.commit();
										hasRequired = true;
										break;
									}
								}
							}
							if(!hasRequired) {
								final AlertDialogFragment alert = new AlertDialogFragment();
								alert.setDialogTitle(R.string.submit_entry_title);
								alert.setDialogMessage(R.string.submit_entry_message);
								alert.setOnFragmentCallback(this);
								alert.setPositiveButton("Submit", new OnClickListener() {
									@Override
									public void onClick(View view) {
										manager.popBackStack();
										saveEntry(true);
									}
								});
								alert.setNegativeButton("Save", new OnClickListener() {
									@Override
									public void onClick(View view) {
										manager.popBackStack();
										saveEntry(false);
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
						else {
							if(saveEntryCallback != null) {
								saveEntryCallback.onSaveEntry(entry);
							}
						}
					}
				}
				break;
			case R.id.btnOptionsForm:
				if(rlOptionsForm.getVisibility() == View.GONE) {
					CodePanUtils.fadeIn(rlOptionsForm);
				}
				else {
					CodePanUtils.fadeOut(rlOptionsForm);
				}
				break;
			case R.id.btnSaveForm:
				rlOptionsForm.performClick();
				saveEntry(false);
				break;
			case R.id.btnCancelForm:
				rlOptionsForm.performClick();
				cancelEntry();
				break;
			case R.id.btnDeleteForm:
				rlOptionsForm.performClick();
				deleteEntry();
				break;
			case R.id.rlOptionsForm:
				if(rlOptionsForm.getVisibility() == View.VISIBLE) {
					CodePanUtils.fadeOut(rlOptionsForm);
				}
				break;
		}
	}

	public void saveEntry(boolean isSubmit) {
		ArrayList<FieldObj> fieldList = new ArrayList<>();
		for(PageObj page : pageList) {
			if(page.fieldList != null) {
				fieldList.addAll(page.fieldList);
			}
			else {
				PageFragment fragment = (PageFragment) manager.findFragmentByTag(page.tag);
				if(fragment != null) {
					fieldList.addAll(fragment.getFieldList());
				}
			}
		}
		if(entry != null) {
			entry.isSubmit = isSubmit;
			boolean result = TarkieLib.updateEntry(db, entry.ID, fieldList, isSubmit);
			if(result) {
				CodePanUtils.alertToast(getActivity(), "Entry has been has successfully updated.");
			}
		}
		else {
			boolean result = TarkieLib.saveEntry(db, form.ID, fieldList, isSubmit);
			if(result) {
				CodePanUtils.alertToast(getActivity(), "Entry has been has successfully saved.");
			}
		}
		if(saveEntryCallback != null) {
			saveEntryCallback.onSaveEntry(entry);
		}
		popAllPages();
	}

	public void cancelEntry() {
		final AlertDialogFragment alert = new AlertDialogFragment();
		alert.setDialogTitle(R.string.discard_entry_title);
		alert.setDialogMessage(R.string.discard_entry_message);
		alert.setOnFragmentCallback(this);
		alert.setPositiveButton("Yes", new OnClickListener() {
			@Override
			public void onClick(View view) {
				manager.popBackStack();
				popAllPages();
			}
		});
		alert.setNegativeButton("No", new OnClickListener() {
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

	public void deleteEntry() {
		final AlertDialogFragment alert = new AlertDialogFragment();
		alert.setDialogTitle(R.string.delete_entry_title);
		alert.setDialogMessage(R.string.delete_entry_message);
		alert.setOnFragmentCallback(this);
		alert.setPositiveButton("Yes", new OnClickListener() {
			@Override
			public void onClick(View view) {
				manager.popBackStack();
				boolean result = TarkieLib.deleteEntry(db, entry.ID);
				if(result) {
					CodePanUtils.alertToast(getActivity(), "Entry has been has successfully deleted.");
				}
				if(deleteEntryCallback != null) {
					deleteEntryCallback.onDeleteEntry(entry);
				}
				popAllPages();
			}
		});
		alert.setNegativeButton("No", new OnClickListener() {
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

	public boolean incrementPage() {
		if(index < pageList.size() - 1) {
			this.index++;
			this.tag = pageList.get(index).tag;
			if(index == pageList.size() - 1) {
				btnNextForm.setText(R.string.finish);
			}
			updateProgress();
			return true;
		}
		return false;
	}

	public void decrementPage() {
		if(index != 0) {
			this.index--;
			this.tag = pageList.get(index).tag;
			btnNextForm.setText(R.string.next);
			updateProgress();
		}
	}

	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}

	public void setOnFragmentCallback(OnFragmentCallback fragmentCallback) {
		this.fragmentCallback = fragmentCallback;
	}

	public void setOnSaveEntryCallback(OnSaveEntryCallback saveEntryCallback) {
		this.saveEntryCallback = saveEntryCallback;
	}

	public void setOnDeleteEntryCallback(OnDeleteEntryCallback deleteEntryCallback) {
		this.deleteEntryCallback = deleteEntryCallback;
	}

	public PageObj getPage(String tag) {
		for(PageObj obj : pageList) {
			if(obj.tag.equals(tag)) {
				return obj;
			}
		}
		return null;
	}

	public void popAllPages() {
		for(PageObj page : pageList) {
			Fragment fragment = manager.findFragmentByTag(page.tag);
			if(fragment != null) {
				manager.popBackStack();
			}
		}
	}

	@Override
	public void onBackPressed() {
		PageFragment current = (PageFragment) manager.findFragmentByTag(tag);
		if(current.withChanges()) {
			PageObj obj = getPage(tag);
			if(obj != null) {
				obj.fieldList = current.getFieldList();
			}
		}
		manager.popBackStack();
		decrementPage();
	}

	public OnBackPressedCallback getOnBackPressedCallback() {
		return this;
	}

	@Override
	public void onFragment(boolean status) {
		if(overrideCallback != null) {
			overrideCallback.onOverride(!status);
		}
	}

	private void setOnBackStack(boolean isOnBackStack) {
		if(fragmentCallback != null) {
			fragmentCallback.onFragment(isOnBackStack);
		}
		if(overrideCallback != null) {
			overrideCallback.onOverride(isOnBackStack);
		}
	}
}
