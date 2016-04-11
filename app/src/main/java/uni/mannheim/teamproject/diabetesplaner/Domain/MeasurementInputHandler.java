package uni.mannheim.teamproject.diabetesplaner.Domain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Utility.Util;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;

/**
 * Created by Naira
 */
public class MeasurementInputHandler {

    public static void loadIntoDatabase(String Measurement, DataBaseHandler helper) {
        readMeasurement(Measurement, helper);

    }


    private static void readMeasurement(String Measurement, DataBaseHandler helper) {
        ArrayList<String[]> list = Util.read(Measurement);

        //header that contains all attributes
        String[] header = list.get(0);

        //iterate over all lines of the CSV file
        for (int i = 1; i < list.size(); i++) {
            int IdMeasurement = Integer.valueOf(list.get(i)[1]);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            java.util.Date StartD = new Date();
            java.util.Date EndD = new Date();
            StartD.setTime(Long.valueOf(list.get(i)[3]));
            EndD.setTime(Long.valueOf(list.get(i)[4]));
            String Start = format.format(StartD);
            String End = format.format(EndD);
            AppGlobal.getHandler().InsertActivity(AppGlobal.getHandler(), IdMeasurement, 1, Start, End);
        }
    }

}



