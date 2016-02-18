package uni.mannheim.teamproject.diabetesplaner.Backend;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by leonidgunko on 31.10.15.
 */
public class DataBaseHandler extends SQLiteOpenHelper {

    SQLiteDatabase db;

    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        Log.d("Database","MySQLiteHelper Constructor Started");
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
    private static final String LOCATION_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " +
                    LOCATION_TABLE_NAME +
                    " (id INTEGER PRIMARY KEY, Latitude double, Longtitude double, Title VARCHAR(20));";
    public static final String LOCATION_SELECT =
            "SELECT * FROM " + LOCATION_TABLE_NAME + ";";
    public static final String LOCATION_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + LOCATION_TABLE_NAME + ";";

    //ActivityList Table
    private static final String ACTIVITYLIST_TABLE_NAME = "ActivityList";
    private static final String ACTIVITYLIST_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " +
                    ACTIVITYLIST_TABLE_NAME +
                    " (id INTEGER PRIMARY KEY, id_Activity Integer, id_Location Integer, Start DateTime, End DateTime, FOREIGN KEY(id_Activity) REFERENCES Activities(id), FOREIGN KEY(id_Location) REFERENCES Locations(id) );";
    public static final String ACTIVITYLIST_SELECT =
            "SELECT * FROM " + ACTIVITYLIST_TABLE_NAME + ";";
    public static final String ACTIVITYLIST_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + ACTIVITYLIST_TABLE_NAME + ";";

    //Profile Table
    private static final String PROFILE_TABLE_NAME = "Profile";
    private static final String PROFILE_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " +
                    PROFILE_TABLE_NAME +
                    " (id INTEGER PRIMARY KEY, age INTEGER, diabetes_type INTEGER, average_bloodsugar_level double);";
    public static final String PROFILE_SELECT =
            "SELECT * FROM " + PROFILE_TABLE_NAME + ";";
    public static final String PROFILE_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + PROFILE_TABLE_NAME + ";";

    public static final String BLOODSUGAR_CREATE_TABLE = "";


    //METHODS

    public Cursor getAllLocations (DataBaseHandler helper){
        SQLiteDatabase db = helper.getWritableDatabase();

        //Create a Cursor that contains all records from the locations table
        Cursor cursor = db.rawQuery("select * from " + LOCATION_TABLE_NAME, null);
        return cursor;
    }



    @Override
    public void onCreate(SQLiteDatabase db) {   //when the App is first installed

        // Create Activity Table
        db.execSQL(ACTIVITIES_CREATE_TABLE);
        Log.d("Database", "Temp Activity Table Created");
        //db.execSQL("insert into Activities(Title) values('walking'); ");

        // Create SubActivities Table
        db.execSQL(SUB_ACTIVITIES_CREATE_TABLE);
        Log.d("Database", "Sub Activities Table Created");

        // Create Location Table
        db.execSQL(LOCATION_CREATE_TABLE);
        Log.d("Database", "Location Table Created");
        db.execSQL("insert into Location(Latitude, Longtitude, Title) values (-1,-1,'Other'); ");   //if the location is unknown

        //Create ActivityList Table
        db.execSQL(ACTIVITYLIST_CREATE_TABLE);
        Log.d("Database", "Routine Table Created");

        //Create Profile Table
        db.execSQL(PROFILE_CREATE_TABLE);
        Log.d("Database", "Profile Table Created");

        //Create BloodSugar Table
        //db.execSQL(BLOODSUGAR_CREATE_TABLE);
        //Log.d("Location db", "Bloodsugar Table Created");
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

    public void InsertLocation (DataBaseHandler handler ,double lat, double longt, String title){
        SQLiteDatabase db1 = handler.getWritableDatabase();
        db1.execSQL("insert into Location(Latitude, Longtitude, Title) values(" + lat + "," + longt + "," + "'" + title + "'" + "); ");
        db1.close();
    }

    public void InsertActivity (DataBaseHandler handler ,int idActivity, int idLocation, String Start, String End){
        SQLiteDatabase db1 = handler.getWritableDatabase();
        db1.execSQL("insert into ActivityList(id_Activity, id_Location, Start, End) values(" + idActivity + "," + idLocation + " , '"+ Start + "','" + End  +"' ); ");
        db1.close();
    }

    public void ReplaceActivity(DataBaseHandler handler ,int idActivity, int idLocation, String Start, String End){
    //under construction
    }

    public void InsertProfile (DataBaseHandler handler ,int age, int dt, double abl){
        SQLiteDatabase db1 = handler.getWritableDatabase();
        db1.execSQL("insert into Profile(age, diabetes_type, average_bloodsugar_level) values(" + age + "," + dt + "," + "'" + abl + "'" + "); ");
        db1.close();
    }

}
