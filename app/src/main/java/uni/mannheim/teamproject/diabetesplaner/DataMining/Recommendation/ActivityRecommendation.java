package uni.mannheim.teamproject.diabetesplaner.DataMining.Recommendation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Domain.DayHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.MeasureItem;
import uni.mannheim.teamproject.diabetesplaner.R;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;

/**
 * Created by Stefan on 26.04.2016.
 */
public class ActivityRecommendation extends Recommendation {

    //    private final static int INTERVAL = 1000 * 60 * 5; //5 minutes
    private final static int INTERVAL = 1000 * 10; //10 sec

    Handler mHandler = new Handler();
    private long lastRecommendation = 0;
    private int mIdOffset = 0;

    private MeasureItem previous = null;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public ActivityRecommendation() {
        super("RoutineRecommendationProcess", INTERVAL);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * Launches recommendation process.
     * Recommendation not started if the last recommendation was within a specified interval
     *
     * @author Stefan 29.06.2016
     */
    @Override
    public void recommend() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notify = preferences.getBoolean("pref_key_activity_rec", true);

        if(notify) {
            Log.d("Rec", "recommend based on bsl");
//            mIdOffset = getMidOffset();
            mIdOffset = Recommendation.ACTIVITY_REC;

            long time = System.currentTimeMillis();

            giveBSbasedRecommendation();

            lastRecommendation = time;
        }
    }

    /**
     * gives a recommendation based on the blood sugar level
     *
     * @author Stefan 05.07.2016, edited 08.09.2016
     */
    public void giveBSbasedRecommendation() {
        int period = 6 * 60; //because diabetes 1 people usually measure their bloodsugar level at least 4 times a day
        DataBaseHandler dbHandler = AppGlobal.getHandler();
        MeasureItem bs = getLastBloodsugarlevel(period);
        if(bs != null) {
            Log.d("Rec", "getLastbsl: " + bs.getMeasure_value() + " : " + bs.getTimestamp());
        }else{
            Log.d("Rec", "bs = null");
        }
        MeasureItem insulin = dbHandler.getMostRecentMeasurmentValue(MeasureItem.MEASURE_KIND_INSULIN);

        //there is no measurment within the specified time interval
        if (bs != null){
            if (previous == null) {
                giveBSLbasedRec(bs, insulin);
                previous = bs;
            }else if(!previous.equals(bs)){
                giveBSLbasedRec(bs, insulin);
                previous = bs;
            }
        }
    }

    /**
     * rules for the recommendation based on the blood sugar level
     * @param bs
     * @param insulin
     * @author Stefan 08.09.2016
     */
    private void giveBSLbasedRec(MeasureItem bs, MeasureItem insulin){
        double bsLevel = bs.getMeasureValueInMG();

        String bsString = " ("+bsLevel+" mg/dl)";

        if (150 <= bsLevel && bsLevel < 200) {
            //then Exercise
            sendNotification(getResources().getString(R.string.rec_bs_between_150_200)+bsString, getNewMid());
        } else if (bsLevel >= 200) {
            //then insulin
            if (insulin != null) {
                Date ins = new Date(insulin.getTimestamp());
                Date bsl = new Date(bs.getTimestamp());
                if (ins.before(bsl)) {
                    sendNotification(getResources().getString(R.string.rec_bs_above_200)+bsString, getNewMid());
                }
            } else {
                sendNotification(getResources().getString(R.string.rec_bs_above_200)+bsString, getNewMid());
            }
        } else if (bsLevel < 100) {
            //Eat
            sendNotification(getResources().getString(R.string.rec_bs_blow_100)+bsString, getNewMid());
        }
    }

    /**
     * @return
     * @author Stefan 30.04.2016
     * returns current activity
     */
    public ActivityItem getCurrentActivity() {
        ArrayList<ActivityItem> routine = DayHandler.getDailyRoutine();

        Date current = TimeUtils.getCurrentDate();

        for (int i = 0; i < routine.size(); i++) {
            ActivityItem item = routine.get(i);
            if (TimeUtils.isTimeInbetween(item.getStarttime(), item.getEndtime(), current)) {
                return item;
            }
        }
        return null;
    }

    /**
     * @return
     * @author Stefan 30.04.2016
     * returns previous activity
     */
    public ActivityItem getPreviousActivity() {
        ArrayList<ActivityItem> routine = DayHandler.getDailyRoutine();

        Date current = TimeUtils.getCurrentDate();

        for (int i = 0; i < routine.size(); i++) {
            ActivityItem item = routine.get(i);
            if (TimeUtils.isTimeInbetween(item.getStarttime(), item.getEndtime(), current)) {
                if (i > 0) {
                    return routine.get(i - 1);
                }
            }
        }
        return null;
    }

    /**
     * @return
     * @author Stefan 30.04.2016
     * returns true if it is sleeptime in daily routine at the current time
     */
    public boolean checkSleeptime() {
        ActivityItem item = getCurrentActivity();
        if (item != null) {
            if (item.getActivityId() == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return
     * @author Stefan 30.04.2016
     * checks if current activity is Schreibtischarbeit and returns true if user is stressed
     */
    public boolean isStressed() {
        ActivityItem item = getCurrentActivity();

        if (item != null) {
            if (item.getIntensity() != null) {
                if (item.getIntensity() > 2 && item.getActivityId() == 12) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return true if intensity was high (intensity = 3)
     * @author Stefan 30.04.2016
     * checks if intensity was high or medium/low
     */
    public boolean checkIntensityOfExercise() {
        ActivityItem item = getPreviousActivity();
        if (item != null) {
            if (item.getIntensity() != null) {
                if (item.getIntensity() > 2 && item.getActivityId() == 13) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * returns the last blood sugar measurement from the database
     * in case it is within the specified time interval
     *
     * @param minSinceMM minutes since measurement
     * @return MeasureItem or null if there was no measurement within the specified interval
     * @author Stefan 08.09.2016
     */
    public MeasureItem getLastBloodsugarlevel(int minSinceMM) {
        DataBaseHandler dbHandler = AppGlobal.getHandler();
        MeasureItem bs = dbHandler.getMostRecentMeasurmentValue(MeasureItem.MEASURE_KIND_BLOODSUGAR);
        if (bs == null) {
            return null;
        }else {
            long bs_timestamp = bs.getTimestamp();
            long curr = TimeUtils.getCurrentDate().getTime();

            if ((curr - bs_timestamp) / (60 * 1000) <= minSinceMM) {
                return bs;
            }
        }
        return null;
    }
}
