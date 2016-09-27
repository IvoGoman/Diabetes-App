package uni.mannheim.teamproject.diabetesplaner.UI.Settings;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

import uni.mannheim.teamproject.diabetesplaner.R;

/**
 * Created by Jan on 11.07.16.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    bloodsugar_dialog dialog;
    private int year;
    private int month;
    private int day;

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
        //Edit by Naira, Changing  date picker color
        //return new DatePickerDialog(getActivity(), this, year, month, day);
        return new DatePickerDialog(getActivity(), R.style.picker, this,year,month,day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user

        //      0 is January therefore for the Month +1
        month += 1;
        try
        {
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
        }catch (Exception e)
        {
            e.getMessage();
        }
    }

    public void setBloodsugarDialog(bloodsugar_dialog bs_dialog)
    {
        dialog = bs_dialog;
    }
}
