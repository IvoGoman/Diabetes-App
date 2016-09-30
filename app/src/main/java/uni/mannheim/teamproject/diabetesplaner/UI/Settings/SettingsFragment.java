package uni.mannheim.teamproject.diabetesplaner.UI.Settings;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.R;
import uni.mannheim.teamproject.diabetesplaner.TechnicalServices.Accelerometer;
import uni.mannheim.teamproject.diabetesplaner.TechnicalServices.GPS_Service.GPS_Service;
import uni.mannheim.teamproject.diabetesplaner.TechnicalServices.Wifi;
import uni.mannheim.teamproject.diabetesplaner.UI.EntryScreenActivity;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;


/**
 * Used to handle the settings for the app
 * @author: Jan and Jens
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG = SettingsFragment.class.getSimpleName();
    private Preference pref_bloodsugar;
    private String bloodsugar_measure_value;
    private String bloodsugar_measure;
    private ListPreference pref_weight_measurement;
    private ListPreference pref_pred_mode;
    private EditTextPreference pref_name;
    private EditTextPreference pref_weight;
    private CheckBoxPreference pref_vacation;
    private SharedPreferences sharedPrefs;
    private DataBaseHandler database;
    private CheckBoxPreference pref_datacollection;
    BloodsugarDialog_and_Settings communicator;
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_Storage = 0;

    private Intent accelerometerCollection;
    private Intent wifi;
    private Intent GPS;
    private SettingsActivity parent;
    private String page;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator = (BloodsugarDialog_and_Settings) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

            openPreferenceScreen(page);

            //List for the selection of the weight unit
            pref_weight_measurement = (ListPreference) findPreference("pref_weightOptions");
            //Edit field for the entry of the weight
            pref_weight = (EditTextPreference) findPreference("pref_key_weight");
            pref_vacation = (CheckBoxPreference) findPreference("pref_vacation");

            database = AppGlobal.getHandler();
            sharedPrefs = getPreferenceManager().getSharedPreferences();



            //Bloodsugar handling
            initialize_bloodsugar();
            initialize_weight();

//            resetting the predicted daily routine
            final Preference pref_algo_reset = findPreference("pref_algos_reset");
            pref_algo_reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    // set title
                    alertDialogBuilder.setTitle(R.string.pref_algos_reset);
                    // set dialog message
                    alertDialogBuilder
                            .setMessage(R.string.pref_algos_reset_message)
                            .setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_alert))
                            .setCancelable(false)
                            .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("PREDICTION_SERVICE_FILE", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("LAST_PREDICTION", "0");
                                    editor.commit();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    dialog.cancel();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();

                    return true;
                }
            });

            //loading the help slider
            final Preference pref_key_help = findPreference("pref_key_help");
            pref_key_help.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    Intent myIntent = new Intent(getActivity(), HelpActivity.class);
                    getActivity().startActivity(myIntent);

                    return true;
                }
            });

            //starting the export
            final Preference pref_key_export = findPreference("pref_key_export");
            pref_key_export.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    // set title
                    alertDialogBuilder.setTitle(R.string.pref_dbexport);
                    // set dialog message
                    alertDialogBuilder
                            .setMessage(R.string.pref_dbexport_message)
                            .setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_alert))
                            .setCancelable(false)
                            .setPositiveButton(R.string.export,new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    Log.d("PositivButton" , "Gedrückt");


                                    dbExport();


                                }
                            })
                            .setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    dialog.cancel();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();

                    return true;
                }
            });
//
//            /**
//             * Ivo
//             * On change listerner for the prediction mode that resets
//             * the shared preference of the last prediction if the mode is changed
//             */
//            pref_pred_mode = (ListPreference) findPreference("pref_pred_mode");
//            pref_pred_mode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//                @Override
//                public boolean onPreferenceChange(Preference preference, Object newValue) {
//                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("PREDICTION_SERVICE_FILE", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("LAST_PREDICTION", "0");
//                    editor.commit();
//                    return true;
//                }
//            });


            //define preference for the onclick-method
            pref_datacollection = (CheckBoxPreference) findPreference("pref_datacollection");
            pref_datacollection.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    CheckBoxPreference pref = (CheckBoxPreference) findPreference("checkbox_preference");
                    //show the notifications only if the data collection is started
                    if (pref_datacollection.isChecked()) {
                        //start the Services and Notification
                        showNotification(1);
                        startServices();

                    } else {
                        //stop the Services and Notification
                        showNotification(2);
                        stopServices();

                    }
                    return true;
                }
            });

            //check if data collection is already started
            if (getDataCollection()) {
                startServices();
            }

            //Write User Data in the Summary Fields

            pref_name = (EditTextPreference) findPreference("pref_key_name");
//            String Test1 = sharedPrefs.getString("pref_key_name", "Vorname, Name");
//            pref_name.setSummary(Test1);
            int id = database.getUserID();

            pref_name.setSummary(database.getUser(database.getUserID())[0] + database.getUser(database.getUserID())[1]);


        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /***
     * Handles the change of preference
     * @author: Jan
     * @param preference
     * @return
     */
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

    /***
     * Handles the change of shared preferences
     * @author: Jan
     * @param sharedPreferences
     * @param key
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        try{
        String[] weight = new String[2];
        //checks if Preference with key is EditTextPreference
        //if so puts the edited text to the summary field. Checks valid input
        //TODO save and handle the settings. Check valid input for all possible scenarios
        if (findPreference(key) instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) findPreference(key);
            EditText nameDialog = editTextPref.getEditText();

            //username, weight and blood sugar level
            if (editTextPref.getKey().equals("pref_key_name")) {


//                Menu menu = EntryScreenActivity.navigationView.getMenu();
//                MenuItem user  = menu.findItem(R.id.username);
//                user.setTitle(nameDialog.getText().toString().split(" ")[0] +" " +nameDialog.getText().toString().split(" ")[1]);
                //View header = EntryScreenActivity.navigationView.getHeaderView(0);
                NavigationView navigationView = EntryScreenActivity.navigationView;

                View header = navigationView.getHeaderView(0);
                TextView name = (TextView) header.findViewById(R.id.username);
                String[] new_name = nameDialog.getText().toString().split(" ");

                Log.d(TAG, String.valueOf(R.string.username));

                if(new_name.length == 1) {
                    database.InsertProfile(new_name[0],
                            "",0);
                }
                else
                {
                    String connected_name = "";
                    for(int j = 0;j<new_name.length-1;j++)
                    {
                        connected_name = connected_name + new_name[j] + " ";
                    }
                    database.InsertProfile(connected_name,
                            new_name[new_name.length-1], 0);
                }

                editTextPref.setSummary(database.getUser(database.getUserID())[0] + database.getUser(database.getUserID())[1]);
            } else if (editTextPref.getKey().equals("pref_key_weight")) {

                if (pref_weight_measurement.getEntry().equals("Kilogramm")) {
                    editTextPref.setSummary(nameDialog.getText() + " kg");
                    database.InsertWeight(1, Double.parseDouble(nameDialog.getText().toString()), "kg");
                }

                if (pref_weight_measurement.getEntry().equals("Pound")) {
                    editTextPref.setSummary(nameDialog.getText() + " lbs");
                    database.InsertWeight(1, Double.parseDouble(nameDialog.getText().toString()), "lbs");
                }
            }
        }
            /**
             * Ivo
             * Reset the Prediction if the Algorithms or Mode change
             */
        if(key.equals("pref_key_gsp") || key.equals("pref_key_dt") ||key.equals("pref_key_fuzzy") ||key.equals("pref_key_heuristics")|| key.equals("pref_pred_mode")){
            sharedPreferences = getActivity().getSharedPreferences("PREDICTION_SERVICE_FILE", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("LAST_PREDICTION", "0");
            editor.commit();
        }
            if(key.equals("pref_bloodsugarOptions")){
                ListPreference bloodsugar = (ListPreference) findPreference("pref_bloodsugarOptions");
                switch(bloodsugar.getValue()){
                    case("%"):
                        bloodsugar.setSummary("Percent");
                        break;
                    default:
                        bloodsugar.setSummary(bloodsugar.getValue());
                        break;
                }

            }
            if(key.equals("pref_insulinOptions")){
                ListPreference insulin = (ListPreference) findPreference("pref_insulinOptions");
                insulin.setSummary(String.valueOf(insulin.getValue()));
            }

        //converts the current weight
        if (findPreference(key) == pref_weight_measurement) {
            SharedPreferences pref_weight = getActivity().getSharedPreferences("pref_key_weight", 0);
            SharedPreferences.Editor pref_weight_editor = pref_weight.edit();
            EditTextPreference prefweight = (EditTextPreference) findPreference("pref_key_weight");
            String old_weight = sharedPrefs.getString("pref_key_weight", "Gewicht eingeben");
            String new_weight = "";

            if (pref_weight_measurement.getValue().equals("Kilogramm")) {
                try {
                    new_weight = String.valueOf(lbs_to_kg(Double.parseDouble(old_weight)));
                    prefweight.setText(new_weight);
                    prefweight.setSummary(new_weight + " kg");
                    pref_weight_measurement.setSummary("Kilogramm");
                    //Test if necessary
                    pref_weight_editor.putFloat("pref_key_weight", Float.parseFloat(new_weight));
                    pref_weight_editor.commit();
                    pref_weight_editor.apply();
                } catch (Exception e) {
                    Log.d("Weight", "No weight specified");
                    pref_weight_measurement.setSummary("Kilogramm");
                    pref_weight_measurement.setValueIndex(0);
                }

            } else if (pref_weight_measurement.getValue().equals("Pound")) {
                try {
                    new_weight = String.valueOf(kg_to_lbs(Double.parseDouble(old_weight)));
                    pref_weight_measurement.setSummary("Pound");
                    prefweight.setText(new_weight);
                    prefweight.setSummary(new_weight + " lbs");
                    pref_weight_editor.putFloat("pref_key_weight", Float.parseFloat(new_weight));
                    pref_weight_editor.commit();
                    pref_weight_editor.apply();
                } catch (Exception e) {
                    Log.d("Weight", "No weight specified");
                    pref_weight_measurement.setSummary("Pound");
                    pref_weight_measurement.setValueIndex(1);
                }
            }
        }
        }catch(Exception e)
        {
            e.getMessage();
        }
    }



    /***
     * JW: Communication of blood sugar dialog and settingsFragement
     * @author: Jan
     *
     * @param data    entered blood sugar level
     * @param measure the entered measurement
     */
    public void bloodsugar_change(bloodsugar_dialog bs, String data, String measure, int ID) {
        if (ID == 1) {
            String[] result =database.getLastBloodsugarMeasurement(1);
            data = result[0];
            measure = result[1];
            pref_bloodsugar.setSummary(data + " " + measure);
            bloodsugar_measure_value = data;
            bloodsugar_measure = measure;
        }
    }

    /***
     * initializes the weight settings
     * @author: Jan
     */
    private void initialize_weight()
    {
        try {
            if (pref_weight_measurement.getValue() != null) {
                if (pref_weight_measurement.getValue().equals("")) {
                    pref_weight_measurement.setValueIndex(0);
                    pref_weight_measurement.setSummary("Kilogram");
                } else if (pref_weight_measurement.getValue().equals("Kilogram")) {
                    pref_weight_measurement.setValueIndex(0);
                    pref_weight_measurement.setSummary("Kilogram");

                } else if (pref_weight_measurement.getValue().equals("Pound")) {
                    pref_weight_measurement.setValueIndex(1);
                    pref_weight_measurement.setSummary("Pound");
                } else {
                    pref_weight_measurement.setValueIndex(0);
                    pref_weight_measurement.setSummary("Kilogram");
                }
            }
        }catch (Exception e)
        {

        }
        if(database.GetLastWeight(database.getUserID()) != null) {
            pref_weight.setSummary(database.GetLastWeight(1)[0] + " " + database.GetLastWeight(1)[1]);
            pref_weight.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    try {
                        database.InsertWeight(1, Double.parseDouble(pref_weight.getText()), pref_weight_measurement.getValue());
                        pref_weight.setSummary(pref_weight.getText() + " " + pref_weight_measurement.getValue());
                        return true;
                    } catch (Exception e) {

                    }
                    return false;
                }
            });
        }
    }





    /***
     * initializes the blood sugar settings
     * @author: Jan
     */
    private void initialize_bloodsugar()
    {
        if(database.getLastBloodsugarMeasurement(1) != null) {
            bloodsugar_measure = database.getLastBloodsugarMeasurement(1)[1];
            bloodsugar_measure_value = database.getLastBloodsugarMeasurement(1)[0];
        }
        else
        {
            bloodsugar_measure_value = sharedPrefs.getString("pref_key_bloodsugar", "Blutzuckerwert eingeben");
            bloodsugar_measure = "";
        }
        pref_bloodsugar = (Preference) findPreference("pref_key_bloodsugar");
        //Set initial values for bloodsugar measurement. Values retrieved from database
        pref_bloodsugar.setSummary(bloodsugar_measure_value + " " + bloodsugar_measure);

        //clickListener for bloodsugar_dialog
        pref_bloodsugar.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FragmentManager manager = getFragmentManager();
                bloodsugar_dialog bs = new bloodsugar_dialog();
                bs.show(manager, "Test");
                //Set the numberpicker to the current value
                communicator.respond(bs, bloodsugar_measure_value, bloodsugar_measure, 2);
                return true;
            }
        });
    }

    /***
     * Starts the DataCollection Services
     */
    private void startServices()
    {
        accelerometerCollection = new Intent();
        accelerometerCollection.setClass(getActivity().getApplicationContext(), Accelerometer.class);
        getActivity().startService(accelerometerCollection);

        if (accelerometerCollection!=null){
            System.out.println("accelerom intent");
        }



        //adjsted by Naira, class to be called is WifiActiveService instead of Wifi
        wifi = new Intent();
        wifi.setClass(getActivity().getApplicationContext(), Wifi.WifiActiveService.class);
        getActivity().startService(wifi);
        Toast.makeText(getActivity(), "Wifi data collection started", Toast.LENGTH_LONG).show();





        GPS = new Intent();
        GPS.setClass(getActivity().getApplicationContext(), GPS_Service.class);
        getActivity().startService(GPS);
    }

    /***
     * Stops the Services
     */
    private void stopServices()
    {
        getActivity().stopService(wifi);
        getActivity().stopService(accelerometerCollection);
        getActivity().stopService(GPS);
    }

    /***
     * Converts kg to lns
     * @author: Jan
     *
     * @param kg
     * @return
     */
    private double kg_to_lbs(double kg) {
        return Math.round(kg * 2.20462 * 100d) / 100d;
    }

    /***
     * Converts lbs to kg
     * @author: Jan
     *
     * @param lbs
     * @return
     */
    private double lbs_to_kg(double lbs) {
        return Math.round((lbs / 2.20462) * 100d) / 100d;
    }

    public double getWeight()
    {
        return Double.parseDouble(pref_weight.getText());
    }

    public String getName()
    {
        return pref_name.getText();
    }

    public boolean getDataCollection()
    {
        return pref_datacollection.isChecked();
    }

    public boolean getVacationMode()
    {
        return pref_vacation.isChecked();
    }

    public String[] getBloodSugar()
    {
        String[] returnValue = new String[2];
        returnValue[0] = bloodsugar_measure_value;
        returnValue[1] = bloodsugar_measure;
        return returnValue;
    }


    public void setParent(SettingsActivity parent) {
        this.parent = parent;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference    preference) {
        super.onPreferenceTreeClick(preferenceScreen, preference);

        // If the user has clicked on a preference screen, set up the action bar
        if (preference instanceof PreferenceScreen) {
            initializeActionBar((PreferenceScreen) preference);
        }

        return false;
    }

    /** Sets up the action bar for an {@link PreferenceScreen} */
    public static void initializeActionBar(PreferenceScreen preferenceScreen) {
        final Dialog dialog = preferenceScreen.getDialog();

        if (dialog != null) {
            // Inialize the action bar
            dialog.getActionBar().setDisplayHomeAsUpEnabled(true);

            // Apply custom home button area click listener to close the PreferenceScreen because PreferenceScreens are dialogs which swallow
            // events instead of passing to the activity
            // Related Issue: https://code.google.com/p/android/issues/detail?id=4611
            View homeBtn = dialog.findViewById(android.R.id.home);

            if (homeBtn != null) {
                View.OnClickListener dismissDialogClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                };

                // Prepare yourselves for some hacky programming
                ViewParent homeBtnContainer = homeBtn.getParent();

                // The home button is an ImageView inside a FrameLayout
                if (homeBtnContainer instanceof FrameLayout) {
                    ViewGroup containerParent = (ViewGroup) homeBtnContainer.getParent();

                    if (containerParent instanceof LinearLayout) {
                        // This view also contains the title text, set the whole view as clickable
                        ((LinearLayout) containerParent).setOnClickListener(dismissDialogClickListener);
                    } else {
                        // Just set it on the home button
                        ((FrameLayout) homeBtnContainer).setOnClickListener(dismissDialogClickListener);
                    }
                } else {
                    // The 'If all else fails' default case
                    homeBtn.setOnClickListener(dismissDialogClickListener);
                }
            }
        }
    }

    /**
     * sets the intent which started the activity
     * @param activityIntent
     * @author Stefan 15.09.2016
     */
    public void setActivityIntent(final Intent activityIntent) {
        if (activityIntent != null) {
            if (Intent.ACTION_VIEW.equals(activityIntent.getAction())) {

                if (activityIntent.getExtras() != null) {
                    page = activityIntent.getExtras().getString("page");
                    if (!TextUtils.isEmpty(page)) {
                        openPreferenceScreen(page);
                    }
                }
            }
        }
    }

    /**
     * opens a PreferenceScreen (for nested screens)
     * @param screenName
     * @author Stefan 15.09.2016
     */
    public void openPreferenceScreen(final String screenName) {
        final Preference pref = findPreference(screenName);
        if (pref instanceof PreferenceScreen) {
            final PreferenceScreen preferenceScreen = (PreferenceScreen) pref;
            ((SettingsActivity) getActivity()).getSupportActionBar().setTitle(preferenceScreen.getTitle());
            setPreferenceScreen((PreferenceScreen) pref);
        }
    }

    public String getPage(){
        return this.page;
    }



    public void dbExport(){

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.
            Log.d("filechooser permission", "No permission");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d("filechooser permission", "Request permission");
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_WRITE_EXTERNAL_Storage);
            }
        }
        else {
            Log.d("filechooser permission", "has permission");


            AppGlobal.getHandler().exportDB();


        }

    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d("filechooser permission", "onRequestPermissionResult");
        switch (requestCode) {
            case MY_PERMISSIONS_WRITE_EXTERNAL_Storage: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("filechooser permission", "permission granted");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.



                    AppGlobal.getHandler().exportDB();








                } else {
                    Log.d("filechooser permission", "permission denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
