package uni.mannheim.teamproject.diabetesplaner.ProcessMining.HeuristicsMiner;

import android.support.v4.util.Pair;
import android.util.Log;

import org.deckfour.xes.classification.XEventAndClassifier;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventLifeTransClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import uni.mannheim.teamproject.diabetesplaner.DataMining.Preprocessing.CustomXLog;
import uni.mannheim.teamproject.diabetesplaner.DataMining.ProcessMiningUtil;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityPrediction;
import uni.mannheim.teamproject.diabetesplaner.ProcessMining.HeuristicsMiner.models.heuristics.impl.ActivitiesMappingStructures;
import uni.mannheim.teamproject.diabetesplaner.ProcessMining.HeuristicsMiner.plugins.heuristicsnet.SimpleHeuristicsNet;
import uni.mannheim.teamproject.diabetesplaner.ProcessMining.HeuristicsMiner.plugins.heuristicsnet.miner.heuristics.HeuristicsMinerConstants;
import uni.mannheim.teamproject.diabetesplaner.ProcessMining.HeuristicsMiner.plugins.heuristicsnet.miner.heuristics.miner.HeuristicsMiner;
import uni.mannheim.teamproject.diabetesplaner.ProcessMining.HeuristicsMiner.plugins.heuristicsnet.miner.heuristics.miner.settings.HeuristicsMinerSettings;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;

/**
 * @author Jan
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

                XEventClassifier nameCl = new XEventNameClassifier();
                XEventClassifier lifeTransCl = new XEventLifeTransClassifier();
                XEventAttributeClassifier attrClass = new XEventAndClassifier(nameCl, lifeTransCl);
                XEventClassifier defaultClassifier = attrClass;

                XLogInfo logInfo = new XLogInfoImpl(log, defaultClassifier, log.getClassifiers());
                //Set settings of the HeuristicsMiner
                XLogInfo logInfo2 = XLogInfoFactory.createLogInfo(log);

                XEventClassifier classifier = logInfo.getEventClassifiers().iterator().next();

                HeuristicsMinerSettings hms = new HeuristicsMinerSettings();
                hms.setPositiveObservationThreshold(positiveObeservationThreshold);
                hms.setDependencyThreshold(dependencyThreshold);
                hms.setRelativeToBestThreshold(relativeToBestThreshold);
                hms.setClassifier(defaultClassifier);

                XEventClasses EventClasses = XEventClasses.deriveEventClasses(defaultClassifier, log);
                ActivitiesMappingStructures struct = new ActivitiesMappingStructures(EventClasses);

                //run the HeuristicsMiner
                HeuristicsMiner miner = new HeuristicsMiner(log, logInfo, hms);
                miner.mine();
                SimpleHeuristicsNet myNet = miner.getSimpleNet2();

                ArrayList<ActivityItem> PredictedActivities = new ArrayList<ActivityItem>();

                int StartId = -1;
                int EndId = -1;
                int ActivityID;
                int ActivitySubID;
                String ID_String;
                int completeID;
                ActivityItem StartElement = new ActivityItem(-1,-1);
                Map<Integer, Double> durationMap;
                durationMap = ProcessMiningUtil.getAverageDurations(eventList);


                //Get Start and EndId
                if(myNet.getStartActivities().size() == 1) {
                    StartId = myNet.getStartActivities().get(0);

                    //Set Start Activity as first item for PredictedActivity List

                    ID_String = myNet.getActivitiesMappingStructures().getActivitiesMapping()[StartId].getId().split("\\+")[0];

                    ActivityID = ProcessMiningUtil.splitID(Integer.parseInt(ID_String))[0];
                    ActivitySubID = ProcessMiningUtil.splitID(Integer.parseInt(ID_String))[1];
                    completeID = Integer.parseInt(ID_String);
                    StartElement.setActivityId(ActivityID);
                    StartElement.setSubactivityId(ActivitySubID);

                    StartElement.setDuration(durationMap.get(completeID));
                    PredictedActivities.add(StartElement);
                }
                else
                {


                }
                if(myNet.getEndActivities().size() == 1)
                {
                    EndId = myNet.getEndActivities().get(0);

                }
                else
                {

                }
                int nextID = StartId;
                if(nextID == EndId)
                {
                    return null;
                }

                List<Pair<Integer,Double>> idDurationMap = new ArrayList<>();

                while (!ProcessMiningUtil.isTotalDurationReached(idDurationMap)) {
                    //determine nextID of the graph
                    nextID = myNet.getMetrics().getBestOutputEvent(myNet.getMetrics().getBestOutputEvent(nextID));

                    //split next ActivityID
                    ID_String = myNet.getActivitiesMappingStructures().getActivitiesMapping()[nextID].getId().split("\\+")[0];
                    ActivityID = ProcessMiningUtil.splitID(Integer.parseInt(ID_String))[0];
                    ActivitySubID = ProcessMiningUtil.splitID(Integer.parseInt(ID_String))[1];
                    completeID = Integer.parseInt(ID_String);

                    //add to duration map to check if day is finished
                    idDurationMap.add(new Pair<Integer, Double>(completeID, durationMap.get(completeID)));

                    //Add next ActivityID as ActivityItem
                    ActivityItem PredictElement = new ActivityItem(ActivityID, ActivitySubID, durationMap.get(completeID));
                    //Add it to result list
                    PredictedActivities.add(PredictElement);
                }

                //Prepare result for the UI
                //Go through all activities, beginning at the start activity and continue with the next activity ActivityID with the highest probability.
                for (int i = 1; i < myNet.getActivitiesMappingStructures().getActivitiesMapping().length; i = i+2) {
                    ActivityItem PredictElement = new ActivityItem(-1, -1);
                    if(i != 1) {
                        nextID = myNet.getMetrics().getBestOutputEvent(nextID);
                    }
                    if(nextID != EndId) {
                        ID_String = myNet.getActivitiesMappingStructures().getActivitiesMapping()[nextID].getId().split("\\+")[0];

                        if (ID_String.contentEquals("Start//Start") == false) {
                            //resolve ActivityID and SubId

                            ActivityID = ProcessMiningUtil.splitID(Integer.parseInt(ID_String))[0];
                            ActivitySubID = ProcessMiningUtil.splitID(Integer.parseInt(ID_String))[1];
                            completeID = Integer.parseInt(ID_String);
                            PredictElement.setActivityId(ActivityID);
                            PredictElement.setSubactivityId(ActivitySubID);

                            PredictElement.setDuration(durationMap.get(completeID));

                            PredictedActivities.add(PredictElement);
                        }
                    }
                    else
                    {
                        //Only start and end activity were predicted
                        if(i == 1)
                        {
                            return null;
                        }
                        ID_String = myNet.getActivitiesMappingStructures().getActivitiesMapping()[EndId].getId().split("\\+")[0];
                        if (ID_String.length() == 3) {
                            ActivityID = Integer.parseInt(ID_String.substring(0, 2));
                            ActivitySubID = Integer.parseInt(ID_String.substring(2));
                            completeID = Integer.parseInt(ID_String);
                            PredictElement.setActivityId(ActivityID);
                            PredictElement.setSubactivityId(ActivitySubID);
                        } else {
                            ActivityID = Integer.parseInt(ID_String);
                            ActivitySubID = 0;
                            completeID = ActivityID;
                            PredictElement.setActivityId(ActivityID);
                            PredictElement.setSubactivityId(ActivitySubID);
                        }
                        PredictElement.setDuration(durationMap.get(completeID));
                        PredictedActivities.add(PredictElement);
                        return PredictedActivities;
                    }


                }

                //Check if result is meaningful
                if (PredictedActivities.size() > 2) {
                    return PredictedActivities;
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            Log.d("HeuristicsMiner", e.getMessage());
            return null;
        }
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
