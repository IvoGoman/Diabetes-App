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

import uni.mannheim.teamproject.diabetesplaner.R;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;

// created by: Naira Ibrahim

public class Wifi extends BroadcastReceiver {

    private final static String TAG = Wifi.class.getSimpleName();
    private static String ssid;


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
        Date time_von;
        Date time_bis;
        Date time_wifi;
      //  Wifi currentWifi;

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
                     ssid = info.getSSID();

                    //save
                    Date current_date = new Date();
                    //save attributes
                   // AppGlobal.getHandler().insertWIFI(AppGlobal.getHandler(),ssid, TimeUtils.dateToDateTimeString(current_date));

                  //

                 //   if (Log.isLoggable(TAG, Log.VERBOSE)) {
                        Log.v(TAG, "The SSID & MAC are " + ssid + " " + mac);
                  //  }
                    // could be removed just created to make sure it is working properly
                   // createNotification(ssid, mac);

                    //stopSelf();
                   // makePredictionWifi(ssid);
                }
            }, 3000);
            return START_NOT_STICKY;
        }

        @Override
        public void onDestroy(){
            //stopSelf();
            super.onDestroy();
            Toast.makeText(getApplicationContext(), "Wifi data collection stopped", Toast.LENGTH_LONG).show();
            Log.v("wifi", "wifi stopped");
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        /**
         * Creates a notification displaying the SSID & MAC addr
         * however could be removed later
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

       public void makePredictionWifi(String ssid){

            System.out.println("MyTest1: in make Prediction");

            //get all wifis
            ArrayList<myWifi> WifiList = new ArrayList<myWifi>();


            ////////////////////////
            Cursor cursor = AppGlobal.getHandler().getAllWIFIs(AppGlobal.getHandler());
/////////////////////////////////////////////////////////////////////////////////////////////
            if (cursor.moveToFirst()) {
                do {
                    myWifi wifi = new myWifi(cursor.getString(1), cursor.getString(2));
                    WifiList.add(wifi);
                }
                while (cursor.moveToNext());
            }
           if (!cursor.isClosed()) {
               cursor.close();
           }
            //get all Activities
            ArrayList<String[]> eventlist  = AppGlobal.getHandler().getAllEvents(AppGlobal.getHandler());
            // für alle activities
            for(int i = 0; i < eventlist.size(); i++){ //geh durch alle activities
                //activity laden
                String[] x = eventlist.get(i);
                //id, name, startdate, enddate
                //Daten zum Vergleich konvertieren
                try {
                    time_von = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(x[2]);
                    time_bis = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(x[3]);
                } catch (ParseException e) {
                    //Handle exception here, most of the time you will just log it.
                    e.printStackTrace();
                }

                //schauen ob location timestamp innerhalb einer activity ist
                //for schleife für alle near locations pro einzelne activity

                for(int q = 0; q < WifiList.size(); q++){
                    //timestamp location zwischen start und end zeit von activity dann mögliche activity für diese location
                    try {
                        time_wifi = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(WifiList.get(q).time);

                    } catch (ParseException e) {
                        //Handle exception here, most of the time you will just log it.
                        e.printStackTrace();
                    }

                    if (TimeUtils.isTimeInbetween(time_von, time_bis, time_wifi) == true ) {
                        System.out.println("relevante Activity für aktuelle WIFI: " + x[1]);
                    } else {
                        System.out.println("keine relevante Activity für aktuelle WIFI: " + x[1]);
                    }

                }

            } //for schleife für alle activities
        }

    }
}