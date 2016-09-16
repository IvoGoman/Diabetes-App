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
     * @author edited 09.09.2016 by Stefan
     */
    private static void readCSV(String filepath){
        DataBaseHandler dbHandler = AppGlobal.getHandler();
        ArrayList<String[]> eventList = Util.read(filepath);

        CaseCreator creator = new CaseCreator(eventList);
        //splits the data into cases and adds a column for the case id for each entry
        creator.createCases();
        //adds a column with the day of the week
        creator.addDayOfWeek();
        //reduces the endtime of the previous activity by 1 if necessary
        creator.shiftSameBorderTime();
        //merges two consecutive activities which are the same
        creator.mergeConsecutiveSameActivity(true);
        creator.removeFirstCase(true);
        creator.removeLastCase();

        //retrieves the preprocessed list
        eventList = creator.getList();

        if(eventList.size() != 0) {

            //header that contains all attributes
            String[] header = eventList.get(0);

            //determine the column indexes
//            int iId = 0;
//            int iActId = 0;
//            int iSubActId = 0;
//            int iStart = 0;
//            int iEnd = 0;
//            for(int i=0; i<header.length; i++){
//                if(header[i].equals("id")){
//                    iId = i;
//                }else if(header[i].equals("activityid")){
//                    iActId = i;
//                }else if(header[i].equals("subactivityid")){
//                    iSubActId = i;
//                }else if(header[i].equals("starttime")){
//                    iStart = i;
//                }else if(header[i].equals("endtime")){
//                    iEnd = i;
//                }
//            }

            ActivityItem item;
            int subId = 0;

            eventList = creator.getList();
            for(String[] event:eventList){

                try{
                    subId = Integer.parseInt(event[3]);
                } catch(Exception e){
                    subId = 0;
                    e.printStackTrace();
                }
                item = new ActivityItem(Integer.parseInt(event[2]), subId, TimeUtils.getDate(event[4]),TimeUtils.getDate(event[5]));
                dbHandler.InsertActivityFromCSV(dbHandler,item);
            }

            //Old version
//            if ((header.length >=5)) {
//                //iterate over all lines of the CSV file
//                java.util.Date StartDlast = null;
//                java.util.Date EndDlast = null;
//                for (int i = 1; i < list.size(); i++) {
//                    int IdActivity = Integer.valueOf(list.get(i)[iActId]);
//                    if (IdActivity==2) {
//                        int IdSubActivity = Integer.valueOf(list.get(i)[iSubActId]);
//                        switch (IdSubActivity) {
//                            case 3:
//                                IdActivity = 17;
//                            case 4:
//                                IdActivity = 18;
//                        }
//                    }
//
//                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
//                    java.util.Date StartD = new Date();
//                    java.util.Date EndD = new Date();
//                    StartD.setTime(Long.valueOf(list.get(i)[iStart])+60000);
//                    EndD.setTime(Long.valueOf(list.get(i)[iEnd]));
//                    if (StartDlast!=null && EndDlast!=null && StartDlast.before(StartD) && EndDlast.after(EndD)) {
//                        continue;
//                    }
//                    if (StartDlast!=null && EndDlast!=null && StartDlast.before(StartD) && EndDlast.before(EndD)) {
//                        StartD.setTime(EndDlast.getTime()+60000);
//                        String Start = format.format(StartD);
//                        String End = format.format(EndD);
//                        AppGlobal.getHandler().InsertActivity(AppGlobal.getHandler(), IdActivity, 1, Start, End);
//                        continue;
//                    }
//                    String Start = format.format(StartD);
//                    String End = format.format(EndD);
//                    AppGlobal.getHandler().InsertActivity(AppGlobal.getHandler(), IdActivity, 1, Start, End);
//                }
//            }
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
