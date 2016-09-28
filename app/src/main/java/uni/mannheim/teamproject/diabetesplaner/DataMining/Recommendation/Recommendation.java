package uni.mannheim.teamproject.diabetesplaner.DataMining.Recommendation;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import uni.mannheim.teamproject.diabetesplaner.R;
import uni.mannheim.teamproject.diabetesplaner.UI.EntryScreenActivity;

/**
 * @author Stefan 09.07.2016
 */
public abstract class Recommendation extends Service {
    public static final int ACTIVITY_REC = 0;
    public static final int BS_REC = 1;
    public static final int FOOD_REC = 2;

    public static final String TAG = "REC";
    public static final int MIN = 1000*60;
    private static int offset = 1;
    private int midOffset;
    private static int mid = 100;
    private int interval = MIN*60;

    private Handler mHandler = new Handler();
    private String name;


    /**
     * Constructor. Initializes midOffset for identification of notifications
     * @param name
     * @author Stefan
     */
    public Recommendation(String name, final int interval) {
//        super(name);
        this.name = name;
//        this.midOffset = offset;
//        offset += 100;
        this.interval = interval;
    }

    Runnable mHandlerTask = new Runnable(){
        @Override
        public void run() {
            recommend();

            mHandler.postDelayed(mHandlerTask, interval);
        }
    };

//    @Override
//    protected void onHandleIntent(Intent intent) {
//
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        startRecommendation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Log.d("Rec", "destroy");
        stopRecommendation();
    }

    /**
     * Override in Subclass.
     * Should contain the process for a single recommendation
     * @author Stefan
     */
    public abstract void recommend();

    public void startRecommendation(){
//        Log.d("Rec", name + " started");
        mHandlerTask.run();
    }

    public void stopRecommendation(){
//        Log.d("Rec", name + " stopped");
        mHandler.removeCallbacks(mHandlerTask);
    }

//    public void setInterval(int interval){
//        this.interval = interval;
//    }

    /**
     * sends a notification to the android system
     * @param text
     * @param mId
     * @author Stefan
     */
    public void sendNotification(String text, int mId){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notify = preferences.getBoolean("pref_key_assistant", true);

        if(notify) {

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Notification.Builder mBuilder =
                    new Notification.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
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

            // mId allows you to update the notification later on.
            mNotificationManager.notify(mId, notification);

            //vibrate
            if(preferences.getBoolean("pref_key_vibrate", true)){
                vibrate(getApplicationContext(), 200);
            }

            //play notification sound
            if (preferences.getBoolean("pref_key_sound", true)){
                playSound();
            }
        }
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     * @author Stefan
     */
//    public class RecBinder extends Binder {
//        public Recommendation getService() {
//            // Return this instance of LocalService so clients can call public methods
//            return Recommendation.this;
//        }
//    }

    public String getName(){
        return this.name;
    }

    /**
     * performs a vibrate
     *
     * @param context context
     * @param millis  milliseconds to vibrate
     * @author Stefan 23.09.2016
     */
    private void vibrate(Context context, int millis) {
        Vibrator vibr = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibr.vibrate(millis);
    }

    /**
     * plays a notification sound
     * @author Stefan 23.09.2016
     */
    private void playSound(){
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getNewMid(){
        return ++mid;
    }
}
