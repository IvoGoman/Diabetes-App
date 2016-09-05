package uni.mannheim.teamproject.diabetesplaner.UI.DailyRoutine;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

import uni.mannheim.teamproject.diabetesplaner.R;




public class TimerPickerFragmentM extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private MeasurementDialog dialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);


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
            if(hourOfDay < 10)
            {
                //dialog.btn_time.setText(String.format("0%d:%d",hourOfDay,minute));
            }
            if (minute < 10)
            {
                //dialog.btn_time.setText(String.format("%d:0%d",hourOfDay,minute));
            }
            if((hourOfDay<10) && (minute < 10))
            {
                //dialog.btn_time.setText(String.format("%0d:0%d",hourOfDay,minute));
            }
            if((hourOfDay>10) && (minute > 10))
            {
                //dialog.btn_time.setText(String.format("%d:%d",hourOfDay,minute));
            }

        }catch (Exception e)
        {
            e.getMessage();
        }
        //dialog.time_picker.setTime(null);
    }

    public void SetDialog(MeasurementDialog m_dialog)
    {
        this.dialog = m_dialog;
    }
}