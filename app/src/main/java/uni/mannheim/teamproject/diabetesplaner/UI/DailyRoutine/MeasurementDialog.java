package uni.mannheim.teamproject.diabetesplaner.UI.DailyRoutine;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.MeasureItem;
import uni.mannheim.teamproject.diabetesplaner.R;
import uni.mannheim.teamproject.diabetesplaner.UI.EntryScreenActivity;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;
import uni.mannheim.teamproject.diabetesplaner.Utility.Util;

/**
 * created by Naira
 */

public class MeasurementDialog extends MeasurementInputDialog {

    EditText addBloodSugar;
    EditText addInsulin;

    RadioButton mg, percentage, mmol;
    RadioButton units, ml;

    Button btn_date;
    Button btn_time;

    private String measure;
    private String measure_value;
    private String insulin;
    private String insulin_value;
    private MeasureItem measureItem;

    private Long timestamp;

    DataBaseHandler database;

    private TimerPickerFragmentM TimerPicker;
    private DatePickerFragmentM DatePicker;
    Date current_date;

    /**
     *
     * @param savedInstanceState
     * @return
     * @author Naira
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.input_measurements);

        View view = getLayout();

        builder.setView(view);

        Date date = Calendar.getInstance(Locale.getDefault()).getTime();
        current_date = date;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:MM:ss");
        String dateString = dateFormat.format(date);
        String timeString = timeFormat.format(date);


        addBloodSugar = (EditText) view.findViewById(R.id.edit_measure_value_entryscreen);
        addInsulin = (EditText) view.findViewById(R.id.edit_insulin_value);
        database = AppGlobal.getHandler();
        mg = (RadioButton) view.findViewById(R.id.bs_mg);
        mmol = (RadioButton) view.findViewById(R.id.bs_mm);
        percentage = (RadioButton) view.findViewById(R.id.bs_percentage);
        units = (RadioButton) view.findViewById(R.id.insulin_unit);
        ml = (RadioButton) view.findViewById(R.id.insulin_ml);
        measure = "";
        insulin = "";


        //Button for DatePicker
        btn_date = (Button) view.findViewById(R.id.btn_date);
        btn_date.setText(dateString);

        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker = new DatePickerFragmentM();
                DatePicker.setBloodsugarDialog(MeasurementDialog.this);
                DatePicker.show(getFragmentManager(), "datePicker");


            }
        });


        //Button for TimerPicker
        btn_time = (Button) view.findViewById(R.id.btn_time);
        btn_time.setText(timeString);
        btn_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimerPicker = new TimerPickerFragmentM();
                TimerPicker.SetDialog(MeasurementDialog.this);
                TimerPicker.show(getFragmentManager(), "timePicker");
                //timerpickerfragment.SetDialog(bloodsugar_dialog.this);
            }
        });


        builder.setPositiveButton(R.string.ADD, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //add measurements

                try {
                    measure_value = addBloodSugar.getText().toString();

                    insulin_value = addInsulin.getText().toString();
                    String date_s = btn_date.getText().toString();
                    String time_s = btn_time.getText().toString();
                    timestamp = TimeUtils.convertDateAndTimeStringToDate(date_s, time_s).getTime();

                    if (measure_value.equals("") == true && insulin_value.equals("") == true) {
                        Toast.makeText(getActivity(), "No Measurements have been entered", Toast.LENGTH_LONG).show();
                    }


                    if (measure_value.equals("") == true) {
                        measureItem = new MeasureItem(timestamp, Double.parseDouble(insulin_value), insulin, MeasureItem.MEASURE_KIND_INSULIN);
                        database.insertMeasurement(measureItem, database.getUserID());

                        EntryScreenActivity.updateDailyRoutine();

                        Toast.makeText(getActivity(), "No Blood sugar level entered; " + insulin_value + " " + insulin + " stored", Toast.LENGTH_LONG).show();

                    } else if (insulin_value.equals("") == true) {
                        measureItem = new MeasureItem(timestamp, Double.parseDouble(measure_value), measure, MeasureItem.MEASURE_KIND_BLOODSUGAR);
                        database.insertMeasurement(measureItem, database.getUserID());

                        EntryScreenActivity.updateDailyRoutine();

                        Toast.makeText(getActivity(), "No Insulin Dosage entered; " + measure_value + " " + measure + " stored", Toast.LENGTH_LONG).show();
                    } else if (check_mg(convert_to_mg(Double.parseDouble(measure_value), measure)) == true && check_Units(convert_to_Units(Double.parseDouble(insulin_value), insulin)) == true) {
                        measureItem = new MeasureItem(timestamp, Double.parseDouble(measure_value), measure, MeasureItem.MEASURE_KIND_BLOODSUGAR);
                        database.insertMeasurement(measureItem, database.getUserID());

                        measureItem = new MeasureItem(timestamp, Double.parseDouble(insulin_value), insulin, MeasureItem.MEASURE_KIND_INSULIN);
                        database.insertMeasurement(measureItem, database.getUserID());

                        EntryScreenActivity.updateDailyRoutine();

                        Toast.makeText(getActivity(), "Measurements: " + measure_value + " " + measure + "," + insulin_value + " " + insulin + " stored", Toast.LENGTH_LONG).show();
                        dismiss();
                    } else {
                        Toast.makeText(getActivity(), "Invalid Measurements", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    Log.d("Rec", "" + e);
                    e.getMessage();
                }
            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dismiss();
                    }
                });

        initialize_measure();
        // initialize_insulinMeasure();
        mg.setOnClickListener(myListenser);
        mmol.setOnClickListener(myListenser);
        percentage.setOnClickListener(myListenser);
        ml.setOnClickListener(myListenser);
        units.setOnClickListener(myListenser);

        // Create the AlertDialog object and return it
        return builder.create();

    }


    private void initialize_measure() {

        if (database.getLastBloodsugarMeasurement(1) != null) {
            addBloodSugar.setText(database.getLastBloodsugarMeasurement(1)[0].toString());
            measure = database.getLastBloodsugarMeasurement(1)[1].toString();
        }

        if (measure.equals("")) {
            measure = "mg/dl";
        }

        switch (measure) {
            case "%":
                percentage.setActivated(true);
                percentage.setChecked(true);
                mmol.setActivated(false);
                mmol.setChecked(false);
                mg.setActivated(false);
                mg.setChecked(false);
                break;

            case "mmol/l":
                mmol.setActivated(true);
                mmol.setChecked(true);
                percentage.setActivated(false);
                percentage.setChecked(false);
                mg.setActivated(false);
                mg.setChecked(false);
                break;

            case "mg/dl":
                mg.setActivated(true);
                mg.setChecked(true);
                percentage.setActivated(false);
                percentage.setChecked(false);
                mmol.setActivated(false);
                mmol.setChecked(false);
                break;
        }
        if (database.getLastInsulinMeasurement(1) != null) {
            addInsulin.setText(database.getLastInsulinMeasurement(1)[0].toString());
            insulin = database.getLastInsulinMeasurement(1)[1].toString();
        }

        if (insulin.equals("")) {
            insulin = "Units";
        }

        switch (insulin) {
            case "Units":
                units.setActivated(true);
                units.setChecked(true);
                ml.setActivated(false);
                ml.setChecked(false);
                break;

            case "mL/cc":
                ml.setActivated(true);
                ml.setChecked(true);
                units.setActivated(false);
                units.setChecked(false);
                break;

        }
    }

  /*  private void initialize_insulinMeasure() {
        if(database.getLastInsulinMeasurement(AppGlobal.getHandler(),1) != null) {
            addInsulin.setText(database.getLastInsulinMeasurement(AppGlobal.getHandler(), 1)[0].toString());
            insulin = database.getLastInsulinMeasurement(AppGlobal.getHandler(), 1)[1].toString();
        }
        if (insulin.equals("")) {
            insulin = "Units";
        }
        switch (insulin) {
            case "Units":
                units.setActivated(true);
                units.setChecked(true);
                ml.setActivated(false);
                ml.setChecked(false);
                break;
            case "mL/cc":
                ml.setActivated(true);
                ml.setChecked(true);
                units.setActivated(false);
                units.setChecked(false);
                break;
        }
    }*/

    /***
     * Click Handler for the dialog.
     *
     * @param view
     */

    private View.OnClickListener myListenser = new View.OnClickListener() {
        public void onClick(View view) {
            //mg/dl is clicked
            if (view.getId() == R.id.bs_mg) {
                if (measure.equals("%")) {
                    if (addBloodSugar.getText().toString().isEmpty() == false) {
                        addBloodSugar.setText(String.valueOf(Util.percentage_to_mg(Double.parseDouble(addBloodSugar.getText().toString()))));
                    }

                } else if (measure.equals("mmol/l")) {
                    if (addBloodSugar.getText().toString().isEmpty() == false) {
                        addBloodSugar.setText(String.valueOf(Util.mmol_to_milligram(Double.parseDouble(addBloodSugar.getText().toString()))));
                    }
                }
                measure = "mg/dl";
            }
            //Percentage is clicked
            else if (view.getId() == R.id.bs_percentage) {

                if (measure.equals("mg/dl")) {
                    if (addBloodSugar.getText().toString().isEmpty() == false) {
                        //Convert from mg to percentage
                        addBloodSugar.setText(String.valueOf(Util.mg_to_percentage(Double.parseDouble(addBloodSugar.getText().toString()))));
                    }
                } else if (measure.equals("mmol/l")) {
                    if (addBloodSugar.getText().toString().isEmpty() == false) {
                        //Convert from mmol to mg to percentage
                        addBloodSugar.setText(String.valueOf(Util.mg_to_percentage(Util.mmol_to_milligram(Double.parseDouble(addBloodSugar.getText().toString())))));
                    }
                }
                measure = "%";
            }
            //mmol/l is clicked
            else if (view.getId() == R.id.bs_mm) {
                if (measure.equals("mg/dl")) {
                    //convert mg to mmol
                    if (addBloodSugar.getText().toString().isEmpty() == false) {
                        addBloodSugar.setText(String.valueOf(Util.miligram_to_mol(Double.parseDouble(addBloodSugar.getText().toString()))));
                    }

                } else if (measure.equals("%")) {
                    if (addBloodSugar.getText().toString().isEmpty() == false) {
                        //Convert from percentage to mg to mmol
                        addBloodSugar.setText(String.valueOf(Util.miligram_to_mol(Util.percentage_to_mg(Double.parseDouble(addBloodSugar.getText().toString())))));
                    }
                }
                measure = "mmol/l";
            }

            //unit clicked
            if (view.getId() == R.id.insulin_unit) {
                if (insulin.equals("mL/cc")) {
                    if (addInsulin.getText().toString().isEmpty() == false) {
                        addInsulin.setText(String.valueOf(Util.ml_to_Units(Double.parseDouble(addInsulin.getText().toString()))));
                    }

                }
                insulin = "Units";
            }
            //ml clicked
            else if (view.getId() == R.id.insulin_ml) {

                if (insulin.equals("Units")) {
                    if (addInsulin.getText().toString().isEmpty() == false) {
                        addInsulin.setText(String.valueOf(Util.Units_to_ml(Double.parseDouble(addInsulin.getText().toString()))));
                    }
                }
                insulin = "mL/cc";
            }

        }
    };

    /***
     * Click Handler for the dialog.
     *
     * @param view
     */
 /*   private View.OnClickListener myListenserTwo = new View.OnClickListener() {
        public void onClick(View view) {
            //Units is clicked
            if (view.getId() == R.id.insulin_unit) {
                if (insulin.equals("Units")) {
                    if (addInsulin.getText().toString().isEmpty() == false) {
                        addInsulin.setText(String.valueOf(Util.Units_to_ml(Double.parseDouble(addInsulin.getText().toString()))));
                    }
                    insulin = "mL/cc";
                }
            }
            //ml/cc is clicked
                else if (view.getId() == R.id.insulin_ml) {
                    if (insulin.equals("mL/cc")) {
                        if (addInsulin.getText().toString().isEmpty() == false) {
                            //Convert from ml to Units
                            addInsulin.setText(String.valueOf(Util.ml_to_Units(Double.parseDouble(addInsulin.getText().toString()))));
                        }
                    }
                    insulin = "Units";
                }
            }
            };*/
    private double convert_to_mg(double value, String unit) {
        switch (unit) {
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
     *
     * @param value
     * @return
     */
    private boolean check_mg(double value) {
        if (value <= 50) {
            return false;
        } else if (value >= 500) {
            return false;
        } else {
            return true;
        }
    }

    private double convert_to_Units(double value, String unit) {
        switch (unit) {
            case "Units":
                return value;
            case "mL/cc":
                return Util.ml_to_Units(value);

        }
        return 0.0;
    }

    /***
     * checks the inserted value
     *
     * @param value
     * @return
     */
    private boolean check_Units(double value) {
        if (value <= 0.0) {
            return false;
        } else if (value >= 80) {
            return false;
        } else {
            return true;
        }
    }


}