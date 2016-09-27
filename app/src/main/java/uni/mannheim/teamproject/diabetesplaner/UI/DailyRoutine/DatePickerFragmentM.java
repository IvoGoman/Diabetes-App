package uni.mannheim.teamproject.diabetesplaner.UI.DailyRoutine;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

import uni.mannheim.teamproject.diabetesplaner.R;

/**
 * Created by Naira
 */
public class DatePickerFragmentM extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    MeasurementDialog dialog;
    private int year;
    private int month;
    private int day;

    /**
     * called when the Date picker opens
     * @param savedInstanceState
     * @return new DatePickerDialog
     * @author Naira
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
            if(year == 0 || month == 0 || day == 0) {
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            }
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), R.style.picker, this, year, month, day);
    }

    /**
     * date setting according to on Date Picker
     * @param view
     * @param year
     * @param month
     * @param day
     * @author Naira
     */
    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        // 0 is January therefore for the Month +1
        month += 1;
        try {
            if ((month < 10) && (day > 9)) {
                dialog.btn_date.setText(String.format("%d.0%d.%d", day, month, year));
            } else if (month > 9 && day < 10) {
                dialog.btn_date.setText(String.format("0%d.%d.%d", day, month, year));
            } else if ((day < 10) && (month < 10)) {
                dialog.btn_date.setText(String.format("0%d.0%d.%d", day, month, year));
            } else {
                dialog.btn_date.setText(String.format("%d.%d.%d", day, month, year));
            }
            this.year = year;
            this.month = month - 1;
            this.day = day;
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * sets the Measurement dialog to the chosen dates
     * @param m_dialog
     * @author Naira
     */
    public void setMeasurementDialog(MeasurementDialog m_dialog) {
        this.dialog = m_dialog;
    }
}