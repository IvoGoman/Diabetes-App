package uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner;

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;

/**
 * Created by Ivo on 10.07.2016.
 *
 * Utility Methods for the Process Mining Algorithms
 */
public class ProcessMiningUtill {

    /**
     * @return ID of the current activity
     */
    public static int getCurrentActivityID(){
        DataBaseHandler dbHandler = new DataBaseHandler(AppGlobal.getcontext());
        ActivityItem currentActivity = dbHandler.getCurrentActivity();
        return currentActivity.getActivityId();
    }
}
