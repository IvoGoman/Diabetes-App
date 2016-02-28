package uni.mannheim.teamproject.diabetesplaner.Backend;

import android.content.Context;

import java.util.ArrayList;

import uni.mannheim.teamproject.diabetesplaner.DailyRoutine.DailyRoutineFragment;



/**
 * Created by Stefan on 22.02.2016.
 */
public class DailyRoutineHandler extends DayHandler{
    private ArrayList<ActivityItem> dailyRoutine;
    public static final String TAG = DailyRoutineHandler.class.getSimpleName();
    private static DailyRoutineFragment drFragment;

    public DailyRoutineHandler(DailyRoutineFragment drFragment) {
        super(drFragment);
        dailyRoutine = getDailyRoutine();
    }


    /**
     * TODO
     * returns the predicted DailyRoutine from the model
     * @return
     */
    public void predictDailyRoutine(){
        ArrayList<Prediction.PeriodAction> prediction = new ArrayList<Prediction.PeriodAction>();
        DataBaseHandler handler = null;
        Context context = AppGlobal.getcontext();
        Prediction prediction1 = new Prediction();
        try{
        if (AppGlobal.getHandler()!=null){
            //handler = AppGlobal.getHandler()

            prediction =prediction1.GetRoutine1();
        }
        else{
            AppGlobal.getHandler().onCreate(AppGlobal.getHandler().db);
           // handler = AppGlobal.getHandler();
            prediction =prediction1.GetRoutine1();
        }


        }
        catch(Exception e)
        {
        e.printStackTrace();
        }
        //TODO get daily routine from model
        for (int i=0;i<prediction.size();i++){
            dailyRoutine.add(new ActivityItem(prediction.get(i).Action+1,0,prediction.get(i).Start,prediction.get(i).End));
        }


    }

    public void clearDailyRoutine(){
        dailyRoutine.clear();
    }
}
