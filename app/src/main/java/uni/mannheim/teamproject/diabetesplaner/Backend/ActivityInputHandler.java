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

        //header that contains all attributes
        String[] header = list.get(0);

        //TODO: check if attributes match the database table (consider order!)

        //iterate over all lines of the CSV file
        for(int i=1; i<list.size(); i++){
            //TODO: load list into database
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
