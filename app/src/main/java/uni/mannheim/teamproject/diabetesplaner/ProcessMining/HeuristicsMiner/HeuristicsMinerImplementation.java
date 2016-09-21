package uni.mannheim.teamproject.diabetesplaner.ProcessMining.HeuristicsMiner;

import android.util.Log;

import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import uni.mannheim.teamproject.diabetesplaner.DataMining.Preprocessing.CustomXLog;
import uni.mannheim.teamproject.diabetesplaner.DataMining.ProcessMiningUtil;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityPrediction;
import uni.mannheim.teamproject.diabetesplaner.ProcessMining.HeuristicsMiner.models.heuristics.HeuristicsNet;
import uni.mannheim.teamproject.diabetesplaner.ProcessMining.HeuristicsMiner.models.heuristics.impl.ActivitiesMappingStructures;
import uni.mannheim.teamproject.diabetesplaner.ProcessMining.HeuristicsMiner.plugins.heuristicsnet.SimpleHeuristicsNet;
import uni.mannheim.teamproject.diabetesplaner.ProcessMining.HeuristicsMiner.plugins.heuristicsnet.miner.heuristics.miner.HeuristicsMiner;
import uni.mannheim.teamproject.diabetesplaner.ProcessMining.HeuristicsMiner.plugins.heuristicsnet.miner.heuristics.miner.settings.HeuristicsMinerSettings;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;

/**
 * Created by Jan on 17.05.16.
 */
public class HeuristicsMinerImplementation {

    public static void main(String[] args) throws IOException {
       HeuristicsMinerImplementation myHeuristics = new HeuristicsMinerImplementation();
        myHeuristics.mineTest();
   }

    public void HeuristicsMinerImplementation()
    {
        mineApp();
    }

    public void mineApp()
    {
        try {
//            String logFile = new File("").getAbsolutePath();
//            logFile = logFile + "/app/src/test/java/data/activityDataCompleteWithCasesJoined2.xes.gz";
//            XesXmlGZIPParser xesXmlGZIPParser = new XesXmlGZIPParser();
//            File file = new File(logFile);
//            System.out.println(xesXmlGZIPParser.canParse(file));
//            List<XLog> logs = xesXmlGZIPParser.parse(file);

            //DummyDataCreator.populateDataBase();
            //ArrayList<ActivityItem> items = AppGlobal.getHandler().getAllActivities(AppGlobal.getHandler());
            ArrayList<ActivityItem> items = AppGlobal.getHandler().getAllActivitiesByWeekday(0);
            CustomXLog customXLog = new CustomXLog(items);
            XLog log = customXLog.getXLog();
            ArrayList<String[]> cases = customXLog.getCases();
            ArrayList<String[]> eventList = customXLog.getEventList();
            cases.remove(0);
            eventList.remove(0);


            XLogInfo logInfo = XLogInfoFactory.createLogInfo(log);
            XEventClassifier classifier = logInfo.getEventClassifiers().iterator().next();
            HeuristicsMinerSettings hms = new HeuristicsMinerSettings();
            hms.setPositiveObservationThreshold(1);
            hms.setDependencyThreshold(0.2);
            hms.setClassifier(classifier);

            XEventClasses EventClasses = XEventClasses.deriveEventClasses(classifier,log);
            ActivitiesMappingStructures struct = new ActivitiesMappingStructures(EventClasses);

            HeuristicsMiner miner = new HeuristicsMiner(log,logInfo,hms);
            HeuristicsNet myNet = miner.mine();

            ArrayList<ActivityPrediction> HeuristicsOutput = createPredictionDataStructure(miner.getSimpleNet2());
            System.out.println( myNet.toString());

            //    SimpleHeuristicsNet HMNet = new SimpleHeuristicsNet(miner.makeBasicRelations(new HeuristicsMetrics(logInfo)),new HeuristicsMetrics(logInfo),hms);
            //  HeuristicsNetImpl HeuristicsNet = new HeuristicsNetImpl(struct);
        }catch (Exception e)
        {
            e.getMessage();
        }
    }


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
     * Sets the output of the HeuristicsMiner into the defined Datastructure of ActivityPrediction
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

    public static ArrayList<ActivityItem> runHeuristicsMiner(ArrayList<ActivityItem> train) {
        try {
            if (train == null) {
                return null;
            } else {
                CustomXLog xLog = new CustomXLog(train);
                XLog log = xLog.getXLog();
                ArrayList<String[]> cases = xLog.getCases();
                ArrayList<String[]> eventList = xLog.getEventList();
                cases.remove(0);
                eventList.remove(0);


                XLogInfo logInfo = XLogInfoFactory.createLogInfo(log);
                XEventClassifier classifier = logInfo.getEventClassifiers().iterator().next();
                HeuristicsMinerSettings hms = new HeuristicsMinerSettings();
                hms.setPositiveObservationThreshold(1);
                hms.setDependencyThreshold(0.2);
                hms.setClassifier(classifier);

                XEventClasses EventClasses = XEventClasses.deriveEventClasses(classifier, log);
                ActivitiesMappingStructures struct = new ActivitiesMappingStructures(EventClasses);

                HeuristicsMiner miner = new HeuristicsMiner(log, logInfo, hms);
                miner.mine();
                SimpleHeuristicsNet myNet = miner.getSimpleNet2();

                ArrayList<ActivityItem> PredictedActivities = new ArrayList<ActivityItem>();


                for (int i = 1; i < myNet.getActivitiesMappingStructures().getActivitiesMapping().length; i = i+2) {
                    ActivityItem PredictElement = new ActivityItem(i, 0);
                    Map<Integer, Double> durationMap;
                    durationMap = ProcessMiningUtil.getAverageDurations(eventList);
                    int ID;
                    int subID;
                    int completeID;
                    String ID_String = myNet.getActivitiesMappingStructures().getActivitiesMapping()[i].getId().split("\\+")[0];
                    if(ID_String.contentEquals("Start//Start") == false) {
                        //resolve ID and SubId
                        if (ID_String.length() == 3) {
                            ID = Integer.parseInt(ID_String.substring(0, 2));
                            subID = Integer.parseInt(ID_String.substring(2));
                            completeID = Integer.parseInt(ID_String);
                            PredictElement.setActivityId(ID);
                            PredictElement.setSubactivityId(subID);
                        } else {
                            ID = Integer.parseInt(ID_String);
                            subID = 0;
                            completeID = ID;
                            PredictElement.setActivityId(ID);
                            PredictElement.setSubactivityId(subID);
                        }
                        PredictElement.setDuration(durationMap.get(completeID));
                        Date Starttime = new Date(ProcessMiningUtil.getAverageStartTime(String.valueOf(completeID),eventList));
                        PredictElement.setStarttime(Starttime);
                        //PredictElement.setEndtime();
                        //PredictElement.setStarttime();

                        //PredictElement.setDuration();

                        //// TODO: 10.09.16 Add Starttime and Endtime
                        PredictedActivities.add(PredictElement);
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


}
