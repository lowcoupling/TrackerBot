package com.lowcoupling.trackerbot;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.lowcoupling.trackerbot.LocationService.LSBinder;

public class MainActivity extends Activity implements ActionBar.TabListener {

	private ViewPager pageView;
	private LocationRequest mLocationRequest;
	private SharedPreferences mPrefs;
	private Editor mEditor;
	private boolean mUpdatesRequested;
	private LocationService locationService;
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className,
				IBinder service) {
			// We've bound to LocalService, cast the IBinder and get LocalService instance
			LSBinder binder = (LSBinder) service;
			locationService = binder.getService();

		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				MainActivity.this.finish();
			}
		});

		
		setContentView(R.layout.activity_locations_mgmt);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setTitle("TrackerBot");
		actionBar.setIcon(null);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		SectionsPagerAdapter pageAdapter = new SectionsPagerAdapter(getFragmentManager());
		pageView = (ViewPager) findViewById(R.id.pager);
		pageView.setAdapter(pageAdapter);
		pageView.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		actionBar.addTab(actionBar.newTab().setText("Locations").setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("Events").setTabListener(this));

		mLocationRequest = LocationRequest.create();
		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		String refreshPeriodString = sharedPref.getString("position_refresh_period", "1");
		int refreshPeriod = 1;
		try {
			refreshPeriod = Integer.parseInt(refreshPeriodString);
		}catch (Exception e){
			
		}
		(mLocationRequest).setInterval(refreshPeriod*60000);
		mLocationRequest.setFastestInterval(refreshPeriod*60000);

		// Open the shared preferences
		mPrefs = getSharedPreferences("SharedPreferences",
				Context.MODE_PRIVATE);
		// Get a SharedPreferences editor
		mEditor = ((SharedPreferences) mPrefs).edit();
		if(!isMyServiceRunning(LocationService.class)){
			//Intent intent = new Intent(this, LocationService.class);
			Intent servIntent = new Intent("com.lowcoupling.trackerbot.LocationService");
			startService(servIntent);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		/*
		 * Get any previous setting for location updates
		 * Gets "false" if an error occurs
		 */
		if (mPrefs.contains("KEY_UPDATES_ON")) {
			mUpdatesRequested =
					mPrefs.getBoolean("KEY_UPDATES_ON", false);

			// Otherwise, turn off location updates
		} else {
			mEditor.putBoolean("KEY_UPDATES_ON", false);
			mEditor.commit();
		}
		getActionBar().setIcon(null);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		Boolean alwaysOn = sharedPref.getBoolean("tracking_always_on", false);
		if(!alwaysOn){
			Toast.makeText(this, "Closing Track ME",
					Toast.LENGTH_SHORT).show();
			stopService(new Intent(this,LocationService.class));
		}
	}


	@Override
	protected void onPause() {
		super.onPause();
		// Save the current setting for updates
		mEditor.putBoolean("KEY_UPDATES_ON", mUpdatesRequested);
		mEditor.commit();
		super.onPause();
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.locations_mgmt, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		}

		if (id== R.id.action_about){
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		pageView.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {



		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			if(position==0){
				return new LocationsFragment();
			}else{
				return new EventsFragment();
			}
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}



	private boolean isMyServiceRunning(Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
}
