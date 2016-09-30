package uni.mannheim.teamproject.diabetesplaner.DataMining.EvaluationFramework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import uni.mannheim.teamproject.diabetesplaner.DataMining.ProcessMiningUtil;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityPrediction;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityPredictionEdge;

/**
 * Class which implements a Fitness Measure for the Process Mining Evaluation
 * This fitness measure that is implemented is the completness, showing the
 * fraction of traces that are covered by the process model
 */


public class Fitness {
    public static final String FITNESS_COMPLETENESS = "PLAIN";
    public static final String FITNESS_WEIGHTED = "WEIGHTED";
    public static final String FITNESS_TOKEN = "TOKEN";
    private static boolean weighted;

    public static double evaluate(ArrayList<ArrayList<ActivityItem>> training, HashMap<Integer, ActivityPrediction> model, String mode) {
        double traceCount = 0;
        double valueCount = 0;
        double score = 0;
        for (ArrayList<ActivityItem> trace : training) {
            switch (mode) {
                case (FITNESS_COMPLETENESS):
                    traceCount += 1;
                    valueCount += Fitness.compareUnweighted(trace, model);
                    break;
                case (FITNESS_WEIGHTED):
                    traceCount += 1;
                    valueCount += Fitness.compareWeighted(trace, model);
                    break;
                case (FITNESS_TOKEN):
                    traceCount += trace.size();
                    valueCount += Fitness.compareTokenWeighted(trace, model);
                    break;
            }
        }
        score = valueCount / traceCount;
        return score;
    }

    /**
     * Compares the trace with the model and returns 1 if the trace can be replayed
     * The weights of the model are not considered
     *
     * @param trace current case that is looked at [one day]
     * @param model current model of the miner
     * @return 1 if the trace can be replayed
     */
    private static int compareUnweighted(ArrayList<ActivityItem> trace, HashMap<Integer, ActivityPrediction> model) {
        Iterator traceIterator = trace.iterator();
        ActivityPrediction predictionItem = null;
        ActivityItem currentTraceItem;
        int result = 0;
        int traceID;
        if (traceIterator.hasNext()) {
//            get the current item in the trace and get the actual model id from it (needed to distinguish between AM and PM)
            currentTraceItem = (ActivityItem) traceIterator.next();
            traceID = ProcessMiningUtil.getProcessModelID(currentTraceItem.getStarttime(), currentTraceItem.getActivityId(), currentTraceItem.getSubactivityId());
//            find start node of the model representation
            if (model.containsKey(Fitness.getStartID(model))) {
                predictionItem = model.get(Fitness.getStartID(model));
            }
            if (predictionItem.getEdgeTargetMap().containsKey(traceID)) {
                predictionItem = model.get(traceID);
            }
            while (predictionItem.getActivityID() == traceID && traceIterator.hasNext()) {
                currentTraceItem = (ActivityItem) traceIterator.next();
                traceID = ProcessMiningUtil.getProcessModelID(currentTraceItem.getStarttime(), currentTraceItem.getActivityId(), currentTraceItem.getSubactivityId());
                if (predictionItem.getEdgeTargetMap().containsKey(traceID)) {
                    predictionItem = model.get(traceID);
                } else {
                    result = 0;
                    break;
                }
                if (!traceIterator.hasNext()) {
                    if (predictionItem.getEdgeTargetMap().containsKey(Fitness.getEndID(model))) {
                        result = 1;
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Compares the trace with the model and returns 1 if the trace can be replayed
     * Weights on the edges are considered and only the most probable is used
     *
     * @param trace current case that is looked at [one day]
     * @param model current model of the miner
     * @return 1 if the trace can be replayed
     */
    private static int compareWeighted(ArrayList<ActivityItem> trace, HashMap<Integer, ActivityPrediction> model) {
        Iterator traceIterator = trace.iterator();
        ActivityPrediction predictionItem = null;
        ActivityItem currentTraceItem;
        int result = 0;
        int traceID;
        if (traceIterator.hasNext()) {
//            get the current item in the trace and get the actual model id from it (needed to distinguish between AM and PM)
            currentTraceItem = (ActivityItem) traceIterator.next();
            traceID = ProcessMiningUtil.getProcessModelID(currentTraceItem.getStarttime(), currentTraceItem.getActivityId(), currentTraceItem.getSubactivityId());
//            find start node of the model representation
//            if (model.containsKey(Fitness.getStartID(model))) {
            if (model.containsKey(9990)) {
                predictionItem = model.get(Fitness.getStartID(model));
            }
            if (Fitness.nextActivityPossible(traceID, predictionItem.getEdgeTargetMap())) {
                predictionItem = model.get(traceID);
            }
            while (predictionItem.getActivityID() == traceID && traceIterator.hasNext()) {
                currentTraceItem = (ActivityItem) traceIterator.next();
                traceID = ProcessMiningUtil.getProcessModelID(currentTraceItem.getStarttime(), currentTraceItem.getActivityId(), currentTraceItem.getSubactivityId());
                if (Fitness.nextActivityPossible(traceID, predictionItem.getEdgeTargetMap())) {
                    predictionItem = model.get(traceID);
                } else {
                    result = 0;
                    break;
                }
                if (!traceIterator.hasNext()) {
//                    if (Fitness.nextActivityPossible(Fitness.getEndID(model), predictionItem.getEdgeTargetMap())) {
                    if (Fitness.nextActivityPossible(9991, predictionItem.getEdgeTargetMap())) {
                        result = 1;
                        break;
                    }
                }
            }
        }
        return result;
    }
    /**
     * Compares the trace with the model and returns 1 if the trace can be replayed
     * Considers the weight of the edge
     * If the edge in the trace is not the most probable path it increases the error
     * and just chooses the next activity of the trace
     *
     * @param trace current case that is looked at [one day]
     * @param model current model of the miner
     * @return 1 if the trace can be replayed
     */
    private static int compareTokenWeighted(ArrayList<ActivityItem> trace, HashMap<Integer, ActivityPrediction> model) {
        Iterator traceIterator = trace.iterator();
        ActivityPrediction predictionItem = null;
        ActivityItem currentTraceItem;
        int error = 0;
        int traceID;
        if (traceIterator.hasNext()) {
//            get the current item in the trace and get the actual model id from it (needed to distinguish between AM and PM)
            currentTraceItem = (ActivityItem) traceIterator.next();
            traceID = ProcessMiningUtil.getProcessModelID(currentTraceItem.getStarttime(), currentTraceItem.getActivityId(), currentTraceItem.getSubactivityId());
            if (model.containsKey(9990)) {
                predictionItem = model.get(Fitness.getStartID(model));
            }
            if (Fitness.nextActivityPossible(traceID, predictionItem.getEdgeTargetMap())) {
                predictionItem = model.get(traceID);
            } else if (!Fitness.nextActivityPossible(traceID, predictionItem.getEdgeTargetMap()) && predictionItem.getEdgeTargetMap().containsKey(traceID)) {
                error += 1;
                predictionItem = model.get(traceID);
            } else {
                while (traceIterator.hasNext() && currentTraceItem.getActivityId() != predictionItem.getActivityID() ) {
                    currentTraceItem = (ActivityItem) traceIterator.next();
                    predictionItem = model.get(Fitness.getNextActivity(predictionItem));
                    error += 1;
                }
                return error;
            }
            while (predictionItem.getActivityID() == traceID && traceIterator.hasNext()) {
                currentTraceItem = (ActivityItem) traceIterator.next();
                traceID = ProcessMiningUtil.getProcessModelID(currentTraceItem.getStarttime(), currentTraceItem.getActivityId(), currentTraceItem.getSubactivityId());
                if (Fitness.nextActivityPossible(traceID, predictionItem.getEdgeTargetMap())) {
                    predictionItem = model.get(traceID);
                } else if (!Fitness.nextActivityPossible(traceID, predictionItem.getEdgeTargetMap()) && predictionItem.getEdgeTargetMap().containsKey(traceID)) {
                    error += 1;
                    predictionItem = model.get(traceID);
                } else {
                    predictionItem = model.get(Fitness.getNextActivity(predictionItem));
                    while (traceIterator.hasNext() && predictionItem.getActivityID() != traceID ) {
                        currentTraceItem = (ActivityItem) traceIterator.next();
                        traceID = ProcessMiningUtil.getProcessModelID(currentTraceItem.getStarttime(), currentTraceItem.getActivityId(), currentTraceItem.getSubactivityId());
                        error += 1;
                    }
                    break;
                }
                if (!traceIterator.hasNext()) {
                    if (!Fitness.nextActivityPossible(9991, predictionItem.getEdgeTargetMap())) {
                        error += 1;
                        break;
                    }
                }
            }
        }
        return error;
    }


    /**
     * returns the Node of the ActivityPrediction
     * @param model of the Process Mining Algorithm
     * @return the ID of the Node which is the Start Activity
     */

    private static int getStartID(HashMap<Integer, ActivityPrediction> model) {
        int startID = 0;
        for (ActivityPrediction predictionNode : model.values()) {
            if (predictionNode.isStart()) {
                startID = predictionNode.getActivityID();
            }
        }
        return startID;
    }
    /**
     * returns the Node of the ActivityPrediction
     * @param model of the Process Mining Algorithm
     * @return the ID of the Node which is the End Activity
     */
    private static int getEndID(HashMap<Integer, ActivityPrediction> model) {
        int endID = 0;
        for (ActivityPrediction predictionNode : model.values()) {
            if (predictionNode.isEnd()) {
                endID = predictionNode.getActivityID();
            }
        }
        return endID;
    }

    /**
     * For a given activity ID look if it can be reached from the current node in the graph
     * looking at its followerPredictionEdgeMapMap
     * @param activityID which is tested to be a possible successor
     * @param followerPredictionEdgeMap the Edges that are possible successors
     * @return true if it is part of the possible successors
     */
    public static boolean nextActivityPossible(int activityID, Map<Integer, ActivityPredictionEdge> followerPredictionEdgeMap) {
        boolean possible = false;
        Set<Integer> possibleSuccessors = getPossibleSuccessors(followerPredictionEdgeMap);
            if (possibleSuccessors.contains(activityID)) {
                possible = true;
            } else {
            possible = false;
        }
        return possible;
    }

    /**
     *
     * @param item PredictionItem
     * @return next most probable activity
     */
    public static int getNextActivity(ActivityPrediction item){
        int next = 0;
        Set<Integer> possibleSuccessors = getPossibleSuccessors(item.getEdgeTargetMap());
        if(possibleSuccessors.size()>1){
            int temp = (int) (possibleSuccessors.size() * Math.random());
            next =  (int) possibleSuccessors.toArray()[temp];
        } else if(possibleSuccessors.size()==1){
            next = (int) possibleSuccessors.toArray()[0];
        } else {
            next = 9991;
        }
        return next;
    }

    /**
     * For a given activity ID look if it can be reached from the current node in the graph
     * looking at its followerPredictionEdgeMapMap
     * @param followerPredictionEdgeMap the Edges that are possible successors
     * @return all possible successors
     */
    public static Set<Integer> getPossibleSuccessors(Map<Integer, ActivityPredictionEdge> followerPredictionEdgeMap) {
        double edgeSignificance = 0.0;
        double edgeCorrelation = 0.0;
        double targetSignificance = 0.0;
        Set<Integer> possibleSuccessors = new HashSet<>();
        int targetID = 0;
            for (ActivityPredictionEdge edge : followerPredictionEdgeMap.values()) {
                if (edgeCorrelation < edge.getEdgeCorrelation()) {
                    edgeCorrelation = edge.getEdgeCorrelation();
                    edgeSignificance = edge.getEdgeSignificance();
                    targetSignificance = edge.getEdgeTargetSignificance();
                    targetID = edge.getModelID();
                    possibleSuccessors.clear();
                    possibleSuccessors.add(targetID);
                } else if (edgeCorrelation == edge.getEdgeCorrelation() && edgeSignificance < edge.getEdgeSignificance()) {
                    edgeCorrelation = edge.getEdgeCorrelation();
                    edgeSignificance = edge.getEdgeSignificance();
                    targetSignificance = edge.getEdgeTargetSignificance();
                    targetID = edge.getModelID();
                    possibleSuccessors.clear();
                    possibleSuccessors.add(targetID);
                } else if (edgeCorrelation == edge.getEdgeCorrelation() && edgeSignificance == edge.getEdgeSignificance() && targetSignificance < edge.getEdgeTargetSignificance()) {
                    edgeCorrelation = edge.getEdgeCorrelation();
                    edgeSignificance = edge.getEdgeSignificance();
                    targetSignificance = edge.getEdgeTargetSignificance();
                    targetID = edge.getModelID();
                    possibleSuccessors.clear();
                    possibleSuccessors.add(targetID);
                } else {
                    possibleSuccessors.add(targetID);
                }
            }
        return possibleSuccessors;
    }
}
