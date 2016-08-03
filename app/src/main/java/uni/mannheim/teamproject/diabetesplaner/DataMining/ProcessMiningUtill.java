package uni.mannheim.teamproject.diabetesplaner.DataMining;


import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;

/**
 * Created by Ivo on 10.07.2016.
 * <p/>
 * Utility Methods for the Process Mining Algorithms
 */
public class ProcessMiningUtill {

    /**
     * @return ID of the current activity
     */
    public static int getCurrentActivityID() {
        DataBaseHandler dbHandler = AppGlobal.getHandler();
        ActivityItem currentActivity = dbHandler.getCurrentActivity();
        return currentActivity.getActivityId();
    }

    /**
     * Calculates the Average Duration of every unique activity contained in the eventList
     *
     * @return
     */
    public static Map<Integer, Double> getAverageDurations(ArrayList<String[]> eventList) {
        Map<Integer, Integer> activityCount = new HashMap<>();
        Map<Integer, Double> activityDuration = new HashMap<>();
        Map<Integer, Double> activityAvgDuration = new HashMap<>();
        int id, count;
        double duration, average;
        Date startTime, endTime;
        for (String[] event : eventList) {
            id = Integer.valueOf(event[1]);
            startTime = TimeUtils.getDate(event[2]);
            endTime = TimeUtils.getDate(event[3]);
            duration = TimeUtils.getDuration(startTime, endTime);
            if (activityCount.containsKey(id) && activityDuration.containsKey(id)) {
                activityCount.put(id, activityCount.get(id) + 1);
                activityDuration.put(id, activityDuration.get(id) + duration);
            } else {
                activityCount.put(id, 1);
                activityDuration.put(id, duration);

            }
        }
        for (int activityId : activityCount.keySet()) {
            count = activityCount.get(activityId);
            duration = activityDuration.get(activityId);
            average = duration / count;
            activityAvgDuration.put(activityId, average);
        }
        return activityAvgDuration;
    }

    /**
     * Returns the ID of the most frequent Activity that occured at 00:01
     *
     * @return
     */
    public static int getMostFrequentStartActivity() {
        DataBaseHandler dbHandler = AppGlobal.getHandler();
        ArrayList<ActivityItem> activities = dbHandler.getAllActivities(dbHandler);
        HashMap<Integer, Integer> activityCount = new HashMap<>();
        int currentCount = 0;
        for (ActivityItem item : activities) {
//            TODO: 00:01 might not be the time where the first activity occured, if it is not mainted over night
            if (TimeUtils.dateToTimeString(item.getStarttimeAsString()).equals("00:01")) {
                if (activityCount.containsKey(item.getActivityId())) {
                    currentCount = activityCount.get(item.getActivityId()) + 1;
                    activityCount.put(item.getActivityId(), currentCount);
                } else {
                    activityCount.put(item.getActivityId(), 1);
                }
            }
        }
        int mostFrequentID = 0;
        int maxCount = 0;
        Set<Integer> ids = activityCount.keySet();
        for (Integer id : ids) {
            currentCount = activityCount.get(id);
            if (currentCount > maxCount) {
                maxCount = currentCount;
                mostFrequentID = id;
            }
        }
        return mostFrequentID;
    }

    /**
     * Calculate the most frequent start activity of a day based on the cases
     * Returns the activity which was most often the first in each case
     *
     * @param cases Output of the CaseCreator
     * @return
     */
    public static int getMostFrequentStartActivity(ArrayList<String[]> cases) {
        HashMap<Integer, Integer> activityCount = new HashMap<>();
        int currentCase = 0, currentActivity = 0, currentCount = 0;
        String[] caseArray;
        for (int i = 0; i < cases.size(); i++) {
            caseArray = cases.get(i);
            if (Integer.valueOf(caseArray[0]) != currentCase | i == cases.size() - 1) {
                currentCase = Integer.valueOf(caseArray[0]);
                currentActivity = Integer.valueOf(caseArray[2]);
                if (activityCount.containsKey(currentActivity)) {
                    currentCount = activityCount.get(currentActivity) + 1;
                    activityCount.put(currentActivity, currentCount);
                } else {
                    activityCount.put(currentActivity, 1);
                }
            }
        }
        int mostFrequentID = 0, maxCount = 0;
        for (Entry<Integer, Integer> entry : activityCount.entrySet()) {
            currentCount = entry.getValue();
            if (maxCount < currentCount) {
                maxCount = currentCount;
                mostFrequentID = entry.getKey();
            }
        }
        return mostFrequentID;
    }

    /**
     * Calculate the most frequent end activity of a day based on the cases
     * Returns the activity which was most often the first in each case
     *
     * @param cases Output of the CaseCreator
     * @return
     */
    public static int getMostFrequentEndActivity(ArrayList<String[]> cases) {
        HashMap<Integer, Integer> activityCount = new HashMap<>();
//       Initial currentCase is 1 so the change between the cases is registered
        int currentCaseKey = 1, currentActivityKey = 0, currentActivityCount = 0;
        String[] predecessorCaseValues = null;
//        iterate throught all cases
        for (String[] caseArray : cases) {
//            is the case of the current caseArray the same as the overall currentCaseKey?
            if (Integer.valueOf(caseArray[0]) != currentCaseKey) {
//                if so then set the currentCaseKey to the new current
                currentCaseKey = Integer.valueOf(caseArray[0]);
//                and set the currentActivity to that of the predecessor , hence the last activity of the old case
                currentActivityKey = Integer.valueOf(predecessorCaseValues[2]);
                if (activityCount.containsKey(currentActivityKey)) {
                    currentActivityCount = activityCount.get(currentActivityKey) + 1;
                    activityCount.put(currentActivityKey, currentActivityCount);
                } else {
                    activityCount.put(currentActivityKey, 1);
                }
            }
//            reset the predecessor values to the current values
            predecessorCaseValues = caseArray;
        }
        int mostFrequentID = 0, maxCount = 0;
        for (Entry<Integer, Integer> entry : activityCount.entrySet()) {
            currentActivityCount = entry.getValue();
            if (maxCount < currentActivityCount) {
                maxCount = currentActivityCount;
                mostFrequentID = entry.getKey();
            }
        }
        return mostFrequentID;
    }

    /**
     * Calculating the sum of the duration of all activities in the ArrayList
     * If the sum is below 24*60 Minutes a full day is not yet reached
     *
     * @param idDurationMap
     * @return
     */
    public static boolean getTotalDuration(List<Pair<Integer, Double>> idDurationMap) {
        Double total = 0.0;
        Boolean result = false;
        for (Pair<Integer, Double> pair : idDurationMap) {
            total += pair.second;
        }
        if (total <= 1440) {
            result = true;
        }
        return result;
    }
}
