package uni.mannheim.teamproject.diabetesplaner.DataMining;

import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import uni.mannheim.teamproject.diabetesplaner.DataMining.SequentialPattern.GSP_Prediction;
import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.ProcessMining.HeuristicsMiner.HeuristicsMinerImplementation;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;
import uni.mannheim.teamproject.diabetesplaner.Utility.Util;

/**
 * Created by Stefan on 06.09.2016.
 */
public class PredictionFramework {
    private static final String TAG = "PredictionFramework";

    public static final int PREDICTION_DECISION_TREE = 0;
    public static final int PREDICTION_GSP = 1;
    public static final int PREDICTION_FUZZY_MINER = 2;
    public static final int PREDICTION_HEURISTICS_MINER = 3;
    public static final int VOTING = 4;

    public static final int EVERY_DAY = 100;
    public static final int WEEKDAYS = 101;
    public static final int WEEKENDS = 102;
    public static final int MONDAY = Calendar.MONDAY;
    public static final int TUESDAY = Calendar.TUESDAY;
    public static final int WEDNESDAY = Calendar.WEDNESDAY;
    public static final int THURSDAY = Calendar.THURSDAY;
    public static final int FRIDAY = Calendar.FRIDAY;
    public static final int SATURDAY = Calendar.SATURDAY;
    public static final int SUNDAY = Calendar.SUNDAY;

    /**
     * returns the training data specified by mode
     *
     * @param mode EVERY_DAY, WEEKDAYS, WEEKENDS, MONDAY, ... , SUNDAY
     * @return
     * @author Stefan 06.09.2016
     */
    public static ArrayList<ArrayList<ActivityItem>> retrieveTrainingData(int mode) {
        DataBaseHandler dbHandler = AppGlobal.getHandler();
        return dbHandler.getAllDays1(mode);
    }

    /**
     * predicts a day
     *
     * @param train     an arrayList that contains the training data (the days to train on)
     * @param algorithms the algorithms that should be chosen
     * @return
     * @author Stefan 06.09.2016
     */
    public static ArrayList<ActivityItem> predict(ArrayList<ArrayList<ActivityItem>> train, ArrayList<Integer> algorithms){
        if(train.size()>0) {
            if (algorithms.size() == 1) {
                switch (algorithms.get(0)) {
                    case PREDICTION_DECISION_TREE:
                        try {
                            return Prediction.GetRoutineAsAI();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, e.toString());
                        }
                        return null;
                    case PREDICTION_GSP:
                        return GSP_Prediction.makeGSPPrediction(train, 0.2f);
                    case PREDICTION_FUZZY_MINER:
                        FuzzyModel model = new FuzzyModel(train, false);
                        return model.makeFuzzyMinerPrediction();
                    case PREDICTION_HEURISTICS_MINER:
//                        return HeuristicsMinerImplementation.runHeuristicsMiner(train);
                        return null;
                }
            } else if (algorithms.size() > 1) {
                return vote(algorithms, train);
            }
        }

        ArrayList<ActivityItem> defaultRoutine = new ArrayList<>();

        if(AppGlobal.getHandler().CheckRoutineAdded(AppGlobal.getHandler())) {
            //default prediction (Sleeping from 0:00 to 23:59)
            Date start = TimeUtils.getDate(new Date(), 0, 0);
            Date end = TimeUtils.getDate(new Date(), 23, 59);
            ActivityItem item = new ActivityItem(1, 1, start, end);
            defaultRoutine.add(item);
        }
        return defaultRoutine;
    }

    /**
     * implements a voting
     *
     * @param algos algorithms to use
     * @param train training data
     * @return dailyRoutine
     * @author Stefan 13.09.2016
     */
    public static ArrayList<ActivityItem> vote(ArrayList<Integer> algos, ArrayList<ArrayList<ActivityItem>> train) {
        HashMap<Integer, ArrayList<ActivityItem>> results = new HashMap<>();
        //run the algorithms specified in parameter algos
        for (int i = 0; i < algos.size(); i++) {
            int algo = algos.get(i);
            switch (algo) {
                case PREDICTION_DECISION_TREE:
                    try {
                        results.put(PREDICTION_DECISION_TREE, Prediction.GetRoutineAsAI());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, e.toString());
                    }
                    break;
                case PREDICTION_GSP:
                    results.put(PREDICTION_GSP, GSP_Prediction.makeGSPPrediction(train, 0.2f));
                case PREDICTION_FUZZY_MINER:
                    FuzzyModel model = new FuzzyModel(train, false);
                    results.put(PREDICTION_FUZZY_MINER, model.makeFuzzyMinerPrediction());
                case PREDICTION_HEURISTICS_MINER:
                    results.put(PREDICTION_HEURISTICS_MINER,HeuristicsMinerImplementation.runHeuristicsMiner(train.get(0)));

            }
        }

        ArrayList<Pair<Integer, Integer>> dailyRoutinePairs = new ArrayList<>();
        //iterate through every minute of the day
        for (int i = 0; i < 1440; i++) {

            //iterate through the list with <algo, prediction>-pairs and get the activity items for the i-th minute
            ArrayList<ActivityItem> minuteVotes = new ArrayList<>();
            for (Map.Entry<Integer, ArrayList<ActivityItem>> entry : results.entrySet()) {
                int algo = entry.getKey();
                ArrayList<ActivityItem> item = entry.getValue();
                minuteVotes.add(Util.getActivityAtMinute(item, i));
            }

            //iterate through the list that contains a predicted activity item for this minute from every algorithm
            HashMap<Pair, Integer> pairCount = new HashMap<>();
            for (int j = 0; j < minuteVotes.size(); j++) {
                ActivityItem tmp = minuteVotes.get(j);
                Pair<Integer, Integer> pair = new Pair<>(tmp.getActivityId(), tmp.getSubactivityId());
                if (pairCount.get(pair) == null) {
                    pairCount.put(pair, 1);
                } else {
                    pairCount.put(pair, (pairCount.get(pair) + 1));
                }
            }

            //iterate through the list that contains the counts for each prediction and determine the most frequent one,
            //or decide randomly if two pairs occur with the same frequency
            Pair<Integer, Integer> pairVote = null;
            int count = 0;
            for (Map.Entry<Pair, Integer> entry : pairCount.entrySet()) {
                Pair key = entry.getKey();
                int value = entry.getValue();

                if (value > count) {
                    count = value;
                    pairVote = key;
                } else if (value == count) {
                    Random random = new Random();
                    if (random.nextBoolean()) {
                        count = value;
                        pairVote = key;
                    }
                }
            }
            dailyRoutinePairs.add(pairVote);
        }

        ArrayList<ActivityItem> dailyRoutine = new ArrayList<>();

        //dailyRoutinePairs is a list, that contains a <activityID ,subactivityID>-pair for every minute
        //now those pairs are combined into ActivityItems
        Pair<Integer, Integer> prevPair = null;
        int start = 0;
        Date date = new Date();
        for (int i = 0; i < dailyRoutinePairs.size(); i++) {
            Pair<Integer, Integer> pair = dailyRoutinePairs.get(i);
            if (prevPair == null) {
                prevPair = pair;
            } else {
                if (!pair.equals(prevPair)) {
                    ActivityItem item = new ActivityItem(prevPair.first, prevPair.second, TimeUtils.minOfDayToDate(start, date), TimeUtils.minOfDayToDate((i - 1), date));
                    start = i;
                    prevPair = pair;
                    dailyRoutine.add(item);
                }
            }
        }
        return dailyRoutine;

    }
}

