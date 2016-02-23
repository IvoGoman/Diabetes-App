package uni.mannheim.teamproject.diabetesplaner.Backend;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import uni.mannheim.teamproject.diabetesplaner.DataMining.Util;

/**
 * Created by Stefan on 22.02.2016.
 */
public class DailyRoutineHandler {
    private static ArrayList<ActivityItem> dailyRoutine = new ArrayList<>();
    public static final String TAG = DailyRoutineHandler.class.getSimpleName();

    /**
     * TODO
     * returns the predicted DailyRoutine from the model
     * @return
     */
    public static void predictDailyRoutine(){
        //TODO get daily routine from model
        dailyRoutine.add(new ActivityItem(1,0,"0:00","9:14"));
        dailyRoutine.add(new ActivityItem(2,0,"9:14","9:53"));
        dailyRoutine.add(new ActivityItem(13,0,"9:53","13:07"));
        dailyRoutine.add(new ActivityItem(2,0,"13:07","13:22"));
        dailyRoutine.add(new ActivityItem(13,0, "13:22", "15:35"));
        dailyRoutine.add(new ActivityItem(10,0, "15:35", "15:38"));
        dailyRoutine.add(new ActivityItem(13,0, "15:38", "21:53"));
        dailyRoutine.add(new ActivityItem(5,0,"21:53","22:22"));
        dailyRoutine.add(new ActivityItem(2,0,"22:22","22:51"));
        dailyRoutine.add(new ActivityItem(1,0, "22:51", "23:59"));
    }

    /**
     * TODO
     * deletes an activity from the daily routine list
     * @param indexes of activities to be deleted
     */
    public static void delete(ArrayList<Integer> indexes){
        //delete
        for(int i=indexes.size()-1; i>=0;i--){
            dailyRoutine.remove(i);
        }

        //adapt times
        for(int i=0; i<dailyRoutine.size(); i++){
            //first item
            if(i==0 && dailyRoutine.get(i).getStarttime().compareTo(Util.getTime("00:00"))!=1){
                dailyRoutine.get(i).setStarttime(Util.getTime("00:00"));
            }else if(i < dailyRoutine.size()-1 && dailyRoutine.get(i+1).getStarttime().compareTo(Util.addMinuteFromDate(dailyRoutine.get(i).getEndtime(), 1))!=1){
                //all items in between

                dailyRoutine.get(i+1).setStarttime(Util.addMinuteFromDate(dailyRoutine.get(i).getEndtime(), +1));
            }else if(i == dailyRoutine.size()-1){
                //last item
                dailyRoutine.get(i).setStarttime(Util.getTime("23:59"));
            }
        }

        //TODO combine with backend, adapt the daily routine
    }


    /**
     * TODO
     * Edits one item of the daily routine
     * @param index in the list
     * @param activityItem
     */
    public static void edit(int index, ActivityItem activityItem){
        //TODO edit the daily routine
    }

    /**
     * adds an activity item to the daily routine
     * TODO: synchronize with database
     * @param activityItem
     */
    public static void add(ActivityItem activityItem){
        Date start = activityItem.getStarttime();
        Date end = activityItem.getEndtime();

        int startindex=0;
        int endindex=0;
        for(int i=0; i<dailyRoutine.size(); i++){

            Date startItem = dailyRoutine.get(i).getStarttime();
            Date endItem = dailyRoutine.get(i).getEndtime();

            if(startItem.before(start) && endItem.after(start)){
                startindex = i;
            }
            if(startItem.before(end) && endItem.after(end)){
                endindex = i;
            }
        }

        //set endtime of previous activity
        ActivityItem itemStart = dailyRoutine.get(startindex);
        itemStart.setEndtime(Util.addMinuteFromDate(start, -1));

        //set starttime of next activity
        ActivityItem itemEnd = dailyRoutine.get(endindex);
        itemStart.setStarttime(Util.addMinuteFromDate(end, 1));

        //remove items in between
        if(endindex-startindex>1){
            for(int i=startindex+1; i<endindex;i++){
                dailyRoutine.remove(i);
            }
        }

        //add activity
        dailyRoutine.add(startindex+1, activityItem);
    }

    /**
     * returns the list with the daily routine
     * @return
     */
    public static ArrayList<ActivityItem> getDailyRoutine(){


        return dailyRoutine;
    }

    /**
     * returns the daily routine as arraylist
     * @return
     */
    public static ArrayList<String[]> getDailyRoutineAsList(){
        //TODO only hardcoded yet
        ArrayList<String[]> list2 = new ArrayList<>();

        for(int i=0; i<dailyRoutine.size(); i++){
            ActivityItem it = dailyRoutine.get(i);
            Log.d(TAG, it.getEndtimeAsString());
            list2.add(new String[]{String.valueOf(it.getActivityId()),it.getStarttimeAsString(),it.getEndtimeAsString()});
        }

        return list2;
    }
}
