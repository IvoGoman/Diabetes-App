package uni.mannheim.teamproject.diabetesplaner.DailyRoutine;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Stefan on 04.02.2016.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private Button button;
    private Integer hour;
    private Integer minute;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        if(hour == null || minute == null) {
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        }

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        if(button != null) {
            if(minute<10){
                button.setText(hourOfDay + ":0" + minute);
            }else {
                button.setText(hourOfDay + ":" + minute);
            }
        }
        Log.d(this.getClass().getSimpleName(),hourOfDay + ":" + minute);
    }

    /**
     * setter for button that is related to the timePicker object
     * @param button
     */
    public void setButton(Button button){
        this.button = button;
    }

    /**
     * sets the minute of the picker to be shown
     * @param minute
     */
    public void setMinute(int minute){
        this.minute = minute;
    }

    /**
     * sets the hour of the picker to be shown
     * @param hour
     */
    public void setHour(int hour){
        this.hour = hour;
    }

    /**
     * takes time in format mm:HH and sets it to initial time of the TimePicker
     * @param time
     */
    public void setTime(String time){
        String[] tmp = time.split(":");
        //set hour
        setHour(Integer.valueOf(tmp[0]));

        //handle 0 in front
        if (tmp[1].charAt(0) == '0') {
            setMinute(Integer.valueOf(String.valueOf(tmp[1].charAt(1))));
        }else {
            setMinute(Integer.valueOf(tmp[1]));
        }
    }
}