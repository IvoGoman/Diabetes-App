package uni.mannheim.teamproject.diabetesplaner.UI.Settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.MeasureItem;
import uni.mannheim.teamproject.diabetesplaner.R;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;
import uni.mannheim.teamproject.diabetesplaner.Utility.Util;

/**
 * Created by Jan on 22.02.16.
 * @author: Jan
 */
public class bloodsugar_dialog extends DialogFragment implements View.OnClickListener{
    Button submit,cancel;
    RadioButton mg,percentage,mmol;
    EditText bloodsugar_level;
    Button btn_date;
    Button btn_time;
    BloodsugarDialog_and_Settings communicator;
    double roundfactor = 10d;
    private TimerPickerFragment TimerPicker;
    private DatePickerFragment DatePicker;
    private java.util.Date date = null;
    //the current selected measure
    private String measure;
    private  String date_s;
    private String time_s;
    //the current selected measure value
    private String measure_value;
    DataBaseHandler database;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator = (BloodsugarDialog_and_Settings) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_layout, null);
        database = AppGlobal.getHandler();
        TimerPicker = new TimerPickerFragment();
        DatePicker = new DatePickerFragment();

        mg = (RadioButton) view.findViewById(R.id.bs_mg);
        mmol = (RadioButton) view.findViewById(R.id.bs_mm);
        percentage = (RadioButton) view.findViewById(R.id.bs_percentage);
        bloodsugar_level = (EditText) view.findViewById(R.id.edit_measure_value);

        if (date == null) {
            date = Calendar.getInstance(Locale.getDefault()).getTime();
        }

        //adjusting date and time formats
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String dateString = dateFormat.format(date);
        String timeString = timeFormat.format(date);

        //Button for DatePicker
        btn_date = (Button) view.findViewById(R.id.btn_Date);
        btn_date.setText(dateString);
        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker.setBloodsugarDialog(bloodsugar_dialog.this);
                DatePicker.show(getFragmentManager(), "datePicker");
            }
        });

        //Button for TimerPicker
        btn_time = (Button) view.findViewById(R.id.btn_time);
        btn_time.setText(timeString);
        btn_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimerPicker.SetDialog(bloodsugar_dialog.this);
                TimerPicker.show(getFragmentManager(), "timePicker");
            }
        });

        date_s = btn_date.getText().toString();
        time_s = btn_time.getText().toString();

        AlertDialog.Builder mybuilder = new AlertDialog.Builder(getActivity());
        mybuilder.setView(view);

        mybuilder.setPositiveButton(R.string.submit2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if value is changed, then store value and change display
                try {
                        measure_value = bloodsugar_level.getText().toString();

                        if(btn_time.getText().toString().length() < 8) {
                            time_s = btn_time.getText().toString()+ ":" + Calendar.getInstance().get(Calendar.SECOND);
                        }
                        long timestamp = TimeUtils.convertDateAndTimeStringToDate(date_s, time_s).getTime();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(timestamp);
                        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),0);
                        timestamp = calendar.getTimeInMillis();
                        //Check if entered value is to high or to low
                        if(check_mg(convert_to_mg(Double.parseDouble(measure_value),measure)) == true) {
                           MeasureItem measureItem = new MeasureItem(timestamp, Double.parseDouble(measure_value),measure, MeasureItem.MEASURE_KIND_BLOODSUGAR);
                            database.insertMeasurement(measureItem, database.getUserID());

                            communicator.respond(null, measure_value, measure, 1);
                            Toast.makeText(getActivity(), "Blood sugar level: " + measure_value + " "
                                            + measure + " stored"
                                    , Toast.LENGTH_LONG).show();
                            dismiss();
                        }
                        else {
                            // if value is to high or to low, throw exception
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                new AlertDialog.Builder(getContext())
                                        .setTitle("Invalid Value")
                                        .setMessage("Please check the entered blood_sugar level. The value might be to high or to low.")
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                                Log.d("bloodsugar_entry", "Value: " + measure_value + " was not valid!");
                            }
                        }

//                    } else {
//                        Log.d("bloodsugar_entry", "Nothing changed");
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            new AlertDialog.Builder(getContext())
//                                    .setTitle("No Changes")
//                                    .setMessage("You did not change the blood_sugar level.")
//                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int which) {
//
//                                        }
//                                    })
//                                    .setIcon(android.R.drawable.ic_dialog_alert)
//                                    .show();
//                        }
//                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }

        });

        mybuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        //super.onCreateDialog(savedInstanceState);

        initialize_measure();
        //submit.setOnClickListener(this);
        //cancel.setOnClickListener(this);
        mg.setOnClickListener(this);
        mmol.setOnClickListener(this);
        percentage.setOnClickListener(this);
        return mybuilder.create();
    }

    /***
     * Initializes the measure buttons
     */
    private void initialize_measure()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        measure = sharedPreferences.getString("pref_bloodsugarOptions","");
        if (measure.equals("")) {
            measure = "mg/dl";
        }

        switch (measure)
        {
            case "%": percentage.setActivated(true);
                percentage.setChecked(true);
                mmol.setActivated(false);
                mmol.setChecked(false);
                mg.setActivated(false);
                mg.setChecked(false);
                break;

            case "mmol/l":  mmol.setActivated(true);
                mmol.setChecked(true);
                percentage.setActivated(false);
                percentage.setChecked(false);
                mg.setActivated(false);
                mg.setChecked(false);
                break;

            case "mg/dl":  mg.setActivated(true);
                mg.setChecked(true);
                percentage.setActivated(false);
                percentage.setChecked(false);
                mmol.setActivated(false);
                mmol.setChecked(false);
                break;
        }
    }

    /***
     * Click Handler for the dialog.
     * @param view
     */
    @Override
    public void onClick(View view)
    {
        //mg/dl is clicked
        if(view.getId() == R.id.bs_mg)
        {
            if (measure.equals("%"))
            {
                    //Convert from percetage to mg
                    //nums[i] = Double.toString(Math.round(percentage_to_mg(Double.parseDouble(nums[i]))));
                if(bloodsugar_level.getText().toString().isEmpty() == false) {
                    bloodsugar_level.setText(String.valueOf(Util.percentage_to_mg(Double.parseDouble(bloodsugar_level.getText().toString()))));
                }

            }else if(measure.equals("mmol/l"))
            {

                    //Convert from mmol to mg
                    //nums[i] = Double.toString(Math.round(mmol_to_milligram(Double.parseDouble(nums[i]))));
                if(bloodsugar_level.getText().toString().isEmpty() == false) {
                    bloodsugar_level.setText(String.valueOf(Util.mmol_to_milligram(Double.parseDouble(bloodsugar_level.getText().toString()))));
                }
            }
            measure = "mg/dl";
        }
        //Percentage is clicked
        else if(view.getId() == R.id.bs_percentage)
        {

            if(measure.equals("mg/dl")) {
                if(bloodsugar_level.getText().toString().isEmpty() == false) {
                    //Convert from mg to percentage
                    bloodsugar_level.setText(String.valueOf(Util.mg_to_percentage(Double.parseDouble(bloodsugar_level.getText().toString()))));
                    //nums[i] = Double.toString(Math.round(mg_to_percentage(Double.parseDouble(nums[i]))*10d)/10d);
                }
            } else if(measure.equals("mmol/l"))
            {
                if(bloodsugar_level.getText().toString().isEmpty() == false) {
                    //Convert from mmol to mg to percentage
                    bloodsugar_level.setText(String.valueOf(Util.mg_to_percentage(Util.mmol_to_milligram(Double.parseDouble(bloodsugar_level.getText().toString())))));
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
                if(bloodsugar_level.getText().toString().isEmpty() == false) {
                    bloodsugar_level.setText(String.valueOf(Util.miligram_to_mol(Double.parseDouble(bloodsugar_level.getText().toString()))));
                    //nums[i] = Double.toString(Math.round(miligram_to_mol(Double.parseDouble(nums[i]))*10d)/10d);
                }

            }else if (measure.equals("%")) {
                if(bloodsugar_level.getText().toString().isEmpty() == false) {
                    //Convert from percentage to mg to mmol
                    bloodsugar_level.setText(String.valueOf(Util.miligram_to_mol(Util.percentage_to_mg(Double.parseDouble(bloodsugar_level.getText().toString())))));
                    //nums[i] = Double.toString(Math.round(miligram_to_mol(percentage_to_mg(Double.parseDouble(nums[i])))*10d)/10d);
                }
            }
            measure = "mmol/l";
        }
    }

    /***
     * Interface Method for SettingsFragment
     * @param bs
     * @param measures
     * @param measurement
     * @param ID
     */
    public void setbloodsugarOnCreate(bloodsugar_dialog bs,String measures, String measurement, int ID)
    {
       if(ID == 2) {
           bs.measure_value = measures;
           bs.measure = measurement;
       }
    }

    /***
     * Converts a given unit to mg
     * @param value the value
     * @param unit
     * @return
     */
    private double convert_to_mg(double value, String unit)
    {
        switch (unit)
        {
            case "mg/dl":
                return value;
            case "mmol/l":
                return Util.mmol_to_milligram(value);
            case "%":
                return Util.percentage_to_mg(value);

        }
        return 0.0;
    }

    /***
     * checks the inserted value
     * @param value
     * @return
     */
    private boolean check_mg(double value)
    {
        if(value <= 0.0)
        {
            return false;
        }
        else if (value >= 250)
        {
            return false;
        }
        else
        {
            return true;
        }
    }




}
