package uni.mannheim.teamproject.diabetesplaner.DailyRoutine;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Button;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import uni.mannheim.teamproject.diabetesplaner.R;

/**
 * Created by Stefan on 04.02.2016.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private Button button;
    private Integer hour;
    private Integer minute;
    private boolean isStart;
    private InputDialog dialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        if(hour == null || minute == null) {
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        }

        // Create a new instance of TimePickerDialog and return it
        // Edit by Naira, Changing Time picker color
      //  return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        return new TimePickerDialog(getActivity(), R.style.picker, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            }
        },hour,minute,DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        String dateString = "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            Date date = dateFormat.parse(hourOfDay+":"+minute);
            dateString = dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if(isStart){
            dialog.setStarttime(dateString);
        }else{
            dialog.setEndtime(dateString);
        }
        Log.d(this.getClass().getSimpleName(),hourOfDay + ":" + minute);
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

    /**
     * sets if timepicker is for starttime or endtime
     * @param isStart
     */
    public void setStart(boolean isStart){
        this.isStart = isStart;
    }
    
    public void setInputDialog(InputDialog dialog){
        this.dialog = dialog;
    }

}