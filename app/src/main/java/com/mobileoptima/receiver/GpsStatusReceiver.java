package com.mobileoptima.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mobileoptima.constant.Key;
import com.mobileoptima.constant.Receiver;
import com.mobileoptima.service.MainService;

public class GpsStatusReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent service = new Intent(context, MainService.class);
		service.putExtra(Key.RECEIVER, Receiver.GPS_STATUS);
		context.startService(service);
	}
}
