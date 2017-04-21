package com.mobileoptima.tarkieattendance;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanTextField;
import com.mobileoptima.adapter.StoresAdapter;
import com.mobileoptima.callback.Interface.OnSelectStoreCallback;
import com.mobileoptima.core.Data;
import com.mobileoptima.model.StoreObj;

import java.util.ArrayList;

public class StoresFragment extends Fragment implements OnClickListener {

	private final int LIMIT = 30;
	private final long IDLE_TIME = 500;

	private boolean isEnd, isPause, isPending, isAdded;
	private OnSelectStoreCallback selectStoreCallback;
	private int visibleItem, totalItem, firstVisible;
	private OnFragmentCallback fragmentCallback;
	private FragmentTransaction transaction;
	private CodePanTextField etSearchStores;
	private ArrayList<StoreObj> storeList;
	private CodePanButton btnBackStores;
	private Handler inputFinishHandler;
	private ImageView ivLoadingStores;
	private FragmentManager manager;
	private StoresAdapter adapter;
	private String search, start;
	private ListView lvStores;
	private SQLiteAdapter db;
	private Animation anim;
	private long lastEdit;

	@Override
	public void onResume() {
		super.onResume();
		if(isPending) {
			setStore(isAdded);
			isPending = false;
		}
		isPause = false;
	}

	@Override
	public void onPause() {
		super.onPause();
		isPause = true;
	}

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
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
		db.openConnection();
		inputFinishHandler = new Handler();
		anim = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_clockwise);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.stores_layout, container, false);
		etSearchStores = (CodePanTextField) view.findViewById(R.id.etSearchStores);
		btnBackStores = (CodePanButton) view.findViewById(R.id.btnBackStores);
		ivLoadingStores = (ImageView) view.findViewById(R.id.ivLoadingStores);
		lvStores = (ListView) view.findViewById(R.id.lvStores);
		btnBackStores.setOnClickListener(this);
		lvStores.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				StoreObj obj = storeList.get(position);
				CodePanUtils.hideKeyboard(etSearchStores, getActivity());
				if(selectStoreCallback != null) {
					selectStoreCallback.onSelectStore(obj);
					manager.popBackStack();
				}
			}
		});
		lvStores.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch(scrollState) {
					case SCROLL_STATE_TOUCH_SCROLL:
						etSearchStores.clearFocus();
						CodePanUtils.hideKeyboard(etSearchStores, getActivity());
						break;
					case SCROLL_STATE_IDLE:
						if(firstVisible == totalItem - visibleItem & !isEnd) {
							loadMoreStores(db, search);
						}
						break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
								 int visibleItemCount, int totalItemCount) {
				firstVisible = firstVisibleItem;
				visibleItem = visibleItemCount;
				totalItem = totalItemCount;
			}
		});
		etSearchStores.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				search = s.toString();
				lastEdit = System.currentTimeMillis();
				inputFinishHandler.removeCallbacks(inputFinishChecker);
				inputFinishHandler.postDelayed(inputFinishChecker, IDLE_TIME);
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		loadStores(db, search);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.btnBackStores:
				manager.popBackStack();
				break;
		}
	}

	public void setOnSelectStoreCallback(OnSelectStoreCallback selectStoreCallback) {
		this.selectStoreCallback = selectStoreCallback;
	}

	public void loadStores(final SQLiteAdapter db, final String search) {
		lvStores.setEnabled(false);
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					storeList = Data.loadStores(db, search, null, false, LIMIT);
					if(storeList.size() < LIMIT) {
						isEnd = true;
						start = null;
					}
					else {
						isEnd = false;
						int lastPosition = storeList.size() - 1;
						start = storeList.get(lastPosition).name;
					}
					loadStoreHandler.sendMessage(loadStoreHandler.obtainMessage());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	Handler loadStoreHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			lvStores.setEnabled(true);
			if(!isPause) {
				setStore(false);
			}
			else {
				isPending = true;
			}
			return true;
		}
	});

	public void loadMoreStores(final SQLiteAdapter db, final String search) {
		ivLoadingStores.startAnimation(anim);
		ivLoadingStores.setVisibility(View.VISIBLE);
		lvStores.setEnabled(false);
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					ArrayList<StoreObj> additionalList = Data.loadStores(db, search, start, true, LIMIT);
					if(additionalList.get(0).isHeader) {
						StoreObj storeObj = storeList.get(storeList.size() - 1);
						storeObj.isFooter = true;
						storeObj.footer = additionalList.get(0).header;
					}
					storeList.addAll(additionalList);
					if(additionalList.size() < LIMIT) {
						isEnd = true;
						start = null;
					}
					else {
						isEnd = false;
						int lastPosition = additionalList.size() - 1;
						start = additionalList.get(lastPosition).name;
					}
					Thread.sleep(500);
					loadMoreStoresHandler.sendMessage(loadMoreStoresHandler.obtainMessage());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	Handler loadMoreStoresHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			ivLoadingStores.clearAnimation();
			ivLoadingStores.setVisibility(View.GONE);
			lvStores.setEnabled(true);
			if(!isPause) {
				setStore(true);
			}
			else {
				isPending = true;
				isAdded = true;
			}
			return true;
		}
	});

	public void setStore(boolean isAdded) {
		if(isAdded) {
			adapter.notifyDataSetChanged();
			lvStores.invalidate();
		}
		else {
			adapter = new StoresAdapter(getActivity(), storeList);
			lvStores.setAdapter(adapter);
		}
	}

	private Runnable inputFinishChecker = new Runnable() {
		@Override
		public void run() {
			if(System.currentTimeMillis() > lastEdit + IDLE_TIME - 500) {
				loadStores(db, search);
			}
		}
	};

	public void setOnBackStack(boolean isOnBackStack) {
		if(fragmentCallback != null) {
			fragmentCallback.onFragment(isOnBackStack);
		}
	}
}
