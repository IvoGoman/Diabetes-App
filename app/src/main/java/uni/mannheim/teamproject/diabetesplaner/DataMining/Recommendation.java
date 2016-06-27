package uni.mannheim.teamproject.diabetesplaner.DataMining;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

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
public class Recommendation {

    private final static int INTERVAL = 1000 * 60 * 5; //5 minutes
    Handler mHandler = new Handler();
    private int mId = 0;

    private Context context;

    public Recommendation(Context context){
        this.context = context;
    }

    Runnable mHandlerTask = new Runnable()
    {
        @Override
        public void run() {
            giveDefaultRecommendation(context);
            mHandler.postDelayed(mHandlerTask, INTERVAL);
        }
    };

    void startRecommendation()
    {
        mHandlerTask.run();
    }

    void stopRecommendation()
    {
        mHandler.removeCallbacks(mHandlerTask);
    }

    /**
     * @author Stefan 30.04.2016
     * gives default recommendation
     */
    public void giveDefaultRecommendation(Context context){
        ActivityItem current = getCurrentActivity();

        if(checkSleeptime()){
            sendNotification("You should better go to bed.", context);
        }else if(isStressed()){
            sendNotification("It seems that you are stressed. Better take some insuline.", context);
        }else{
            if(checkInsulin()){
                sendNotification("Doing a low intense exercise would be good for you", context);
            }else{
                sendNotification("You should better do some exercise.", context);
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
     * @param context
     */
    public void sendNotification(String text, Context context){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.account_box)
                        .setContentTitle(context.getResources().getString(R.string.recommendation))
                        .setContentText(text);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, EntryScreenActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
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
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
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
