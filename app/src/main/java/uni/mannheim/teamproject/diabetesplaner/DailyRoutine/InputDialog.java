package uni.mannheim.teamproject.diabetesplaner.DailyRoutine;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import uni.mannheim.teamproject.diabetesplaner.Backend.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Backend.DailyRoutineHandler;
import uni.mannheim.teamproject.diabetesplaner.R;

/**
 * Created by Stefan on 04.02.2016.
 */
public class InputDialog extends DialogFragment {
    private static final String TAG = InputDialog.class.getSimpleName();
    private String starttime;
    private String endtime;
    private int activity = 0;
    private TimePickerFragment timePickerFragmentStart;
    private TimePickerFragment timePickerFragmentEnd;
    private Button startTimeButton;
    private Button endTimeButton;
    private String selectedItem;

    private ActivityItem activityItem;
    private DailyRoutineHandler drHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //init times
        Date date = Calendar.getInstance(Locale.getDefault()).getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        starttime = dateFormat.format(date);
        endtime = dateFormat.format(date);
        super.onCreate(savedInstanceState);
    }

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
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapter, View view, int position, long id) {
                activity = ActivityItem.getActivityId(adapter.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //button for starttime TimePicker
        startTimeButton = (Button) v.findViewById(R.id.start_button);
        startTimeButton.setText(endtime);
        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerFragmentStart = new TimePickerFragment();
                timePickerFragmentStart.setInputDialog(InputDialog.this);
                timePickerFragmentStart.setStart(true);
                timePickerFragmentStart.setTime(starttime);
                timePickerFragmentStart.show(getFragmentManager(), "timePicker");
            }
        });

        //button for endtime TimePicker
        endTimeButton = (Button) v.findViewById(R.id.end_button);
        endTimeButton.setText(starttime);
        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerFragmentEnd = new TimePickerFragment();
                timePickerFragmentEnd.setInputDialog(InputDialog.this);
                timePickerFragmentEnd.setStart(false);
                timePickerFragmentEnd.setTime(endtime);
                timePickerFragmentEnd.show(getFragmentManager(), "timePicker");

            }
        });

        return v;
    }

    public boolean isTimeValid() {
        Date starttime = null;
        Date endtime = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            starttime = dateFormat.parse(getStarttime());
            endtime = dateFormat.parse(getEndtime());
            return starttime.before(endtime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
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

    public int getChosenActivity(){
        return activity;
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

    public Button getEndtimeButton(){
        return endTimeButton;
    }

    public Button getStarttimeButton(){
        return startTimeButton;
    }

    public void setActivityItem(ActivityItem activityItem) {
        this.activityItem = activityItem;
    }

    /**
     * returns id of selected activity
     * @return
     */
    public Integer getSelectedItem() {
        return activity;
    }

    public void setDailyRoutineHandler(DailyRoutineHandler drHandler){
        this.drHandler = drHandler;
    }

    public DailyRoutineHandler getDrHandler(){
        return drHandler;
    }
}

