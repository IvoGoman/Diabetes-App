package uni.mannheim.teamproject.diabetesplaner.DataMining.Recommendation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Domain.DayHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.MeasureItem;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;
import uni.mannheim.teamproject.diabetesplaner.Utility.Util;

/**
 * Created by Stefan on 26.04.2016.
 */
public class RoutineRecommendation extends Recommendation {

//    private final static int INTERVAL = 1000 * 60 * 5; //5 minutes
    private final static int INTERVAL = MIN * 1; //10 sec

    private final static int DEFAUL_REC = 0;
    private final static int FIRST_REC = 1;

    Handler mHandler = new Handler();
    private long lastRecommendation = 0;
    private int mIdOffset = 0;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public RoutineRecommendation() {
        super("RoutineRecommendationProcess");
    }


    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        setInterval(INTERVAL);
        startRecommendation();

        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this, "service stopped", Toast.LENGTH_SHORT).show();

        stopRecommendation();
        return super.onUnbind(intent);
    }

//    if Blood Sugar Level = 100=<x<200 then Exercise  (81 / 0 / 0)
//
//    if Blood Sugar Level = >=200 then insulin  (0 / 59 / 0)
//
//    else Eat  (0 / 0 / 19)

    /**
     * Launches recommendation process.
     * Recommendation not started if the last recommendation was within a specified interval
     * @author Stefan 29.06.2016
     */
    @Override
    public void recommend(){
        mIdOffset = getMidOffset();

        //switch between different recommendation methods
        int rec = FIRST_REC;
        long time= System.currentTimeMillis();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notify = preferences.getBoolean("pref_key_assistant", true);
        if(time-lastRecommendation>=INTERVAL && notify){
            switch(rec) {
                case DEFAUL_REC:
                    giveDefaultRecommendation();
                    break;
                case FIRST_REC:
                    giveFirstRecommendation();
                    break;
            }
        }
        lastRecommendation = time;
    }

    /**
     * first recommendation process
     * @author Stefan 05.07.2016
     */
    public void giveFirstRecommendation(){
        int period = 5;
        double bsLevel = getLastBloodsugarlevel(period);

        //there is no measurment within specified time interval
        if(bsLevel == 0){
            //TODO Ammars rules missing
            sendNotification("No blood sugar level measurement within the last " + period + " hours. " +
                    "TODO: give recommendation based on activities" , mIdOffset);
        }else{
            if(100<= bsLevel &&  bsLevel < 200){
                //then Exercise
                sendNotification("You should better do some exercise because your blood sugar level is high!", mIdOffset);
            } else if(bsLevel >=200){
                //then insulin
                sendNotification("You should take insulin because your blood sugar is way to high!", mIdOffset);
            } else{
                //Eat
                sendNotification("Your blood sugar is low, have a meal.", mIdOffset);
            }
        }
    }

    /**
     * gives default recommendation
     * @author Stefan 30.04.2016
     */
    public void giveDefaultRecommendation(){
        ActivityItem current = getCurrentActivity();

        if(checkSleeptime()){
            sendNotification("You should better go to bed.", mIdOffset);
        }else if(isStressed()){
            sendNotification("It seems that you are stressed. Better take some insuline.", mIdOffset);
        }else{
            if(checkInsulin()){
                sendNotification("Doing a low intense exercise would be good for you", mIdOffset);
            }else{
                sendNotification("You should better do some exercise.", mIdOffset);
            }
        }

        if(isExercise(current)){

        }
        if(isLowIntenseExercise(current)){
            if(checkSleeptime()){

            }
        }
    }

    public boolean isExercise(ActivityItem curr){
        return false;
    }

    public boolean isLowIntenseExercise(ActivityItem curr){
        return false;
    }


    /**
     * @author Stefan 30.04.2016
     * returns current activity
     * @return
     */
    public ActivityItem getCurrentActivity(){
        ArrayList<ActivityItem> routine = DayHandler.getDailyRoutine();

        Date current = TimeUtils.getCurrentDate();

        for(int i=0; i<routine.size(); i++) {
            ActivityItem item = routine.get(i);
            if(TimeUtils.isTimeInbetween(item.getStarttime(), item.getEndtime(), current)){
                return item;
            }
        }
        return null;
    }

    /**
     * @author Stefan 30.04.2016
     * returns previous activity
     * @return
     */
    public ActivityItem getPreviousActivity(){
        ArrayList<ActivityItem> routine = DayHandler.getDailyRoutine();

        Date current = TimeUtils.getCurrentDate();

        for(int i=0; i<routine.size(); i++) {
            ActivityItem item = routine.get(i);
            if(TimeUtils.isTimeInbetween(item.getStarttime(), item.getEndtime(), current)){
                if(i>0) {
                    return routine.get(i-1);
                }
            }
        }
        return null;
    }

    /**
     * @author Stefan 30.04.2016
     * returns true if it is sleeptime in daily routine at the current time
     * @return
     */
    public boolean checkSleeptime(){
        ActivityItem item = getCurrentActivity();
        if(item != null) {
            if (item.getActivityId() == 1) {
                return true;
            }
        }
        return false;
    }

    public boolean checkInsulin(){
        DataBaseHandler dbHandler = AppGlobal.getHandler();
        return false;
    }

    /**
     * @author Stefan 30.04.2016
     * checks if current activity is Schreibtischarbeit and returns true if user is stressed
     * @return
     */
    public boolean isStressed(){
        ActivityItem item = getCurrentActivity();

        if(item != null){
            if(item.getIntensity() != null){
                if(item.getIntensity() > 2 && item.getActivityId() == 12){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @author Stefan 30.04.2016
     * checks if intensity was high or medium/low
     * @return true if intensity was high (intensity = 3)
     */
    public boolean checkIntensityOfExercise(){
        ActivityItem item = getPreviousActivity();
        if(item != null){
            if(item.getIntensity() != null) {
                if (item.getIntensity() > 2 && item.getActivityId() == 13) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @author Stefan 03.07.2016
     * returns the most recent bloodsugar level in mmol within the specified time interval
     * return 0 if no measurement was found
     * @param hoursSinceMM time period in hours before the actual to time in
     *                      that the measurement should be to be taken into account
     * @return
     */
    public double getLastBloodsugarlevel(int hoursSinceMM){
        Date curr = TimeUtils.getCurrentDate();

        DataBaseHandler dbHandler = AppGlobal.getHandler();
        ArrayList<MeasureItem> mms = dbHandler.getMeasurementValues(dbHandler, curr, "DAY", MeasureItem.MEASURE_KIND_BLOODSUGAR);

        MeasureItem last = null;
        long timediff = Long.MAX_VALUE;

        //find recent measurement
        for(int i=0; i<mms.size(); i++){
            //compares actual time with time from measurement
            long tmpDiff = curr.getTime()-mms.get(i).getTimestamp();
            if(tmpDiff <timediff){
                last = mms.get(i);
                timediff = tmpDiff;
            }
        }

        long timeSinceMM = TimeUnit.MILLISECONDS.toMinutes(timediff);

        if(timeSinceMM <hoursSinceMM && last!= null){
            double value = last.getMeasure_value();
            switch (last.getMeasure_unit()) {
                case "%":
                    return Util.miligram_to_mol(Util.percentage_to_mg(value));

                case "mmol/l":
                    return value;

                case "mg/dl":
                    return Util.miligram_to_mol(value);
            }
        }
        return 0;
    }

}
