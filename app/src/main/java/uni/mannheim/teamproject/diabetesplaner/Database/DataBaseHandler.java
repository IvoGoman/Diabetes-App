package uni.mannheim.teamproject.diabetesplaner.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import uni.mannheim.teamproject.diabetesplaner.DataMining.Prediction;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Utility.Util;


/**
 * Created by leonidgunko on 31.10.15.
 */
public class DataBaseHandler extends SQLiteOpenHelper {

    public SQLiteDatabase db;

    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        Log.d("Database", "MySQLiteHelper Constructor Started");
    }

    // Database Constants
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Diabetes.db";


    // Activity Table
    private static final String ACTIVITIES_TABLE_NAME = "Activities";
    private static final String ACTIVITIES_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " +
                    ACTIVITIES_TABLE_NAME +
                    " (id INTEGER PRIMARY KEY, title VARCHAR(20));";
    public static final String ACTIVITES_SELECT =
            "SELECT * FROM " + ACTIVITIES_TABLE_NAME + ";";
    public static final String ACTIVITES_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + ACTIVITIES_TABLE_NAME + ";";

    // SubActivity Table
    private static final String SUB_ACTIVITIES_TABLE_NAME = "SubActivities";
    private static final String SUB_ACTIVITIES_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " +
                    SUB_ACTIVITIES_TABLE_NAME +
                    " (id INTEGER PRIMARY KEY, id_Activity INTEGER, Title VARCHAR(20), FOREIGN KEY(id_Activity) REFERENCES Activities(id));";
    public static final String Sub_ACTIVITIES_SELECT =
            "SELECT * FROM " + SUB_ACTIVITIES_TABLE_NAME + ";";
    public static final String SUB_ACTIVITIES_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + SUB_ACTIVITIES_TABLE_NAME + ";";

    //Location Table
    private static final String LOCATION_TABLE_NAME = "Location";
    private static final String LOCATION1_TABLE_NAME = "Location1";
    private static final String LOCATION_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " +
                    LOCATION_TABLE_NAME +
                    " (id INTEGER PRIMARY KEY, Latitude double, Longtitude double, Title VARCHAR(20));";
    private static final String LOCATION1_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " +
                    LOCATION1_TABLE_NAME +
                    " (id INTEGER PRIMARY KEY, Latitude double, Longtitude double, Timestamp DateTime);";
    public static final String LOCATION_SELECT =
            "SELECT * FROM " + LOCATION_TABLE_NAME + ";";
    public static final String LOCATION_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + LOCATION_TABLE_NAME + ";";

    //ActivityList Table
    /*
    public static final String TAG = ActivityItem.class.getSimpleName();

    private int activityId;
    private int subactivityId;
    private Date starttime;
    private Date endtime;
    private String meal;
    private String imagePath;
    private Date date;
    private Integer intensity;
    */
    private static final String ACTIVITYLIST_TABLE_NAME = "ActivityList";
    private static final String ACTIVITYLIST_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " +
                    ACTIVITYLIST_TABLE_NAME +
                    " (id INTEGER PRIMARY KEY, id_Activity Integer, id_Location Integer, Start DateTime, End DateTime, Meal String, ImagePath String, Intensity Integer, FOREIGN KEY(id_Activity) REFERENCES Activities(id), FOREIGN KEY(id_Location) REFERENCES Locations(id) );";
    public static final String ACTIVITYLIST_SELECT =
            "SELECT * FROM " + ACTIVITYLIST_TABLE_NAME + ";";
    public static final String ACTIVITYLIST_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + ACTIVITYLIST_TABLE_NAME + ";";

    /* JW: Added Bloodsugar_History Table
    */

    //Bloodsugar History
    public static final String MEASUREMENT_TABLE_NAME = "Measurements";
    public static final String MEASUREMENT_CREATE_TABLE = " CREATE TABLE IF NOT EXISTS " + MEASUREMENT_TABLE_NAME +
            "(timestamp DateTime PRIMARY KEY, profile_ID INTEGER, measure_value double, measure_unit VARCHAR(8), measure_kind VARCHAR(8));";

    public static final String MEASUREMENT_SELECT =
            "SELECT * FROM " + MEASUREMENT_TABLE_NAME + ";";
    public static final String MEASUREMENT_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + MEASUREMENT_TABLE_NAME + ";";

    //Profile Table
    private static final String PROFILE_TABLE_NAME = "Profile";
    private static final String PROFILE_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " +
                    PROFILE_TABLE_NAME +
                    " (id INTEGER PRIMARY KEY, name VARCHAR(20), lastname VARCHAR(20), " +
                    "age INTEGER, diabetes_type INTEGER, " + "timestamp Timestamp, " +
                    "FOREIGN KEY (id) REFERENCES "+MEASUREMENT_TABLE_NAME+"(profile_ID));";
    public static final String PROFILE_SELECT =
            "SELECT * FROM " + PROFILE_TABLE_NAME + ";";
    public static final String PROFILE_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + PROFILE_TABLE_NAME + ";";


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

    public Cursor getAllRoutine(DataBaseHandler helper) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select ActivityList.id, Activities.title, Location.Title, ActivityList.Start, ActivityList.End from ActivityList inner join Activities on ActivityList.id_Activity = Activities.id inner join Location on ActivityList.id_Location = Location.id", null);
        return cursor;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {   //when the App is first installed
        try {
            // Create Activity Table
            db.execSQL(ACTIVITIES_CREATE_TABLE);
            Log.d("Database", "Temp Activity Table Created");
            db.execSQL("insert into Activities(Title) values('Schlafen'); ");
            db.execSQL("insert into Activities(Title) values('Essen/Trinken'); ");
            db.execSQL("insert into Activities(Title) values('Körperpflege'); ");
            db.execSQL("insert into Activities(Title) values('Transportmittel benutzen'); ");
            db.execSQL("insert into Activities(Title) values('Entspannen'); ");
            db.execSQL("insert into Activities(Title) values('Fortbewegen(mit Gehilfe)'); ");
            db.execSQL("insert into Activities(Title) values('Medicamente einnehmen'); ");
            db.execSQL("insert into Activities(Title) values('Einkaufen'); ");
            db.execSQL("insert into Activities(Title) values('Hausarbeit'); ");
            db.execSQL("insert into Activities(Title) values('Essen zubereiten'); ");
            db.execSQL("insert into Activities(Title) values('Geselligkeit'); ");
            db.execSQL("insert into Activities(Title) values('Fortbewegen'); ");
            db.execSQL("insert into Activities(Title) values('Schreibtischarbeit'); ");
            db.execSQL("insert into Activities(Title) values('Sport'); ");
            db.execSQL("insert into Activities(Title) values('Default'); "); //15
            // Create SubActivities Table
            db.execSQL(SUB_ACTIVITIES_CREATE_TABLE);
            Log.d("Database", "Sub Activities Table Created");

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
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(1, 1, '2016-01-01 00:00' , '2016-01-01 09:00'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(4, 1, '2016-01-01 09:05' , '2016-01-01 09:30'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(2, 1, '2016-01-01 09:30' , '2016-01-01 10:00'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(3, 1, '2016-01-01 10:03' , '2016-01-01 13:55'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(4, 1, '2016-01-01 13:55' , '2016-01-01 14:45'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(3, 1, '2016-01-01 15:00' , '2016-01-01 19:00'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(2, 1, '2016-01-01 19:03' , '2016-01-01 19:40'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(4, 1, '2016-01-01 19:45' , '2016-01-01 20:00'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(1, 1, '2016-01-01 20:00' , '2016-01-01 23:59'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(1, 1, '2016-01-02 00:00' , '2016-01-02 09:00'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(4, 1, '2016-01-02 09:05' , '2016-01-02 09:30'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(2, 1, '2016-01-02 09:30' , '2016-01-02 10:00'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(3, 1, '2016-01-02 10:03' , '2016-01-02 13:55'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(4, 1, '2016-01-02 13:55' , '2016-01-02 14:45'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(3, 1, '2016-01-02 15:00' , '2016-01-02 19:00'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(2, 1, '2016-01-02 19:03' , '2016-01-02 19:40'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(4, 1, '2016-01-02 19:45' , '2016-01-0 20:00'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(1, 1, '2016-01-02 20:00' , '2016-01-02 23:59'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(1, 1, '2016-01-03 00:00' , '2016-01-03 09:00'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(4, 1, '2016-01-03 09:05' , '2016-01-03 09:30'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(2, 1, '2016-01-03 09:30' , '2016-01-03 10:00'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(3, 1, '2016-01-03 10:03' , '2016-01-03 13:55'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(4, 1, '2016-01-03 13:55' , '2016-01-03 14:45'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(3, 1, '2016-01-03 15:00' , '2016-01-03 19:00'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(2, 1, '2016-01-03 19:03' , '2016-01-03 19:40'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(4, 1, '2016-01-03 19:45' , '2016-01-03 20:00'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(1, 1, '2016-01-03 20:00' , '2016-01-03 23:59'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(1, 1, '2016-01-04 00:00' , '2016-01-04 09:00'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(4, 1, '2016-01-04 09:05' , '2016-01-04 09:30'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(2, 1, '2016-01-04 09:30' , '2016-01-04 10:00'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(3, 1, '2016-01-04 10:03' , '2016-01-04 13:55'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(4, 1, '2016-01-04 13:55' , '2016-01-04 14:45'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(3, 1, '2016-01-04 15:00' , '2016-01-04 19:00'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(2, 1, '2016-01-04 19:03' , '2016-01-04 19:40'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(4, 1, '2016-01-04 19:45' , '2016-01-04 20:00'); ");
            db.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(1, 1, '2016-01-04 20:00' , '2016-01-04 23:59'); ");

            //db.close();

            //Create BloodSugar Table
            db.execSQL(MEASUREMENT_CREATE_TABLE);
            Log.d("Database", "Measurement Table Created");

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

    public void InsertLocation(DataBaseHandler handler, double lat, double longt, String title) {
        SQLiteDatabase db1 = handler.getWritableDatabase();
        db1.execSQL("insert into Location(Latitude, Longtitude, Title) values(" + lat + "," + longt + "," + "'" + title + "'" + "); ");
        db1.close();
    }

    public void InsertCurrentLocation(DataBaseHandler handler, double lat, double longt, Date time) {
        SQLiteDatabase db1 = handler.getWritableDatabase();
        db1.execSQL("insert into Location(Latitude, Longtitude, Title) values(" + lat + "," + longt + "," + "'" + Util.dateToDateTimeString(time) + "'" + "); ");
        db1.close();
    }

/*
    public static final String TAG = ActivityItem.class.getSimpleName();

    private int activityId;
    private int subactivityId;
    private Date starttime;
    private Date endtime;
    private String meal;
    private String imagePath;
    private Date date;
    private Integer intensity;
    */

    //(id INTEGER PRIMARY KEY, id_Activity Integer, id_Location Integer, id_SubActivity Integer, Start DateTime, End DateTime, Meal String, ImagePath String, Intensity Integer

    public void InsertActivity(DataBaseHandler handler, ActivityItem Activ) {
        String ImagePath = Activ.getImagePath();
        int idActivity = Activ.getActivityId();
        int idLocation =1;
        String idSubActivity = String.valueOf(Activ.getSubactivityId());
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
        db1.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End, Meal, ImagePath, Intensity) values(" + idActivity + "," + idLocation + " , '" + Start + "','" + End + "','" + Meal + "','" + ImagePath + "'," + Intensity + "); ");
        db1.close();
    }

    public void DeleteActivity(DataBaseHandler handler, ActivityItem Activ) {
        String Start = Activ.getStarttimeAsString();
        String End = Activ.getEndtimeAsString();
        SQLiteDatabase db1 = handler.getWritableDatabase();
        db1.execSQL("delete from ActivityList where Start = '" + Start + "' and End = '" + End + "';");
        db1.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(15,1,'"+Start+"','"+End+"')");
        db1.close();
    }

    public void ReplaceActivity(DataBaseHandler handler,  ActivityItem Activ){
        int idActivity = Activ.getActivityId();
        int idLocation =1;
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
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public void InsertActivity(DataBaseHandler handler, int idActivity, int idLocation, String Start, String End) {
        SQLiteDatabase db1 = handler.getWritableDatabase();
        db1.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(" + idActivity + "," + idLocation + " , '" + Start + "','" + End + "' ); ");
        db1.close();
    }

    public void DeleteActivity(DataBaseHandler handler, String Start, String End) {
        SQLiteDatabase db1 = handler.getWritableDatabase();
        db1.execSQL("delete from ActivityList where Start = '" + Start + "' and End = '" + End + "';");
        db1.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(15,1,'"+Start+"','"+End+"')");
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
    public void InsertBloodsugar(DataBaseHandler handler, int profile_id, double bloodsugar_level, String measure_unit) {
        SQLiteDatabase db1 = handler.getWritableDatabase();
        long tslong = System.currentTimeMillis() / 1000;
        Log.d("Database","tslong InsertBloodSugar"+tslong);
        db1.execSQL("insert into " + MEASUREMENT_TABLE_NAME + "(profile_ID, measure_value, timestamp, measure_unit, measure_kind) values(" + profile_id + ","
                + bloodsugar_level + " , '" + tslong + "' , '" +measure_unit+"' , 'bloodsugar');");
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

    /*
    Naira: Inser ts a new measurements input
     */
    public void InsertMeasurements(DataBaseHandler handler, int bloodsugar_level, int insulin_dosage, String timestamp) {
        SQLiteDatabase db1 = handler.getWritableDatabase();
        //long tslong = System.currentTimeMillis() / 1000;
        db1.execSQL("insert into History_Bloodsugar(bloodsugar_level,insulin_dosage, timestamp) values("  + bloodsugar_level + " , " + insulin_dosage + " , '"+ timestamp + "' ); ");
        db1.close();
    }

    public Cursor GetMeasurements(DataBaseHandler handler){
        SQLiteDatabase db1 = handler.getWritableDatabase();
        Cursor cursor = db1.rawQuery("select * from History_Bloodsugar", null);
        return cursor;
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
    /**
     * @author Ivo Gosemann 18.03.2016
     * Method to retrieve all Insulin Values
     * //TODO: Combine with Bloodsugar and Timestamp Methods
     * @param handler
     * @return ArrayList containing the Integer Values of all the Insulin Values of one day
     */
    public ArrayList<Integer> getAllInsulin(DataBaseHandler handler, Date date, String window) {
        SQLiteDatabase db = handler.getReadableDatabase();
        Long[] windowStartEnd = Util.convertDateStringToTimestamp(getWindowStartEnd(date,window));
        Log.d("Database","timestamps of retrieval"+windowStartEnd[0]+" "+windowStartEnd[1]);
        Cursor cursor = db.rawQuery("select measure_value from  " + MEASUREMENT_TABLE_NAME + " " +
                "where timestamp>='"+windowStartEnd[0]+"' and timestamp <'"+windowStartEnd[1]+"'" +
                "AND measure_kind = 'insulin';",null);
        ArrayList<Integer> insulinValues = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                insulinValues.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return insulinValues;
    }

    /**
     * @author Ivo Gosemann 18.03.2016
     * Method to retrieve all Bloodsugar Values
     * //TODO: Combine with Insulin and Timestamp Methods
     * @param handler
     * @return ArrayList containing all the Integer Values fo the Bloodsugar of one day
     */
    public ArrayList<Integer> getAllBloodSugar(DataBaseHandler handler, Date date, String window){
        SQLiteDatabase db = handler.getReadableDatabase();
        Long[] windowStartEnd = Util.convertDateStringToTimestamp(getWindowStartEnd(date,window));
        Cursor cursor = db.rawQuery("select measure_value from  " + MEASUREMENT_TABLE_NAME + " "  +
                "where timestamp>='"+windowStartEnd[0]+"' and timestamp <'"+windowStartEnd[1]+"'" +
                "AND measure_kind = 'bloodsugar';",null);
        ArrayList<Integer> bloodsugarValues = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                bloodsugarValues.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return bloodsugarValues;
    }
    /**
     * @author Ivo Gosemann 18.03.2016
     * Method to retrieve all Timestamps for Measurements
     * //TODO: Combine with Insulin and Bloodsugar Method
     * @param handler
     * @return ArrayList containing all the timestamp values for one day
     */
    public ArrayList<String> getAllTimestamps(DataBaseHandler handler,Date date,String window){
        SQLiteDatabase db = handler.getReadableDatabase();
        Long[] timeWindow = Util.convertDateStringToTimestamp(getWindowStartEnd(date,window));
        Cursor cursor = db.rawQuery("select timestamp from  " + MEASUREMENT_TABLE_NAME + " " +
                "where timestamp>='"+timeWindow[0]+"' and timestamp <'"+timeWindow[1]+"';",null);
        ArrayList<String> timestampList = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                timestampList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return timestampList;
    }

    /**
     * @author Ivo Gosemann 08.04.2016
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
        String S = "select Activities.id, ActivityList.Start, ActivityList.End from ActivityList inner join Activities on ActivityList.id_Activity = Activities.id where (ActivityList.End >= '" + StartOfDay + "' and ActivityList.Start < '" + EndOfDay + "') or (ActivityList.Start < '" + EndOfDay + "' and ActivityList.Start >= '" + StartOfDay+ "');";
        Cursor cursor = db.rawQuery("select Activities.id, ActivityList.Start, ActivityList.End from ActivityList inner join Activities on ActivityList.id_Activity = Activities.id where ActivityList.End >= '" + StartOfDay + "' and ActivityList.Start < '" + EndOfDay + "' or ActivityList.Start < '" + EndOfDay + "' and ActivityList.Start >= '" + StartOfDay+ "' order by ActivityList.Start;", null);

        ArrayList<ActivityItem> activityList = GetArrayFromCursor(cursor, date);
        return activityList;
    }
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

    /***
     * returns the ID of the current user
     * @param handler
     * @param name
     * @param surename
     * @return UserID
     */
    public String GetProfileId(DataBaseHandler handler,String name, String surename)
    {
        SQLiteDatabase db1 = handler.getWritableDatabase();
        Cursor cursor = db.rawQuery("select id from "+ PROFILE_TABLE_NAME+ ";",null);
        db1.close();
        return cursor.getString(1);
    }

    public void ReplaceActivity(DataBaseHandler handler, int idActivity, int idLocation, String Start, String End){

        findActionbyStartEndTime(handler, Start, End);
        findActionbyStartEndTime2(handler, Start, End);
        findActionbyStartTime(handler, Start);
        findActionbyEndTime(handler, End);

        SQLiteDatabase db1 = handler.getWritableDatabase();
        InsertActivity(handler, idActivity, idLocation, Start, End);
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
                int idActivity = cursor.getInt(1);
                int idLocation = cursor.getInt(2);
                String Start1 = cursor.getString(3);
                String End1 = cursor.getString(4);
                db1.execSQL("update ActivityList set End = '" + MinusMinute(Start) + "' where id = '" + cursor.getString(0) + "';");
                InsertActivity(handler, idActivity, idLocation, End, End1);
            } while (cursor.moveToNext());
            db1.close();
        }
    }

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
            db1.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(" + idActivity + "," + idLocation + " , '" + Start + "','" + End + "' ); ");
        }
        db1.close();
    }


    public void InsertProfile(DataBaseHandler handler, int age, int dt, double abl) {
        SQLiteDatabase db1 = handler.getWritableDatabase();
        db1.execSQL("insert into Profile(age, diabetes_type, average_bloodsugar_level) values(" + age + "," + dt + "," + "'" + abl + "'" + "); ");
        db1.close();
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
        String S = "select Activities.id, ActivityList.Start, ActivityList.End from ActivityList inner join Activities on ActivityList.id_Activity = Activities.id where (ActivityList.End >= '" + StartOfDay + "' and ActivityList.Start < '" + EndOfDay + "') or (ActivityList.Start < '" + EndOfDay + "' and ActivityList.Start >= '" + StartOfDay+ "');";
        Cursor cursor = db.rawQuery("select Activities.id, ActivityList.Start, ActivityList.End from ActivityList inner join Activities on ActivityList.id_Activity = Activities.id where ActivityList.End >= '" + StartOfDay + "' and ActivityList.Start < '" + EndOfDay + "' or ActivityList.Start < '" + EndOfDay + "' and ActivityList.Start >= '" + StartOfDay+ "' order by ActivityList.Start;", null);
                                                                                                                                                                                    //(ActivityList.End > '2016-01-01 00:00' and ActivityList.Start < '2016-01-01 23:59') or (ActivityList.Start < '2016-01-01 23:59' and ActivityList.Start > '2016-01-01 00:00')
        return GetArrayFromCursor(cursor, Date);
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
            Date1 = Util.addMinuteFromDate(Date1, 1);
            SDate1 = Util.dateToDateTimeString(Date1);

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
            Date1 = Util.addMinuteFromDate(Date1, -1);
            SDate1 = Util.dateToDateTimeString(Date1);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return SDate1;
    }


}
    /*
    if (cursor.moveToFirst()) {
        do {
            String ActionId = cursor.getString(0);
            String Start = cursor.getString(1);
            String End = cursor.getString(2);
        }
        while (cursor1.moveToNext());
    }
    // close cursor
    if (!cursor.isClosed()) {
        cursor.close();
    }
    */
