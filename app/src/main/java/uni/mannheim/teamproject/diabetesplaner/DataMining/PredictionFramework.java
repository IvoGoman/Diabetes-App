package uni.mannheim.teamproject.diabetesplaner.DataMining;

import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import uni.mannheim.teamproject.diabetesplaner.DataMining.EvaluationFramework.Fitness;
import uni.mannheim.teamproject.diabetesplaner.DataMining.SequentialPattern.GSP_Prediction;
import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Domain.DailyRoutineHandler;
import uni.mannheim.teamproject.diabetesplaner.ProcessMining.HeuristicsMiner.HeuristicsMinerImplementation;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;
import uni.mannheim.teamproject.diabetesplaner.Utility.Util;

/**
 * Created by Stefan on 06.09.2016.
 */
public class PredictionFramework{
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

    private static ArrayList<ActivityItem> dailyRoutine = new ArrayList<>();
    private static HashMap<Integer, ArrayList<ActivityItem>> results = new HashMap<>();
    private static HashMap<Integer, Double> accuracies = new HashMap<>();
    private static HashMap<Integer, Double> accuraciesFlow = new HashMap<>();
    private final ArrayList<ArrayList<ActivityItem>> train;
    private static ArrayList<Integer> algorithms;
    private static int completed = 0;
    public static ArrayList<Thread> threads = new ArrayList<>();

    public PredictionFramework(final ArrayList<ArrayList<ActivityItem>> train, final ArrayList<Integer> algorithms){
        super();
        this.train = train;
        Log.d(TAG, "Training data: " + train.size());
        this.algorithms = algorithms;
        killRunningThreads();
        run();
    }

    /**
     * kills all running threads from previous usages of the PredictionFramework
     * @author Stefan
     */
    private void killRunningThreads() {
        for(int i = threads.size()-1; i>=0; i--){
            threads.get(i).interrupt();
            threads.remove(i);
        }
    }

    private void run() {
        completed = 0;
        dailyRoutine = new ArrayList<>();

        //check if there is training data
        if(train.size()>0) {
            runAlgorithms(train, algorithms);
            //wait until all algorithms terminated
            while (completed != algorithms.size()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //for debugging the results of the Prediction
//            String debugResult = "";
//            for(int k = 0; k < algorithms.size();k++) {
//                debugResult = debugResult + "-----------------------" + "\n";
//                debugResult = debugResult + "Algorithm: " + String.valueOf(algorithms.get(k)) + "\n";
//                for (int l = 0; l < results.get(algorithms.get(k)).size(); l++) {
//
//
//                    ActivityItem currentActivity = results.get(algorithms.get(k)).get(l);
//                    debugResult = debugResult + currentActivity.getActivityId() + "," + currentActivity.getSubactivityId() + ","
//                            + currentActivity.getStarttimeAsString() + "," + currentActivity.getEndtimeAsString() + ","
//                            + currentActivity.getDuration() + "\n";
//                }
//                debugResult = debugResult + "-----------------------" + "\n";
//            }
//            Log.d("ResultsPrediction",debugResult);
            //check if voting should be performed
            if (algorithms.size() > 1) {
                dailyRoutine = voteBasedOnAccuracy();
                double acc1 = Evaluation.Accuracy(train, dailyRoutine);
                double acc2 = Evaluation.AccuracyFlow(train, dailyRoutine);
                double precision = Evaluation.Precision(train, dailyRoutine);
                double recall = Evaluation.Recall(train, dailyRoutine);
                double fMeasure = Evaluation.Fmeasure(precision,recall);
                Log.d(TAG, "Voting");
                Log.d(TAG, "Accuracy: \t\t" + acc1);
                Log.d(TAG, "Accuracy Flow:\t" + acc2);
                Log.d(TAG, "Precision:\t\t" + precision);
                Log.d(TAG, "Recall:\t\t\t" + recall);
                Log.d(TAG, "fMeasure:\t\t\t" + fMeasure);
                Log.d(TAG, "Predicted Activities: " + dailyRoutine.size());
            } else if (algorithms.size() == 1) {
                dailyRoutine = results.get(algorithms.get(0));
            }
        }

        //daily routine is empty
        if (dailyRoutine!=null && dailyRoutine.size() < 1) {
            if (!AppGlobal.getHandler().CheckRoutineAdded()) {
                //default prediction (Sleeping from 0:00 to 23:59)
                Date start = TimeUtils.getDate(new Date(), 0, 0);
                Date end = TimeUtils.getDate(new Date(), 7, 14);
                ActivityItem item = new ActivityItem(1, 1, start, end);
                dailyRoutine.add(item);

                start = TimeUtils.getDate(new Date(), 7, 15);
                end = TimeUtils.getDate(new Date(), 7, 29);
                item = new ActivityItem(10, 10, start, end);
                dailyRoutine.add(item);

                start = TimeUtils.getDate(new Date(), 7, 30);
                end = TimeUtils.getDate(new Date(), 7, 44);
                item = new ActivityItem(2, 18, start, end);
                dailyRoutine.add(item);

                start = TimeUtils.getDate(new Date(), 7, 45);
                end = TimeUtils.getDate(new Date(), 9, 29);
                item = new ActivityItem(3, 3, start, end);
                dailyRoutine.add(item);

                start = TimeUtils.getDate(new Date(), 9, 30);
                end = TimeUtils.getDate(new Date(), 10, 29);
                item = new ActivityItem(13, 13, start, end);
                dailyRoutine.add(item);

                start = TimeUtils.getDate(new Date(), 10, 30);
                end = TimeUtils.getDate(new Date(), 10, 34);
                item = new ActivityItem(2, 21, start, end);
                dailyRoutine.add(item);

                start = TimeUtils.getDate(new Date(), 10, 35);
                end = TimeUtils.getDate(new Date(), 12, 29);
                item = new ActivityItem(13, 13, start, end);
                dailyRoutine.add(item);

                start = TimeUtils.getDate(new Date(), 12, 30);
                end = TimeUtils.getDate(new Date(), 13, 29);
                item = new ActivityItem(2, 19, start, end);
                dailyRoutine.add(item);

                start = TimeUtils.getDate(new Date(), 13, 30);
                end = TimeUtils.getDate(new Date(), 15, 29);
                item = new ActivityItem(13, 13, start, end);
                dailyRoutine.add(item);

                start = TimeUtils.getDate(new Date(), 15, 30);
                end = TimeUtils.getDate(new Date(), 15, 34);
                item = new ActivityItem(2, 21, start, end);
                dailyRoutine.add(item);

                start = TimeUtils.getDate(new Date(), 15, 35);
                end = TimeUtils.getDate(new Date(), 17, 29);
                item = new ActivityItem(13, 13, start, end);
                dailyRoutine.add(item);

                start = TimeUtils.getDate(new Date(), 17, 30);
                end = TimeUtils.getDate(new Date(), 17, 59);
                item = new ActivityItem(14, 62, start, end);
                dailyRoutine.add(item);

                start = TimeUtils.getDate(new Date(), 18, 00);
                end = TimeUtils.getDate(new Date(), 18, 59);
                item = new ActivityItem(5, 36, start, end);
                dailyRoutine.add(item);

                start = TimeUtils.getDate(new Date(), 19, 00);
                end = TimeUtils.getDate(new Date(), 19, 29);
                item = new ActivityItem(11, 11, start, end);
                dailyRoutine.add(item);

                start = TimeUtils.getDate(new Date(), 19, 30);
                end = TimeUtils.getDate(new Date(), 19, 59);
                item = new ActivityItem(10, 10, start, end);
                dailyRoutine.add(item);

                start = TimeUtils.getDate(new Date(), 20, 00);
                end = TimeUtils.getDate(new Date(), 21, 59);
                item = new ActivityItem(2, 20, start, end);
                dailyRoutine.add(item);

                start = TimeUtils.getDate(new Date(), 22, 00);
                end = TimeUtils.getDate(new Date(), 22, 14);
                item = new ActivityItem(2, 21, start, end);
                dailyRoutine.add(item);

                start = TimeUtils.getDate(new Date(), 22, 15);
                end = TimeUtils.getDate(new Date(), 22, 29);
                item = new ActivityItem(7, 7, start, end);
                dailyRoutine.add(item);

                start = TimeUtils.getDate(new Date(), 22, 30);
                end = TimeUtils.getDate(new Date(), 23, 59);
                item = new ActivityItem(1, 1, start, end);
                dailyRoutine.add(item);
            }
        }

        DailyRoutineHandler.setDailyRoutine(dailyRoutine);

    }


    /**
     * returns the training data specified by mode
     *
     * @param mode EVERY_DAY, WEEKDAYS, WEEKENDS, MONDAY, ... , SUNDAY
     * @return
     * @author Stefan 06.09.2016
     */
    public static ArrayList<ArrayList<ActivityItem>> retrieveTrainingData(int mode) {
        DataBaseHandler dbHandler = AppGlobal.getHandler();
        return dbHandler.getAllDays(mode);
    }

    /**
     * runs the algorithms in algorithms, each in a differnt thread
     * @param train      an arrayList that contains the training data (the days to train on)
     * @param algorithms the algorithms that should be chosen
     * @author Stefan 22.09.2016
     */
    public static void runAlgorithms(final ArrayList<ArrayList<ActivityItem>> train, ArrayList<Integer> algorithms){
        Log.d(TAG, "Size Training Data: " + train.size());
        //run the algorithms specified in parameter algos
        for (int i = 0; i < algorithms.size(); i++) {
            int algo = algorithms.get(i);
            switch (algo) {
                case PREDICTION_DECISION_TREE:
                    Thread t0 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                double acc3 = Evaluation.usageTreeK(train,15);
                                ArrayList<ActivityItem> prediction = Prediction.GetRoutineAsAI(train);
                                double acc1 = Evaluation.Accuracy(train, prediction);
                                double acc2 = Evaluation.AccuracyFlow(train, prediction);
                                double precision = Evaluation.Precision(train, prediction);
                                double recall = Evaluation.Recall(train, prediction);
                                double fMeasure = Evaluation.Fmeasure(precision,recall);
                                results.put(PREDICTION_DECISION_TREE, prediction);
                                accuracies.put(PREDICTION_DECISION_TREE, acc1);
                                accuraciesFlow.put(PREDICTION_DECISION_TREE, acc2);
                                Log.d(TAG, "Decision Tree");
                                Log.d(TAG, "Accuracy: \t\t" + acc1);
                                Log.d(TAG, "Accuracy Flow:\t" + acc2);
                                Log.d(TAG, "Precision:\t\t" + precision);
                                Log.d(TAG, "Recall:\t\t\t" + recall);
                                Log.d(TAG, "fMeasure:\t\t\t" + fMeasure);
                                Log.d(TAG, "Predicted Activities: " + prediction.size());
                                Log.d(TAG, "-------------------------------");
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e(TAG, "Decision Tree " + e.getLocalizedMessage());
                            }
                            completed++;
//                            Log.d(TAG, "Decision Tree done");
                        }
                    });
                    t0.start();
                    threads.add(t0);
                    break;
                case PREDICTION_GSP:
                    Thread t1 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<ActivityItem> prediction = GSP_Prediction.makeGSPPrediction(train, 0.2f);
                            results.put(PREDICTION_GSP, prediction);
                            double acc1 = Evaluation.Accuracy(train, prediction);
                            double acc2 = Evaluation.AccuracyFlow(train, prediction);
                            double precision = Evaluation.Precision(train, prediction);
                            double recall = Evaluation.Recall(train, prediction);
                            double fMeasure = Evaluation.Fmeasure(precision,recall);
                            accuracies.put(PREDICTION_GSP, acc1);
                            accuraciesFlow.put(PREDICTION_GSP, acc2);
                            Log.d(TAG, "GSP");
                            Log.d(TAG, "Accuracy: \t\t" + acc1);
                            Log.d(TAG, "Accuracy Flow:\t" + acc2);
                            Log.d(TAG, "Precision:\t\t" + precision);
                            Log.d(TAG, "Recall:\t\t\t" + recall);
                            Log.d(TAG, "fMeasure:\t\t\t" + fMeasure);
                            Log.d(TAG, "Predicted Activities: " + prediction.size());
                            Log.d(TAG, "-------------------------------");
                            completed++;

                        }
                    });
                    t1.start();
                    threads.add(t1);
                    break;
                case PREDICTION_FUZZY_MINER:
                    Thread t2 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            FuzzyModel model = new FuzzyModel(train, false);
                            ArrayList<ActivityItem> prediction = model.makeFuzzyMinerPrediction();
                            results.put(PREDICTION_FUZZY_MINER, prediction);
                            double fit = Fitness.evaluate(train, model.getPredictionStructure(),Fitness.FITNESS_COMPLETENESS);
                            double fitWeighted = Fitness.evaluate(train, model.getPredictionStructure(),Fitness.FITNESS_WEIGHTED);
                            double fitToken = 1- Fitness.evaluate(train, model.getPredictionStructure(),Fitness.FITNESS_TOKEN);
                            double acc1 = Evaluation.Accuracy(train, prediction);
                            double acc2 = Evaluation.AccuracyFlow(train, prediction);
                            double precision = Evaluation.Precision(train, prediction);
                            double recall = Evaluation.Recall(train, prediction);
                            double fMeasure = Evaluation.Fmeasure(precision,recall);
                            accuracies.put(PREDICTION_FUZZY_MINER, acc1);
                            accuraciesFlow.put(PREDICTION_FUZZY_MINER, acc2);
                            Log.d(TAG, "Fuzzy Miner");
                            Log.d(TAG, "Plain Fitness: \t\t" + fit);
                            Log.d(TAG, "Weighted Fitness: \t\t" + fitWeighted);
                            Log.d(TAG, "Weighted Token Fitness: \t\t" + fitToken);
                            Log.d(TAG, "Accuracy: \t\t" + acc1);
                            Log.d(TAG, "Accuracy Flow:\t" + acc2);
                            Log.d(TAG, "Precision:\t\t" + precision);
                            Log.d(TAG, "Recall:\t\t\t" + recall);
                            Log.d(TAG, "fMeasure:\t\t\t" + fMeasure);
                            Log.d(TAG, "Predicted Activities: " + prediction.size());
                            Log.d(TAG, "-------------------------------");
                            completed++;

                        }
                    });
                    t2.start();
                    threads.add(t2);
                    break;
                case PREDICTION_HEURISTICS_MINER:
                    Thread t3 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HeuristicsMinerImplementation HMmodel = new HeuristicsMinerImplementation(0.7,1,0.05);
                            ArrayList<ActivityItem> prediction = HMmodel.runHeuristicsMiner(train);
                            results.put(PREDICTION_HEURISTICS_MINER, prediction);
                            double acc1 = Evaluation.Accuracy(train, prediction);
                            double acc2 = Evaluation.AccuracyFlow(train, prediction);
                            double precision = Evaluation.Precision(train, prediction);
                            double recall = Evaluation.Recall(train, prediction);
                            double fMeasure = Evaluation.Fmeasure(precision,recall);
                            accuracies.put(PREDICTION_HEURISTICS_MINER, acc1);
                            accuraciesFlow.put(PREDICTION_HEURISTICS_MINER, acc2);
                            Log.d(TAG, "Heuristics Miner");
                            Log.d(TAG, "Accuracy: \t\t" + acc1);
                            Log.d(TAG, "Accuracy Flow:\t" + acc2);
                            Log.d(TAG, "Precision:\t\t" + precision);
                            Log.d(TAG, "Recall:\t\t\t" + recall);
                            Log.d(TAG, "fMeasure:\t\t\t" + fMeasure);
                            Log.d(TAG, "Predicted Activities: " + prediction.size());
                            Log.d(TAG, "-------------------------------");
                            completed++;

                        }
                    });
                    t3.start();
                    threads.add(t3);
                    break;
            }
        }
    }

    /**
     * performs voting based on the mean of normal accuracy and flow accuracy.
     * Daily routine of the algorithm with the best score will be returned
     * @return
     * @author Stefan 28.09.2016
     */
    private static ArrayList<ActivityItem> voteBasedOnAccuracy(){
        HashMap<Integer, Double> avgAccs = new HashMap<>();
        for(int i=0; i<algorithms.size(); i++){
            int algo = algorithms.get(i);
            avgAccs.put(algo, (accuracies.get(algo)+accuraciesFlow.get(algo))/2);
        }

        Integer bestAlg = null;
        double max = Double.MAX_VALUE;
        for (Map.Entry<Integer, Double> entry : avgAccs.entrySet()) {
            Integer algo = entry.getKey();
            Double score = entry.getValue();
            Log.d(TAG, "Algorithm: " + algo + " result: " + score);
            if(bestAlg == null){
                bestAlg = algo;
                max = score;
            }else if(score > max){
                bestAlg = algo;
                max = score;
            }
        }

        Log.d(TAG, "Best algorithm: " + bestAlg + " nr. of algorithms: " + algorithms.size());

        return results.get(bestAlg);
    }


    /**
     * implements a voting
     *
     * @return dailyRoutine
     * @author Stefan 13.09.2016
     */
    private static ArrayList<ActivityItem> vote() {

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

        ArrayList<Pair<Integer, Integer>> votedDailyRoutine = new ArrayList<>();
        Pair<Integer, Integer> prev = null;
        for(int i=0; i<dailyRoutinePairs.size(); i++){
            Pair<Integer, Integer> curr = dailyRoutinePairs.get(i);
            if(i%2 == 1){
                if(prev != curr){
                    votedDailyRoutine.add(curr);
                }else{
                    Random random = new Random();
                    //take curr
                    if(random.nextBoolean()){
                        votedDailyRoutine.add(curr);
                    }else{
                        //take prev
                        votedDailyRoutine.add(prev);
                    }
                }
            }
            prev = dailyRoutinePairs.get(i);
        }

        Pair<Integer, Integer> prevPair = null;
        int start = 0;
        Date date = new Date();
        for (int i = 0; i < votedDailyRoutine.size(); i++) {
            Pair<Integer, Integer> pair = votedDailyRoutine.get(i);
            if (prevPair == null) {
                prevPair = pair;
            } else {
                if (!pair.equals(prevPair)) {
                    ActivityItem item = new ActivityItem(prevPair.first, prevPair.second, TimeUtils.minOfDayToDate(start, date), TimeUtils.minOfDayToDate(((i+1)*2 - 1), date));
                    start = (i+1)*2;
                    prevPair = pair;
                    dailyRoutine.add(item);
                }
            }
        }
        //add last item
        ActivityItem item = new ActivityItem(prevPair.first, prevPair.second, TimeUtils.minOfDayToDate(start, date), TimeUtils.minOfDayToDate(1439, date));
        dailyRoutine.add(item);
        return dailyRoutine;

    }
}

