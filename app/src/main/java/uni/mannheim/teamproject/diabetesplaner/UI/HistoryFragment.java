package uni.mannheim.teamproject.diabetesplaner.UI;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Domain.DayHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.MeasureItem;
import uni.mannheim.teamproject.diabetesplaner.R;
import uni.mannheim.teamproject.diabetesplaner.UI.DailyRoutine.DailyRoutineFragment;
import uni.mannheim.teamproject.diabetesplaner.UI.DailyRoutine.DailyRoutineView;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HistoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends DailyRoutineFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "history";
    private static ArrayList<DailyRoutineView> items_history = new ArrayList<>();
    private static LinearLayout linearLayout;
    private Date date;
    private OnFragmentInteractionListener mListener;
    private DayHandler dayHandler;


    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static ArrayList<DailyRoutineView> getItems() {
        return items_history;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate(){
        return this.date;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
        AppCompatActivity aca = (AppCompatActivity) getActivity();
        aca.getSupportActionBar().setTitle(R.string.menu_item_history);
        dayHandler = new DayHandler(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View inflaterView = inflater.inflate(R.layout.fragment_history, container, false);
        linearLayout = (LinearLayout) inflaterView.findViewById(R.id.layout_historic_routine);
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TextView dateView = (TextView) inflaterView.findViewById(R.id.history_date_view);

        String dateString = DateFormat.getDateInstance().format(new Date());

        //TODO: move formatter util class
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date dateToday = sdf.getCalendar().getTime();
        DateFormat df = DateFormat.getDateInstance();
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        date = calendar.getTime();
        dateString = df.format(date);

        dateView.setText(dateString);
        onDateSelected(linearLayout, params, date);
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                DialogFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(fragmentManager, "datePicker");
            }
        });
        ScrollView scrollView = (ScrollView) inflaterView.findViewById(R.id.history_scrollview);
        // Inflate the layout for this fragment
        updateView();
        return inflaterView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public ArrayList<DailyRoutineView> getActivityList() {
        return items_history;
    }

    @Override
    public DayHandler getDrHandler() {
        return dayHandler;
    }

    @Override
    public void updateView() {
        //get predicted routine
        linearLayout.removeAllViews();
        items_history.clear();
        ArrayList<ActivityItem> listItems;
        listItems = dayHandler.getDayRoutine(date);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        DataBaseHandler dbHandler = AppGlobal.getHandler();
        ArrayList<MeasureItem> bsList = dbHandler.getMeasurementValues(date, "DAY", MeasureItem.MEASURE_KIND_BLOODSUGAR);
        ArrayList<MeasureItem> insulinList = dbHandler.getMeasurementValues(date, "DAY", MeasureItem.MEASURE_KIND_INSULIN);
        if (listItems.size()> 0) {
            for (int i = 0; i < listItems.size(); i++) {
                DailyRoutineView drv = new DailyRoutineView(getActivity(), listItems.get(i));
                String bloodsugar = "";
                String insulin = "";
                int numberOfMeasuresWithinOneBS = 0;
                int numberOfMeasuresWithinOneINS = 0;

                for (int j = 0; j < bsList.size(); j++) {
                    MeasureItem bs = bsList.get(j);
                    //checks if time of the bloodsugar measurement is inbetween start and endtime of an activity
                    if (TimeUtils.isTimeInbetween(listItems.get(i).getStarttime(), listItems.get(i).getEndtime(), TimeUtils.getDate(bs.getTimestamp()))) {
                        String name = getResources().getString(R.string.pref_blood_sugar);
                        String at = getResources().getString(R.string.at);

                        if (numberOfMeasuresWithinOneBS == 0) {
                            bloodsugar = name + " " + at + " " + TimeUtils.getTimeInUserFormat(bs.getTimestamp(), getContext()) + ": " + bs.getMeasure_value() + " " + bs.getMeasure_unit();
                            numberOfMeasuresWithinOneBS = 1;
                        } else {
                            bloodsugar += "\n" + name + " " + at + " " + TimeUtils.getTimeInUserFormat(bs.getTimestamp(), getContext()) + ": " + bs.getMeasure_value() + " " + bs.getMeasure_unit();
                        }
                    }
                }

                drv.setBloodsugarText(bloodsugar);

                for (int j = 0; j < insulinList.size(); j++) {
                    MeasureItem ins = insulinList.get(j);
                    //checks if time of the bloodsugar measurement is inbetween start and endtime of an activity
                    if (TimeUtils.isTimeInbetween(listItems.get(i).getStarttime(), listItems.get(i).getEndtime(), TimeUtils.getDate(ins.getTimestamp()))) {
                        String name = getResources().getString(R.string.insulinInput);
                        String at = getResources().getString(R.string.at);

                        if (numberOfMeasuresWithinOneINS == 0) {
                            insulin = name + " " + at + " " + TimeUtils.getTimeInUserFormat(ins.getTimestamp(), getContext()) + ": " + ins.getMeasure_value() + " " + ins.getMeasure_unit();
                            numberOfMeasuresWithinOneINS = 1;
                        } else {
                            insulin += "\n" + name + " " + at + " " + TimeUtils.getTimeInUserFormat(ins.getTimestamp(), getContext()) + ": " + ins.getMeasure_value() + " " + ins.getMeasure_unit();
                        }
                    }
                }

                drv.setInsulinText(insulin);
                linearLayout.addView(drv);
                drv.setState(true);
                drv.setLayoutParams(params);
                items_history.add(drv);
            }
        } else {
            TextView tv = new TextView(getContext());
            tv.setText(R.string.no_data);
            linearLayout.addView(tv);
            tv.setLayoutParams(params);
            tv.setGravity(Gravity.CENTER);
        }

    }

    /**
     * every time a date is chosen with the date picker this method is called to create the
     * new activity list
     *
     * @param linearLayout the layout of the history fragment
     * @param params       the layout parameters
     */
    public void onDateSelected(LinearLayout linearLayout, LinearLayout.LayoutParams params, Date dateSelected) {
        date = dateSelected;
        linearLayout.removeAllViews();
        DailyRoutineView.clearSelectedActivities();
        Log.i(TAG, date.toString());
        DailyRoutineView.clearSelectedActivities();
        DailyRoutineView.setSelectable(false);
        DailyRoutineView.setActionBarItems();
        setDate(date);
        updateView();
    }

    /**
     * @return a random generated list of activities with random start and end time and random activity type
     */
    public ArrayList<String[]> generateRandomRoutine() {
        Random generator = new Random();
        int randomActivity, randomStartMinute, randomStartHour;
        ArrayList<String[]> day = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            randomActivity = generator.nextInt(13) + 1;
            randomStartMinute = generator.nextInt(59) + 1;
            randomStartHour = generator.nextInt(23) + 1;
            day.add(new String[]{"" + randomActivity, randomStartHour + ":" + randomStartMinute, randomStartHour + ":" + randomStartMinute});
        }
        return day;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        linearLayout = null;
        dayHandler.clearDailyRoutine();
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @SuppressLint("ValidFragment")
    class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {


        /**
         * creates a Dialog with a Date Picker with the currently displayed day pre-selected
         */

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            TextView tv = (TextView) this.getActivity().findViewById(R.id.history_date_view);
            Log.i(TAG, "textview:" + tv.getText().toString());
            DateFormat simpleFormat = DateFormat.getDateInstance();
            Date date = null;
            try {
                date = simpleFormat.parse(tv.getText().toString());
                Log.i(TAG, "datefromtv:" + date.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), R.style.picker, this, year, month, day);

        }

        /**
         * Ivo Gosemann
         * get the Date selected in the History Fragment
         */
        public Date getDate() {
            return date;
        }

        /**
         * When the Date for the Fragment is set via the Datepicker Dialog it is
         * checked if it is in the past and then the whole Activity List is updated
         * @param view  the current view
         * @param year  the selected year
         * @param month the selected month
         * @param day   the selected day
         */
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            LinearLayout oLL = (LinearLayout) this.getActivity().findViewById(R.id.layout_historic_routine);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            TextView tv = (TextView) this.getActivity().findViewById(R.id.history_date_view);
            GregorianCalendar calendar = new GregorianCalendar(year, month, day);
            DateFormat simpleDate = DateFormat.getDateInstance();
            DateFormat timeDAte = DateFormat.getTimeInstance();
            Log.i(TAG, timeDAte.format(calendar.getTime()));
            Calendar calendar2 = Calendar.getInstance(Locale.getDefault());
            calendar2.add(Calendar.DAY_OF_MONTH, -1);
            Date dateToday = calendar2.getTime();
            Log.i(TAG, "today:" + dateToday.toString());
            Date dateSelected = calendar.getTime();
            Log.i(TAG, "selected:" + dateSelected.toString());
            if (dateToday.after(dateSelected)) {
                onDateSelected(oLL, params, dateSelected);
                tv.setText(simpleDate.format(calendar.getTime()));
            } else {
                Toast.makeText(getContext(), R.string.date_in_future, Toast.LENGTH_SHORT).show();
            }
        }
    }
}