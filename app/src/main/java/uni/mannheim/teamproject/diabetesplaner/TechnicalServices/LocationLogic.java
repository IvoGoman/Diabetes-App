package uni.mannheim.teamproject.diabetesplaner.TechnicalServices;

/**
 * Created by leonidgunko on 06.01.16.
 */
import android.database.Cursor;
import android.util.Log;

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;


public class LocationLogic {

/*
    public static String compareWithPredefLocations(DataBaseHandler helper, double long1, double lat1) {
        double long2, lat2, distance;
        double shortestDistance = 0.05;

        Cursor cursor = helper.getAllLocations(helper);

        // in a loop, compute distance of current locations and locations in the database
        if (cursor.moveToFirst()) {
            do {
                long2 = cursor.getDouble(1);
                lat2 = cursor.getDouble(2);
                distance = distance(long1, lat1, long2, lat2);

                // if distance < 50m, use predefined location label
                if ( distance < 0.05 && distance < shortestDistance) {
                    location = cursor.getString(3);
                    shortestDistance=distance;
                }
            } while (cursor.moveToNext());
        }if (!cursor.isClosed()) {
            cursor.close();
        }
        Log.d("LOCATION",location);
        return location;
    }
*/

    static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return dist;
    }

    static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


    public static CharSequence[] getPopularLocations () {


        CharSequence[] locas = new CharSequence[4];

        // default values
        locas[0] = "home";
        locas[1] = "work";
        locas[2] = "gym";
        locas[3] = "Other location";

        return locas;

    }

/*
    public static void askForLocation (String location) {
        Log.d("locationlogic", "enabled");

        // check if the last stored location was unknown
        // if yes, increase the noMatchLocationsCounter
        if (location == noMatchLocation)
            noMatchLocationsCounter++;
        else
            noMatchLocationsCounter = 0;

        Log.d("locationLogic count", Integer.toString(noMatchLocationsCounter));

        // check if the noMatchLocationsCounter reached the maximum amount
        // of unknown location entries which are stored in the database
        if (noMatchLocationsCounter >= 7) {

            // ask for location
            //PopUps.addLocation();

            // reset the counter
            noMatchLocationsCounter = 0;
        }

    }
*/

    public static boolean checkAndAddLocation (DataBaseHandler helper, String tittle, double lat, double lon) {
        boolean nameExists = false;

        // get names of stored locations
        // check if location already exists

        // download all stored location names first
        Cursor cursor = helper.getAllLocations();
        if (cursor.moveToFirst()) {
            do {
                Log.d("LOCATION entry", cursor.getString(0));
                if(cursor.getString(0).equals(tittle)) {
                    nameExists = true;
                    break;
                }
            } while (cursor.moveToNext());
        }
        // close cursor
        if (!cursor.isClosed()) {
            cursor.close();
        }

        if (!nameExists) {
            // add location to database
            helper.insertLocation(lat, lon, tittle);
            return true;
        }
        return false;
    }


    public static int similarLocations (DataBaseHandler helper, double askedLat, double askedLon) {
        double distance;
        double mindistance = 0.15;
        int minID = -1;
        int tempID;
        double tempLat, tempLon;
        // get timestamps of similar locations
        // download all locations first
        Cursor cursor = helper.getAllLocations();
        // in a loop, compute distance of current locations and locations in the database
        if (cursor.moveToFirst()) {
            do {
                //tempID = cursor.getInt(0);
                //tempLat = cursor.getInt(1);
                //tempLon = cursor.getInt(2);
                distance = distance(askedLat, askedLon, cursor.getDouble(1), cursor.getDouble(2));

                // if distance < 50m, the location is considered as the same
                if ((distance < 0.15) && (distance < mindistance)) {
                    mindistance = distance;
                    minID = cursor.getInt(0);
                }
            } while (cursor.moveToNext());
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return minID;
    }

}
