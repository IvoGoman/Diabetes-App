package uni.mannheim.teamproject.diabetesplaner.Utility;

import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
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
                return R.color.color1;
            case 2:
                return R.color.color2;
            case 3:
                return R.color.color6;
            case 4:
                return R.color.color6;
            case 5:
                return R.color.color1;
            case 6:
                return R.color.color4;
            case 7:
                return R.color.color3;
            case 8:
                return R.color.color4;
            case 9:
                return R.color.color4;
            case 10:
                return R.color.color6;
            case 11:
                return R.color.color6;
            case 12:
                return R.color.color4;
            case 13:
                return R.color.color5;
            case 14:
                return R.color.color4;
            case 15:
                return R.color.color5;
            case 16:
                return R.color.color6;
            default:
                return R.color.color1;
        }
    }
}
