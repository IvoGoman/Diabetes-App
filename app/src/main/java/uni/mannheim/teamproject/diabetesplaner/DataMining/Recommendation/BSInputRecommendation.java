package uni.mannheim.teamproject.diabetesplaner.DataMining.Recommendation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

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
import uni.mannheim.teamproject.diabetesplaner.R;
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
    private int eps = 60;
    private long tsLastNoficiation = 0;

    public BSInputRecommendation() {
        super("BSInputRecommendationService", INTERVAL);
        dbHandler = AppGlobal.getHandler();

//        createFakeBS();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * creates fake bloodsugar values for testing
     * @author Stefan
     */
    public void createFakeBS(){

        for(int i=0; i<20; i++) {
            int day = (int)(Math.random()*30)+1;
            String date = "";
            if(day<10){
                date = "2016.06.0" + day;
            }else{
                date = "2016.06." + day;
            }

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
            String date = "";
            if(day<10){
                date = "2016.06.0" + day;
            }else{
                date = "2016.06." + day;
            }

            int hour = (int)(Math.random())+12;
            int min = (int)(Math.random()*60);
            int sec = (int)(Math.random()*60);

            Time time = new Time(hour, min, sec);

            long timestamp = TimeUtils.convertDateAndTimeStringToDate(String.valueOf(date),String.valueOf(time)).getTime();
            MeasureItem item = new MeasureItem(timestamp, 150,"mg/dl",MeasureItem.MEASURE_KIND_BLOODSUGAR);
            dbHandler.insertMeasurement(item,1);
        }

        String date = "2016.07.11";

        int hour = 21;
        int min = 10;
        int sec = 3;

        Time time = new Time(hour, min, sec);
        long timestamp = TimeUtils.convertDateAndTimeStringToDate(String.valueOf(date),String.valueOf(time)).getTime();
        MeasureItem item = new MeasureItem(timestamp, 150,"mg/dl",MeasureItem.MEASURE_KIND_BLOODSUGAR);
        dbHandler.insertMeasurement(item,1);
    }

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
            findClusters(5, eps);

//            mIdOffset = getMidOffset();
            mIdOffset = Recommendation.BS_REC;
            //for testing
            for(int i=0;i<means.size(); i++){
                int tmp = (int)((double)means.get(i));
                String date = TimeUtils.getTimeInUserFormat(TimeUtils.minutesOfDayToTimestamp(tmp), this);
//                Log.d(TAG,"Cluster Mean: " + date);
            }

            Date date = new Date();
            long timestamp = date.getTime();
            int currMinOfDay = TimeUtils.getMinutesOfDay(timestamp);
            dbHandler.getLastBloodsugarMeasurement(1);

            MeasureItem mi = dbHandler.getMostRecentMeasurmentValue(MeasureItem.MEASURE_KIND_BLOODSUGAR);


            if (means != null) {
                for (int i = 0; i < means.size(); i++) {
                    int mean = (int) ((double) means.get(i));
                    long meanTs = TimeUtils.minutesOfDayToTimestamp(mean);
                    mIds.put(mean, mIdOffset + i);

                    long currTime = new Date().getTime();

                    if (!(currTime - (eps * 60000) <= tsLastNoficiation && tsLastNoficiation <= currTime + (eps * 60000))) {
                        if (currMinOfDay - eps <= mean && mean <= currMinOfDay + eps) {
                            //check if there wasn't already a measurement input within the specified radius eps
                            if (mi != null) {
                                if ((timestamp - mi.getTimestamp()) / 1000 / 60 > eps) {
                                    String usualTime = TimeUtils.getTimeInUserFormat(meanTs, this);
                                    sendNotification(getResources().getString(R.string.usualBSM) + " " + usualTime + ". " + getResources().getString(R.string.timeToInputBS), mIds.get(mean));
//                                    Log.d(TAG, "time to input bloodsugar");
                                    tsLastNoficiation = new Date().getTime();
                                }
                            } else {
                                String usualTime = TimeUtils.getTimeInUserFormat(meanTs, this);
                                sendNotification(getResources().getString(R.string.usualBSM) + " " + usualTime + ". " + getResources().getString(R.string.timeToInputBS), mIds.get(mean));
//                                Log.d(TAG, "time to input bloodsugar");
                                tsLastNoficiation = new Date().getTime();
                            }
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
     * @author Stefan
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
        ArrayList<MeasureItem> mms = dbHandler.getAllMeasurementValues("bloodsugar");
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

    /**
     * compares two ArrayLists
     * @param list1
     * @param list2
     * @return
     * @author Stefan 27.09.2016
     */
    public boolean compare(ArrayList<Double> list1, ArrayList<Double> list2){
        for(int i=0; i<list1.size(); i++){
            boolean found = false;
            for(int j=0; j<list2.size(); j++){
                if(list1.get(i).equals(list2.get(j))){
                    found = true;
                }
            }
            if(!found){
                return false;
            }
        }
        return true;
    }
}
