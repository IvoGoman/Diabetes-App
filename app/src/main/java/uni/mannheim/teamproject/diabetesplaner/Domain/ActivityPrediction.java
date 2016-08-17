package uni.mannheim.teamproject.diabetesplaner.Domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ivo on 17.08.2016.
 *
 * Class to identify the following IDs of a Activity
 */
public class ActivityPrediction {

    private int activityID;
    private int subActivityID;
    private String activityName;
    private HashMap<Integer, Double> followerProbabilityMap;
    private int averageDuration;
// TODO: Add Average Start and End Date for the Activity

    public int getActivityID() {
        return activityID;
    }

    public void setActivityID(int activityID) {
        this.activityID = activityID;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
    public Map<Integer, Double> getFollowerProbabilityMap() {
        return followerProbabilityMap;
    }

    public void setFollowerProbabilityMap(HashMap<Integer, Double> followerProbabilityMap) {
        this.followerProbabilityMap = followerProbabilityMap;
    }

    public int getAverageDuration() {
        return averageDuration;
    }

    public void setAverageDuration(int averageDuration) {
        this.averageDuration = averageDuration;
    }

}
