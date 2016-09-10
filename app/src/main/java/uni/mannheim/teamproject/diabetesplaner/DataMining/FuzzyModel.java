package uni.mannheim.teamproject.diabetesplaner.DataMining;

import android.support.v4.util.Pair;

import org.deckfour.xes.model.XLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.Default.FMEdge;
import uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.Default.FMNode;
import uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.Default.FuzzyMinerImpl;
import uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.Default.MutableFuzzyGraph;
import uni.mannheim.teamproject.diabetesplaner.DataMining.Preprocessing.CustomXLog;
import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityPrediction;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.DummyDataCreator;

/**
 * Created by Ivo on 10.07.2016.
 * Creates a Fuzzy Graph based on a XLog created from the Activities of the Database.
 * Allows to find the next Activity based on the current one.
 * The Daily Routine is either 1440 Minutes(100%) long or stops if the most frequent end activity is reached.
 *
 */
public class FuzzyModel {
    private MutableFuzzyGraph fuzzyMinerModel;

    /**
     * Create a FuzzyModel based on the Activities of a certain Weekday
     *
     * @param day number between 0 and 6 (where 0 is sunday)
     * @throws Exception
     */
    public FuzzyModel(int day, boolean percentage) throws Exception {
//        DummyDataCreator.populateDataBase();
        ArrayList<ActivityItem> items = AppGlobal.getHandler().getAllActivitiesByWeekday(AppGlobal.getHandler(), day);
        CustomXLog customXLog = new CustomXLog(items);
        XLog xLog = customXLog.getXLog();
        FuzzyMinerImpl fuzzyMiner = new FuzzyMinerImpl(xLog);
        fuzzyMinerModel = fuzzyMiner.getFuzzyGraph();
        ArrayList<String[]> cases = customXLog.getCases();
        ArrayList<String[]> eventList = customXLog.getEventList();
        cases.remove(0);
        eventList.remove(0);
        Map<Integer, Double> durationMap;
        if (percentage) {
            durationMap = ProcessMiningUtill.getAveragePercentualDurations(eventList);
        } else {
            durationMap = ProcessMiningUtill.getAverageDurations(eventList);
        }
        int startID = ProcessMiningUtill.getMostFrequentStartActivity(cases);
        int endID = ProcessMiningUtill.getMostFrequentEndActivity(cases);

        List<Pair<Integer, Double>> idDurationMap = createDailyRoutine(startID,endID,durationMap,percentage);
        ArrayList<ActivityPrediction> activityPredictions = createPredictionDataStructure(durationMap);
        idDurationMap.size();
//        ProcessMiningUtil.createActivities(idDurationMap, percentage);

    }

    /**
     * Create a FuzzyModel based on all available data
     *
     * @throws Exception
     */
    public FuzzyModel(boolean percentage) throws Exception {
        DummyDataCreator.populateDataBase();
        ArrayList<ActivityItem> items = AppGlobal.getHandler().getAllActivities(AppGlobal.getHandler());
        CustomXLog customXLog = new CustomXLog(items);
        XLog xLog = customXLog.getXLog();
        FuzzyMinerImpl fuzzyMiner = new FuzzyMinerImpl(xLog);
        fuzzyMinerModel = fuzzyMiner.getFuzzyGraph();
        List<Pair<Integer, Double>> idDurationMap = new ArrayList<>();
        ArrayList<String[]> cases = customXLog.getEventList();
        cases.remove(0);
        Map<Integer, Double> durationMap;
        if (percentage) {
            durationMap = ProcessMiningUtill.getAveragePercentualDurations(cases);
        } else {
            durationMap = ProcessMiningUtill.getAverageDurations(cases);
        }
        int startID = ProcessMiningUtill.getMostFrequentStartActivity(cases);
        int endID = ProcessMiningUtill.getMostFrequentEndActivity(cases);
        int currentId = startID, predecessorId = startID, tempId = startID;
        idDurationMap.add(new Pair<>(currentId, durationMap.get(currentId)));
        if (percentage) {
            while (!ProcessMiningUtill.isTotalPercentageReached(idDurationMap)) {
                tempId = currentId;
                if (currentId != endID) {
                    currentId = getNextActivity(currentId);
                } else {
                    break;
                }
                idDurationMap.add(new Pair<>(currentId, durationMap.get(currentId)));
                predecessorId = tempId;
            }
        } else {
            while (!ProcessMiningUtill.isTotalDurationReached(idDurationMap)) {
                tempId = currentId;
//            currentId = getNextActivity(currentId, predecessorId);
                if (currentId != endID) {
                    currentId = getNextActivity(currentId);
                } else {
                    break;
                }
                idDurationMap.add(new Pair<>(currentId, durationMap.get(currentId)));
                predecessorId = tempId;
            }
        }
        idDurationMap.size();
//        ProcessMiningUtil.createActivities(idDurationMap, percentage);
    }
private List<Pair<Integer, Double>> createDailyRoutine(int startID, int endID, Map<Integer, Double> durationMap, boolean percentage) throws Exception{
    List<Pair<Integer, Double>> idDurationMap = new ArrayList<>();
    int currentId = startID, predecessorId = startID, tempId;
    idDurationMap.add(new Pair<>(currentId, durationMap.get(currentId)));
    if(percentage)

    {
        while (!ProcessMiningUtill.isTotalPercentageReached(idDurationMap)) {
            tempId = currentId;
            if (currentId != endID) {
                currentId = getNextActivity(currentId);
            } else {
                break;
            }
            idDurationMap.add(new Pair<>(currentId, durationMap.get(currentId)));
            predecessorId = tempId;
        }
    }

    else

    {
        while (!ProcessMiningUtill.isTotalDurationReached(idDurationMap)) {
            tempId = currentId;
//            currentId = getNextActivity(currentId, predecessorId);
            if (currentId != endID) {
                currentId = getNextActivity(currentId);
            } else {
                break;
            }
            idDurationMap.add(new Pair<>(currentId, durationMap.get(currentId)));
            predecessorId = tempId;
        }
    }
return idDurationMap;
}
    /**
     * Return the most likely successor of the current activity based on the significance in the model
     * Loops are forbidden as Activity A followed by Activity B followed by Activity A
     * @param currentActivityId id of the current activity
     * @return return most likely successor activity id for current activity
     */
    public int getNextActivity(int currentActivityId, int predecessorId) {
        Set<FMNode> nodes = fuzzyMinerModel.getNodes();
        Set<FMEdge<? extends FMNode, ? extends FMNode>> likelySuccessors = new HashSet<>();
        Set<FMNode> interestingNodes = new HashSet<>();
//        Retrieve all nodes from the model which have the same ID and are the End of that activity
        for (FMNode node : nodes) {
            if (node.getElementName().equals(String.valueOf(currentActivityId)) && node.getEventType().equals("Complete")) {
                likelySuccessors.addAll(node.getGraph().getOutEdges(node));

            }
        }
        int successorID = 0;
        FMNode target;
        double successorSignificance = 0.0;
        double successorCorrelation = 0.0;
        for (FMEdge edge : likelySuccessors) {
            target = (FMNode) edge.getTarget();
            if ((edge.getSignificance() > successorSignificance) && (Integer.parseInt(target.getElementName())!=predecessorId) && (Integer.parseInt(target.getElementName())!=currentActivityId) && (Integer.parseInt(target.getElementName())!=0)) {
                if (target.getEventType().equals("Start")) {
                    successorID = Integer.parseInt(target.getElementName());
                    successorSignificance = edge.getSignificance();
                    successorCorrelation = edge.getCorrelation();
                }
            }
        }
        return successorID;
    }
    /**
     * Return the most likely successor of the current activity based on the significance in the model
     *
     * @param currentActivityId id of the current activity
     * @return most likely successor activity id of the current activity
     */
    private int getNextActivity(int currentActivityId) {
        Set<FMNode> nodes = fuzzyMinerModel.getNodes();
        Set<FMEdge<? extends FMNode, ? extends FMNode>> likelySuccessors = new HashSet<>();
        Set<FMNode> interestingNodes = new HashSet<>();
//        Retrieve all nodes from the model which have the same ID and are the End of that activity
        for (FMNode node : nodes) {
            if (node.getElementName().equals(String.valueOf(currentActivityId)) && node.getEventType().equals("Complete")) {
                likelySuccessors.addAll(node.getGraph().getOutEdges(node));

            }
        }
        int successorID = 0;
        FMNode target;
        double successorSignificance = 0.0;
        double successorCorrelation = 0.0;
        for (FMEdge edge : likelySuccessors) {
            target = (FMNode) edge.getTarget();
            if (edge.getSignificance() > successorSignificance && Integer.parseInt(target.getElementName())!=currentActivityId && (Integer.parseInt(target.getElementName())!=0)){
                if (target.getEventType().equals("Start")) {
                    successorID = Integer.parseInt(target.getElementName());
                    successorSignificance = edge.getSignificance();
                    successorCorrelation = edge.getCorrelation();
                }
            }
        }
        return successorID;
    }

    private ArrayList<ActivityPrediction> createPredictionDataStructure(Map<Integer, Double> durationMap){
        ArrayList<ActivityPrediction> activityPredictions = new ArrayList<>();
        DataBaseHandler handler = AppGlobal.getHandler();
        String activityName;
        int activityID;
        HashMap<Integer, Double> successorProbabilityMap = new HashMap<>();
        ActivityPrediction activityPrediction;
        for(FMNode node : fuzzyMinerModel.getNodes()){
            if(!node.getElementName().equals("Start//Start") && node.getEventType().equals("Complete")) {
                activityID = Integer.parseInt(node.getElementName());
                activityName = handler.getActionById(handler, ProcessMiningUtill.removeAMPMFlag(activityID));
                activityPrediction = new ActivityPrediction(activityID, 0, activityName);
                for (FMEdge<? extends FMNode, ? extends FMNode> edge : node.getGraph().getOutEdges(node)) {
                    successorProbabilityMap.put(Integer.valueOf(edge.getTarget().getElementName()), edge.getSignificance());
                }
                activityPrediction.setFollowerProbabilityMap(successorProbabilityMap);
                activityPrediction.setAverageDuration(durationMap.get(Integer.parseInt(node.getElementName())));
                activityPredictions.add(activityPrediction);
                successorProbabilityMap = new HashMap<>();
            }
        }
    return activityPredictions;

    }
}
