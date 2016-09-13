package uni.mannheim.teamproject.diabetesplaner.UI.DailyRoutine;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import uni.mannheim.teamproject.diabetesplaner.R;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;

/**
 * Created by Stefan on 04.02.2016.
 */
public class TimePickerFragment extends DialogFragment{

    private Button button;
    private Integer hour;
    private Integer minute;
    private boolean isStart;
    private Date date;
    private InputDialog dialog;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        if(hour == null || minute == null) {
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        }

        // Create a new instance of TimePickerFragment and return it
        // Edit by Naira, Changing Time picker color
        //  return new TimePickerFragment(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        return new android.app.TimePickerDialog(getActivity(), R.style.picker, new android.app.TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                date = TimeUtils.getDate(date, hourOfDay, minute);
                //if this is the dialog for the starttime
                if(isStart){
                    dialog.setStartDate(date);
                    //dialog.setStarttime(Util.timeToString(date));
                }else{
                    dialog.setEndDate(date);
                    //dialog.setEndtime(Util.timeToString(date));
                }
            }
        },hour,minute,DateFormat.is24HourFormat(getActivity()));
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

//    /**
//     * takes time in format mm:HH and sets it to initial time of the TimePicker
//     * @param time
//     */
//    public void setTime(String time){
//        String[] tmp = time.split(":");
//        //set hour
//        setHour(Integer.valueOf(tmp[0]));
//
//        //handle 0 in front
//        if (tmp[1].charAt(0) == '0') {
//            setMinute(Integer.valueOf(String.valueOf(tmp[1].charAt(1))));
//        }else {
//            setMinute(Integer.valueOf(tmp[1]));
//        }
//    }

    /**
     * @author Stefan 30.03.2016
     * Sets the minute and hour of the TimePicker from a date object
     * @param date
     */
    public void setTime(Date date){
        this.date = date;
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date
        int hour = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
        int minute = calendar.get(Calendar.MINUTE); //gets the minute

        setMinute(minute);
        setHour(hour);
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