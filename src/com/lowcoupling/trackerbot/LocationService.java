package com.lowcoupling.trackerbot;

import java.util.Iterator;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.widget.Toast;

public class LocationService extends Service implements android.location.LocationListener {

	public class LSBinder extends Binder{
		LocationService getService() {
			return LocationService.this;
		}
	}

	private com.lowcoupling.trackerbot.Location currentLocation = null;
	private com.lowcoupling.trackerbot.Location nextLocation = null;
	private int obs=0;
	private LocationManager locationManager;
	private String provider;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// Get the location manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		locationManager.requestLocationUpdates(provider, 10000, 0, this);
		Toast.makeText(this, "Starting Location Service",
				Toast.LENGTH_SHORT).show();

	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return  null; //mBinder;
	}


	@Override
	public void onLocationChanged(android.location.Location location) {
		// TODO Auto-generated method stub
		com.lowcoupling.trackerbot.Location lc = getLocationFromPosition(location.getLatitude(),location.getLongitude());
		DataSource lds = new DataSource(this.getBaseContext());
		long seconds =System.currentTimeMillis()/1000;
		//Toast.makeText(this, "Location Update",
		//		Toast.LENGTH_SHORT).show();
		if (lc==null && currentLocation==null){
			return;
		}

		lds.open();

		//if the location is not null
		if(lc!=null){
			if(nextLocation!=null){
				if(nextLocation.getName().equals(lc.getName())){
					obs=obs+1;
					//System.out.println("incrementing "+lc.getName());
				}else{
					nextLocation=lc;
					obs=0;
				}
			}else{
				nextLocation=lc;
				obs=0;
			}
		}else{
			//if the location is null then it means the current Location is not
			if(nextLocation==null){
				obs=obs+1;
				//System.out.println("incrementing null");
			}else{
				nextLocation=null;
				obs=0;
			}
		}

		if(obs==3){
			//System.out.println("SUCCESS");
			if(currentLocation!=null){
				if(lc!=null){
					if(!currentLocation.getName().equals(lc.getName())){
						Toast.makeText(this, "You are leaving "+currentLocation.getName(),
								Toast.LENGTH_SHORT).show();
						lds.createEvent(currentLocation.getName(), 2, seconds);
						SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
						Boolean smsOn = sharedPref.getBoolean("sms_on", false);
						if(smsOn){
							String tel1 =  sharedPref.getString("sms_1", "");
							try{
								SmsManager.getDefault().sendTextMessage(tel1,null,"I am leaving "+currentLocation.getName(),null,null);
							}catch (Exception e){
								
							}
						}
					}else{
						return;
					}
				}else{
					Toast.makeText(this, "You are leaving "+currentLocation.getName(),
							Toast.LENGTH_SHORT).show();
					lds.createEvent(currentLocation.getName(), 2, seconds);
					SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
					Boolean smsOn = sharedPref.getBoolean("sms_on", false);
					if(smsOn){
						String tel1 =  sharedPref.getString("sms_1", "");
						try{
							SmsManager.getDefault().sendTextMessage(tel1,null,"I am leaving "+currentLocation.getName(),null,null);
						}catch (Exception e){
							
						}
					}
				}
			}
			if(lc!=null){
				Toast.makeText(this, "You are entering "+lc.getName(),
						Toast.LENGTH_SHORT).show();
				lds.createEvent(lc.getName(), 1, seconds);
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
				Boolean smsOn = sharedPref.getBoolean("sms_on", false);
				if(smsOn){
					String tel1 =  sharedPref.getString("sms_1", "");
					try{
						SmsManager.getDefault().sendTextMessage(tel1,null,"I am at "+lc.getName(),null,null);
					}catch (Exception e){
						
					}
				}
			}

			nextLocation=null;
			obs=0;
			currentLocation=lc;
		}

		lds.close();

		//Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	public com.lowcoupling.trackerbot.Location getLocationFromPosition(double lat, double lng){
		DataSource lds = new DataSource(this.getBaseContext());
		lds.open();
		List<com.lowcoupling.trackerbot.Location> locations = lds.getAllLocations();
		Iterator<com.lowcoupling.trackerbot.Location> locIt = locations.iterator();
		com.lowcoupling.trackerbot.Location ret = null;
		while (locIt.hasNext()){
			com.lowcoupling.trackerbot.Location lc = locIt.next();
			double distance = distFrom(lc.getLatitude(),lc.getLongitude(),lat,lng);
			if(distance <= lc.getRange()){
				ret = lc;
			}

		}
		return ret;
	}

	private double distFrom(double lat1, double lng1, double lat2, double lng2) {
		double earthRadius = 3958.75;
		double dLat = Math.toRadians(lat2-lat1);
		double dLng = Math.toRadians(lng2-lng1);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
				Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
				Math.sin(dLng/2) * Math.sin(dLng/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double dist = earthRadius * c;

		int meterConversion = 1609;

		return  (dist * meterConversion);
	}
	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

}
