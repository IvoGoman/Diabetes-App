package uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner;

import android.util.Pair;

import org.deckfour.xes.model.XLog;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Default.FMNode;
import Default.FuzzyMinerImpl;
import Default.MutableFuzzyGraph;
import uni.mannheim.teamproject.diabetesplaner.DataMining.Preprocessing.CustomXLog;

/**
 * Created by Ivo on 10.07.2016.
 * Creates a Fuzzy Graph based on a XLog created from the Activities of the Database.
 * Allows to find the next Activity based on the current one.
 */
public class FuzzyModel {
    private MutableFuzzyGraph fuzzyMinerModel;
    public FuzzyModel() {
        XLog xLog = CustomXLog.createXLog();
        FuzzyMinerImpl fuzzyMiner = new FuzzyMinerImpl(xLog);
        fuzzyMinerModel = fuzzyMiner.getFuzzyGraph();
    }
    public int getNextActivity(){
        Set<FMNode> nodes = fuzzyMinerModel.getNodes();
        int currentActivity = ProcessMiningUtill.getCurrentActivityID();
        Set<FMNode> likelySuccessors = new HashSet<>();
        Set<FMNode> interestingNodes = new HashSet<>();
//        Retrieve all nodes from the model which have the same ID and are the End of that activity
        for (FMNode node : nodes) {
            if(node.getElementName().equals(currentActivity) && node.getEventType().equals("Complete")){
               likelySuccessors.addAll(node.getSuccessors());
            }
        }
        int successorID = 0;
        double successorSignificance = 0.0;
        for (FMNode node: likelySuccessors) {
         if(node.getSignificance()>successorSignificance){
             successorID = Integer.getInteger(node.getElementName());
             successorSignificance = node.getSignificance();
         }


        }
    return successorID;
    }
}
