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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.UI.ActivityMeasurementFrag.MeasurementFragment;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.MeasurementInputHandler;
import uni.mannheim.teamproject.diabetesplaner.UI.CustomListView;
import uni.mannheim.teamproject.diabetesplaner.R;
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

    private String measure;
    private String measure_value;

    private String insulin;
    private String insulin_value;


    DataBaseHandler database;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Input Measurements");

        View view = getLayout();


        builder.setView(view);

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


        builder.setPositiveButton(R.string.ADD, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //add measurements
                try {
                    measure_value = addBloodSugar.getText().toString();

                    insulin_value = addInsulin.getText().toString();
                    database.InsertBloodsugarEntryScreen(database, TimeUtils.getTimeStampAsDateString(Calendar.getInstance().getTimeInMillis()), 1, Double.parseDouble(measure_value), measure);

                   database.InsertInsulinEntryScreen(database, TimeUtils.getTimeStampAsDateString(Calendar.getInstance().getTimeInMillis()), 1, Double.parseDouble(insulin_value), insulin);
                    Toast.makeText(getActivity(), "Measurements: " + measure_value + " " + measure + "," + insulin_value + " " + insulin + " stored", Toast.LENGTH_LONG).show();
                    dismiss();
                    /* else {
                        Log.d("bloodsugar_entry", "Nothing changed");
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

                    }*/
                } catch (Exception e) {
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

        if (database.getLastBloodsugarMeasurement(AppGlobal.getHandler(), 1) != null) {
            addBloodSugar.setText(database.getLastBloodsugarMeasurement(AppGlobal.getHandler(), 1)[0].toString());
            measure = database.getLastBloodsugarMeasurement(AppGlobal.getHandler(), 1)[1].toString();
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



}
