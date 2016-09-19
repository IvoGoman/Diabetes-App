package uni.mannheim.teamproject.diabetesplaner.DataMining.Recommendation;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.MeasureItem;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;

/**
 * @author Stefan 09.07.2016
 */
public class BSInputRecommendation extends Recommendation {
    private DataBaseHandler dbHandler;
    public static final int INTERVAL = 60*1000;
    private List clusters;
    private ArrayList<Double> means;
    private HashMap<Integer, Integer> mIds = new HashMap<>();
    private int mIdOffset;
    private int eps = 45;

    public BSInputRecommendation() {
        super("BSInputRecommendationService");
        dbHandler = AppGlobal.getHandler();

//        createFakeBS();
        findClusters(5, eps);
        setInterval(INTERVAL);

    }

    /**
     * creates fake bloodsugar values
     */
    public void createFakeBS(){

        for(int i=0; i<20; i++) {
            int day = (int)(Math.random()*30)+1;
            java.sql.Date date = java.sql.Date.valueOf("2016-06-" + day);

            int hour = (int)(Math.random())+20;
            int min = (int)(Math.random()*60);
            int sec = (int)(Math.random()*60);

            Time time = new Time(hour, min, sec);
            long timestamp =TimeUtils.convertDateAndTimeStringToDate(String.valueOf(date),String.valueOf(time)).getTime();
            MeasureItem item = new MeasureItem(timestamp, 150,"mg/dl",MeasureItem.MEASURE_KIND_BLOODSUGAR);
            dbHandler.insertMeasurement(item,1);
        }

        for(int i=0; i<6; i++) {
            int day = (int)(Math.random()*30)+1;
            java.sql.Date date = java.sql.Date.valueOf("2016-06-" + day);

            int hour = (int)(Math.random())+10;
            int min = (int)(Math.random()*60);
            int sec = (int)(Math.random()*60);

            Time time = new Time(hour, min, sec);

            long timestamp = TimeUtils.convertDateAndTimeStringToDate(String.valueOf(date),String.valueOf(time)).getTime();
            MeasureItem item = new MeasureItem(timestamp, 150,"mg/dl",MeasureItem.MEASURE_KIND_BLOODSUGAR);
            dbHandler.insertMeasurement(item,1);
        }

        java.sql.Date date = java.sql.Date.valueOf("2016-07-11");

        int hour = 21;
        int min = 10;
        int sec = 3;

        Time time = new Time(hour, min, sec);
        long timestamp = TimeUtils.convertDateStringToTimestamp(TimeUtils.convertDateAndTimeStringToDate(String.valueOf(date),String.valueOf(time)));
        MeasureItem item = new MeasureItem(timestamp, 150,"mg/dl",MeasureItem.MEASURE_KIND_BLOODSUGAR);
        dbHandler.insertMeasurement(item,1);
    }

//    @Override
//    public IBinder onBind(Intent intent) {
//
//        setInterval(INTERVAL);
//        startRecommendation();
//
//        return super.onBind(intent);
//    }
//
//    @Override
//    public boolean onUnbind(Intent intent) {
//
//        stopRecommendation();
//        return super.onUnbind(intent);
//    }

    /**
     * recommends to input bloodsugar when its the time the user usually inputs the bloodsugar
     * @author Stefan
     */
    @Override
    public void recommend(){
        //check if notifications are switched on
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notify = preferences.getBoolean("pref_key_bs_input", true);

        if(notify) {
//            mIdOffset = getMidOffset();
            mIdOffset = Recommendation.BS_REC;
            //for testing
            for(int i=0;i<means.size(); i++){
                int tmp = (int)((double)means.get(i));
                String date = TimeUtils.getTimeInUserFormat(TimeUtils.minutesOfDayToTimestamp(tmp), this);
                Log.d(TAG,"Cluster Mean: " + date);
            }

            Date date = new Date();
            long timestamp = date.getTime();
            int currMinOfDay = TimeUtils.getMinutesOfDay(timestamp);
            dbHandler.getLastBloodsugarMeasurement(dbHandler, 1);

            MeasureItem mi = dbHandler.getMostRecentMeasurmentValue(dbHandler, MeasureItem.MEASURE_KIND_BLOODSUGAR);


            if (means != null) {
                for (int i = 0; i < means.size(); i++) {
                    int mean = (int) ((double)means.get(i));
                    long meanTs = TimeUtils.minutesOfDayToTimestamp(mean);
                    mIds.put(mean,mIdOffset+i);


//                    Log.d(TAG, currMinOfDay-eps + " <= " + mean + " <= " + currMinOfDay+eps);
                    if(currMinOfDay-eps <= mean && mean <= currMinOfDay+eps){
                        //check if there wasn't already a measurement input within the specified radius eps
//                        Log.d(TAG, "timestamp: " + TimeUtils.getTimeStampAsDateString(timestamp) + " - "
//                                + TimeUtils.getTimeStampAsDateString(mi.getTimestamp()) + " > " + eps + " = "
//                                + ((timestamp - mi.getTimestamp())/1000/60) + " > " + eps);
                        if(mi != null) {
                            if ((timestamp - mi.getTimestamp()) / 1000 / 60 > eps) {
                                String usualTime = TimeUtils.getTimeInUserFormat(meanTs, this);
                                sendNotification("Your usual bloodsugar measurement is at " + usualTime + ". It's time to input your measurement.", mIds.get(mean));
                                Log.d(TAG, "time to input bloodsugar");
                            }
                        }else{
                            String usualTime = TimeUtils.getTimeInUserFormat(meanTs, this);
                            sendNotification("Your usual bloodsugar measurement is at " + usualTime + ". It's time to input your measurement.", mIds.get(mean));
                            Log.d(TAG, "time to input bloodsugar");
                        }
                    }
                }
            }
        }
    }

    /**
     * finds clusters in bloodsugar measurement inputs
     * @param minPts minimum points within a cluster
     * @param eps radius within the minPts have to occur
     * @return list with clusters
     * @author Stefan 09.07.2016
     */
    public void findClusters(int minPts, int eps){
        this.eps = eps;

        DBSCANClusterer dbScan = new DBSCANClusterer(eps, minPts);

        clusters = dbScan.cluster(getBloodsugarMeasurments());
        means = getClusterMeans(clusters);
    }

    /**
     * returns the mean of every cluster in a list
     * @param cs list with clusters
     * @return ArrayList with the means
     */
    public ArrayList<Double> getClusterMeans(List<Cluster<DoublePoint>> cs){
        ArrayList<Double> means = new ArrayList<>();
        for(int i=0; i<cs.size(); i++){
            List<DoublePoint> c = cs.get(i).getPoints();
            means.add(calcAvg(c));
        }

        return means;
    }

    /**
     * calculates the average of a list with double points.
     * Assumes that a double point is 1-dimensional (i.e. y = 0)
     * @param list
     * @return
     * @author Stefan 09.07.2016
     */
    private double calcAvg(List<DoublePoint> list){
        double sum = 0d;
        for(int i=0; i<list.size(); i++){
            DoublePoint dp = list.get(i);
            sum+= dp.getPoint()[0];
        }
        return sum/list.size();
    }

    /**
     * creates an ArrayList with points where <br>
     * x = minute of day where the measurement input occurred <br>
     * y = 0
     * @return
     * @author Stefan 09.07.2016
     */
    private ArrayList<DoublePoint> getBloodsugarMeasurments(){
        ArrayList<MeasureItem> mms = dbHandler.getAllMeasurementValues(dbHandler, "bloodsugar");
        ArrayList<DoublePoint> tmpList = new ArrayList<>();
        for(int i=0; i<mms.size(); i++){
            double[] arr = new double[2];

            int minOfDay = TimeUtils.getMinutesOfDay(mms.get(i).getTimestamp());

            arr[0] = minOfDay;
            arr[1] = 0;

            tmpList.add(new DoublePoint(arr));
        }

        return tmpList;
    }
}
