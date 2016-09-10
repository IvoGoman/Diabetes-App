package uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.transform;


import uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.Default.FMClusterNode;
import uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.Default.FMNode;
import uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.Default.MutableFuzzyGraph;

public class TransformerUtils {
	public static String getNodeLabel(FMNode fmNode) {
		String label;
		if (fmNode instanceof FMClusterNode) {
			FMClusterNode clusterNode = (FMClusterNode) fmNode;
			label = "<html>" + clusterNode.id() + "<br>" + Integer.toString(clusterNode.size()) + " elements " + "<br>"
					+ "~" + MutableFuzzyGraph.format(clusterNode.getSignificance()) + "<html>";
		} else {

			label = "<html>" + fmNode.getElementName() + "<br>" + fmNode.getEventType() + "<br>"
					+ MutableFuzzyGraph.format(fmNode.getSignificance()) + "<html>";
		}
		return label;

	}

}
