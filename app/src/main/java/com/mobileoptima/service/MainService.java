package com.mobileoptima.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.codepan.database.SQLiteAdapter;
import com.codepan.model.GpsObj;
import com.codepan.utils.CodePanUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.mobileoptima.cache.SQLiteCache;
import com.mobileoptima.constant.App;
import com.mobileoptima.constant.Incident;
import com.mobileoptima.constant.Key;
import com.mobileoptima.constant.Notification;
import com.mobileoptima.constant.Process;
import com.mobileoptima.constant.Receiver;
import com.mobileoptima.constant.RequestCode;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.core.TimeSecurity;
import com.mobileoptima.core.TimeSecurity.TimeSecurityType;
import com.mobileoptima.model.StoreObj;

import static com.google.android.gms.location.LocationServices.FusedLocationApi;


public class MainService extends Service implements LocationListener, ConnectionCallbacks,
		OnConnectionFailedListener {

	private final long GEO_FENCE_INTERVAL = 60000;
	private final long LOCATION_INTERVAL = 60000;
	private final long FASTEST_UPDATE_INTERVAL = 1000;
	private final long UPDATE_INTERVAL = 5000;
	private final float ACCURACY = 100;

	private final IBinder binder = new LocalBinder();
	private BroadcastReceiver batteryReceiver;
	private GoogleApiClient googleApiClient;
	private LocationRequest locationRequest;
	private LocalBroadcastManager manager;
	private long lastLocationUpdate;
	private LocationManager lm;
	private boolean isRunning;
	private Location location;
	private SQLiteAdapter db;
	private String provider;

	@Override
	public void onCreate() {
		super.onCreate();
		if(CodePanUtils.isPermissionGranted(this)) {
			lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			manager = LocalBroadcastManager.getInstance(this);
			db = SQLiteCache.getDatabase(this, App.DB);
			db.openConnection();
			buildGoogleApiClient();
			googleApiClient.connect();
		}
		else {
			stopSelf();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(CodePanUtils.isPermissionGranted(this)) {
			SQLiteAdapter db = getDatabase();
			if(intent != null && intent.hasExtra(Key.RECEIVER)) {
				handleReceiver(intent, db);
			}
			if(TarkieLib.isTimeIn(db)) {
				isRunning = true;
				startLocationUpdates();
				registerReceiver(db);
				runProcesses(db);
			}
			else {
				isRunning = false;
			}
		}
		return START_STICKY;
	}

	public void handleReceiver(Intent intent, SQLiteAdapter db) {
		final int type = intent.getIntExtra(Key.RECEIVER, 0);
		switch(type) {
			case Receiver.AIRPLANE_MODE:
				if(TarkieLib.isTimeIn(db)) {
					boolean isEnabled = intent.getBooleanExtra(Key.AIRPLANE_MODE, false);
					boolean result = TarkieLib.alternateIncident(db, getGps(), isEnabled,
							Incident.AIRPLANE_MODE_ON, Incident.AIRPLANE_MODE_OFF);
					if(result) sendBroadcast(Notification.AIRPLANE_MODE_STATUS);
				}
				break;
			case Receiver.BOOT_COMPLETE:
				TimeSecurity.saveBootDelay(db);
				TimeSecurity.updateServerTimeAfterBoot(db);
				if(TarkieLib.isTimeIn(db)) {
					boolean result = TarkieLib.saveIncident(db, getGps(),
							Incident.TURN_ON_PHONE);
					if(result) sendBroadcast();
				}
				break;
			case Receiver.SHUT_DOWN:
				TimeSecurity.saveLastKnownTime(db);
				if(TarkieLib.isTimeIn(db)) {
					boolean result = TarkieLib.saveIncident(db, getGps(),
							Incident.TURN_OFF_PHONE);
					if(result) sendBroadcast();
				}
				break;
			case Receiver.GPS_STATUS:
				if(TarkieLib.isTimeIn(db)) {
					boolean isEnabled = CodePanUtils.isGpsEnabled(this);
					TarkieLib.alternateIncident(db, getGps(), isEnabled,
							Incident.TURN_ON_GPS, Incident.TURN_OFF_GPS);
					sendBroadcast(Notification.GPS_STATUS);
				}
				break;
			case Receiver.CONNECTIVITY_CHANGE:
				if(TarkieLib.isTimeIn(db)) {
					boolean isEnabled = CodePanUtils.isGpsEnabled(this);
					boolean result = TarkieLib.alternateIncident(db, getGps(), isEnabled,
							Incident.MOBILE_DATA_ON, Incident.MOBILE_DATA_OFF);
					if(result) sendBroadcast(Notification.MOBILE_DATA_STATUS);
				}
				break;
			case Receiver.TIME_CHANGE:
				tamperedTime(TimeSecurityType.TIME_CHANGED);
				break;
			case Receiver.DATE_CHANGE:
				tamperedTime(TimeSecurityType.DATE_CHANGED);
				break;
			case Receiver.TIME_ZONE_CHANGE:
				tamperedTime(TimeSecurityType.TIME_ZONE_CHANGED);
				break;
		}
	}

	private void tamperedTime(TimeSecurityType type) {
		if(!TimeSecurity.isRightTime(db)) {
			TimeSecurity.updateTimeSecurity(db, type, true);
			if(TarkieLib.isTimeIn(db)) {
				boolean result = TarkieLib.saveIncident(db, getGps(), Incident.TAMPERED_TIME);
				if(result) sendBroadcast(Notification.TAMPERED_TIME);
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return this.binder;
	}

	protected synchronized void buildGoogleApiClient() {
		googleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();
		createLocationRequest();
	}

	protected void createLocationRequest() {
		locationRequest = new LocationRequest();
		locationRequest.setInterval(UPDATE_INTERVAL);
		locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}

	protected void startLocationUpdates() {
		if(googleApiClient.isConnected()) {
			try {
				FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
			}
			catch(SecurityException se) {
				se.printStackTrace();
			}
		}
	}

	protected void stopLocationUpdates() {
		if(googleApiClient.isConnected()) {
			FusedLocationApi.removeLocationUpdates(googleApiClient, this);
		}
	}

	public GpsObj getGps() {
		GpsObj gps = CodePanUtils.getGps(this, location, lastLocationUpdate,
				UPDATE_INTERVAL, ACCURACY);
		if(TarkieLib.isTimeIn(db)) {
			checkGpsSignal(db, gps);
			checkMockStatus(db, gps);
		}
		return gps;
	}

	@Override
	public void onLocationChanged(Location location) {
		this.lastLocationUpdate = SystemClock.elapsedRealtime();
		this.location = location;
		try {
			Location network = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if(network != null && location != null) {
				if(network.getLongitude() == location.getLongitude() &&
						network.getLatitude() == location.getLatitude()) {
					this.provider = network.getProvider();
				}
			}
			Location gps = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if(gps != null && location != null) {
				if(gps.getTime() == location.getTime()) {
					this.provider = gps.getProvider();
				}
			}
		}
		catch(SecurityException e) {
			e.printStackTrace();
		}
	}

	public void runProcesses(SQLiteAdapter db) {
		if(!CodePanUtils.isThreadRunning(Process.GEO_FENCING)) {
			checkGeoFence(db);
		}
		if(!CodePanUtils.isThreadRunning(Process.LOCATION)) {
			checkLocation(db);
		}
	}

	public void checkLocation(final SQLiteAdapter db) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					GpsObj gps = getGps();
					if(gps.isValid) {
						TarkieLib.saveLocation(db, gps, provider);
					}
					Thread.sleep(LOCATION_INTERVAL);
					if(isRunning) {
						checkLocation(db);
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.setName(Process.LOCATION);
		bg.start();
	}

	public void checkGeoFence(final SQLiteAdapter db) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					StoreObj store = TarkieLib.getDefaultStore(db);
					if(TarkieLib.isTimeIn(db) && store != null) {
						if(getGps().isValid) {
							Location center = new Location(LocationManager.GPS_PROVIDER);
							center.setLongitude(store.longitude);
							center.setLatitude(store.latitude);
							float distance = center.distanceTo(location);
							boolean isInside = distance <= store.radius;
							boolean result = TarkieLib.alternateIncident(db, getGps(), isInside,
									Incident.INSIDE_GEO_FENCE, Incident.OUTSIDE_GEO_FENCE);
							if(result) sendBroadcast(Notification.GEO_FENCE_STATUS);
						}
					}
					Thread.sleep(GEO_FENCE_INTERVAL);
					if(isRunning) {
						checkGeoFence(db);
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.setName(Process.GEO_FENCING);
		bg.start();
	}

	public void checkGpsSignal(final SQLiteAdapter db, final GpsObj gps) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean result = TarkieLib.alternateIncident(db, gps, gps.isValid,
						Incident.GPS_ACQUIRED, Incident.NO_GPS_SIGNAL);
				if(result) sendBroadcast();
			}
		});
		bg.start();
	}

	public void checkMockStatus(final SQLiteAdapter db, final GpsObj gps) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean isEnabled = CodePanUtils.isMockEnabled(MainService.this);
				boolean result = TarkieLib.alternateIncident(db, gps, isEnabled,
						Incident.MOCK_LOCATION_ON, Incident.MOCK_LOCATION_OFF);
				if(result) sendBroadcast();
			}
		});
		bg.start();
	}

	@Override
	public void onConnected(Bundle bundle) {
		if(location == null) {
			try {
				location = FusedLocationApi.getLastLocation(googleApiClient);
			}
			catch(SecurityException se) {
				se.printStackTrace();
			}
		}
		startLocationUpdates();
	}

	@Override
	public void onConnectionSuspended(int i) {
		googleApiClient.connect();
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult result) {
		Log.e("Google API Client", "Connection Failed");
	}

	public SQLiteAdapter getDatabase() {
		if(db == null) {
			db = SQLiteCache.getDatabase(this, App.DB);
			db.openConnection();
		}
		return this.db;
	}

	public class LocalBinder extends Binder {
		public MainService getService() {
			return MainService.this;
		}
	}

	public void registerReceiver(final SQLiteAdapter db) {
		batteryReceiver = batteryReceiver != null ? batteryReceiver :
				new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						int incident = Incident.LOW_BATTERY;
						int level = CodePanUtils.getBatteryLevel(context);
						if(level < App.BATTERY_THRESHOLD && !TarkieLib.hasIncident(db, incident)) {
							boolean result = TarkieLib.saveIncident(db, getGps(), incident, level);
							if(result) sendBroadcast();
						}
					}
				};
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batteryReceiver, intentFilter);
	}

	public void unregisterReceiver() {
		if(batteryReceiver != null) {
			unregisterReceiver(batteryReceiver);
		}
	}

	public void sendBroadcast() {
		Intent intent = new Intent(RequestCode.NOTIFICATION);
		manager.sendBroadcast(intent);
	}

	public void sendBroadcast(int notification) {
		Intent intent = new Intent(RequestCode.NOTIFICATION);
		intent.putExtra(Key.NOTIFICATION, notification);
		manager.sendBroadcast(intent);
	}

	public void sendBroadcast(Intent intent) {
		manager.sendBroadcast(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopLocationUpdates();
		unregisterReceiver();
		isRunning = false;
	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		super.onTaskRemoved(rootIntent);
		stopLocationUpdates();
		if(TarkieLib.isTimeIn(db)) {
			restartService();
		}
	}

	public void restartService() {
		Intent intent = new Intent(this, getClass());
		intent.setPackage(getPackageName());
		PendingIntent pi = PendingIntent.getService(this,
				RequestCode.SERVICE, intent, PendingIntent.FLAG_ONE_SHOT);
		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		long trigger = SystemClock.elapsedRealtime() + 1000;
		alarm.set(AlarmManager.ELAPSED_REALTIME, trigger, pi);
	}
}
