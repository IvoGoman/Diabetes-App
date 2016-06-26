package uni.mannheim.teamproject.diabetesplaner.UI.DailyRoutine;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.UI.ActivityMeasurementFrag.MeasurementFragment;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.MeasurementInputHandler;
import uni.mannheim.teamproject.diabetesplaner.UI.CustomListView;
import uni.mannheim.teamproject.diabetesplaner.R;

/**
 * created by Naira
 */

public class MeasurementInputDialog extends DialogFragment {
  /*  private static ArrayAdapter adapter;
    public static AbsListView lv;
    public static ArrayList<String> measurementList = new ArrayList<String>();
    public static final String TAG = MeasurementFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    String measure;

    private static View inflaterView;*/
  String measure ="";
    private int activity = 0;

  /*  public static MeasurementFragment newInstance(String param1, String param2) {
        MeasurementFragment fragment = new MeasurementFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View getLayout(){
        LayoutInflater inflater = (LayoutInflater) getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.pop_measurement_window, null);

       /* final EditText GlucoseInput = (EditText) v.findViewById(R.id.editText);
        GlucoseInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        final EditText InsulinInput = (EditText) v.findViewById(R.id.editText2);
        InsulinInput.setInputType(InputType.TYPE_CLASS_NUMBER); */

        final RadioButton BloodSugarRadioButton = (RadioButton) v.findViewById(R.id.bloodSugarRadioButton);
        final RadioButton InsulinDosageRadioButton = (RadioButton) v.findViewById(R.id.insulinDosageRadioButton);

            if (measure.equals("Insert Blood Sugar lvl")) {
                BloodSugarRadioButton.setActivated(true);
                BloodSugarRadioButton.setChecked(true);
                InsulinDosageRadioButton.setActivated(false);
                InsulinDosageRadioButton.setChecked(false);

            }

        if (measure.equals("Insert Insulin Dosage")) {
            InsulinDosageRadioButton.setActivated(true);
            InsulinDosageRadioButton.setChecked(true);
            BloodSugarRadioButton.setActivated(false);
            BloodSugarRadioButton.setChecked(false);
        }


        return v;
    }
    /**
     * set the activity of the input dialog
     * @param activity activity id
     */
    public void setActivity(int activity){
        this.activity = activity;
    }


}
