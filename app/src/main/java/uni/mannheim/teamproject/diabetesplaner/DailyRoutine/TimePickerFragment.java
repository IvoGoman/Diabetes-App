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
        if(button != null) {
            button.setText(hourOfDay + ":" + minute);
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
}