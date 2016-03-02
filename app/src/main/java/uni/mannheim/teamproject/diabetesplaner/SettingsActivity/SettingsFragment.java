package uni.mannheim.teamproject.diabetesplaner.SettingsActivity;

import android.app.Dialog;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
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

        final EditTextPreference pref_EditText = (EditTextPreference) findPreference("pref_key_bloodsugar");
        //pref_EditText.setDialogLayoutResource(1);
        pref_EditText.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FragmentManager manager = getFragmentManager();
                bloodsugar_dialog bs = new bloodsugar_dialog();
                bs.show(manager, "Test");
                return true;
            }
        });


        final Preference pref_add_Activity = findPreference("pref_add_activity");
        //pref_EditText.setDialogLayoutResource(1);
        pref_add_Activity.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FragmentManager manager = getFragmentManager();
                edit_activitylist_dialog ea = new edit_activitylist_dialog();
                ea.show(manager,"Test");
                return true;
            }
        });




        //define preference for the onclick-method
        final CheckBoxPreference pref_datacollection = (CheckBoxPreference) findPreference("pref_datacollection");
        pref_datacollection.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                System.out.println("Test Jens"); //TODO:delete if not needed
                CheckBoxPreference pref = (CheckBoxPreference) findPreference("checkbox_preference");


                //show the notifications only if the data collection is started
                if (pref_datacollection.isChecked()){
                    showNotification(1);
                }else{
                    showNotification(2);
                }

                return true;
            }
        });

        //Write User Data in the Summary Fields

        SharedPreferences sharedPrefs = getPreferenceManager().getSharedPreferences();

        final EditTextPreference pref_name = (EditTextPreference) findPreference("pref_key_name");
        String Test1 = sharedPrefs.getString("pref_key_name", "Vorname, Name");
        pref_name.setSummary(Test1);

        final EditTextPreference pref_weight = (EditTextPreference) findPreference("pref_key_weight");
        String Test2 = sharedPrefs.getString("pref_key_weight", "Gewicht eingeben");
        pref_weight.setSummary(Test2 + " kg");

        final EditTextPreference pref_bloodsugar = (EditTextPreference) findPreference("pref_key_bloodsugar");
        String Test3 = sharedPrefs.getString("pref_key_bloodsugar", "Blutzuckerwert eingeben");
        pref_bloodsugar.setSummary(Test3 + " mmol/L");

    }

    //First Test with Notification for Data Collection
    //TODO: Looking for other Notifications and handle them in a central and common way
    public void showNotification(int fall) {
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context
                .NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getActivity())
        //TODO:set other preferences for the notification
                .setSmallIcon(R.drawable.side_nav_bar) //TODO:generate standard icon for the app
                .setContentTitle("Daily Routine Planer")
                .setContentText("Data Collection started")
                .setOngoing(true);

        if (fall==1){
            notificationManager.notify(9999, notificationBuilder.build());
        }else{
            notificationManager.cancel(9999);
        }
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


}
