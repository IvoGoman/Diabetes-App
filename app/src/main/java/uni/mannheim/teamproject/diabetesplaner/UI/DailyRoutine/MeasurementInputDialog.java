package uni.mannheim.teamproject.diabetesplaner.UI.DailyRoutine;


import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TableRow;

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

    /**
     * called when the input dialog is activated
     *
     * @param savedInstanceState
     * @author Naira
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * sets up the layout of the input dialog
     *
     * @return inflater
     * @author Naira
     */
    public View getLayout() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.popup_measurement_window, null);

        //initializing variables to their corresponding layout elements
        first_tableRow = (TableRow) v.findViewById(R.id.first_tableRow);
        blood_sugar_label = (RadioButton) v.findViewById(R.id.blood_sugar_label);
        second_tableRow = (TableRow) v.findViewById(R.id.second_tableRow);
        insulinDosage_label = (RadioButton) v.findViewById(R.id.insulinDosage_label);

        //drop down elements are by default hidden
        first_tableRow.setVisibility(View.GONE);
        second_tableRow.setVisibility(View.GONE);

        //showing/hiding drop down elements on the dialog for the blood sugar radio button
        blood_sugar_label.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (first_tableRow.isShown()) {
                            Util.slide_up(getActivity(), first_tableRow);
                            first_tableRow.setVisibility(View.GONE);
                            blood_sugar_label.setChecked(false);
                        } else {
                            first_tableRow.setVisibility(View.VISIBLE);
                            Util.slide_down(getActivity(), first_tableRow);
                            blood_sugar_label.setChecked(true);
                            insulinDosage_label.setChecked(false);
                        }
                    }
                }
        );

        //showing/hiding drop down elements on the dialog for the insulin radio button
        insulinDosage_label.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (second_tableRow.isShown()) {
                            Util.slide_up(getActivity(), second_tableRow);
                            second_tableRow.setVisibility(View.GONE);
                            insulinDosage_label.setChecked(false);
                        } else {
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
}