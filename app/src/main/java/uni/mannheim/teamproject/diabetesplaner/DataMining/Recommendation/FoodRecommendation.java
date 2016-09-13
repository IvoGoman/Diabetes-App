package uni.mannheim.teamproject.diabetesplaner.DataMining.Recommendation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Domain.DayHandler;
import uni.mannheim.teamproject.diabetesplaner.R;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;

/**
 * Created by Stefan on 12.09.2016.
 */
public class FoodRecommendation extends Recommendation {

    private DataBaseHandler db = AppGlobal.getHandler();
    private final int PERSONAL_GROOMING = db.getActivityID("Körperpflege");
    private final int SLEEPING = db.getActivityID("Schlafen");
    private final int EATING_DRINKING = db.getActivityID("Essen/Trinken");
    private final int TRANSPORTATION = db.getActivityID("Transportmittel benutzen");
    private final int RELAXING = db.getActivityID("Entspannen");
    private final int MEDICATION = db.getActivityID("Medikamente einnehmen");
    private final int SHOPPING = db.getActivityID("Einkaufen");
    private final int HOUSEWORK = db.getActivityID("Hausarbeit");
    private final int MEAL_PREP = db.getActivityID("Essen zubereiten");
    private final int SOCIALIZING = db.getActivityID("Geselligkeit");
    private final int MOVEMENT = db.getActivityID("Fortbewegen");
    private final int DESK_WORK = db.getActivityID("Schreibtischarbeit");
    private final int SPORT = db.getActivityID("Sport");

    private final int EXERCISE = db.getSuperActivityID(SPORT);

    private int mIdOffset = 0;
    public static final int INTERVAL = 60 * 1000;

    private ActivityItem previous;
    private boolean first = true;

    public FoodRecommendation(String name) {
        super(name);
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

    @Override
    public void recommend() {
        //check if notifications are switched on
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notify = preferences.getBoolean("pref_key_food_rec", true);

        if (notify) {
            Log.d("Rec", "recommend based on food");
            mIdOffset = getMidOffset();

            ActivityItem item = getLastActivity();
            if(item != null) {
                if (first) {
                    previous = item;
                    first = false;
                    doRecommendation(item);
                } else {
                    if (!previous.equals(item)) {
                        doRecommendation(item);
                        previous = item;
                    }
                }
            }
        }
    }

    /**
     * does the actual recommendation
     * @param item
     * @author Stefan 13.09.2016
     */
    private void doRecommendation(ActivityItem item){
        int id = item.getActivityId();
        if (id == PERSONAL_GROOMING) {
            sendNotification(getIntroString(id)
                    + getResources().getString(R.string.recom_pre) + " " + getResources().getString(R.string.a_eine) + " "
                    + getResources().getString(R.string.banana) + ".", mIdOffset);
        } else if (id == MEAL_PREP) {
            sendNotification(getIntroString(id)
                    + getResources().getString(R.string.recom_pre) + " " + getResources().getString(R.string.an_eine) + " "
                    + getResources().getString(R.string.orange) + ", " + getResources().getString(R.string.an) + " "
                    + getResources().getString(R.string.apple) + " " + getResources().getString(R.string.or) + " "
                    + getResources().getString(R.string.a_eine) + " " + getResources().getString(R.string.strawberry) + ".", mIdOffset);
        } else if (id == SHOPPING) {
            sendNotification(getIntroString(id)
                    + getResources().getString(R.string.recom_pre) + " " + getResources().getString(R.string.a_ein) + " "
                    + getResources().getString(R.string.reg_soda) + ".", mIdOffset);
        } else if (id == HOUSEWORK) {
            sendNotification(getIntroString(id)
                    + getResources().getString(R.string.recom_pre) + " " + getResources().getString(R.string.b_potatoes) + " "
                    + getResources().getString(R.string.or) + " " + getResources().getString(R.string.kale) + ".", mIdOffset);
        } else if (id == SOCIALIZING) {
            sendNotification(getIntroString(id)
                    + getResources().getString(R.string.recom_pre) + " " + getResources().getString(R.string.a_ein) + " "
                    + getResources().getString(R.string.reg_soda) + ".", mIdOffset);
        } else if (id == DESK_WORK) {
            sendNotification(getIntroString(id)
                    + getResources().getString(R.string.recom_pre) + " " + getResources().getString(R.string.yoghurt) + ".", mIdOffset);
        } else if (id == TRANSPORTATION) {
            sendNotification(getIntroString(id)
                    + getResources().getString(R.string.recom_pre) + " " + getResources().getString(R.string.diet_soda) + " "
                    + getResources().getString(R.string.or) + " " + getResources().getString(R.string.an_eine) + " "
                    + getResources().getString(R.string.orange) + ".", mIdOffset);
        } else if (id == SLEEPING) {
            sendNotification(getIntroString(id)
                    + getResources().getString(R.string.recom_pre) + " " + getResources().getString(R.string.an) + " "
                    + getResources().getString(R.string.apple) + ".", mIdOffset);
        } else if (id == RELAXING) {
            sendNotification(getIntroString(id)
                    + getResources().getString(R.string.recom_pre) + " " + getResources().getString(R.string.two) + " "
                    + getResources().getString(R.string.apples) + " " + getResources().getString(R.string.or) + " "
                    + getResources().getString(R.string.tea) + ".", mIdOffset);
        } else if (db.getSuperActivityID(id) == EXERCISE) {
            if (item.getIntensity() != null) {
                if (item.getIntensity() == ActivityItem.INTENSITY_MEDIUM) {
                    sendNotification(getIntroString(id)
                            + getResources().getString(R.string.recom_pre) + " " + getResources().getString(R.string.tuna) + " "
                            + getResources().getString(R.string.or) + " " + getResources().getString(R.string.a_eine) + " "
                            + getResources().getString(R.string.banana) + ".", mIdOffset);
                } else if (item.getIntensity() == ActivityItem.INTENSITY_HIGH) {
                    sendNotification(getIntroString(id)
                            + getResources().getString(R.string.recom_pre) + " " + getResources().getString(R.string.b_potatoes) + " "
                            + getResources().getString(R.string.or) + " " + getResources().getString(R.string.a_eine) + " "
                            + getResources().getString(R.string.quinoa) + ".", mIdOffset);
                }
            }
        }
    }

    /**
     * returns the introductory string like "Your current activity is..." based on activity id
     *
     * @param id activity id
     * @return
     * @author Stefan 12.09.2016
     */
    public String getIntroString(int id) {
        return getResources().getString(R.string.your_activity) + " " + db.getActionById(db, id) + ". ";
    }

    /**
     * @return index in daily routine
     * @author Stefan 30.04.2016
     * returns current activity
     */
    public int getCurrentActivity() {
        ArrayList<ActivityItem> routine = DayHandler.getDailyRoutine();

        Date current = TimeUtils.getCurrentDate();

        for (int i = 0; i < routine.size(); i++) {
            ActivityItem item = routine.get(i);
            if (TimeUtils.isTimeInbetween(item.getStarttime(), item.getEndtime(), current)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * returns the last actitivity
     * @return
     * @author Stefan 13.09.2016
     */
    public ActivityItem getLastActivity(){
        int iCurr = getCurrentActivity();
        ArrayList<ActivityItem> routine = DayHandler.getDailyRoutine();
        if(iCurr > 0){
            return routine.get(iCurr-1);
        }else{
            ArrayList<ActivityItem> yesterday = AppGlobal.getHandler().getActivities(AppGlobal.getHandler(), TimeUtils.getYesterdaysDate(TimeUtils.getCurrentDate()), "DAY");
            return yesterday.get(yesterday.size()-1);
        }
    }
}
