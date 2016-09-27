package uni.mannheim.teamproject.diabetesplaner.ProcessMining.HeuristicsMiner;

import android.support.v4.util.Pair;
import android.util.Log;

import org.deckfour.xes.classification.XEventAndClassifier;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventLifeTransClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.classification.XEventResourceClassifier;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uni.mannheim.teamproject.diabetesplaner.DataMining.Preprocessing.CustomXLog;
import uni.mannheim.teamproject.diabetesplaner.DataMining.ProcessMiningUtil;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityPrediction;
import uni.mannheim.teamproject.diabetesplaner.ProcessMining.HeuristicsMiner.models.heuristics.impl.ActivitiesMappingStructures;
import uni.mannheim.teamproject.diabetesplaner.ProcessMining.HeuristicsMiner.plugins.heuristicsnet.SimpleHeuristicsNet;
import uni.mannheim.teamproject.diabetesplaner.ProcessMining.HeuristicsMiner.plugins.heuristicsnet.miner.heuristics.HeuristicsMinerConstants;
import uni.mannheim.teamproject.diabetesplaner.ProcessMining.HeuristicsMiner.plugins.heuristicsnet.miner.heuristics.miner.HeuristicsMiner;
import uni.mannheim.teamproject.diabetesplaner.ProcessMining.HeuristicsMiner.plugins.heuristicsnet.miner.heuristics.miner.settings.HeuristicsMinerSettings;

/**
 * @author Jan
 * Used to connect the HeuristicsMiner with the UI and uses the HeuristicsMiner Framework
 */
public class HeuristicsMinerImplementation {

    private double relativeToBestThreshold = HeuristicsMinerConstants.RELATIVE_TO_BEST_THRESHOLD;
    private int positiveObeservationThreshold = HeuristicsMinerConstants.POSITIVE_OBSERVATIONS_THRESHOLD;
    private double dependencyThreshold = HeuristicsMinerConstants.DEPENDENCY_THRESHOLD;

    /***
     * Just for testing the HeuristicsMiner Implementation
     * @param args
     *
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
       HeuristicsMinerImplementation myHeuristics = new HeuristicsMinerImplementation();
        myHeuristics.mineTest();
   }

    public HeuristicsMinerImplementation()
    {
    }

    public HeuristicsMinerImplementation(Double DependencyThreshold, int PositiveObservationThreshold, Double RelativeToBestThreshold)
    {
        dependencyThreshold = DependencyThreshold;
        positiveObeservationThreshold = PositiveObservationThreshold;
        relativeToBestThreshold = RelativeToBestThreshold;
    }


    /***
     * Test of HeuristicsMiner Implementation
     */
    public void mineTest()
    {
        try {
            String logFile = new File("").getAbsolutePath();
            logFile = logFile + "/app/src/test/java/data/activityDataCompleteWithCasesJoined2.xes.gz";
            XesXmlGZIPParser xesXmlGZIPParser = new XesXmlGZIPParser();
            File file = new File(logFile);
            System.out.println(xesXmlGZIPParser.canParse(file));
            List<XLog> logs = xesXmlGZIPParser.parse(file);
            XLog log = logs.get(0);


            XLogInfo logInfo = XLogInfoFactory.createLogInfo(log);
            XEventClassifier classifier = logInfo.getEventClassifiers().iterator().next();
            HeuristicsMinerSettings hms = new HeuristicsMinerSettings();
            hms.setClassifier(classifier);

            XEventClasses EventClasses = XEventClasses.deriveEventClasses(classifier,log);
            ActivitiesMappingStructures struct = new ActivitiesMappingStructures(EventClasses);

            HeuristicsMiner miner = new HeuristicsMiner(log,logInfo,hms);
            miner.mine();


            ArrayList<ActivityPrediction> HeuristicsOutput = createPredictionDataStructure(miner.getSimpleNet2());
            System.out.println();
            //    SimpleHeuristicsNet HMNet = new SimpleHeuristicsNet(miner.makeBasicRelations(new HeuristicsMetrics(logInfo)),new HeuristicsMetrics(logInfo),hms);
            //  HeuristicsNetImpl HeuristicsNet = new HeuristicsNetImpl(struct);
        }catch (Exception e)
        {
            e.getMessage();
        }
    }

    /***
     * Sets the output of the HeuristicsMiner into the defined Datastructure of ActivityPrediction.
     *
     * Can be used for Future Work.
     * @param SimpleNet
     * @return ArrayList of ActivityPrediction objects
     */
    private ArrayList<ActivityPrediction> createPredictionDataStructure(SimpleHeuristicsNet SimpleNet)
    {
        ArrayList<ActivityPrediction> myPrediction = new ArrayList<ActivityPrediction>();
        for(int i = 0;i< SimpleNet.getActivitiesMappingStructures().getActivitiesMapping().length; i++)
        {
            ActivityPrediction PredictElement = new ActivityPrediction(i,0,SimpleNet.getActivitiesMappingStructures().getActivitiesMapping()[i].getId());
            PredictElement.setFollowerProbabilityMap(SimpleNet.getMetrics().getDependencyMeasuresAcceptedElement(i));


            if(i == SimpleNet.getMetrics().getBestStart())
            {
                PredictElement.setStart(true);
            }
            else if(i == SimpleNet.getMetrics().getBestEnd())
            {
                PredictElement.setEnd(true);
            }

            myPrediction.add(PredictElement);
        }
        return myPrediction;
    }

    /***
     * For Testing HeuristicsMiner with different DB function
     * @param train
     * @return
     */
    public ArrayList<ActivityItem> runHeuristicsMinerTest(ArrayList<ActivityItem> train)
    {

        return HeuristicsMining(new CustomXLog(train));
    }

    /***
     * runs the HeuristicsMining Algorithm
     * @param xLogInput
     * @return
     */
    private ArrayList<ActivityItem> HeuristicsMining(CustomXLog xLogInput)
    {
        try {
            if (xLogInput == null) {
                return null;
            } else {

                CustomXLog xLog = xLogInput;
                XLog log = xLog.getXLog();
                ArrayList<String[]> cases = xLog.getCases();
                ArrayList<String[]> eventList = xLog.getEventList();
                cases.remove(0);
                eventList.remove(0);

                XEventAttributeClassifier attrClass = new XEventAndClassifier(new XEventNameClassifier(), new XEventLifeTransClassifier());
                XEventClassifier defaultClassifier = attrClass;

                XLogInfo logInfo = new XLogInfoImpl(log, defaultClassifier, log.getClassifiers());
                //Set settings of the HeuristicsMiner
                //XLogInfo logInfo2 = XLogInfoFactory.createLogInfo(log);

                XEventClassifier classifier = logInfo.getEventClassifiers().iterator().next();

                HeuristicsMinerSettings hms = new HeuristicsMinerSettings();
                hms.setPositiveObservationThreshold(positiveObeservationThreshold);
                hms.setDependencyThreshold(dependencyThreshold);
                hms.setRelativeToBestThreshold(relativeToBestThreshold);
                hms.setClassifier(defaultClassifier);

               // XEventClasses EventClasses = XEventClasses.deriveEventClasses(defaultClassifier, log);
                //ActivitiesMappingStructures struct = new ActivitiesMappingStructures(EventClasses);

                //run the HeuristicsMiner
                HeuristicsMiner miner = new HeuristicsMiner(log,hms);
                miner.mine();
                SimpleHeuristicsNet myNet = miner.getSimpleNet2();

                int StartId = -1;
                int EndId = -1;
                int ActivityID;
                int ActivitySubID;
                String ID_String;
                int completeID = -1;
                HashMap<Integer, ArrayList<Integer>> ResolveTrace = new HashMap<>();
                Map<Integer, Double> durationMap;
                durationMap = ProcessMiningUtil.getAverageDurations(eventList);

                StartId = myNet.getMetrics().getBestOutputEvent(myNet.getMetrics().getBestOutputEvent(myNet.getMetrics().getBestOutputEvent(myNet.getMetrics().getBestStart())));
                //StartId = myNet.getActivitiesMappingStructures().getReverseActivitiesMapping().get(
                  //      new XEventClass(String.valueOf(ProcessMiningUtil.getMostFrequentStartActivity())+"+Complete", 0)).get(0);

                if(myNet.getEndActivities().size() == 1)
                {
                    EndId = myNet.getEndActivities().get(0);

                }
                int currentID = StartId;
                if(currentID == EndId)
                {
                    return null;
                }

                List<Pair<Integer,Double>> idDurationMap = new ArrayList<>();
                //determine nextID of the graph
                //nextID = myNet.getMetrics().getBestOutputEvent(myNet.getMetrics().getBestOutputEvent(nextID));
                //split next ActivityID
                ID_String = myNet.getActivitiesMappingStructures().getActivitiesMapping()[StartId].getId().split("\\+")[0];
                ActivityID = ProcessMiningUtil.splitID(Integer.parseInt(ID_String))[0];
                ActivitySubID = ProcessMiningUtil.splitID(Integer.parseInt(ID_String))[1];
                completeID = Integer.parseInt(ID_String);

                ResolveTrace.put(StartId,new ArrayList<Integer>());
//                int nextStartID = myNet.getMetrics().getBestOutputEvent(myNet.getMetrics().getBestOutputEvent(currentID));
//                ArrayList<Integer> tempArray = ResolveTrace.get(StartId);
//                tempArray.add(nextStartID);
//                ResolveTrace.put(StartId, tempArray);

                int count = 0;
                while (!ProcessMiningUtil.isTotalDurationReached(idDurationMap)) {

                   /* //loop detected
                    if(ResolveTrace.get(nextID) == nextID)
                    {
                        nextID = myNet.getMetrics().getBestOutputEvent(myNet.getMetrics().getBestOutputEvent(nextID));
                        //Loop which will never end
                        if((myNet.getMetrics().getOutputSet(nextID).size() == 1) && myNet.getMetrics().getOutputSet(pre_id).size() == 1)
                            return ProcessMiningUtil.createActivities(idDurationMap,false);

                        //Take the second highest dependecy measure as ID of next Event
                        nextID = myNet.getMetrics().getBestOutputEvent(getNextIdIfLoop(nextID,pre_id,myNet));
                    }*/
                    //store predecessor ID
                    count++;
                    //add to duration map to check if day is finished
                    idDurationMap.add(new Pair<Integer, Double>(completeID, durationMap.get(completeID)));


                    if(!ResolveTrace.containsKey(currentID))
                    {
                        ResolveTrace.put(currentID,new ArrayList<Integer>());
                    }
                    ArrayList<Integer> tempArray = ResolveTrace.get(currentID);

                    //Put next Successor to the Successorlist of current ID
                    tempArray = ResolveTrace.get(currentID);
                    int nextID = myNet.getMetrics().getBestOutputEvent(myNet.getMetrics().getBestOutputEvent(currentID));
                    //if new successor is already in successor list, search for a new one
                    if(tempArray.contains(nextID)) {
                        try {
                            //if no new ID can be found, return the current Duration map as Activities
                            nextID = getNextIdIfLoop(currentID,nextID,tempArray,myNet);
                        }catch (Exception e)
                        {
                            Log.d("HeuristicsMiner", "HM calculation done!--------------------------------");
                            return ProcessMiningUtil.createActivities(idDurationMap,false);
                        }

                        tempArray.add(nextID);
                    }else
                    {
                        tempArray.add(nextID);
                    }
                    ResolveTrace.put(currentID, tempArray);


                    //determine nextID of the graph
                    //currentID = myNet.getMetrics().getBestOutputEvent(myNet.getMetrics().getBestOutputEvent(currentID));
                    currentID = nextID;
                    //split next ActivityID
                    ID_String = myNet.getActivitiesMappingStructures().getActivitiesMapping()[currentID].getId().split("\\+")[0];
                    ActivityID = ProcessMiningUtil.splitID(Integer.parseInt(ID_String))[0];
                    ActivitySubID = ProcessMiningUtil.splitID(Integer.parseInt(ID_String))[1];
                    completeID = Integer.parseInt(ID_String);
                    if(completeID == 9991)
                    {
                        break;
                    }
                }

                Log.d("HeuristicsMiner", "HM calculation done!--------------------------------");
                return ProcessMiningUtil.createActivities(idDurationMap,false);

            }
        } catch (Exception e) {
            Log.d("HeuristicsMiner", e.getMessage());
            return null;
        }
    }

    /***
     * returns the secondhighest measure_id of a given row_id
     * @param currentID
     * @param nextID
     * @param followerArray
     *@param myNet  @return
     */
    private int getNextIdIfLoop(int currentID, int nextID, ArrayList<Integer> followerArray, SimpleHeuristicsNet myNet) {
        TreeMap<Double, Integer> idMeasureMap = new TreeMap<Double, Integer>();
        //Decide betweend nextID and pre_ID
        if ((myNet.getOutputSet(currentID).get(0).size() == 1) && (myNet.getOutputSet(nextID).get(0).size() > 1)) {
            for (int row = 0; row < myNet.getOutputSet(nextID).get(0).size(); row++) {
                if(!followerArray.contains(myNet.getOutputSet(nextID).get(0).get(row)-1)) {
                    double new_measure = myNet.getMetrics().getDependencyMeasuresAccepted().getQuick(currentID, myNet.getOutputSet(currentID).get(0).get(row));

                    if(idMeasureMap.containsKey(new_measure)) {
                        //Flip coin if new ID is used
                        if ((Math.random()*100+1) % 2 < 1) {
                            idMeasureMap.put(myNet.getMetrics().getDependencyMeasuresAccepted().getQuick(nextID, myNet.getOutputSet(nextID).get(0).get(row)), myNet.getOutputSet(nextID).get(0).get(row));
                        }
                    }
                    else
                    {
                        idMeasureMap.put(myNet.getMetrics().getDependencyMeasuresAccepted().getQuick(nextID, myNet.getOutputSet(nextID).get(0).get(row)), myNet.getOutputSet(nextID).get(0).get(row));
                    }
                }
            }
        }
        else {

            for (int row = 0; row < myNet.getOutputSet(currentID).get(0).size(); row++) {
                //check if new ID is not the ID of the loop
                int future_id = myNet.getOutputSet(currentID).get(0).get(row)-1;
                if(!followerArray.contains(future_id)) {
                    double new_measure = myNet.getMetrics().getDependencyMeasuresAccepted().getQuick(currentID, future_id+1);
                    idMeasureMap.put(new_measure, future_id);

                }
            }
        }
        //return the last key
        return idMeasureMap.lastEntry().getValue();
    }

    /***
     * Runs the HeuristicsMiner and sets the output for the DailyRoutine Fragment
     * @param train
     * @return
     */
    public ArrayList<ActivityItem> runHeuristicsMiner(ArrayList<ArrayList<ActivityItem>> train) {
        return HeuristicsMining(new CustomXLog(ProcessMiningUtil.convertDayToALStructure(train)));
    }




}
