package org.ragecastle.movies_udacity;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jahall on 11/1/15.
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SettingsFragment())
                    .commit();
        }
    }

    public static class SettingsFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

         @Override
         public void onCreate(Bundle savedInstanceState) {
             super.onCreate(savedInstanceState);

             addPreferencesFromResource(R.xml.pref_general);
             bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sort_key)));
         }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.getOnPreferenceChangeListener();
            preference.getPreferenceManager().getSharedPreferences();

        onPreferenceChange(preference, PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), ""));

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();

            if(preference instanceof ListPreference){

                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if(prefIndex >= 0){
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            }
            else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    }

}
