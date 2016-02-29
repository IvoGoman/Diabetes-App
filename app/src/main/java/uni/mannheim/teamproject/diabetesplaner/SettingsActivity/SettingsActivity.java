package uni.mannheim.teamproject.diabetesplaner.SettingsActivity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import uni.mannheim.teamproject.diabetesplaner.Backend.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Backend.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.R;


public class SettingsActivity extends AppCompatActivity implements Dialog_communicator {

    public static final String TAG = SettingsActivity.class.getSimpleName();


    private String oldCatName;
    private EditTextPreference catName;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private SettingsFragment setFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the settingsFragment as the content.

        setFrag = new SettingsFragment();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, setFrag)
                .commit();

        getSupportActionBar().setTitle(R.string.menu_item_settings);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void respond(String data, String measure) {
        int profile_id = 1;

        Log.d("Database", "Write blood sugar level of " + data + " " + measure +" " +
                "to profile " + String.valueOf(profile_id));


        setFrag.bloodsugar_change(data,measure);
        //ToDo: Database Handling here
    }
}
