package uni.mannheim.teamproject.diabetesplaner.Utility;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import java.util.ArrayList;

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;

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
    private static long time;
    private static Location LastLocation;
    private static DataBaseHandler Handler1;
    private static boolean EditFlag;
    public static ArrayList<Integer> gpsUnusualActivities = new ArrayList<>();
    public static ArrayList<Integer> accUnusualActivities = new ArrayList<>();
    public static int wifiUnusualActivity=-1;

    public AppGlobal getInstance(){
        return singleton;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        time=0;
        Handler1 = new DataBaseHandler(this, "mydatabase.db", null, 1);
        SQLiteDatabase db;
        db = Handler1.getReadableDatabase();
        Handler1.getAllRoutine();
    }

    public static DataBaseHandler getHandler(){
        return Handler1;
    }

    public static void setTime(long time1){
        time = time1;
    }

    public static boolean getEditFlag(){
        return EditFlag;
    }

    public static void setEditFlag(boolean b){
        EditFlag = b;
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
