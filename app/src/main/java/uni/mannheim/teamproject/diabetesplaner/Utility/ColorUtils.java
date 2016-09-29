package uni.mannheim.teamproject.diabetesplaner.Utility;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.R;

/**
 * Created by Stefan on 30.03.2016.
 */
public class ColorUtils {
    /**
     * returns the color with respect to the intensity of an acitiviy
     * returns default color if intensity == null
     * @param intensity
     * @return
     * @author Stefan 28.09.2016
     */
    public static int getIntensityColor(Integer intensity, int color){

        ColorDrawable cd = new ColorDrawable(color);
        int col = cd.getColor();
        int alpha = cd.getAlpha();
        int red = Color.red(col);
        int green = Color.green(col);
        int blue = Color.blue(col);

        switch (intensity){
            case 1: return Color.argb(alpha, red - 20, green - 20, blue - 20);
            case 2: return Color.argb(alpha, red - 40, green - 40, blue - 40);
            default: return color;
        }

    }

    /**
     * @author Stefan
     * returns the color for an activity
     *
     * @param activityItem
     * @return id of color in resources
     */
    public static int getColor(ActivityItem activityItem, Context context) {
        int activityId = activityItem.getActivityId();
        DataBaseHandler dbHandler = AppGlobal.getHandler();

        int superId = dbHandler.getSuperActivityID(activityItem.getActivityId());

        if(dbHandler.getGermanActivityName(activityId).equals("Arbeiten") && activityItem.getIntensity() !=null){
            return getIntensityColor(activityItem.getIntensity(), getColorBySuperActivity(superId, context));
        }else if(dbHandler.getGermanActivityName(activityId).equals("Sport") && activityItem.getIntensity() != null){
            return getIntensityColor(activityItem.getIntensity(), getColorBySuperActivity(superId, context));
        }

        return getColorBySuperActivity(superId, context);
    }

    /**
     * returns the color by the super activity id
     * @param superId
     * @param context
     * @return
     * @author Stefan 28.09.2016
     */
    public static int getColorBySuperActivity(int superId, Context context){
        switch (superId) {
            case 1:
                return ContextCompat.getColor(context, R.color.color1);
            case 2:
                return ContextCompat.getColor(context, R.color.color2);
            case 3:
                return ContextCompat.getColor(context, R.color.color3);
            case 4:
                return ContextCompat.getColor(context, R.color.color4);
            case 5:
                return ContextCompat.getColor(context, R.color.color5);
            case 6:
                return ContextCompat.getColor(context, R.color.color6);
            default:
                return ContextCompat.getColor(context, R.color.color6);
        }
    }
}
