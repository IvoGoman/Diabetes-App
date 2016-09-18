package uni.mannheim.teamproject.diabetesplaner.Utility;

import java.util.ArrayList;

import uni.mannheim.teamproject.diabetesplaner.DataMining.Preprocessing.CaseCreator;
import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;

/**
 * Created by Ivo on 12.07.2016.
 * yyyy-MM-dd HH:mm
 */
public class DummyDataCreator {
    public static void populateDataBase(){
        //String logFile = new File("").getAbsolutePath();
        //String source = logFile + "/app/src/test/java/data/SDC_ActivityData.csv";
        String source = "/data/data/uni.mannheim.teamproject.diabetesplaner/files/SDC_ActivityData.csv";
        ArrayList<String[]> eventList = Util.read(source);
//        eventList.remove(0);
        DataBaseHandler dbHandler = AppGlobal.getHandler();
        ActivityItem item;
        int subId;

        CaseCreator creator = new CaseCreator(eventList);
        creator.createCases();
        creator.mergeConsecutiveSameActivity(true);
        creator.removeFirstCase(true);
        creator.removeLastCase();
        eventList = creator.getList();
        for(String[] event:eventList){

            try{
                subId = Integer.parseInt(event[3]);
            } catch(Exception e){
                subId = 0;
                e.printStackTrace();
            }
            item = new ActivityItem(Integer.parseInt(event[2]),subId, TimeUtils.getDate(event[4]),TimeUtils.getDate(event[5]),1);
            dbHandler.InsertActivity(item);
        }
    }
    public static void createDummyData() {
        DataBaseHandler handler = AppGlobal.getHandler();
        ActivityItem item;
        String i;
        for (int j = 1; j < 31; j++) {
            if(j<10){ i="0"+j;}else{i=""+j;}
            item = new ActivityItem(1, 0, TimeUtils.getDateFromString("2016-06-" + i + " 00:01"),TimeUtils.getDateFromString("2016-06-" + i + " 06:50"),3);
            handler.InsertActivity(item);
            item = new ActivityItem(3, 0, TimeUtils.getDateFromString("2016-06-" + i + " 06:51"),TimeUtils.getDateFromString("2016-06-" + i + " 07:15"),3);
            handler.InsertActivity(item);
            item = new ActivityItem(2, 0, TimeUtils.getDateFromString("2016-06-" + i + " 07:16"),TimeUtils.getDateFromString("2016-06-" + i + " 07:30"),3);
            handler.InsertActivity(item);
            item = new ActivityItem(12, 0, TimeUtils.getDateFromString("2016-06-" + i + " 07:31"),TimeUtils.getDateFromString("2016-06-" + i + " 07:40"),3);
            handler.InsertActivity(item);
            item = new ActivityItem(4, 0, TimeUtils.getDateFromString("2016-06-" + i + " 07:46"),TimeUtils.getDateFromString("2016-06-" + i + " 08:15"),3);
            handler.InsertActivity(item);
            item = new ActivityItem(13, 0, TimeUtils.getDateFromString("2016-06-" + i + " 08:16"),TimeUtils.getDateFromString("2016-06-" + i + " 11:25"),3);
            handler.InsertActivity(item);
            item = new ActivityItem(12, 0, TimeUtils.getDateFromString("2016-06-" + i + " 11:26"),TimeUtils.getDateFromString("2016-06-" + i + " 11:35"),3);
            handler.InsertActivity(item);
            item = new ActivityItem(2, 0, TimeUtils.getDateFromString("2016-06-" + i + " 11:36"),TimeUtils.getDateFromString("2016-06-" + i + " 12:15"),3);
            handler.InsertActivity(item);
            item = new ActivityItem(12, 0, TimeUtils.getDateFromString("2016-06-" + i + " 12:16"),TimeUtils.getDateFromString("2016-06-" + i + " 12:26"),3);
            handler.InsertActivity(item);
            item = new ActivityItem(13, 0, TimeUtils.getDateFromString("2016-06-" + i + " 12:27"),TimeUtils.getDateFromString("2016-06-" + i + " 17:20"),3);
            handler.InsertActivity(item);
            item = new ActivityItem(4, 0, TimeUtils.getDateFromString("2016-06-" + i + " 17:21"),TimeUtils.getDateFromString("2016-06-" + i + " 17:45"),3);
            handler.InsertActivity(item);
            item = new ActivityItem(2, 0, TimeUtils.getDateFromString("2016-06-" + i + " 17:46"),TimeUtils.getDateFromString("2016-06-" + i + " 18:15"),3);
            handler.InsertActivity(item);
            item = new ActivityItem(5, 0, TimeUtils.getDateFromString("2016-06-" + i + " 18:16"),TimeUtils.getDateFromString("2016-06-" + i + " 22:17"),3);
            handler.InsertActivity(item);
            item = new ActivityItem(3, 0, TimeUtils.getDateFromString("2016-06-" + i + " 22:18"),TimeUtils.getDateFromString("2016-06-" + i + " 22:27"),3);
            handler.InsertActivity(item);
            item = new ActivityItem(1, 0, TimeUtils.getDateFromString("2016-06-" + i + " 22:28"),TimeUtils.getDateFromString("2016-06-" + i + " 23:59"),3);
            handler.InsertActivity(item);

        }
    }
}
