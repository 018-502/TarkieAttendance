package com.mobileoptima.core;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.codepan.callback.Interface.OnErrorCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.mobileoptima.callback.Interface.OnResultCallback;
import com.mobileoptima.constant.App;
import com.mobileoptima.constant.ProcessName;
import com.mobileoptima.model.BreakInObj;
import com.mobileoptima.model.BreakOutObj;
import com.mobileoptima.model.EntryObj;
import com.mobileoptima.model.IncidentReportObj;
import com.mobileoptima.model.PhotoObj;
import com.mobileoptima.model.TimeInObj;
import com.mobileoptima.model.TimeOutObj;

public class Process {

	private OnResultCallback resultCallback;
	private OnErrorCallback errorCallback;
	private boolean result;
	private Thread bg;

	public Process(OnResultCallback resultCallback) {
		this.resultCallback = resultCallback;
	}

	public void authorizeDevice(final SQLiteAdapter db, final String authorizationCode, final String deviceID) {
		bg = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					result = Rx.authorizeDevice(db, authorizationCode, deviceID, errorCallback);
					Thread.sleep(250);
					handler.sendMessage(handler.obtainMessage());
					if(result) {
						result = Rx.getSyncBatchID(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getCompany(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getConvention(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getStores(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getEmployees(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getBreaks(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getIncidents(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getServerTime(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.setName(ProcessName.AUTHORIZATION);
		bg.start();
	}

	public void login(final SQLiteAdapter db, final String username, final String password) {
		bg = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					result = Rx.getEmployees(db, errorCallback);
					Thread.sleep(250);
					handler.sendMessage(handler.obtainMessage());
					if(result) {
						result = Rx.login(db, username, password, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getForms(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getFields(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getTasks(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getSettings(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getServerTime(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.setName(ProcessName.LOGIN);
		bg.start();
	}

	public void updateMasterFile(final SQLiteAdapter db) {
		bg = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					result = Rx.getCompany(db, errorCallback);
					Thread.sleep(250);
					handler.sendMessage(handler.obtainMessage());
					if(result) {
						result = Rx.getConvention(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getStores(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getEmployees(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getBreaks(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getIncidents(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getForms(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getFields(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getEntries(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getTasks(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getSettings(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Rx.getServerTime(db, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.setName(ProcessName.UPDATE_MASTER_FILE);
		bg.start();
	}

	public void sendBackUp(final SQLiteAdapter db, final String fileName) {
		bg = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					result = CodePanUtils.decryptTextFile(db.getContext(), App.BACKUP, App.ERROR_PWD);
					Thread.sleep(250);
					handler.sendMessage(handler.obtainMessage());
					if(result) {
						result = CodePanUtils.extractDatabase(db.getContext(), App.BACKUP, App.DB);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = CodePanUtils.zipFolder(db.getContext(), App.BACKUP, App.FOLDER, fileName);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = Tx.uploadSendBackUp(db, fileName, errorCallback);
						Thread.sleep(250);
						handler.sendMessage(handler.obtainMessage());
					}
					if(result) {
						result = CodePanUtils.deleteFilesInDir(db.getContext(), App.BACKUP);
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
		bg.setName(ProcessName.SEND_BACKUP);
		bg.start();
	}

	public void syncData(final SQLiteAdapter db) {
		bg = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					result = Rx.getSyncBatchID(db, errorCallback);
					Thread.sleep(250);
					handler.sendMessage(handler.obtainMessage());
					for(TimeInObj in : Data.loadTimeInSync(db)) {
						if(result) {
							result = Tx.syncTimeIn(db, in, errorCallback);
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(TimeOutObj out : Data.loadTimeOutSync(db)) {
						if(result) {
							result = Tx.syncTimeOut(db, out, errorCallback);
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(BreakInObj in : Data.loadBreakInSync(db)) {
						if(result) {
							result = Tx.syncBreakIn(db, in, errorCallback);
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(BreakOutObj out : Data.loadBreakOutSync(db)) {
						if(result) {
							result = Tx.syncBreakOut(db, out, errorCallback);
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(PhotoObj photo : Data.loadPhotosUpload(db)) {
						if(result) {
							result = Tx.uploadEntryPhoto(db, photo, errorCallback);
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(EntryObj entry : Data.loadEntriesSync(db)) {
						if(result) {
							result = Tx.syncEntry(db, entry, errorCallback);
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(IncidentReportObj ir : Data.loadIncidentReportSync(db)) {
						if(result) {
							result = Tx.syncIncidentReport(db, ir, errorCallback);
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(PhotoObj photo : Data.loadTimeInPhotoUpload(db)) {
						if(result) {
							result = Tx.uploadTimeInPhoto(db, photo, errorCallback);
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(PhotoObj photo : Data.loadTimeOutPhotoUpload(db)) {
						if(result) {
							result = Tx.uploadTimeOutPhoto(db, photo, errorCallback);
							Thread.sleep(250);
							handler.sendMessage(handler.obtainMessage());
						}
					}
					for(PhotoObj photo : Data.loadSignatureUpload(db)) {
						if(result) {
							result = Tx.uploadSignature(db, photo, errorCallback);
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
		bg.setName(ProcessName.SYNC_DATA);
		bg.start();
	}

	public void validateTime(final SQLiteAdapter db) {
		bg = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					result = Rx.getServerTime(db, errorCallback);
					Thread.sleep(250);
					handler.sendMessage(handler.obtainMessage());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.setName(ProcessName.VALIDATE_TIME);
		bg.start();
	}

	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message message) {
			if(resultCallback != null) {
				resultCallback.onResult(result);
			}
			return true;
		}
	});

	public void setOnErrorCallback(OnErrorCallback errorCallback) {
		this.errorCallback = errorCallback;
	}

	public void stop() {
		if(bg != null && bg.isAlive()) {
			bg.interrupt();
		}
	}
}
