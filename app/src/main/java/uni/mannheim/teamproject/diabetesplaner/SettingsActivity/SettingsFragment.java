package uni.mannheim.teamproject.diabetesplaner.SettingsActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import uni.mannheim.teamproject.diabetesplaner.EntryScreenActivity;
import uni.mannheim.teamproject.diabetesplaner.R;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG = SettingsFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        //checks if Preference with key is EditTextPreference
        //if so puts the edited text to the summary field
        //TODO save and handle the settings
        if(findPreference(key) instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) findPreference(key);
            EditText nameDialog = editTextPref.getEditText();

            if (editTextPref.getKey().equals("pref_key_name")) {
                editTextPref.setSummary(nameDialog.getText());

                View header = EntryScreenActivity.navigationView.getHeaderView(0);
                TextView name = (TextView) header.findViewById(R.id.username);
                name.setText(nameDialog.getText());
               // Log.d(TAG, String.valueOf(getActivity().findViewById(R.id.username)));

            } else if (editTextPref.getKey().equals("pref_key_weight")) {
                if (nameDialog.getText().toString().matches("\\d+\\s?kg")) {
                    editTextPref.setSummary(nameDialog.getText());
                } else if (nameDialog.getText().toString().matches("\\d+")) {
                    editTextPref.setSummary(nameDialog.getText() + " kg");
                }
            } else if (editTextPref.getKey().equals("pref_key_bloodsugar")) {
                if (nameDialog.getText().toString().matches("\\d+\\s?mmol/L")) {
                    editTextPref.setSummary(nameDialog.getText());
                } else if (nameDialog.getText().toString().matches("\\d+")) {
                    editTextPref.setSummary(nameDialog.getText() + " mmol/L");
                }
            }
        }



    }


    public void onPreferenceClickListener(Preference Preference) {
    }
}
