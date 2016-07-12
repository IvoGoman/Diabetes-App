package uni.mannheim.teamproject.diabetesplaner.DataMining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;

/**
 * Created by Ivo on 10.07.2016.
 *
 * Utility Methods for the Process Mining Algorithms
 */
public class ProcessMiningUtill {

    /**
     * @return ID of the current activity
     */
    public static int getCurrentActivityID(){
        DataBaseHandler dbHandler = new DataBaseHandler(AppGlobal.getcontext());
        ActivityItem currentActivity = dbHandler.getCurrentActivity();
        return currentActivity.getActivityId();
    }

    /**
     * Calculates the Average Duration of every Unique Activity kind
     * @return
     */
    public static Map<Integer,Double> getAverageDurationForActivityID(){
        DataBaseHandler dbHandler = new DataBaseHandler(AppGlobal.getcontext());
        Map<Integer,String> activityIds = dbHandler.getAllActionIDsAndTitle(dbHandler);
        Map<Integer, Double> averageDurationPerActivity = new HashMap<>();
        for (int activityId:activityIds.keySet()) {
            ArrayList<ActivityItem> activityItems = dbHandler.getActivitiesById(dbHandler, activityId);
            Double averageDuration = ProcessMiningUtill.getAverageDuration(activityItems);
            averageDurationPerActivity.put(activityId,averageDuration);
        }
        return averageDurationPerActivity;
    }

    /**
     * Calculates the Average Duration of the ActivityItems provided
     * @param activityItems
     * @return Double Value of average Activity Duration in Minutes
     */
    public static Double getAverageDuration(ArrayList<ActivityItem> activityItems){
        int numberOfActivities = activityItems.size();
        double duration = 0.0;
        for (ActivityItem item: activityItems) {
            duration += TimeUtils.getDuration(item.getStarttime(),item.getEndtime());
        }
        duration = duration / numberOfActivities;
        return duration;
    }

    /**
     * Returns the ID of the most frequent Activity that occured at 00:01
     * @return
     */
    public static int getMostFrequentStartActivity(){
        DataBaseHandler dbHandler = new DataBaseHandler(AppGlobal.getcontext());
        ArrayList<ActivityItem> activities = dbHandler.getAllActivities(dbHandler);
        HashMap<Integer,Integer> activityCount = new HashMap<>();
        int currentCount = 0;
        for (ActivityItem item:activities) {
//            TODO: 00:01 might not be the time where the first activity occured, if it is not mainted over night
            if (TimeUtils.dateToTimeString(item.getStarttimeAsString()).equals("00:01")){
                currentCount = activityCount.get(item.getActivityId())+1;
                activityCount.put(item.getActivityId(),currentCount);
            }
        }
        int mostFrequentID = 0;
        int maxCount = 0;
        Set<Integer> ids = activityCount.keySet();
        for (Integer id: ids) {
            currentCount = activityCount.get(id);
            if (currentCount>maxCount){
                maxCount = currentCount;
                mostFrequentID = id;
            }
        }
        return mostFrequentID;
    }

}
