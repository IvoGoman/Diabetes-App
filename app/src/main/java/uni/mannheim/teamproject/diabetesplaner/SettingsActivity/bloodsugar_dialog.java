package uni.mannheim.teamproject.diabetesplaner.SettingsActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Toast;
import java.math.RoundingMode;

import uni.mannheim.teamproject.diabetesplaner.Backend.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Backend.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.R;

/**
 * Created by Jan on 22.02.16.
 */
public class bloodsugar_dialog extends DialogFragment implements View.OnClickListener{
    Button submit,cancel;
    RadioButton mg,percentage,mmol;
    NumberPicker bloodsugar_level;
    BloodsugarDialog_and_Settings communicator;

    //the current selected measure
    private String measure;
    //the current selected measure value
    private String measure_value;
    DataBaseHandler database;
    double value;
    String[] nums;

    public bloodsugar_dialog()
    {}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator = (BloodsugarDialog_and_Settings) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.dialog_layout, null);

        setCancelable(false);

        database = AppGlobal.getHandler();
        submit = (Button) view.findViewById(R.id.bs_submit);
        cancel = (Button) view.findViewById(R.id.bs_cancel);
        mg = (RadioButton) view.findViewById(R.id.bs_mg);
        mmol = (RadioButton) view.findViewById(R.id.bs_mm);
        percentage = (RadioButton) view.findViewById(R.id.bs_percentage);
        bloodsugar_level = (NumberPicker) view.findViewById(R.id.numberPicker);
        String[] nums = new String[50];
        int measureID = 0;

        if(measure.equals(""))
        {
            measure = "mg/dl";
        }

        for(int i = 0;i<nums.length;i++) {
            if (measure.equals("%")) {
                percentage.setActivated(true);
                percentage.setChecked(true);
                mmol.setActivated(false);
                mmol.setChecked(false);
                mg.setActivated(false);
                mg.setChecked(false);
                //Convert from percetage to mg
                nums[i] = Double.toString(Math.round((4.7 + 0.1 * i)*10d)/10d);

            } else if (measure.equals("mmol/l")) {
                mmol.setActivated(true);
                mmol.setChecked(true);
                percentage.setActivated(false);
                percentage.setChecked(false);
                mg.setActivated(false);
                mg.setChecked(false);
                //Convert from mmol to mg
                nums[i] = Double.toString(Math.round(miligram_to_mol(percentage_to_mg(4.7 + 0.1 * i))*10d)/10d);

            } else if (measure.equals("mg/dl")) {
                //convert mg to mmol
                mg.setActivated(true);
                mg.setChecked(true);
                percentage.setActivated(false);
                percentage.setChecked(false);
                mmol.setActivated(false);
                mmol.setChecked(false);
                nums[i] = Double.toString(Math.round(percentage_to_mg(4.7 + 0.1 * i) * 10d) / 10d);

            }
            if(nums[i].equals(measure_value))
            {
                measureID = i;
            }
        }
        bloodsugar_level.setWrapSelectorWheel(false);
        bloodsugar_level.setDisplayedValues(nums);
        bloodsugar_level.setValue(measureID);
        bloodsugar_level.setMinValue(0);
        bloodsugar_level.setMaxValue(49);
        submit.setOnClickListener(this);
        cancel.setOnClickListener(this);
        mg.setOnClickListener(this);
        mmol.setOnClickListener(this);
        percentage.setOnClickListener(this);
        return view;
    }

    /***
     * Click Handler for the dialog.
     * @param view
     */
    @Override
    public void onClick(View view)
    {
        nums = bloodsugar_level.getDisplayedValues();
        //submit button clicked
        if(view.getId() == R.id.bs_submit)
        {
            //if value is changed, then store value and change display
            if(value != bloodsugar_level.getValue()) {
                measure_value =nums[bloodsugar_level.getValue()];
                database.InsertBloodsugar(database, 1, Double.parseDouble(measure_value), measure);
                communicator.respond(null,String.valueOf(nums[bloodsugar_level.getValue()]), measure, 1);
                Toast.makeText(getActivity(), "Blood sugar level: " + measure_value + " "
                        + measure + " stored"
                        , Toast.LENGTH_LONG).show();
                dismiss();
            }else{
                Log.d("bloodsugar_entry","Nothing changed");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("No Changes")
                            .setMessage("You did not change the blood_sugar level.")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        }
        else if(view.getId() == R.id.bs_cancel)
        {
            dismiss();
        }
        //mg/dl is clicked
        else if(view.getId() == R.id.bs_mg)
        {

            if (measure.equals("%"))
            {
                for (int i = 0; i < nums.length; i++) {
                    //Convert from percetage to mg
                    nums[i] = Double.toString(Math.round(percentage_to_mg(Double.parseDouble(nums[i]))));
                }
            }else if(measure.equals("mmol/l"))
            {
                for (int i = 0; i < nums.length; i++) {
                    //Convert from mmol to mg
                    nums[i] = Double.toString(Math.round(mmol_to_milligram(Double.parseDouble(nums[i]))));
                }
            }
            bloodsugar_level.setDisplayedValues(nums);
            measure = "mg/dl";
        }
        //Percentage is clicked
        else if(view.getId() == R.id.bs_percentage)
        {

            if(measure.equals("mg/dl")) {
                for (int i = 0; i < nums.length; i++) {
                    //Convert from mg to percentage
                    nums[i] = Double.toString(Math.round(mg_to_percentage(Double.parseDouble(nums[i]))*10d)/10d);
                }
            } else if(measure.equals("mmol/l"))
            {
                for (int i = 0; i < nums.length; i++) {
                    //Convert from mmol to mg to percentage
                    nums[i] = Double.toString(Math.round(mg_to_percentage(mmol_to_milligram(Double.parseDouble(nums[i])))*10d)/10d);
                }
            }

            bloodsugar_level.setDisplayedValues(nums);
            measure = "%";
        }
        //mmol/l is clicked
        else if(view.getId() == R.id.bs_mm)
        {
            if(measure.equals("mg/dl")) {
                //convert mg to mmol
                for (int i = 0; i < nums.length; i++) {
                    nums[i] = Double.toString(Math.round(miligram_to_mol(Double.parseDouble(nums[i]))*10d)/10d);
                }
                bloodsugar_level.setDisplayedValues(nums);

            }else if (measure.equals("%")) {
                for (int i = 0; i < nums.length; i++) {
                    //Convert from percentage to mg to mmol
                    nums[i] = Double.toString(Math.round(miligram_to_mol(percentage_to_mg(Double.parseDouble(nums[i])))*10d)/10d);
                }
            }
            bloodsugar_level.setDisplayedValues(nums);
            measure = "mmol/l";
        }
    }

    /***
     * Converts mg/dl in mmol/l
     * @param mg
     * @return mmol/l
     */
    private double miligram_to_mol(double mg)
    {
        return mg * 0.0555;
    }

    /***
     * Converts mmol/l in mg/dl
     * @param mmol
     * @return mg/dl
     */
    private double mmol_to_milligram(double mmol)
    {
        return mmol * 18.0182;
    }


    /***
     * Converts HbA1c percentage to mg/dl
     * @param percent
     * @return mg/dl
     */
    private double percentage_to_mg(double percent)
    {
        return percent*33.3-86.0;
    }

    /***
     * Converts mg/dl to HbA1c percentage
     * @param mg
     * @return percentage of HbA1c
     */
    private double mg_to_percentage(double mg)
    {
        return (mg+86.0)/33.3;
    }

    public void setbloodsugarOnCreate(bloodsugar_dialog bs,String measures, String measurement, int ID)
    {
       if(ID == 2) {
           bs.measure_value = measures;
           bs.measure = measurement;
       }
    }


}
