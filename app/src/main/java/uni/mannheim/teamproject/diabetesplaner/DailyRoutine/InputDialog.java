package uni.mannheim.teamproject.diabetesplaner.DailyRoutine;

import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.Calendar;

import uni.mannheim.teamproject.diabetesplaner.R;

/**
 * Created by Stefan on 04.02.2016.
 */
public class InputDialog extends DialogFragment {
    private String starttime = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE);
    private String endtime = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE);
    private int activity = 0;
    private TimePickerFragment timePickerFragmentStart;
    private TimePickerFragment timePickerFragmentEnd;


    /**
     * creates the layout and returns the view object that contains it
     * @return
     */
    public View getLayout(){
        LayoutInflater inflater = (LayoutInflater) getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.add_dialog, null);

        //spinner menu with the activities
        Spinner spinner = (Spinner) v.findViewById(R.id.add_dialog_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.activity_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setSelection(activity);

        //button for starttime TimePicker
        final Button startTimeButton = (Button) v.findViewById(R.id.start_button);
        startTimeButton.setText(getStarttime());
        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerFragmentStart = new TimePickerFragment();
                timePickerFragmentStart.setButton(startTimeButton);
                timePickerFragmentStart.setTime(starttime);
                timePickerFragmentStart.show(getFragmentManager(), "timePicker");
            }
        });

        //button for endtime TimePicker
        final Button endTimeButton = (Button) v.findViewById(R.id.end_button);
        endTimeButton.setText(getEndtime());
        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerFragmentEnd = new TimePickerFragment();
                timePickerFragmentEnd.setButton(endTimeButton);
                timePickerFragmentEnd.setTime(endtime);
                timePickerFragmentEnd.show(getFragmentManager(), "timePicker");

            }
        });

        return v;
    }

    /**
     * returns the starttime
     * @return
     */
    public String getStarttime(){
        return starttime;
    }

    /**
     * returns the endtime
     * @return
     */
    public String getEndtime(){
        return endtime;
    }

    /**
     * sets the endtime that should be displayed in the endtime button
     * @param endtime
     */
    public void setEndtime(String endtime){
        this.endtime = endtime;
    }

    /**
     * sets the starttime that should be displayed in the starttime button
     * @param starttime
     */
    public void setStarttime(String starttime){
        this.starttime = starttime;
    }

    /**
     * set the activity of the input dialog
     * @param activity activity id
     */
    public void setActivity(int activity){
        this.activity = activity;
    }

    /**
     * returns the TimePickerFragment
     * @return
     */
    public TimePickerFragment getTimePickerFragmentStart(){
        return timePickerFragmentStart;
    }

    /**
     * returns the TimePickerFragment
     * @return
     */
    public TimePickerFragment getTimePickerFragmentEnd(){
        return timePickerFragmentStart;
    }
}
