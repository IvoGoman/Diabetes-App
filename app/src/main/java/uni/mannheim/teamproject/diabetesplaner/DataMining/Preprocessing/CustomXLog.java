package uni.mannheim.teamproject.diabetesplaner.DataMining.Preprocessing;


import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;

import java.util.ArrayList;
import java.util.Date;

import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;

/**
 * Created by Ivo on 10.07.2016.
 */
public class CustomXLog {
    public static XLog createXLog() {
        ArrayList<ActivityItem> list = AppGlobal.getHandler().getAllActivities(AppGlobal.getHandler());
        ArrayList<String[]> eventList = new ArrayList<>();
        String[] event;
        event = new String[]{"","","starttime","endtime"};
        eventList.add(event);
        for (ActivityItem item : list
                ) {
            Long[] timestamps = TimeUtils.convertDateStringToTimestamp(new String[]{item.getStarttimeAsString(),item.getEndtimeAsString()});
//            event = new String[]{String.valueOf(item.getActivityId()), AppGlobal.getHandler().getActionById(AppGlobal.getHandler(), item.getActivityId()), String.valueOf(timestamps[0]*1000), String.valueOf(timestamps[1]*1000)};
            event = new String[]{String.valueOf(item.getActivityId()), String.valueOf(item.getActivityId()), String.valueOf(timestamps[0]*1000), String.valueOf(timestamps[1]*1000)};
            eventList.add(event);
        }
        //creates a CaseCreator object with the CSV file in an ArrayList
        CaseCreator creator = new CaseCreator(eventList);
        //splits the data into cases and adds a column for the case id for each entry
        creator.createCases();
        //adds a column with the day of the week
//		creator.addDayOfWeek();
//		Build the XLog from the List
        LogBuilder builder = new LogBuilder();
        builder.startLog("ActivityLog");
        eventList = creator.getList();
        String caseHelper = eventList.get(0)[0];
        builder.addTrace(eventList.get(0)[0]);
        int j =0;
        builder.addEvent("Start");
        builder.addAttribute("Activity", "Start");
        builder.addAttribute("lifecyle:transition", "Start");
//                builder.addAttribute("time:timestamp", Long.valueOf(eventList.get(i)[3]));
        builder.addAttribute(new XAttributeTimestampImpl("time:timestamp", new Date(Long.valueOf(eventList.get(0)[3]))));
        builder.addAttribute("ID", "Start");
//                builder.addEvent(eventList.get(i)[2]);
        builder.addEvent(String.valueOf(j));
        builder.addAttribute("Activity", "Start");
        builder.addAttribute("lifecyle:transition", "Complete");
//                builder.addAttribute("time:timestamp", Long.valueOf(eventList.get(i)[3]));
        builder.addAttribute(new XAttributeTimestampImpl("time:timestamp", new Date(Long.valueOf(eventList.get(0)[3]))));
        builder.addAttribute("ID", "Start");
//                builder.addEvent(eventList.get(i)[2]);
        builder.addEvent(String.valueOf(j));
//		iterate through the event list with cases
//        TODO:dont reduce the full amount of activites
        for (int i = 0; i < 150; i++) {
//			If it is the same case then fill the trace with events
            if (eventList.get(i)[0].equals(caseHelper)) {
//				Add Start Node of Activity
                builder.addAttribute("Activity", eventList.get(i)[2]);
                builder.addAttribute("lifecyle:transition", "Start");
//                builder.addAttribute("time:timestamp", Long.valueOf(eventList.get(i)[3]));
                builder.addAttribute(new XAttributeTimestampImpl("time:timestamp", Long.valueOf(eventList.get(i)[3])));
                builder.addAttribute("ID", eventList.get(i)[1]);
//                builder.addEvent(eventList.get(i)[2]);
                builder.addEvent(String.valueOf(j));

//				Add Complete Node of an Activity
                builder.addAttribute("Activity", eventList.get(i)[2]);
                builder.addAttribute("lifecyle:transition", "Complete");
//                builder.addAttribute("time:timestamp", TimeUtils.getTimeStampAsDateString(Long.valueOf(eventList.get(i)[4])));
                builder.addAttribute(new XAttributeTimestampImpl("time:timestamp", Long.valueOf(eventList.get(i)[4])));
                builder.addAttribute("ID", eventList.get(i)[1]);
//                builder.addEvent(eventList.get(i)[2]);
                builder.addEvent(String.valueOf(j+1));
                j+=1;
            } else {
//				Add a new Trace to the Builder [this happens for every case]
                builder.addTrace(eventList.get(i)[0]);
                j = 0;
            }
            caseHelper = eventList.get(i)[0];
        }
//		create the XLog Object
        XLog log = builder.build();
        return log;
    }

}
