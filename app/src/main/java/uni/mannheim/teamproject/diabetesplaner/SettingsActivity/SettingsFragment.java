package uni.mannheim.teamproject.diabetesplaner.SettingsActivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
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

import uni.mannheim.teamproject.diabetesplaner.Backend.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Backend.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.EntryScreenActivity;
import uni.mannheim.teamproject.diabetesplaner.R;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG = SettingsFragment.class.getSimpleName();
    private Preference pref_bloodsugar;
    private String bloodsugar_measure_value;
    private String bloodsugar_measure;
    private ListPreference pref_weight_measurement;
    private EditTextPreference pref_weight;
    private SharedPreferences sharedPrefs;
    private DataBaseHandler database;
    BloodsugarDialog_and_Settings communicator;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator = (BloodsugarDialog_and_Settings) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        pref_weight_measurement = (ListPreference) findPreference("pref_weightOptions");
        pref_weight = (EditTextPreference) findPreference("pref_key_weight");
        database = AppGlobal.getHandler();
        sharedPrefs = getPreferenceManager().getSharedPreferences();

        //Bloodsugar handling
        initialize_bloodsugar();


        final Preference pref_add_Activity = findPreference("pref_add_activity");
        //pref_EditText.setDialogLayoutResource(1);
        pref_add_Activity.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FragmentManager manager = getFragmentManager();
                edit_activitylist_dialog ea = new edit_activitylist_dialog();
                ea.show(manager, "Test");
                return true;
            }
        });


        //define preference for the onclick-method
        final CheckBoxPreference pref_datacollection = (CheckBoxPreference) findPreference("pref_datacollection");
        pref_datacollection.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                CheckBoxPreference pref = (CheckBoxPreference) findPreference("checkbox_preference");

                //show the notifications only if the data collection is started
                if (pref_datacollection.isChecked()) {
                    showNotification(1);
                } else {
                    showNotification(2);
                }

                return true;
            }
        });

        //Write User Data in the Summary Fields

        final EditTextPreference pref_name = (EditTextPreference) findPreference("pref_key_name");
        String Test1 = sharedPrefs.getString("pref_key_name", "Vorname, Name");
        pref_name.setSummary(Test1);


        String Test2 = sharedPrefs.getString("pref_key_weight", "Gewicht eingeben");
        if (pref_weight_measurement.getValue() == null) {
            pref_weight.setSummary(Test2 + "kg");
        }
        else if (pref_weight_measurement.getValue().equals("Kilogramm"))
        {
            if (pref_weight_measurement.getValue() == null) {

            } else if (pref_weight_measurement.getValue().equals("Kilogram")) {
                pref_weight.setSummary(Test2 + " kg");
            } else if (pref_weight_measurement.getValue().equals("Pound")) {
                pref_weight.setSummary(Test2 + " lbs");
            }
            pref_weight_measurement.setSummary(sharedPrefs.getString("pref_weightOptions", "Test"));
        }
    }


    public boolean onPreferenceChanged(Preference.OnPreferenceChangeListener preference) {
        String old_weight = sharedPrefs.getString("pref_key_weight", "Gewicht eingeben");
        SharedPreferences.Editor edit_value = sharedPrefs.edit();
        String new_weight = "";
        if (pref_weight_measurement.getValue() == "Kilogramm") {
            new_weight = String.valueOf(lbs_to_kg(Double.parseDouble(old_weight)));
            sharedPrefs.edit().putString("pref_weightOptions", new_weight);
        } else if (pref_weight_measurement.getValue() == "Pound") {
            new_weight = String.valueOf(kg_to_lbs(Double.parseDouble(old_weight)));
            sharedPrefs.edit().putString("pref_weightOptions", new_weight);
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

        if (fall == 1) {
            notificationManager.notify(9999, notificationBuilder.build());
        } else {
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String[] weight = new String[2];
        //checks if Preference with key is EditTextPreference
        //if so puts the edited text to the summary field. Checks valid input
        //TODO save and handle the settings. Check valid input for all possible scenarios
        if (findPreference(key) instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) findPreference(key);
            EditText nameDialog = editTextPref.getEditText();

            //username, weight and blood sugar level
            if (editTextPref.getKey().equals("pref_key_name")) {
                editTextPref.setSummary(nameDialog.getText());

                View header = EntryScreenActivity.navigationView.getHeaderView(0);
                TextView name = (TextView) header.findViewById(R.id.username);
                name.setText(nameDialog.getText());
                // Log.d(TAG, String.valueOf(getActivity().findViewById(R.id.username)));
                database.InsertProfile(database,nameDialog.getText().toString().split(" ")[0],
                        nameDialog.getText().toString().split(" ")[1],20);
            } else if (editTextPref.getKey().equals("pref_key_weight")) {

                if (pref_weight_measurement.getEntry().equals("Kilogramm")) {
                    editTextPref.setSummary(nameDialog.getText() + " kg");
                    database.InsertWeight(database,1,Double.parseDouble(nameDialog.getText().toString()),"kg");
                }

                if (pref_weight_measurement.getEntry().equals("Pound")) {
                    editTextPref.setSummary(nameDialog.getText() + " lbs");
                    database.InsertWeight(database,1,Double.parseDouble(nameDialog.getText().toString()),"lbs");
                }
            }
        }

        //converts the current weight
        if (findPreference(key) == pref_weight_measurement) {
            SharedPreferences pref_weight = getActivity().getSharedPreferences("pref_key_weight", 0);
            SharedPreferences.Editor pref_weight_editor = pref_weight.edit();
            EditTextPreference prefweight = (EditTextPreference) findPreference("pref_key_weight");
            String old_weight = sharedPrefs.getString("pref_key_weight", "Gewicht eingeben");
            String new_weight = "";
            if (pref_weight_measurement.getValue().equals("Kilogramm")) {
                new_weight = String.valueOf(lbs_to_kg(Double.parseDouble(old_weight)));
                prefweight.setText(new_weight);
                prefweight.setSummary(new_weight + " kg");
                pref_weight_measurement.setSummary("Kilogramm");
                //Test if necessary
                pref_weight_editor.putFloat("pref_key_weight", Float.parseFloat(new_weight));
                pref_weight_editor.commit();
                pref_weight_editor.apply();
            } else if (pref_weight_measurement.getValue().equals("Pound")) {
                new_weight = String.valueOf(kg_to_lbs(Double.parseDouble(old_weight)));
                pref_weight_measurement.setSummary("Pound");
                prefweight.setText(new_weight);
                prefweight.setSummary(new_weight + " lbs");
                pref_weight_editor.putFloat("pref_key_weight", Float.parseFloat(new_weight));
                pref_weight_editor.commit();
                pref_weight_editor.apply();
            }
        }
    }


    /***
     * JW: Communication of blood sugar dialog and settingsFragement
     *
     * @param data    entered blood sugar level
     * @param measure the entered measurement
     */
    public void bloodsugar_change(bloodsugar_dialog bs, String data, String measure, int ID) {
        if (ID == 1) {
            pref_bloodsugar.setSummary(data + " " + measure);
            bloodsugar_measure_value = data;
            bloodsugar_measure = measure;
        }
    }

    private void initialize_bloodsugar()
    {
        if(database.getLastBloodsugarMeasurement(database,1) != null) {
            bloodsugar_measure = database.getLastBloodsugarMeasurement(database,1)[1];
            bloodsugar_measure_value = database.getLastBloodsugarMeasurement(database,1)[0];
        }
        else
        {
            bloodsugar_measure_value = sharedPrefs.getString("pref_key_bloodsugar", "Blutzuckerwert eingeben");
            bloodsugar_measure = "";
        }
        pref_bloodsugar = (Preference) findPreference("pref_key_bloodsugar");
        //Set initial values for bloodsugar measurement. Values retrieved from database
        pref_bloodsugar.setSummary(bloodsugar_measure_value +" "+ bloodsugar_measure);

        //clickListener for bloodsugar_dialog
        pref_bloodsugar.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FragmentManager manager = getFragmentManager();
                bloodsugar_dialog bs = new bloodsugar_dialog();
                bs.show(manager,"Test");
                //Set the numberpicker to the current value
                communicator.respond(bs,bloodsugar_measure_value,bloodsugar_measure,2);
                return true;
            }
        });
    }

    /***
     * Converts kg to lns
     *
     * @param kg
     * @return
     */
    private double kg_to_lbs(double kg) {
        return Math.round(kg * 2.20462 * 100d) / 100d;
    }

    /***
     * Converts lbs to kg
     *
     * @param lbs
     * @return
     */
    private double lbs_to_kg(double lbs) {
        return Math.round((lbs / 2.20462) * 100d) / 100d;
    }
}
