package com.mobileoptima.tarkieattendance;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.callback.Interface.OnPermissionGrantedCallback;
import com.codepan.callback.Interface.OnRefreshCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.model.GpsObj;
import com.codepan.utils.CodePanUtils;
import com.codepan.utils.SpannableMap;
import com.codepan.widget.CircularImageView;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.callback.Interface.OnCountdownFinishCallback;
import com.mobileoptima.callback.Interface.OnGpsFixedCallback;
import com.mobileoptima.callback.Interface.OnHighlightEntriesCallback;
import com.mobileoptima.callback.Interface.OnMultiUpdateCallback;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.callback.Interface.OnSaveEntryCallback;
import com.mobileoptima.callback.Interface.OnSelectStoreCallback;
import com.mobileoptima.callback.Interface.OnTimeInCallback;
import com.mobileoptima.callback.Interface.OnTimeOutCallback;
import com.mobileoptima.callback.Interface.OnTimeValidatedCallback;
import com.mobileoptima.constant.App;
import com.mobileoptima.constant.Convention;
import com.mobileoptima.constant.DialogTag;
import com.mobileoptima.constant.ImageType;
import com.mobileoptima.constant.Key;
import com.mobileoptima.constant.Module.Action;
import com.mobileoptima.constant.Notification;
import com.mobileoptima.constant.RequestCode;
import com.mobileoptima.constant.Result;
import com.mobileoptima.constant.TabType;
import com.mobileoptima.constant.Tag;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.core.TimeSecurity;
import com.mobileoptima.model.AttendanceObj;
import com.mobileoptima.model.BreakInObj;
import com.mobileoptima.model.BreakObj;
import com.mobileoptima.model.EmployeeObj;
import com.mobileoptima.model.EntryObj;
import com.mobileoptima.model.StoreObj;
import com.mobileoptima.model.TimeInObj;
import com.mobileoptima.model.TimeOutObj;
import com.mobileoptima.service.MainService;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;
import static com.mobileoptima.callback.Interface.OnInitializeCallback;
import static com.mobileoptima.callback.Interface.OnLoginCallback;
import static com.mobileoptima.constant.Settings.TIME_IN_CLIENT;
import static com.mobileoptima.constant.Settings.TIME_IN_PHOTO;
import static com.mobileoptima.constant.Settings.TIME_OUT_PHOTO;

public class MainActivity extends FragmentActivity implements OnClickListener, OnRefreshCallback,
		OnOverrideCallback, OnLoginCallback, OnInitializeCallback, ServiceConnection,
		OnTimeValidatedCallback, OnGpsFixedCallback, OnCountdownFinishCallback,
		OnHighlightEntriesCallback, OnMultiUpdateCallback, OnSaveEntryCallback,
		OnTimeInCallback, OnTimeOutCallback, OnSelectStoreCallback {

	private CodePanLabel tvTimeInMain, tvSyncMain, tvLastSyncMain, tvEmployeeNameMain, tvEmployeeNoMain, tvClientsMenu;
	private boolean isInitialized, isOverridden, isServiceConnected, isPause, isSecured, isGpsOff;
	private CodePanButton btnNotificationMain, btnSyncMain, btnHomeMain, btnVisitsMain, btnExpenseMain, btnPhotosMain,
			btnEntriesMain, btnSelectMain, btnMenuMain, btnAddVisitMain, btnAddExpenseReportMain;
	private View vHomeMain, vVisitsMain, vExpenseMain, vPhotosMain, vEntriesMain;
	private OnPermissionGrantedCallback permissionGrantedCallback;
	private ImageView ivTimeInMain, ivTimeOutMain, ivLogoMain;
	private OnBackPressedCallback backPressedCallback;
	private LocalBroadcastManager broadcastManager;
	private RelativeLayout rlMain, rlMenuMain;
	private CircularImageView ivEmployeeMain;
	private String tabType = TabType.DEFAULT, conventionClient;
	private FragmentTransaction transaction;
	private BroadcastReceiver receiver;
	private LinearLayout llTimeInMain;
	private FragmentManager manager;
	private MainService service;
	private DrawerLayout dlMain;
	private SQLiteAdapter db;

	@Override
	protected void onStart() {
		super.onStart();
		if(isInitialized) {
			bindService();
			registerReceiver();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if(isInitialized) {
			unbindService();
			unregisterReceiver();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		isPause = false;
		if(isInitialized) {
			checkSecurity();
			resumeTimeIn();
			checkTimeIn();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		isPause = true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		manager = getSupportFragmentManager();
		tvTimeInMain = (CodePanLabel) findViewById(R.id.tvTimeInMain);
		tvSyncMain = (CodePanLabel) findViewById(R.id.tvSyncMain);
		tvLastSyncMain = (CodePanLabel) findViewById(R.id.tvLastSyncMain);
		tvEmployeeNameMain = (CodePanLabel) findViewById(R.id.tvEmployeeNameMain);
		tvEmployeeNoMain = (CodePanLabel) findViewById(R.id.tvEmployeeNoMain);
		ivEmployeeMain = (CircularImageView) findViewById(R.id.ivEmployeeMain);
		ivTimeInMain = (ImageView) findViewById(R.id.ivTimeInMain);
		ivTimeOutMain = (ImageView) findViewById(R.id.ivTimeOutMain);
		ivLogoMain = (ImageView) findViewById(R.id.ivLogoMain);
		btnNotificationMain = (CodePanButton) findViewById(R.id.btnNotificationMain);
		btnSyncMain = (CodePanButton) findViewById(R.id.btnSyncMain);
		btnHomeMain = (CodePanButton) findViewById(R.id.btnHomeMain);
		btnVisitsMain = (CodePanButton) findViewById(R.id.btnVisitsMain);
		btnExpenseMain = (CodePanButton) findViewById(R.id.btnExpenseMain);
		btnPhotosMain = (CodePanButton) findViewById(R.id.btnPhotosMain);
		btnEntriesMain = (CodePanButton) findViewById(R.id.btnEntriesMain);
		btnSelectMain = (CodePanButton) findViewById(R.id.btnSelectMain);
		btnAddVisitMain = (CodePanButton) findViewById(R.id.btnAddVisitMain);
		btnAddExpenseReportMain = (CodePanButton) findViewById(R.id.btnAddExpenseReportMain);
		btnMenuMain = (CodePanButton) findViewById(R.id.btnMenuMain);
		llTimeInMain = (LinearLayout) findViewById(R.id.llTimeInMain);
		rlMenuMain = (RelativeLayout) findViewById(R.id.rlMenuMain);
		rlMain = (RelativeLayout) findViewById(R.id.rlMain);
		dlMain = (DrawerLayout) findViewById(R.id.dlMain);
		vHomeMain = findViewById(R.id.vHomeMain);
		vVisitsMain = findViewById(R.id.vVisitsMain);
		vExpenseMain = findViewById(R.id.vExpenseMain);
		vPhotosMain = findViewById(R.id.vPhotosMain);
		vEntriesMain = findViewById(R.id.vEntriesMain);
		tvClientsMenu = (CodePanLabel) findViewById(R.id.tvClientsMenu);
		findViewById(R.id.llAttendanceMain).setOnClickListener(this);
		findViewById(R.id.llBreaksMain).setOnClickListener(this);
		findViewById(R.id.llClientsMain).setOnClickListener(this);
		findViewById(R.id.llUpdateMasterFileMain).setOnClickListener(this);
		findViewById(R.id.llSendBackUpMain).setOnClickListener(this);
		findViewById(R.id.llSupportMain).setOnClickListener(this);
		findViewById(R.id.llSettingsMain).setOnClickListener(this);
		findViewById(R.id.llLocationsMain).setOnClickListener(this);
		findViewById(R.id.llLogoutMain).setOnClickListener(this);
		findViewById(R.id.btnMenuMain).setOnClickListener(this);
		findViewById(R.id.btnSearchMain).setOnClickListener(this);
		findViewById(R.id.btnDateMain).setOnClickListener(this);
		llTimeInMain.setOnClickListener(this);
		btnSelectMain.setOnClickListener(this);
		btnAddVisitMain.setOnClickListener(this);
		btnAddExpenseReportMain.setOnClickListener(this);
		btnHomeMain.setOnClickListener(this);
		btnVisitsMain.setOnClickListener(this);
		btnExpenseMain.setOnClickListener(this);
		btnExpenseMain.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				GpsObj gps = getGps();
				String dDate = CodePanUtils.getDate();
				String dTime = CodePanUtils.getTime();
				String expenseID = TarkieLib.saveExpense(db, dDate, dTime, gps);
				if(!expenseID.isEmpty()) {
					if(tabType.equals(TabType.EXPENSE)) {
						Fragment fragment = manager.findFragmentByTag(tabType);
						if(fragment != null) {
							ExpenseFragment expense = (ExpenseFragment) fragment;
							expense.addExpenseItem(dDate, dTime, expenseID);
						}
					}
					CodePanUtils.alertToast(MainActivity.this, "Expense " + expenseID + " has been added. You may enter more details later.");
				}
				return true;
			}
		});
		btnPhotosMain.setOnClickListener(this);
		btnEntriesMain.setOnClickListener(this);
		btnNotificationMain.setOnClickListener(this);
		btnSyncMain.setOnClickListener(this);
		btnMenuMain.setOnClickListener(this);
		int color = getResources().getColor(R.color.black_trans_twenty);
		dlMain.setScrimColor(color);
		init(savedInstanceState);
	}

	public void init(Bundle savedInstanceState) {
		if(savedInstanceState != null) {
			if(isInitialized) {
				checkRevokedPermissions();
			}
			else {
				this.finish();
				overridePendingTransition(0, 0);
				Intent intent = new Intent(this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
			}
		}
		else {
			SplashFragment splash = new SplashFragment();
			splash.setOnInitializeCallback(this);
			transaction = manager.beginTransaction();
			transaction.add(R.id.rlMain, splash);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}

	public void checkRevokedPermissions() {
		if(!CodePanUtils.isPermissionGranted(this)) {
			manager.popBackStack(null, POP_BACK_STACK_INCLUSIVE);
			Intent intent = new Intent(this, getClass());
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent);
			this.finish();
		}
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnHomeMain:
				if(!tabType.equals(TabType.HOME)) {
					Fragment current = manager.findFragmentByTag(tabType);
					Fragment old = manager.findFragmentByTag(TabType.HOME);
					transaction = manager.beginTransaction();
					if(old != null) {
						transaction.show(old);
					}
					else {
						HomeFragment home = new HomeFragment();
						home.setOnOverrideCallback(this);
						home.setOnSaveEntryCallback(this);
						transaction.add(R.id.flContainerMain, home, TabType.HOME);
					}
					if(current != null) {
						transaction.hide(current);
					}
					transaction.commit();
				}
				setTabType(TabType.HOME);
				break;
			case R.id.btnVisitsMain:
				if(!tabType.equals(TabType.VISITS)) {
					Fragment current = manager.findFragmentByTag(tabType);
					Fragment old = manager.findFragmentByTag(TabType.VISITS);
					transaction = manager.beginTransaction();
					if(old != null) {
						transaction.show(old);
					}
					else {
						VisitsFragment visits = new VisitsFragment();
						visits.setOnOverrideCallback(this);
						transaction.add(R.id.flContainerMain, visits, TabType.VISITS);
					}
					if(current != null) {
						transaction.hide(current);
					}
					transaction.commit();
				}
				setTabType(TabType.VISITS);
				break;
			case R.id.btnExpenseMain:
				if(!tabType.equals(TabType.EXPENSE)) {
					Fragment current = manager.findFragmentByTag(tabType);
					Fragment old = manager.findFragmentByTag(TabType.EXPENSE);
					transaction = manager.beginTransaction();
					if(old != null) {
						transaction.show(old);
					}
					else {
						ExpenseFragment expense = new ExpenseFragment();
						transaction.add(R.id.flContainerMain, expense, TabType.EXPENSE);
					}
					if(current != null) {
						transaction.hide(current);
					}
					transaction.commit();
				}
				setTabType(TabType.EXPENSE);
				break;
			case R.id.btnPhotosMain:
				if(!tabType.equals(TabType.PHOTOS)) {
					Fragment current = manager.findFragmentByTag(tabType);
					Fragment old = manager.findFragmentByTag(TabType.PHOTOS);
					transaction = manager.beginTransaction();
					if(old != null) {
						transaction.show(old);
					}
					else {
						PhotosFragment photos = new PhotosFragment();
						photos.setOnOverrideCallback(this);
						transaction.add(R.id.flContainerMain, photos, TabType.PHOTOS);
					}
					if(current != null) {
						transaction.hide(current);
					}
					transaction.commit();
				}
				setTabType(TabType.PHOTOS);
				break;
			case R.id.btnEntriesMain:
				if(!tabType.equals(TabType.ENTRIES)) {
					Fragment current = manager.findFragmentByTag(tabType);
					Fragment old = manager.findFragmentByTag(TabType.ENTRIES);
					transaction = manager.beginTransaction();
					if(old != null) {
						transaction.show(old);
					}
					else {
						EntriesFragment entries = new EntriesFragment();
						entries.setOnOverrideCallback(this);
						entries.setOnHighlightEntriesCallback(this);
						transaction.add(R.id.flContainerMain, entries, TabType.ENTRIES);
					}
					if(current != null) {
						transaction.hide(current);
					}
					transaction.commit();
				}
				setTabType(TabType.ENTRIES);
				break;
			case R.id.btnMenuMain:
				if(dlMain.isDrawerOpen(rlMenuMain)) {
					dlMain.closeDrawer(rlMenuMain);
				}
				else {
					dlMain.openDrawer(rlMenuMain);
				}
				break;
			case R.id.llTimeInMain:
				dlMain.closeDrawer(rlMenuMain);
				if(!TarkieLib.isTimeIn(db)) {
					if(isGpsSecured()) {
						GpsObj gps = getGps();
						if(!gps.isValid) {
							SearchGpsFragment search = new SearchGpsFragment();
							search.setCountdown(App.GPS_COUNTDOWN);
							search.setOnGpsFixedCallback(this);
							search.setOnCountdownFinishCallback(this);
							transaction = manager.beginTransaction();
							transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
									R.anim.fade_in, R.anim.fade_out);
							transaction.add(R.id.rlMain, search);
							transaction.addToBackStack(null);
							transaction.commit();
						}
						else {
							onGpsFixed(gps);
						}
					}
					else {
						isGpsOff = true;
					}
				}
				else {
					if(!TarkieLib.isCheckIn(db)) {
						if(!TarkieLib.hasUnsubmittedEntries(db)) {
							final AlertDialogFragment alert = new AlertDialogFragment();
							alert.setDialogTitle("Confirm Time-out");
							alert.setDialogMessage("Do you want to time-out?");
							alert.setPositiveButton("Yes", new OnClickListener() {
								@Override
								public void onClick(View view) {
									manager.popBackStack();
									String date = CodePanUtils.getDate();
									String time = CodePanUtils.getTime();
									GpsObj gps = getGps();
									if(TarkieLib.isSettingsEnabled(db, TIME_OUT_PHOTO)) {
										CameraFragment camera = new CameraFragment();
										camera.setGps(gps);
										camera.setDate(date);
										camera.setTime(time);
										camera.setImageType(ImageType.TIME_OUT);
										camera.setOnTimeOutCallback(MainActivity.this);
										camera.setOnOverrideCallback(MainActivity.this);
										transaction = manager.beginTransaction();
										transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
												R.anim.slide_in_ltr, R.anim.slide_out_ltr);
										transaction.add(R.id.rlMain, camera);
										transaction.addToBackStack(null);
										transaction.commit();
									}
									else {
										TimeOutObj out = new TimeOutObj();
										out.gps = gps;
										out.dDate = date;
										out.dTime = time;
										onTimeOut(out);
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
							final AlertDialogFragment alert = new AlertDialogFragment();
							alert.setDialogTitle(R.string.unsubmitted_entries_title);
							alert.setDialogMessage(R.string.unsubmitted_entries_message);
							alert.setPositiveButton("View", new OnClickListener() {
								@Override
								public void onClick(View view) {
									manager.popBackStack();
									setTab(TabType.ENTRIES);
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
					}
					else {
						String title = TarkieLib.getPendingVisit(db);
						String message = getString(R.string.currently_check_in_message);
						String font = getString(R.string.proxima_nova_bold);
						int start = message.indexOf("$");
						int end = start + title.length();
						message = message.replace("$title", title);
						ArrayList<SpannableMap> list = new ArrayList<>();
						list.add(new SpannableMap(this, font, start, end));
						final AlertDialogFragment alert = new AlertDialogFragment();
						alert.setDialogTitle(R.string.currently_check_in_title);
						alert.setSpannableList(list);
						alert.setDialogMessage(message);
						alert.setPositiveButton("View", new OnClickListener() {
							@Override
							public void onClick(View view) {
								manager.popBackStack();
								setTab(TabType.VISITS);
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
				}
				break;
			case R.id.llAttendanceMain:
				dlMain.closeDrawer(rlMenuMain);
				AttendanceFragment attendance = new AttendanceFragment();
				transaction = manager.beginTransaction();
				transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
						R.anim.slide_in_ltr, R.anim.slide_out_ltr);
				transaction.add(R.id.rlMain, attendance);
				transaction.addToBackStack(null);
				transaction.commit();
				break;
			case R.id.llBreaksMain:
				dlMain.closeDrawer(rlMenuMain);
				if(TarkieLib.isTimeIn(db)) {
					if(TarkieLib.hasBreak(db)) {
						BreakFragment breaks = new BreakFragment();
						breaks.setOnOverrideCallback(this);
						transaction = manager.beginTransaction();
						transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
								R.anim.fade_in, R.anim.fade_out);
						transaction.add(R.id.rlMain, breaks);
						transaction.addToBackStack(null);
						transaction.commit();
					}
					else {
						CodePanUtils.alertToast(this, R.string.no_break);
					}
				}
				else {
					CodePanUtils.alertToast(this, R.string.break_without_time_in);
				}
				break;
			case R.id.llClientsMain:
				dlMain.closeDrawer(rlMenuMain);
				transaction = manager.beginTransaction();
				transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
						R.anim.fade_in, R.anim.fade_out);
				transaction.add(R.id.rlMain, new StoresFragment());
				transaction.addToBackStack(null);
				transaction.commit();
				break;
			case R.id.llUpdateMasterFileMain:
				dlMain.closeDrawer(rlMenuMain);
				if(CodePanUtils.hasInternet(this)) {
					final AlertDialogFragment alert = new AlertDialogFragment();
					alert.setDialogTitle("Update Master File");
					alert.setDialogMessage("Do you want to download the latest master file?");
					alert.setPositiveButton("Yes", new OnClickListener() {
						@Override
						public void onClick(View view) {
							manager.popBackStack();
							LoadingDialogFragment loading = new LoadingDialogFragment();
							loading.setAction(Action.UPDATE_MASTER_FILE);
							loading.setOnRefreshCallback(MainActivity.this);
							loading.setOnOverrideCallback(MainActivity.this);
							transaction = manager.beginTransaction();
							transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
									R.anim.fade_in, R.anim.fade_out);
							transaction.add(R.id.rlMain, loading);
							transaction.addToBackStack(null);
							transaction.commit();
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
					CodePanUtils.alertToast(this, R.string.no_internet_message);
				}
				break;
			case R.id.llSendBackUpMain:
				dlMain.closeDrawer(rlMenuMain);
				if(CodePanUtils.hasInternet(this)) {
					final AlertDialogFragment alert = new AlertDialogFragment();
					alert.setDialogTitle("Send Back-up");
					alert.setDialogMessage("Do you want to send back-up?");
					alert.setPositiveButton("Yes", new OnClickListener() {
						@Override
						public void onClick(View view) {
							manager.popBackStack();
							LoadingDialogFragment loading = new LoadingDialogFragment();
							loading.setAction(Action.SEND_BACKUP);
							loading.setOnRefreshCallback(MainActivity.this);
							loading.setOnOverrideCallback(MainActivity.this);
							transaction = manager.beginTransaction();
							transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
									R.anim.fade_in, R.anim.fade_out);
							transaction.add(R.id.rlMain, loading);
							transaction.addToBackStack(null);
							transaction.commit();
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
					CodePanUtils.alertToast(this, R.string.no_internet_message);
				}
				break;
			case R.id.llSupportMain:
				dlMain.closeDrawer(rlMenuMain);
				break;
			case R.id.llSettingsMain:
				dlMain.closeDrawer(rlMenuMain);
				break;
			case R.id.llLocationsMain:
				dlMain.closeDrawer(rlMenuMain);
				LocationFragment location = new LocationFragment();
				transaction = manager.beginTransaction();
				transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
						R.anim.slide_in_ltr, R.anim.slide_out_ltr);
				transaction.add(R.id.rlMain, location);
				transaction.addToBackStack(null);
				transaction.commit();
				break;
			case R.id.btnNotificationMain:
				final AnnouncementsFragment announcements = new AnnouncementsFragment();
				transaction = manager.beginTransaction();
				transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
						R.anim.slide_in_ltr, R.anim.slide_out_ltr);
				transaction.add(R.id.rlMain, announcements);
				transaction.addToBackStack(null);
				transaction.commit();
				break;
			case R.id.btnSyncMain:
				if(CodePanUtils.hasInternet(this)) {
					int count = TarkieLib.getCountSyncTotal(db);
					if(count > 0) {
						String transactions = count == 1 ? "transaction" : "transactions";
						String message = "You have " + count + " unsaved " + transactions + ". " +
								"Do you want to send it to the server now?";
						final AlertDialogFragment alert = new AlertDialogFragment();
						alert.setDialogTitle("Sync Data");
						alert.setDialogMessage(message);
						alert.setPositiveButton("Yes", new OnClickListener() {
							@Override
							public void onClick(View view) {
								manager.popBackStack();
								LoadingDialogFragment loading = new LoadingDialogFragment();
								loading.setAction(Action.SYNC_DATA);
								loading.setOnRefreshCallback(MainActivity.this);
								loading.setOnOverrideCallback(MainActivity.this);
								loading.setOnMultiUpdateCallback(MainActivity.this);
								transaction = manager.beginTransaction();
								transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
										R.anim.fade_in, R.anim.fade_out);
								transaction.add(R.id.rlMain, loading);
								transaction.addToBackStack(null);
								transaction.commit();
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
						CodePanUtils.alertToast(this, R.string.no_data_to_sync_message);
					}
				}
				else {
					CodePanUtils.alertToast(this, R.string.no_internet_message);
				}
				break;
			case R.id.llLogoutMain:
				dlMain.closeDrawer(rlMenuMain);
				final AlertDialogFragment alert = new AlertDialogFragment();
				alert.setDialogTitle(R.string.logout_title);
				alert.setDialogMessage(R.string.logout_message);
				alert.setPositiveButton("Yes", new OnClickListener() {
					@Override
					public void onClick(View view) {
						manager.popBackStack();
						boolean result = TarkieLib.logout(db);
						if(result) {
							manager.popBackStack();
							onRefresh();
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
				break;
			case R.id.btnSelectMain:
				if(tabType.equals(TabType.ENTRIES)) {
					EntriesFragment entries = (EntriesFragment) manager.findFragmentByTag(tabType);
					if(!entries.isHighlight()) {
						entries.select(true);
					}
					else {
						entries.submit();
					}
				}
				break;
			case R.id.btnSearchMain:
				SearchEntriesFragment search = new SearchEntriesFragment();
				search.setOnOverrideCallback(this);
				transaction = manager.beginTransaction();
				transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
						R.anim.slide_in_ltr, R.anim.slide_out_ltr);
				transaction.replace(R.id.rlMain, search);
				transaction.addToBackStack(null);
				transaction.commit();
				break;
			case R.id.btnDateMain:
				if(tabType.equals(TabType.VISITS)) {
					VisitsFragment visits = (VisitsFragment) manager.findFragmentByTag(tabType);
					visits.showCalendar();
				}
				break;
			case R.id.btnAddVisitMain:
				if(tabType.equals(TabType.VISITS)) {
					VisitsFragment visits = (VisitsFragment) manager.findFragmentByTag(tabType);
					visits.addVisit();
				}
				break;
			case R.id.btnAddExpenseReportMain:
				if(tabType.equals(TabType.EXPENSE)) {
					ExpenseFragment expense = (ExpenseFragment) manager.findFragmentByTag(tabType);
					expense.addExpenseReport();
				}
				break;
		}
	}

	private void setTabType(String tabType) {
		View rlSyncMain = findViewById(R.id.rlSyncMain);
		View rlSearchMain = findViewById(R.id.rlSearchMain);
		View rlNotifMain = findViewById(R.id.rlNotifMain);
		View rlDateMain = findViewById(R.id.rlDateMain);
		vHomeMain.setVisibility(View.GONE);
		vVisitsMain.setVisibility(View.GONE);
		vExpenseMain.setVisibility(View.GONE);
		vPhotosMain.setVisibility(View.GONE);
		vEntriesMain.setVisibility(View.GONE);
		switch(tabType) {
			case TabType.HOME:
				vHomeMain.setVisibility(View.VISIBLE);
				rlNotifMain.setVisibility(View.VISIBLE);
				rlSyncMain.setVisibility(View.VISIBLE);
				rlSearchMain.setVisibility(View.GONE);
				rlDateMain.setVisibility(View.GONE);
				btnSelectMain.setVisibility(View.GONE);
				btnAddVisitMain.setVisibility(View.GONE);
				btnAddExpenseReportMain.setVisibility(View.GONE);
				break;
			case TabType.VISITS:
				vVisitsMain.setVisibility(View.VISIBLE);
				rlNotifMain.setVisibility(View.GONE);
				rlSyncMain.setVisibility(View.GONE);
				rlSearchMain.setVisibility(View.GONE);
				rlDateMain.setVisibility(View.VISIBLE);
				btnSelectMain.setVisibility(View.GONE);
				btnAddVisitMain.setVisibility(View.VISIBLE);
				btnAddExpenseReportMain.setVisibility(View.GONE);
				break;
			case TabType.EXPENSE:
				vExpenseMain.setVisibility(View.VISIBLE);
				rlNotifMain.setVisibility(View.GONE);
				rlSyncMain.setVisibility(View.GONE);
				rlSearchMain.setVisibility(View.GONE);
				rlDateMain.setVisibility(View.GONE);
				btnSelectMain.setVisibility(View.GONE);
				btnAddVisitMain.setVisibility(View.GONE);
				btnAddExpenseReportMain.setVisibility(View.VISIBLE);
				break;
			case TabType.PHOTOS:
				vPhotosMain.setVisibility(View.VISIBLE);
				rlNotifMain.setVisibility(View.VISIBLE);
				rlSyncMain.setVisibility(View.VISIBLE);
				rlSearchMain.setVisibility(View.GONE);
				rlDateMain.setVisibility(View.GONE);
				btnSelectMain.setVisibility(View.GONE);
				btnAddVisitMain.setVisibility(View.GONE);
				btnAddExpenseReportMain.setVisibility(View.GONE);
				break;
			case TabType.ENTRIES:
				vEntriesMain.setVisibility(View.VISIBLE);
				rlNotifMain.setVisibility(View.GONE);
				rlSyncMain.setVisibility(View.GONE);
				rlDateMain.setVisibility(View.GONE);
				rlSearchMain.setVisibility(View.VISIBLE);
				btnSelectMain.setVisibility(View.VISIBLE);
				btnAddVisitMain.setVisibility(View.GONE);
				btnAddExpenseReportMain.setVisibility(View.GONE);
				break;
		}
		this.tabType = tabType;
	}

	public RelativeLayout getMainParent() {
		return this.rlMain;
	}

	public SQLiteAdapter getDatabase() {
		return this.db;
	}

	public MainService getService() {
		return this.service;
	}

	public void authenticate() {
		if(!TarkieLib.isAuthorized(db)) {
			AuthorizationFragment authorization = new AuthorizationFragment();
			authorization.setOnOverrideCallback(this);
			authorization.setOnRefreshCallback(this);
			transaction = manager.beginTransaction();
			transaction.replace(R.id.rlMain, authorization);
			transaction.addToBackStack(null);
			transaction.commit();
		}
		else {
			if(!TarkieLib.isLoggedIn(db)) {
				LoginFragment login = new LoginFragment();
				login.setOnOverrideCallback(this);
				login.setOnRefreshCallback(this);
				login.setOnLoginCallback(this);
				transaction = manager.beginTransaction();
				transaction.replace(R.id.rlMain, login);
				transaction.addToBackStack(null);
				transaction.commit();
			}
		}
	}

	public void setOnPermissionGrantedCallback(OnPermissionGrantedCallback permissionGrantedCallback) {
		this.permissionGrantedCallback = permissionGrantedCallback;
	}

	public void setOnBackPressedCallback(OnBackPressedCallback backPressedCallback) {
		this.backPressedCallback = backPressedCallback;
	}

	@Override
	public void onInitialize(SQLiteAdapter db) {
		this.isInitialized = true;
		this.db = db;
		bindService();
		authenticate();
		checkSecurity();
		checkTimeIn();
		setDefaultTab();
		updateUser();
		updateLogo();
		setReceiver();
		registerReceiver();
		updateSyncCount();
		updateLastSynced();
		setConventions();
	}

	public void setDefaultTab() {
		if(btnHomeMain != null) {
			btnHomeMain.performClick();
		}
	}

	public void setConventions() {
		conventionClient = TarkieLib.getConvention(db, Convention.STORES);
		if(conventionClient != null) {
			conventionClient = StringUtils.capitalize(conventionClient);
			tvClientsMenu.setText(conventionClient);
		}
	}

	@Override
	public void onLogin() {
		updateUser();
	}

	@Override
	public void onRefresh() {
		authenticate();
		checkSecurity();
		updateSyncCount();
		updateLastSynced();
		reloadSchedule();
		reloadForms();
		reloadVisits();
		reloadExpenseItems();
		reloadExpenseReports();
		reloadEntries();
	}

	@Override
	public void onOverride(boolean isOverridden) {
		this.isOverridden = isOverridden;
	}

	@Override
	public void onBackPressed() {
		if(isInitialized && isSecured) {
			Log.e("isOverridden", "" + isOverridden);
			Log.e("backPressedCallback", "" + (backPressedCallback != null));
			if(isOverridden) {
				if(backPressedCallback != null) {
					backPressedCallback.onBackPressed();
				}
			}
			else {
				int count = manager.getBackStackEntryCount();
				if(!tabType.equals(TabType.HOME) && count == 0) {
					setDefaultTab();
				}
				else {
					super.onBackPressed();
				}
			}
		}
		else {
			this.finish();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
										   @NonNull int[] grantResults) {
		switch(requestCode) {
			case RequestCode.PERMISSION:
				if(grantResults.length > 0) {
					boolean isPermissionGranted = true;
					for(int result : grantResults) {
						if(result == PackageManager.PERMISSION_DENIED) {
							isPermissionGranted = false;
							break;
						}
					}
					if(permissionGrantedCallback != null) {
						permissionGrantedCallback.onPermissionGranted(isPermissionGranted);
					}
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
				break;
		}
	}

	public void bindService() {
		Intent service = new Intent(this, MainService.class);
		bindService(service, this, Context.BIND_AUTO_CREATE);
	}

	public void unbindService() {
		if(isServiceConnected) {
			unbindService(this);
		}
	}

	public void registerReceiver() {
		broadcastManager = LocalBroadcastManager.getInstance(this);
		IntentFilter filter = new IntentFilter(RequestCode.NOTIFICATION);
		broadcastManager.registerReceiver((receiver), filter);
	}

	public void unregisterReceiver() {
		broadcastManager = LocalBroadcastManager.getInstance(this);
		broadcastManager.unregisterReceiver(receiver);
	}

	public GpsObj getGps() {
		if(!isServiceConnected) {
			bindService();
		}
		return service.getGps();
	}

	public void setDefaultStore(StoreObj store) {
		Fragment home = manager.findFragmentByTag(TabType.HOME);
		if(home != null) {
			((HomeFragment) home).setStore(store);
		}
	}

	public void resumeTimeIn() {
		if(isGpsOff && CodePanUtils.isGpsEnabled(this)) {
			llTimeInMain.performClick();
			isGpsOff = false;
		}
	}

	public void reloadSchedule() {
		if(!isPause) {
			Fragment fragment = manager.findFragmentByTag(TabType.HOME);
			if(fragment != null) {
				HomeFragment home = (HomeFragment) fragment;
				home.loadSchedule(db);
			}
		}
	}

	public void reloadForms() {
		if(!isPause) {
			Fragment fragment = manager.findFragmentByTag(TabType.HOME);
			if(fragment != null) {
				HomeFragment home = (HomeFragment) fragment;
				home.loadForms(db);
			}
		}
	}

	public void reloadEntries() {
		if(!isPause) {
			Fragment fragment = manager.findFragmentByTag(TabType.ENTRIES);
			if(fragment != null) {
				EntriesFragment entries = (EntriesFragment) fragment;
				entries.select(false);
				entries.loadEntries(db);
			}
		}
	}

	public void reloadVisits() {
		if(!isPause) {
			Fragment fragment = manager.findFragmentByTag(TabType.VISITS);
			if(fragment != null) {
				VisitsFragment visits = (VisitsFragment) fragment;
				String date = visits.getSelectedDate();
				if(date != null) {
					visits.loadVisits(db, date);
				}
			}
		}
	}

	public void reloadExpenseItems() {
		if(!isPause) {
			Fragment fragment = manager.findFragmentByTag(TabType.EXPENSE);
			if(fragment != null) {
				ExpenseFragment expense = (ExpenseFragment) fragment;
				expense.loadExpenseItems(db);
			}
		}
	}

	public void reloadExpenseReports() {
		if(!isPause) {
			Fragment fragment = manager.findFragmentByTag(TabType.EXPENSE);
			if(fragment != null) {
				ExpenseFragment expense = (ExpenseFragment) fragment;
				expense.loadExpenseReports(db);
			}
		}
	}

	public void reloadPhotos() {
		if(!isPause) {
			Fragment fragment = manager.findFragmentByTag(TabType.PHOTOS);
			if(fragment != null) {
				PhotosFragment photos = (PhotosFragment) fragment;
				photos.loadPhotos(db);
			}
		}
	}

	public void openMenu() {
		if(btnMenuMain != null) {
			btnMenuMain.performClick();
		}
	}

	public void setTab(String tabType) {
		switch(tabType) {
			case TabType.HOME:
				btnHomeMain.performClick();
				break;
			case TabType.VISITS:
				btnVisitsMain.performClick();
				break;
			case TabType.EXPENSE:
				btnExpenseMain.performClick();
				break;
			case TabType.PHOTOS:
				btnPhotosMain.performClick();
				break;
			case TabType.ENTRIES:
				btnEntriesMain.performClick();
				break;
		}
	}

	public void setReceiver() {
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if(intent.hasExtra(Key.NOTIFICATION)) {
					int code = intent.getIntExtra(Key.NOTIFICATION, 0);
					switch(code) {
						case Notification.OVER_BREAK:
							//TODO Pop-up dialog for excessive break
							break;
					}
					checkSecurity();
				}
				updateSyncCount();
				updateLastSynced();
			}
		};
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder binder) {
		this.service = ((MainService.LocalBinder) binder).getService();
		this.isServiceConnected = true;
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		this.isServiceConnected = false;
	}

	public boolean isGpsSecured() {
		boolean isGpsSecured = false;
		if(!CodePanUtils.isOnBackStack(this, DialogTag.GPS) &&
				!CodePanUtils.isGpsEnabled(this) && !isPause) {
			final AlertDialogFragment alert = new AlertDialogFragment();
			alert.setDialogTitle(R.string.gps_title);
			alert.setDialogMessage(R.string.gps_message);
			alert.setPositiveButton("OK", new OnClickListener() {
				@Override
				public void onClick(View view) {
					manager.popBackStack();
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(intent);
				}
			});
			alert.setNegativeButton("Cancel", new OnClickListener() {
				@Override
				public void onClick(View view) {
					manager.popBackStack();
					if(TarkieLib.isTimeIn(db)) {
						finish();
					}
				}
			});
			transaction = manager.beginTransaction();
			transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
					R.anim.fade_in, R.anim.fade_out);
			transaction.add(R.id.rlMain, alert, DialogTag.GPS);
			transaction.addToBackStack(null);
			transaction.commit();
		}
		else if(!CodePanUtils.isOnBackStack(this, DialogTag.MOCK) &&
				CodePanUtils.isMockEnabled(this) && !isPause) {
			final AlertDialogFragment alert = new AlertDialogFragment();
			alert.setDialogTitle(R.string.mock_title);
			alert.setDialogMessage(R.string.mock_message);
			alert.setPositiveButton("OK", new OnClickListener() {
				@Override
				public void onClick(View view) {
					manager.popBackStack();
					Intent intent = new Intent(Settings.ACTION_SETTINGS);
					startActivity(intent);
				}
			});
			alert.setNegativeButton("Cancel", new OnClickListener() {
				@Override
				public void onClick(View view) {
					manager.popBackStack();
					if(TarkieLib.isTimeIn(db)) {
						finish();
					}
				}
			});
			transaction = manager.beginTransaction();
			transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
					R.anim.fade_in, R.anim.fade_out);
			transaction.add(R.id.rlMain, alert, DialogTag.MOCK);
			transaction.addToBackStack(null);
			transaction.commit();
		}
		else {
			isGpsSecured = true;
		}
		return isGpsSecured;
	}

	public void checkSecurity() {
		if(TarkieLib.isAuthorized(db) && TarkieLib.isLoggedIn(db)) {
			TimeSecurity.checkBootComplete(db);
			if(TimeSecurity.isTimeUnknown(db) && !TimeSecurity.isValidated(db)) {
				forceToValidate();
			}
			else {
				boolean isTimeSecured = false;
				if(!TimeSecurity.isTimeChanged(db) && !TimeSecurity.isDateChanged(db) &&
						!TimeSecurity.isTimeZoneChanged(db)) {
					isTimeSecured = !TimeSecurity.isTimeCheck(db) || TimeSecurity.isRightTime(db);
				}
				else {
					isTimeSecured = TimeSecurity.isRightTime(db);
				}
				if(isTimeSecured) {
					TimeSecurity.resetTimeSecurity(db);
					if(CodePanUtils.isOnBackStack(this, DialogTag.TIME_UNKNOWN) ||
							CodePanUtils.isOnBackStack(this, DialogTag.TIME_SECURITY)) {
						manager.popBackStack(null, POP_BACK_STACK_INCLUSIVE);
					}
					isSecured = !TarkieLib.isTimeIn(db) || isGpsSecured();
				}
				else {
					if(!CodePanUtils.isOnBackStack(this, DialogTag.TIME_SECURITY) && !isPause) {
						TimeSecurityFragment timeSecurity = new TimeSecurityFragment();
						timeSecurity.setOnTimeValidatedCallback(this);
						transaction = manager.beginTransaction();
						transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
								R.anim.fade_in, R.anim.fade_out);
						transaction.add(R.id.rlMain, timeSecurity, DialogTag.TIME_SECURITY);
						transaction.addToBackStack(null);
						transaction.commit();
					}
					isSecured = false;
				}
			}
		}
		else {
			isSecured = false;
		}
	}

	public void checkTimeIn() {
		Intent service = new Intent(this, MainService.class);
		if(TarkieLib.isTimeIn(db)) {
			tvTimeInMain.setText(R.string.time_out);
			ivTimeInMain.setVisibility(View.GONE);
			ivTimeOutMain.setVisibility(View.VISIBLE);
			startService(service);
		}
		else {
			tvTimeInMain.setText(R.string.time_in);
			ivTimeInMain.setVisibility(View.VISIBLE);
			ivTimeOutMain.setVisibility(View.GONE);
			stopService(service);
		}
	}

	public void updateSyncCount() {
		int count = TarkieLib.getCountSyncTotal(db);
		if(count > 0) {
			tvSyncMain.setVisibility(View.VISIBLE);
			tvSyncMain.setText(String.valueOf(count));
			int res = count > 99 ? R.dimen.eight : R.dimen.ten;
			float textSize = getResources().getDimension(res);
			tvSyncMain.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		}
		else {
			tvSyncMain.setVisibility(View.GONE);
		}
	}

	public void updateLastSynced() {
		if(tvLastSyncMain != null) {
			String title = getString(R.string.last_synced);
			String lastSynced = TarkieLib.getLastSynced(db);
			if(lastSynced != null) {
				String text = title + " " + lastSynced;
				tvLastSyncMain.setText(text);
			}
			else {
				String text = title + " N/A";
				tvLastSyncMain.setText(text);
			}
		}
	}

	public void updateUser() {
		String empID = TarkieLib.getEmployeeID(db);
		if(empID != null) {
			EmployeeObj employee = TarkieLib.getEmployee(db, empID);
			tvEmployeeNameMain.setText(employee.fullName);
			tvEmployeeNoMain.setText(employee.employeeNo);
			CodePanUtils.displayImage(ivEmployeeMain, employee.imageUrl,
					R.drawable.ic_user_placeholder);
		}
	}

	public void updateLogo() {
		String logoUrl = TarkieLib.getCompanyLogo(db);
		if(logoUrl != null) {
			CodePanUtils.displayImage(ivLogoMain, logoUrl);
			Fragment fragment = manager.findFragmentByTag(TabType.HOME);
			if(fragment != null) {
				HomeFragment home = (HomeFragment) fragment;
				home.updateLogo(db, logoUrl);
			}
		}
	}

	public void checkBreakIn() {
		if(TarkieLib.isBreakIn(db)) {
			if(!CodePanUtils.isOnBackStack(this, DialogTag.BREAK)) {
				manager.popBackStack(null, POP_BACK_STACK_INCLUSIVE);
				BreakInObj in = TarkieLib.getBreakIn(db);
				BreakObj obj = TarkieLib.getBreak(db, in.ID);
				BreakTimeFragment breakTime = new BreakTimeFragment();
				breakTime.setBreak(obj);
				breakTime.setOnOverrideCallback(this);
				transaction = manager.beginTransaction();
				transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
						R.anim.fade_in, R.anim.fade_out);
				transaction.add(R.id.rlMain, breakTime, DialogTag.BREAK);
				transaction.addToBackStack(null);
				transaction.commit();
			}
		}
	}

	public void forceToValidate() {
		if(!CodePanUtils.isOnBackStack(this, DialogTag.TIME_UNKNOWN) && !isPause) {
			final AlertDialogFragment alert = new AlertDialogFragment();
			alert.setDialogTitle(R.string.time_unknown_title);
			alert.setDialogMessage(R.string.time_unknown_message);
			alert.setPositiveButton("Validate", new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(CodePanUtils.hasInternet(MainActivity.this)) {
						LoadingDialogFragment loading = new LoadingDialogFragment();
						loading.setAction(Action.VALIDATE_SERVER_TIME);
						loading.setOnTimeValidatedCallback(MainActivity.this);
						transaction = manager.beginTransaction();
						transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
								R.anim.fade_in, R.anim.fade_out);
						transaction.add(R.id.rlMain, loading);
						transaction.addToBackStack(null);
						transaction.commit();
					}
					else {
						if(isGpsSecured()) {
							SearchGpsTimeFragment search = new SearchGpsTimeFragment();
							search.setOnTimeValidatedCallback(MainActivity.this);
							transaction = manager.beginTransaction();
							transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
									R.anim.fade_in, R.anim.fade_out);
							transaction.add(R.id.rlMain, search);
							transaction.addToBackStack(null);
							transaction.commit();
						}
					}
				}
			});
			alert.setNegativeButton("Cancel", new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
			transaction = manager.beginTransaction();
			transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
					R.anim.fade_in, R.anim.fade_out);
			transaction.add(R.id.rlMain, alert, DialogTag.TIME_UNKNOWN);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}

	@Override
	public void onTimeValidated() {
		if(CodePanUtils.isOnBackStack(this, DialogTag.TIME_UNKNOWN)) {
			manager.popBackStack();
		}
		checkSecurity();
	}

	@Override
	public void onGpsFixed(GpsObj gps) {
		if(TarkieLib.isSettingsEnabled(db, TIME_IN_CLIENT)) {
			SelectStoreFragment select = new SelectStoreFragment();
			select.setGps(gps);
			select.setOnSelectStoreCallback(this);
			transaction = manager.beginTransaction();
			transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
					R.anim.fade_in, R.anim.fade_out);
			transaction.add(R.id.rlMain, select, Tag.SELECT_STORE);
			transaction.addToBackStack(null);
			transaction.commit();
		}
		else {
			onSelectStore(null);
		}
	}

	@Override
	public void onSelectStore(StoreObj store) {
		GpsObj gps = getGps();
		setDefaultStore(store);
		if(TarkieLib.isSettingsEnabled(db, TIME_IN_PHOTO)) {
			Fragment fragment = manager.findFragmentByTag(Tag.SELECT_STORE);
			CameraFragment camera = new CameraFragment();
			camera.setGps(gps);
			camera.setStore(store);
			camera.setImageType(ImageType.TIME_IN);
			camera.setOnTimeInCallback(this);
			camera.setOnOverrideCallback(this);
			transaction = manager.beginTransaction();
			transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
					R.anim.slide_in_ltr, R.anim.slide_out_ltr);
			if(fragment != null) {
				transaction.add(R.id.rlMain, camera);
				transaction.hide(fragment);
			}
			else {
				transaction.replace(R.id.rlMain, camera);
			}
			transaction.addToBackStack(null);
			transaction.commit();
		}
		else {
			TimeInObj in = new TimeInObj();
			in.gps = gps;
			in.store = store;
			onTimeIn(in);
		}
	}

	@Override
	public void onCountdownFinish() {
		final AlertDialogFragment alert = new AlertDialogFragment();
		alert.setDialogTitle(R.string.no_gps_signal_title);
		alert.setDialogMessage(R.string.no_gps_signal_message);
		alert.setPositiveButton("Retry", new OnClickListener() {
			@Override
			public void onClick(View view) {
				manager.popBackStack();
				llTimeInMain.performClick();
			}
		});
		alert.setNegativeButton("Proceed", new OnClickListener() {
			@Override
			public void onClick(View view) {
				manager.popBackStack();
				onGpsFixed(getGps());
			}
		});
		transaction = manager.beginTransaction();
		transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
				R.anim.fade_in, R.anim.fade_out);
		transaction.add(R.id.rlMain, alert, DialogTag.MOCK);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public void onHighlightEntries(boolean isHighlight) {
		if(isHighlight) {
			btnSelectMain.setText(R.string.submit_caps);
		}
		else {
			btnSelectMain.setText(R.string.select_caps);
		}
	}

	@Override
	public void onMultiUpdate() {
		updateSyncCount();
	}

	@Override
	public void onSaveEntry(EntryObj entry) {
		setTab(TabType.ENTRIES);
		updateSyncCount();
		reloadEntries();
		reloadPhotos();
	}

	@Override
	public void onTimeIn(final TimeInObj in) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					StoreObj store = in.store;
					String storeID = store != null ? store.ID : null;
					boolean result = TarkieLib.saveTimeIn(db, in.gps, storeID, in.photo);
					timeInHandler.sendMessage(timeInHandler.
							obtainMessage(result ? Result.SUCCESS : Result.FAILED));
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	Handler timeInHandler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what) {
				case Result.SUCCESS:
					CodePanUtils.alertToast(MainActivity.this, "Time-in successful");
					manager.popBackStack(null, POP_BACK_STACK_INCLUSIVE);
					checkTimeIn();
					updateSyncCount();
					service.syncData(db);
					break;
				case Result.FAILED:
					CodePanUtils.alertToast(MainActivity.this, "Failed to save time-in.");
					break;
			}
			return true;
		}
	});

	@Override
	public void onTimeOut(final TimeOutObj out) {
		Fragment captured = manager.findFragmentByTag(Tag.CAPTURED);
		String timeInID = TarkieLib.getTimeInID(db);
		AttendanceObj attendance = TarkieLib.getAttendance(db, timeInID);
		attendance.out.dDate = out.dDate;
		attendance.out.dTime = out.dTime;
		SummaryFragment summary = new SummaryFragment();
		summary.setGps(out.gps);
		summary.setImage(out.photo);
		summary.setIsTimeOut(true);
		summary.setAttendance(attendance);
		summary.setOnOverrideCallback(this);
		transaction = manager.beginTransaction();
		transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
				R.anim.slide_in_ltr, R.anim.slide_out_ltr);
		if(captured != null) {
			transaction.add(R.id.rlMain, summary);
			transaction.hide(captured);
		}
		else {
			transaction.replace(R.id.rlMain, summary);
		}
		transaction.addToBackStack(null);
		transaction.commit();
	}
}
