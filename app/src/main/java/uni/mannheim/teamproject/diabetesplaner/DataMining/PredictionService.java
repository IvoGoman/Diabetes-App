package uni.mannheim.teamproject.diabetesplaner.DataMining;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;

import uni.mannheim.teamproject.diabetesplaner.UI.EntryScreenActivity;

/**
 * Created by Stefan on 26.09.2016.
 */
public class PredictionService extends IntentService {

    public PredictionService(){
        super("PredictionFramework");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();

        ArrayList<Integer> algos = new ArrayList<>();
//        algos.add(PredictionFramework.PREDICTION_DECISION_TREE);
        algos.add(PredictionFramework.PREDICTION_GSP);
//        algos.add(PredictionFramework.PREDICTION_FUZZY_MINER);
//        algos.add(PredictionFramework.PREDICTION_HEURISTICS_MINER);
//        try {
        predictDailyRoutine(algos,PredictionFramework.FRIDAY);

        String status = "completed";
        Intent localIntent =
                new Intent(EntryScreenActivity.BROADCAST_ACTION)
                        // Puts the status into the Intent
                        .putExtra(EntryScreenActivity.EXTENDED_DATA_STATUS, status);
        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    /**
     * predicts the daily routine and gets the algorithms to choose from the settings
     * @param mode     EVERY_DAY, WEEKDAYS, WEEKENDS, MONDAY, ... , SUNDAY
     * @author Stefan 06.09.2016
     */
    public void predictDailyRoutine(int mode, Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        ArrayList<Integer> algorithms = new ArrayList<>();
        boolean dt = preferences.getBoolean("pref_key_dt", true);
        if(dt){
            algorithms.add(PredictionFramework.PREDICTION_DECISION_TREE);
        }
        boolean gsp = preferences.getBoolean("pref_key_gsp", true);
        if(gsp){
            algorithms.add(PredictionFramework.PREDICTION_GSP);
        }
        boolean fuzzy = preferences.getBoolean("pref_key_fuzzy", true);
        if(fuzzy){
            algorithms.add(PredictionFramework.PREDICTION_FUZZY_MINER);
        }
        boolean heuristics = preferences.getBoolean("pref_key_heuristics", true);
        if(heuristics){
            algorithms.add(PredictionFramework.PREDICTION_HEURISTICS_MINER);
        }

        try {
            new Thread(new PredictionFramework(PredictionFramework.retrieveTrainingData(mode), algorithms)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * predicts the daily routine and lets you choose between different algorithms
     * @param algorithms a list with the algorithms to use. Possible algorithms:
     *                  <li>PredictionFramework.PREDICTION_DECISION_TREE</li>
     *                  <li>PredictionFramework.PREDICTION_GSP</li>
     *                  <li>PredictionFramework.PREDICTION_FUZZY_MINER</li>
     *                  <li>PredictionFramework.PREDICTION_HEURISTICS_MINER</li>
     * @param mode     EVERY_DAY, WEEKDAYS, WEEKENDS, MONDAY, ... , SUNDAY
     * @author Stefan 06.09.2016
     */
    public void predictDailyRoutine(ArrayList<Integer> algorithms, int mode){
        new Thread(new PredictionFramework(PredictionFramework.retrieveTrainingData(mode), algorithms)).start();
    }

}

