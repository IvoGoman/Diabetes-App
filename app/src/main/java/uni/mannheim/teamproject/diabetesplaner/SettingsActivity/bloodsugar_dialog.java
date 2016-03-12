package uni.mannheim.teamproject.diabetesplaner.SettingsActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
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
    Dialog_communicator communicator;
    private String measure;
    DataBaseHandler database;
    double value;
    String[] nums;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator = (Dialog_communicator) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.dialog_layout, null);

        setCancelable(false);
        measure = "mg/dl";
        database = new DataBaseHandler(getActivity().getApplicationContext());
        submit = (Button) view.findViewById(R.id.bs_submit);
        cancel = (Button) view.findViewById(R.id.bs_cancel);
        mg = (RadioButton) view.findViewById(R.id.bs_mg);
        mmol = (RadioButton) view.findViewById(R.id.bs_mm);
        percentage = (RadioButton) view.findViewById(R.id.bs_percentage);
        bloodsugar_level = (NumberPicker) view.findViewById(R.id.numberPicker);

        String[] nums = new String[50];
        for(int i = 0;i<nums.length;i++)
        {
            nums[i] = Double.toString(Math.round(percentage_to_mg(4.7 + 0.1 * i) * 10d) / 10d);
        }
        bloodsugar_level.setWrapSelectorWheel(false);
        bloodsugar_level.setDisplayedValues(nums);

        bloodsugar_level.setMinValue(0);
        bloodsugar_level.setMaxValue(49);
        submit.setOnClickListener(this);
        cancel.setOnClickListener(this);
        mg.setOnClickListener(this);
        mmol.setOnClickListener(this);
        percentage.setOnClickListener(this);
        value = (double) bloodsugar_level.getValue();
        nums = bloodsugar_level.getDisplayedValues();


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
        if(view.getId() == R.id.bs_submit)
        {
            if(value != bloodsugar_level.getValue()) {
                //database.InsertBloodsugar(AppGlobal.getHandler(), 1, (double) bloodsugar_level.getValue());
                communicator.respond(String.valueOf(nums[bloodsugar_level.getValue()]), measure);
                Toast.makeText(getActivity(), "Blood sugar level: " + String.valueOf(bloodsugar_level.getValue()) + " stored"
                        , Toast.LENGTH_LONG);
                dismiss();
            }else{
                Log.d("bloodsugar_entry","Nothing changed");
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
        else if(view.getId() == R.id.bs_cancel)
        {
            dismiss();
        }
        //mg/dl is clicked
        else if(view.getId() == R.id.bs_mg)
        {

            if (measure=="%") {
                for (int i = 0; i < nums.length; i++) {
                    //Convert from percetage to mg
                    nums[i] = Double.toString(Math.round(percentage_to_mg(Double.parseDouble(nums[i]))));
                }
            }else if(measure == "mmol/l")
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

            if(measure == "mg/dl") {
                for (int i = 0; i < nums.length; i++) {
                    //Convert from mg to percentage
                    nums[i] = Double.toString(Math.round(mg_to_percentage(Double.parseDouble(nums[i]))*10d)/10d);
                }
            } else if(measure == "mmol/l")
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
            if(measure == "mg/dl") {
                //convert mg to mmol
                for (int i = 0; i < nums.length; i++) {
                    nums[i] = Double.toString(Math.round(miligram_to_mol(Double.parseDouble(nums[i]))*10d)/10d);
                }
                bloodsugar_level.setDisplayedValues(nums);

            }else if (measure=="%") {
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


}
