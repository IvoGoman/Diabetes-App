package uni.mannheim.teamproject.diabetesplaner.Backend;

import java.util.ArrayList;

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
    private static void readCSV(String filepath){
        ArrayList<String[]> list = Util.read(filepath);

        int activityIndex = 0;
        int subactivityIndex = 0;
        int startIndex = 0;
        int endIndex = 0;


        //header that contains all attributes
        String[] header = list.get(0);

        for(int i=0; i<header.length; i++){
            switch(header[i]){
                case "activityId":
                    activityIndex = i;
                case "subactivityid":
                    subactivityIndex = i;
                case "starttime":
                    startIndex = i;
                case "endtime":
                    endIndex = i;
            }
        }

        //TODO: check if attributes match the database table (consider order!)

        //iterate over all lines of the CSV file
        for(int i=1; i<list.size(); i++){
            String activityid = list.get(i)[activityIndex];
            String subactivityid = list.get(i)[subactivityIndex];
            String starttime = list.get(i)[startIndex];
            String endtime = list.get(i)[endIndex];

            //TODO something like: Database.addActivity(activityid, subactivityid, starttime, endtime);
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
