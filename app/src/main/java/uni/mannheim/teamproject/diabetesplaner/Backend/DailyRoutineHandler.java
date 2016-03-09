package uni.mannheim.teamproject.diabetesplaner.Backend;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import uni.mannheim.teamproject.diabetesplaner.DailyRoutine.DailyRoutineFragment;
import uni.mannheim.teamproject.diabetesplaner.DataMining.Util;


/**
 * Created by Stefan on 22.02.2016.
 */
public class DailyRoutineHandler extends DayHandler{
    private ArrayList<ActivityItem> dailyRoutine;
    public static final String TAG = DailyRoutineHandler.class.getSimpleName();
    private static DailyRoutineFragment drFragment;
    public Date date;

    public DailyRoutineHandler(DailyRoutineFragment drFragment) {
        super(drFragment);
        dailyRoutine = getDailyRoutine();
    }


    /**
     * TODO
     * returns the predicted DailyRoutine from the model
     * @return
     */
    public void predictDailyRoutine(Date date){
        this.setDate(date);
        ArrayList<Prediction.PeriodAction> prediction = new ArrayList<Prediction.PeriodAction>();
        DataBaseHandler handler = null;
        Context context = AppGlobal.getcontext();
        Prediction prediction1 = new Prediction();
        try{
            if (AppGlobal.getHandler()!=null){
                //handler = AppGlobal.getHandler()
                if (!AppGlobal.getHandler().CheckRoutineAdded(AppGlobal.getHandler())){
                    prediction = prediction1.GetRoutine1();
                    AppGlobal.getHandler().InsertNewRoutine(AppGlobal.getHandler(), prediction);
                    dailyRoutine.clear();
                    String dateString = null;
                    for (int i = 0; i < prediction.size(); i++) {
                        dateString = Util.convertDateToDateString(date);
                        dailyRoutine.add(new ActivityItem(prediction.get(i).Action + 1, 0, Util.setTime(dateString, prediction.get(i).Start), Util.setTime(dateString, prediction.get(i).End)));
                    }
                    Log.i(TAG, dailyRoutine.toString());
                }
                else{
                    Calendar calendar = Calendar.getInstance();
                    //Date date = calendar.getTime();
                    ArrayList<ActivityItem> Day1 = AppGlobal.getHandler().GetDay(AppGlobal.getHandler(),date);
                    dailyRoutine.clear();
                    String dateString = null;
                    for (int i = 0; i < Day1.size(); i++) {
                        dateString = Util.convertDateToDateString(date);
                        dailyRoutine.add(new ActivityItem(Day1.get(i).getActivityId(),0,Day1.get(i).getStarttimeAsString(),Day1.get(i).getEndtimeAsString()));
                    }

                    Log.i(TAG, dailyRoutine.toString());
                }
            }
            else{
                AppGlobal.getHandler().onCreate(AppGlobal.getHandler().db);
                if (!AppGlobal.getHandler().CheckRoutineAdded(AppGlobal.getHandler())){
                    prediction = prediction1.GetRoutine1();
                    AppGlobal.getHandler().InsertNewRoutine(AppGlobal.getHandler(), prediction);
                    dailyRoutine.clear();
                    String dateString = null;
                    for (int i = 0; i < prediction.size(); i++) {
                        dateString = Util.convertDateToDateString(date);
                        dailyRoutine.add(new ActivityItem(prediction.get(i).Action + 1, 0, Util.setTime(dateString, prediction.get(i).Start), Util.setTime(dateString, prediction.get(i).End)));
                    }
                    Log.i(TAG, dailyRoutine.toString());
                }
                else{
                    Calendar calendar = Calendar.getInstance();
                    //Date date = calendar.getTime();
                    ArrayList<ActivityItem> Day1 = AppGlobal.getHandler().GetDay(AppGlobal.getHandler(),date);
                    dailyRoutine.clear();
                    String dateString = null;
                    for (int i = 0; i < Day1.size(); i++) {
                        dateString = Util.convertDateToDateString(date);
                        dailyRoutine.add(new ActivityItem(Day1.get(i).getActivityId(),0,Day1.get(i).getStarttimeAsString(),Day1.get(i).getEndtimeAsString()));
                    }
                    Log.i(TAG, dailyRoutine.toString());
                }
            }


        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        //TODO get daily routine from model

        /*dailyRoutine.add(new ActivityItem(1,0,"0:00","9:14"));
        dailyRoutine.add(new ActivityItem(2,0,"9:15","9:53"));
        dailyRoutine.add(new ActivityItem(13,0,"9:54","13:07"));
        dailyRoutine.add(new ActivityItem(2,0,"13:08","13:22"));
        dailyRoutine.add(new ActivityItem(13,0, "13:23", "15:35"));
        dailyRoutine.add(new ActivityItem(10,0, "15:36", "15:38"));
        dailyRoutine.add(new ActivityItem(13,0, "15:39", "21:53"));
        dailyRoutine.add(new ActivityItem(5,0,"21:54","22:22"));
        dailyRoutine.add(new ActivityItem(2,0,"22:23","22:51"));
        dailyRoutine.add(new ActivityItem(1,0, "22:52", "23:59"));*/
    }

    public void clearDailyRoutine(){
        dailyRoutine.clear();
    }
}
