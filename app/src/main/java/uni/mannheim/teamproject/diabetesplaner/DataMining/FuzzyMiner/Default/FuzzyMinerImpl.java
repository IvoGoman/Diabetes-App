package uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.Default;

import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;

import uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.attenuation.Attenuation;
import uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.attenuation.LinearAttenuation;
import uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.metrics.MetricsRepository;
import uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.metrics.unary.UnaryMetric;
import uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.transform.BestEdgeTransformer;
import uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.transform.FuzzyEdgeTransformer;
import uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.transform.NewConcurrencyEdgeTransformer;
import uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.util.FMLogEvents;
import uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.util.FuzzyMinerLog;


/**
 * Created by Ivo on 4/25/2016.
 *
 * Class to use the Implementation of Fuzzy Miner
 */
public class FuzzyMinerImpl {
    private MutableFuzzyGraph fuzzyGraph;

    public MutableFuzzyGraph getFuzzyGraph() {
        return fuzzyGraph;
    }

    public void setFuzzyGraph(MutableFuzzyGraph fuzzyGraph) {
        this.fuzzyGraph = fuzzyGraph;
    }

    public FuzzyMinerImpl(XLog log){
        FMLogEvents logEvents = FuzzyMinerLog.getLogEvents(log);

        MetricsRepository repository;
        Attenuation attenuation = new LinearAttenuation(8,2);
//      Create XLogInfo from the XLog and use it to create the Metric Repository
//      Metric Repository will initialize all metrics based on XLogInfo
        XLogInfo logInfo = XLogInfoFactory.createLogInfo(log);
//        create a metrics repository and create the metrics with apply
        repository = MetricsRepository.createRepository(logInfo);
        repository.apply(log,attenuation,3);
//      Create a Fuzzy Graph based on the metrics Repository, Log, LogEvents, set Fuzzy Map to false
        UnaryMetric unaryMetric = repository.getAggregateUnaryMetric();
        fuzzyGraph = new MutableFuzzyGraph(repository.getUnaryMetrics().get(0),repository.getAggregateSignificanceBinaryLogMetric(),repository.getAggregateCorrelationBinaryLogMetric(), log, logEvents, false);
        fuzzyGraph.setBinaryRespectiveSignificance();
//      Add Edges based on Nodes and metrics
        fuzzyGraph.setEdgeImpls();


//  Conflict Resolution Step [Checking for Self-loops etc.

//        Either Fuzzy Edge Transformer
        FuzzyEdgeTransformer edgeTransformer = new FuzzyEdgeTransformer();
        edgeTransformer.setIgnoreSelfLoops(true);
        edgeTransformer.setInterpretPercentageAbsolute(true);
        edgeTransformer.setSignificanceCorrelationRatio(0.8);
        edgeTransformer.setPreservePercentage(0.3);
        edgeTransformer.transform(fuzzyGraph);

//        Or Best Edge Transformer
//        BestEdgeTransformer edgeTransformer = new BestEdgeTransformer();
//        edgeTransformer.transform(fuzzyGraph);

//  Edge Filtering based on local utility [Regarding Utility of In- and Outgoing Edges]

//        Either Concurrency Edge Transformer
//        ConcurrencyEdgeTransformer concurrencyEdgeTransformer = new ConcurrencyEdgeTransformer();
//        concurrencyEdgeTransformer.setPreserveThreshold(0.1);
//        concurrencyEdgeTransformer.setRatioThreshold(0.1);
//        concurrencyEdgeTransformer.transform(fuzzyGraph);

//        Or New Concurrency Edge Transformer
        NewConcurrencyEdgeTransformer concurrencyEdgeTransformer = new NewConcurrencyEdgeTransformer();
        concurrencyEdgeTransformer.setPreserveThreshold(0.2);
        concurrencyEdgeTransformer.setRatioThreshold(0.9);
        concurrencyEdgeTransformer.transform(fuzzyGraph);

//  Node Aggregation and Abstraction Step [Clustering Nodes]

//        Either Simple Transformer
//        SimpleTransformer transformer = new SimpleTransformer();
//      smaller threshold more cluster nodes, bigger threshold fewer cluster nodes
//        transformer.setThreshold(0.5);
//        transformer.transform(fuzzyGraph);

//        Or Fast Transfomer
//        FastTransformer aggregationTransformer = new FastTransformer();
//        aggregationTransformer.setThreshold(0.5);
//        Ability to add more transformer as internal transformers. Not sure why
//        aggregationTransformer.addPreTransformer();
//        aggregationTransformer.addInterimTransformer();
//        aggregationTransformer.addPostTransformer();
//        aggregationTransformer.transform(fuzzyGraph);

//        Always trowing null pointer exception when called after transformations are done.
//        StatisticalCleanupTransformer statisticalCleanupTransformer = new StatisticalCleanupTransformer();
//        statisticalCleanupTransformer.transform(fuzzyGraph);
//        fuzzyGraph.toString();
//        String source = ".\\input\\ActivityData.csv";
//        String target = ".\\input\\ActivityDataLog.csv";
//        ArrayList<String[]> csvList = Util.read(source);
//        CaseCreator caseCreator = new CaseCreator(csvList);
//        caseCreator.saveActivityWithCases(".\\input\\ActivityData.csv",".\\input\\ActivityDataLog.csv");
//        csvList= caseCreator.getActivityListWithCases(source);
//        Util.print(csvList);
//        Util.write(csvList,target);

    }

}
