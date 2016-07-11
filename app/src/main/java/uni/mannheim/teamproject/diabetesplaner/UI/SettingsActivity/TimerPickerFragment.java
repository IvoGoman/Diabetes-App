package uni.mannheim.teamproject.diabetesplaner.UI.SettingsActivity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.TimeUtils;
import android.widget.TimePicker;


import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


/**
 * Created by Jan on 11.07.16.
 */

public class TimerPickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private bloodsugar_dialog dialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        try
        {
            if(hourOfDay < 10)
            {
                dialog.btn_time.setText(String.format("0%d:%d",hourOfDay,minute));
            }
            if (minute < 10)
            {
                dialog.btn_time.setText(String.format("%d:0%d",hourOfDay,minute));
            }
            if((hourOfDay<10) && (minute < 10))
            {
                dialog.btn_time.setText(String.format("%0d:0%d",hourOfDay,minute));
            }
            if((hourOfDay>10) && (minute > 10))
            {
                dialog.btn_time.setText(String.format("%d:%d",hourOfDay,minute));
            }

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