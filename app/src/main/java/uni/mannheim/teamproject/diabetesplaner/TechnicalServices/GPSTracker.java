package uni.mannheim.teamproject.diabetesplaner.TechnicalServices;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.UI.PopUps;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;


/** GPS tracker class that is used to provide a GPS tracking service for our android application.
 *  Providing the location constantly on the request of the user.

 if gps is off - we offer to switch it on

 onLocationChanged checkes if current location is already in database, if not - after 20 minutes without changing place the user
 is asked to save his current location in database

 *
 */
public class GPSTracker extends Service implements LocationListener {

    private final Context mContext;

    private DataBaseHandler Helper1;

    Location LastLocation;

    int SimilarLocationCounter = 0;

    // flag for GPS status if its enabled or not to be used if not as an alert message
    boolean isGPSEnabled = false;

    // flag for network status if available or not
    boolean isNetworkEnabled = false;

    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters (can be changed as we please)
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 50 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 60 * 1000 ; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    /**this is the class constructor for the GPS tracker which creates the GPS tracking object
     */
    public GPSTracker(Context context, DataBaseHandler helper) {
        this.mContext = context;
        getLocation();
        this.Helper1 = helper;
        /*Toast toast = Toast.makeText(mContext,
                "new tracker", Toast.LENGTH_SHORT);
        toast.show();*/
    }

    /** method that returns the geolocation of the android phone during the moment its called.
     *  The location object returned has longitudes and latitudes with a timestamp which are the attributes
     *  of the object returned.
     *  */
    public Location getLocation() {

        try {
                locationManager = (LocationManager) mContext
                        .getSystemService(LOCATION_SERVICE);
            if (locationManager != null)
            if ( !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) //if GPS is off - we offer to switchit on
            {
                showSettingsAlert();
            }
                    if (locationManager != null) {
                        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);}
                    }
                if (locationManager != null) {
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            120000,
                            100, this);
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);}
                }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /** method that returns the Latitude of the location
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /** method that returns the Longitude of the location
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }


    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.ic_delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    /** method that stops the GPS listener in the application when needed
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
                locationManager.removeUpdates(GPSTracker.this);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub

        int Location_ID = LocationLogic.similarLocations(Helper1, location.getLatitude(), location.getLongitude());
        if ( Location_ID < 0 )
        {
            if (AppGlobal.getLastLocation() != null) {
                if (LocationLogic.distance(AppGlobal.getLastLocation().getLatitude(), AppGlobal.getLastLocation().getLongitude(), location.getLatitude(), location.getLongitude())<0.1)
                {
                    long a = location.getTime() - AppGlobal.getLastLocation().getTime();
                    AppGlobal.setTime(AppGlobal.getTime() + a);

                    if (AppGlobal.getTime() >= 20* 60*1000) {        //20 minutes
                        PopUps.addLocation(mContext, Helper1, location);
                        AppGlobal.setTime(0);
                    }
                    long b = AppGlobal.getTime();
                }
                else {
                    AppGlobal.setTime(0);
                }
            }
        }
        AppGlobal.setLastLocation(location);
    }

    public int getLocation(DataBaseHandler helper){
        return LocationLogic.similarLocations(Helper1, location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);}
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    120000,
                    100, this);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);}
    }


    @Override
    public void onProviderDisabled(String provider) {

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showSettingsAlert();
        }
        getLocation();
    }

}

