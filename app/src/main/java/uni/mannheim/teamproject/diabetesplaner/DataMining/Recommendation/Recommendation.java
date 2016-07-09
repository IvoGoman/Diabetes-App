package uni.mannheim.teamproject.diabetesplaner.DataMining.Recommendation;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import uni.mannheim.teamproject.diabetesplaner.R;
import uni.mannheim.teamproject.diabetesplaner.UI.EntryScreenActivity;

/**
 * @author Stefan 09.07.2016
 */
public abstract class Recommendation extends IntentService {
    public static final int ROUTINE_REC = 0;
    public static final int BS_REC = 1;
    private Handler mHandler = new Handler();
    private String name;
    private int interval = 1000*60;


    public Recommendation(String name) {
        super(name);
        this.name = name;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    /**
     * Override in Subclass.
     * Should contain the process for a single recommendation
     */
    public abstract void recommend();

    Runnable mHandlerTask = new Runnable(){
        @Override
        public void run() {
            Log.d("Rec","recommend bloodsugar input");
            recommend();

            mHandler.postDelayed(mHandlerTask, interval);
        }
    };

    public void startRecommendation(){
        Log.d("Rec", name + " started");
        mHandlerTask.run();
    }

    public void stopRecommendation(){
        Log.d("Rec", name + " stopped");
        mHandler.removeCallbacks(mHandlerTask);
    }

    public void setInterval(int interval){
        this.interval = interval;
    }

    /**
     * sends a notification to the android system
     * @param text
     * @param mId
     */
    public void sendNotification(String text, int mId){
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
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class RecBinder extends Binder {
        public Recommendation getService() {
            // Return this instance of LocalService so clients can call public methods
            return Recommendation.this;
        }
    }
}
