package uni.mannheim.teamproject.diabetesplaner;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
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

import uni.mannheim.teamproject.diabetesplaner.DailyRoutine.DailyRoutineView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HistoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private AppCompatActivity aca;
    private ArrayList<DailyRoutineView> items = new ArrayList<DailyRoutineView>();

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

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        aca = (AppCompatActivity) getActivity();
        aca.getSupportActionBar().setTitle(R.string.menu_item_history);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View inflaterView = inflater.inflate(R.layout.fragment_history, container, false);
        final LinearLayout linearLayout = (LinearLayout) inflaterView.findViewById(R.id.layout_historic_routine);
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TextView dateView = (TextView) inflaterView.findViewById(R.id.history_date_view);
       // String dateString = DateFormat.getDateInstance().format(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date date = Calendar.getInstance(Locale.getDefault()).getTime();
      String dateString = sdf.format(date);
    //    String dateString = DateFormat.getDateInstance().format(date);
        dateView.setText(dateString);
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                DialogFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(fragmentManager, "datePicker");
            }
        });
        onDateSelected(linearLayout, params);
        //TODO: add history item at the point where a daily routine is completed


        // Inflate the layout for this fragment
        return inflaterView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * every time a date is chosen with the date picker this method is called to create the
     * new activity list
     *
     * @param linearLayout the layout of the history fragment
     * @param params       the layout parameters
     */
    public void onDateSelected(LinearLayout linearLayout, LinearLayout.LayoutParams params) {
        linearLayout.removeAllViews();
        ArrayList<String[]> day = generateRandomRoutine();
        for (int i = 0; i < day.size(); i++) {
            DailyRoutineView drv = new DailyRoutineView(getActivity(), Integer.valueOf(day.get(i)[0]), 0, day.get(i)[1], day.get(i)[2]);
            drv.setState(true);
            linearLayout.addView(drv);
            drv.setLayoutParams(params);
            //drv.getLayoutParams().height = drv.getTotalHeight();
            items.add(drv);
        }
    }

    /**
     * @return a random generated list of activites with random start and end time and random activity type
     */
    public ArrayList<String[]> generateRandomRoutine() {
        Random generator = new Random();
        int randomActivity, randomStartMinute, randomStartHour;
        ArrayList<String[]> day = new ArrayList<String[]>();
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
         * creates a Dialog with a Date Picker with the currently displayed day presselected
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            TextView tv = (TextView) this.getActivity().findViewById(R.id.history_date_view);
            SimpleDateFormat simpleFormat = new SimpleDateFormat("dd.MM.yyyy",Locale.getDefault());
            Date date = null;
            try {
                date = simpleFormat.parse(tv.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        /**
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
            SimpleDateFormat simpleDate = new SimpleDateFormat();
            simpleDate.applyPattern("dd.MM.yyyy");
            Date dateToday = Calendar.getInstance(Locale.getDefault()).getTime();
            Date dateSelected = calendar.getTime();
            if (dateToday.after(dateSelected)) {
                onDateSelected(oLL, params);
                tv.setText(simpleDate.format(calendar.getTime()));
            }else{
                Toast.makeText(getContext(), R.string.date_in_future, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
