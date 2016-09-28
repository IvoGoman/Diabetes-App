package uni.mannheim.teamproject.diabetesplaner.Domain;

/**
 * Created by Ivo on 9/27/2016.
 */
public class ActivityPredictionEdge {
    private int activityID;
    private int subActivityID;
    private int modelID;
    private String targetName;
    private double edgeSignificance;
    private double edgeCorrelation;
    private double edgeTargetSignificance;

    public ActivityPredictionEdge(int modelID, double edgeSignificance ){
        this.modelID = modelID;
        this.edgeSignificance = edgeSignificance;
        this.edgeSignificance = 0.0;
        this.edgeTargetSignificance = 0.0;
    }
    public ActivityPredictionEdge(int modelID, double edgeSignificance, double edgeCorrelation, double edgeTargetSignificance){
        this.modelID = modelID;
        this.edgeSignificance = edgeSignificance;
        this.edgeCorrelation = edgeCorrelation;
        this.edgeTargetSignificance = edgeTargetSignificance;
    }

    public int getModelID() {
        return modelID;
    }

    public double getEdgeSignificance() {
        return edgeSignificance;
    }

    public double getEdgeCorrelation() {
        return edgeCorrelation;
    }

    public double getEdgeTargetSignificance() {
        return edgeTargetSignificance;
    }
}
