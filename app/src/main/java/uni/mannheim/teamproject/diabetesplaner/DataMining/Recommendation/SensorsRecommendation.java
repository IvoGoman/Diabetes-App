package uni.mannheim.teamproject.diabetesplaner.DataMining.Recommendation;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;

/**
 * Created by leonidgunko on 28/09/16.
 */

public class SensorsRecommendation extends Recommendation {
    int id;

    public SensorsRecommendation() {
        super("Sensors", 10*Recommendation.MIN);
        Log.d("Rec", "recommend based on sensors started");
        id = getNewMid();
    }

    /**
     * Created by leonidgunko
     * if 2 from 3 sensors predict that current action is unlikely then provide
     * a hint
     */
    @Override
    public void recommend() {
        Log.d("Rec", "recommend based on sensors");
        try {
            if (unusual(AppGlobal.getHandler().getCurrentActivity()) > 2) {
                sendNotification("Please do not forget to input your daily routine", id);
                Log.d("Sensors:", "recommend");
            }
        }
        catch(Exception e){}
    }

    private int unusual(int subActId){
        int res = 0;
        if (AppGlobal.accUnusualActivities.contains(subActId)){res++;};
        if (AppGlobal.gpsUnusualActivities.contains(subActId)){res++;};
        if (AppGlobal.wifiUnusualActivity==subActId){res++;};
        return res;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
