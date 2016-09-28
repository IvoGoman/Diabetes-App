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
    private HashMap<Integer, ActivityPredictionEdge> edgeTargetMap;
    private double averageDuration;
    private boolean isStart = false;
    private boolean isEnd = false;
// TODO: Add Average Start and End Date for the Activity


//    public ActivityPrediction(int activityID, int subActivityID, String activityName, HashMap<Integer, Double> followerProbabilityMap, double averageDuration) {
//        this.activityID = activityID;
//        this.subActivityID = subActivityID;
//        this.activityName = activityName;
//        this.followerProbabilityMap = followerProbabilityMap;
//        this.averageDuration = averageDuration;
//    }
    public ActivityPrediction(int activityID, int subActivityID, String activityName, HashMap<Integer, ActivityPredictionEdge> edgeTargetMap, double averageDuration) {
        this.activityID = activityID;
        this.subActivityID = subActivityID;
        this.activityName = activityName;
        this.edgeTargetMap = edgeTargetMap;
        this.averageDuration = averageDuration;
    }

    public ActivityPrediction(int activityID, int subActivityID, String activityName) {
        this.activityID = activityID;
        this.subActivityID = subActivityID;
        this.activityName = activityName;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public boolean isStart() {
        return isStart;
    }

    public boolean isEnd() {
        return isEnd;
    }

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


    public double getAverageDuration() {
        return averageDuration;
    }

    public void setAverageDuration(double averageDuration) {
        this.averageDuration = averageDuration;
    }


    public HashMap<Integer, ActivityPredictionEdge> getEdgeTargetMap() {
        return edgeTargetMap;
    }

    public void setEdgeTargetMap(HashMap<Integer, ActivityPredictionEdge> edgeTargetMap) {
        this.edgeTargetMap = edgeTargetMap;
    }
}
