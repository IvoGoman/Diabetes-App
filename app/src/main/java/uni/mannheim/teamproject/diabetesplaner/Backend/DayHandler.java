package uni.mannheim.teamproject.diabetesplaner.Backend;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import uni.mannheim.teamproject.diabetesplaner.DailyRoutine.DailyRoutineFragment;
import uni.mannheim.teamproject.diabetesplaner.DailyRoutine.DailyRoutineView;
import uni.mannheim.teamproject.diabetesplaner.DataMining.Util;

/**
 * Created by Stefan on 24.02.2016.
 */
public class DayHandler {
    private ArrayList<ActivityItem> dailyRoutine = new ArrayList<>();
    public static final String TAG = DayHandler.class.getSimpleName();
    private DailyRoutineFragment drFragment;


    private Date date;

    public DayHandler(DailyRoutineFragment drFragment) {
        this.drFragment = drFragment;
    }


    /**
     * TODO
     * deletes an activity from the daily routine list
     *
     * @param indexes of activities to be deleted
     */
    public void delete(ArrayList<Integer> indexes) {
        //delete
        for (int i = indexes.size() - 1; i >= 0; i--) {
            dailyRoutine.remove((int) indexes.get(i));
        }

        //adapt times TODO
        for (int i = 0; i < dailyRoutine.size(); i++) {
            if (i == 0 && dailyRoutine.get(i).getStarttime().compareTo(Util.getTime("00:00")) != 0) {
                //first activity of the day
                dailyRoutine.get(i).setStarttime(Util.getTime("00:00"));
            } else if (i < dailyRoutine.size() - 1 && dailyRoutine.get(i + 1).getStarttime().compareTo(Util.addMinuteFromDate(dailyRoutine.get(i).getEndtime(), 1)) != 0) {
                //all activities within first and last activity of the day
                dailyRoutine.get(i + 1).setStarttime(Util.addMinuteFromDate(dailyRoutine.get(i).getEndtime(), +1));
            } else if (i == dailyRoutine.size() - 1) {
                //last activity of the day
                dailyRoutine.get(i).setEndtime(Util.getTime("23:59"));
            }
        }


        //handle the actionBar items and selected activities
        DailyRoutineView.clearSelectedActivities();
        DailyRoutineView.setSelectable(false);
        DailyRoutineView.setActionBarItems();

        drFragment.updateView();
        //TODO combine with backend, adapt the daily routine
    }


    /**
     * TODO
     * Edits one item of the daily routine.
     * First removes the item to edit, then adds new one
     *
     * @param indexSelected in the list
     * @param activityItem
     */
    public void edit(int indexSelected, ActivityItem activityItem) {
        //TODO edit the daily routine
        ArrayList<Integer> selected = new ArrayList<Integer>();
        selected.add(indexSelected);
        delete(selected);
        add(activityItem);
    }

    /**
     * adds an activity item to the daily routine
     * TODO: synchronize with database
     *
     * @param activityItem
     */
    public void add(ActivityItem activityItem) {
        //get the start and endtime of the new activity
        Date start = activityItem.getStarttime();
        Date startItem = null;
        Date end = activityItem.getEndtime();
        Date endItem = null;
        //find the index of the item before and after the position where the item should be inserted
        int startindex = 0;
        int endindex = 0;
        for (int i = 0; i < dailyRoutine.size(); i++) {

            //get the start and end time of item i
            startItem = dailyRoutine.get(i).getStarttime();
            endItem = dailyRoutine.get(i).getEndtime();
            startItem.compareTo(start);
            //check if starttime of item to add is during time of item i
            if (startItem.compareTo(start) < 0 && endItem.compareTo(start) >= 0) {
                startindex = i;

            } else if (startItem.compareTo(start) == 0) {
                startindex = i - 1;
            }

            //check if endtime of item to add is during time of item i
            if (startItem.compareTo(end) <= 0 && endItem.compareTo(end) > 0) {
                endindex = i;
            } else if (endItem.compareTo(end) == 0) {
                endindex = i + 1;
            }
        }

        //added activity is during one single activity
        if (startindex == endindex) {
            ActivityItem prev = new ActivityItem(dailyRoutine.get(startindex));
            ActivityItem next = new ActivityItem(dailyRoutine.get(startindex));

            dailyRoutine.remove(startindex);

            prev.setEndtime(Util.addMinuteFromDate(start, -1));
            next.setStarttime(Util.addMinuteFromDate(end, 1));

            //order vice verse!
            dailyRoutine.add(startindex, next);
            dailyRoutine.add(startindex, activityItem);
            dailyRoutine.add(startindex, prev);


        } else {

            //set endtime of previous activity
            if (startindex >= 0) {
                ActivityItem itemStart = dailyRoutine.get(startindex);
                itemStart.setEndtime(Util.addMinuteFromDate(start, -1));
            }

            //set starttime of next activity
            if (endindex < dailyRoutine.size()) {
                ActivityItem itemEnd = dailyRoutine.get(endindex);
                itemEnd.setStarttime(Util.addMinuteFromDate(end, 1));
            }

            //remove items in between
            if (endindex - startindex > 1) {
                for (int i = endindex - 1; i > startindex; i--) {
                    dailyRoutine.remove(i);
                }
            }

            //add activity
            dailyRoutine.add(startindex + 1, activityItem);
        }
        drFragment.updateView();
    }

    /**
     * Ivo Gosemann 05.03.2016
     *
     * @return Date of the Day
     */

    public Date getDate() {
        return date;
    }

    /**
     * Ivo Gosemann 05.03.2016
     *
     * @params Date of the Day
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * returns the list with the daily routine
     *
     * @return
     */
    public ArrayList<ActivityItem> getDailyRoutine() {


        return dailyRoutine;
    }

    public ArrayList<ActivityItem> getDayRoutine(Date date) {
        DataBaseHandler handler = AppGlobal.getHandler();
        this.date = date;
        dailyRoutine = handler.GetDay(handler, date);
        return dailyRoutine;
    }

    /**
     * returns the daily routine as arraylist
     *
     * @return
     */
    public ArrayList<String[]> getDailyRoutineAsList() {
        //TODO only hardcoded yet
        ArrayList<String[]> list2 = new ArrayList<>();

        for (int i = 0; i < dailyRoutine.size(); i++) {
            ActivityItem it = dailyRoutine.get(i);
            Log.d(TAG, it.getEndtimeAsString());
            list2.add(new String[]{String.valueOf(it.getActivityId()), it.getStarttimeAsString(), it.getEndtimeAsString()});
        }

        return list2;
    }

    public void clearDailyRoutine() {
        dailyRoutine.clear();
    }

    public void setDailyRoutine(ArrayList<ActivityItem> dailyRoutine) {
        this.dailyRoutine = dailyRoutine;
    }
}
