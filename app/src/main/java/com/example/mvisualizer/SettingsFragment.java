package com.example.mvisualizer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.example.mvisualizer.AudioVisuals.VisualizerView;


public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {
    static final String TAG = SettingsFragment.class.getSimpleName();
    private EditTextPreference editTextPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.visualizer_preferences);
        editTextPreference=findPreference(getString(R.string.size_key));
        Preference preference=findPreference(getString(R.string.size_key));
        preference.setOnPreferenceChangeListener(this);


        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();

          // Go through all of the preferences, and set up their preference summary.
                  for (int i = 0; i < count; i++) {
                      Preference p = prefScreen.getPreference(i);
                      if (p instanceof EditTextPreference) {
                          String value = sharedPreferences.getString(p.getKey(), "");
                          p.setSummary(value);

                      }
                  }

    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Log.d(TAG, "Share preferences triggered");

        // Figure out which preference was changed
        if (key.equals(getString(R.string.size_key))) {
            String currentSizeValue = sharedPreferences.getString(key, getString(R.string.size_default));
            Log.d(TAG, currentSizeValue);
            editTextPreference.setSummary(currentSizeValue);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Toast error = Toast.makeText(getContext(), "Please select a number between 0.1 and 3", Toast.LENGTH_SHORT);

        String sizeKey = getString(R.string.size_key);
        if (preference.getKey().equals(sizeKey)) {
            String stringSize = (String) newValue;
            try {
                float size = Float.parseFloat(stringSize);
                if (size > 3 || size <= 0 ) {
                    error.show();
                    return false;
                }
            } catch (NumberFormatException nfe) {
                error.show();
                return false;
            }
        }
        return true;
    }
}
