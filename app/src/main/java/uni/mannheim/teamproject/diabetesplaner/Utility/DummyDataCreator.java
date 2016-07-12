package uni.mannheim.teamproject.diabetesplaner.Utility;

import java.util.Date;

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;

/**
 * Created by Ivo on 12.07.2016.
 * yyyy-MM-dd HH:mm
 */
public class DummyDataCreator {
    public static void createDummyData() {
        DataBaseHandler handler = AppGlobal.getHandler();
        ActivityItem item;
        Date start,end;
        String i;
        for (int j = 1; j < 31; j++) {
            if(j<10){ i="0"+j;}else{i=""+j;}
            item = new ActivityItem(1, 0, TimeUtils.getDateFromString("2016-06-" + i + " 00:01"),TimeUtils.getDateFromString("2016-06-" + i + " 06:50"),3);
            handler.InsertActivity(handler,item);
            item = new ActivityItem(3, 0, TimeUtils.getDateFromString("2016-06-" + i + " 06:51"),TimeUtils.getDateFromString("2016-06-" + i + " 07:15"),3);
            handler.InsertActivity(handler,item);
            item = new ActivityItem(2, 0, TimeUtils.getDateFromString("2016-06-" + i + " 07:16"),TimeUtils.getDateFromString("2016-06-" + i + " 07:30"),3);
            handler.InsertActivity(handler,item);
            item = new ActivityItem(12, 0, TimeUtils.getDateFromString("2016-06-" + i + " 07:31"),TimeUtils.getDateFromString("2016-06-" + i + " 07:40"),3);
            handler.InsertActivity(handler,item);
            item = new ActivityItem(4, 0, TimeUtils.getDateFromString("2016-06-" + i + " 07:46"),TimeUtils.getDateFromString("2016-06-" + i + " 08:15"),3);
            handler.InsertActivity(handler,item);
            item = new ActivityItem(13, 0, TimeUtils.getDateFromString("2016-06-" + i + " 08:16"),TimeUtils.getDateFromString("2016-06-" + i + " 11:25"),3);
            handler.InsertActivity(handler,item);
            item = new ActivityItem(12, 0, TimeUtils.getDateFromString("2016-06-" + i + " 11:26"),TimeUtils.getDateFromString("2016-06-" + i + " 11:35"),3);
            handler.InsertActivity(handler,item);
            item = new ActivityItem(2, 0, TimeUtils.getDateFromString("2016-06-" + i + " 11:36"),TimeUtils.getDateFromString("2016-06-" + i + " 12:15"),3);
            handler.InsertActivity(handler,item);
            item = new ActivityItem(12, 0, TimeUtils.getDateFromString("2016-06-" + i + " 12:16"),TimeUtils.getDateFromString("2016-06-" + i + " 12:26"),3);
            handler.InsertActivity(handler,item);
            item = new ActivityItem(13, 0, TimeUtils.getDateFromString("2016-06-" + i + " 12:27"),TimeUtils.getDateFromString("2016-06-" + i + " 17:20"),3);
            handler.InsertActivity(handler,item);
            item = new ActivityItem(4, 0, TimeUtils.getDateFromString("2016-06-" + i + " 17:21"),TimeUtils.getDateFromString("2016-06-" + i + " 17:45"),3);
            handler.InsertActivity(handler,item);
            item = new ActivityItem(2, 0, TimeUtils.getDateFromString("2016-06-" + i + " 17:46"),TimeUtils.getDateFromString("2016-06-" + i + " 18:15"),3);
            handler.InsertActivity(handler,item);
            item = new ActivityItem(5, 0, TimeUtils.getDateFromString("2016-06-" + i + " 18:16"),TimeUtils.getDateFromString("2016-06-" + i + " 22:17"),3);
            handler.InsertActivity(handler,item);
            item = new ActivityItem(3, 0, TimeUtils.getDateFromString("2016-06-" + i + " 22:18"),TimeUtils.getDateFromString("2016-06-" + i + " 22:27"),3);
            handler.InsertActivity(handler,item);
            item = new ActivityItem(1, 0, TimeUtils.getDateFromString("2016-06-" + i + " 22:28"),TimeUtils.getDateFromString("2016-06-" + i + " 23:59"),3);
            handler.InsertActivity(handler,item);

        }
    }
}
