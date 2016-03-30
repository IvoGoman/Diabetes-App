package uni.mannheim.teamproject.diabetesplaner.DailyRoutine;

import uni.mannheim.teamproject.diabetesplaner.Backend.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.R;

/**
 * Created by Stefan on 30.03.2016.
 */
public class ColorUtils {
    /**
     * @author Stefan
     * returns the color with respect to the intensity of an acitiviy
     * returns default color if intensity == null
     * @param intensity
     * @return
     */
    public static int getIntensityColor(Integer intensity){
        if(intensity != null){
            switch (intensity){
                case 0: return R.color.good;
                case 1: return R.color.potential_bad;
                case 2: return R.color.bad;
            }
        }
        return R.color.no_influence;
    }

    /**
     * @author Stefan
     * returns the color for an activity
     *
     * @param activityid id of activity
     * @return id of color in resources
     */
    public static int getColor(int activityid, ActivityItem activityItem) {
        switch (activityid) {
            case 1:
                return R.color.good;
            case 2:
                return R.color.bad;
            case 3:
                return R.color.no_influence;
            case 4:
                return R.color.potential_bad;
            case 5:
                return R.color.good;
            case 6:
                return R.color.good;
            case 7:
                return R.color.no_influence;
            case 8:
                return R.color.no_influence;
            case 9:
                return R.color.no_influence;
            case 10:
                return R.color.good;
            case 11:
                return R.color.good;
            case 12:
                getIntensityColor(activityItem.getIntensity());
            case 13:
                getIntensityColor(activityItem.getIntensity());
            default:
                return R.color.unknown;
        }
    }
}
