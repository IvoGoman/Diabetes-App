package uni.mannheim.teamproject.diabetesplaner.DataMining;

import android.support.v4.util.Pair;

import org.deckfour.xes.model.XLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityPredictionEdge;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;

/**
 * Created by Ivo on 10.07.2016.
 * Creates a Fuzzy Graph based on a XLog created from the Activities of the Database.
 * Allows to find the next Activity based on the current one.
 * The Daily Routine is either 1440 Minutes(100%) long or stops if the most frequent end activity is reached.
 */
public class FuzzyModel {
    private MutableFuzzyGraph fuzzyMinerModel;
    private List<Pair<Integer, Double>> idDurationMap;
    private boolean percentage;
    private HashMap<Integer, ActivityPrediction> predictionStructure;
    private ArrayList<FMEdge<? extends FMNode, ? extends FMNode>> visitedEdges = new ArrayList<>();
    private HashMap<Integer, Integer> visitedEdgeIDs = new HashMap<>();

    /**
     * Create a FuzzyModel based on the Activities of a certain Weekday
     *
     * @param day        int value between 0 - 9 where 0 = Sunday
     * @param percentage values for the duration in % or minutes
     * @throws Exception
     */
    public FuzzyModel(int day, boolean percentage) {
        this.percentage = percentage;
        this.visitedEdges.clear();
        ArrayList<ActivityItem> items = AppGlobal.getHandler().getAllActivitiesByWeekday(day);
        if (items.size() > 0) {
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
                durationMap = ProcessMiningUtil.getAveragePercentualDurations(eventList);
            } else {
                durationMap = ProcessMiningUtil.getAverageDurations(eventList);
            }
            int startID = ProcessMiningUtil.getMostFrequentStartActivityFromCases(cases);
            int endID = ProcessMiningUtil.getMostFrequentEndActivity(cases);
            try {
                this.idDurationMap = createDailyRoutine(startID, endID, durationMap, percentage);
                this.createPredictionDataStructure(durationMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Alternative constructor to integrate with the Prediction Framework
     *
     * @param train      data provided by Prediction Framework
     * @param percentage Flag to choose if the durations should be handled as minutes or %
     */
    public FuzzyModel(ArrayList<ArrayList<ActivityItem>> train, boolean percentage) {
        ArrayList<ActivityItem> items = ProcessMiningUtil.convertDayToALStructure(train);
        this.visitedEdges.clear();
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
            durationMap = ProcessMiningUtil.getAveragePercentualDurations(eventList);
        } else {
            durationMap = ProcessMiningUtil.getAverageDurations(eventList);
        }
        int startID = 9990;
        int endID = 9991;
        try {
            this.idDurationMap = createDailyRoutine(startID, endID, durationMap, percentage);
            this.createPredictionDataStructure(durationMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a FuzzyModel based on all available data
     *
     * @param percentage durations calculated in % or Minutes
     */
    public FuzzyModel(boolean percentage) {
        ArrayList<ActivityItem> items = AppGlobal.getHandler().getAllActivities();
        this.visitedEdges.clear();
        CustomXLog customXLog = new CustomXLog(items);
        XLog xLog = customXLog.getXLog();
        FuzzyMinerImpl fuzzyMiner = new FuzzyMinerImpl(xLog);
        fuzzyMinerModel = fuzzyMiner.getFuzzyGraph();
        List<Pair<Integer, Double>> idDurationMap = new ArrayList<>();
        ArrayList<String[]> cases = customXLog.getEventList();
        cases.remove(0);
        Map<Integer, Double> durationMap;
        if (percentage) {
            durationMap = ProcessMiningUtil.getAveragePercentualDurations(cases);
        } else {
            durationMap = ProcessMiningUtil.getAverageDurations(cases);
        }
        int startID = ProcessMiningUtil.getMostFrequentStartActivityFromCases(cases);
        int endID = ProcessMiningUtil.getMostFrequentEndActivity(cases);
        int currentId = startID;
        idDurationMap.add(new Pair<>(currentId, durationMap.get(currentId)));
        try {
            idDurationMap = createDailyRoutine(startID, endID, durationMap, percentage);
            this.createPredictionDataStructure(durationMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        idDurationMap.size();
    }

    /**
     * @return Activities of the Daily Routine
     */
    public ArrayList<ActivityItem> makeFuzzyMinerPrediction() {
        return ProcessMiningUtil.createActivities(idDurationMap, percentage);
    }

    /**
     * @param startID     int which indicates the expected start activity
     * @param endID       int which indicates the expected end activity
     * @param durationMap map of int activity ids and corresponding average double durations
     * @param percentage  boolean flag indicating if the durations are precentual or absolute
     * @return Daily Routine as a List of Activity IDs and Corresponding Duration
     */
    private List<Pair<Integer, Double>> createDailyRoutine(int startID, int endID, Map<Integer, Double> durationMap, boolean percentage) {
        List<Pair<Integer, Double>> idDurationMap = new ArrayList<>();
        int currentID = startID;

        if (percentage) {
            while (!ProcessMiningUtil.isTotalPercentageReached(idDurationMap)) {
                currentID = getNextActivity(currentID);
                if (currentID == endID) {
                    break;
                } else {
                    idDurationMap.add(new Pair<>(currentID, durationMap.get(currentID)));
                }
            }
        } else {
            while (!ProcessMiningUtil.isTotalDurationReached(idDurationMap)) {
                currentID = getNextActivity(currentID);
                if (currentID == endID) {
                    break;
                } else {
                    idDurationMap.add(new Pair<>(currentID, durationMap.get(currentID)));
                }
            }
        }
        return idDurationMap;
    }

    /**
     * Return the most likely successor of the current activity based on the significance && correlation in the model
     *
     * @param currentActivityId Activity ID of the current activity
     * @return Activity ID of the most probable successor of the current activity
     */
    private int getNextActivity(int currentActivityId) {
        Set<FMNode> nodes = fuzzyMinerModel.getNodes();
        Set<FMEdge<? extends FMNode, ? extends FMNode>> likelySuccessors = new HashSet<>();
        Set<FMEdge<? extends FMNode, ? extends FMNode>> tempSuccessors = new HashSet<>();
        Set<FMEdge<? extends FMNode, ? extends FMNode>> targets = new HashSet<>();
//        Retrieve all nodes from the model which have the same ID and are the End of that activity
        for (FMNode node : nodes) {
            if (node.getElementName().equals(String.valueOf(currentActivityId))) {
                likelySuccessors.addAll(node.getGraph().getOutEdges(node));
            }
        }

//        do the likelySuccessors contain any of the visited edges
        Set<FMEdge<? extends FMNode, ? extends FMNode>> candidates = new HashSet<>();
        Set<FMEdge<? extends FMNode, ? extends FMNode>> candidatesAMPM = new HashSet<>();
        for (FMEdge<? extends FMNode, ? extends FMNode> likelySuccessor : likelySuccessors) {
            for (FMEdge<? extends FMNode, ? extends FMNode> visitedEdge : visitedEdges) {
                if (visitedEdge.getSource().getElementName().equals(likelySuccessor.getSource().getElementName()) && visitedEdge.getTarget().getElementName().equals(likelySuccessor.getTarget().getElementName())) {
                    candidates.add(likelySuccessor);
                    break;
                }
//                no self loop
                if (likelySuccessor.getSource().getElementName().equals(likelySuccessor.getTarget().getElementName())) {
                    candidates.add(likelySuccessor);
                    break;
                }
//                no pm activity moving to am activity
                if (Integer.valueOf(likelySuccessor.getSource().getElementName()) < 110000 && Integer.valueOf(likelySuccessor.getTarget().getElementName()) > 110000) {
                    candidatesAMPM.add(likelySuccessor);
                }
            }
        }
//        remove already visited edges
        if (!candidates.containsAll(likelySuccessors)) {
            likelySuccessors.removeAll(candidates);
        }
//        remove all edges that go from PM to AM
        if(!candidatesAMPM.containsAll(likelySuccessors)){
            likelySuccessors.removeAll(candidatesAMPM);
        }

//      are any successors left after removing the visited edges?
        if (likelySuccessors.size() < 1) {
            for (FMNode node : nodes) {
                if (node.getElementName().equals(String.valueOf(currentActivityId))) {
                    likelySuccessors.addAll(node.getGraph().getOutEdges(node));
                }
            }
        }

        int successorID = 0;
        int targetID;
        FMNode target;
        FMNode tempNode;
        FMEdge<? extends FMNode, ? extends FMNode> targetNode;
        double edgeSignificance = 0.0;
        double edgeCorrelation = 0.0;
        double nodeSignificance = 0.0;
//        do we have any likely successors?
        if (likelySuccessors.size() > 0) {

            for (FMEdge edge : likelySuccessors) {
                target = (FMNode) edge.getTarget();
                targetID = Integer.parseInt(target.getElementName());
//            check if target produces potential self loops
                targets.clear();
                targets.addAll(target.getGraph().getOutEdges(target));
//            start of the activity? then get the outedges of the complete cycle

//            check if the target is a start activity or not
                if (targets.size() == 1) {
                    for (FMEdge<? extends FMNode, ? extends FMNode> targetEdge : targets) {
                        if (targetEdge.getTarget().getElementName().equals(target.getElementName()) && targetEdge.getTarget().getEventType().equals("Complete")) {
                            tempNode = targetEdge.getTarget();
                            targets.clear();
                            targets.addAll(tempNode.getGraph().getOutEdges(tempNode));
                        }
                    }
                }
//                go through all edges
                Iterator<FMEdge<? extends FMNode, ? extends FMNode>> iterator = targets.iterator();
                while (iterator.hasNext()) {
                    targetNode = iterator.next();
//                remove potential 2 edge loops
                    if (Integer.parseInt(targetNode.getTarget().getElementName()) == currentActivityId) {
                        iterator.remove();
                    }
//                remove irrelevant edges
//                } else if (targetNode.getTarget().getEventType().equals("Complete")) {
//                    iterator.remove();
//                }
                }

//            check if target produces potential self loops
                targetID = Integer.parseInt(target.getElementName());
                if (targets.size() > 0 | targetID == 9991) {
                    if ((Integer.parseInt(target.getElementName()) != currentActivityId) && (targetID != 0) /*&& target.getEventType().equals("Start")*/) {
                        if (edge.getCorrelation() > edgeCorrelation) {
//                        if (edge.getSignificance() > edgeSignificance) {
                            edgeSignificance = edge.getSignificance();
                            edgeCorrelation = edge.getCorrelation();
                            successorID = targetID;
                            nodeSignificance = target.getSignificance();
                            tempSuccessors.clear();
                            tempSuccessors.add(edge);
                        } else if ((edge.getCorrelation() == edgeCorrelation) && (edge.getSignificance() > edgeSignificance)) {
//                        } else if ((edge.getSignificance() == edgeSignificance) && (edge.getCorrelation() > edgeCorrelation)) {
                            edgeSignificance = edge.getSignificance();
                            edgeCorrelation = edge.getCorrelation();
                            successorID = targetID;
                            nodeSignificance = target.getSignificance();
                            tempSuccessors.clear();
                            tempSuccessors.add(edge);
                        } else if ((edge.getSignificance() == edgeSignificance) && (edge.getCorrelation() == edgeCorrelation) && (target.getSignificance() > nodeSignificance)) {
                            edgeSignificance = edge.getSignificance();
                            edgeCorrelation = edge.getCorrelation();
                            successorID = targetID;
                            nodeSignificance = target.getSignificance();
                            tempSuccessors.clear();
                            tempSuccessors.add(edge);
                        } else if ((edge.getSignificance() == edgeSignificance) && (edge.getCorrelation() == edgeCorrelation) && (target.getSignificance() == nodeSignificance)) {
                            tempSuccessors.add(edge);
                        }
                    }
                }
            }
        }
        //            if edge significance, edge correlation and node significance are the same choose at random
        if (tempSuccessors.size() > 1) {
            int temp = (int) (tempSuccessors.size() * Math.random());
            FMEdge resultEdge = (FMEdge) tempSuccessors.toArray()[temp];
            target = (FMNode) resultEdge.getTarget();
            visitedEdges.add(resultEdge);
            successorID = Integer.parseInt(target.getElementName());
        } else if (tempSuccessors.size() == 1) {
            FMEdge resultEdge = (FMEdge) tempSuccessors.toArray()[0];
            target = (FMNode) resultEdge.getTarget();
            visitedEdges.add(resultEdge);
            successorID = Integer.parseInt(target.getElementName());
        }
        return successorID;

    }


    /**
     * @param durationMap Map of Activity IDs and corresponding Durations
     * @return Datastructure representing the fuzzy miner model
     */

    private void createPredictionDataStructure(Map<Integer, Double> durationMap) {
//        ArrayList<ActivityPrediction> activityPredictions = new ArrayList<>();
        HashMap<Integer, ActivityPrediction> activityPredictions = new HashMap<>();
        DataBaseHandler handler = AppGlobal.getHandler();
        String activityName;
        int activityID, targetID;
        HashMap<Integer, ActivityPredictionEdge> successorProbabilityMap = new HashMap<>();
        ActivityPrediction activityPrediction;
        ActivityPredictionEdge activityPredictionEdge;
        for (FMNode node : fuzzyMinerModel.getNodes()) {
            if (node.getEventType().equals("Complete")) {
                activityID = Integer.parseInt(node.getElementName());
//                activityName = handler.getActionById(ProcessMiningUtil.removeAMPMFlag(activityID));
                activityName = handler.getActivitybySubActicityId(ProcessMiningUtil.splitID(activityID)[1]);
                activityPrediction = new ActivityPrediction(activityID, 0, activityName);
                for (FMEdge<? extends FMNode, ? extends FMNode> edge : node.getGraph().getOutEdges(node)) {
                    targetID = Integer.valueOf(edge.getTarget().getElementName());
                    activityPredictionEdge = new ActivityPredictionEdge(targetID, edge.getSignificance(), edge.getCorrelation(), edge.getTarget().getSignificance());
                    successorProbabilityMap.put(Integer.valueOf(edge.getTarget().getElementName()), activityPredictionEdge);
                }
                activityPrediction.setEdgeTargetMap(successorProbabilityMap);
                if (durationMap.containsKey(Integer.parseInt(node.getElementName()))) {
                    activityPrediction.setAverageDuration(durationMap.get(Integer.parseInt(node.getElementName())));
                }
                if (activityID == 9990) {
                    activityPrediction.setStart(true);
                } else if (activityID == 9991) {
                    activityPrediction.setEnd(true);
                }
                activityPredictions.put(activityPrediction.getActivityID(), activityPrediction);
                successorProbabilityMap = new HashMap<>();
            }
        }
        this.predictionStructure = activityPredictions;
    }

    /**
     * @return POJO representing the Fuzzy Miner Model
     */
    public HashMap<Integer, ActivityPrediction> getPredictionStructure() {
        return this.predictionStructure;
    }
}
