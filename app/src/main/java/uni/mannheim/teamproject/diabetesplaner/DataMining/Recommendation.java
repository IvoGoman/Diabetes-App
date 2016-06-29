package uni.mannheim.teamproject.diabetesplaner.DataMining;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Domain.DayHandler;
import uni.mannheim.teamproject.diabetesplaner.R;
import uni.mannheim.teamproject.diabetesplaner.UI.EntryScreenActivity;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;

/**
 * Created by Stefan on 26.04.2016.
 */
public class Recommendation extends IntentService {

    private final static int INTERVAL = 1000 * 60 * 5; //5 minutes
//    private final static int INTERVAL = 1000 * 10; //10 sec

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


    Runnable mHandlerTask = new Runnable(){
        @Override
        public void run() {
            Log.d("Rec","recommend");
            recommend();
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

    /**
     * @author Stefan 29.06.2016
     * Launches recommendation process.
     * Recommendation not started if the last recommendation was within a specified interval
     */
    public void recommend(){
        long time= System.currentTimeMillis();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notify = preferences.getBoolean("pref_key_assistant", true);
        if(time-lastRecommendation>=INTERVAL && notify){
            giveDefaultRecommendation();
        }
        lastRecommendation = time;
    }

    /**
     * @author Stefan 30.04.2016
     * gives default recommendation
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
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
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
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(mId, mBuilder.build());

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
     * returns current activity
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
}
