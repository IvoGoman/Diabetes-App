package uni.mannheim.teamproject.diabetesplaner.UI.DailyRoutine;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

import uni.mannheim.teamproject.diabetesplaner.R;

/**
 * Created by Naira
 */
public class TimerPickerFragmentM extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private MeasurementDialog dialog;
    private int hour;
    private int minute;
    private boolean initial = true;

    /**
     * called when the time picker opens
     * @param savedInstanceState
     * @return Time Picker
     * @author Naira
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        if(initial) {
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
            initial = false;
        }
        return new TimePickerDialog(getActivity(), R.style.picker, this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    /**
     * time setting according to on Time Picker
     * @param view
     * @param hourOfDay
     * @param minute
     * @author Naira
     */
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        try {
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
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * sets the Measurement dialog to the chosen time
     * @param m_dialog
     * @author Naira
     */
    public void SetDialog(MeasurementDialog m_dialog) {
        this.dialog = m_dialog;
    }
}