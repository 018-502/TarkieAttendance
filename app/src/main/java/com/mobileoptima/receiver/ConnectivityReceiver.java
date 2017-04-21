package com.mobileoptima.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mobileoptima.constant.Key;
import com.mobileoptima.constant.Receiver;
import com.mobileoptima.service.MainService;

public class ConnectivityReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if(info.getType() == ConnectivityManager.TYPE_MOBILE) {
			Intent service = new Intent(context, MainService.class);
			service.putExtra(Key.RECEIVER, Receiver.CONNECTIVITY_CHANGE);
			service.putExtra(Key.MOBILE_DATA, info.isConnected());
			context.startService(service);
		}
	}
}
