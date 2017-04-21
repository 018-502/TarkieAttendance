package com.mobileoptima.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mobileoptima.constant.Key;
import com.mobileoptima.constant.Receiver;
import com.mobileoptima.service.MainService;

public class AirplaneModeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		boolean status = intent.getBooleanExtra("state", false);
		Intent service = new Intent(context, MainService.class);
		service.putExtra(Key.RECEIVER, Receiver.AIRPLANE_MODE);
		service.putExtra(Key.AIRPLANE_MODE, status);
		context.startService(service);
	}
}
