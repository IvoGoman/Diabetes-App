package uni.mannheim.teamproject.diabetesplaner.DataMining;

import android.support.v4.util.Pair;

import org.deckfour.xes.model.XLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.Default.FMEdge;
import uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.Default.FMNode;
import uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.Default.FuzzyMinerImpl;
import uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.Default.MutableFuzzyGraph;
import uni.mannheim.teamproject.diabetesplaner.DataMining.Preprocessing.CustomXLog;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;

/**
 * Created by Ivo on 10.07.2016.
 * Creates a Fuzzy Graph based on a XLog created from the Activities of the Database.
 * Allows to find the next Activity based on the current one.
 */
public class FuzzyModel {
    private MutableFuzzyGraph fuzzyMinerModel;

    public FuzzyModel() throws Exception {
//        DummyDataCreator.createDummyData();
        ArrayList<ActivityItem> items = AppGlobal.getHandler().getAllActivities(AppGlobal.getHandler());
        XLog xLog = CustomXLog.createXLog(items);
        FuzzyMinerImpl fuzzyMiner = new FuzzyMinerImpl(xLog);
        fuzzyMinerModel = fuzzyMiner.getFuzzyGraph();
        List<Pair<Integer, Double>> idDurationMap = new ArrayList<>();
        Map<Integer, Double> durationMap = ProcessMiningUtill.getAverageDurationForActivityID();
        int currentId = ProcessMiningUtill.getMostFrequentStartActivity();
        idDurationMap.add(new Pair<Integer, Double>(currentId, durationMap.get(currentId)));
        while (ProcessMiningUtill.getTotalDuration(idDurationMap)) {
            currentId = getNextActivity(currentId);
            idDurationMap.add(new Pair<Integer, Double>(currentId, durationMap.get(currentId)));
        }
        idDurationMap.size();
    }

    /**
     * Return the most likely successor of the current activity based on the significance in the model
     *
     * @param currentActivityId
     * @return
     */
    public int getNextActivity(int currentActivityId) {
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
            if (edge.getSignificance() > successorSignificance) {
                target = (FMNode) edge.getTarget();
                if (target.getEventType().equals("Start")) {
                    successorID = Integer.parseInt(target.getElementName());
                    successorSignificance = edge.getSignificance();
                    successorCorrelation = edge.getCorrelation();
                }
            }
        }
        return successorID;
    }
}
