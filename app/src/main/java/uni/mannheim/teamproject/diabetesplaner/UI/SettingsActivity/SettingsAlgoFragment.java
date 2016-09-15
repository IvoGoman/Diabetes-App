package uni.mannheim.teamproject.diabetesplaner.UI.SettingsActivity;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import uni.mannheim.teamproject.diabetesplaner.R;

/**
 * Created by Stefan on 15.09.2016.
 */
public class SettingsAlgoFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_algorithms);
    }
}
