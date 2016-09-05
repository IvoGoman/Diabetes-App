package uni.mannheim.teamproject.diabetesplaner.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import uni.mannheim.teamproject.diabetesplaner.DataMining.Prediction;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Domain.MeasureItem;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;



/**
 * Created by leonidgunko on 31.10.15.
 */
public class DataBaseHandler extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Diabetes.db";
    //Bloodsugar History
    public static final String MEASUREMENT_TABLE_NAME = "Measurements";
    public static final String MEASUREMENT_CREATE_TABLE = " CREATE TABLE IF NOT EXISTS " + MEASUREMENT_TABLE_NAME +
            "(timestamp Double PRIMARY KEY, profile_ID INTEGER, measure_value double, measure_unit VARCHAR(8), measure_kind VARCHAR(8));";
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
                    " (id INTEGER PRIMARY KEY, title VARCHAR(20), id_SuperActivity INTEGER,  FOREIGN KEY(id_SuperActivity) REFERENCES SuperActivities(id));";

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
                    " (id INTEGER PRIMARY KEY, id_Activity INTEGER, Title VARCHAR(20), FOREIGN KEY(id_Activity) REFERENCES Activities(id));";

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

    //ActivityList Table
    private static final String ACTIVITYLIST_TABLE_NAME = "ActivityList";
    public static final String ACTIVITYLIST_SELECT =
            "SELECT * FROM " + ACTIVITYLIST_TABLE_NAME + ";";

    /* JW: Added Bloodsugar_History Table
    */
    public static final String ACTIVITYLIST_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + ACTIVITYLIST_TABLE_NAME + ";";
    private static final String ACTIVITYLIST_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " +
                    ACTIVITYLIST_TABLE_NAME +
                    " (id INTEGER PRIMARY KEY, id_SubActivity Integer, id_Location Integer, Start DateTime, End DateTime, Meal String, ImagePath String, Intensity Integer, FOREIGN KEY(id_SubActivity) REFERENCES SubActivities(id), FOREIGN KEY(id_Location) REFERENCES Locations(id));";

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

        Log.d("Database", "MySQLiteHelper Constructor Started");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {   //when the App is first installed
        try {

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
            db.execSQL("insert into Activities(Title, id_SuperActivity) values('Schlafen','1'); ");
            db.execSQL("insert into Activities(Title, id_SuperActivity) values('Essen/Trinken','2'); ");
            db.execSQL("insert into Activities(Title, id_SuperActivity) values('Körperpflege','6'); ");
            db.execSQL("insert into Activities(Title, id_SuperActivity) values('Transportmittel benutzen','6'); ");
            db.execSQL("insert into Activities(Title, id_SuperActivity) values('Entspannen','1'); ");
            db.execSQL("insert into Activities(Title, id_SuperActivity) values('Fortbewegen(mit Gehilfe)','4'); ");
            db.execSQL("insert into Activities(Title, id_SuperActivity) values('Medikamente einnehmen','3'); ");
            db.execSQL("insert into Activities(Title, id_SuperActivity) values('Einkaufen','4'); ");
            db.execSQL("insert into Activities(Title, id_SuperActivity) values('Hausarbeit','4'); ");
            db.execSQL("insert into Activities(Title, id_SuperActivity) values('Essen zubereiten','6'); ");
            db.execSQL("insert into Activities(Title, id_SuperActivity) values('Geselligkeit','6'); ");
            db.execSQL("insert into Activities(Title, id_SuperActivity) values('Fortbewegen','4'); ");
            db.execSQL("insert into Activities(Title, id_SuperActivity) values('Schreibtischarbeit','5'); ");
            db.execSQL("insert into Activities(Title, id_SuperActivity) values('Sport','4'); ");
            db.execSQL("insert into Activities(Title, id_SuperActivity) values('Default','6'); "); //15


            // Create SubActivities Table
            db.execSQL(SUB_ACTIVITIES_CREATE_TABLE);
            Log.d("Database", "Sub Activities Table Created");
            db.execSQL("insert into SubActivities(Title, id_Activity) values('Schlafen','1'); ");   //1
            db.execSQL("insert into SubActivities(Title, id_Activity) values('Mittagessen','2'); ");//2
            db.execSQL("insert into SubActivities(Title, id_Activity) values('Körperpflege','3'); ");//3
            db.execSQL("insert into SubActivities(Title, id_Activity) values('Transportmittel benutzen','4'); ");//4
            db.execSQL("insert into SubActivities(Title, id_Activity) values('Entspannen','5'); ");//5
            db.execSQL("insert into SubActivities(Title, id_Activity) values('Fortbewegen(mit Gehilfe)','6'); ");//6
            db.execSQL("insert into SubActivities(Title, id_Activity) values('Medikamente einnehmen','7'); ");//7
            db.execSQL("insert into SubActivities(Title, id_Activity) values('Einkaufen','8'); ");//8
            db.execSQL("insert into SubActivities(Title, id_Activity) values('Hausarbeit','9'); ");//9
            db.execSQL("insert into SubActivities(Title, id_Activity) values('Essen zubereiten','10'); ");//10
            db.execSQL("insert into SubActivities(Title, id_Activity) values('Geselligkeit','11'); ");//11
            db.execSQL("insert into SubActivities(Title, id_Activity) values('Fortbewegen','12'); ");//12
            db.execSQL("insert into SubActivities(Title, id_Activity) values('Schreibtischarbeit','13'); ");//13
            db.execSQL("insert into SubActivities(Title, id_Activity) values('Sport','14'); ");//14
            db.execSQL("insert into SubActivities(Title, id_Activity) values('Default','15'); "); //15
            db.execSQL("insert into SubActivities(Title, id_Activity) values('Frühstuck','2'); ");//16
            db.execSQL("insert into SubActivities(Title, id_Activity) values('Mittagessen','2'); ");//17
            db.execSQL("insert into SubActivities(Title, id_Activity) values('Abendessen','2'); ");//18
            db.execSQL("insert into SubActivities(Title, id_Activity) values('Auto fahren','4'); ");//19


            // Create Location Table
            db.execSQL(LOCATION_CREATE_TABLE);
            Log.d("Database", "Location Table Created");
            db.execSQL("insert into Location(Latitude, Longtitude, Title) values (-1,-1,'Other'); ");   //if the location is unknown


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

            //db.close();
        }catch(Exception e)
        {
            e.getMessage();
        }
       // db.close();
    }

    public ArrayList<String> GetSubActivities(int idActivity)
    {
        SQLiteDatabase db1 = this.getReadableDatabase();
        Cursor cursor = db1.rawQuery("select Title from SubActivities where id_Activity= "+ String.valueOf(idActivity)+ "; ", null);
        ArrayList<String> SubActivityList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                SubActivityList.add(cursor.getString(0).replace(" ",""));
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
        SQLiteDatabase db1 = this.getReadableDatabase();
        Cursor cursor = db1.rawQuery("select id from Activities where title= '"+ activity+ "'; ", null);
        if (cursor.moveToFirst()) {
                return (cursor.getInt(0));
        }
        return -1;
    }

    public int getActivityIDbySubActicity(String subactivity)
    {
        SQLiteDatabase db1 = this.getReadableDatabase();
        Cursor cursor = db1.rawQuery("select id_Activity from SubActivities where title= '"+ subactivity+ "'; ", null);
        if (cursor.moveToFirst()) {
            return (cursor.getInt(0));
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
        return "Default";
    }

    public String getActivitybySubActicityId(int subactivityId)
    {
        SQLiteDatabase db1 = this.getReadableDatabase();
        Cursor cursor = db1.rawQuery("select Activities.Title from SubActivities inner join Activities on SubActivities.id_Activity=Activities.id where SubActivities.id= '"+ subactivityId+ "'; ", null);
        if (cursor.moveToFirst()) {
            return (cursor.getString(0));
        }
        return "Default";
    }

    public int getActivityIdbySubActicityId(int subactivityId)
    {
        SQLiteDatabase db1 = this.getReadableDatabase();
        Cursor cursor = db1.rawQuery("select Activities.id from SubActivities inner join Activities on SubActivities.id_Activity=Activities.id where SubActivities.id= '"+ subactivityId+ "'; ", null);
        if (cursor.moveToFirst()) {
            return (cursor.getInt(0));
        }
        return 15;
    }

    public int getSubactivityID(String subactivity)
    {
        SQLiteDatabase db1 = this.getReadableDatabase();
        Cursor cursor = db1.rawQuery("select id from SubActivities where title= '"+ subactivity + "'; ", null);
        if (cursor.moveToFirst()) {
            return (cursor.getInt(0));
        }
        return -1;
    }

    public String getSubactivity(int subactivityID)
    {
        SQLiteDatabase db1 = this.getReadableDatabase();
        Cursor cursor = db1.rawQuery("select title from SubActivities where id= "+ subactivityID + "; ", null);
        if (cursor.moveToFirst()) {
            return (cursor.getString(0));
        }
        return "";
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older tables if they exist
        db.execSQL(SUB_ACTIVITIES_DELETE_TABLE);
        db.execSQL(ACTIVITES_DELETE_TABLE);
        db.execSQL(LOCATION_DELETE_TABLE);
        db.execSQL(ACTIVITYLIST_DELETE_TABLE);

        this.onCreate(db);
        Log.d("Database", "Database Upgraded, All Tables Dropped");
    }

//    Insert Statements
    public void insertLocation(DataBaseHandler handler, double lat, double longt, String title) {
        SQLiteDatabase db1 = handler.getWritableDatabase();
        db1.execSQL("insert into Location(Latitude, Longtitude, Title) values(" + lat + "," + longt + "," + "'" + title + "'" + "); ");
        db1.close();
    }

    public void InsertActivity(DataBaseHandler handler, ActivityItem Activ) {
        String ImagePath = Activ.getImagePath();
        int idActivity = Activ.getActivityId();
        int idSubActivity = Activ.getSubactivityId();
        int idLocation =1;
        String Start = Activ.getStarttimeAsString();
        String End = Activ.getEndtimeAsString();
        String Meal = Activ.getMeal();
        if (ImagePath== null){
            ImagePath = "";
        }
        else {
            ImagePath = Activ.getImagePath();
        }
        int Intensity = Activ.getIntensity();

        SQLiteDatabase db1 = handler.getWritableDatabase();
        db1.execSQL("insert into ActivityList(id_SubActivity, id_Location, Start, End, Meal, ImagePath, Intensity) values("+ idActivity + "," + idLocation + " , '" + Start + "','" + End + "','" + Meal + "','" + ImagePath + "'," + Intensity + "); ");
        db1.close();
    }


    public void InsertActivity(DataBaseHandler handler, int idActivity, int idLocation, String Start, String End) {
        SQLiteDatabase db1 = handler.getWritableDatabase();
        db1.execSQL("insert into ActivityList(id_SubActivity, id_Location, Start, End) values(" + idActivity + "," + idLocation + " , '" + Start + "','" + End + "' ); ");
        db1.close();
    }

    public void InsertProfile(DataBaseHandler handler, String name, String surename, int age)
    {
        try {
            SQLiteDatabase db1 = handler.getWritableDatabase();
            long tslong = System.currentTimeMillis() / 1000;
            db1.execSQL("insert into " + PROFILE_TABLE_NAME + "(name, lastname, age, timestamp)" +
                    " values('" + name + "' , '" + surename + "' , '" + age + "' , '" + tslong + "' );");
            db1.close();
        }catch(Exception e)
        {
            e.getMessage();
        }
    }

    /***
     * Insert a new bloodsugar level
     * @param handler
     * @param profile_id
     * @param bloodsugar_level
     * @param measure_unit
     */
    public void InsertBloodsugar(DataBaseHandler handler, Date date, Time time, int profile_id, double bloodsugar_level, String measure_unit) {
        SQLiteDatabase db1 = handler.getWritableDatabase();
        Timestamp timestamp = Timestamp.valueOf(date.toString() + " " + time.toString());
        Log.d("Database", timestamp + "InsertBloodSugar");

        db1.execSQL("insert into " + MEASUREMENT_TABLE_NAME + "(profile_ID, measure_value, timestamp, measure_unit, measure_kind) values(" + profile_id + ","
                + bloodsugar_level + " , '" + timestamp + "' , '" + measure_unit + "' , 'bloodsugar');");
        db1.close();
    }


    public void InsertBloodsugarEntryScreen(long timestamp, int profile_id, double bloodsugar_level, String measure_unit) {
        SQLiteDatabase db1 = this.getWritableDatabase();
        Log.d("Database", timestamp + "InsertBloodSugar");

        db1.execSQL("insert into " + MEASUREMENT_TABLE_NAME + "(profile_ID, measure_value, timestamp, measure_unit, measure_kind) values(" + profile_id + ","
                + bloodsugar_level + " , '" + timestamp + "' , '" + measure_unit + "' , 'bloodsugar');");
        db1.close();
    }

    public void InsertInsulinEntryScreen(long timestamp, int profile_id, double insulin_level, String insulin_unit) {
        SQLiteDatabase db1 = this.getWritableDatabase();
        Log.d("Database", timestamp + "InsertInsulin");

        db1.execSQL("insert into " + INSULIN_TABLE_NAME + "(profile_ID, insulin_value, timestamp, insulin_unit, insulin_kind) values(" + profile_id + ","
                + insulin_level + " , '" + timestamp + "' , '" + insulin_unit + "' , 'insulin');");
        db1.close();
    }

    /***
     * Insert a new insulin level
     * @param handler
     * @param profile_id
     * @param insulin
     * @param measure_unit
     */
    public void InsertInsulin(DataBaseHandler handler, int profile_id, double insulin, String measure_unit) {
        SQLiteDatabase db1 = handler.getWritableDatabase();
        long tslong = System.currentTimeMillis() / 1000;
        db1.execSQL("insert into " + MEASUREMENT_TABLE_NAME + "(profile_ID, measure_value, timestamp, measure_unit, measure_kind) values(" + profile_id + ","
                + insulin + " , '" + tslong + "' , '" +measure_unit+"' , 'insulin');");
        db1.close();
    }

  /*  public void InsertInsulin(int profile_id, double insulin, String measure_unit,long tslong) {
        SQLiteDatabase db1 = this.getWritableDatabase();
        //long tslong = System.currentTimeMillis() / 1000;
        db1.execSQL("insert into " + MEASUREMENT_TABLE_NAME + "(profile_ID, measure_value, timestamp, measure_unit, measure_kind) values(" + profile_id + ","
                + insulin + " , '" + tslong + "' , '" +measure_unit+"' , 'insulin');");
        db1.close();
    }*/

    /***
     * Insert a new weight measurement
     * @param handler
     * @param profile_id
     * @param weight
     * @param measure_unit
     */
    public void InsertWeight(DataBaseHandler handler, int profile_id, double weight, String measure_unit) {
        SQLiteDatabase db1 = handler.getWritableDatabase();
        long tslong = System.currentTimeMillis() / 1000;
        db1.execSQL("insert into " + MEASUREMENT_TABLE_NAME + "(profile_ID, measure_value, timestamp, measure_unit, measure_kind) values(" + profile_id + ","
                + weight + " , '" + tslong + "' , '" +measure_unit+"' , 'weight');");
        db1.close();
    }

    /***
     * Returns the last weight measurement
     * @param handler
     * @param profile_id
     * @return
     */
    public String[] GetLastWeight(DataBaseHandler handler, int profile_id)
    {
        try {
            SQLiteDatabase db1 = handler.getWritableDatabase();
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
            return result;
        }
        catch (Exception e)
        {
            return null;
        }

    }
    public void ReplaceActivity(DataBaseHandler handler,  ActivityItem Activ){
        String Start = Activ.getStarttimeAsString();
        String End = Activ.getEndtimeAsString();
        findActionbyStartEndTime(handler, Start, End);
        findActionbyStartEndTime2(handler, Start, End);
        findActionbyStartTime(handler, Start);
        findActionbyEndTime(handler, End);

        SQLiteDatabase db1 = handler.getWritableDatabase();
        InsertActivity(handler, Activ);
        db1.close();
    }

    public int getSuperActivityID(int idActivity){
        SQLiteDatabase db = this.getReadableDatabase();
        //Return Super Activity of idActivity
        Cursor cursor = db.rawQuery("select * from SuperActivities where id = (select id_SuperActivity from Activities where id =" + idActivity +")", null);
        if (cursor.moveToFirst()){
            return cursor.getInt(0);
        }
        return 6;
    }

    public String getSuperActivity(int idActivity){
        SQLiteDatabase db = this.getReadableDatabase();
        //Return Super Activity of idActivity
        Cursor cursor = db.rawQuery("select * from SuperActivities where id = (select id_SuperActivity from Activities where id =" + idActivity +")", null);
        if (cursor.moveToFirst()){
            return cursor.getString(1);
        }
        return "Default";
    }

    public void InsertNewRoutine(DataBaseHandler handler, ArrayList<Prediction.PeriodAction> prediction) {
        int idActivity;
        int idLocation;
        String Start;
        String End;

        SQLiteDatabase db1 = handler.getWritableDatabase();
        for(int i=0;i<prediction.size();i++){
            idActivity =prediction.get(i).Action+1;
            idLocation = 1;
            String StartOfDay, EndOfDay;
            Calendar calendar = Calendar.getInstance();
            int Year = calendar.get(Calendar.YEAR);
            String Month = formatMonthOrDay(calendar.get(Calendar.MONTH) + 1);
            String Day = formatMonthOrDay(calendar.get(Calendar.DAY_OF_MONTH));
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
            db1.execSQL("insert into ActivityList(id_SubActivity, id_Location, Start, End) values(" + idActivity + "," + idLocation + " , '" + Start + "','" + End + "' ); ");
        }
        db1.close();
    }

//    Select Statements
    //METHODS

    public Cursor getAllLocations(DataBaseHandler helper) {
        SQLiteDatabase db = helper.getWritableDatabase();
        //Create a Cursor that contains all records from the locations table
        Cursor cursor = db.rawQuery("select * from " + LOCATION_TABLE_NAME, null);
        return cursor;
    }

    /**
     * returns an activity name by id
     * @param helper
     * @param id
     * @return
     */
    public String getActionById(DataBaseHandler helper, int id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        //Create a Cursor that contains all records from the locations table
        Cursor cursor = db.rawQuery("select title from " + ACTIVITIES_TABLE_NAME + " where id=" + id, null);
        String name = "";
        if(cursor.moveToFirst()){
            name = cursor.getString(0);
        }
        return name;
    }

    public Cursor getAllActions(DataBaseHandler helper) {
        SQLiteDatabase db = helper.getWritableDatabase();
        //Create a Cursor that contains all records from the locations table
        Cursor cursor = db.rawQuery("select * from " + ACTIVITIES_TABLE_NAME, null);
        return cursor;
    }

    /**
     * 27.06.2016 Stefan
     * returns all activities as a list
     * @param helper
     * @return
     */
    public ArrayList<String> getAllActionsAsList(DataBaseHandler helper) {
        SQLiteDatabase db = helper.getWritableDatabase();
        //Create a Cursor that contains all records from the locations table
        Cursor cursor = db.rawQuery("select * from " + ACTIVITIES_TABLE_NAME, null);
        ArrayList<String> actionsList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                actionsList.add(cursor.getString(1).replace(" ",""));
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
        Cursor cursor = db.rawQuery("select ActivityList.id, SubActivities.title, Location.Title, ActivityList.Start, ActivityList.End from ActivityList inner join SubActivities on ActivityList.id_SubActivity = SubActivities.id inner join Location on ActivityList.id_Location = Location.id", null);
        return cursor;
    }

    public ArrayList<String[]> getAllEvents(DataBaseHandler handler){
        SQLiteDatabase db = handler.getReadableDatabase();
        ArrayList<String[]> eventList = new ArrayList<String[]>();
        Cursor cursor = db.rawQuery("select ActivityList.id, SubActivities.title, ActivityList.Start, ActivityList.End from ActivityList inner join SubActivities on ActivityList.id_SubActivity = SubActivities.id", null);
        if(cursor.moveToFirst()){
            do {
                String[] event = {cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3)};
                eventList.add(event);
            } while (cursor.moveToNext());
        }
        return eventList;
    }
    /***
     * returns the last measurement of blood sugar and insulin of the selected user
     * @param handler
     * @return
     */
    public Cursor getAllMeasurements(DataBaseHandler handler, int profile_id) {
        try {
            SQLiteDatabase db1 = handler.getWritableDatabase();
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

    /***
     * returns the last measurement of the selected user
     * @param handler
     * @return
     */
    public String[] getLastBloodsugarMeasurement(DataBaseHandler handler, int profile_id){
        try {
            SQLiteDatabase db1 = handler.getWritableDatabase();
            String[] result = new String[2];
            Cursor cursor = db1.rawQuery("SELECT measure_value,measure_unit " +
                    "FROM " + MEASUREMENT_TABLE_NAME + " " +
                    "where profile_ID = " + profile_id + " " +
                    "and measure_kind = 'bloodsugar'" +
                    "ORDER BY timestamp DESC;", null);
            cursor.moveToFirst();
            if (cursor.getCount() >= 1) {
                result[0] = cursor.getString(0);
                result[1] = cursor.getString(1);
            } else {
                result = null;
            }
            return result;
        }
        catch (Exception e)
        {
            return null;
        }


    }

    /***
     * returns the last insulin of the selected user
     * @param handler
     * @return
     */
    public String[] getLastInsulinMeasurement(DataBaseHandler handler, int profile_id){
        try {
            SQLiteDatabase db1 = handler.getWritableDatabase();
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
     * @param handler database handler instance
     * @param date date for which the values should be retrieved
     * @param window "DAY","WEEK" or "MONTH" are acceptable values. Counting backwards from date the
     *               values for that timeframe are returned
     * @param measure_kind "insulin" or "bloodsugar"
     * @return ArrayList containing all the MeasureItems for a time window
     */
    public ArrayList<MeasureItem> getMeasurementValues(DataBaseHandler handler, Date date, String window, String measure_kind){
        SQLiteDatabase db = handler.getReadableDatabase();
        Long[] windowStartEnd = TimeUtils.convertDateStringToTimestamp(getWindowStartEnd(date,window));
        Cursor cursor = db.rawQuery("select measure_value, measure_unit, timestamp from  " + MEASUREMENT_TABLE_NAME + " "  +
                "where timestamp>='"+windowStartEnd[0]+"' and timestamp <'"+windowStartEnd[1]+"'" +
                "AND measure_kind = '"+measure_kind+"';",null);
        ArrayList<MeasureItem> measureList = new ArrayList<>();
        MeasureItem measureItem = null;
        if(cursor.moveToFirst()){
            do{
                measureItem = new MeasureItem(Long.parseLong(cursor.getString(2)),cursor.getDouble(0),cursor.getString(1));
                measureList.add(measureItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return measureList;
    }

    /**
     * Retrieve all measurements of a certain measure kind
     * @param handler database handler instance
     * @param measure_kind "insulin" or "bloodsugar"
     * @return ArrayList containing all measurements as MeasureItems
     * @author Stefan 09.07.2016
     */
    public ArrayList<MeasureItem> getAllMeasurementValues(DataBaseHandler handler, String measure_kind){
        SQLiteDatabase db = handler.getReadableDatabase();
        Cursor cursor = db.rawQuery("select measure_value, measure_unit, timestamp from  " + MEASUREMENT_TABLE_NAME + " "  +
                "where measure_kind = '"+measure_kind+"';",null);
        ArrayList<MeasureItem> measureList = new ArrayList<>();
        MeasureItem measureItem = null;
        if(cursor.moveToFirst()){
            do{
                measureItem = new MeasureItem(cursor.getLong(2),cursor.getDouble(0),cursor.getString(1));
                measureList.add(measureItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return measureList;
    }

    /**
     * returns most recent bloodsugar measurment
     * @param handler database handler instance
     * @param measure_kind "insulin" or "bloodsugar"
     * @return the most recentMeasureItem
     * @author Stefan 09.07.2016
     */
    public MeasureItem getMostRecentMeasurmentValue(DataBaseHandler handler, String measure_kind){
        SQLiteDatabase db = handler.getReadableDatabase();
        Cursor cursor = db.rawQuery("select measure_value, measure_unit, MAX(timestamp) from  " + MEASUREMENT_TABLE_NAME + " "  +
                "where measure_kind = '"+measure_kind+"' group by measure_value, measure_unit;",null);
        ArrayList<MeasureItem> measureList = new ArrayList<>();
        MeasureItem measureItem = null;
        if(cursor.moveToFirst()){
            do{
                measureItem = new MeasureItem(cursor.getLong(2),cursor.getDouble(0),cursor.getString(1));
                measureList.add(measureItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        if(measureList.size() == 0){
            return null;
        }
        return measureList.get(0);
    }

    /**
     * @author Ivo Gosemann 08.04.2016
     * TODO: Merge with GetDay
     * @param handler instance of the DB Handler
     * @param date current date of the day
     * @param window String value to indicate the time window ["DAY","WEEK","MONTH"]
     * @return ArrayList of all ActivityItems in the timeframe
     */
    public ArrayList<ActivityItem> getActivities(DataBaseHandler handler, Date date, String window){
//        calculate the timeframe for the given date and window values
        String[] timeWindow = getWindowStartEnd(date,window);
        String StartOfDay = timeWindow[0],EndOfDay = timeWindow[1];
        SQLiteDatabase db = handler.getReadableDatabase();
        String S = "select SubActivities.id, ActivityList.Start, ActivityList.End from ActivityList inner join SubActivities on ActivityList.id_SubActivity = SubActivities.id where (ActivityList.End >= '" + StartOfDay + "' and ActivityList.Start < '" + EndOfDay + "') or (ActivityList.Start < '" + EndOfDay + "' and ActivityList.Start >= '" + StartOfDay+ "');";
        Cursor cursor = db.rawQuery("select SubActivities.id, ActivityList.Start, ActivityList.End from ActivityList inner join SubActivities on ActivityList.id_SubActivity = SubActivities.id where ActivityList.End >= '" + StartOfDay + "' and ActivityList.Start < '" + EndOfDay + "' or ActivityList.Start < '" + EndOfDay + "' and ActivityList.Start >= '" + StartOfDay+ "' order by ActivityList.Start;", null);

        ArrayList<ActivityItem> activityList = GetArrayFromCursor(cursor, date);
        return activityList;
    }

    public ArrayList<ActivityItem> GetDay(DataBaseHandler handler, Date Date) {
        String StartOfDay, EndOfDay;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Date);
        int Year = calendar.get(Calendar.YEAR);
        String Month = formatMonthOrDay(calendar.get(Calendar.MONTH) + 1);
        String Day = formatMonthOrDay(calendar.get(Calendar.DAY_OF_MONTH));
        StartOfDay = String.valueOf(Year) + "-" + String.valueOf(Month) + "-" + String.valueOf(Day) + " " + "00:00";
        EndOfDay = String.valueOf(Year) + "-" + String.valueOf(Month) + "-" + String.valueOf(Day) + " " + "23:59";

        //String S = "select Activities.id, ActivityList.Start, ActivityList.End from ActivityList inner join Activities on ActivityList.id_Activity = Activities.id where ActivityList.Start > '" + StartOfDay + "' and ActivityList.Start < '" + EndOfDay + "'";

        SQLiteDatabase db = handler.getReadableDatabase();
        String S = "select SubActivities.id, ActivityList.Start, ActivityList.End from ActivityList inner join SubActivities on ActivityList.id_SubActivity = SubActivities.id where (ActivityList.End >= '" + StartOfDay + "' and ActivityList.Start < '" + EndOfDay + "') or (ActivityList.Start < '" + EndOfDay + "' and ActivityList.Start >= '" + StartOfDay+ "');";
        Cursor cursor = db.rawQuery("select SubActivities.id, ActivityList.Start, ActivityList.End from ActivityList inner join SubActivities on ActivityList.id_SubActivity = SubActivities.id where ActivityList.End >= '" + StartOfDay + "' and ActivityList.Start < '" + EndOfDay + "' or ActivityList.Start < '" + EndOfDay + "' and ActivityList.Start >= '" + StartOfDay+ "' order by ActivityList.Start;", null);
        //(ActivityList.End > '2016-01-01 00:00' and ActivityList.Start < '2016-01-01 23:59') or (ActivityList.Start < '2016-01-01 23:59' and ActivityList.Start > '2016-01-01 00:00')
        return GetArrayFromCursor(cursor, Date);
    }


//    Delete Statements

    public void DeleteActivity(DataBaseHandler handler, ActivityItem Activ) {
        String Start = Activ.getStarttimeAsString();
        String End = Activ.getEndtimeAsString();
        SQLiteDatabase db1 = handler.getWritableDatabase();
        if (AppGlobal.getEditFlag()) {
            db1.execSQL("delete from ActivityList where Start = '" + Start + "' and End = '" + End + "';");
        }
        else{
            db1.execSQL("delete from ActivityList where Start = '" + Start + "' and End = '" + End + "';");
            db1.execSQL("insert into ActivityList(id_SubActivity, id_Location, Start, End) values(15,1,'"+Start+"','"+End+"');");
        }

        db1.close();
    }
    public void DeleteActivity(DataBaseHandler handler, String Start, String End) {
        SQLiteDatabase db1 = handler.getWritableDatabase();

        if (AppGlobal.getEditFlag()) {
            db1.execSQL("delete from ActivityList where Start = '" + Start + "' and End = '" + End + "';");
        }
        else{
            db1.execSQL("delete from ActivityList where Start = '" + Start + "' and End = '" + End + "';");
            db1.execSQL("insert into ActivityList(id_SubActivity, id_Location, Start, End) values(15,1,'"+Start+"','"+End+"');");
        }
        db1.close();
    }


    private void findActionbyStartTime(DataBaseHandler handler, String Start) {
        SQLiteDatabase db1 = handler.getWritableDatabase();
        Cursor cursor = db1.rawQuery("select id from ActivityList where Start <= '" + Start + "' and End >= '" + Start + "'; ", null);
        if (cursor.moveToFirst()) {
            do {
                db1.execSQL("update ActivityList set End = '" + MinusMinute(Start) + "' where id = '" + cursor.getString(0) + "';");
            } while (cursor.moveToNext());
            db1.close();
        }
    }

    private void findActionbyEndTime(DataBaseHandler handler, String End) {
        SQLiteDatabase db1 = handler.getWritableDatabase();
        Cursor cursor = db1.rawQuery("select id from ActivityList where Start <= '" + End + "' and End >= '" + End + "'; ", null);
        if (cursor.moveToFirst()) {
            do {
                db1.execSQL("update ActivityList set Start = '" + PlusMinute(End) + "' where id = '" + cursor.getString(0) + "';");
            } while (cursor.moveToNext());
            db1.close();
        }
    }

    private void findActionbyStartEndTime(DataBaseHandler handler, String Start, String End) {
        SQLiteDatabase db1 = handler.getWritableDatabase();
        Cursor cursor = db1.rawQuery("select id from ActivityList where Start >= '" + Start + "' and End <= '" + End + "'; ", null);
        if (cursor.moveToFirst()) {
            do {
                db1.execSQL("delete from ActivityList where id = '" + cursor.getString(0) + "';");
            } while (cursor.moveToNext());
            db1.close();
        }
    }


    private void findActionbyStartEndTime2(DataBaseHandler handler, String Start, String End) {
        SQLiteDatabase db1 = handler.getWritableDatabase();
        Cursor cursor = db1.rawQuery("select * from ActivityList where Start <= '" + Start + "' and End >= '" + End + "'; ", null);
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(0);
                int idSubActivity = cursor.getInt(1);
                int idLocation = cursor.getInt(2);
                String Start1 = cursor.getString(3);
                String End1 = cursor.getString(4);
                db1.execSQL("update ActivityList set End = '" + MinusMinute(Start) + "' where id = '" + cursor.getString(0) + "';");
                InsertActivity(handler, idSubActivity, idLocation, End, End1);
            } while (cursor.moveToNext());
            db1.close();
        }
    }

//      Handler Utility Methods
    public boolean CheckRoutineAdded(DataBaseHandler handler){
        String StartOfDay, EndOfDay;
        Calendar calendar = Calendar.getInstance();
        int Year = calendar.get(Calendar.YEAR);
        String Month = formatMonthOrDay(calendar.get(Calendar.MONTH) + 1);
        String Day = formatMonthOrDay(calendar.get(Calendar.DAY_OF_MONTH));
        StartOfDay = String.valueOf(Year) + "-" + String.valueOf(Month) + "-" + String.valueOf(Day) + " " + "00:00";
        EndOfDay = String.valueOf(Year) + "-" + String.valueOf(Month) + "-" + String.valueOf(Day) + " " + "23:59";
        SQLiteDatabase db = handler.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from ActivityList where Start>= '" + StartOfDay + "' and End<= '" + EndOfDay + "'", null);
        if (cursor.getCount() < 1)
        {
            return false;
        }
        else{
            return true;
        }
    }

    public ArrayList<ActivityItem> GetArrayFromCursor(Cursor cursor, Date Date) {
        int ActionID;
        Date Start;
        Date End;
        Date EndOfDay,StartOfDay;
        ArrayList<ActivityItem> Activities = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                try {
                    ActionID = cursor.getInt(0);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                    Start = format.parse(cursor.getString(1));
                    End = format.parse(cursor.getString(2));
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
                    ActivityItem PA = new ActivityItem(ActionID,0,Start,End);
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
    /**
     * Ivo Gosemann 18.03.2016
     * Reusing Leonids Code to calculate the start and end of a day
     * The start and end are then returned as unix timestamps
     * In Addition a Parameter can be provided to specify the time window
     * DAY, WEEK or MONTH are acceptable inputs
     * @param date the day for which start and end shall be returned
     * @param window string with the value for the timeframe
     * @return array with 2 fields [0] = windowStart ; [1] = windowEnd
     */
    private String[] getWindowStartEnd(Date date,String window) {
        String startDay, endDay;
        Timestamp timestampStart=null,timestampEnd =null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
//      Set the end of the time window
        int year = calendar.get(Calendar.YEAR);
        String month = formatMonthOrDay(calendar.get(Calendar.MONTH) + 1);
        String day = formatMonthOrDay(calendar.get(Calendar.DAY_OF_MONTH));
        endDay = String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day) + " " + "23:59";
        switch (window){
            case "MONTH":
                calendar.add(Calendar.DAY_OF_MONTH,-30);
                break;
            case "WEEK":
                calendar.add(Calendar.DAY_OF_MONTH,-7);
        }
        year =calendar.get(Calendar.YEAR);
        month = formatMonthOrDay(calendar.get(Calendar.MONTH)+1);
        day = formatMonthOrDay(calendar.get(Calendar.DAY_OF_MONTH));

        startDay = String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day) + " " + "00:00";
        String[] timeWindow = {startDay,endDay};
        return timeWindow;
    }


    public String formatMonthOrDay(int i) {
        if (i > 9) {
            return String.valueOf(i);
        } else {
            return "0" + String.valueOf(i);
        }
    }

    public String PlusMinute(String SDate1) {
        Date Date1;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            Date1 = format.parse(SDate1);
            Date1 = TimeUtils.addMinuteFromDate(Date1, 1);
            SDate1 = TimeUtils.dateToDateTimeString(Date1);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return SDate1;
    }

    public String MinusMinute(String SDate1) {
        Date Date1;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            Date1 = format.parse(SDate1);
            Date1 = TimeUtils.addMinuteFromDate(Date1, -1);
            SDate1 = TimeUtils.dateToDateTimeString(Date1);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return SDate1;
    }



    public String[] getUser(DataBaseHandler handler, int id)
    {
        SQLiteDatabase db1 = handler.getWritableDatabase();
        try {

            String[] result = new String[2];
            Cursor cursor = db1.rawQuery("SELECT name, lastname from "+ PROFILE_TABLE_NAME + " where id = " + id +";",null);
            if (cursor.getCount() >= 1) {
                cursor.moveToFirst();
                result[0] = cursor.getString(0);
                //cursor.moveToNext();
                result[1] = cursor.getString(1);
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

    public int getUserID(DataBaseHandler handler)
    {
        SQLiteDatabase db1 = handler.getWritableDatabase();
        try {
            Cursor cursor = db1.rawQuery("SELECT MAX(id) from "+ PROFILE_TABLE_NAME +";",null);
            if (cursor.getCount() >= 1) {
                cursor.moveToFirst();
                return cursor.getInt(0);
            } else {
                return cursor.getInt(0);
            }
        }catch(Exception e)
        {
            e.getMessage();
            return 0;
        }
    }

}

//  TODO: Methods which will be deleted
//    /*
//    Naira: Inser ts a new measurements input
//     */
//    public void InsertMeasurements(DataBaseHandler handler, int bloodsugar_level, int insulin_dosage, String timestamp) {
//        SQLiteDatabase db1 = handler.getWritableDatabase();
//        //long tslong = System.currentTimeMillis() / 1000;
//        db1.execSQL("insert into History_Bloodsugar(bloodsugar_level,insulin_dosage, timestamp) values("  + bloodsugar_level + " , " + insulin_dosage + " , '"+ timestamp + "' ); ");
//        db1.close();
//    }
//
//    public Cursor GetMeasurements(DataBaseHandler handler){
//        SQLiteDatabase db1 = handler.getWritableDatabase();
//        Cursor cursor = db1.rawQuery("select * from History_Bloodsugar", null);
//        return cursor;
//    }

//
//    public void InsertProfile(DataBaseHandler handler, int age, int dt, double abl) {
//        SQLiteDatabase db1 = handler.getWritableDatabase();
//        db1.execSQL("insert into Profile(age, diabetes_type, average_bloodsugar_level) values(" + age + "," + dt + "," + "'" + abl + "'" + "); ");
//        db1.close();
//    }
//    /***
//     * returns the ID of the current user
//     * @param handler
//     * @param name
//     * @param surename
//     * @return UserID
//     */
//    public String GetProfileId(DataBaseHandler handler,String name, String surename)
//    {
//        SQLiteDatabase db1 = handler.getWritableDatabase();
//        Cursor cursor = db.rawQuery("select id from "+ PROFILE_TABLE_NAME+ ";",null);
//        db1.close();
//        return cursor.getString(1);
//    }