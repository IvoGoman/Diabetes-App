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
    private HashMap<Integer, Double> followerProbabilityMap;
    private int averageDuration;


    public int getActivityID() {
        return activityID;
    }

    public void setActivityID(int activityID) {
        this.activityID = activityID;
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
