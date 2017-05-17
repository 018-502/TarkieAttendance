package com.mobileoptima.tarkieattendance;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
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
import com.codepan.widget.CircularImageView;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.callback.Interface.OnCountdownFinishCallback;
import com.mobileoptima.callback.Interface.OnGpsFixedCallback;
import com.mobileoptima.callback.Interface.OnHighlightEntriesCallback;
import com.mobileoptima.callback.Interface.OnMultiUpdateCallback;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.callback.Interface.OnSaveEntryCallback;
import com.mobileoptima.callback.Interface.OnTimeValidatedCallback;
import com.mobileoptima.constant.App;
import com.mobileoptima.constant.DialogTag;
import com.mobileoptima.constant.ImageType;
import com.mobileoptima.constant.Key;
import com.mobileoptima.constant.Module.Action;
import com.mobileoptima.constant.Notification;
import com.mobileoptima.constant.RequestCode;
import com.mobileoptima.constant.TabType;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.core.TimeSecurity;
import com.mobileoptima.model.BreakInObj;
import com.mobileoptima.model.BreakObj;
import com.mobileoptima.model.EmployeeObj;
import com.mobileoptima.model.EntryObj;
import com.mobileoptima.model.StoreObj;
import com.mobileoptima.service.MainService;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;
import static com.mobileoptima.callback.Interface.OnInitializeCallback;
import static com.mobileoptima.callback.Interface.OnLoginCallback;

public class MainActivity extends FragmentActivity implements OnClickListener, OnRefreshCallback,
		OnOverrideCallback, OnLoginCallback, OnInitializeCallback, ServiceConnection,
		OnTimeValidatedCallback, OnGpsFixedCallback, OnCountdownFinishCallback,
		OnHighlightEntriesCallback, OnMultiUpdateCallback, OnSaveEntryCallback {

	private boolean isInitialized, isOverridden, isServiceConnected, isPause, isSecured, isGpsOff;
	private CodePanLabel tvHomeMain, tvVisitsMain, tvInventoryMain, tvPhotosMain, tvEntriesMain,
			tvTimeInMain, tvSyncMain, tvLastSyncMain, tvEmployeeNameMain, tvEmployeeNoMain;
	private CodePanButton btnNotificationMain, btnSyncMain, btnHomeMain, btnVisitsMain, btnInventoryMain,
			btnPhotosMain, btnEntriesMain, btnSelectMain;
	private ImageView ivHomeMain, ivVisitsMain, ivInventoryMain, ivPhotosMain, ivEntriesMain;
	private OnPermissionGrantedCallback permissionGrantedCallback;
	private OnBackPressedCallback backPressedCallback;
	private LocalBroadcastManager broadcastManager;
	private ImageView ivTimeInMain, ivTimeOutMain;
	private RelativeLayout rlMain, rlMenuMain;
	private CircularImageView ivEmployeeMain;
	private String tabType = TabType.DEFAULT;
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
		ivHomeMain = (ImageView) findViewById(R.id.ivHomeMain);
		ivVisitsMain = (ImageView) findViewById(R.id.ivVisitsMain);
		ivInventoryMain = (ImageView) findViewById(R.id.ivInventoryMain);
		ivPhotosMain = (ImageView) findViewById(R.id.ivPhotosMain);
		ivEntriesMain = (ImageView) findViewById(R.id.ivEntriesMain);
		tvHomeMain = (CodePanLabel) findViewById(R.id.tvHomeMain);
		tvVisitsMain = (CodePanLabel) findViewById(R.id.tvVisitsMain);
		tvInventoryMain = (CodePanLabel) findViewById(R.id.tvInventoryMain);
		tvPhotosMain = (CodePanLabel) findViewById(R.id.tvPhotosMain);
		tvEntriesMain = (CodePanLabel) findViewById(R.id.tvEntriesMain);
		tvTimeInMain = (CodePanLabel) findViewById(R.id.tvTimeInMain);
		tvSyncMain = (CodePanLabel) findViewById(R.id.tvSyncMain);
		tvLastSyncMain = (CodePanLabel) findViewById(R.id.tvLastSyncMain);
		tvEmployeeNameMain = (CodePanLabel) findViewById(R.id.tvEmployeeNameMain);
		tvEmployeeNoMain = (CodePanLabel) findViewById(R.id.tvEmployeeNoMain);
		ivEmployeeMain = (CircularImageView) findViewById(R.id.ivEmployeeMain);
		ivTimeInMain = (ImageView) findViewById(R.id.ivTimeInMain);
		ivTimeOutMain = (ImageView) findViewById(R.id.ivTimeOutMain);
		btnNotificationMain = (CodePanButton) findViewById(R.id.btnNotificationMain);
		btnSyncMain = (CodePanButton) findViewById(R.id.btnSyncMain);
		btnHomeMain = (CodePanButton) findViewById(R.id.btnHomeMain);
		btnVisitsMain = (CodePanButton) findViewById(R.id.btnVisitsMain);
		btnInventoryMain = (CodePanButton) findViewById(R.id.btnInventoryMain);
		btnPhotosMain = (CodePanButton) findViewById(R.id.btnPhotosMain);
		btnEntriesMain = (CodePanButton) findViewById(R.id.btnEntriesMain);
		btnSelectMain = (CodePanButton) findViewById(R.id.btnSelectMain);
		rlMenuMain = (RelativeLayout) findViewById(R.id.rlMenuMain);
		rlMain = (RelativeLayout) findViewById(R.id.rlMain);
		dlMain = (DrawerLayout) findViewById(R.id.dlMain);
		llTimeInMain = (LinearLayout) findViewById(R.id.llTimeInMain);
		findViewById(R.id.llAttendanceMain).setOnClickListener(this);
		findViewById(R.id.llBreaksMain).setOnClickListener(this);
		findViewById(R.id.llUpdateMasterFileMain).setOnClickListener(this);
		findViewById(R.id.llSendBackUpMain).setOnClickListener(this);
		findViewById(R.id.llSupportMain).setOnClickListener(this);
		findViewById(R.id.llSettingsMain).setOnClickListener(this);
		findViewById(R.id.llLocationsMain).setOnClickListener(this);
		findViewById(R.id.llLogoutMain).setOnClickListener(this);
		findViewById(R.id.btnMenuMain).setOnClickListener(this);
		findViewById(R.id.btnSearchMain).setOnClickListener(this);
		llTimeInMain.setOnClickListener(this);
		btnSelectMain.setOnClickListener(this);
		btnHomeMain.setOnClickListener(this);
		btnVisitsMain.setOnClickListener(this);
		btnInventoryMain.setOnClickListener(this);
		btnPhotosMain.setOnClickListener(this);
		btnEntriesMain.setOnClickListener(this);
		btnNotificationMain.setOnClickListener(this);
		btnSyncMain.setOnClickListener(this);
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
						UpgradeFragment upgrade = new UpgradeFragment();
						transaction.add(R.id.flContainerMain, upgrade, TabType.VISITS);
					}
					if(current != null) {
						transaction.hide(current);
					}
					transaction.commit();
				}
				setTabType(TabType.VISITS);
				break;
			case R.id.btnInventoryMain:
				if(!tabType.equals(TabType.INVENTORY)) {
					Fragment current = manager.findFragmentByTag(tabType);
					Fragment old = manager.findFragmentByTag(TabType.INVENTORY);
					transaction = manager.beginTransaction();
					if(old != null) {
						transaction.show(old);
					}
					else {
						UpgradeFragment upgrade = new UpgradeFragment();
						transaction.add(R.id.flContainerMain, upgrade, TabType.INVENTORY);
					}
					if(current != null) {
						transaction.hide(current);
					}
					transaction.commit();
				}
				setTabType(TabType.INVENTORY);
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
					if(!TarkieLib.hasUnsubmittedEntries(db)) {
						final AlertDialogFragment alert = new AlertDialogFragment();
						alert.setDialogTitle("Confirm Time-out");
						alert.setDialogMessage("Do you want to time-out?");
						alert.setPositiveButton("Yes", new OnClickListener() {
							@Override
							public void onClick(View view) {
								manager.popBackStack();
								CameraFragment camera = new CameraFragment();
								camera.setGps(getGps());
								camera.setOnOverrideCallback(MainActivity.this);
								camera.setImageType(ImageType.TIME_OUT);
								camera.setDate(CodePanUtils.getDate());
								camera.setTime(CodePanUtils.getTime());
								transaction = manager.beginTransaction();
								transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
										R.anim.slide_in_ltr, R.anim.slide_out_ltr);
								transaction.add(R.id.rlMain, camera);
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
				transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
						R.anim.fade_in, R.anim.fade_out);
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
		}
	}

	private void setTabType(String tabType) {
		View rlSyncMain = findViewById(R.id.rlSyncMain);
		View rlSearchMain = findViewById(R.id.rlSearchMain);
		View rlNotifMain = findViewById(R.id.rlNotifMain);
		resetTab();
		switch(tabType) {
			case TabType.HOME:
				tvHomeMain.setEnabled(true);
				ivHomeMain.setImageResource(R.drawable.ic_home_enabled);
				rlNotifMain.setVisibility(View.VISIBLE);
				rlSyncMain.setVisibility(View.VISIBLE);
				rlSearchMain.setVisibility(View.GONE);
				btnSelectMain.setVisibility(View.GONE);
				break;
			case TabType.VISITS:
				tvVisitsMain.setEnabled(true);
				ivVisitsMain.setImageResource(R.drawable.ic_visits_enabled);
				rlNotifMain.setVisibility(View.VISIBLE);
				rlSyncMain.setVisibility(View.VISIBLE);
				rlSearchMain.setVisibility(View.GONE);
				btnSelectMain.setVisibility(View.GONE);
				break;
			case TabType.INVENTORY:
				tvInventoryMain.setEnabled(true);
				ivInventoryMain.setImageResource(R.drawable.ic_inventory_enabled);
				rlNotifMain.setVisibility(View.VISIBLE);
				rlSyncMain.setVisibility(View.VISIBLE);
				rlSearchMain.setVisibility(View.GONE);
				btnSelectMain.setVisibility(View.GONE);
				break;
			case TabType.PHOTOS:
				tvPhotosMain.setEnabled(true);
				ivPhotosMain.setImageResource(R.drawable.ic_photos_enabled);
				rlNotifMain.setVisibility(View.VISIBLE);
				rlSyncMain.setVisibility(View.VISIBLE);
				rlSearchMain.setVisibility(View.GONE);
				btnSelectMain.setVisibility(View.GONE);
				break;
			case TabType.ENTRIES:
				tvEntriesMain.setEnabled(true);
				ivEntriesMain.setImageResource(R.drawable.ic_entries_enabled);
				rlNotifMain.setVisibility(View.GONE);
				rlSyncMain.setVisibility(View.GONE);
				rlSearchMain.setVisibility(View.VISIBLE);
				btnSelectMain.setVisibility(View.VISIBLE);
				break;
		}
		this.tabType = tabType;
	}

	public void resetTab() {
		tvHomeMain.setEnabled(false);
		tvVisitsMain.setEnabled(false);
		tvInventoryMain.setEnabled(false);
		tvPhotosMain.setEnabled(false);
		tvEntriesMain.setEnabled(false);
		ivHomeMain.setImageResource(R.drawable.ic_home_disabled);
		ivVisitsMain.setImageResource(R.drawable.ic_visits_disabled);
		ivInventoryMain.setImageResource(R.drawable.ic_inventory_disabled);
		ivPhotosMain.setImageResource(R.drawable.ic_photos_disabled);
		ivEntriesMain.setImageResource(R.drawable.ic_entries_disabled);
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
		setReceiver();
		registerReceiver();
		updateSyncCount();
		updateLastSynced();
	}

	public void setDefaultTab() {
		if(btnHomeMain != null) {
			btnHomeMain.performClick();
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
		reloadForms();
		reloadEntries();
	}

	@Override
	public void onOverride(boolean isOverridden) {
		this.isOverridden = isOverridden;
	}

	@Override
	public void onBackPressed() {
		if(isInitialized && isSecured) {
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

	public void reloadForms() {
		if(!isPause) {
			Fragment fragment = manager.findFragmentByTag(TabType.HOME);
			if(fragment != null) {
				HomeFragment home = (HomeFragment) fragment;
				home.loadItems(db);
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

	public void reloadPhotos() {
		if(!isPause) {
			Fragment fragment = manager.findFragmentByTag(TabType.PHOTOS);
			if(fragment != null) {
				PhotosFragment photos = (PhotosFragment) fragment;
				photos.loadPhotos(db);
			}
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
			case TabType.INVENTORY:
				btnInventoryMain.performClick();
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
		SelectStoreFragment select = new SelectStoreFragment();
		select.setGps(gps);
		select.setOnOverrideCallback(this);
		transaction = manager.beginTransaction();
		transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
				R.anim.fade_in, R.anim.fade_out);
		transaction.add(R.id.rlMain, select);
		transaction.addToBackStack(null);
		transaction.commit();
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
			btnSelectMain.setText(R.string.submit);
		}
		else {
			btnSelectMain.setText(R.string.select);
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
}
