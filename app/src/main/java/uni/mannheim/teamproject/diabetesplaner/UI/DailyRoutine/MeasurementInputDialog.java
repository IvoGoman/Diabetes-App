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
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.UI.ActivityMeasurementFrag.MeasurementFragment;
import uni.mannheim.teamproject.diabetesplaner.UI.SettingsActivity.DatePickerFragment;
import uni.mannheim.teamproject.diabetesplaner.UI.SettingsActivity.TimerPickerFragment;
import uni.mannheim.teamproject.diabetesplaner.UI.SettingsActivity.bloodsugar_dialog;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.MeasurementInputHandler;
import uni.mannheim.teamproject.diabetesplaner.UI.CustomListView;
import uni.mannheim.teamproject.diabetesplaner.R;
import uni.mannheim.teamproject.diabetesplaner.Utility.Util;

/**
 * created by Naira
 */

public class MeasurementInputDialog extends DialogFragment {



    TableRow first_tableRow;
    RadioButton blood_sugar_label;
    TableRow second_tableRow;
    RadioButton insulinDosage_label;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View getLayout(){
        LayoutInflater inflater = (LayoutInflater) getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.popup_measurement_window, null);

        first_tableRow = (TableRow) v.findViewById(R.id.first_tableRow);
        blood_sugar_label = (RadioButton) v.findViewById(R.id.blood_sugar_label);
        second_tableRow = (TableRow) v.findViewById(R.id.second_tableRow);
        insulinDosage_label = (RadioButton) v.findViewById(R.id.insulinDosage_label);

        first_tableRow.setVisibility(View.GONE);
        second_tableRow.setVisibility(View.GONE);



        blood_sugar_label.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(first_tableRow.isShown()){
                            Util.slide_up(getActivity(), first_tableRow);
                            first_tableRow.setVisibility(View.GONE);
                            blood_sugar_label.setChecked(false);

                        }
                        else{
                            first_tableRow.setVisibility(View.VISIBLE);
                            Util.slide_down(getActivity(), first_tableRow);
                            blood_sugar_label.setChecked(true);
                            insulinDosage_label.setChecked(false);


                        }

                    }
                }
        );

        insulinDosage_label.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(second_tableRow.isShown()){
                            Util.slide_up(getActivity(), second_tableRow);
                            second_tableRow.setVisibility(View.GONE);
                            insulinDosage_label.setChecked(false);

                        }
                        else{
                            second_tableRow.setVisibility(View.VISIBLE);
                            Util.slide_down(getActivity(), second_tableRow);
                            blood_sugar_label.setChecked(false);
                            insulinDosage_label.setChecked(true);


                        }

                    }
                }
        );

        return v;
    }

   /* @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        //first_tableRow = (TableRow) getActivity().findViewById(R.id.first_tableRow);
        //blood_sugar_label = (TextView) getActivity().findViewById(R.id.blood_sugar_label);
       first_tableRow.setVisibility(View.GONE);
        blood_sugar_label.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(first_tableRow.isShown()){
                            Util.slide_up(getActivity(), first_tableRow);
                            first_tableRow.setVisibility(View.GONE);
                        }
                        else{
                            first_tableRow.setVisibility(View.VISIBLE);
                            Util.slide_down(getActivity(), first_tableRow);
                        }

                    }
                }
        );

        return inflater.inflate(R.layout.popup_measurement_window, parent, false);
    }*/








   /* public void toggle_contents(View v){
        if(first_tableRow.isShown()){
            Util.slide_up(this.getActivity(), first_tableRow);
            first_tableRow.setVisibility(View.GONE);
        }
        else{
            first_tableRow.setVisibility(View.VISIBLE);
            Util.slide_down(this.getActivity(), first_tableRow);
        }
    }*/





}
