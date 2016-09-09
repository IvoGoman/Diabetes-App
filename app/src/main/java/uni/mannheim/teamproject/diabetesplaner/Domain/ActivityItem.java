package uni.mannheim.teamproject.diabetesplaner.Domain;

import android.graphics.Bitmap;

import java.util.Date;

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;
import uni.mannheim.teamproject.diabetesplaner.Utility.Util;

/**
 * Created by Stefan on 22.02.2016.
 * ActivityItem is used to represent the Activities of a Daily Routine
 */
public class ActivityItem {

    public static final String TAG = ActivityItem.class.getSimpleName();

    private int activityId;
    private int subactivityId;
    private Date starttime;
    private Date endtime;
    private String meal;
    private String imagePath;
    private Date date;
    private Integer intensity;
    private Integer duration;

    public static final Integer INTENSITY_HIGH = 3;
    public static final Integer INTENSITY_MEDIUM = 2;
    public static final Integer INTENSITY_LOW = 1;

    /**
     * constructor for an Activity Item with times as Date
     *
     * @param activityId
     * @param subactivityId
     * @param starttime     as Date
     * @param endtime       as Date
     */
    public ActivityItem(int activityId, int subactivityId, Date starttime, Date endtime) {
        this.activityId = activityId;
        this.subactivityId = subactivityId;
        this.starttime = starttime;
        this.endtime = endtime;
    }

    public ActivityItem(int activityId, int subactivityId) {
        this.activityId = activityId;
        this.subactivityId = subactivityId;
    }

    public ActivityItem(int activityId, int subactivityId, Date starttime, Date endtime, Date date) {
        this.activityId = activityId;
        this.subactivityId = subactivityId;
        this.starttime = starttime;
        this.endtime = endtime;
        this.date = date;
    }

    public ActivityItem(int activityId, int subactivityId, Date starttime, Date endtime, String imagePath, String meal) {
        this.activityId = activityId;
        this.subactivityId = subactivityId;
        this.starttime = starttime;
        this.endtime = endtime;
        this.meal = meal;
        this.imagePath = imagePath;
    }

    public ActivityItem(int activityId, int subactivityId, Date starttime, Date endtime, String imagePath, String meal, Integer intensity) {
        this.activityId = activityId;
        this.subactivityId = subactivityId;
        this.starttime = starttime;
        this.endtime = endtime;
        this.meal = meal;
        this.imagePath = imagePath;
        this.intensity = intensity;
    }

    public ActivityItem(int activityId, int subactivityId, Date starttime, Date endtime, Integer intensity) {
        this.activityId = activityId;
        this.subactivityId = subactivityId;
        this.starttime = starttime;
        this.endtime = endtime;
        this.intensity = intensity;
    }

    public ActivityItem(ActivityItem activityItem) {
        this.activityId = activityItem.getActivityId();
        this.subactivityId = activityItem.getSubactivityId();
        this.starttime = activityItem.getStarttime();
        this.endtime = activityItem.getEndtime();
        this.date = activityItem.getDate();
        this.meal = activityItem.getMeal();
        this.imagePath = activityItem.getImagePath();
        this.date = activityItem.getDate();
        this.intensity = activityItem.getIntensity();
    }

    /**
     * constructor for an Activity Item with times as String
     *
     * @param activityId
     * @param subactivityId
     * @param starttime     as String
     * @param endtime       as String
     */
    public ActivityItem(int activityId, int subactivityId, String starttime, String endtime) {
        this.activityId = activityId;
        this.subactivityId = subactivityId;
        this.starttime = TimeUtils.getTime(starttime);
        this.endtime = TimeUtils.getTime(endtime);
    }

    /*
        public ActivityItem(int activityId, int subactivityId, String starttime, String endtime, String imagePath, String meal){
            this.activityId = activityId;
            this.subactivityId = subactivityId;
            this.starttime = starttime;
            this.endtime = endtime;
            this.meal = meal;
            this.imagePath = imagePath;
        }
    */
    public int getActivityId() {
        return activityId;
    }

    public int getSubactivityId() {
        return subactivityId;
    }

    public Date getStarttime() {
        return starttime;
    }

    /**
     * returns starttime in HH:mm format
     *
     * @return
     */
    public String getStarttimeAsString() {
        return TimeUtils.dateToDateTimeString(starttime);
    }

    /**
     * returns endtime in HH:mm format
     *
     * @return
     */
    public String getEndtimeAsString() {
        return TimeUtils.dateToDateTimeString(endtime);
    }

    public Date getEndtime() {
        return endtime;
    }

    public Date getDate() {
        return date;
    }

    public void setStarttime(Date date) {
        this.starttime = date;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public void setSubactivityId(int subactivityId) {
        this.subactivityId = subactivityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    /**
     * returns the name of the activity with id
     *
     * @param id
     * @return
     */
    public static String getActivityString(int id) {
        DataBaseHandler dbHandler = AppGlobal.getHandler();
        return dbHandler.getActionById(dbHandler, id);
    }

    /**
     * returns subactivity TODO should be read from the database
     *
     * @param id subactivity id
     * @return name of activity
     */
    public static String getSubactivity(int id) {
        return AppGlobal.getHandler().getSubactivity(id);
    }

    /**
     * 27.06.16 Stefan
     * returns the id of an activity by the name
     *
     * @param name
     * @return id
     */
    public static Integer getActivityId(String name) {

        return AppGlobal.getHandler().getActivityID(name);
//        ArrayList<String> actionsList = AppGlobal.getHandler().getAllActionsAsList(AppGlobal.getHandler());
//        for(int i=0; i<actionsList.size(); i++){
//            if(actionsList.get(i).equals(name)){
//                return i+1;
//            }
//        }
//        return actionsList.size();
    }


    /**
     * returns a compressed bitmap image. Returns null if imagePath is null
     *
     * @return
     */
    public Bitmap getMealImage() {
        if (imagePath != null) {
            return Util.getCompressedPic(imagePath);
        } else {
            return null;
        }
    }

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setIntensity(Integer intensity) {
        this.intensity = intensity;
    }

    public Integer getIntensity() {
        return this.intensity;
    }

    /**
     * returns properties as a String that can be printed to the console
     *
     * @return
     * @author Stefan 06.09.2016
     */
    public String print() {
        String tmp = "";
        tmp += "Activity: " + activityId;
        tmp += "\nSubactivity: " + subactivityId;
        if (starttime != null) {
            tmp += "\nStarttime: " + TimeUtils.dateToDateTimeString(starttime);
        } else {
            tmp += "\nStarttime: null";
        }
        if (endtime != null) {
            tmp += "\nEndtime: " + TimeUtils.dateToDateTimeString(endtime);
        } else {
            tmp += "\nEndtime: null";
        }
        tmp += "\nImagePath: " + imagePath;
        tmp += "\nMeal: " + meal;
        tmp += "\nIntensity: " + intensity;
        return tmp;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
