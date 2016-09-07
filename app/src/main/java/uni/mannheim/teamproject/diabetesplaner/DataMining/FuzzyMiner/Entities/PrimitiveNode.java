package uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.Entities;

/**
 * Created by Ivo on 4/20/2016.
 * Class is representing a Node in the Model
 * A Node corresponds to a Event from the Log
 */
public class PrimitiveNode extends Node {

    public PrimitiveNode(int activityID, long startTime, long endTime) {
        super(activityID, startTime, endTime);
    }
}
