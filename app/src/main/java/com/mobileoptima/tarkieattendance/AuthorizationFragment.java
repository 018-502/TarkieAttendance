package com.mobileoptima.tarkieattendance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.callback.Interface.OnRefreshCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanTextField;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.constant.Key;
import com.mobileoptima.constant.Module.Action;

public class AuthorizationFragment extends Fragment implements OnClickListener, OnRefreshCallback,
		OnBackPressedCallback, OnFragmentCallback {

	private CodePanTextField etCodeAuthorization;
	private OnOverrideCallback overrideCallback;
	private OnRefreshCallback refreshCallback;
	private CodePanButton btnAuthorization;
	private FragmentManager manager;
	private boolean inOtherFragment;
	private SQLiteAdapter db;

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
		View view = inflater.inflate(R.layout.authorization_layout, container, false);
		etCodeAuthorization = (CodePanTextField) view.findViewById(R.id.etCodeAuthorization);
		btnAuthorization = (CodePanButton) view.findViewById(R.id.btnAuthorization);
		btnAuthorization.setOnClickListener(this);
		etCodeAuthorization.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE) {
					btnAuthorization.performClick();
				}
				return false;
			}
		});
		return view;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.btnAuthorization:
				String authorizationCode = etCodeAuthorization.getText().toString().trim();
				if(!authorizationCode.isEmpty()) {
					if(CodePanUtils.hasInternet(getActivity())) {
						LoadingDialogFragment loading = new LoadingDialogFragment();
						Bundle bundle = new Bundle();
						bundle.putString(Key.AUTH_CODE, authorizationCode);
						loading.setArguments(bundle);
						loading.setAction(Action.AUTHORIZE_DEVICE);
						loading.setOnRefreshCallback(this);
						loading.setOnFragmentCallback(this);
						loading.setOnOverrideCallback(overrideCallback);
						FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
						transaction.add(R.id.rlMain, loading);
						transaction.addToBackStack(null);
						transaction.commit();
					}
					else {
						CodePanUtils.alertToast(getActivity(), "Internet connection required.");
					}
				}
				else {
					CodePanUtils.alertToast(getActivity(), "Please input authorization code.");
				}
				break;
		}
	}

	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}

	public void setOnRefreshCallback(OnRefreshCallback refreshCallback) {
		this.refreshCallback = refreshCallback;
	}

	@Override
	public void onRefresh() {
		manager.popBackStack();
		if(refreshCallback != null) {
			refreshCallback.onRefresh();
		}
	}

	@Override
	public void onBackPressed() {
		if(!inOtherFragment) {
			getActivity().finish();
		}
		else {
			manager.popBackStack();
		}
	}

	@Override
	public void onFragment(boolean status) {
		this.inOtherFragment = status;
	}

	private void setOnBackStack(boolean isOnBackStack) {
		if(overrideCallback != null) {
			overrideCallback.onOverride(isOnBackStack);
		}
	}
}