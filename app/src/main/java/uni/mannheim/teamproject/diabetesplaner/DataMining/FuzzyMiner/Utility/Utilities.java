package uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.Utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ivo on 4/20/2016.
 */
public class Utilities {

    public static String getActivityNameByID(int id){
        return "";
    }

    /**
     *
     * @param timestamp long timestamp of milliseconds
     * @return String concatenated of Int(Day of Year) and Int(Year)
     * For Example 01.01.2000 === 12000
     */
    public static String getUniqueDateString(long timestamp){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        date.setTime(timestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        String uniqueDate = calendar.get(Calendar.DAY_OF_YEAR)+""+calendar.get(Calendar.YEAR);
        return uniqueDate;

    }
}
