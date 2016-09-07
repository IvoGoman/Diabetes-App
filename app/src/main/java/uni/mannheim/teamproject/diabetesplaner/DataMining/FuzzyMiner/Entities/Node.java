package uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.Entities;


import uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.Utility.Utilities;

/**
 * Created by Ivo on 4/23/2016.
 * Parent Class of all more specific node classes.
 * This class provides the basic functionality of Nodes.
 */
public class Node {
    private String activityName;
    private int activityID;
    private long startTime;
    private long endTime;
    private double duration;
    private int frequency;

    private double unarySignificance;
    private Edge[] incomingEdges;
    private Edge[] outgoingEdges;

    public Node(int activityID, long startTime, long endTime) {
        this.activityName = Utilities.getActivityNameByID(activityID);
        this.activityID = activityID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.frequency=1;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public int getActivityID() {
        return activityID;
    }

    public void setActivityID(int activityID) {
        this.activityID = activityID;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public double getUnarySignificance() {
        return unarySignificance;
    }

    public void setUnarySignificance(double unarySignificance) {
        this.unarySignificance = unarySignificance;
    }

    public Edge[] getIncomingEdges() {
        return incomingEdges;
    }

    public void setIncomingEdges(Edge[] incomingEdges) {
        this.incomingEdges = incomingEdges;
    }

    public Edge[] getOutgoingEdges() {
        return outgoingEdges;
    }

    public void setOutgoingEdges(Edge[] outgoingEdges) {
        this.outgoingEdges = outgoingEdges;
    }

    public void increaseFrequency() {
        this.frequency++;
    }

    public void decreaseFrequency() {
        this.frequency--;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }
}
