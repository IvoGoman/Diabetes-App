package uni.mannheim.teamproject.diabetesplaner.SettingsActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.NotificationCompat;
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

        //define preference for the onclick-method
        final CheckBoxPreference pref_datacollection = (CheckBoxPreference) findPreference("pref_datacollection");
        pref_datacollection.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                System.out.println("Test Jens"); //TODO:delete if not needed
                CheckBoxPreference pref = (CheckBoxPreference) findPreference("checkbox_preference");

                //show the notifications only if the data collection is started
                if (pref_datacollection.isChecked()){
                    showNotification();
                }
                return true;
            }
        });
    }

    //First Test with Notification for Data Collection
    //TODO: Looking for other Notifications and handle them in a central and common way
    public void showNotification() {
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context
                .NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getActivity())
        //TODO:set other preferences for the notification
                .setSmallIcon(R.drawable.side_nav_bar) //TODO:generate standard icon for the app
                .setContentTitle("Daily Routine Planer")
                .setContentText("Data Collection started");
        notificationManager.notify(9999, notificationBuilder.build());
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
        //if so puts the edited text to the summary field. Checks valid input
        //TODO save and handle the settings. Check valid input for all possible scenarios
        if(findPreference(key) instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) findPreference(key);
            EditText nameDialog = editTextPref.getEditText();

            //username, weight and blood sugar level
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
        // pref_datacollection
    }
}
