package uni.mannheim.teamproject.diabetesplaner.Domain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import uni.mannheim.teamproject.diabetesplaner.Utility.Util;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;

/**
 * Created by Stefan on 19.02.2016.
 * Class for handling the input of Activity Logs
 */
public class ActivityInputHandler {

    /**
     * Reads an input file that contains an activity log and
     * adds it to the database
     * @param filepath path where the file is located
     */
    public static void loadIntoDatabase(String filepath){

        if(filepath.matches(".*\\.csv")){
            //handle CSV file
            System.out.println("Fileformat: CSV");
            readCSV(filepath);
        }else if(filepath.matches(".*\\.sqlite")){
            //handle the SQLite file
            System.out.println("Fileformat: SQLite");
            readSqlite(filepath);
        }
    }

    /**
     * checks if the filepaths contains a file with a valid file format
     * @param filepath location of a file
     * @return true: valid /false: not valid
     */
    public static boolean isFileFormatValid(String filepath){
        if(filepath.matches(".*\\.csv") || filepath.matches(".*\\.sqlite")){
            return true;
        }else {
            return false;
        }
    }

    /**
     * reads a CSV file and puts it into the database
     * @param filepath path where the file is located
     */
    private static void readCSV(String filepath){
        ArrayList<String[]> list = Util.read(filepath);

        if(list.size() != 0) {

            //header that contains all attributes
            String[] header = list.get(0);


            if ((header.length >=5) && (header[0].equals("id")) && (header[1].equals("activityid")) && (header[2].equals("subactivityid")) && (header[3].equals("starttime")) && (header[4].equals("endtime")) ) {
                //iterate over all lines of the CSV file
                for (int i = 1; i < list.size(); i++) {
                    int IdActivity = Integer.valueOf(list.get(i)[1]);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                    java.util.Date StartD = new Date();
                    java.util.Date EndD = new Date();
                    StartD.setTime(Long.valueOf(list.get(i)[3])+60000);
                    EndD.setTime(Long.valueOf(list.get(i)[4]));



                    String Start = format.format(StartD);
                    String End = format.format(EndD);
                    AppGlobal.getHandler().InsertActivity(AppGlobal.getHandler(), IdActivity, 1, Start, End);
                }
            }
        }
    }

    /**
     * Reads a sqlite database that contains activity logs and puts it into the database
     * @param filepath path where the file is located
     */
    private static void readSqlite(String filepath){
        //TODO read the sqlite file and put it into the database
    }

}
