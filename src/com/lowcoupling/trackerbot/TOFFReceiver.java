package com.lowcoupling.trackerbot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TOFFReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		DataSource lds = new DataSource(context);
		lds.open();
		long seconds =System.currentTimeMillis()/1000;
		lds.createEvent("Turned OFF", -1, seconds);
		lds.close();
	}
}