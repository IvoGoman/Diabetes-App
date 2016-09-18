package uni.mannheim.teamproject.diabetesplaner.DataMining.Recommendation;

import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
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

    private final static int DEFAUL_REC = 0;
    private final static int FIRST_REC = 1;

    Handler mHandler = new Handler();
    private long lastRecommendation = 0;
    private int mIdOffset = 0;

    private MeasureItem previous = null;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public ActivityRecommendation() {
        super("RoutineRecommendationProcess");
        setInterval(INTERVAL);
    }


//    @Override
//    public IBinder onBind(Intent intent) {
//        setInterval(INTERVAL);
//        startRecommendation();
//
//        return super.onBind(intent);
//    }
//
//    @Override
//    public boolean onUnbind(Intent intent) {
//        stopRecommendation();
//        return super.onUnbind(intent);
//    }

//    if Blood Sugar Level = 100=<x<200 then Exercise  (81 / 0 / 0)
//
//    if Blood Sugar Level = >=200 then insulin  (0 / 59 / 0)
//
//    else Eat  (0 / 0 / 19)

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

            //switch between different recommendation methods
            int rec = FIRST_REC;
            long time = System.currentTimeMillis();

            switch (rec) {
                case DEFAUL_REC:
                    giveDefaultRecommendation();
                    break;
                case FIRST_REC:
                    giveBSbasedRecommendation();
                    break;
            }

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
        MeasureItem insulin = dbHandler.getMostRecentMeasurmentValue(dbHandler, MeasureItem.MEASURE_KIND_INSULIN);

        //there is no measurment within the specified time interval
        if (bs == null) {
//            //TODO Ammars rules missing
//            sendNotification("No blood sugar level measurement within the last " + period / 60 + " hours " + period % 60 + " minutes." +
//                    "TODO: give recommendation based on activities", mIdOffset);
        } else {
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
            sendNotification(getResources().getString(R.string.rec_bs_between_150_200)+bsString, mIdOffset);
        } else if (bsLevel >= 200) {
            //then insulin
            if (insulin != null) {
                Date ins = new Date(insulin.getTimestamp());
                Date bsl = new Date(bs.getTimestamp());
                if (ins.before(bsl)) {
                    sendNotification(getResources().getString(R.string.rec_bs_above_200)+bsString, mIdOffset);
                }
            } else {
                sendNotification(getResources().getString(R.string.rec_bs_above_200)+bsString, mIdOffset);
            }
        } else if (bsLevel < 100) {
            //Eat
            sendNotification(getResources().getString(R.string.rec_bs_blow_100)+bsString, mIdOffset);
        }
    }

    /**
     * gives default recommendation
     *
     * @author Stefan 30.04.2016
     */
    public void giveDefaultRecommendation() {
        ActivityItem current = getCurrentActivity();

        if (checkSleeptime()) {
            sendNotification("You should better go to bed.", mIdOffset);
        } else if (isStressed()) {
            sendNotification("It seems that you are stressed. Better take some insuline.", mIdOffset);
        } else {
            if (checkInsulin()) {
                sendNotification("Doing a low intense exercise would be good for you", mIdOffset);
            } else {
                sendNotification("You should better do some exercise.", mIdOffset);
            }
        }

        if (isExercise(current)) {

        }
        if (isLowIntenseExercise(current)) {
            if (checkSleeptime()) {

            }
        }
    }

    public boolean isExercise(ActivityItem curr) {
        return false;
    }

    public boolean isLowIntenseExercise(ActivityItem curr) {
        return false;
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

    public boolean checkInsulin() {
        DataBaseHandler dbHandler = AppGlobal.getHandler();
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
        MeasureItem bs = dbHandler.getMostRecentMeasurmentValue(dbHandler, MeasureItem.MEASURE_KIND_BLOODSUGAR);
        if (bs == null) {
            return null;
        }else {
            long bs_timestamp = bs.getTimestamp();
            long curr = TimeUtils.getCurrentDate().getTime();

            if ((curr - bs_timestamp) / (60 * 1000) <= minSinceMM) {
                return bs;
            }
//            return bs;
        }
        return null;
    }

    /**
     * @author Stefan 03.07.2016
     * returns the most recent bloodsugar level in mmol within the specified time interval
     * return 0 if no measurement was found
     * @param hoursSinceMM time period in hours before the actual to time in
     *                      that the measurement should be to be taken into account
     * @return
     */
//    public double getLastBloodsugarlevel(int hoursSinceMM){
//        Date curr = TimeUtils.getCurrentDate();
//
//        DataBaseHandler dbHandler = AppGlobal.getHandler();
//        ArrayList<MeasureItem> mms = dbHandler.getMeasurementValues(dbHandler, curr, "DAY", MeasureItem.MEASURE_KIND_BLOODSUGAR);
//
//        MeasureItem last = null;
//        long timediff = Long.MAX_VALUE;
//
//        //find recent measurement
//        for(int i=0; i<mms.size(); i++){
//            //compares actual time with time from measurement
//            long tmpDiff = curr.getTime()-mms.get(i).getTimestamp();
//            if(tmpDiff <timediff){
//                last = mms.get(i);
//                timediff = tmpDiff;
//            }
//        }
//
//        long timeSinceMM = TimeUnit.MILLISECONDS.toMinutes(timediff);
//
//        if(timeSinceMM <hoursSinceMM && last!= null){
//            double value = last.getMeasure_value();
//            switch (last.getMeasure_unit()) {
//                case "%":
//                    return Util.miligram_to_mol(Util.percentage_to_mg(value));
//
//                case "mmol/l":
//                    return value;
//
//                case "mg/dl":
//                    return Util.miligram_to_mol(value);
//            }
//        }
//        return 0;
//    }

}
