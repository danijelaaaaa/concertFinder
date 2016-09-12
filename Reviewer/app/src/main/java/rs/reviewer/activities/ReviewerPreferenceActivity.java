package rs.reviewer.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import rs.reviewer.R;
import rs.reviewer.sync.SyncService;

public class ReviewerPreferenceActivity extends PreferenceActivity {

	public static boolean changed;
	public static boolean favChanged;
	public static boolean radiusChanged;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
//		addPreferencesFromResource(R.xml.preferences);
		getFragmentManager().beginTransaction().replace(android.R.id.content,
				new PrefsFragment()).commit();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	public static class PrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			changed = false;
			favChanged = false;
			radiusChanged = false;
			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.preferences);
		}

		@Override
		public void onResume() {
			super.onResume();
			getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			if(key.equals(getString(R.string.notif_for_fav_concerts_key))){
				favChanged = true;
			}

			if(key.equals(getString(R.string.pref_radius))){
				radiusChanged = true;
			}

			changed = true;
		}

		@Override
		public void onPause() {
			getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
			super.onPause();
		}
	}


	
}
