package uni.mannheim.teamproject.diabetesplaner.Domain;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import uni.mannheim.teamproject.diabetesplaner.DataMining.Prediction;
import uni.mannheim.teamproject.diabetesplaner.DataMining.PredictionFramework;
import uni.mannheim.teamproject.diabetesplaner.UI.DailyRoutine.DailyRoutineFragment;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;


/**
 * Created by Stefan on 22.02.2016.
 */
public class DailyRoutineHandler extends DayHandler{
    private static ArrayList<ActivityItem> dailyRoutine;
    public static final String TAG = DailyRoutineHandler.class.getSimpleName();
    private static DailyRoutineFragment drFragment;
    public Date date;

    public DailyRoutineHandler(DailyRoutineFragment drFragment) {
        super(drFragment);
        dailyRoutine = getDailyRoutine();
    }

    /**
     * predicts the daily routine and lets you choose between different algorithms
     * @param algorithms a list with the algorithms to use. Possible algorithms:
     *                  <li>PredictionFramework.PREDICTION_DECISION_TREE</li>
     *                  <li>PredictionFramework.PREDICTION_GSP</li>
     *                  <li>PredictionFramework.PREDICTION_FUZZY_MINER</li>
     *                  <li>PredictionFramework.PREDICTION_HEURISTICS_MINER</li>
     * @param mode     EVERY_DAY, WEEKDAYS, WEEKENDS, MONDAY, ... , SUNDAY
     * @author Stefan 06.09.2016
     */
//    public void predictDailyRoutine(ArrayList<Integer> algorithms, int mode){
//        dailyRoutine.clear();
////        new Thread(new PredictionFramework(PredictionFramework.retrieveTrainingData(mode), algorithms, this)).start();
////        Log.d(TAG, "predicted daily routine: ");
////        for(int i=0; i<dailyRoutine.size();i++){
////            Log.d(TAG, dailyRoutine.get(i).print());
////        }
////        AppGlobal.getHandler().insertNewRoutine(dailyRoutine);
//    }

    /**
     * predicts the daily routine and gets the algorithms to choose from the settings
     * @param mode     EVERY_DAY, WEEKDAYS, WEEKENDS, MONDAY, ... , SUNDAY
     * @author Stefan 06.09.2016
     */
    public void predictDailyRoutine(int mode, Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        ArrayList<Integer> algorithms = new ArrayList<>();
        boolean dt = preferences.getBoolean("pref_key_dt", true);
        if(dt){
            algorithms.add(PredictionFramework.PREDICTION_DECISION_TREE);
        }
        boolean gsp = preferences.getBoolean("pref_key_gsp", true);
        if(gsp){
            algorithms.add(PredictionFramework.PREDICTION_GSP);
        }
        boolean fuzzy = preferences.getBoolean("pref_key_fuzzy", true);
        if(fuzzy){
            algorithms.add(PredictionFramework.PREDICTION_FUZZY_MINER);
        }
        boolean heuristics = preferences.getBoolean("pref_key_heuristics", true);
        if(heuristics){
            algorithms.add(PredictionFramework.PREDICTION_HEURISTICS_MINER);
        }

        try {
//            new Thread(new PredictionFramework(PredictionFramework.retrieveTrainingData(mode), algorithms, this)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("DailyRoutineFragment", "Size after predict " +dailyRoutine.get(0).getStarttime());
    }


    /**
     * Created by leonidgunko
     * predicts routine as arraylist of activity items from the whole routine
     */
    public void predictDailyRoutine(Date date){
        this.setDate(date);
        ArrayList<Prediction.PeriodAction> prediction = new ArrayList<Prediction.PeriodAction>();
        Prediction prediction1 = new Prediction();
        try{
            AppGlobal.getHandler().deleteDay(TimeUtils.getDateFromString("2016-09-21"));
            if (AppGlobal.getHandler()!=null){
                if (!AppGlobal.getHandler().CheckRoutineAdded()){
                    prediction = prediction1.GetRoutineAsPAforInserting();
                    AppGlobal.getHandler().InsertNewRoutine(prediction);
                    dailyRoutine.clear();
                    String dateString = null;
                    for (int i = 0; i < prediction.size(); i++) {
                        dateString = TimeUtils.convertDateToDateString(date);
                        dailyRoutine.add(new ActivityItem(prediction.get(i).Action + 1, 0, TimeUtils.setTime(dateString, prediction.get(i).Start), TimeUtils.setTime(dateString, prediction.get(i).End)));
                    }
                    Log.i(TAG, dailyRoutine.toString());
                }
                else{
                    Calendar calendar = Calendar.getInstance();
                    //Date date = calendar.getTime();
                    ArrayList<ActivityItem> Day1 = AppGlobal.getHandler().GetDay(date);
                    dailyRoutine.clear();
                    String dateString = null;
                    for (int i = 0; i < Day1.size(); i++) {
                        dateString = TimeUtils.convertDateToDateString(date);
                        dailyRoutine.add(Day1.get(i));
                    }

                    Log.i(TAG, dailyRoutine.toString());
                }
            }
            else{
                AppGlobal.getHandler().onCreate(AppGlobal.getHandler().db);
                if (!AppGlobal.getHandler().CheckRoutineAdded()){
                    prediction = prediction1.GetRoutineAsPAforInserting();
                    AppGlobal.getHandler().InsertNewRoutine(prediction);
                    dailyRoutine.clear();
                    String dateString = null;
                    for (int i = 0; i < prediction.size(); i++) {
                        dateString = TimeUtils.convertDateToDateString(date);
                        dailyRoutine.add(new ActivityItem(prediction.get(i).Action + 1, 0, TimeUtils.setTime(dateString, prediction.get(i).Start), TimeUtils.setTime(dateString, prediction.get(i).End)));
                    }
                    Log.i(TAG, dailyRoutine.toString());
                }
                else{
                    Calendar calendar = Calendar.getInstance();
                    //Date date = calendar.getTime();
                    ArrayList<ActivityItem> Day1 = AppGlobal.getHandler().GetDay(date);
                    dailyRoutine.clear();
                    String dateString = null;
                    for (int i = 0; i < Day1.size(); i++) {
                        dateString = TimeUtils.convertDateToDateString(date);
                        dailyRoutine.add(new ActivityItem(Day1.get(i).getActivityId(),0,Day1.get(i).getStarttimeAsString(),Day1.get(i).getEndtimeAsString()));
                    }
                    Log.i(TAG, dailyRoutine.toString());
                }
            }


        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void clearDailyRoutine(){
        dailyRoutine.clear();
    }

    public static void setDailyRoutine(ArrayList<ActivityItem> dailyRoutine){
        DailyRoutineHandler.dailyRoutine = dailyRoutine;
        AppGlobal.getHandler().insertNewRoutine(dailyRoutine);
    }

}
