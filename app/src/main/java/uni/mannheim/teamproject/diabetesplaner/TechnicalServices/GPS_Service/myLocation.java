package uni.mannheim.teamproject.diabetesplaner.TechnicalServices.GPS_Service;

/**
 * Created by Jens on 19.07.2016.
 */
public class MyLocation {
    double lat;
    double lng;
    String time;

    public MyLocation(double lat, double lng, String time){
        this.lat = lat;
        this.lng = lng;
        this.time = time;
    }
}
