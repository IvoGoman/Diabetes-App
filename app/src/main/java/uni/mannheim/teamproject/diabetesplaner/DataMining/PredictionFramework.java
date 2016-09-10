package uni.mannheim.teamproject.diabetesplaner.DataMining;

import java.util.ArrayList;
import java.util.Calendar;

import uni.mannheim.teamproject.diabetesplaner.DataMining.SequentialPattern.GSP_Prediction;
import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.ProcessMining.HeuristicsMiner.HeuristicsMinerImplementation;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;

/**
 * Created by Stefan on 06.09.2016.
 */
public class PredictionFramework {
    public static final int PREDICTION_DECISION_TREE = 0;
    public static final int PREDICTION_GSP = 1;
    public static final int PREDICTION_FUZZY_MINER = 2;
    public static final int PREDICTION_HEURISTICS_MINER = 3;

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
     * @param mode EVERY_DAY, WEEKDAYS, WEEKENDS, MONDAY, ... , SUNDAY
     * @return
     * @author Stefan 06.09.2016
     */
    public static ArrayList<ArrayList<ActivityItem>> retrieveTrainingData(int mode){
        DataBaseHandler dbHandler = AppGlobal.getHandler();
        return dbHandler.getAllDays(mode);
    }

    /**
     * predicts a day
     * @param train an arrayList that contains the training data (the days to train on)
     * @param algorithm the algorithm that should be chosen
     * @return
     * @author Stefan 06.09.2016
     */
    public static ArrayList<ActivityItem> predict(ArrayList<ArrayList<ActivityItem>> train, int algorithm){
        switch (algorithm){
            case PREDICTION_DECISION_TREE:
                //TODO predict with decision tree
                break;
            case PREDICTION_GSP:
                return GSP_Prediction.makeGSPPrediction(train, 0.2f);
            case PREDICTION_FUZZY_MINER:
                //TODO predict with fuzzy miner
                break;
            case PREDICTION_HEURISTICS_MINER:
                return HeuristicsMinerImplementation.runHeuristicsMiner(train.get(0));

        }
        return null;
    }
}
