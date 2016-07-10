package uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.Entities;

import java.util.ArrayList;

/**
 * Created by Ivo on 4/23/2016.
 * Node representing a cluster of ActivityNodes.
 * ClusterNodes are important to preserve the logical structure.
 * The contained nodes are of no interest.
 */
public class ClusterNode extends Node {
    private ArrayList<Node> childrenNodes;

    public ClusterNode(int clusterID, long startTime, long endTime) {
        super(clusterID, startTime, endTime);
    }

    public ArrayList<Node> getChildrenNodes() {
        return childrenNodes;
    }

    public void addChildrenNode(Node childrenNode) {
        this.childrenNodes.add(childrenNode);
    }
}
