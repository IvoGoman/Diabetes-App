package uni.mannheim.teamproject.diabetesplaner.Domain;

import java.util.ArrayList;
import java.util.Date;

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.UI.DailyRoutine.DailyRoutineFragment;
import uni.mannheim.teamproject.diabetesplaner.UI.DailyRoutine.DailyRoutineView;
import uni.mannheim.teamproject.diabetesplaner.UI.EntryScreenActivity;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;

/**
 * Created by Stefan on 24.02.2016.
 */
public class DayHandler {
    private static ArrayList<ActivityItem> dailyRoutine = new ArrayList<>();
    public static final String TAG = DayHandler.class.getSimpleName();
    private DailyRoutineFragment drFragment;


    private Date date;

    public DayHandler(DailyRoutineFragment drFragment) {
        DailyRoutineView.clearSelectedActivities();
        this.drFragment = drFragment;
    }


    /**
     * TODO
     * deletes an activity from the daily routine list
     *
     * @param indexes of activities to be deleted
     * @author Stefan
     */
    public void delete(ArrayList<Integer> indexes) {
        //ic_delete
        AppGlobal.setEditFlag(false);
        for (int i = indexes.size() - 1; i >= 0; i--) {
            dailyRoutine.remove((int) indexes.get(i));
        }

        //adapt times TODO

        for (int i = 0; i < dailyRoutine.size(); i++) {

            if (i == 0 && dailyRoutine.get(i).getStarttime().compareTo(TimeUtils.getTime("00:00")) != 0) {
                //first activity of the day
                dailyRoutine.get(i).setStarttime(TimeUtils.getTime("00:00"));
            } else if (i < dailyRoutine.size() - 1 && dailyRoutine.get(i + 1).getStarttime().compareTo(TimeUtils.addMinuteFromDate(dailyRoutine.get(i).getEndtime(), 1)) != 0) {
                //all activities within first and last activity of the day
                dailyRoutine.get(i + 1).setStarttime(TimeUtils.addMinuteFromDate(dailyRoutine.get(i).getEndtime(), +1));
            } else if (i == dailyRoutine.size() - 1) {
                //last activity of the day
                dailyRoutine.get(i).setEndtime(TimeUtils.getTime("23:59"));
            }
        }


        //handle the actionBar items and selected activities
        DailyRoutineView.clearSelectedActivities();
        DailyRoutineView.setSelectable(false);
        DailyRoutineView.setActionBarItems();

        for (int i = 1; i < dailyRoutine.size(); i++) {
            if (dailyRoutine.get(i-1).getActivityId()==dailyRoutine.get(i).getActivityId()){
                ActivityItem Act = dailyRoutine.get(i-1);
                String Start = dailyRoutine.get(i-1).getStarttimeAsString();
                Date End = dailyRoutine.get(i-1).getEndtime();
                Act.setEndtime(End);
                AppGlobal.getHandler().DeleteActivity(dailyRoutine.get(i).getStarttime().toString(),dailyRoutine.get(i).getEndtime().toString());
                AppGlobal.getHandler().DeleteActivity(dailyRoutine.get(i).getStarttime().toString(),dailyRoutine.get(i).getEndtime().toString());
                AppGlobal.getHandler().InsertActivity(Act);
            }
        }

        drFragment.updateView();
        //TODO combine with backend, adapt the daily routine
    }

    /**
     * updates the daily routine
     * @author Stefan
     */
    public void update() {
        if(EntryScreenActivity.getOptionsMenu() != null) {
            DailyRoutineView.clearSelectedActivities();
            DailyRoutineView.setSelectable(false);
            DailyRoutineView.setActionBarItems();
        }
        clearDailyRoutine();
        getDayRoutine(date);

        drFragment.updateView();
    }

    /**
     * TODO
     * Edits one item of the daily routine.
     * First removes the item to edit, then adds new one
     *
     * @param indexSelected in the list
     * @param activityItem
     * @author Stefan
     */
    public void edit(int indexSelected, ActivityItem activityItem) {
        //TODO edit the daily routine
        AppGlobal.setEditFlag(true);
        ArrayList<Integer> selected = new ArrayList<Integer>();
        selected.add(indexSelected);
        delete(selected);
        add(activityItem);
        for (int i = 1; i < dailyRoutine.size(); i++) {
            if (dailyRoutine.get(i-1).getActivityId()==dailyRoutine.get(i).getActivityId()){
                ActivityItem Act = dailyRoutine.get(i-1);
                String Start = dailyRoutine.get(i-1).getStarttimeAsString();
                Date End = dailyRoutine.get(i-1).getEndtime();
                Act.setEndtime(End);
                AppGlobal.getHandler().DeleteActivity(dailyRoutine.get(i).getStarttime().toString(),dailyRoutine.get(i).getEndtime().toString());
                AppGlobal.getHandler().DeleteActivity(dailyRoutine.get(i).getStarttime().toString(),dailyRoutine.get(i).getEndtime().toString());
                AppGlobal.getHandler().InsertActivity(Act);
            }
        }
    }

    /**
     * adds an activity item to the daily routine
     * TODO: synchronize with database
     *
     * @param activityItem
     * @author Stefan
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

        //added activity is during one single activity & the activities are not the same
        if (startindex == endindex && dailyRoutine.get(startindex).getActivityId() != activityItem.getActivityId()) {
            if ((dailyRoutine.get(startindex).getStarttime().compareTo(activityItem.getStarttime()) == 0) && (dailyRoutine.get(startindex).getEndtime().compareTo(activityItem.getEndtime()) == 0)) {
                dailyRoutine.remove(startindex);
                dailyRoutine.add(startindex, activityItem);
            } else {
                ActivityItem prev = new ActivityItem(dailyRoutine.get(startindex));
                ActivityItem next = new ActivityItem(dailyRoutine.get(startindex));

                dailyRoutine.remove(startindex);

                prev.setEndtime(TimeUtils.addMinuteFromDate(start, -1));
                next.setStarttime(TimeUtils.addMinuteFromDate(end, 1));

                //order vice verse!
                dailyRoutine.add(startindex, next);
                dailyRoutine.add(startindex, activityItem);
                dailyRoutine.add(startindex, prev);
            }


        } else {

            //set endtime of previous activity
            if (startindex >= 0) {
                ActivityItem itemStart = dailyRoutine.get(startindex);
                itemStart.setEndtime(TimeUtils.addMinuteFromDate(start, -1));
            }

            //set starttime of next activity
            if (endindex < dailyRoutine.size()) {
                ActivityItem itemEnd = dailyRoutine.get(endindex);
                itemEnd.setStarttime(TimeUtils.addMinuteFromDate(end, 1));
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
        for (int i = 1; i < dailyRoutine.size(); i++) {
            if (dailyRoutine.get(i-1).getActivityId()==dailyRoutine.get(i).getActivityId()){
                ActivityItem Act = dailyRoutine.get(i-1);
                String Start = dailyRoutine.get(i-1).getStarttimeAsString();
                Date End = dailyRoutine.get(i-1).getEndtime();
                Act.setEndtime(End);
                AppGlobal.getHandler().DeleteActivity(dailyRoutine.get(i).getStarttime().toString(),dailyRoutine.get(i).getEndtime().toString());
                AppGlobal.getHandler().DeleteActivity(dailyRoutine.get(i).getStarttime().toString(),dailyRoutine.get(i).getEndtime().toString());
                AppGlobal.getHandler().InsertActivity(Act);
            }
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
    public static ArrayList<ActivityItem> getDailyRoutine() {
        return dailyRoutine;
    }

    public ArrayList<ActivityItem> getDayRoutine(Date date) {
        DataBaseHandler handler = AppGlobal.getHandler();
        this.date = date;
        dailyRoutine = handler.GetDay(date);
        return dailyRoutine;
    }

    public void clearDailyRoutine() {
        dailyRoutine.clear();
    }

    public DailyRoutineFragment getDailyRoutineFragment(){
        return drFragment;
    }
}
