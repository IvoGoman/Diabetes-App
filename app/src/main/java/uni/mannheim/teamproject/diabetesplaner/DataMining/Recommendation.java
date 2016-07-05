package uni.mannheim.teamproject.diabetesplaner.DataMining;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Domain.DayHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.MeasureItem;
import uni.mannheim.teamproject.diabetesplaner.R;
import uni.mannheim.teamproject.diabetesplaner.UI.EntryScreenActivity;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;
import uni.mannheim.teamproject.diabetesplaner.Utility.Util;

/**
 * Created by Stefan on 26.04.2016.
 */
public class Recommendation extends IntentService {

//    private final static int INTERVAL = 1000 * 60 * 5; //5 minutes
    private final static int INTERVAL = 1000 * 10; //10 sec

    private final static int DEFAUL_REC = 0;
    private final static int FIRST_REC = 1;

    Handler mHandler = new Handler();
    private int mId = 0;
    private long lastRecommendation = 0;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public Recommendation() {
        super("recommendationProcess");
    }


    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        startRecommendation();

        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this, "service stopped", Toast.LENGTH_SHORT).show();

        stopRecommendation();
        return super.onUnbind(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        recommend();
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class RecBinder extends Binder {
        public Recommendation getService() {
            // Return this instance of LocalService so clients can call public methods
            return Recommendation.this;
        }
    }


    Runnable mHandlerTask = new Runnable(){
        @Override
        public void run() {
            Log.d("Rec","recommend");
            recommend(FIRST_REC);

            mHandler.postDelayed(mHandlerTask, INTERVAL);
        }
    };

    void startRecommendation(){
        mHandlerTask.run();
    }

    void stopRecommendation(){
        Log.d("Rec", "stopped");
        mHandler.removeCallbacks(mHandlerTask);
    }

//    if Blood Sugar Level = 100=<x<200 then Exercise  (81 / 0 / 0)
//
//    if Blood Sugar Level = >=200 then insulin  (0 / 59 / 0)
//
//    else Eat  (0 / 0 / 19)

    /**
     * Launches recommendation process.
     * Recommendation not started if the last recommendation was within a specified interval
     * @param rec switch between different recommendation methods
     * @author Stefan 29.06.2016
     */
    public void recommend(int rec){
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
            //TODO
            sendNotification("No blood sugar level measurement within the last " + period + " hours. " +
                    "TODO: give recommendation based on activities" );
        }else{
            if(100<= bsLevel &&  bsLevel < 200){
                //then Exercise
                sendNotification("You should better do some exercise because your blood sugar level is high!");
            } else if(bsLevel >=200){
                //then insulin
                sendNotification("You should take insulin because your blood sugar is way to high!");
            } else{
                //Eat
                sendNotification("Your blood sugar is low, have a meal.");
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
            sendNotification("You should better go to bed.");
        }else if(isStressed()){
            sendNotification("It seems that you are stressed. Better take some insuline.");
        }else{
            if(checkInsulin()){
                sendNotification("Doing a low intense exercise would be good for you");
            }else{
                sendNotification("You should better do some exercise.");
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
     * sends a notification to the android system
     * @param text
     */
    public void sendNotification(String text){
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder mBuilder =
                new Notification.Builder(this)
                        .setSmallIcon(R.drawable.account_box)
                        .setContentTitle(getResources().getString(R.string.recommendation))
                        .setContentText(text);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, EntryScreenActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(EntryScreenActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);


        Notification notification = new Notification.BigTextStyle(mBuilder)
                .bigText(text).build();
        mNotificationManager.notify(0, notification);

        // mId allows you to update the notification later on.
        mNotificationManager.notify(mId, notification);

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
        ArrayList<MeasureItem> mms = dbHandler.getMeasurementValues(dbHandler, curr, "DAY", "bloodsugar");

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
