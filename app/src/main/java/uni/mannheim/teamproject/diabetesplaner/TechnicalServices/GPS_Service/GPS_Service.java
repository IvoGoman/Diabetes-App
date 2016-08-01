package uni.mannheim.teamproject.diabetesplaner.TechnicalServices.GPS_Service;

/**
 * Created by Jens on 11.04.2016.
 */


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;


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

        Toast.makeText(this, "Service created", Toast.LENGTH_LONG).show();

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


        //first number = time in milliseconds (5 minutes)
        //second number = distance in meters (100 meters)
        locationManager.requestLocationUpdates(provider, 300000, 100, this);



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service destroyed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = (double) (location.getLatitude());
        lng = (double) (location.getLongitude());
        AppGlobal.getHandler().insertLocation(AppGlobal.getHandler(),lat, lng, "" );

        //ic_delete toast if it works
        Toast.makeText(getApplicationContext(), lat + " : " + lng, Toast.LENGTH_SHORT).show();
        
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
