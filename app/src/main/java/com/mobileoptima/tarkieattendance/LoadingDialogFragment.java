package com.mobileoptima.tarkieattendance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.callback.Interface.OnErrorCallback;
import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.ProgressWheel;
import com.mobileoptima.callback.Interface.OnMultiUpdateCallback;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.callback.Interface.OnResultCallback;
import com.mobileoptima.callback.Interface.OnTimeValidatedCallback;
import com.mobileoptima.constant.App;
import com.mobileoptima.constant.DialogTag;
import com.mobileoptima.constant.Key;
import com.mobileoptima.constant.Module;
import com.mobileoptima.constant.Module.Action;
import com.mobileoptima.core.Process;
import com.mobileoptima.core.TarkieLib;

import static com.codepan.callback.Interface.OnRefreshCallback;

public class LoadingDialogFragment extends Fragment implements OnErrorCallback,
		OnBackPressedCallback, OnResultCallback {

	private CodePanLabel tvTitleLoadingDialog, tvCountLoadingDialog;
	private OnTimeValidatedCallback timeValidationCallback;
	private String successMsg, failedMsg, error, message;
	private OnMultiUpdateCallback multiUpdateCallback;
	private OnFragmentCallback fragmentCallback;
	private OnOverrideCallback overrideCallback;
	private ProgressWheel progressLoadingDialog;
	private OnRefreshCallback refreshCallback;
	private FragmentTransaction transaction;
	private boolean isDone, isPause, result;
	private FragmentManager manager;
	private Process process;
	private int progress, max;
	private SQLiteAdapter db;
	private Action action;
	private float percent;
	private Bundle bundle;

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
	public void onPause() {
		super.onPause();
		isPause = true;
	}

	@Override
	public void onResume() {
		super.onResume();
		if(isPause && isDone) {
			showResult(message);
		}
		isPause = false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivity main = (MainActivity) getActivity();
		main.setOnBackPressedCallback(this);
		manager = main.getSupportFragmentManager();
		db = main.getDatabase();
		db.openConnection();
		bundle = this.getArguments();
		process = new Process(this);
		process.setOnErrorCallback(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.loading_dialog_layout, container, false);
		progressLoadingDialog = (ProgressWheel) view.findViewById(R.id.progressLoadingDialog);
		tvTitleLoadingDialog = (CodePanLabel) view.findViewById(R.id.tvTitleLoadingDialog);
		tvCountLoadingDialog = (CodePanLabel) view.findViewById(R.id.tvCountLoadingDialog);
		String title = null;
		switch(action) {
			case AUTHORIZE_DEVICE:
				setMax(9);
				successMsg = "Authorization successful.";
				failedMsg = "Failed to authorize the device.";
				title = "Authorizing Device";
				String authorizationCode = bundle.getString(Key.AUTH_CODE);
				String deviceID = CodePanUtils.getDeviceID(db.getContext());
				process.authorizeDevice(db, authorizationCode, deviceID);
				break;
			case LOGIN:
				setMax(6);
				successMsg = "Login successful.";
				failedMsg = "Failed to login.";
				title = "Validating Account";
				String username = bundle.getString(Key.USERNAME);
				String password = bundle.getString(Key.PASSWORD);
				process.login(db, username, password);
				break;
			case UPDATE_MASTER_FILE:
				setMax(11);
				successMsg = "Update master list successful.";
				failedMsg = "Failed to update master file.";
				title = "Updating Master File";
				process.updateMasterFile(db);
				break;
			case SEND_BACKUP:
				setMax(5);
				successMsg = "Send back-up successful.";
				failedMsg = "Failed to send back-up.";
				title = "Sending Back-up Data";
				String fileName = TarkieLib.getBackupFileName(db);
				process.sendBackUp(db, fileName);
				break;
			case SYNC_DATA:
				int count = TarkieLib.getCountSyncTotal(db);
				setMax(count + 2);
				successMsg = "Sync Data successful.";
				failedMsg = "Failed to sync data.";
				title = "Syncing Data";
				tvTitleLoadingDialog.setText(title);
				process.syncData(db);
				break;
			case VALIDATE_SERVER_TIME:
				setMax(1);
				successMsg = "Validation successful.";
				failedMsg = "Failed to validate time.";
				title = "Validating Date and Time";
				process.validateTime(db);
				break;
		}
		tvTitleLoadingDialog.setText(title);
		return view;
	}

	@Override
	public void onResult(boolean result) {
		this.result = result;
		if(result) {
			updateProgress();
			if(progress >= max) {
				message = successMsg;
				isDone = true;
				if(!isPause) {
					showResult(message);
				}
			}
			if(multiUpdateCallback != null) {
				multiUpdateCallback.onMultiUpdate();
			}
		}
		else {
			isDone = true;
			message = error != null ? error : failedMsg;
			if(!isPause) {
				showResult(message);
			}
		}
	}

	public void updateProgress() {
		progress++;
		percent = ((float) progress / (float) max) * 100f;
		String percentage = (int) percent + "%";
		progressLoadingDialog.incrementProgress();
		progressLoadingDialog.setText(percentage);
		String count = progress + "/" + max;
		tvCountLoadingDialog.setText(count);
	}

	public void setMax(int max) {
		this.max = max;
		progress = 0;
		progressLoadingDialog.setmax(max);
		String count = "0/" + max;
		tvCountLoadingDialog.setText(count);
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public void setOnRefreshCallback(OnRefreshCallback refreshCallback) {
		this.refreshCallback = refreshCallback;
	}

	public void setOnMultiUpdateCallback(OnMultiUpdateCallback multiUpdateCallback) {
		this.multiUpdateCallback = multiUpdateCallback;
	}

	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}

	public void setOnFragmentCallback(OnFragmentCallback fragmentCallback) {
		this.fragmentCallback = fragmentCallback;
	}

	public void setOnTimeValidatedCallback(OnTimeValidatedCallback timeValidationCallback) {
		this.timeValidationCallback = timeValidationCallback;
	}

	private void setOnBackStack(boolean isOnBackStack) {
		if(overrideCallback != null) {
			overrideCallback.onOverride(isOnBackStack);
		}
		if(fragmentCallback != null) {
			fragmentCallback.onFragment(isOnBackStack);
		}
	}

	public void showResult(String message) {
		if(!CodePanUtils.isOnBackStack(getActivity(), DialogTag.RESULT)) {
			manager.popBackStack();
			String title = Module.getTitle(action);
			final AlertDialogFragment alert = new AlertDialogFragment();
			alert.setDialogTitle(title);
			alert.setDialogMessage(message);
			alert.setPositiveButton("OK", new OnClickListener() {
				@Override
				public void onClick(View v) {
					manager.popBackStack();
					if(refreshCallback != null) {
						refreshCallback.onRefresh();
					}
					if(timeValidationCallback != null && result) {
						timeValidationCallback.onTimeValidated();
					}
				}
			});
			transaction = manager.beginTransaction();
			transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
					R.anim.fade_in, R.anim.fade_out);
			transaction.add(R.id.rlMain, alert, DialogTag.RESULT);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}

	@Override
	public void onError(String error, String params, String response, boolean showError) {
		CodePanUtils.setErrorMsg(getActivity(), error, params, response, App.BACKUP, App.ERROR_PWD);
		if(showError) {
			this.error = error;
		}
	}

	@Override
	public void onBackPressed() {
		if(!CodePanUtils.isOnBackStack(getActivity(), DialogTag.CANCEL)) {
			String title = Module.getTitle(action);
			final AlertDialogFragment alert = new AlertDialogFragment();
			alert.setDialogTitle(title);
			alert.setDialogMessage("Are you sure you want to cancel " + title + "?");
			alert.setPositiveButton("Yes", new OnClickListener() {
				@Override
				public void onClick(View v) {
					manager.popBackStack();
					manager.popBackStack();
				}
			});
			alert.setNegativeButton("No", new OnClickListener() {
				@Override
				public void onClick(View v) {
					manager.popBackStack();
					if(isDone) {
						manager.popBackStack();
					}
				}
			});
			transaction = manager.beginTransaction();
			transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
					R.anim.fade_in, R.anim.fade_out);
			transaction.add(R.id.rlMain, alert, DialogTag.CANCEL);
			transaction.addToBackStack(null);
			transaction.commit();
		}
		else {
			manager.popBackStack();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		process.stop();
	}
}

