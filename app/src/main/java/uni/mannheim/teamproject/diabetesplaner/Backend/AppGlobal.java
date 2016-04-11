package uni.mannheim.teamproject.diabetesplaner.Backend;

import android.app.Application;
import android.content.Context;
import android.location.Location;

/**
 * Created by leonidgunko on 27.01.16.
 */
public class AppGlobal extends Application {
    // Values in seconds
    // conditions for 100% Reliability: The calculation "(finTimeWindow - initTimeWindow)/overlapWindow" must lead to an integer
    public static final int initTimeWindow = 6;
    public static final int overlapWindow = 3;
    public static final int finTimeWindow = 12;
    public static final int arraySize = (int) ((finTimeWindow - initTimeWindow)/overlapWindow+1);

    // Value in Hz
    public static final int sampleRate = 50;

    // Calculate the size of accelerometerCache, how many sensor data points in the array
    static double x = (finTimeWindow * sampleRate);

    // Array to store the sensor data for processing
    public static double[][] accelerometerCache = new double[(int) x][4];

    // File to be used for classification. Enables to use individual models or generic models.
    public static String modelDataFileName = "J48.generic";
    private static AppGlobal singleton;
    private static int num;
    private static long time;
    private static Location LastLocation;
    private static DataBaseHandler Handler1;
    private static Context context;
    public AppGlobal getInstance(){
        return singleton;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        time=0;
        AppGlobal.context = getApplicationContext();
        Handler1 = new DataBaseHandler(context);
        Handler1.getAllRoutine(Handler1);
        android.database.sqlite.SQLiteDatabase db;


    }

    public static DataBaseHandler getHandler(){
        return Handler1;
    }

    public static Context getcontext(){
        return context;
    }

    public static void setTime(long time1){
        time = time1;
    }

    public static long getTime(){
        return time;
    }

    public static void setLastLocation(Location loc1){
        LastLocation = loc1;
    }

    public static Location getLastLocation(){
        return LastLocation;
    }
}
