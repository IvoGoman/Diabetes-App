package uni.mannheim.teamproject.diabetesplaner.UI.DailyRoutine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Domain.DailyRoutineHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.DayHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.MeasureItem;
import uni.mannheim.teamproject.diabetesplaner.UI.EntryScreenActivity;
import uni.mannheim.teamproject.diabetesplaner.R;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.Util;

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
    private Date date;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            arglist = getArguments().getStringArrayList(ARG_LIST);
        }

        //sets title of the page in the ActionBar
        aca = (AppCompatActivity) getActivity();
        aca.getSupportActionBar().setTitle(R.string.menu_item_daily_routine);

        drHandler = new DailyRoutineHandler(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View inflaterView = inflater.inflate(R.layout.fragment_daily_routine, container, false);

        //get the layout
        linearLayout = (LinearLayout) inflaterView.findViewById(R.id.layout_daily_routine);

        TextView textView = (TextView) inflaterView.findViewById(R.id.daily_routine_date_view);
        textView.setText(getDate());

        //create a DailyRoutineView for every list item, so for every activity in the daily routine
//        for(int i=0; i<list2.size(); i++){
//            DailyRoutineView drv = new DailyRoutineView(getActivity(),Integer.valueOf(list2.get(i)[0]),0,list2.get(i)[1], list2.get(i)[2]);
//            linearLayout.addView(drv);
//            drv.setState(false);
//            drv.setLayoutParams(params);
//            items.add(drv);
//        }

        drHandler.predictDailyRoutine(this.date);
        DailyRoutineView.clearSelectedActivities();
        updateView();


        //get Scrollview
        scrollView = (ScrollView) inflaterView.findViewById(R.id.scroll_view_daily_routine);

        return inflaterView;
    }

    /**
     * updates View
     */
    public void updateView(){
        //get predicted routine
        linearLayout.removeAllViews();
        items.clear();
        ArrayList<ActivityItem> listItems = new ArrayList<>();
        listItems = drHandler.getDailyRoutine();
        Log.d(TAG, "list size after update: " +listItems.size());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        DataBaseHandler dbHandler = AppGlobal.getHandler();
//        ArrayList<Integer> bsList = dbHandler.getAllBloodSugar(dbHandler, Util.getCurrentDate(),"WEEK");
        ArrayList<MeasureItem> bsList = dbHandler.getMeasurementValues(dbHandler,Util.getCurrentDate(),"WEEK","bloodsugar");
        for(int i=0; i<listItems.size(); i++){
            DailyRoutineView drv = new DailyRoutineView(getActivity(), listItems.get(i));

            //TODO getting the bloodsugar of current activity and set it
//            String bloodsugar = "";
//            int numberOfMeasuresWithinOne = 0;
//            for(int j=0; j<bsList.size(); j++){
//                if(Util.isTimeInbetween(listItems.get(i).getStarttime(), listItems.get(i).getStarttime(), dateOfBloodsugarMeasure){
//                    String name = getResources().getString(R.string.pref_blood_sugar);
//                    String at = getResources().getString(R.string.at);
//                    if(numberOfMeasuresWithinOne == 0) {
//                        bloodsugar = name + " " + at + " " timeOfBloodsugarMeasure + ": " + bloodsugarlevel + " " + measurementUnit;
//                        numberOfMeasuresWithinOne = 1;
//                    }else{
//                        bloodsugar += "\n"+ name + " " + at + " " timeOfBloodsugarMeasure + ": " + bloodsugarlevel + " " + measurementUnit;
//                    }
//                }
//            }
//
//            drv.setBloodsugar(bloodsugar);


            //-----for testing--------------
            if(i==1) {
                drv.setSubactivity(4);
            }else if(i==1){
                drv.setBloodsugar("4.0");
               // drv.setSubactivity(5);
               // drv.setMeal("Kartoffeln mit Speck, Schweinshaxen und Salatbeilage");
            }

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




    /**
     * Returns the date adapted to the phones date format
     * @return date as String
     */
    public String getDate(){
        Date date = Calendar.getInstance(Locale.getDefault()).getTime();
        this.date = date;
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd.MM.yyyy");
        String dateString = dateFormat.format(date);

        return dateString;
    }

    /**
     * get the day of the week
     * @param dayOfWeek integer value
     * @return day of the week as String
     */
    public String getDayOfWeek(int dayOfWeek){
        switch (dayOfWeek){
            case 1: return getString(R.string.Sunday);
            case 2: return getString(R.string.Monday);
            case 3: return getString(R.string.Tuesday);
            case 4: return getString(R.string.Wednesday);
            case 5: return getString(R.string.Thursday);
            case 6: return getString(R.string.Friday);
            case 7: return getString(R.string.Saturday);
            default: return "";
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }**/

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
     * @param dp
     * @return
     */
    public int getpx(int dp){
        return (int)(dp*getResources().getDisplayMetrics().density);
    }

    /**
     * return the dp value for px
     * @param px
     * @return
     */
    public int getdp(int px){
        return (int)(px/getResources().getDisplayMetrics().density);
    }

    /**
     * returns parent activity
     * @return parant activity
     */
    public AppCompatActivity getParentActivity(){
        return aca;
    }

    /**
     * returns the list with all activity views of the dailyroutine
     * @return DailyRoutineView
     */
    public ArrayList<DailyRoutineView> getActivityList(){
        return items;
    }

    /**
     * returns the layout
     * @return LinearLayout
     */
    public static LinearLayout getLinearLayout(){
        return linearLayout;
    }

    /**
     * sets the delete icon in the action bar visible true/false
     * @param isVisible visible/invisible
     */
    public static void setDeleteIconVisible(boolean isVisible){
        MenuItem deleteItem = EntryScreenActivity.getOptionsMenu().findItem(R.id.delete_icon_action_bar);
        deleteItem.setVisible(isVisible);
    }

    /**
     * sets visibility of add item in the action bar
     * @param isVisible visible/invisible
     */
    public static void setAddItemVisible(boolean isVisible){
        MenuItem addItem = EntryScreenActivity.getOptionsMenu().findItem(R.id.add_icon_action_bar_routine);
        addItem.setVisible(isVisible);
    }

    /**
     * sets the edit icon in the action bar visible
     * @param isVisible visible/invisible
     */
    public static void setEditIconVisible(boolean isVisible){
        MenuItem editItem = EntryScreenActivity.getOptionsMenu().findItem(R.id.edit_icon_action_bar_routine);
        editItem.setVisible(isVisible);
    }

    @Override
    public void onPause(){
        super.onPause();
        timer.cancel();
    }

    @Override
    public void onResume(){
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
        } catch (IllegalStateException e){
            android.util.Log.e(TAG, "resume error");
        }
    }



    public static ArrayList<DailyRoutineView> getItems(){
        return items;
    }

    /**
     * returns the scrollview
     * @return
     */
    public static ScrollView getScrollView(){
        return scrollView;
    }

    public DayHandler getDrHandler(){
        return drHandler;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult in Fragement");
    }
}
