package uni.mannheim.teamproject.diabetesplaner.UI.Settings;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

import uni.mannheim.teamproject.diabetesplaner.R;


/**
 * Created by Jan on 11.07.16.
 */

public class TimerPickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private bloodsugar_dialog dialog;
    private int hour;
    private int minute;
    private boolean initial = true;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        if(initial) {
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
            initial = false;
        }

        // Create a new instance of TimePickerDialog and return it
        //Edit by Naira, Changing date picker color
        //return new TimePickerDialog(getActivity(), this, year, month, day);
        return new TimePickerDialog(getActivity(),  R.style.picker,this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        try
        {
            if ((hourOfDay < 10) && (minute > 9)) {
                dialog.btn_time.setText(String.format("0%d:%d", hourOfDay, minute));
            } else if ((hourOfDay > 9) && (minute < 10)) {
                dialog.btn_time.setText(String.format("%d:0%d", hourOfDay, minute));
            } else if ((hourOfDay < 10) && (minute < 10)) {
                dialog.btn_time.setText(String.format("%0d:0%d", hourOfDay, minute));
            } else {
                dialog.btn_time.setText(String.format("%d:%d", hourOfDay, minute));
            }
            this.hour = hourOfDay;
            this.minute = minute;

        }catch (Exception e)
        {
            e.getMessage();
        }
        //dialog.time_picker.setTime(null);
    }

    public void SetDialog(bloodsugar_dialog bloodsugar_dialog)
    {
        this.dialog = bloodsugar_dialog;
    }
}