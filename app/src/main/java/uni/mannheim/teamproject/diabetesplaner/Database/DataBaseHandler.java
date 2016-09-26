package uni.mannheim.teamproject.diabetesplaner.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import uni.mannheim.teamproject.diabetesplaner.DataMining.Prediction;
import uni.mannheim.teamproject.diabetesplaner.DataMining.PredictionFramework;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Domain.MeasureItem;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;
import uni.mannheim.teamproject.diabetesplaner.Utility.Util;


/**
 * Created by leonidgunko on 31.10.15.
 */
public class DataBaseHandler extends SQLiteOpenHelper {

    private final Context context;

    public static final String DATABASE_NAME = "Diabetes.db";
    //Bloodsugar History
    public static final String MEASUREMENT_TABLE_NAME = "Measurements";
    public static final String MEASUREMENT_CREATE_TABLE = " CREATE TABLE IF NOT EXISTS " + MEASUREMENT_TABLE_NAME +
            "(timestamp long PRIMARY KEY, profile_ID INTEGER, measure_value double, measure_unit VARCHAR(8), measure_kind VARCHAR(10));";
    public static final String MEASUREMENT_SELECT =
            "SELECT * FROM " + MEASUREMENT_TABLE_NAME + ";";
    public static final String MEASUREMENT_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + MEASUREMENT_TABLE_NAME + ";";

    //InsulinHistory
    public static final String INSULIN_TABLE_NAME = "Insulin";
    public static final String INSULIN_CREATE_TABLE = " CREATE TABLE IF NOT EXISTS " + INSULIN_TABLE_NAME +
            "(timestamp Double PRIMARY KEY, profile_ID INTEGER, insulin_value double, insulin_unit VARCHAR(8), insulin_kind VARCHAR(8));";
    public static final String INSULIN_SELECT =
            "SELECT * FROM " + INSULIN_TABLE_NAME + ";";
    public static final String INSULIN_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + INSULIN_TABLE_NAME + ";";

    // Database Constants
    private static final int DATABASE_VERSION = 1;
    // Activity Table
    private static final String ACTIVITIES_TABLE_NAME = "Activities";
    public static final String ACTIVITES_SELECT =
            "SELECT * FROM " + ACTIVITIES_TABLE_NAME + ";";
    public static final String ACTIVITES_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + ACTIVITIES_TABLE_NAME + ";";
    private static final String ACTIVITIES_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " +
                    ACTIVITIES_TABLE_NAME +
                    " (id INTEGER PRIMARY KEY, title VARCHAR(20),title_eng VARCHAR(20), id_SuperActivity INTEGER,  FOREIGN KEY(id_SuperActivity) REFERENCES SuperActivities(id));";

    // Super Activity Table
    private static final String SUPER_ACTIVITIES_TABLE_NAME = "SuperActivities";
    public static final String SUPER_ACTIVITES_SELECT =
            "SELECT * FROM " + SUPER_ACTIVITIES_TABLE_NAME + ";";
    public static final String SUPER_ACTIVITES_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + SUPER_ACTIVITIES_TABLE_NAME + ";";
    private static final String SUPER_ACTIVITIES_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " +
                    SUPER_ACTIVITIES_TABLE_NAME +
                    " (id INTEGER PRIMARY KEY, title VARCHAR(20));";

    // SubActivity Table
    private static final String SUB_ACTIVITIES_TABLE_NAME = "SubActivities";
    public static final String Sub_ACTIVITIES_SELECT =
            "SELECT * FROM " + SUB_ACTIVITIES_TABLE_NAME + ";";
    public static final String SUB_ACTIVITIES_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + SUB_ACTIVITIES_TABLE_NAME + ";";
    private static final String SUB_ACTIVITIES_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " +
                    SUB_ACTIVITIES_TABLE_NAME +
                    " (id INTEGER PRIMARY KEY, id_Activity INTEGER, Title VARCHAR(20), Title_eng VARCHAR(20), FOREIGN KEY(id_Activity) REFERENCES Activities(id));";

    //Location Table
    private static final String LOCATION_TABLE_NAME = "Location";
    public static final String LOCATION_SELECT =
            "SELECT * FROM " + LOCATION_TABLE_NAME + ";";
    public static final String LOCATION_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + LOCATION_TABLE_NAME + ";";
    private static final String LOCATION1_TABLE_NAME = "Location1";
    private static final String LOCATION_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " +
                    LOCATION_TABLE_NAME +
                    " (id INTEGER PRIMARY KEY, Latitude double, Longtitude double, Title VARCHAR(20));";
    private static final String LOCATION1_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " +
                    LOCATION1_TABLE_NAME +
                    " (id INTEGER PRIMARY KEY, Latitude double, Longtitude double, Timestamp DateTime);";

    //Wifi Table
    private static final String WIFI_TABLE_NAME = "WIFI";
    public static final String WIFI_SELECT =
            "SELECT * FROM " + WIFI_TABLE_NAME + ";";
    public static final String WIFI_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + WIFI_TABLE_NAME + ";";

    private static final String WIFI_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " +
                    WIFI_TABLE_NAME +
                    " (id INTEGER PRIMARY KEY, ssid VARCHAR(20), Title VARCHAR(20));";

    //ActivityList Table
    private static final String ACTIVITYLIST_TABLE_NAME = "ActivityList";
    public static final String ACTIVITYLIST_SELECT =
            "SELECT * FROM " + ACTIVITYLIST_TABLE_NAME + ";";
    public static final String ACTIVITYLIST_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + ACTIVITYLIST_TABLE_NAME + ";";
    private static final String ACTIVITYLIST_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " +
                    ACTIVITYLIST_TABLE_NAME +
                    " (id INTEGER PRIMARY KEY, id_SubActivity Integer, id_Location Integer, id_WIFI Integer, Start DateTime, End DateTime, Meal String, ImagePath String, Intensity Integer, FOREIGN KEY(id_SubActivity) REFERENCES SubActivities(id), FOREIGN KEY(id_Location) REFERENCES Locations(id), FOREIGN KEY(id_WIFI) REFERENCES WIFIs(id));";


    //Profile Table
    private static final String PROFILE_TABLE_NAME = "Profile";
    public static final String PROFILE_SELECT =
            "SELECT * FROM " + PROFILE_TABLE_NAME + ";";
    public static final String PROFILE_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + PROFILE_TABLE_NAME + ";";
    private static final String PROFILE_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " +
                    PROFILE_TABLE_NAME +
                    " (id INTEGER PRIMARY KEY, name VARCHAR(20), lastname VARCHAR(20), " +
                    "age INTEGER, diabetes_type INTEGER, " + "timestamp Timestamp, " +
                    "FOREIGN KEY (id) REFERENCES "+MEASUREMENT_TABLE_NAME+"(profile_ID));";

    public SQLiteDatabase db;
    public DataBaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory,
                           int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);

        this.context = context;

        Log.d("Database", "MySQLiteHelper Constructor Started");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {   //when the App is first installed
//        try {

        // Create Activity Table
        db.execSQL(SUPER_ACTIVITIES_CREATE_TABLE);
        Log.d("Database", "Temp Activity Table Created");
        db.execSQL("insert into SuperActivities(Title) values('Schlafen'); ");
        db.execSQL("insert into SuperActivities(Title) values('Essen/Trinken'); ");
        db.execSQL("insert into SuperActivities(Title) values('Insulin'); ");
        db.execSQL("insert into SuperActivities(Title) values('Exercise'); ");
        db.execSQL("insert into SuperActivities(Title) values('Stress'); ");
        db.execSQL("insert into SuperActivities(Title) values('Default'); ");

        // Create Activity Table
        db.execSQL(ACTIVITIES_CREATE_TABLE);
        Log.d("Database", "Temp Activity Table Created");
        ArrayList<String[]> activities = Util.readActivities("Activity.csv", context);


        for(int i=0 ;i< activities.size(); i++){
            db.execSQL("insert into Activities(Title_eng, Title, id_SuperActivity) values('" + activities.get(i)[2] + "','" + activities.get(i)[1] + "','" + activities.get(i)[3] + "'); ");
        }


        // Create SubActivities Table
        db.execSQL(SUB_ACTIVITIES_CREATE_TABLE);
        Log.d("Database", "Sub Activities Table Created");
        for(int i=0 ;i< activities.size(); i++){
            db.execSQL("insert into SubActivities(Title_eng, Title, id_Activity) values('"+activities.get(i)[2] +"','"+ activities.get(i)[1] +"','"+ activities.get(i)[0] +"'); ");
        }

        ArrayList<String[]> subActs = Util.readSubActivities("SubActivity.csv", context);
        for(int i=0 ;i< subActs.size(); i++){
            db.execSQL("insert into SubActivities(Title_eng, Title, id_Activity) values('"+ subActs.get(i)[3] +"','"+ subActs.get(i)[2]+"','"+ subActs.get(i)[1] +"'); ");
        }



        // Create Location Table
        db.execSQL(LOCATION_CREATE_TABLE);
        Log.d("Database", "Location Table Created");
        db.execSQL("insert into Location(Latitude, Longtitude, Title) values (-1,-1,'Other'); ");   //if the location is unknown

        // Create WIF Table
        db.execSQL(WIFI_CREATE_TABLE);
        Log.d("Database", "WIFI Table Created");
        db.execSQL("insert into WIFI(ssid , Title) values (-1,'Other'); ");


        // Create Location1 Table
        db.execSQL(LOCATION1_CREATE_TABLE);
        Log.d("Database", "Location1 Table Created");
        db.execSQL("insert into Location1(Latitude, Longtitude, TimeStamp) values (-1,-1,'2016-01-01 00:00'); ");   //if the location is unknown


        //Create ActivityList Table
        db.execSQL(ACTIVITYLIST_CREATE_TABLE);
        Log.d("Database", "Routine Table Created");


        //Create BloodSugar Table
        db.execSQL(MEASUREMENT_CREATE_TABLE);
        Log.d("Database", "Measurement Table Created");

        //Create Insulin Table
        db.execSQL(INSULIN_CREATE_TABLE);
        Log.d("Database", "Insulin Table Created");


        //Create Profile Table
        db.execSQL(PROFILE_CREATE_TABLE);
        Log.d("Database", "Profile Table Created");
    }

    /**
     * returns the number of different activities in the database
     * @return
     * @author Stefan 09.09.2016
     */
    public int getNumberOfActivities(){
        SQLiteDatabase db1 = this.getReadableDatabase();
        int numberOfActivities = 0;
        Cursor cursor = db1.rawQuery("select count(*) from Activities; ", null);

        if (cursor.moveToFirst()) {
            numberOfActivities = Integer.parseInt(cursor.getString(0));
        }
        // close cursor
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return numberOfActivities;
    }

    public ArrayList<String> GetSubActivities(int idActivity)
    {
        SQLiteDatabase db1 = this.getReadableDatabase();

        Cursor cursor = db1.rawQuery("select Title from SubActivities where id_Activity= "+ String.valueOf(idActivity)+ " and id_Activity != id; ", null);
        if (Locale.getDefault().getLanguage().equals("en")) {
            cursor = db1.rawQuery("select Title_eng from SubActivities where id_Activity= "+ String.valueOf(idActivity)+ " and id_Activity != id; ", null);
        }
        ArrayList<String> SubActivityList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                SubActivityList.add(cursor.getString(0));
            }
            while (cursor.moveToNext());
        }
        // close cursor
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return SubActivityList;
    }

    public int getActivityID(String activity)
    {
        int activityID = -1;
        SQLiteDatabase db1 = this.getReadableDatabase();
        Cursor cursor = db1.rawQuery("select id from Activities where title= '"+ activity + "'; ", null);
        if (Locale.getDefault().getLanguage().equals("en")) {
            cursor = db1.rawQuery("select id from Activities where title_eng= '"+ activity + "'; ", null);
        }
        if (cursor.moveToFirst()) {
            activityID = cursor.getInt(0);
        }
        // close cursor
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return activityID;
    }

    public int getActivityIDForPred(String activity)
    {
        int activityID = -1;
        String activityTitle;
        SQLiteDatabase db1 = this.getReadableDatabase();
        HashMap<String,Integer> Activities = new HashMap<>();
        Cursor cursor = db1.rawQuery("select id,title from Activities;", null);
        if (Locale.getDefault().getLanguage().equals("en")) {
            cursor = db1.rawQuery("select id,title_eng as title from Activities;", null);
        }
        if (cursor.moveToFirst()) {
            do {
                activityID = cursor.getInt(cursor.getColumnIndex("id"));
                activityTitle = cursor.getString(cursor.getColumnIndex("title"));
                Activities.put(activityTitle.replace(" ", ""), activityID);
            }
            while(cursor.moveToNext());
        }
        // close cursor
        if (!cursor.isClosed()) {
            cursor.close();
        }
        activityID = Activities.get(activity);
        return activityID;
    }

    public int getActivityIDbySubActicity(String subactivity)
    {
        SQLiteDatabase db1 = this.getReadableDatabase();
        Cursor cursor = db1.rawQuery("select id_Activity from SubActivities where title= '"+ subactivity+ "'; ", null);
        if (cursor.moveToFirst()) {
            return (cursor.getInt(0));
        }
        // close cursor
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return -1;
    }

    public String getActivitybySubActicity(String subactivity)
    {
        SQLiteDatabase db1 = this.getReadableDatabase();
        Cursor cursor = db1.rawQuery("select Activities.Title from SubActivities inner join Activities on SubActivities.id_Activity=Activities.id where SubActivities.title= '"+ subactivity+ "'; ", null);
        if (cursor.moveToFirst()) {
            return (cursor.getString(0));
        }
        // close cursor
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return "Default";
    }

    public String getActivitybySubActicityId(int subactivityId)
    {
        SQLiteDatabase db1 = this.getReadableDatabase();
        Cursor cursor = db1.rawQuery("select Activities.Title from SubActivities inner join Activities on SubActivities.id_Activity=Activities.id where SubActivities.id= '"+ subactivityId+ "'; ", null);
        if (cursor.moveToFirst()) {
            return (cursor.getString(0));
        }
        // close cursor
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return "Default";
    }

    public int getActivityIdbySubActicityId(int subactivityId)
    {
        int activityId=16;
        SQLiteDatabase db1 = this.getReadableDatabase();
        Cursor cursor = db1.rawQuery("select Activities.id from SubActivities inner join Activities on SubActivities.id_Activity=Activities.id where SubActivities.id= '"+ subactivityId+ "'; ", null);
        if (cursor.moveToFirst()) {
            activityId = cursor.getInt(0);
        }
        // close cursor
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return activityId;
    }

    public int getSubactivityID(String subactivity)
    {
        int subActivityId=16;
        SQLiteDatabase db1 = this.getReadableDatabase();
        Cursor cursor = db1.rawQuery("select id from SubActivities where title= '"+ subactivity + "'; ", null);
        if (Locale.getDefault().getLanguage().equals("en")) {
            cursor = db1.rawQuery("select id from SubActivities where title_eng= '"+ subactivity + "'; ", null);
        }
        if (cursor.moveToFirst()) {
            subActivityId = cursor.getInt(cursor.getColumnIndex("id"));
        }
        // close cursor
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return subActivityId;
    }

    public String getSubactivity(int subactivityID)
    {
        String subActivity="";
        SQLiteDatabase db1 = this.getReadableDatabase();
        Cursor cursor = db1.rawQuery("select title from SubActivities where id= "+ subactivityID + "; ", null);
        if (Locale.getDefault().getLanguage().equals("en")) {
            cursor = db1.rawQuery("select Title_eng from SubActivities where id= "+ subactivityID + "; ", null);
        }
        if (cursor.moveToFirst()) {
            subActivity = cursor.getString(0);
        }
        // close cursor
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return subActivity;
    }


    public HashMap<String,Integer> getAllSubactivities(int activityId) {
        SQLiteDatabase db1 = this.getReadableDatabase();
        HashMap<String,Integer> result = new HashMap<>();
        Cursor cursor = db1.rawQuery("select id,title from SubActivities where id_Activity=" + String.valueOf(activityId), null);
        if (Locale.getDefault().getLanguage().equals("en")) {
            cursor = db1.rawQuery("select id,title_eng as Title from SubActivities where id_Activity=" + String.valueOf(activityId), null);
        }
        if (cursor.moveToFirst()) {
            do {
                result.put(cursor.getString(cursor.getColumnIndex("Title")).replace(" ",""),cursor.getInt(cursor.getColumnIndex("id")));
            } while (cursor.moveToNext());

        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return result;
    }

    public ArrayList<Integer> getAllSubactivitiesId() {
        ArrayList<Integer> result = new ArrayList<>();
        SQLiteDatabase db1 = this.getReadableDatabase();
        Cursor cursor = db1.rawQuery("select id from SubActivities;", null);
        if (cursor.moveToFirst()) {
            do {
                result.add(cursor.getInt(cursor.getColumnIndex("id")));
            } while (cursor.moveToNext());

        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return result;
    }

    public ArrayList<String> getAllSubactivities() {
        SQLiteDatabase db1 = this.getReadableDatabase();
        ArrayList<String> result = new ArrayList<>();
        Cursor cursor = db1.rawQuery("select Title from SubActivities;", null);
        if (Locale.getDefault().getLanguage().equals("en")) {
            cursor = db1.rawQuery("select Title_eng from SubActivities;", null);
        }
        if (cursor.moveToFirst()) {
            do {
                result.add(cursor.getString(cursor.getColumnIndex("Title")));
            } while (cursor.moveToNext());

        }
        cursor.close();
        return result;
    }

    public ArrayList<String> getAllActivityNames()
    {
        SQLiteDatabase db1 = this.getReadableDatabase();
        ArrayList<String> result = new ArrayList<>();
        Cursor cursor = db1.rawQuery("select title from Activities; ", null);
        if (cursor.moveToFirst()) {
            do {
                result.add(cursor.getString(0));
            } while (cursor.moveToNext());

        }
        cursor.close();
        return result;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older tables if they exist
        db.execSQL(SUB_ACTIVITIES_DELETE_TABLE);
        db.execSQL(ACTIVITES_DELETE_TABLE);
        db.execSQL(LOCATION_DELETE_TABLE);
        db.execSQL(ACTIVITYLIST_DELETE_TABLE);
        db.execSQL(WIFI_DELETE_TABLE);

        this.onCreate(db);
        Log.d("Database", "Database Upgraded, All Tables Dropped");
    }

    //    Insert Statements
    public void insertLocation(double lat, double longt, String title) {
        SQLiteDatabase db1 = this.getWritableDatabase();
        db1.execSQL("insert into Location(Latitude, Longtitude, Title) values(" + lat + "," + longt + "," + "'" + title + "'" + "); ");
//        db1.close();
    }

    public void insertWIFI(String ssid, String title) {
        SQLiteDatabase db1 = this.getWritableDatabase();
        db1.execSQL("insert into WIFI(ssid, Title) values(" + ssid + "," + "'" + title + "'" + "); ");
//        db1.close();
    }

    /**
     * Inserts an actitvity item from a CSV file
     * Difference to other function: idActivity = #activities + Activ.subactivityId (to get the right offset)
     * @param Activ
     * @author Stefan 09.09.2016
     */
    public void InsertActivityFromCSV(ActivityItem Activ) {
        String ImagePath = Activ.getImagePath();
        int idActivity;
        if(GetSubActivities(Activ.getActivityId()).size() > 0){
            idActivity = Activ.getSubactivityId() + AppGlobal.getHandler().getNumberOfActivities();
        }else{
            idActivity = Activ.getActivityId();
        }
        int idLocation =1;
        int idWIFI = 1;
        String Start = Activ.getStarttimeAsString();
        String End = Activ.getEndtimeAsString();
        String Meal = Activ.getMeal();
        if (ImagePath== null){
            ImagePath = "";
        }
        else {
            ImagePath = Activ.getImagePath();
        }
        Integer Intensity = Activ.getIntensity();

        SQLiteDatabase db1 = this.getWritableDatabase();
        db1.execSQL("insert into ActivityList(id_SubActivity, id_Location, id_WIFI, Start, End, Meal, ImagePath, Intensity) values("+ idActivity + "," + idLocation + " , " + idWIFI +" ,'" + Start + "','" + End + "','" + Meal + "','" + ImagePath + "'," + Intensity + "); ");
//        db1.close();
    }

    /**
     *
     * @param Activ
     * @author edited 09.09.2016 by Stefan
     */
    public void InsertActivity(ActivityItem Activ) {
        String ImagePath = Activ.getImagePath();
        int idActivity = Activ.getSubactivityId();
        int idLocation =1;
        int idWIFI=1;
        String Start = Activ.getStarttimeAsString();
        String End = Activ.getEndtimeAsString();
        String Meal = Activ.getMeal();
        if (ImagePath== null){
            ImagePath = "";
        }
        else {
            ImagePath = Activ.getImagePath();
        }
        Integer Intensity = Activ.getIntensity();
        if (Intensity!=null && Intensity==-1){
            Intensity=null;
        }
        SQLiteDatabase db1 = this.getWritableDatabase();
        System.out.println("insert into ActivityList(id_SubActivity, id_Location,id_WIFI, Start, End, Meal, ImagePath, Intensity) values("+ idActivity + "," + idLocation+ "," + idWIFI + " , '" + Start + "','" + End + "','" + Meal + "','" + ImagePath + "'," + Intensity + "); ");
        db1.execSQL("insert into ActivityList(id_SubActivity, id_Location,id_WIFI, Start, End, Meal, ImagePath, Intensity) values("+ idActivity + "," + idLocation + "," + idWIFI+ " , '" + Start + "','" + End + "','" + Meal + "','" + ImagePath + "'," + Intensity + "); ");
//        db1.close();
    }


    public void InsertActivity(int idActivity, int idLocation, int idWIFI, String Start, String End) {
        SQLiteDatabase db1 = this.getWritableDatabase();
        db1.execSQL("insert into ActivityList(id_SubActivity, id_Location,id_WIFI Start, End) values(" + idActivity + "," + idLocation+ "," + idWIFI + " , '" + Start + "','" + End + "' ); ");
//        db1.close();
    }

    public void InsertProfile(String name, String surename, int age)
    {
        try {
            SQLiteDatabase db1 = this.getWritableDatabase();
            long tslong = System.currentTimeMillis() / 1000;
            db1.execSQL("insert into " + PROFILE_TABLE_NAME + "(name, lastname, age, timestamp)" +
                    " values('" + name + "' , '" + surename + "' , '" + age + "' , '" + tslong + "' );");
//            db1.close();
        }catch(Exception e)
        {
            e.getMessage();
        }
    }

    /**
     * Method to store either bloodsugar or insulin to the database
     * This depends on the values provided
     *
     * @param item Measureitem containing the necessary values to insert the measurement into the db
     * @param profile_id Profile ID of the User
     */
    public void insertMeasurement(MeasureItem item, int profile_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("insert into " + MEASUREMENT_TABLE_NAME + "(profile_ID, timestamp, measure_value, measure_unit, measure_kind) values(" + profile_id + ","
                 + item.getTimestamp() +" , '" + item.getMeasure_value()+ "' , '" + item.getMeasure_unit() + "' ,'"+ item.getMeasure_kind()+"');");
//        db.close();
    }


    /***
     * Insert a new weight measurement
     * @param profile_id
     * @param weight
     * @param measure_unit
     */
    public void InsertWeight(int profile_id, double weight, String measure_unit) {
        SQLiteDatabase db1 = this.getWritableDatabase();
        long tslong = System.currentTimeMillis() / 1000;
        db1.execSQL("insert into " + MEASUREMENT_TABLE_NAME + "(profile_ID, measure_value, timestamp, measure_unit, measure_kind) values(" + profile_id + ","
                + weight + " , '" + tslong + "' , '" +measure_unit+"' , 'weight');");
//        db1.close();
    }

    /***
     * Returns the last weight measurement
     * @param profile_id
     * @return
     */
    public String[] GetLastWeight(int profile_id)
    {
        try {
            SQLiteDatabase db1 = this.getWritableDatabase();
            String[] result = new String[2];
            Cursor cursor = db1.rawQuery("SELECT measure_value,measure_unit " +
                    "FROM " + MEASUREMENT_TABLE_NAME + " " +
                    "where profile_ID = " + profile_id + " " +
                    "and measure_kind = 'weight'" +
                    "ORDER BY timestamp DESC;", null);
            cursor.moveToFirst();
            if (cursor.getCount() >= 1) {
                result[0] = cursor.getString(0);
                result[1] = cursor.getString(1);
            } else {
                result = null;
            }
            cursor.close();
            return result;
        }
        catch (Exception e)
        {
            return null;
        }

    }
    public void ReplaceActivity(ActivityItem Activ){
        String Start = Activ.getStarttimeAsString();
        String End = Activ.getEndtimeAsString();
        findActionbyStartEndTime(Start, End);
        findActionbyStartEndTime2(Start, End);
        findActionbyStartTime(Start);
        findActionbyEndTime(End);


        InsertActivity(Activ);
        mergeSimilarActivities();
        SQLiteDatabase db1 = this.getWritableDatabase();
        db1.execSQL("delete from ActivityList where Start>=End");
//        db1.close();
    }

    public int getSuperActivityID(int idActivity){
        SQLiteDatabase db = this.getReadableDatabase();
        int superActivityID;
        //Return Super Activity of idActivity
        Cursor cursor = db.rawQuery("select * from SuperActivities where id = (select id_SuperActivity from Activities where id =" + idActivity +")", null);
        if (cursor.moveToFirst()){
            superActivityID = cursor.getInt(0);
            cursor.close();
            return superActivityID;
        }
        cursor.close();
        return 6;
    }

    public String getSuperActivity(int idActivity){
        SQLiteDatabase db = this.getReadableDatabase();
        //Return Super Activity of idActivity
        Cursor cursor = db.rawQuery("select * from SuperActivities where id = (select id_SuperActivity from Activities where id =" + idActivity +")", null);
        if (cursor.moveToFirst()){
            return cursor.getString(1);
        }
        cursor.close();
        return "Default";
    }

    /**
     * removes a single day from the database
     * @param day day to remove
     * @author Stefan 20.09.2016
     */
    public void deleteDay(Date day){
        SQLiteDatabase db = this.getReadableDatabase();
        Date next = TimeUtils.addMinuteFromDate(day, 1440);
        db.execSQL("delete from ActivityList where Start >= '"+TimeUtils.dateToDateString(day)+"' and Start < '" + TimeUtils.dateToDateString(next) + "' ;");

    }

    /**
     * inserts a predicted day into the database
     * @param prediction arrayList with activities
     * @author Stefan 20.09.2016
     */
    public void insertNewRoutine(ArrayList<ActivityItem> prediction){
        if(prediction!=null && prediction.size()>0) {
            deleteDay(prediction.get(0).getStarttime());

            for(int i=0; i<prediction.size(); i++){
                InsertActivity(prediction.get(i));
            }
        }

    }

    public void InsertNewRoutine(ArrayList<Prediction.PeriodAction> prediction) {
        int idActivity;
        int idLocation;
        int idWIFI;
        String Start;
        String End;

        SQLiteDatabase db1 = this.getWritableDatabase();
        for(int i=0;i<prediction.size();i++){
            idActivity =prediction.get(i).Action;
            idLocation = 1;
            idWIFI = 1;
            String StartOfDay, EndOfDay;
            Calendar calendar = Calendar.getInstance();
            int Year = calendar.get(Calendar.YEAR);
            String Month = TimeUtils.formatMonthOrDay(calendar.get(Calendar.MONTH) + 1);
            String Day = TimeUtils.formatMonthOrDay(calendar.get(Calendar.DAY_OF_MONTH));
            StartOfDay = String.valueOf(Year) + "-" + String.valueOf(Month) + "-" + String.valueOf(Day);
            EndOfDay = String.valueOf(Year) + "-" + String.valueOf(Month) + "-" + String.valueOf(Day);
            Start =prediction.get(i).Start;
            End =prediction.get(i).End;
            if (Start.charAt(1) == ':' && Start.length()==3){
                String Start2 ="";
                Start2+="0"+Start.charAt(0)+":0"+Start.charAt(2);
                Start=Start2;
            }
            if (Start.charAt(1) == ':' && Start.length()==4){
                String Start2 ="";
                Start2+="0"+Start.charAt(0)+":"+Start.charAt(2)+Start.charAt(3);
                Start=Start2;
            }
            if (Start.charAt(2) == ':' && Start.length()==4){
                String Start2 ="";
                Start2+=Start+"0";
                Start=Start2;
            }
            if (Start.charAt(2) == ':' && Start.length()==4){
                String Start2 ="";
                Start2+=Start.charAt(0)+Start.charAt(1)+":0"+Start.charAt(3);
                Start=Start2;
            }

            if (End.charAt(1) == ':' && End.length()==3){
                String End2 ="";
                End2+="0"+End.charAt(0)+":0"+End.charAt(2);
                End=End2;
            }
            if (End.charAt(1) == ':' && End.length()==4){
                String End2 ="";
                End2+="0"+End.charAt(0)+":"+End.charAt(2)+End.charAt(3);
                End=End2;
            }
            if (End.charAt(2) == ':' && End.length()==4){
                String End2 ="";
                End2+=End+"0";
                End=End2;
            }
            if (End.charAt(2) == ':' && End.length()==4){
                String End2 ="";
                End2+=End.charAt(0)+End.charAt(1)+":0"+End.charAt(3);
                End=End2;
            }


            Start = StartOfDay.toString() + " " + Start;
            End = EndOfDay.toString()  + " " + End;
            db1.execSQL("insert into ActivityList(id_SubActivity, id_Location,id_WIFI, Start, End) values(" + idActivity + "," + idLocation +"," + idWIFI + " , '" + Start + "','" + End + "' ); ");
        }
//        db1.close();
    }


    public Cursor getAllLocations() {
        SQLiteDatabase db = this.getWritableDatabase();
        //Create a Cursor that contains all records from the locations table
        Cursor cursor = db.rawQuery("select * from " + LOCATION_TABLE_NAME, null);
        return cursor;
    }

    public Cursor getAllWIFIs() {
        SQLiteDatabase db = this.getWritableDatabase();
        //Create a Cursor that contains all records from the WIFI table
        Cursor cursor = db.rawQuery("select * from " + WIFI_TABLE_NAME, null);
        return cursor;
    }


    /**
     * returns an activity name by id
     * @param id
     * @return
     * @author Stefan
     * 25.09 edited by Leonid
     */
    public String getActionById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select title from " + ACTIVITIES_TABLE_NAME + " where id=" + id, null);
        if (Locale.getDefault().getLanguage().equals("en")) {
            cursor = db.rawQuery("select title_eng from " + ACTIVITIES_TABLE_NAME + " where id=" + id, null);
        }
        String name = "";
        if(cursor.moveToFirst()){
            name = cursor.getString(0);
        }
        cursor.close();
        return name;
    }

    public Cursor getAllActions() {
        SQLiteDatabase db = this.getWritableDatabase();
        //Create a Cursor that contains all records from the locations table
        Cursor cursor = db.rawQuery("select title from " + ACTIVITIES_TABLE_NAME, null);
        return cursor;
    }

    public Cursor getAllActionsPr() {
        SQLiteDatabase db = this.getWritableDatabase();
        //Create a Cursor that contains all records from the locations table
        Cursor cursor = db.rawQuery("select distinct SubActivities.title from ActivityList inner join SubActivities on SubActivities.id=ActivityList.id_SubActivity", null);
        return cursor;
    }

    /**
     * Returns a ArrayList containing all Acitivity IDs
     * @return
     */
    public Map<Integer,String> getAllActionIDsAndTitle() {
        SQLiteDatabase db = this.getWritableDatabase();
        //Create a Cursor that contains all records from the locations table
        Cursor cursor = db.rawQuery("select * from " + ACTIVITIES_TABLE_NAME, null);
        Map<Integer,String> idMap = new HashMap<Integer,String>();
        cursor.moveToFirst();
        do {
            idMap.put(cursor.getInt(0),cursor.getString(1));
        } while(cursor.moveToNext());
        cursor.close();
        return idMap;
    }

    /**
     * 27.06.2016 Stefan
     * returns all activities as a list
     * @return
     * 25.09 edited Leonid
     */
    public ArrayList<String> getAllActionsAsList() {
        SQLiteDatabase db = this.getWritableDatabase();
        //Create a Cursor that contains all records from the locations table
        Cursor cursor = db.rawQuery("select title from " + ACTIVITIES_TABLE_NAME, null);
        if (Locale.getDefault().getLanguage().equals("en")) {
            cursor = db.rawQuery("select title_eng as title from " + ACTIVITIES_TABLE_NAME, null);
        }
        ArrayList<String> actionsList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                actionsList.add(cursor.getString(cursor.getColumnIndex("title")));
            }
            while (cursor.moveToNext());
        }
        // close cursor
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return actionsList;
    }

    public Cursor getAllRoutine() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select ActivityList.id, Activities.title as Activity, SubActivities.title as SubActivity,  ActivityList.Start, ActivityList.End from ActivityList inner join SubActivities on ActivityList.id_SubActivity = SubActivities.id inner join Activities on Subactivities.id_Activity = Activities.id", null);
        if (Locale.getDefault().getLanguage().equals("en")) {
            cursor = db.rawQuery("select ActivityList.id, Activities.title_eng as Activity, SubActivities.title_eng as SubActivity,  ActivityList.Start, ActivityList.End from ActivityList inner join SubActivities on ActivityList.id_SubActivity = SubActivities.id inner join Activities on Subactivities.id_Activity = Activities.id", null);
        }
        return cursor;
    }

    /**
     * Queries all available activities from the database
     * @return
     */
    public ArrayList<String[]> getAllEvents(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String[]> eventList = new ArrayList<String[]>();
        Cursor cursor = db.rawQuery("select ActivityList.id, SubActivities.title, ActivityList.Start, ActivityList.End from ActivityList inner join SubActivities on ActivityList.id_SubActivity = SubActivities.id", null);
        if(cursor.moveToFirst()){
            do {
                String[] event = {cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3)};
                eventList.add(event);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return eventList;
    }

    /**
     * Queries all available activities from the database.
     * @return ArrayList<ActivityItem> containing all activities of the weekday provided
     */
    public ArrayList<ActivityItem> getAllActivities(){
        SQLiteDatabase db = this.getReadableDatabase();
        ActivityItem activity = null;
        ArrayList<ActivityItem> activityList = new ArrayList<ActivityItem>();
        Cursor cursor = db.rawQuery("select ActivityList.id_SubActivity, SubActivities.title, ActivityList.Start, ActivityList.End from ActivityList inner join SubActivities on ActivityList.id_SubActivity = SubActivities.id order by ActivityList.Start ASC", null);
        if(cursor.moveToFirst()){
            do {
                activity = new ActivityItem(cursor.getInt(0),0,TimeUtils.getDateFromString(cursor.getString(2)),TimeUtils.getDateFromString(cursor.getString(3)));
                activityList.add(activity);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return activityList;
    }

    /**
     * Queries all available activities from the database.
     * Filters by day of week
     * 0==Sunday, 1==Monday, ..., 6==Saturday etc.
     * @param day integer corresponding to the weekday
     * @return ArrayList<ActivityItem> containing all activities of the weekday provided
     */
    public ArrayList<ActivityItem> getAllActivitiesByWeekday(int day){
        SQLiteDatabase db = this.getReadableDatabase();
        ActivityItem activity = null;
        int subactivityID = 0;
        ArrayList<ActivityItem> activityList = new ArrayList<ActivityItem>();
        Cursor cursor = db.rawQuery("select id_Activity, ActivityList.id_SubActivity, SubActivities.title, ActivityList.Start, ActivityList.End from ActivityList inner join SubActivities on ActivityList.id_SubActivity = SubActivities.id  where strftime('%w', ActivityList.Start)='"+day+"' order by ActivityList.Start ASC", null);
        if(cursor.moveToFirst()){
            do {
                try {
                    subactivityID = cursor.getInt(0);
                } catch (Exception e) {
                    subactivityID = 0;
                }
                activity = new ActivityItem(cursor.getInt(0),cursor.getInt(1),TimeUtils.getDateFromString(cursor.getString(3)),TimeUtils.getDateFromString(cursor.getString(4)));
                activityList.add(activity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return activityList;
    }
    /**
     * returns the last measurement of blood sugar and insulin of the selected user
     * @return
     */
    public Cursor getAllMeasurements(int profile_id) {
        try {
            SQLiteDatabase db1 = this.getWritableDatabase();
            String[] result = new String[2];
            Cursor cursor = db1.rawQuery("SELECT timestamp,measure_value,measure_unit " +
                    "FROM " + MEASUREMENT_TABLE_NAME + " " +
                    "where profile_ID = " + profile_id + " " +
                    "and (measure_kind = 'bloodsugar' OR measure_kind = 'insulin')" +
                    "ORDER BY timestamp DESC;", null);
            if (cursor.getCount() >= 1) {
                return cursor;
            } else {
                result = null;
            }
            return cursor;
        }catch(Exception e)
        {
            return null;
        }
    }

    /**
     * returns the last measurement of the selected user
     * @return String[value, unit, timestamp]
     * Changed 08.09.2016 by Stefan
     */
    public String[] getLastBloodsugarMeasurement(int profile_id){
        try {
            profile_id = 1;
            SQLiteDatabase db1 = this.getWritableDatabase();
            String[] result = new String[3];
            Cursor cursor = db1.rawQuery("SELECT measure_value,measure_unit,timestamp " +
                    "FROM " + MEASUREMENT_TABLE_NAME + " " +
                    "where profile_ID = " + profile_id + " " +
                    "and measure_kind = 'bloodsugar'" +
                    "ORDER BY timestamp DESC;", null);
            cursor.moveToFirst();
            if (cursor.getCount() >= 1) {
                result[0] = cursor.getString(0);
                result[1] = cursor.getString(1);
                result[2] = cursor.getString(2);
            } else {
                result = null;
            }
            cursor.close();
            return result;
        }
        catch (Exception e)
        {
            return null;
        }


    }

    /***
     * returns the last insulin of the selected user
     * @return
     */
    public String[] getLastInsulinMeasurement(int profile_id){
        try {
            SQLiteDatabase db1 = this.getWritableDatabase();
            String[] result = new String[2];
            Cursor cursor = db1.rawQuery("SELECT insulin_value,insulin_unit " +
                    "FROM " + INSULIN_TABLE_NAME + " " +
                    "where profile_ID = " + profile_id + " " +
                    "and insulin_kind = 'insulin'" +
                    "ORDER BY timestamp DESC;", null);
            cursor.moveToFirst();
            if (cursor.getCount() >= 1) {
                result[0] = cursor.getString(0);
                result[1] = cursor.getString(1);
            } else {
                result = null;
            }
            cursor.close();
            return result;
        }
        catch (Exception e)
        {
            return null;
        }


    }
    /**
     * @author Ivo Gosemann 18.03.2016
     * Method to retrieve all Values as MeasureItems for a given measurekind and timeframe
     *
     * @param date date for which the values should be retrieved
     * @param window "DAY","WEEK" or "MONTH" are acceptable values. Counting backwards from date the
     *               values for that timeframe are returned
     * @param measure_kind "insulin" or "bloodsugar"
     * @return ArrayList containing all the MeasureItems for a time window
     */
    public ArrayList<MeasureItem> getMeasurementValues(Date date, String window, String measure_kind){
        SQLiteDatabase db = this.getReadableDatabase();
        Long[] windowStartEnd = TimeUtils.convertDateStringToTimestamp(TimeUtils.getWindowStartEnd(date,window));
        Cursor cursor = db.rawQuery("select measure_value, measure_unit, timestamp from  " + MEASUREMENT_TABLE_NAME + " "  +
                "where timestamp>='"+windowStartEnd[0]+"' and timestamp <'"+windowStartEnd[1]+"'" +
                "AND measure_kind = '"+measure_kind+"';",null);
        ArrayList<MeasureItem> measureList = new ArrayList<>();
        MeasureItem measureItem;
        if(cursor.moveToFirst()){
            do{
                measureItem = new MeasureItem(Long.parseLong(cursor.getString(2)),cursor.getDouble(0),cursor.getString(1));
                measureList.add(measureItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        db.close();
        return measureList;
    }

    /**
     * Retrieve all measurements of a certain measure kind
     * @param measure_kind "insulin" or "bloodsugar"
     * @return ArrayList containing all measurements as MeasureItems
     * @author Stefan 09.07.2016
     */
    public ArrayList<MeasureItem> getAllMeasurementValues(String measure_kind){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select measure_value, measure_unit, timestamp from  " + MEASUREMENT_TABLE_NAME + " "  +
                "where measure_kind = '"+measure_kind+"';",null);
        ArrayList<MeasureItem> measureList = new ArrayList<>();
        MeasureItem measureItem;
        if(cursor.moveToFirst()){
            do{
                measureItem = new MeasureItem(cursor.getLong(2),cursor.getDouble(0),cursor.getString(1));
                measureList.add(measureItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        db.close();
        return measureList;
    }

    /**
     * returns most recent bloodsugar measurment
     * @param measure_kind "insulin" or "bloodsugar"
     * @return the most recentMeasureItem
     * @author Stefan 09.07.2016
     */
    public MeasureItem getMostRecentMeasurmentValue(String measure_kind){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select measure_value, measure_unit, MAX(timestamp) from  " + MEASUREMENT_TABLE_NAME + " "  +
                "where measure_kind = '"+measure_kind+"' ;",null);
        MeasureItem measureItem = null;
        if(cursor.moveToFirst()){
            measureItem = new MeasureItem(cursor.getLong(2),cursor.getDouble(0),cursor.getString(1));
        }
        cursor.close();
//        db.close();
        return measureItem;
    }

    /**
     * @author Ivo Gosemann 08.04.2016
     * TODO: Merge with GetDay
     * @param date current date of the day
     * @param window String value to indicate the time window ["DAY","WEEK","MONTH"]
     * @return ArrayList of all ActivityItems in the timeframe
     */
    public ArrayList<ActivityItem> getActivities(Date date, String window){
//        calculate the timeframe for the given date and window values
        String[] timeWindow = TimeUtils.getWindowStartEnd(date,window);
        String StartOfDay = timeWindow[0],EndOfDay = timeWindow[1];
        SQLiteDatabase db = this.getReadableDatabase();
        String S = "select SubActivities.id, ActivityList.Start, ActivityList.End from ActivityList inner join SubActivities on ActivityList.id_SubActivity = SubActivities.id where (ActivityList.End >= '" + StartOfDay + "' and ActivityList.Start < '" + EndOfDay + "') or (ActivityList.Start < '" + EndOfDay + "' and ActivityList.Start >= '" + StartOfDay+ "');";
        Cursor cursor = db.rawQuery("select SubActivities.id_Activity,ActivityList.id_SubActivity, ActivityList.Meal, ActivityList.Intensity, ActivityList.ImagePath, ActivityList.Start, ActivityList.End from ActivityList inner join SubActivities on ActivityList.id_SubActivity = SubActivities.id where ActivityList.End >= '" + StartOfDay + "' and ActivityList.Start < '" + EndOfDay + "' or ActivityList.Start < '" + EndOfDay + "' and ActivityList.Start >= '" + StartOfDay+ "' order by ActivityList.Start;", null);

        ArrayList<ActivityItem> activityList = GetArrayFromCursor(cursor, date);
        if(!cursor.isClosed()) cursor.close();
        return activityList;
    }

    /**
     * Returns all Activities with the given Activity ID
     * @param activityID
     * @return
     */
    public ArrayList<ActivityItem> getActivitiesById(int activityID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select Activities.id, ActivityList.Start, ActivityList.End from ActivityList inner join Activities on ActivityList.id_Activity = Activities.id where ActivityList.id_Activity ="+activityID+";", null);
        ArrayList<ActivityItem> activityItems = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                activityItems.add(new ActivityItem(cursor.getInt(0),0, TimeUtils.getDateFromString(cursor.getString(1)),TimeUtils.getDateFromString(cursor.getString(2))));
            } while(cursor.moveToNext());
        }
        cursor.close();
        return activityItems;
    }

    public ArrayList<ActivityItem> GetDay(Date Date) {
        String StartOfDay, EndOfDay;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Date);
        int Year = calendar.get(Calendar.YEAR);
        String Month = TimeUtils.formatMonthOrDay(calendar.get(Calendar.MONTH) + 1);
        String Day = TimeUtils.formatMonthOrDay(calendar.get(Calendar.DAY_OF_MONTH));
        StartOfDay = String.valueOf(Year) + "-" + String.valueOf(Month) + "-" + String.valueOf(Day) + " " + "00:00";
        EndOfDay = String.valueOf(Year) + "-" + String.valueOf(Month) + "-" + String.valueOf(Day) + " " + "23:59";

        //String S = "select Activities.id, ActivityList.Start, ActivityList.End from ActivityList inner join Activities on ActivityList.id_Activity = Activities.id where ActivityList.Start > '" + StartOfDay + "' and ActivityList.Start < '" + EndOfDay + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "select SubActivities.id as id_SubActivity, Activities.id as id_Activity, ActivityList.Meal, ActivityList.ImagePath,ActivityList.Intensity, ActivityList.Start, ActivityList.End from ActivityList inner join SubActivities on ActivityList.id_SubActivity = SubActivities.id inner join Activities on Subactivities.id_Activity = Activities.id where (ActivityList.End >= '" + StartOfDay + "' and ActivityList.Start < '" + EndOfDay + "') or (ActivityList.Start < '" + EndOfDay + "' and ActivityList.Start >= '" + StartOfDay+ "') order by ActivityList.Start;";
        Cursor cursor = db.rawQuery(Query, null);
        //(ActivityList.End > '2016-01-01 00:00' and ActivityList.Start < '2016-01-01 23:59') or (ActivityList.Start < '2016-01-01 23:59' and ActivityList.Start > '2016-01-01 00:00')
        return GetArrayFromCursor(cursor, Date);
    }

    public ActivityItem getCurrentActivity(){
        Date currentTime = TimeUtils.getCurrentDate();
        Cursor cursor = db.rawQuery("select * from ActivityList where ActivityList.Start < '" + currentTime.toString() + "' and ActivityList.End >= '"+ currentTime.toString() +"';",null );
        if (GetArrayFromCursor(cursor,currentTime).size()>0) {
            return GetArrayFromCursor(cursor, currentTime).get(0);
        }
        else {
            return null;
        }
    }

//    Delete Statements

    public void DeleteActivity(ActivityItem Activ) {
        String Start = Activ.getStarttimeAsString();
        String End = Activ.getEndtimeAsString();
        SQLiteDatabase db1 = this.getWritableDatabase();
        if (AppGlobal.getEditFlag()) {
            db1.execSQL("delete from ActivityList where Start = '" + Start + "' and End = '" + End + "';");
        }
        else{
            db1.execSQL("delete from ActivityList where Start = '" + Start + "' and End = '" + End + "';");
            db1.execSQL("insert into ActivityList(id_SubActivity, id_Location,id_WIFI, Start, End) values(16,1,1,'"+Start+"','"+End+"');");
        }

//        db1.close();
    }
    public void DeleteActivity(String Start, String End) {
        SQLiteDatabase db1 = this.getWritableDatabase();

        if (AppGlobal.getEditFlag()) {
            db1.execSQL("delete from ActivityList where Start = '" + Start + "' and End = '" + End + "';");
        }
        else{
            db1.execSQL("delete from ActivityList where Start = '" + Start + "' and End = '" + End + "';");
            db1.execSQL("insert into ActivityList(id_SubActivity, id_Location,id_WIFI, Start, End) values(16,1,1,'"+Start+"','"+End+"');");
        }
//        db1.close();
    }


    private void findActionbyStartTime(String Start) {
        SQLiteDatabase db1 = this.getWritableDatabase();
        Cursor cursor = db1.rawQuery("select id from ActivityList where Start <= '" + Start + "' and End >= '" + Start + "'; ", null);
        if (cursor.moveToFirst()) {
            do {
                db1.execSQL("update ActivityList set End = '" + TimeUtils.MinusMinute(Start) + "' where id = '" + cursor.getString(cursor.getColumnIndex("id")) + "';");
            } while (cursor.moveToNext());
//            db1.close();
            cursor.close();
        }
    }

    private void findActionbyEndTime(String End) {
        SQLiteDatabase db1 = this.getWritableDatabase();
        Cursor cursor = db1.rawQuery("select id from ActivityList where Start <= '" + End + "' and End >= '" + End + "'; ", null);
        if (cursor.moveToFirst()) {
            do {
                db1.execSQL("update ActivityList set Start = '" + TimeUtils.PlusMinute(End) + "' where id = '" + cursor.getString(cursor.getColumnIndex("id")) + "';");
            } while (cursor.moveToNext());
//            db1.close();
            cursor.close();
        }
    }

    private void findActionbyStartEndTime(String Start, String End) {
        SQLiteDatabase db1 = this.getWritableDatabase();
        Cursor cursor = db1.rawQuery("select id from ActivityList where Start >= '" + Start + "' and End <= '" + End + "'; ", null);
        if (cursor.moveToFirst()) {
            do {
                db1.execSQL("delete from ActivityList where id = '" + cursor.getString(cursor.getColumnIndex("id")) + "';");
            } while (cursor.moveToNext());
            cursor.close();
//            db1.close();
        }
    }


    private void findActionbyStartEndTime2(String Start, String End) {
        SQLiteDatabase db1 = this.getWritableDatabase();
        Cursor cursor = db1.rawQuery("select * from ActivityList where Start <= '" + Start + "' and End >= '" + End + "'; ", null);
        if (cursor.moveToFirst()) {
            do {
                int idSubActivity = cursor.getInt(cursor.getColumnIndex("id_SubActivity"));
                String End1 = cursor.getString(cursor.getColumnIndex("End"));
                Date StartNew = TimeUtils.getDateFromString(End);
                Date EndNew = TimeUtils.getDateFromString(End1);

                ActivityItem activityItem = new ActivityItem(getActivityIdbySubActicityId(idSubActivity),idSubActivity,StartNew,EndNew);
                String id = cursor.getString(cursor.getColumnIndex("id"));
                db1.execSQL("update ActivityList set End = '" + TimeUtils.MinusMinute(Start) + "' where id = '" + id + "';");

                InsertActivity(activityItem);
            } while (cursor.moveToNext());
//            db1.close();
            cursor.close();
        }
    }

    //      Handler Utility Methods
    public boolean CheckRoutineAdded(){
        boolean result = true;
        String StartOfDay, EndOfDay;
        Calendar calendar = Calendar.getInstance();
        int Year = calendar.get(Calendar.YEAR);
        String Month = TimeUtils.formatMonthOrDay(calendar.get(Calendar.MONTH) + 1);
        String Day = TimeUtils.formatMonthOrDay(calendar.get(Calendar.DAY_OF_MONTH));
        StartOfDay = String.valueOf(Year) + "-" + String.valueOf(Month) + "-" + String.valueOf(Day) + " " + "00:00";
        EndOfDay = String.valueOf(Year) + "-" + String.valueOf(Month) + "-" + String.valueOf(Day) + " " + "23:59";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from ActivityList where Start>= '" + StartOfDay + "' and End<= '" + EndOfDay + "'", null);
        if (cursor.getCount() < 1)
        {
            cursor.close();
            result = false;
        }
        else {
            cursor.close();
            result = true;
        }
        return result;
    }

    public ArrayList<ActivityItem> GetArrayFromCursor(Cursor cursor, Date Date) {
        int activityId;
        int subactivityId;

        Date starttime;
        Date endtime;
        Date EndOfDay,StartOfDay;
        String meal;
        String imagePath;
        Integer intensity;
        ArrayList<ActivityItem> Activities = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                try {
                    activityId = cursor.getInt(cursor.getColumnIndex("id_Activity"));
                    subactivityId = cursor.getInt(cursor.getColumnIndex("id_SubActivity"));
                    meal = cursor.getString(cursor.getColumnIndex("Meal"));
                    imagePath = cursor.getString(cursor.getColumnIndex("ImagePath"));
                    intensity = cursor.getInt(cursor.getColumnIndex("Intensity"));
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                    starttime = format.parse(cursor.getString(cursor.getColumnIndex("Start")));
                    endtime = format.parse(cursor.getString(cursor.getColumnIndex("End")));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(Date);
                    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59);
                    EndOfDay = calendar.getTime();
                    calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH), 00,00);
                    StartOfDay = calendar.getTime();

//                    if (End.after(EndOfDay)){
//                        End = EndOfDay;
//                    }
//                    if (Start.before(StartOfDay)){
//                        Start = StartOfDay;
//                    }
                    ActivityItem PA = new ActivityItem(activityId,subactivityId,starttime,endtime,imagePath,meal,intensity);
                    Activities.add(PA);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            while (cursor.moveToNext());
        }
        // close cursor
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return Activities;
    }

//  Potential Utility Functions

    public void mergeSimilarActivities() {
        SQLiteDatabase db1 = this.getWritableDatabase();
        Cursor cursor = db1.rawQuery("select id_SubActivity,Start,End,Meal,ImagePath,Intensity from ActivityList order by Start;", null);
        ActivityItem last_activity = new ActivityItem(0, 0, TimeUtils.getDateFromString("1980-01-01 00:00"), TimeUtils.getDateFromString("1980-01-01 00:00"));
        ActivityItem activity = new ActivityItem(0, 0, TimeUtils.getDateFromString("1980-01-01 00:00"), TimeUtils.getDateFromString("1980-01-01 00:00"));
        if (cursor.moveToFirst()) {
            do {
                if (activity.getSubactivityId() != 0) {
                    last_activity.setSubactivityId(activity.getSubactivityId());
                    last_activity.setActivityId(activity.getActivityId());
                    last_activity.setStarttime(activity.getStarttime());
                    last_activity.setEndtime(activity.getEndtime());
                    last_activity.setMeal(activity.getMeal());
                    last_activity.setIntensity(activity.getIntensity());
                    last_activity.setDuration(activity.getDuration());
                }
                activity.setSubactivityId(cursor.getInt(cursor.getColumnIndex("id_SubActivity")));
                int activityId = this.getActivityIdbySubActicityId(activity.getSubactivityId());
                activity.setActivityId(activityId);
                activity.setStarttime(TimeUtils.getDateFromString(cursor.getString(cursor.getColumnIndex("Start"))));
                activity.setEndtime(TimeUtils.getDateFromString(cursor.getString(cursor.getColumnIndex("End"))));
                activity.setMeal(cursor.getString(cursor.getColumnIndex("Meal")));
                activity.setImagePath(cursor.getString(cursor.getColumnIndex("ImagePath")));
                activity.setIntensity(cursor.getInt(cursor.getColumnIndex("Intensity")));

                if (activity.getSubactivityId() == last_activity.getSubactivityId()){
                    if (getСompleteness(activity)>getСompleteness(last_activity)){
                        if (activity.getStarttime().after(last_activity.getStarttime())){
                            activity.setStarttime(last_activity.getStarttime());
                        }
                        if (activity.getEndtime().before(last_activity.getEndtime())){
                            activity.setEndtime(last_activity.getEndtime());
                        }
                        String Start = activity.getStarttimeAsString();
                        String End = activity.getEndtimeAsString();
                        findActionbyStartEndTime(Start, End);
                        findActionbyStartEndTime2(Start, End);
                        findActionbyStartTime(Start);
                        findActionbyEndTime(End);


                        InsertActivity(activity);
                    }
                    else{
                        if (last_activity.getStarttime().after(activity.getStarttime())){
                            last_activity.setStarttime(activity.getStarttime());
                        }
                        if (last_activity.getEndtime().before(activity.getEndtime())){
                            last_activity.setEndtime(activity.getEndtime());
                        }
                        String Start = last_activity.getStarttimeAsString();
                        String End = last_activity.getEndtimeAsString();
                        findActionbyStartEndTime(Start, End);
                        findActionbyStartEndTime2(Start, End);
                        findActionbyStartTime(Start);
                        findActionbyEndTime(End);


                        InsertActivity(last_activity);
                    }
                }
            }
            while (cursor.moveToNext());
        }
    }

    private int getСompleteness(ActivityItem ai){
        int completeness =0;
        if (ai.getImagePath()!=null && ai.getImagePath()!=""){
            completeness++;
        }
        if (ai.getIntensity()!=null && ai.getIntensity()!=0){
            completeness++;
        }
        if (ai.getMeal()!=null && ai.getMeal()!=""){
            completeness++;
        }
        return completeness;
    }

    public String[] getUser(int id)
    {
        SQLiteDatabase db1 = this.getWritableDatabase();
        try {

            String[] result = new String[2];
            Cursor cursor = db1.rawQuery("SELECT name, lastname from "+ PROFILE_TABLE_NAME + " where id = " + id +";",null);
            if (cursor.getCount() >= 1) {
                cursor.moveToFirst();
                result[0] = cursor.getString(0);
                //cursor.moveToNext();
                result[1] = cursor.getString(1);
                cursor.close();
                return result;
            } else {
                result = null;
            }
            return result;
        }catch(Exception e)
        {
            e.getMessage();
            return null;
        }
    }

    public int getUserID()
    {
        int userID;
        SQLiteDatabase db1 = this.getWritableDatabase();
        try {
            Cursor cursor = db1.rawQuery("SELECT MAX(id) from "+ PROFILE_TABLE_NAME +";",null);
            if (cursor.getCount() >= 1) {
                cursor.moveToFirst();
                userID = cursor.getInt(0);
            } else {
                userID = cursor.getInt(0);
            }
            cursor.close();
            return userID;
        }catch(Exception e)
        {
            e.getMessage();
            return 0;
        }
    }

    /**
     * returns a list with all relevant days from the database (training data).
     * Only days that have a complete daily routine will be returned, the current day is excluded
     * @param mode specifies which days should be returned. <br/>
     *             Valid values are: <br/>
     *
     *             <li><b>PredictionFramework.EVERY_DAY</b>: returns every day</li>
     *             <li><b>PredictionFramework.WEEKDAYS</b>: returns all weekdays </li>
     *             <li><b>PredictionFramework.WEEKENDS</b>: returns all weekend days </li>
     *             <li><b>Calendar.MONDAY</b>,...,<b>Calendar.SUNDAY</b>: returns all days of the specified day </li>
     * @return ArrayList contains an arraylist with ActivityItems for every single day <br/>
     * @author Stefan 06.09.2016
     */
    public ArrayList<ArrayList<ActivityItem>> getAllDays(int mode) {
        String query = "SELECT al.*, s.id_Activity FROM " + ACTIVITYLIST_TABLE_NAME + " al " +
                "INNER JOIN " + SUB_ACTIVITIES_TABLE_NAME + " s " + "ON al.id_SubActivity = s.id";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<ArrayList<ActivityItem>> relevantDays = new ArrayList<>();
        switch (mode) {
            case PredictionFramework.EVERY_DAY: {

                Date startPrev = null;

                ArrayList<ActivityItem> day = new ArrayList<>();
                ActivityItem item;
                if (cursor.moveToFirst()) {
                    do {
                        int activityId = cursor.getInt(cursor.getColumnIndex("id_Activity"));
                        int subactivityId = cursor.getInt(cursor.getColumnIndex("id_SubActivity"));
                        String meal = cursor.getString(cursor.getColumnIndex("Meal"));
                        String imagePath = cursor.getString(cursor.getColumnIndex("ImagePath"));
                        int intensity = cursor.getInt(cursor.getColumnIndex("Intensity"));
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                        Date starttime = null;
                        Date endtime = null;
                        try {
                            starttime = format.parse(cursor.getString(cursor.getColumnIndex("Start")));
                            endtime = format.parse(cursor.getString(cursor.getColumnIndex("End")));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        //if startPrev is initialized (not initialized in first run)
                        if (startPrev != null) {
                            //if starttime of previous activity was on a different day
                            if (TimeUtils.isDifferentDay(startPrev, starttime)) {
                                if(Util.checkDay(day)) {
                                    relevantDays.add(day);
                                }
                                day = new ArrayList<>();
                            }
                        }
                        item = new ActivityItem(activityId, subactivityId, starttime, endtime, imagePath, meal, intensity);
                        day.add(item);

                        startPrev = starttime;
                    } while (cursor.moveToNext());
                    if(Util.checkDay(day)) {
                        relevantDays.add(day);
                    }
                }
                cursor.close();
                return relevantDays;
            }
            case PredictionFramework.WEEKDAYS: {
                Date startPrev = null;

                ArrayList<ActivityItem> day = new ArrayList<>();
                ActivityItem item;
                if (cursor.moveToFirst()) {
                    do {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                        Date starttime = null;
                        Date endtime = null;
                        try {
                            starttime = format.parse(cursor.getString(cursor.getColumnIndex("Start")));
                            endtime = format.parse(cursor.getString(cursor.getColumnIndex("End")));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if(starttime != null) {
                            //get the day of week from starttime
                            Calendar cal1 = Calendar.getInstance();
                            cal1.setTime(starttime);
                            int dayOfWeek = cal1.get(Calendar.DAY_OF_WEEK);

                            if (dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.TUESDAY || dayOfWeek == Calendar.WEDNESDAY || dayOfWeek == Calendar.THURSDAY || dayOfWeek == Calendar.FRIDAY) {
                                //get the rest of the parameters
                                int activityId = cursor.getInt(cursor.getColumnIndex("id_Activity"));
                                int subactivityId = cursor.getInt(cursor.getColumnIndex("id_SubActivity"));
                                String meal = cursor.getString(cursor.getColumnIndex("Meal"));
                                String imagePath = cursor.getString(cursor.getColumnIndex("ImagePath"));
                                int intensity = cursor.getInt(cursor.getColumnIndex("Intensity"));

                                if (startPrev != null) {
                                    if (TimeUtils.isDifferentDay(startPrev, starttime)) {
                                        if(Util.checkDay(day)) {
                                            relevantDays.add(day);
                                        }
                                        day = new ArrayList<>();
                                    }
                                }
                                item = new ActivityItem(activityId, subactivityId, starttime, endtime, imagePath, meal, intensity);
                                day.add(item);

                                startPrev = starttime;
                            }
                        }
                    } while (cursor.moveToNext());
                    if(Util.checkDay(day)) {
                        relevantDays.add(day);
                    }
                }
                cursor.close();
                return relevantDays;
            }
            case PredictionFramework.WEEKENDS: {
                Date startPrev = null;

                ArrayList<ActivityItem> day = new ArrayList<>();
                ActivityItem item;
                if (cursor.moveToFirst()) {
                    do {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                        Date starttime = null;
                        Date endtime = null;
                        try {
                            starttime = format.parse(cursor.getString(cursor.getColumnIndex("Start")));
                            endtime = format.parse(cursor.getString(cursor.getColumnIndex("End")));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (starttime != null) {
                            //get the day of week from starttime
                            Calendar cal1 = Calendar.getInstance();
                            cal1.setTime(starttime);
                            int dayOfWeek = cal1.get(Calendar.DAY_OF_WEEK);

                            if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                                //get the rest of the parameters
                                int activityId = cursor.getInt(cursor.getColumnIndex("id_Activity"));
                                int subactivityId = cursor.getInt(cursor.getColumnIndex("id_SubActivity"));
                                String meal = cursor.getString(cursor.getColumnIndex("Meal"));
                                String imagePath = cursor.getString(cursor.getColumnIndex("ImagePath"));
                                int intensity = cursor.getInt(cursor.getColumnIndex("Intensity"));

                                if (startPrev != null) {
                                    if (TimeUtils.isDifferentDay(startPrev, starttime)) {
                                        if(Util.checkDay(day)) {
                                            relevantDays.add(day);
                                        }
                                        day = new ArrayList<>();
                                    }
                                }
                                item = new ActivityItem(activityId, subactivityId, starttime, endtime, imagePath, meal, intensity);
                                day.add(item);

                                startPrev = starttime;
                            }
                        }
                    } while (cursor.moveToNext());
                    if(Util.checkDay(day)) {
                        relevantDays.add(day);
                    }
                }
                cursor.close();
                return relevantDays;
            }
            default: {
                //single day
                if (mode >= Calendar.SUNDAY && mode <= Calendar.SATURDAY) {
                    Date startPrev = null;

                    ArrayList<ActivityItem> day = new ArrayList<>();
                    ActivityItem item;
                    if (cursor.moveToFirst()) {
                        do {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                            Date starttime = null;
                            Date endtime = null;
                            try {
                                starttime = format.parse(cursor.getString(cursor.getColumnIndex("Start")));
                                endtime = format.parse(cursor.getString(cursor.getColumnIndex("End")));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            //get the day of week from starttime
                            if (starttime != null) {
                                Calendar cal1 = Calendar.getInstance();
                                cal1.setTime(starttime);
                                int dayOfWeek = cal1.get(Calendar.DAY_OF_WEEK);

                                if (dayOfWeek == mode) {
                                    //get the rest of the parameters
                                    int activityId = cursor.getInt(cursor.getColumnIndex("id_Activity"));
                                    int subactivityId = cursor.getInt(cursor.getColumnIndex("id_SubActivity"));
                                    String meal = cursor.getString(cursor.getColumnIndex("Meal"));
                                    String imagePath = cursor.getString(cursor.getColumnIndex("ImagePath"));
                                    int intensity = cursor.getInt(cursor.getColumnIndex("Intensity"));

                                    if (startPrev != null) {
                                        if (TimeUtils.isDifferentDay(startPrev, starttime)) {
                                            if (Util.checkDay(day)) {
                                                relevantDays.add(day);
                                            }
                                            day = new ArrayList<>();
                                        }
                                    }
                                    item = new ActivityItem(activityId, subactivityId, starttime, endtime, imagePath, meal, intensity);
                                    day.add(item);

                                    startPrev = starttime;
                                }
                            }
                        } while (cursor.moveToNext());
                        if (Util.checkDay(day)) {
                            relevantDays.add(day);
                        }

                    }
                }
            }
        }
        cursor.close();
        return relevantDays;
    }
}