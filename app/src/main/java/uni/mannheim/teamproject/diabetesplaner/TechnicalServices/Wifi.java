package uni.mannheim.teamproject.diabetesplaner.TechnicalServices;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import uni.mannheim.teamproject.diabetesplaner.R;

// created by: Naira Ibrahim

public class Wifi extends BroadcastReceiver {

    private final static String TAG = Wifi.class.getSimpleName();

    @Override
    public void onReceive(final Context context, final Intent intent) {
        int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction()) && WifiManager.WIFI_STATE_ENABLED == wifiState) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "Wifi is  on");
            }
            context.startService(new Intent(context, WifiActiveService.class));
        }
    }

    /**
     * Getting the network info and displaying the notification is handled in a service
     * as we need to delay fetching the SSID name.
     *
     * As the broadcast receiver is flagged for termination as soon as onReceive() completes,
     *  Placing it in a service lets us control the lifetime.
     */
    public static class WifiActiveService extends Service {

        private final static String TAG = WifiActiveService.class.getSimpleName();

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            final WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            // Need to wait a bit for the SSID
            // if done get null
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    WifiInfo info = wifiManager.getConnectionInfo();
                    String mac = info.getMacAddress();
                    // name of wifi
                    String ssid = info.getSSID();
                    if (Log.isLoggable(TAG, Log.VERBOSE)) {
                        Log.v(TAG, "The SSID & MAC are " + ssid + " " + mac);
                    }
                    // could be removed just created to make sure it is working properly
                    createNotification(ssid, mac);
                    stopSelf();
                }
            }, 5000);
            return START_NOT_STICKY;
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        /**
         * Creates a notification displaying the SSID & MAC addr
         * however could be removed later
         */
        private void createNotification(String ssid, String mac) {
            Notification n = new NotificationCompat.Builder(this)
                    .setContentTitle("WifiReceiver Connection")
                    .setContentText("Connected to " + ssid)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("You're connected to " + ssid + " at " + mac))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .notify(0, n);
        }
    }
}