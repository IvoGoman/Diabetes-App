package uni.mannheim.teamproject.diabetesplaner.UI.DailyRoutine;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

import uni.mannheim.teamproject.diabetesplaner.R;


public class DatePickerFragmentM extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    MeasurementDialog dialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        //Edit by Naira, Changing  date picker color
        //return new DatePickerDialog(getActivity(), this, year, month, day);
        return new DatePickerDialog(getActivity(), R.style.picker, this,year,month,day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        try
        {
            if (month < 10)
            {
                dialog.btn_date.setText(String.format("%d.0%d.%d",day,month,year));
            }
            if(day < 10)
            {
                dialog.btn_date.setText(String.format("0%d.%d.%d",day,month,year));
            }
            if((day < 10) && (month < 10))
            {
                dialog.btn_date.setText(String.format("0%d.0%d.%d",day,month,year));
            }
            if((day > 10) && (month > 10))
            {
                dialog.btn_date.setText(String.format("%d.%d.%d",day,month,year));
            }

        }catch (Exception e)
        {
            e.getMessage();
        }
    }

    public void setBloodsugarDialog(MeasurementDialog bs_dialog)
    {
        this.dialog = bs_dialog;
    }
}