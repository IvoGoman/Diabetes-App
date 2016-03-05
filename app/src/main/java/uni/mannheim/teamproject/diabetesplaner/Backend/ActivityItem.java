package uni.mannheim.teamproject.diabetesplaner.Backend;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.Date;

import uni.mannheim.teamproject.diabetesplaner.DataMining.Util;

/**
 * Created by Stefan on 22.02.2016.
 * ActivityItem is used to represent the Activities of a Daily Routine
 */
public class ActivityItem {

    private int activityId;
    private int subactivityId;
    private Date starttime;
    private Date endtime;
    private Bitmap mealImage;
    private String meal;
    private Uri imageUri;
    private Date date;

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
//        this.date = date;
    }

    public ActivityItem(int activityId, int subactivityId, Date starttime, Date endtime, Uri imageUri, Bitmap mealImage, String meal) {
        this.activityId = activityId;
        this.subactivityId = subactivityId;
        this.starttime = starttime;
        this.endtime = endtime;
        this.mealImage = mealImage;
        this.meal = meal;
        this.imageUri = imageUri;
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

    public ActivityItem(int activityId, int subactivityId, String starttime, String endtime, Uri imageUri, Bitmap mealImage, String meal){
        this.activityId = activityId;
        this.subactivityId = subactivityId;
        this.starttime = Util.getTime(starttime);
        this.endtime = Util.getTime(endtime);
        this.mealImage = mealImage;
        this.meal = meal;
        this.imageUri = imageUri;
    }

    public int getActivityId() {
        return activityId;
    }
    public int getSubactivityId() {
        return subactivityId;
    }

    public Date getStarttime() {
        return starttime;
    }

    public String getStarttimeAsString() {
        return Util.timeToString(starttime);
    }

    public String getEndtimeAsString() {
        return Util.timeToString(endtime);
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
        //TODO access the activity name from the database table
        switch (id) {
            case 1:
                return "Schlafen";
            case 2:
                return "Essen/Trinken";
            case 3:
                return "Körperpflege";
            case 4:
                return "Transportmittel benutzen";
            case 5:
                return "Entspannen";
            case 6:
                return "Fortbewegen (mit Gehilfe)";
            case 7:
                return "Medikamente einnehmen";
            case 8:
                return "Einkaufen";
            case 9:
                return "Hausarbeit";
            case 10:
                return "Essen zubereiten";
            case 11:
                return "Geselligkeit";
            case 12:
                return "Fortbewegen";
            case 13:
                return "Schreibtischarbeit";
            case 14:
                return "Sport";
            case 15:
                return "Previous Activity";
            case 16:
                return "Next Activity";
            default:
                return "unknown activity";
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
            case "Fortbewegen (mit Gehilfe)":
                return 6;
            case "Medikamente einnehmen":
                return 7;
            case "Einkaufen":
                return 8;
            case "Hausarbeit":
                return 9;
            case "Essen zubereiten":
                return 10;
            case "Geselligkeit":
                return 11;
            case "Fortbewegen":
                return 12;
            case "Schreibtischarbeit":
                return 13;
            case "Sport":
                return 14;
            default:
                return 0;
        }
    }

    public Bitmap getMealImage() {
        return mealImage;
    }

    public String getMeal(){
        return meal;
    }

    public Uri getImageUri(){
        return imageUri;
    }
}
