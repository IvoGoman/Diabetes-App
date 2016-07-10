package uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.impl;


import uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.Default.FMEdge;
import uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.Default.FMNode;

public class FMEdgeImpl extends FMEdge<FMNode, FMNode> {
	public FMEdgeImpl(FMNode source, FMNode target, double significance, double correlation) {

		super(source, target, significance, correlation);
//		getAttributeMap().put(AttributeMap.EDGEEND, ArrowType.ARROWTYPE_TECHNICAL);
//		getAttributeMap().put(AttributeMap.EDGEENDFILLED, true);
	}

	public String toString() {
		String sourceLabel = source.getElementName() + "(" + source.getEventType() + ")";
		String targetLabel = target.getElementName() + "(" + target.getEventType() + ")";
		String edgeLabel = sourceLabel + "-->" + targetLabel;
		return edgeLabel;
	}
}
