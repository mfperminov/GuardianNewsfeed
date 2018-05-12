package com.example.mperminov.guardiannewsfeed;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.DatePicker;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

    }

    public static class NewsfeedPreferenceFragment extends PreferenceFragment
            implements DatePickerDialog.OnDateSetListener, Preference.OnPreferenceChangeListener {
        public Preference dateFrom;
        private String newDateFromString;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);
            //find all preferences and bind'em all
            dateFrom = findPreference(getString(R.string.settings_date_from_key));
            bindPreferenceSummaryToValue(dateFrom);
            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);
            Preference topic = findPreference(getString(R.string.settings_topic_key));
            bindPreferenceSummaryToValue(topic);
            dateFrom.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showDateDialog();
                    return false;
                }
            });

        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            newDateFromString = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth);
            dateFrom.setSummary(newDateFromString);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            //write change of date to SharedPreference
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.settings_date_from_key), newDateFromString);
            editor.apply();

        }

        private void showDateDialog() {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            new DatePickerDialog(getActivity(), this, year, month, day).show();
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            // The code in this method takes care of updating
            // the displayed preference summary after it has been changed
            String stringValue = newValue.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences =
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }
    }
}
