package com.mobileoptima.tarkieattendance;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.codepan.callback.Interface.OnRefreshCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.utils.SpannableMap;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.callback.Interface.OnSaveEntryCallback;
import com.mobileoptima.constant.Tag;
import com.mobileoptima.core.Data;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.FormObj;
import com.mobileoptima.model.StoreObj;
import com.mobileoptima.model.VisitObj;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

	private LinearLayout llInventoryHome, llScheduleHome, llFormHome;
	private OnSaveEntryCallback saveEntryCallback;
	private OnOverrideCallback overrideCallback;
	private FragmentTransaction transaction;
	private ArrayList<VisitObj> visitList;
	private DisplayImageOptions options;
	private ArrayList<FormObj> formList;
	private CodePanLabel tvStoreHome;
	private FragmentManager manager;
	private ImageLoader imageLoader;
	private SQLiteAdapter db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imageLoader = ImageLoader.getInstance();
		if(!imageLoader.isInited()) {
			imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
		}
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.color.gray_sep)
				.showImageForEmptyUri(R.color.gray_sep)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.build();
		MainActivity main = (MainActivity) getActivity();
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
		db.openConnection();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.home_layout, container, false);
		llInventoryHome = (LinearLayout) view.findViewById(R.id.llInventoryHome);
		llScheduleHome = (LinearLayout) view.findViewById(R.id.llScheduleHome);
		llFormHome = (LinearLayout) view.findViewById(R.id.llFormHome);
		tvStoreHome = (CodePanLabel) view.findViewById(R.id.tvStoreHome);
		setStore(TarkieLib.getDefaultStore(db));
		loadSchedule(db);
		loadForms(db);
		return view;
	}

	public void setStore(StoreObj store) {
		if(tvStoreHome != null) {
			if(store != null) {
				String bold = getActivity().getString(R.string.proxima_nova_bold);
				String at = getActivity().getString(R.string.you_are_at);
				String text = at + " " + store.name;
				ArrayList<SpannableMap> mapList = new ArrayList<>();
				mapList.add(new SpannableMap(getActivity(), bold, at.length(), text.length()));
				SpannableStringBuilder ssb = CodePanUtils.customizeText(mapList, text);
				tvStoreHome.setVisibility(View.VISIBLE);
				tvStoreHome.setText(ssb);
			}
			else {
				tvStoreHome.setVisibility(View.GONE);
			}
		}
	}

	public void loadSchedule(final SQLiteAdapter db) {
		final String date = CodePanUtils.getDate();
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					visitList = Data.loadVisits(db, date, true);
					scheduleHandler.sendMessage(scheduleHandler.obtainMessage());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	Handler scheduleHandler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message message) {
			llScheduleHome.removeAllViews();
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View view = getView();
			if(view != null) {
				ViewGroup container = (ViewGroup) view.getParent();
				for(final VisitObj visit : visitList) {
					View child = inflater.inflate(R.layout.schedule_list_item, container, false);
					CodePanLabel tvNameSchedule = (CodePanLabel) child.findViewById(R.id.tvNameSchedule);
					CodePanLabel tvAddressSchedule = (CodePanLabel) child.findViewById(R.id.tvAddressSchedule);
					CodePanButton btnItemSchedule = (CodePanButton) child.findViewById(R.id.btnItemSchedule);
					tvNameSchedule.setText(visit.name);
					if(visit.store != null) {
						StoreObj store = visit.store;
						tvAddressSchedule.setText(store.address);
					}
					btnItemSchedule.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							VisitDetailsFragment details = new VisitDetailsFragment();
							details.setVisit(visit);
							details.setOnOverrideCallback(overrideCallback);
							details.setOnRefreshCallback(new OnRefreshCallback() {
								@Override
								public void onRefresh() {
									MainActivity main = (MainActivity) getActivity();
									main.reloadVisits();
									loadSchedule(db);
								}
							});
							transaction = manager.beginTransaction();
							transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
									R.anim.slide_in_ltr, R.anim.slide_out_ltr);
							transaction.add(R.id.rlMain, details);
							transaction.addToBackStack(null);
							transaction.commit();
						}
					});
					llScheduleHome.addView(child);
				}
				MainActivity main = (MainActivity) getActivity();
				main.checkBreakIn();
			}
			return true;
		}
	});

	public void loadForms(final SQLiteAdapter db) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					formList = Data.loadForms(db);
					formHandler.sendMessage(formHandler.obtainMessage());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	Handler formHandler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message message) {
			llFormHome.removeAllViews();
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View view = getView();
			if(view != null) {
				ViewGroup container = (ViewGroup) view.getParent();
				for(final FormObj obj : formList) {
					View child = inflater.inflate(R.layout.home_list_item, container, false);
					CodePanLabel tvNameHome = (CodePanLabel) child.findViewById(R.id.tvNameHome);
					CodePanButton btnItemHome = (CodePanButton) child.findViewById(R.id.btnItemHome);
					ImageView ivLogoHome = (ImageView) child.findViewById(R.id.ivLogoHome);
					imageLoader.displayImage(obj.logoUrl, ivLogoHome, options);
					btnItemHome.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							if(TarkieLib.isTimeIn(db)) {
								FormFragment form = new FormFragment();
								form.setForm(obj);
								form.setOnOverrideCallback(overrideCallback);
								form.setOnSaveEntryCallback(saveEntryCallback);
								transaction = manager.beginTransaction();
								transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
										R.anim.slide_in_ltr, R.anim.slide_out_ltr);
								transaction.replace(R.id.rlMain, form, Tag.FORM);
								transaction.addToBackStack(null);
								transaction.commit();
							}
							else {
								Resources res = getResources();
								String message = res.getString(R.string.time_in_required_form_message);
								String bold = res.getString(R.string.proxima_nova_bold);
								ArrayList<SpannableMap> list = new ArrayList<>();
								int start = message.indexOf("'");
								int end = message.length() - 1;
								list.add(new SpannableMap(getActivity(), bold, start, end));
								final AlertDialogFragment alert = new AlertDialogFragment();
								alert.setDialogTitle(R.string.time_in_required_title);
								alert.setDialogMessage(R.string.time_in_required_form_message);
								alert.setSpannableList(list);
								alert.setPositiveButton("OK", new View.OnClickListener() {
									@Override
									public void onClick(View view) {
										manager.popBackStack();
										MainActivity main = (MainActivity) getActivity();
										main.openMenu();
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
					});
					if(formList.indexOf(obj) == formList.size() - 1) {
						btnItemHome.setBackgroundResource(R.drawable.state_rect_trans_rad_bot_five);
					}
					tvNameHome.setText(obj.name);
					llFormHome.addView(child);
				}
				MainActivity main = (MainActivity) getActivity();
				main.checkBreakIn();
			}
			return true;
		}
	});

	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}

	public void setOnSaveEntryCallback(OnSaveEntryCallback saveEntryCallback) {
		this.saveEntryCallback = saveEntryCallback;
	}

	public void upgrade() {
		UpgradeFragment upgrade = new UpgradeFragment();
		transaction = manager.beginTransaction();
		transaction.add(R.id.flContainerMain, upgrade);
		transaction.hide(this);
		transaction.addToBackStack(null);
		transaction.commit();
	}
}
