package uni.mannheim.teamproject.diabetesplaner.SettingsActivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.preference.Preference;
import android.widget.Toast;

import uni.mannheim.teamproject.diabetesplaner.EntryScreenActivity;
import uni.mannheim.teamproject.diabetesplaner.R;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG = SettingsFragment.class.getSimpleName();
    private Preference pref_bloodsugar;
    private ListPreference pref_weight_measurement;
    private EditTextPreference pref_weight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        pref_weight_measurement = (ListPreference) findPreference("pref_weightOptions");
        pref_weight = (EditTextPreference) findPreference("pref_key_weight");


        pref_bloodsugar = (Preference) findPreference("pref_key_bloodsugar");

        pref_bloodsugar.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
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


        String Test2 = sharedPrefs.getString("pref_key_weight", "Gewicht eingeben");
        pref_weight.setSummary(Test2 + " kg");

        String Test3 = sharedPrefs.getString("pref_key_bloodsugar", "Blutzuckerwert eingeben");
        pref_bloodsugar.setSummary(Test3 + " mmol/L");


        pref_weight_measurement.setSummary(sharedPrefs.getString("pref_weightOptions","Test"));

    }


    public boolean onPreferenceChanged(Preference.OnPreferenceChangeListener preference) {
        if (pref_weight_measurement.getValue() == "kg") {

        } else if (pref_weight_measurement.getValue() == "Pound") {

        }
        return true;
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
        String[] weight = new String[2];
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

            } else if (editTextPref.getKey().equals("pref_key_weight"))
            {
                if(pref_weight_measurement.getValue().equals("Kilogram")) {
                    weight[1]="kg";
                    pref_weight_measurement.setSummary("Kilogram");
                    pref_weight.setSummary(nameDialog.getText() + " " + weight[1]);

                }
                else if(pref_weight_measurement.getValue().equals("Pound")) {
                    weight[1]="lbs";
                    pref_weight_measurement.setSummary("Pound");
                    pref_weight.setSummary(nameDialog.getText() + " " + weight[1]);

                }
                ;
            }
        }

        //converts the current weight
        if(findPreference(key) == pref_weight_measurement)
        {
            EditText nameDialog = pref_weight.getEditText();
            if(pref_weight_measurement.getValue().equals("Kilogram")) {
                weight[0] = String.valueOf(lbs_to_kg(Double.parseDouble(pref_weight.getText())));
                weight[1]="kg";
                pref_weight_measurement.setSummary("Kilogram");
                pref_weight.setSummary(weight[0] + " " + weight[1]);
                pref_weight.getEditText().setText(weight[0]);
            }
            else if(pref_weight_measurement.getValue().equals("Pound")) {
                weight[0] = String.valueOf(kg_to_lbs(Double.parseDouble(pref_weight.getText())));
                weight[1]="lbs";
                pref_weight_measurement.setSummary("Pound");
                pref_weight.setSummary(weight[0] + " " + weight[1]);
                pref_weight.getEditText().setText(weight[0]);

            }

        }
    }


    /***
     * JW: Communication of blood sugar dialog and settingsFragement
     * @param data entered blood sugar level
     * @param measure the entered measurement
     */
    public void bloodsugar_change(String data, String measure) {
        pref_bloodsugar.setSummary(data + " " + measure);

    }

    /***
     * Converts kg to lns
     * @param kg
     * @return
     */
    private double kg_to_lbs(double kg)
    {
        return Math.round(kg * 2.20462 * 100d) / 100d;
    }

    /***
     * Converts lbs to kg
     * @param lbs
     * @return
     */
    private double lbs_to_kg(double lbs)
    {
        return Math.round((lbs / 2.20462)*100d) / 100d;
    }
}
