package uni.mannheim.teamproject.diabetesplaner.TechnicalServices;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

import uni.mannheim.teamproject.diabetesplaner.DataMining.Calculation;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.PauseSystem;


/**
 * Created by Naira on 4/6/2016.
 */
public class Accelerometer extends Service implements SensorEventListener {
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    private int accelerometerCacheCounter = 0;

    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;

    /**
     * Called when the activity is first created.
     * @author Naira
     */
    @Override
    public void onCreate() {
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        Toast.makeText(this, "Data Collection started accelerometer", Toast.LENGTH_SHORT).show();
    }

    /**
     * detects phone axis (x,y,z)
     * @param sensorEvent
     * @author Naira
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        //phone axis
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {

                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }

        float[] values = sensorEvent.values.clone();
        if (PauseSystem.gapSuitable()) {

            /**
             * edited by leonidgunko
             * predicts current action from accelerometer cache
             */
            if (this.accelerometerCacheCounter < AppGlobal.accelerometerCache.length) {

                // add Accelerometer data to the array/ collection
                AppGlobal.accelerometerCache[this.accelerometerCacheCounter][0] = values[0];
                AppGlobal.accelerometerCache[this.accelerometerCacheCounter][1] = values[1];
                AppGlobal.accelerometerCache[this.accelerometerCacheCounter][2] = values[2];
                AppGlobal.accelerometerCache[this.accelerometerCacheCounter][3] = System.currentTimeMillis();
                //Log.d("time in DataCollector", "" + P.accelerometerCache[this.accelerometerCacheCounter][3]);

                // increase counter of array
                this.accelerometerCacheCounter++;
            } else {
                // start a new array/collection
                this.accelerometerCacheCounter = 0;

                // deliver the collection to the Calculation class
                // to process the Accelerometer data
                Log.d("COLLECTION OF SIZE " + AppGlobal.accelerometerCache.length, "sent");

                try {
                    InputStream model = getAssets().open(AppGlobal.modelDataFileName);

                    double[][] preprocessedRecords = Calculation.preprocessRecords(AppGlobal.accelerometerCache);
                    String[][] labeledRecords = Calculation.labelRecords(preprocessedRecords, model, this);
                    final String[] aggregatedRecords = Calculation.aggregateRecords(labeledRecords);

                    Toast.makeText(this, aggregatedRecords[1], Toast.LENGTH_SHORT).show();

                    System.out.println(aggregatedRecords[1]);
                    // edited by Leonid
                    if (aggregatedRecords[1]=="Walking" || aggregatedRecords[1]=="Climbing up" || aggregatedRecords[1]=="Climbing down" || aggregatedRecords[1]=="Running" || aggregatedRecords[1]=="Jumping")
                    {
                        //1,2,13,17,18,19,20,21,34
                        AppGlobal.accUnusualActivities.add(1);
                        AppGlobal.accUnusualActivities.add(2);
                        AppGlobal.accUnusualActivities.add(13);
                        AppGlobal.accUnusualActivities.add(17);
                        AppGlobal.accUnusualActivities.add(18);
                        AppGlobal.accUnusualActivities.add(19);
                        AppGlobal.accUnusualActivities.add(20);
                        AppGlobal.accUnusualActivities.add(21);
                        AppGlobal.accUnusualActivities.add(34);
                    }
                    else{
                        AppGlobal.accUnusualActivities.clear();
                    }

                    try {
                        //threadHandler.post(runnerDatabase);
                        //Log.d("THREAD writetoDatabase", "started");
                    } catch (Exception e) {
                        //Log.wtf("Diabetes Planner", "Something went wrong with classification!", e);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     *
     * @param sensor
     * @param accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    /**
     * @param intent
     * @return null
     * @author Naira
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * called when accelerometer sensor is ordered to be stopped
     * @author Naira
     */
    @Override
    public void onDestroy() {
        senSensorManager.unregisterListener(this);
        Toast.makeText(this, "Data Collection stopped", Toast.LENGTH_SHORT).show();
        Log.d("ACCELEROMETER", "access killed!");
    }
}

