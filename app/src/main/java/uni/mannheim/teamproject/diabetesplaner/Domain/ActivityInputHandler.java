package uni.mannheim.teamproject.diabetesplaner.Domain;

import java.util.ArrayList;

import uni.mannheim.teamproject.diabetesplaner.DataMining.Preprocessing.CaseCreator;
import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;
import uni.mannheim.teamproject.diabetesplaner.Utility.Util;

/**
 * Created by Stefan on 19.02.2016.
 * Class for handling the input of Activity Logs
 */
public class ActivityInputHandler {

    /**
     * Reads an input file that contains an activity log and
     * adds it to the database
     * @param filepath path where the file is located
     * @author Stefan
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
     * @author Stefan
     */
    public static boolean isFileFormatValid(String filepath){
        if(filepath.matches(".*\\.csv") || filepath.matches(".*\\.sqlite")){
            return true;
        }else {
            return false;
        }
    }

    /**
     * Created by leonidgunko
     * reads a CSV file and puts it into the database
     * @param filepath path where the file is located
     * @author edited 09.09.2016 by Stefan
     */
    public static void readCSV(String filepath){

        DataBaseHandler dbHandler = AppGlobal.getHandler();
        ArrayList<String[]> eventList = Util.read(filepath);

        CaseCreator creator = new CaseCreator(eventList);
        //splits the data into cases and adds a column for the case id for each entry
        creator.createCases();
        //adds a column with the day of the week
        creator.addDayOfWeek();
        //first case of the complete list is csv header (causes type conversation errors if not removed)
        creator.removeFirstCase(true);
        creator.shiftSameBorderTime();
        //merges two consecutive activities which are the same
        creator.mergeConsecutiveSameActivity(true);
        //removes activities where endtime is before starttime
        creator.removeActivitiesWithEndBeforeStarttime();

        //removes first and last case since they are incomplete and would skrew the prediction
        creator.removeFirstCase(true);
        creator.removeLastCase();

        //retrieves the preprocessed list
        eventList = creator.getList();

        if(eventList.size() != 0) {

            //header that contains all attributes
            String[] header = eventList.get(0);

            ActivityItem item;
            long lastitemEnd=0;
            int subId = 0;

            eventList = creator.getList();
            for(String[] event:eventList){

                try{
                    subId = Integer.parseInt(event[3]);
                } catch(Exception e){
                    subId = Integer.parseInt(event[2]);
                    e.printStackTrace();
                }
                    if (lastitemEnd!=0) {
                        item = new ActivityItem(Integer.parseInt(event[2]), subId, TimeUtils.getDate(String.valueOf(lastitemEnd + 60000)), TimeUtils.getDate(event[5]));
                        AppGlobal.setEditFlag(true);
                        dbHandler.DeleteActivity(item.getStarttimeAsString(),item.getEndtimeAsString());
                        dbHandler.InsertActivityFromCSV(item);
                    }else{
                        item = new ActivityItem(Integer.parseInt(event[2]), subId, TimeUtils.getDate(event[4]), TimeUtils.getDate(event[5]));
                        AppGlobal.setEditFlag(true);
                        dbHandler.DeleteActivity(item.getStarttimeAsString(),item.getEndtimeAsString());
                        dbHandler.InsertActivityFromCSV(item);
                    }
                lastitemEnd = Long.valueOf(event[5]);
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
