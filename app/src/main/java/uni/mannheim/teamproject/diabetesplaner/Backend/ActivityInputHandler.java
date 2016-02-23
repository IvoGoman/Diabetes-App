package uni.mannheim.teamproject.diabetesplaner.Backend;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import uni.mannheim.teamproject.diabetesplaner.DataMining.Util;

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
    public static void loadIntoDatabase(String filepath,DataBaseHandler helper){

        if(filepath.matches(".*\\.csv")){
            //handle CSV file
            System.out.println("Fileformat: CSV");
            readCSV(filepath,helper);
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
        if(filepath.matches(".*\\.csv")){
            return true;
        }else if(filepath.matches(".*\\.sqlite")){
            return true;
        }else {
            return false;
        }
    }

    /**
     * reads a CSV file and puts it into the database
     * @param filepath path where the file is located
     */
    private static void readCSV(String filepath,DataBaseHandler helper){
        ArrayList<String[]> list = Util.read(filepath);

        //header that contains all attributes
        String[] header = list.get(0);



        //TODO: check if attributes match the database table (consider order!)

        //iterate over all lines of the CSV file
        for(int i=1; i<list.size(); i++){
            int IdActivity = Integer.valueOf(list.get(i)[1]);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            java.util.Date StartD = new Date();
            java.util.Date EndD = new Date();
            StartD.setTime(Long.valueOf(list.get(i)[3]));
            EndD.setTime(Long.valueOf(list.get(i)[4]));
            String Start = format.format(StartD);
            String End = format.format(EndD);
            helper.InsertActivity(helper,IdActivity,1,Start,End);
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
