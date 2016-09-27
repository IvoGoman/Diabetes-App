package uni.mannheim.teamproject.diabetesplaner.UI.Settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import uni.mannheim.teamproject.diabetesplaner.R;

/**
 * @author Stefan
 */
public class SettingsActivity extends AppCompatActivity implements BloodsugarDialog_and_Settings {

    public static final String TAG = SettingsActivity.class.getSimpleName();


    private String oldCatName;
    private EditTextPreference catName;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private SettingsFragment setFrag;
    private bloodsugar_dialog setBloodsugar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the settingsFragment as the content.
        setFrag = new SettingsFragment();
        setFrag.setActivityIntent(getIntent());
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, setFrag).commit();

        setBloodsugar = new bloodsugar_dialog();

        getSupportActionBar().setTitle(R.string.menu_item_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if(setFrag.getPage() != null) {
                    if (setFrag.getPage().equals("pref_key_algos_super")) {
                        setFrag = new SettingsFragment();
                        Intent i = new Intent();
                        i.putExtra("page","pref_screen");
                        setFrag.setActivityIntent(i);
                        getFragmentManager().beginTransaction()
                                .replace(android.R.id.content, setFrag).commit();
                        getSupportActionBar().setTitle(R.string.menu_item_settings);
                        return true;
                    }
                }
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
    public void respond(bloodsugar_dialog bs, String data, String measure, int ID) {
        int profile_id = 1;

        Log.d("Database", "Write blood sugar level of " + data + " " + measure + " " +
                "to profile " + String.valueOf(profile_id));

        if (ID == 1) {
            //ToDo: Database Handling here
            setFrag.bloodsugar_change(bs,data, measure, ID);
        } else if (ID == 2)
        {
            setBloodsugar.setbloodsugarOnCreate(bs,data,measure,ID);
        }
    }

}
