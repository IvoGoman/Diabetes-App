package uni.mannheim.teamproject.diabetesplaner.UI;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import uni.mannheim.teamproject.diabetesplaner.R;

/**
 * created by Naira
 */
public class MeasurementPop extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_measurement_window);

        // adjust the format of the popup
        DisplayMetrics dm= new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .4));


        //set action bar title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Measurement");



    }
}
