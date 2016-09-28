package uni.mannheim.teamproject.diabetesplaner.Domain;

/**
 * Created by Naira Ibrahim on 09.09.2016.
 */
public class myWifi {
    private String ssid;
    private String time;
    /**
     * Wifi Constructior
     *
     * @param ssid
     * @param time
     * @author Naira
     */
    public myWifi(String ssid, String time) {
        this.ssid = ssid;
        this.time = time;
    }

    public String getSSID(){
        return this.ssid;
    }

    public String getTime(){
        return this.time;
    }
}
