package com.lowcoupling.trackerbot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class TONReceiver extends BroadcastReceiver {


	@Override
	public void onReceive(Context context, Intent arg1) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		Boolean always_on = sharedPref.getBoolean("tracking_always_on", false);
		if(always_on){
			Intent startServiceIntent = new Intent(context, LocationService.class);
			context.startService(startServiceIntent);
		}
		DataSource lds = new DataSource(context);
		lds.open();
		long seconds =System.currentTimeMillis()/1000;
		lds.createEvent("Turned ON", -1, seconds);
		lds.close();
	}
}