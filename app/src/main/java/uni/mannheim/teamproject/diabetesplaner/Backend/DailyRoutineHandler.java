package uni.mannheim.teamproject.diabetesplaner.Backend;

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
        //TODO get daily routine from model
        dailyRoutine.add(new ActivityItem(1,0,"0:00","9:14"));
        dailyRoutine.add(new ActivityItem(2,0,"9:15","9:53"));
        dailyRoutine.add(new ActivityItem(13,0,"9:54","13:07"));
        dailyRoutine.add(new ActivityItem(2,0,"13:08","13:22"));
        dailyRoutine.add(new ActivityItem(13,0, "13:23", "15:35"));
        dailyRoutine.add(new ActivityItem(10,0, "15:36", "15:38"));
        dailyRoutine.add(new ActivityItem(13,0, "15:39", "21:53"));
        dailyRoutine.add(new ActivityItem(5,0,"21:54","22:22"));
        dailyRoutine.add(new ActivityItem(2,0,"22:23","22:51"));
        dailyRoutine.add(new ActivityItem(1,0, "22:52", "23:59"));
    }

    public void clearDailyRoutine(){
        dailyRoutine.clear();
    }
}
