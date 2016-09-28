package uni.mannheim.teamproject.diabetesplaner.TechnicalServices;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import uni.mannheim.teamproject.diabetesplaner.Domain.myWifi;
import uni.mannheim.teamproject.diabetesplaner.R;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;

/**
 * Created by Naira Ibrahim
 */


public class Wifi extends BroadcastReceiver {

    private final static String TAG = Wifi.class.getSimpleName();
    private static String ssid;
    /**
     * @param context
     * @param intent
     * @author Naira
     */
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
     * As the broadcast receiver is flagged for termination as soon as onReceive() completes,
     * Placing it in a service lets us control the lifetime
     *
     * @author Naira
     */
    public static class WifiActiveService extends Service {

        private final static String TAG = WifiActiveService.class.getSimpleName();
        Date time_from;
        Date time_to;
        Date time_wifi;

        /**
         * @param intent
         * @param flags
         * @param startId
         * @return START_NOT_STICKY
         * @author Naira
         */
        @Override
        public int onStartCommand(final Intent intent, int flags, int startId) {
            final WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            int status = wifiManager.getWifiState();
            final String statusString = Integer.toString(status);


            /**
             * Need to wait a bit for the SSID, if done get null
             */
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    WifiInfo info = wifiManager.getConnectionInfo();
                    String mac = info.getMacAddress();

                    // name of wifi
                    ssid = info.getSSID();

                    Date current_date = new Date();


                    if (wifiManager.getWifiState() == 3 && ssid.equals("")!= true ){ //if wifi status is detected and recognized
                        Log.v(TAG,"status"+ "   "+statusString);

                        //save attributes
                        AppGlobal.getHandler().insertWIFI(ssid, TimeUtils.dateToDateTimeString(current_date));

                        Log.v(TAG, "The SSID & MAC are " + ssid + " " + mac);
                        makePredictionWifi(ssid);
                    }
                    else{ //if wifi can not be detected
                        Log.v(TAG,"status"+ "  "+statusString);
                        Toast.makeText(getApplicationContext(), "Wifi status is unknown", Toast.LENGTH_LONG).show();
                        stopSelf();
                    }
                }
            }, 7000);
            return START_NOT_STICKY;
        }

        /**
         * called when wifi is stopped
         *
         * @author Naira
         */
        @Override
        public void onDestroy() {
            super.onDestroy();
            Toast.makeText(getApplicationContext(), "Wifi data collection stopped", Toast.LENGTH_LONG).show();
            Log.v("wifi", "wifi stopped");
        }

        /**
         * @param intent
         * @return null
         * @author Naira
         */
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        /**
         * Creates a notification displaying the SSID & MAC addr, was used for testing
         *
         * @param ssid
         * @param mac
         */
        public void createNotification(String ssid, String mac) {
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

        /**
         * checks if the wifi name usually gets connected with the chosen Activity but the user
         * used for prediction process
         *
         * @param ssid
         * @author Naira
         */
        public void makePredictionWifi(String ssid) {

            // print in log cat for testing
            System.out.println("MyTest1: in make Prediction wifi");

            ArrayList<myWifi> WifiList = new ArrayList<myWifi>();

            Cursor cursor = AppGlobal.getHandler().getAllWIFIs();

            if (cursor.moveToFirst()) {
                do {
                    //adding all wifis in the DB into a WIFI array list
                    myWifi wifi = new myWifi(cursor.getString(1), cursor.getString(2));
                    WifiList.add(wifi);
                }
                while (cursor.moveToNext());
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
            //get all Activities
            ArrayList<String[]> eventlist = AppGlobal.getHandler().getAllEvents();

            for (int i = 0; i < eventlist.size(); i++) { //loop over activities
                //activity laden
                String[] x = eventlist.get(i);

                //convert date for comparison
                try {
                    time_from = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(x[2]);
                    time_to = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(x[3]);
                    System.out.println(time_from);
                    System.out.println(time_to);
                } catch (ParseException e) {
                    //Handle exception here
                    e.printStackTrace();
                }

                for (int q = 1; q < WifiList.size(); q++) {//loop over wifi array list
                    try {
                        time_wifi = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(WifiList.get(q).getTime());
                    } catch (ParseException e) {
                        //Handle exception here
                        e.printStackTrace();
                    }
                    //check if wifi timestamp occurs in between the timings of the start and end activity
                    if (TimeUtils.isTimeInbetween(time_from, time_to, time_wifi) == true) {
                        System.out.println("relevant Activity for current WIFI: " + x[1]);
                    } else {
                        System.out.println("irrelevant Activity for current WIFI: " + x[1]);
                        /**
                         * edited by leonidgunko
                         */
                        try {
                            AppGlobal.wifiUnusualActivity = AppGlobal.getHandler().getSubactivityID(x[1]);
                        }
                        catch(Exception e){
                            AppGlobal.wifiUnusualActivity=-1;
                        }
                    }
                }
            }
        }
    }
}