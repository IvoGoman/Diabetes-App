package uni.mannheim.teamproject.diabetesplaner.UI.DailyRoutine;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import uni.mannheim.teamproject.diabetesplaner.DataMining.PredictionService;
import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Domain.DailyRoutineHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.DayHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.MeasureItem;
import uni.mannheim.teamproject.diabetesplaner.R;
import uni.mannheim.teamproject.diabetesplaner.UI.EntryScreenActivity;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;

/**
 * Created by Stefan
 */

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DailyRoutineFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DailyRoutineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailyRoutineFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String ARG_LIST = "list";
    private ArrayList<String[]> list2 = new ArrayList<String[]>();
    //private ArrayList<ActivityItem> listItems;
    private static ArrayList<DailyRoutineView> items = new ArrayList<DailyRoutineView>();
    private static LinearLayout linearLayout;

    public static final String TAG = DailyRoutineFragment.class.getSimpleName();
    private Date date = TimeUtils.getCurrentDate();
    private Timer timer;
    private TimerTask timerTask;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static Uri imageURI;

    // TODO: Rename and change types of parameters
    private ArrayList<String> arglist;


    private OnFragmentInteractionListener mListener;
    private DailyRoutineView dailyRoutineView;
    private static AppCompatActivity aca;
    private static ScrollView scrollView;
    private DailyRoutineHandler drHandler;
    private Activity parentActivity;
    private IntentFilter mStatusIntentFilter;
    private String nameBS;
    private String at;
    private RelativeLayout progressBar;
    private LinearLayout routineLayout;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DailyRoutineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DailyRoutineFragment newInstance(ArrayList<String> list) {
        DailyRoutineFragment fragment = new DailyRoutineFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_LIST, list);
        fragment.setArguments(args);

        return fragment;
    }

    public DailyRoutineFragment() {
        // Required empty public constructor
    }

    /**
     * Sets the action bar and initializes the DailyRoutineHandler
     *
     * @param savedInstanceState
     * @author Stefan
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            arglist = getArguments().getStringArrayList(ARG_LIST);
        }

        nameBS = getResources().getString(R.string.pref_blood_sugar);
        at = getResources().getString(R.string.at);

        //sets title of the page in the ActionBar
        aca = (AppCompatActivity) getActivity();
        aca.getSupportActionBar().setTitle(R.string.menu_item_daily_routine);

        drHandler = new DailyRoutineHandler(this);
        parentActivity = getActivity();

        // The filter's action is BROADCAST_ACTION
        mStatusIntentFilter = new IntentFilter(
                EntryScreenActivity.BROADCAST_ACTION);
    }

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     * @author Stefan
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View inflaterView = inflater.inflate(R.layout.fragment_daily_routine, container, false);

        //get the layout
        linearLayout = (LinearLayout) inflaterView.findViewById(R.id.layout_daily_routine);
        routineLayout = (LinearLayout) inflaterView.findViewById(R.id.daily_routine_layout);
        progressBar = (RelativeLayout) inflaterView.findViewById(R.id.daily_routine_progress);
        progressBar.setVisibility(View.GONE);

        TextView textView = (TextView) inflaterView.findViewById(R.id.daily_routine_date_view);
        textView.setText(TimeUtils.getDateAsString());
        this.date = TimeUtils.getCurrentDate();


        //create a DailyRoutineView for every list item, so for every activity in the daily routine
//        for(int i=0; i<list2.size(); i++){
//            DailyRoutineView drv = new DailyRoutineView(getActivity(),Integer.valueOf(list2.get(i)[0]),0,list2.get(i)[1], list2.get(i)[2]);
//            linearLayout.addView(drv);
//            drv.setState(false);
//            drv.setLayoutParams(params);
//            items.add(drv);
//        }


        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("PREDICTION_SERVICE_FILE", Context.MODE_PRIVATE);
        String last_prediction = sharedPreferences.getString("LAST_PREDICTION", "0");

        Calendar current = Calendar.getInstance();
        Calendar predicted = Calendar.getInstance();
        long timestamp_current = current.getTimeInMillis();
        long timestamp_predicted = Long.valueOf(last_prediction);
        current.setTimeInMillis(timestamp_current);
        predicted.setTimeInMillis(timestamp_predicted);

//        if (predicted.get(Calendar.DAY_OF_YEAR) < current.get(Calendar.DAY_OF_YEAR) && predicted.get(Calendar.YEAR) <= current.get(Calendar.YEAR)) {


        /*
         * Creates a new Intent to start the RSSPullService
         * IntentService. Passes a URI in the
         * Intent's "data" field.
         */
            Intent mServiceIntent = new Intent(getActivity(), PredictionService.class);
            // Starts the IntentService
            getActivity().startService(mServiceIntent);
            progressBar.setVisibility(View.VISIBLE);
            routineLayout.setVisibility(View.GONE);

        // Instantiates a new ResponseReceiver
            ResponseReceiver mDownloadStateReceiver =
                    new ResponseReceiver();
            // Registers the ResponseReceiver and its intent filters
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                    mDownloadStateReceiver, mStatusIntentFilter);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("LAST_PREDICTION", String.valueOf(current.getTimeInMillis()));
            editor.commit();
//        }


        DailyRoutineView.clearSelectedActivities();
        updateView();


        //get Scrollview
        scrollView = (ScrollView) inflaterView.findViewById(R.id.scroll_view_daily_routine);

        return inflaterView;
    }

    /**
     * updates the View that displays the daily routine
     *
     * @author Stefan
     */
    public void updateView() {
        //get predicted routine
        linearLayout.removeAllViews();
        items.clear();
        //TODO --------- for testing ------------------------------------------
        ArrayList<ActivityItem> listItems = drHandler.getDayRoutine(date);
//        ArrayList<ActivityItem> listItems = new ArrayList<>();
//
//        String start1 = "17.09.2016 00:00:00";
//        String end1 = "17.09.2016 09:00:00";
//        String start2 = "17.09.2016 09:01:00";
//        String end2 = "17.09.2016 14:45:00";
//        String start3 = "17.09.2016 14:46:00";
//        String end3 = "17.09.2016 17:01:00";
//        String start4 = "17.09.2016 17:02:00";
//        String end4 = "17.09.2016 23:59:00";
//
//        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
//        try {
//
//
//            Date s1 = sdf.parse(start1);
//            Date e1 = sdf.parse(end1);
//            Date s2 = sdf.parse(start2);
//            Date e2 = sdf.parse(end2);
//            Date s3 = sdf.parse(start3);
//            Date e3 = sdf.parse(end3);
//            Date s4 = sdf.parse(start4);
//            Date e4 = sdf.parse(end4);
//
//
//            listItems.add(new ActivityItem(1,0,s1,e1));
//            listItems.add(new ActivityItem(10,0,s2,e2));
//            listItems.add(new ActivityItem(2,2,s3,e3));
//            listItems.add(new ActivityItem(1,0,s4,e4));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        Log.d(TAG, "list size after update: " + listItems.size());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        DataBaseHandler dbHandler = AppGlobal.getHandler();
//        dbHandler.InsertBloodsugarEntryScreen(Calendar.getInstance().getTimeInMillis(),1,100,MeasureItem.UNIT_MG);
//        dbHandler.InsertBloodsugarEntryScreen((new Date()).getTime()-(60*1000*150),1,90,MeasureItem.UNIT_MG);

        ArrayList<MeasureItem> bsList = dbHandler.getMeasurementValues(date, "DAY", MeasureItem.MEASURE_KIND_BLOODSUGAR);
        ArrayList<MeasureItem> insulinList = dbHandler.getMeasurementValues(date, "DAY", MeasureItem.MEASURE_KIND_INSULIN);

//        bsList.add(new MeasureItem((new Date()).getTime(), 100, MeasureItem.UNIT_MG));
//        insulinList.add(new MeasureItem((new Date()).getTime(), 100, MeasureItem.UNIT_MG));

        Log.d(TAG, "BS List size: " + bsList.size());
        Log.d(TAG, "insulinList: " + bsList.size());
        for (int i = 0; i < listItems.size(); i++) {
            DailyRoutineView drv = new DailyRoutineView(parentActivity, listItems.get(i));

            //TODO getting the bloodsugar of current activity and set it
            String bloodsugar = "";
            String insulin = "";
            int numberOfMeasuresWithinOneBS = 0;
            int numberOfMeasuresWithinOneINS = 0;

            for (int j = 0; j < bsList.size(); j++) {
                MeasureItem bs = bsList.get(j);
                //checks if time of the bloodsugar measurement is inbetween start and endtime of an activity
                if (TimeUtils.isTimeInbetween(listItems.get(i).getStarttime(), listItems.get(i).getEndtime(), TimeUtils.getDate(bs.getTimestamp()))) {

                    if (numberOfMeasuresWithinOneBS == 0) {
                        bloodsugar = nameBS + " " + at + " " + TimeUtils.getTimeInUserFormat(bs.getTimestamp(), getContext()) + ": " + bs.getMeasure_value() + " " + bs.getMeasure_unit();
                        numberOfMeasuresWithinOneBS = 1;
                    } else {
                        bloodsugar += "\n" + nameBS + " " + at + " " + TimeUtils.getTimeInUserFormat(bs.getTimestamp(), getContext()) + ": " + bs.getMeasure_value() + " " + bs.getMeasure_unit();
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


            //-----for testing--------------
//            if(i==1) {
//                drv.setSubactivity(4);
//            }else if(i==1){
//                drv.setBloodsugarText("4.0");
//               // drv.setSubactivity(5);
//               // drv.setMeal("Kartoffeln mit Speck, Schweinshaxen und Salatbeilage");
//            }

            linearLayout.addView(drv);
            drv.setState(false);
            drv.setLayoutParams(params);
            items.add(drv);
        }
        DailyRoutineView.clearSelectedActivities();
        //DailyRoutineView.getSelectedActivities().clear();
        //DailyRoutineView.setSelectable(false);
        //DailyRoutineView.setActionBarItems();

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * @Override public void onAttach(Activity activity) {
     * super.onAttach(activity);
     * try {
     * mListener = (OnFragmentInteractionListener) activity;
     * } catch (ClassCastException e) {
     * throw new ClassCastException(activity.toString()
     * + " must implement OnFragmentInteractionListener");
     * }
     * }
     **/

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        linearLayout = null;
        drHandler.clearDailyRoutine();
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
        public void onFragmentInteraction(Uri uri);
    }

    /**
     * returns the px value for dp
     *
     * @param dp
     * @return
     * @author Stefan
     */
    public int getpx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    /**
     * return the dp value for px
     *
     * @param px
     * @return
     * @author Stefan
     */
    public int getdp(int px) {
        return (int) (px / getResources().getDisplayMetrics().density);
    }

    /**
     * returns parent activity
     *
     * @return parant activity
     * @author Stefan
     */
    public AppCompatActivity getParentActivity() {
        return aca;
    }

    /**
     * returns the list with all activity views of the dailyroutine
     *
     * @return DailyRoutineView
     * @author Stefan
     */
    public ArrayList<DailyRoutineView> getActivityList() {
        return items;
    }

    /**
     * returns the layout
     *
     * @return LinearLayout
     * @author Stefan
     */
    public static LinearLayout getLinearLayout() {
        return linearLayout;
    }

    /**
     * sets the ic_delete icon in the action bar visible true/false
     *
     * @param isVisible visible/invisible
     * @author Stefan
     */
    public static void setDeleteIconVisible(boolean isVisible) {
        MenuItem deleteItem = EntryScreenActivity.getOptionsMenu().findItem(R.id.delete_icon_action_bar);
        deleteItem.setVisible(isVisible);
    }

    /**
     * sets visibility of add item in the action bar
     *
     * @param isVisible visible/invisible
     * @author Stefan
     */
    public static void setAddItemVisible(boolean isVisible) {
        MenuItem addItem = EntryScreenActivity.getOptionsMenu().findItem(R.id.add_icon_action_bar_routine);
        addItem.setVisible(isVisible);
    }

    /**
     * sets the edit icon in the action bar visible
     *
     * @param isVisible visible/invisible
     * @author Stefan
     */
    public static void setEditIconVisible(boolean isVisible) {
        MenuItem editItem = EntryScreenActivity.getOptionsMenu().findItem(R.id.edit_icon_action_bar_routine);
        editItem.setVisible(isVisible);
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            //timer for updating the actual activity
            //updates every 10 seconds
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    aca.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < items.size(); i++) {
                                items.get(i).setState(false);
                                items.get(i).invalidate();
                            }
                        }
                    });

                }
            };
            timer.schedule(timerTask, 10000, 10000);
        } catch (IllegalStateException e) {
            android.util.Log.e(TAG, "resume error");
        }
    }


    public static ArrayList<DailyRoutineView> getItems() {
        return items;
    }

    /**
     * returns the scrollview
     *
     * @return
     * @author Stefan
     */
    public static ScrollView getScrollView() {
        return scrollView;
    }

    public DayHandler getDrHandler() {
        return drHandler;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult in Fragement");
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate(){
        return this.date;
    }

    public LinearLayout getLayout() {
        return linearLayout;
    }

    private class ResponseReceiver extends BroadcastReceiver {
        // Prevents instantiation
        private ResponseReceiver() {
        }

        // Called when the BroadcastReceiver gets an Intent it's registered to receive
//    @
        public void onReceive(Context context, Intent intent) {
//            if(intent.getStringExtra(EntryScreenActivity.EXTENDED_DATA_STATUS).equals("completed")) {
                if (EntryScreenActivity.getFragment() instanceof DailyRoutineFragment) {
                    DailyRoutineFragment drf = ((DailyRoutineFragment) EntryScreenActivity.getFragment());
                    if (drf.getActivity() instanceof EntryScreenActivity) {
                        drHandler.clearDailyRoutine();
                        drHandler.update();
                        progressBar.setVisibility(View.GONE);
                        routineLayout.setVisibility(View.VISIBLE);
                    }
                }
//            }
        }
    }
}
