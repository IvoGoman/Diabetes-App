package uni.mannheim.teamproject.diabetesplaner.UI.SettingsActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Double2;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Toast;

import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.R;

/**
 * Created by Jan on 22.02.16.
 */
public class bloodsugar_dialog extends DialogFragment implements View.OnClickListener{
    Button submit,cancel;
    RadioButton mg,percentage,mmol;
    EditText bloodsugar_level;
    BloodsugarDialog_and_Settings communicator;
    double roundfactor = 10d;

    //the current selected measure
    private String measure;
    //the current selected measure value
    private String measure_value;
    DataBaseHandler database;
    double value;


    public bloodsugar_dialog()
    {}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator = (BloodsugarDialog_and_Settings) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_layout, null);

        setCancelable(false);

        database = AppGlobal.getHandler();
        submit = (Button) view.findViewById(R.id.bs_submit);
        cancel = (Button) view.findViewById(R.id.bs_cancel);
        mg = (RadioButton) view.findViewById(R.id.bs_mg);
        mmol = (RadioButton) view.findViewById(R.id.bs_mm);
        percentage = (RadioButton) view.findViewById(R.id.bs_percentage);
        bloodsugar_level = (EditText) view.findViewById(R.id.edit_measure_value);


        if (measure.equals("")) {
            measure = "mg/dl";

        }


        if (measure.equals("%")) {
            percentage.setActivated(true);
            percentage.setChecked(true);
            mmol.setActivated(false);
            mmol.setChecked(false);
            mg.setActivated(false);
            mg.setChecked(false);
            //Convert from percetage to mg
            //nums[i] = Double.toString(Math.round((4.7 + 0.1 * i)*10d)/10d);

        } else if (measure.equals("mmol/l")) {
            mmol.setActivated(true);
            mmol.setChecked(true);
            percentage.setActivated(false);
            percentage.setChecked(false);
            mg.setActivated(false);
            mg.setChecked(false);
            //Convert from mmol to mg
            //nums[i] = Double.toString(Math.round(miligram_to_mol(percentage_to_mg(4.7 + 0.1 * i))*10d)/10d);

        } else if (measure.equals("mg/dl")) {
            //convert mg to mmol
            mg.setActivated(true);
            mg.setChecked(true);
            percentage.setActivated(false);
            percentage.setChecked(false);
            mmol.setActivated(false);
            mmol.setChecked(false);
            //nums[i] = Double.toString(Math.round(percentage_to_mg(4.7 + 0.1 * i) * 10d) / 10d);
        }

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

        //submit button clicked
        if(view.getId() == R.id.bs_submit)
        {
            //if value is changed, then store value and change display
            if(measure_value.equals(bloodsugar_level.getText().toString()) == false) {
                measure_value = bloodsugar_level.getText().toString();
                database.InsertBloodsugar(database, 1, Double.parseDouble(measure_value), measure);
                communicator.respond(null,measure_value, measure, 1);
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

                    //Convert from percetage to mg
                    //nums[i] = Double.toString(Math.round(percentage_to_mg(Double.parseDouble(nums[i]))));
                if(bloodsugar_level.getText().toString() != null) {
                    bloodsugar_level.setText(percentage_to_mg(bloodsugar_level.getText().toString()));
                }

            }else if(measure.equals("mmol/l"))
            {

                    //Convert from mmol to mg
                    //nums[i] = Double.toString(Math.round(mmol_to_milligram(Double.parseDouble(nums[i]))));
                if(bloodsugar_level.getText().toString() != null) {
                    bloodsugar_level.setText(mmol_to_milligram(bloodsugar_level.getText().toString()));
                }
            }
            measure = "mg/dl";
        }
        //Percentage is clicked
        else if(view.getId() == R.id.bs_percentage)
        {

            if(measure.equals("mg/dl")) {
                if(bloodsugar_level.getText().toString() != null) {
                    //Convert from mg to percentage
                    bloodsugar_level.setText(mg_to_percentage(bloodsugar_level.getText().toString()));
                    //nums[i] = Double.toString(Math.round(mg_to_percentage(Double.parseDouble(nums[i]))*10d)/10d);
                }
            } else if(measure.equals("mmol/l"))
            {
                if(bloodsugar_level.getText().toString() != null) {
                    //Convert from mmol to mg to percentage
                    bloodsugar_level.setText(mg_to_percentage(mmol_to_milligram(bloodsugar_level.getText().toString())));
                    //nums[i] = Double.toString(Math.round(mg_to_percentage(mmol_to_milligram(Double.parseDouble(nums[i])))*10d)/10d);
                }
            }
            measure = "%";
        }
        //mmol/l is clicked
        else if(view.getId() == R.id.bs_mm)
        {
            if(measure.equals("mg/dl")) {
                //convert mg to mmol
                if(bloodsugar_level.getText().toString() != null) {
                    bloodsugar_level.setText(miligram_to_mol(bloodsugar_level.getText().toString()));
                    //nums[i] = Double.toString(Math.round(miligram_to_mol(Double.parseDouble(nums[i]))*10d)/10d);
                }

            }else if (measure.equals("%")) {
                if(bloodsugar_level.getText().toString() != null) {
                    //Convert from percentage to mg to mmol
                    bloodsugar_level.setText(miligram_to_mol(percentage_to_mg(bloodsugar_level.getText().toString())));
                    //nums[i] = Double.toString(Math.round(miligram_to_mol(percentage_to_mg(Double.parseDouble(nums[i])))*10d)/10d);
                }
            }
            measure = "mmol/l";
        }
    }

    /***
     * Converts mg/dl in mmol/l
     * @param mg
     * @return mmol/l
     */
    private String miligram_to_mol(String mg)
    {
        return String.valueOf(Math.round(Double.parseDouble(mg) * 0.0555*roundfactor)/roundfactor);
    }

    /***
     * Converts mmol/l in mg/dl
     * @param mmol
     * @return mg/dl
     */
    private String mmol_to_milligram(String mmol)
    {
        return String.valueOf(Math.round(Double.parseDouble(mmol) * 18.0182*roundfactor)/roundfactor);
    }


    /***
     * Converts HbA1c percentage to mg/dl
     * @param percent
     * @return mg/dl
     */
    private String percentage_to_mg(String percent)
    {
        return String.valueOf(Math.round((Double.parseDouble(percent)*33.3-86.0)*roundfactor)/roundfactor);
    }

    /***
     * Converts mg/dl to HbA1c percentage
     * @param mg
     * @return percentage of HbA1c
     */
    private String mg_to_percentage(String mg)
    {
        return String.valueOf(Math.round(((Double.parseDouble(mg)+86.0)/33.3)*roundfactor)/roundfactor);
    }

    public void setbloodsugarOnCreate(bloodsugar_dialog bs,String measures, String measurement, int ID)
    {
       if(ID == 2) {
           bs.measure_value = measures;
           bs.measure = measurement;
       }
    }


}
