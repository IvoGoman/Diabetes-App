package uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.metrics;


import java.util.ArrayList;
import java.util.HashMap;

import uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.Entities.Node;

/**
 * Created by Ivo on 4/20/2016.
 * Computes the Unary Significance of a Activity Node
 * TODO: Implement Frequency Significance.
 * TODO: Test Frequency Significance. Should be implemented.
 * TODO: Implement Routing Significance
 */
public class UnarySignificance {

    public static HashMap getFrequencies(ArrayList<Node> nodes) {
        HashMap<Integer, Double> frequencyMap = new HashMap();
        double frequency = 0.0;
        double temp;
//        count the frequencies of each event class meaning each distinct ActivityID
        for (Node n : nodes) {
            if (frequencyMap.containsKey(n.getActivityID())) {
                frequencyMap.put(n.getActivityID(), frequencyMap.get(n.getActivityID()) + 1);
            } else {
                frequencyMap.put(n.getActivityID(), 1.0);
            }
        }
        int size = frequencyMap.size();
        System.out.println(size);
//      calculate the frequencies and set them for every Activity Node
//        for (Node n : nodes) {
//            temp = frequencyMap.get(n.getActivityID());
////            frequency = temp / size;
//            frequencyMap.put(n.getActivityID(), temp);
//        }
        return frequencyMap;
    }

    public static ArrayList<Node> getFrequencySignificance(ArrayList<Node> nodes) {
        HashMap<Integer, Double> frequencyMap = getFrequencies(nodes);
        Double maxFrequency = 0.0;
        Double significance = 0.0;
        for (Integer i : frequencyMap.keySet()) {
            if (maxFrequency < i) {
                maxFrequency = frequencyMap.get(i);
            }
        }
        System.out.println("Max Frequency: "+maxFrequency);
        for (Node n : nodes) {
            significance = (frequencyMap.get(n.getActivityID()) / maxFrequency);
            n.setUnarySignificance(significance);
        }
        return nodes;
    }


}
