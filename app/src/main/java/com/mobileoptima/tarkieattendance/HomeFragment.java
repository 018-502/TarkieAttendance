package com.mobileoptima.tarkieattendance;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
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

import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.utils.SpannableMap;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.callback.Interface.OnSaveEntryCallback;
import com.mobileoptima.constant.InventoryType;
import com.mobileoptima.constant.Tag;
import com.mobileoptima.core.Data;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.model.FormObj;
import com.mobileoptima.model.InventoryObj;
import com.mobileoptima.model.StoreObj;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

	private LinearLayout llInventoryHome, llFormHome;
	private ArrayList<InventoryObj> inventoryList;
	private OnSaveEntryCallback saveEntryCallback;
	private OnOverrideCallback overrideCallback;
	private FragmentTransaction transaction;
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
		llFormHome = (LinearLayout) view.findViewById(R.id.llFormHome);
		tvStoreHome = (CodePanLabel) view.findViewById(R.id.tvStoreHome);
		setStore(TarkieLib.getDefaultStore(db));
		loadItems(db);
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

	public void loadItems(final SQLiteAdapter db) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					formList = Data.loadForms(db);
					inventoryList = Data.loadInventory(db);
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
		public boolean handleMessage(Message message) {
			llInventoryHome.removeAllViews();
			llFormHome.removeAllViews();
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View view = getView();
			if(view != null) {
				ViewGroup container = (ViewGroup) view.getParent();
				for(final InventoryObj obj : inventoryList) {
					View child = inflater.inflate(R.layout.home_list_item, container, false);
					CodePanLabel tvNameHome = (CodePanLabel) child.findViewById(R.id.tvNameHome);
					CodePanButton btnItemHome = (CodePanButton) child.findViewById(R.id.btnItemHome);
					ImageView ivLogoHome = (ImageView) child.findViewById(R.id.ivLogoHome);
					switch(obj.type) {
						case InventoryType.TRACKING:
							ivLogoHome.setImageResource(R.drawable.ic_tracking);
							break;
						case InventoryType.ORDERS:
							ivLogoHome.setImageResource(R.drawable.ic_orders);
							break;
						case InventoryType.PULL_OUTS:
							ivLogoHome.setImageResource(R.drawable.ic_pull_out);
							break;
					}
					btnItemHome.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							upgrade();
						}
					});
					if(inventoryList.indexOf(obj) == inventoryList.size() - 1) {
						btnItemHome.setBackgroundResource(R.drawable.state_rect_trans_rad_bot_five);
					}
					tvNameHome.setText(obj.name);
					llInventoryHome.addView(child);
				}
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
			}
			((MainActivity) getActivity()).checkBreakIn();
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
