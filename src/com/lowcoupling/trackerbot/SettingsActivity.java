package com.lowcoupling.trackerbot;

import com.lowcoupling.trackerbot.R;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity
{
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        final ActionBar actionBar = getActionBar();
		actionBar.setTitle("TrackerBot");
		SharedPreferences.OnSharedPreferenceChangeListener spChanged = new
				SharedPreferences.OnSharedPreferenceChangeListener() {

			@Override
			public void onSharedPreferenceChanged(
					SharedPreferences arg0, String arg1) {
				if(arg0.contains("tracking_always_on")){

				}
				if(arg0.contains("sms_1")){
//					String num = arg0.getString("sms_1", "");
//					Preference pref = findPreference("sms_1");
//					pref.setSummary(num);
				}

			}
			// your stuff here		
		};
//		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
//		String num = sharedPref.getString("sms_1", "");
//		Preference pref = findPreference("sms_1");
//		pref.setSummary(num);
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}