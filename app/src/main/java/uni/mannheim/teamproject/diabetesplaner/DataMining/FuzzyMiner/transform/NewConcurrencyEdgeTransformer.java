package uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.transform;


import uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.Default.MutableFuzzyGraph;

public class NewConcurrencyEdgeTransformer extends FuzzyGraphTransformer {
	protected MutableFuzzyGraph graph;
	protected double preserveThreshold;
	protected double ratioThreshold;
	protected int counterParallelized;
	protected int counterResolved;

	public NewConcurrencyEdgeTransformer() {
		super("Concurrency edge transformer");
		this.graph = null;
		this.preserveThreshold = 0.4;
		this.ratioThreshold = 0.7;
	}

	public void setPreserveThreshold(double threshold) {
		preserveThreshold = threshold;
	}

	public void setRatioThreshold(double threshold) {
		ratioThreshold = threshold;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.mining.fuzzymining.graph.transform.FuzzyGraphTransformer
	 * #transform(org.processmining.mining.fuzzymining.graph.FuzzyGraph)
	 */
	public void transform(MutableFuzzyGraph graph) {
		counterParallelized = 0;
		counterResolved = 0;
		this.graph = graph;
		int numberOfNodes = graph.getNumberOfInitialNodes();
		for (int x = 0; x < numberOfNodes; x++) {
			for (int y = 0; y < x; y++) {
				processRelationPair(x, y);
			}
		}
//		context.log("    Concurrency edge transformer: Resolved " + counterResolved + " conflicts, parallelized "
//				+ counterParallelized + " node pairs.", MessageLevel.NORMAL);
	}

	protected void processRelationPair(int indexA, int indexB) {
		double relImpAB = graph.getBinaryRespectiveSignificance(indexA, indexB);
		double relImpBA = graph.getBinaryRespectiveSignificance(indexB, indexA);
		if (relImpAB > 0.0 && relImpBA > 0.0) {
			// conflict situation
			if (relImpAB > preserveThreshold && relImpBA > preserveThreshold) {
				// preserve both links, as they are sufficiently locally important
			} else {
				// investigate ratio of local importance between both relations
				double ratio = Math.min(relImpAB, relImpBA) / Math.max(relImpAB, relImpBA);
				if (ratio < ratioThreshold) {
					// preserve locally more important relation
					if (relImpAB > relImpBA) {
						// erase B -> A
						graph.setBinarySignificance(indexB, indexA, 0.0);
						graph.setBinaryCorrelation(indexB, indexA, 0.0);
					} else {
						// erase A -> B
						graph.setBinarySignificance(indexA, indexB, 0.0);
						graph.setBinaryCorrelation(indexA, indexB, 0.0);
					}
					counterResolved++;
				} else {
					// erase both.
					// erase A -> B
					graph.setBinarySignificance(indexA, indexB, 0.0);
					graph.setBinaryCorrelation(indexA, indexB, 0.0);
					// erase B -> A
					graph.setBinarySignificance(indexB, indexA, 0.0);
					graph.setBinaryCorrelation(indexB, indexA, 0.0);
					counterParallelized++;
				}
			}
		}
	}

	protected double getRelativeImportanceForEndNodes(int fromIndex, int toIndex) {
		double sigRef = graph.getBinarySignificance(fromIndex, toIndex);
		// accumulate all outgoing significances of source node, and
		// all incoming significances of target node, respectively.
		double sigSourceOutAcc = 0.0;
		double sigTargetInAcc = 0.0;
		for (int i = graph.getNumberOfInitialNodes() - 1; i >= 0; i--) {
			if (i != fromIndex) { // ignore self-loops in calculation
				sigSourceOutAcc += graph.getBinarySignificance(fromIndex, i);
			}
			if (i != toIndex) { // ignore self-loops in calculation
				sigTargetInAcc += graph.getBinarySignificance(i, toIndex);
			}
		}
		// relative importance is the product of the relative significances
		// within the source's outgoing and the target's incoming links
		double relativeImportance = ((sigRef / sigSourceOutAcc) + (sigRef / sigTargetInAcc)) / 2.0;
		return relativeImportance;
	}

}
