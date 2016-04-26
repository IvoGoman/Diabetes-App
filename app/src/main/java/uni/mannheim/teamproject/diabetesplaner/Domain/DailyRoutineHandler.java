package uni.mannheim.teamproject.diabetesplaner.Domain;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import uni.mannheim.teamproject.diabetesplaner.DataMining.Prediction;
import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.UI.DailyRoutine.DailyRoutineFragment;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;


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
                        dateString = TimeUtils.convertDateToDateString(date);
                        dailyRoutine.add(new ActivityItem(prediction.get(i).Action + 1, 0, TimeUtils.setTime(dateString, prediction.get(i).Start), TimeUtils.setTime(dateString, prediction.get(i).End)));
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
                        dateString = TimeUtils.convertDateToDateString(date);
                        dailyRoutine.add(new ActivityItem(Day1.get(i).getActivityId(), 0, Day1.get(i).getStarttime(), Day1.get(i).getEndtime()));
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
                        dateString = TimeUtils.convertDateToDateString(date);
                        dailyRoutine.add(new ActivityItem(prediction.get(i).Action + 1, 0, TimeUtils.setTime(dateString, prediction.get(i).Start), TimeUtils.setTime(dateString, prediction.get(i).End)));
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
                        dateString = TimeUtils.convertDateToDateString(date);
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
    }

    public void clearDailyRoutine(){
        dailyRoutine.clear();
    }
}
