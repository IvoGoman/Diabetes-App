package uni.mannheim.teamproject.diabetesplaner.Backend;

import android.app.Application;
import android.content.Context;
import android.location.Location;

/**
 * Created by leonidgunko on 27.01.16.
 */
public class AppGlobal extends Application {
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

        //android.database.sqlite.SQLiteDatabase db;
    }

    public static DataBaseHandler getHandler(){
        return AppGlobal.Handler1;
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
