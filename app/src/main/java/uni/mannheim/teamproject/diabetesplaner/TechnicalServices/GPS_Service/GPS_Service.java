package uni.mannheim.teamproject.diabetesplaner.TechnicalServices.GPS_Service;

/**
 * Created by Jens on 11.04.2016.
 */


import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;


public class GPS_Service extends Service implements LocationListener {
    double lat;
    double lng;
    private LocationManager locationManager;
    private String provider;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        System.out.println("Beginn Service");

        LocationManager service = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default

        Criteria criteria = new Criteria();

        final Criteria locationCriteria = new Criteria();
        locationCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        locationCriteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(locationCriteria, true);

        if (locationManager.PASSIVE_PROVIDER.equalsIgnoreCase(provider)){
            provider = locationManager.GPS_PROVIDER;
        }

        Toast.makeText(getApplicationContext(), provider, Toast.LENGTH_SHORT).show();

        String permission = "android.permission.INTERNET";
        int res = checkCallingOrSelfPermission(permission);

        Location location = locationManager.getLastKnownLocation(provider);

        //first number = time in milliseconds (5 minutes) 300000
        //second number = distance in meters (100 meters) 100
        locationManager.requestLocationUpdates(provider, 10000, 1, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 1, this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service destroyed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location current_location) {
        lat = (double) (current_location.getLatitude());
        lng = (double) (current_location.getLongitude());

        Date current_date = new Date();
        AppGlobal.getHandler().insertLocation(AppGlobal.getHandler(),lat, lng, TimeUtils.dateToDateTimeString(current_date));

        //delete toast if it works
        //Toast.makeText(getApplicationContext(), lat + " : " + lng, Toast.LENGTH_SHORT).show();

        makePrediction(current_location);
        
    }

    public void makePrediction(Location current_location){
        System.out.println("MyTest1: in make Prediction");
        //get all Locations
        ArrayList<MyLocation> locationList = new ArrayList<MyLocation>();
        ArrayList<MyLocation> near_locationList = new ArrayList<MyLocation>();
        Cursor cursor = AppGlobal.getHandler().getAllLocations(AppGlobal.getHandler());

        if (cursor.moveToFirst()) {
            do {
                MyLocation location = new MyLocation(cursor.getDouble(1), cursor.getDouble(2), cursor.getString(3));
                locationList.add(location);
            }
            while (cursor.moveToNext());
        }

        for (int i = 0; i<locationList.size();i++){
            System.out.println("my test3: "+ locationList.get(i).time);
        }

        //look for locations that are near to the current location
        for (int i = 0; i<locationList.size();i++){
            Location x_Location = current_location; //only that is not null
            x_Location.setLatitude(locationList.get(i).lat);
            x_Location.setLongitude(locationList.get(i).lng);

            //300 meter treshold
            if (current_location.distanceTo(x_Location) > 300){
                near_locationList.add(locationList.get(i));
            }
        }

        //get all Activities
        ArrayList<String[]> eventlist  = AppGlobal.getHandler().getAllEvents(AppGlobal.getHandler());


        // Todo: für alle activities
       //--------------------------------------
        String[] x = eventlist.get(1);
        //id, name, startdate, enddate
        System.out.println("MyTest: " + x[0] + "  " + x[1] + "  " + x[2] + "  " + x[3]);
        System.out.println("MyTest: " + eventlist.size());
        //Daten zum Vergleich konvertieren

        try {
            Date time_von = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(x[2]);
            Date time_bis = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(x[3]);
            System.out.println("My test 3: " + time_von + " bis " + time_bis);
        } catch (ParseException e) {
            //Handle exception here, most of the time you will just log it.
            e.printStackTrace();
        }


        //schauen ob location timestamp innerhalb einer activity ist
        //--------------------------------------



        //compare timestamp from activities and near locations

        //get activites from near locations

        //list all activites by frequency

        //take the top 3

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

}
