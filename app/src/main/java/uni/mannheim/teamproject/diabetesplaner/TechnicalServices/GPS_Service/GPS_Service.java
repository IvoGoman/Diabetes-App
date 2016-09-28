package uni.mannheim.teamproject.diabetesplaner.TechnicalServices.GPS_Service;

/**
 * Created by Jens on 11.04.2016.
 */


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;


public class GPS_Service extends Service implements LocationListener {
    double lat;
    double lng;
    private LocationManager locationManager;
    private String provider;
    Location x_Location = new Location("dummyprovider");
    Date time_from;
    Date time_bis;
    Date time_loc;

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

        //Toast.makeText(getApplicationContext(), provider, Toast.LENGTH_SHORT).show();

        String permission = "android.permission.INTERNET";
        int res = checkCallingOrSelfPermission(permission);

        Location location = locationManager.getLastKnownLocation(provider);

        //first number = time in milliseconds (5 minutes) 300000
        //second number = distance in meters (100 meters) 100
        locationManager.requestLocationUpdates(provider, 180000, 50, this);
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 1, this);

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
        AppGlobal.getHandler().insertLocation(lat, lng, TimeUtils.dateToDateTimeString(current_date));


        System.out.println("Activities that never occure in that location: " + makePrediction(current_location));
        
    }

    public ArrayList<String> makePrediction(Location current_location){

        System.out.println("MyTest1: in make Prediction");

        //get all Locations
        ArrayList<MyLocation> locationList = new ArrayList<MyLocation>();
        ArrayList<MyLocation> near_locationList = new ArrayList<MyLocation>();
        ArrayList<String> relevant_Activities = new ArrayList<String>();
        ArrayList<String> AllActivityNames = new ArrayList<String>();
        ArrayList<String> UnusualActivities = new ArrayList<String>();

        AllActivityNames = AppGlobal.getHandler().getAllActivityNames();
        AllActivityNames.addAll(AppGlobal.getHandler().getAllSubactivities());




        Cursor cursor = AppGlobal.getHandler().getAllLocations();

        if (cursor.moveToFirst()) {
            do {
                MyLocation location = new MyLocation(cursor.getDouble(1), cursor.getDouble(2), cursor.getString(3));
                locationList.add(location);
            }
            while (cursor.moveToNext());
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }

        //zeigt alle gespeicherten locations an == locationList
        //for (int i = 0; i<locationList.size();i++){
        //    System.out.println("Test4: "+ locationList.get(i).time + " info: " + locationList.get(i).lat + " / " + locationList.get(i).lng);
        //}

        System.out.println("Test4 , Anzahl Locations: " + locationList.size());

        //look for locations that are near to the current location
        for (int i = 0; i<locationList.size()-1;i++){ //-1 das letzte gespeicherte Location nicht mitgezählt wird

            x_Location.setLatitude(locationList.get(i).lat);
            x_Location.setLongitude(locationList.get(i).lng);

           // System.out.println("Test4: CurrentLocation : " + current_location);
           // System.out.println("Test4: x_Location : " + x_Location);
           // System.out.println("Test4 , Distance: " + current_location.distanceTo(x_Location));

            //300 meter treshold
            if (current_location.distanceTo(x_Location) < 200){
                near_locationList.add(locationList.get(i));
            }
        }

       System.out.println("Test4 , Anzahl nearLocations: " + near_locationList.size());

        //get all Activities
        ArrayList<String[]> eventlist  = AppGlobal.getHandler().getAllEvents();
       System.out.println("Test4 , Anzahl Activities: " + eventlist.size());

        // für alle activities
       //--------------------------------------

        for(int i = 0; i < eventlist.size(); i++){ //geh durch alle activities

            //activity laden

            String[] x = eventlist.get(i);
            //id, name, startdate, enddate
           System.out.println("MyTest4 Activity " + i + " : " + x[0] + "  " + x[1] + "  " + x[2] + "  " + x[3]);

            //Daten zum Vergleich konvertieren

            try {
                time_from = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(x[2]);
                time_bis = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(x[3]);
               // System.out.println("MyTest4 Activity " + i + " : " + time_from + " bis " + time_bis);
            } catch (ParseException e) {
                //Handle exception here, most of the time you will just log it.
                e.printStackTrace();
            }

            //schauen ob location timestamp innerhalb einer activity ist
            //for schleife für alle near locations pro einzelne activity

            for(int q = 0; q < near_locationList.size(); q++){
                //timestamp location zwischen start und end zeit von activity dann mögliche activity für diese location
                try {
                    time_loc = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(near_locationList.get(q).time);

                } catch (ParseException e) {
                    //Handle exception here, most of the time you will just log it.
                    e.printStackTrace();
                }

                if (TimeUtils.isTimeInbetween(time_from, time_bis, time_loc) == true ) {

                    if (relevant_Activities.contains(x[1]) == false) {
                        relevant_Activities.add(relevant_Activities.size(), x[1]);
                    }
                    //Liste mit Aktivitäten die in der location vorgekommen sind

                } else {
                    //System.out.println("keine relevante Activity für aktuelle Location: " + x[1]);
                }
            }
        } //for schleife für alle activities

        //remove from all activities those that occures at a specific location to get a list with those that never occures at this location
        AllActivityNames.removeAll(relevant_Activities);
        UnusualActivities = AllActivityNames;
        /**
         * edited by leonidgunko
         */
        try {
            AppGlobal.gpsUnusualActivities.clear();
            for (String ActivityName : UnusualActivities) {
                AppGlobal.gpsUnusualActivities.add(AppGlobal.getHandler().getSubactivityID(ActivityName));
            }
        }
        catch(Exception e){}
        return UnusualActivities;
    }//end of makePrediction

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
