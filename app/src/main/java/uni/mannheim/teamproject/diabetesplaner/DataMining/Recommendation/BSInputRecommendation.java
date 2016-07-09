package uni.mannheim.teamproject.diabetesplaner.DataMining.Recommendation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;

import java.util.ArrayList;
import java.util.Date;
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
    private static final int MIN = 60*1000;
    public static final int INTERVAL = 1*MIN;
    private List clusters;
    private ArrayList<Double> means;
    private Integer lastRecCheck;
    private int mId = 1;

    Handler mHandler = new Handler();
    private int eps = 60;

    public BSInputRecommendation() {
        super("BSInputRecommendationService");
        dbHandler = AppGlobal.getHandler();
    }

    @Override
    public IBinder onBind(Intent intent) {

        setInterval(INTERVAL);
        startRecommendation();

        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {

        stopRecommendation();
        return super.onUnbind(intent);
    }

    /**
     * recommends to input bloodsugar when its the time the user usually inputs the bloodsugar
     */
    @Override
    public void recommend(){
        //check if notifications are switched on
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notify = preferences.getBoolean("pref_key_assistant", true);

        if(notify) {

            Date date = new Date();
            long timestamp = date.getTime();
            int currMinOfDay = TimeUtils.getMinutesOfDay(timestamp);
            dbHandler.getLastBloodsugarMeasurement(dbHandler, 1);

            if (lastRecCheck == null) {
                lastRecCheck = currMinOfDay;
            }

            if (means != null) {
                for (int i = 0; i < means.size(); i++) {
                    int mean = (int) Math.round(means.get(i));

                    //check if the mean value is between the current time and the last check
                    if (mean > lastRecCheck && mean <= currMinOfDay) {
                        MeasureItem mi = dbHandler.getMostRecentMeasurmentValue(dbHandler, MeasureItem.MEASURE_KIND_BLOODSUGAR);
                        //check if there was already a timestamp within the specified radius eps
                        if ((timestamp - mi.getTimestamp()) * 1000 * 60 > eps) {
                            long ts = TimeUtils.minutesOfDayToTimestamp(mean);
                            String usualTime = TimeUtils.getTimeInUserFormat(ts, this);
                            sendNotification("Your usual bloodsugar measurement is at " + usualTime + ". It's time to input your measurement.", mId);
                            lastRecCheck = currMinOfDay;
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
