package uni.mannheim.teamproject.diabetesplaner.SettingsActivity;

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

import uni.mannheim.teamproject.diabetesplaner.Backend.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Backend.Calculation;
import uni.mannheim.teamproject.diabetesplaner.Backend.PauseSystem;


/**
 * Created by Dell on 4/6/2016.
 */
public class Accelerometer extends Service implements SensorEventListener {
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    private int accelerometerCacheCounter = 0;

    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;

    /** Called when the activity is first created. */
    @Override
    public void onCreate() {

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }



    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

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


        Log.d("x", Float.toString(last_x));
        Log.d("y",Float.toString(last_y));
        Log.d("z",Float.toString(last_z));

        float[] values = sensorEvent.values.clone();
        if (PauseSystem.gapSuitable()) {

            // write Accelerometer data into Log Cat
            //Log.d("x y z time", values[0]+" "+values[1]+" "+values[2]+" "+System.currentTimeMillis());

            // create collection of P.finTimeWindow * P.sampleRate values and send it to the Calculation class
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

                // call function to process the data -> preprocessRecords()

				/*
			     * START CLASSIFICATION
			     * @author Mats
			     * @author Robert
			     */

                try {
                    InputStream model = getAssets().open(AppGlobal.modelDataFileName);

                    double[][] preprocessedRecords = Calculation.preprocessRecords(AppGlobal.accelerometerCache);
                    String[][] labeledRecords = Calculation.labelRecords(preprocessedRecords, model, this);
                    final String[] aggregatedRecords = Calculation.aggregateRecords(labeledRecords);

                    Toast.makeText(this, aggregatedRecords[1], Toast.LENGTH_SHORT).show();

                    //write aggregated records and location label to database
                    final Runnable runnerDatabase = new Runnable() {
                        public void run() {
                            int u = 1;//Calculation.writetoDatabase(testHelper, aggregatedRecords, location);
                        }
                    };

                    try {
                        //threadHandler.post(runnerDatabase);
                        //Log.d("THREAD writetoDatabase", "started");
                    } catch (Exception e) {
                        //Log.wtf("Diabetes Planner", "Something went wrong with classification!", e);
                    }

		        /*
		         * END CLASSIFICATION
		         */
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}

