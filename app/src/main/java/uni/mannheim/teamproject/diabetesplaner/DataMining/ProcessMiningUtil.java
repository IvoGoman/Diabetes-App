package uni.mannheim.teamproject.diabetesplaner.DataMining;


import android.support.v4.util.Pair;

import java.sql.Time;
import java.text.SimpleDateFormat;
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
 * Utility Methods for the Process Mining Algorithms
 */
public class ProcessMiningUtil {

    /**
     * @return ID of the current activity
     */
    public static int getCurrentActivityID() {
        DataBaseHandler dbHandler = AppGlobal.getHandler();
        return dbHandler.getCurrentActivity();
    }

    /**
     * Calculates the Average Duration of every unique activity contained in the eventList
     *
     * @return Map of ActivityIDs and Average Duration in minutes
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
            duration = TimeUtils.getDurationMinutes(startTime, endTime);
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
     * Calculates the average % for the day of every unique activity
     *
     * @return Map of ActivityIDs and Average Duration in %
     */
    public static Map<Integer, Double> getAveragePercentualDurations(ArrayList<String[]> eventList) {
        Map<Integer, Integer> activityCount = new HashMap<>();
        Map<Integer, Double> activityPercentage = new HashMap<>();
        Map<Integer, Double> activityAvgPercentage = new HashMap<>();
        int id, count;
        double duration, average, percentage;
        Date startTime, endTime;
        for (String[] event : eventList) {
            id = Integer.valueOf(event[1]);
            startTime = TimeUtils.getDate(event[2]);
            endTime = TimeUtils.getDate(event[3]);
            duration = TimeUtils.getDurationMinutes(startTime, endTime);
            percentage = duration / 1440 * 100;
            if (activityCount.containsKey(id) && activityPercentage.containsKey(id)) {
                activityCount.put(id, activityCount.get(id) + 1);
                activityPercentage.put(id, activityPercentage.get(id) + percentage);
            } else {
                activityCount.put(id, 1);
                activityPercentage.put(id, percentage);

            }
        }
        for (int activityId : activityCount.keySet()) {
            count = activityCount.get(activityId);
            duration = activityPercentage.get(activityId);
            average = duration / count;
            activityAvgPercentage.put(activityId, average);
        }
        return activityAvgPercentage;
    }

    /**
     * Returns the ID of the most frequent Activity that occurred at 00:01
     *
     * @return activity ID of the most frequent start activity
     */
    public static int getMostFrequentStartActivity() {
        DataBaseHandler dbHandler = AppGlobal.getHandler();
        ArrayList<ActivityItem> activities = dbHandler.getAllActivities();
        HashMap<Integer, Integer> activityCount = new HashMap<>();
        int currentCount;
        for (ActivityItem item : activities) {
//            TODO: 00:01 might not be the time where the first activity occurred, if it is not maintained over night
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
     * Calculate the most frequent start activity of a day based on the training data of the prediction framework
     * Returns the activity which was most often the first in each trace
     *
     * @param train Training Data used in the Predcition Framework
     * @return activity ID of the most frequent start activity
     */

    public static int getMostFrequentStartActivityFromTrain(ArrayList<ArrayList<ActivityItem>> train){
        ActivityItem tempItem;
        String tempIDString;
        Long[] timestamps;
        HashMap<String, Integer> activityCount = new HashMap<>();
        if(train.size()>0) {
            for (ArrayList<ActivityItem> trace : train) {
                if(trace.size() > 0) {
                    tempItem = trace.get(0);
                    timestamps = TimeUtils.convertDateStringToTimestamp(new String[]{tempItem.getStarttimeAsString(),tempItem.getEndtimeAsString()});
                    tempIDString = TimeUtils.isAM(timestamps[0]+tempItem.getActivityId()) + ProcessMiningUtil.getIDwithLeadingZero(tempItem.getActivityId())+ProcessMiningUtil.getIDwithLeadingZero(tempItem.getSubactivityId());
                    if(activityCount.containsKey(tempIDString)){
                        activityCount.put(tempIDString,activityCount.get(tempIDString)+1);
                    } else{
                        activityCount.put(tempIDString,1);
                    }

                }
            }
        }
        int maxCount = 0, currentCount;
        tempIDString = "0";
        for (Entry<String, Integer> entry : activityCount.entrySet()) {
            currentCount = entry.getValue();
            if (maxCount < currentCount) {
                maxCount = currentCount;
                tempIDString = entry.getKey();
            }
        }
        return Integer.valueOf(tempIDString);
    }
    /**
     * Calculate the most frequent start activity of a day based on the cases
     * Returns the activity which was most often the first in each case
     *
     * @param cases Output of the CaseCreator
     * @return activity ID of the most frequent start activity
     */

    public static int getMostFrequentStartActivityFromCases(ArrayList<String[]> cases) {
        HashMap<Integer, Integer> activityCount = new HashMap<>();
        int currentCase = 0, currentActivity, currentCount;
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
     * @return activity ID of the most frequent end activity
     */
    public static int getMostFrequentEndActivity(ArrayList<String[]> cases) {
        HashMap<Integer, Integer> activityCount = new HashMap<>();
//       Initial currentCase is 1 so the change between the cases is registered
        int currentCaseKey = 1, currentActivityKey, currentActivityCount;
        String[] predecessorCaseValues = null;
//        iterate through all cases
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
     * @param idDurationMap map activity ID and duration pairs
     * @return True if sum of durations >= 1440 else False
     */
    public static boolean isTotalDurationReached(List<Pair<Integer, Double>> idDurationMap) {
        Double total = 0.0;
        for (Pair<Integer, Double> pair : idDurationMap) {
            total += pair.second;
        }
        return total > 1440;
    }

    /**
     * Method calculating the total percentage of the day covered by the Activities in the the List Provided
     * If the sum is below 100.00 the total day is not yet reached.
     *
     * @param idDurationMap map activity ID and duration pairs
     * @return True if sum of durations >= 100% else False
     */
    public static boolean isTotalPercentageReached(List<Pair<Integer, Double>> idDurationMap) {
        Double total = 0.0;
        for (Pair<Integer, Double> pair : idDurationMap) {
            total += pair.second;
        }
        return total > 100.0;

    }

    /**
     * Convert the ID-Duration Map into the Activities that it represents
     *
     * @param idDurationMap Map of Activity IDs and Durations
     * @param percentage are the value absolut or relative
     * @return List of ActivityItems
     */
    public static ArrayList<ActivityItem> createActivities(List<Pair<Integer, Double>> idDurationMap, boolean percentage) {
        double total = 0.0;
        int duration;
        ArrayList<ActivityItem> result = new ArrayList<>();
//        DataBaseHandler dbHandler = AppGlobal.getHandler();
        Date[] startEndDate;
        ActivityItem item;
//        How much is every minute/ percent worth towards the total day
        double relativeDurationOfUnit;

//      how much of the 1440 minutes / 100% of the day are covered by the result?
        for (Pair<Integer, Double> pair : idDurationMap) {
            total += pair.second;
        }
//      if the total is not reached compute how much every minute/ % is worth
        if (total < 1440 && !percentage) {
            relativeDurationOfUnit = 1440 / total;
        } else if (total < 100.00 && percentage) {
            relativeDurationOfUnit = 100.0 / total;
        } else {
            relativeDurationOfUnit = 1;
        }
//      Create activity items based on the list and the relativeDurationOfUnit

        Date date = TimeUtils.getCurrentDate();
        date = TimeUtils.getDate(date, 0, 0);
        for (int i = 0; i < idDurationMap.size(); i++) {

            duration = (int) Math.floor(idDurationMap.get(i).second * relativeDurationOfUnit);
//            calculate the minutes of the activity from the percentage it takes from the day
            if (percentage) {
                duration = duration * 1440 / 100;
            }
            startEndDate = TimeUtils.getDate(date, duration);
//            remove the am/pm-flag from the id
//            int id = removeAMPMFlag(idDurationMap.get(i).first);
            int[] ids = splitID(idDurationMap.get(i).first);
            item = new ActivityItem(ids[0], ids[1], startEndDate[0], startEndDate[1], 0);
            result.add(item);
            date = startEndDate[1];
        }
        return result;
    }
    /**
     * split the ID back to ID and SubActivityID
     * @param id Acity
     */
    public static int[] splitID(int id){
        int subActivityID;
        int activityID;

        String tempId = String.valueOf(id);

        //Implemented for HeuristicsMiner Start- and EndActivity
        if(tempId.length() == 4)
        {
            activityID = Integer.parseInt(tempId.substring(0,2));
            subActivityID = Integer.parseInt(tempId.substring(2,4));
//            return new int[]{activityID,subActivityID};
        } else {
            activityID = Integer.parseInt(tempId.substring(2, 4));
            subActivityID = Integer.parseInt(tempId.substring(4, 6));
        }
        return new int[]{activityID,subActivityID};
    }

    /**
     * Calculate how long each activity is in minutes with the real computed duration of each unit
     *
     * @param duration     either in percent or minutes of the day
     * @param realDuration actual contribution of each percent/ minute towards the day
     * @param percentual   duration in percent or minutes
     * @return Duration converted into minutes and calculated with the realDuration
     */
    public static double getActualDuration(double duration, double realDuration, boolean percentual) {
        if (percentual) {
            duration = realDuration * duration;
        } else {
            duration = realDuration * duration;
        }
        return duration;
    }

    /**
     *
     * Removes the flag of the activity for AM/PM
     *
     * @param id activity id with AM/PM flag
     * @return cleaned activity id
     */
    public static int removeAMPMFlag(int id) {
        //            remove the am/pm-flag from the id
        String tempId = String.valueOf(id);
        id = Integer.parseInt(tempId.substring(2, tempId.length()));
        return id;
    }


    /**
     * Convert the list of activities grouped by days as one big list
     * @param train the training data provided by the Prediction Framework
     * @return training data format used by FuzzyMiner
     */
    public static ArrayList<ActivityItem> convertDayToALStructure(ArrayList<ArrayList<ActivityItem>> train) {
        ArrayList<ActivityItem> items = new ArrayList<>();
        for (ArrayList<ActivityItem> list : train) {
            for (ActivityItem item : list) {
                items.add(item);
            }
        }
        return items;
    }

    /**
     * Adds a leading zero to all activity ids smaller than 9
     * @param id actual Activity ID
     * @return Two Digit Activity ID
     */
    public static String getIDwithLeadingZero(int id){
        if(id < 10){
            return "0"+id;
        }else{
            return String.valueOf(id);
        }
    }

    public static long getAverageStartTime(String ID, ArrayList<String[]> eventlist) {
        long allStartTimes = (long) 0.0;
        long divide = (long) 0.0;
        for (String[] item : eventlist) {
            if (item[1].contains(ID)) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                String time = sdf.format(new Date());
                Time t = Time.valueOf(time);
                divide++;
                allStartTimes = allStartTimes + (t.getTime());
            }
        }
        return allStartTimes / divide;
    }

    public static long getAverageEndTime(String ID, ArrayList<String[]> eventlist) {
        long allEndTimes = (long) 0.0;
        long divide = (long) 0.0;
        for (String[] item : eventlist) {
            if (item[1].contains(ID)) {
                divide++;
                allEndTimes = allEndTimes + (Long.parseLong(item[3]));
            }
        }
        return allEndTimes / divide;
    }

    /**
     * Creates the ID used in the Process Mining models from Date, ActivityID and SubActivityID
     * @param startDate of the Activity
     * @param activityID Activity ID
     * @param subactivityID SubActivityID
     * @return
     */

    public static int getProcessModelID(Date startDate, int activityID, int subactivityID){
        String temp = TimeUtils.isAM(startDate.getTime());
        temp += ProcessMiningUtil.getIDwithLeadingZero(activityID);
        temp += ProcessMiningUtil.getIDwithLeadingZero(subactivityID);
        return Integer.valueOf(temp);
    }
}
