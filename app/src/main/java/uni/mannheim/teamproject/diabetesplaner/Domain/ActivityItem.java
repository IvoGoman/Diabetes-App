package uni.mannheim.teamproject.diabetesplaner.Domain;

import android.graphics.Bitmap;

import java.util.Date;

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
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

    /**
     * constructor for an Activity Item with times as Date
     * @param activityId
     * @param subactivityId
     * @param starttime as Date
     * @param endtime as Date
     */
    public ActivityItem(int activityId, int subactivityId, Date starttime, Date endtime){
         this.activityId = activityId;
        this.subactivityId = subactivityId;
        this.starttime = starttime;
        this.endtime = endtime;
    }

    public ActivityItem(int activityId, int subactivityId, Date starttime, Date endtime, Date date){
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

    public ActivityItem(ActivityItem activityItem){
        this.activityId = activityItem.getActivityId();
        this.subactivityId = activityItem.getSubactivityId();
        this.starttime = activityItem.getStarttime();
        this.endtime = activityItem.getEndtime();
        this.date = activityItem.getDate();
    }

    /**
     * constructor for an Activity Item with times as String
     * @param activityId
     * @param subactivityId
     * @param starttime as String
     * @param endtime as String
     */
    public ActivityItem(int activityId, int subactivityId, String starttime, String endtime){
        this.activityId = activityId;
        this.subactivityId = subactivityId;
        this.starttime = Util.getTime(starttime);
        this.endtime = Util.getTime(endtime);
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
     * @return
     */
    public String getStarttimeAsString() {
        return Util.dateToDateTimeString(starttime);
    }

    /**
     * returns endtime in HH:mm format
     * @return
     */
    public String getEndtimeAsString() {
        return Util.dateToDateTimeString(endtime);
    }

    public Date getEndtime() {
        return endtime;
    }

    public Date getDate() {
        return date;
    }

    public void setStarttime(Date date){
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
     * @param id
     * @return
     */
    public static String getActivityString(int id){
        DataBaseHandler dbHandler = AppGlobal.getHandler();
        return dbHandler.getActionById(dbHandler, id);

//        //TODO access the activity name from the database table
//        switch (id) {
//            case 1:
//                return "Schlafen";
//            case 2:
//                return "Essen/Trinken";
//            case 3:
//                return "Körperpflege";
//            case 4:
//                return "Transportmittel benutzen";
//            case 5:
//                return "Entspannen";
//            case 6:
//                return "Fortbewegen (mit Gehilfe)";
//            case 7:
//                return "Medikamente einnehmen";
//            case 8:
//                return "Einkaufen";
//            case 9:
//                return "Hausarbeit";
//            case 10:
//                return "Essen zubereiten";
//            case 11:
//                return "Geselligkeit";
//            case 12:
//                return "Fortbewegen";
//            case 13:
//                return "Schreibtischarbeit";
//            case 14:
//                return "Sport";
//            case 15:
//                return "Previous Activity";
//            case 16:
//                return "Next Activity";
//            default:
//                return "unknown activity";
//        }
    }

    /**
     * returns subactivity TODO should be read from the database
     *
     * @param id subactivity id
     * @return name of activity
     */
    public static String getSubactivity(int id) {
        switch (id) {
            case 1:
                return "Joggen";
            case 2:
                return "Biken";
            case 3:
                return "Climbing";
            case 4:
                return "Frühstück";
            case 5:
                return "Mittagessen";
            default:
                return "";
        }
    }

    /**
     * returns the name of the activity with id
     * @param name
     * @return id
     */
    public static Integer getActivityId(String name){
        //TODO access the activity name from the database table
        switch (name) {
            case "Schlafen":
                return 1;
            case "Essen/Trinken":
                return 2;
            case "Körperpflege":
                return 3;
            case "Transportmittel benutzen":
                return 4;
            case "Entspannen":
                return 5;
            case "Medikamente einnehmen":
                return 6;
            case "Einkaufen":
                return 7;
            case "Hausarbeit":
                return 8;
            case "Essen zubereiten":
                return 9;
            case "Geselligkeit":
                return 10;
            case "Fortbewegen":
                return 11;
            case "Schreibtischarbeit":
                return 12;
            case "Sport":
                return 13;
            default:
                return 0;
        }
    }

    /**
     * returns a compressed bitmap image. Returns null if imagePath is null
     * @return
     */
    public Bitmap getMealImage() {
        if(imagePath != null) {
            return Util.getCompressedPic(imagePath);
        }else{
            return null;
        }
    }

    public String getMeal(){
        return meal;
    }

    public String getImagePath(){
        return imagePath;
    }

    public void setIntensity(Integer intensity){
        this.intensity = intensity;
    }

    public Integer getIntensity(){
        return this.intensity;
    }
}
