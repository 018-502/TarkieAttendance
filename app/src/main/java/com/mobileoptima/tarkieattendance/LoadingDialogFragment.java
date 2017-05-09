package com.mobileoptima.tarkieattendance;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.mobileoptima.callback.Interface.OnTimeValidatedCallback;
import com.mobileoptima.constant.App;
import com.mobileoptima.constant.DialogTag;
import com.mobileoptima.constant.Key;
import com.mobileoptima.constant.Module;
import com.mobileoptima.constant.Module.Action;
import com.mobileoptima.constant.Process;
import com.mobileoptima.core.Data;
import com.mobileoptima.core.Rx;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.core.Tx;
import com.mobileoptima.model.BreakInObj;
import com.mobileoptima.model.BreakOutObj;
import com.mobileoptima.model.EntryObj;
import com.mobileoptima.model.ImageObj;
import com.mobileoptima.model.IncidentReportObj;
import com.mobileoptima.model.TimeInObj;
import com.mobileoptima.model.TimeOutObj;

import static com.codepan.callback.Interface.OnRefreshCallback;

public class LoadingDialogFragment extends Fragment implements OnErrorCallback,
		OnBackPressedCallback {

	private CodePanLabel tvTitleLoadingDialog, tvCountLoadingDialog;
	private boolean result, isDone, isPause, isMultiUpdate;
	private OnTimeValidatedCallback timeValidationCallback;
	private String successMsg, failedMsg, error, message;
	private OnMultiUpdateCallback multiUpdateCallback;
	private OnFragmentCallback fragmentCallback;
	private OnOverrideCallback overrideCallback;
	private ProgressWheel progressLoadingDialog;
	private OnRefreshCallback refreshCallback;
	private FragmentTransaction transaction;
	private FragmentManager manager;
	private int progress, max;
	private SQLiteAdapter db;
	private Action action;
	private float percent;
	private Bundle bundle;
	private Thread bg;

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
				authorizeDevice(db, authorizationCode, deviceID);
				break;
			case LOGIN:
				setMax(5);
				successMsg = "Login successful.";
				failedMsg = "Failed to login.";
				title = "Validating Account";
				String username = bundle.getString(Key.USERNAME);
				String password = bundle.getString(Key.PASSWORD);
				login(db, username, password);
				break;
			case UPDATE_MASTER_FILE:
				setMax(10);
				successMsg = "Update master list successful.";
				failedMsg = "Failed to update master file.";
				title = "Updating Master File";
				updateMasterFile(db);
				break;
			case SEND_BACKUP:
				setMax(5);
				successMsg = "Send back-up successful.";
				failedMsg = "Failed to send back-up.";
				title = "Sending Back-up Data";
				String fileName = TarkieLib.getBackupFileName(db);
				sendBackUp(db, fileName);
				break;
			case SYNC_DATA:
				int count = TarkieLib.getCountSyncTotal(db);
				setMax(count + 2);
				successMsg = "Sync Data successful.";
				failedMsg = "Failed to sync data.";
				title = "Syncing Data";
				tvTitleLoadingDialog.setText(title);
				syncData(db);
				break;
			case VALIDATE_SERVER_TIME:
				setMax(1);
				successMsg = "Validation successful.";
				failedMsg = "Failed to validate time.";
				title = "Validating Date and Time";
				validateTime(db);
				break;
		}
		tvTitleLoadingDialog.setText(title);
		return view;
	}

	public void authorizeDevice(final SQLiteAdapter db, final String authorizationCode, final String deviceID) {
		bg = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					result = Rx.authorizeDevice(db, authorizationCode, deviceID, getErrorCallback());
					Thread.sleep(250);
					handler.sendMessage(handler.obtainMessage());
					if(result) {
						result = Rx.getSyncBatchID(db, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getCompany(db, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getConvention(db, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getStores(db, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getEmployees(db, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getBreaks(db, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getIncidents(db, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getServerTime(db, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.setName(Process.AUTHORIZATION);
		bg.start();
	}

	public void login(final SQLiteAdapter db, final String username, final String password) {
		bg = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					result = Rx.getEmployees(db, getErrorCallback());
					Thread.sleep(250);
					handler.sendMessage(handler.obtainMessage());
					if(result) {
						result = Rx.login(db, username, password, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getForms(db, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getFields(db, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getServerTime(db, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.setName(Process.LOGIN);
		bg.start();
	}

	public void updateMasterFile(final SQLiteAdapter db) {
		bg = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					result = Rx.getCompany(db, getErrorCallback());
					Thread.sleep(250);
					handler.sendMessage(handler.obtainMessage());
					if(result) {
						result = Rx.getConvention(db, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getStores(db, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getEmployees(db, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getBreaks(db, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getIncidents(db, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getForms(db, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getFields(db, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getEntries(db, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getServerTime(db, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.setName(Process.UPDATE_MASTER_FILE);
		bg.start();
	}

	public void sendBackUp(final SQLiteAdapter db, final String fileName) {
		bg = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					result = CodePanUtils.decryptTextFile(getActivity(), App.BACKUP, App.ERROR_PWD);
					Thread.sleep(250);
					handler.sendMessage(handler.obtainMessage());
					if(result) {
						result = CodePanUtils.extractDatabase(getActivity(), App.BACKUP, App.DB);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = CodePanUtils.zipFolder(getActivity(), App.BACKUP, App.FOLDER, fileName);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Tx.uploadSendBackUp(db, fileName, getErrorCallback());
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = CodePanUtils.deleteFilesInDir(getActivity(), App.BACKUP);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
//					if(result) {
//						result = TarkieLib.purgeData(db);
//						Thread.sleep(250);
//						handler.sendMessage(handler.obtainMessage());
//					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.setName(Process.SEND_BACKUP);
		bg.start();
	}

	public void syncData(final SQLiteAdapter db) {
		bg = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					result = Rx.getSyncBatchID(db, getErrorCallback());
					Thread.sleep(250);
					handler.sendMessage(handler.obtainMessage());
					for(TimeInObj in : Data.loadTimeInSync(db)) {
						if(result) {
							result = Tx.syncTimeIn(db, in, getErrorCallback());
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(TimeOutObj out : Data.loadTimeOutSync(db)) {
						if(result) {
							result = Tx.syncTimeOut(db, out, getErrorCallback());
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(BreakInObj in : Data.loadBreakInSync(db)) {
						if(result) {
							result = Tx.syncBreakIn(db, in, getErrorCallback());
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(BreakOutObj out : Data.loadBreakOutSync(db)) {
						if(result) {
							result = Tx.syncBreakOut(db, out, getErrorCallback());
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(ImageObj image : Data.loadPhotosUpload(db)) {
						if(result) {
							result = Tx.uploadEntryPhoto(db, image, getErrorCallback());
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(EntryObj entry : Data.loadEntriesSync(db)) {
						if(result) {
							result = Tx.syncEntry(db, entry, getErrorCallback());
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(IncidentReportObj ir : Data.loadIncidentReportSync(db)) {
						if(result) {
							result = Tx.syncIncidentReport(db, ir, getErrorCallback());
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(ImageObj image : Data.loadTimeInPhotoUpload(db)) {
						if(result) {
							result = Tx.uploadTimeInPhoto(db, image, getErrorCallback());
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(ImageObj image : Data.loadTimeOutPhotoUpload(db)) {
						if(result) {
							result = Tx.uploadTimeOutPhoto(db, image, getErrorCallback());
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(ImageObj image : Data.loadSignatureUpload(db)) {
						if(result) {
							result = Tx.uploadSignature(db, image, getErrorCallback());
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					if(result) {
						result = TarkieLib.updateLastSynced(db);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.setName(Process.SYNC_DATA);
		bg.start();
	}

	public void validateTime(final SQLiteAdapter db) {
		bg = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					result = Rx.getServerTime(db, getErrorCallback());
					Thread.sleep(250);
					handler.sendMessage(handler.obtainMessage());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.setName(Process.VALIDATE_TIME);
		bg.start();
	}

	Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
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
			return true;
		}
	});

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

	public OnErrorCallback getErrorCallback() {
		return this;
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

	public void stopProcess() {
		if(bg != null && bg.isAlive()) {
			bg.interrupt();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopProcess();
	}
}

