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
    private ArrayList<ActivityItem> items;
    private XLog xLog;
    private ArrayList<String[]> eventList;

    public CustomXLog(ArrayList<ActivityItem> items){
        this.items = items;
        this.eventList = this.loadEvents(this.items);
        this.createXLog(eventList);
    }
    public XLog getXLog(){
        return this.xLog;
    }
    private void createXLog(ArrayList<String[]> eventList) {
//		Build the XLog from the List
        LogBuilder builder = new LogBuilder();
        builder.startLog("ActivityLog");
        String caseHelper = eventList.get(1)[0];
        builder.addTrace(eventList.get(1)[0]);
        int j =0;

       this.createInitalEvent(builder,eventList.get(1));
//		iterate through the event list with cases
//        TODO:dont reduce the full amount of activites
        for (int i = 1; i < eventList.size(); i++) {
//			If it is the same case then fill the trace with events
            if (eventList.get(i)[0].equals(caseHelper)) {
//				Add Start Node of Activity
                builder.addEvent(eventList.get(i)[2]);
                builder.addAttribute("Activity", eventList.get(i)[1]);
                builder.addAttribute("lifecycle:transition", "Start");
                builder.addAttribute(new XAttributeTimestampImpl("time:timestamp", new Date(Long.valueOf(eventList.get(i)[3]))));
//				Add Complete Node of an Activity
                builder.addEvent(eventList.get(i)[2]);
                builder.addAttribute("Activity", eventList.get(i)[1]);
                builder.addAttribute("lifecycle:transition", "Complete");
                builder.addAttribute(new XAttributeTimestampImpl("time:timestamp",new Date(Long.valueOf(eventList.get(i)[4]))));
            } else {
//				Add a new Trace to the Builder [this happens for every case]
                builder.addTrace(eventList.get(i)[0]);
            this.createInitalEvent(builder,eventList.get(i));
            }
            caseHelper = eventList.get(i)[0];
        }
//		create the XLog Object
        XLog log = builder.build();
        this.xLog = log;
    }
    private void createInitalEvent(LogBuilder builder,String[] event){
        builder.addEvent("Start//Start");
        builder.addAttribute("Activity", "Start");
        builder.addAttribute("lifecycle:transition", "Start");
        builder.addAttribute(new XAttributeTimestampImpl("time:timestamp", new Date(Long.valueOf(event[3]))));
        builder.addEvent("Start//Start");
        builder.addAttribute("Activity", "Start");
        builder.addAttribute("lifecycle:transition", "Complete");
        builder.addAttribute(new XAttributeTimestampImpl("time:timestamp",new Date(Long.valueOf(event[3]))));

    }

    /**
     * Create the cases from the ArrayList of ActivityItems queried
     * @param items
     * @return
     */
    private ArrayList<String[]> loadEvents(ArrayList<ActivityItem> items){
        ArrayList<ActivityItem> list = items;
        ArrayList<String[]> eventList = new ArrayList<>();
        String[] event;
        event = new String[]{"","","starttime","endtime"};
        eventList.add(event);
        for (ActivityItem item : list
                ) {
            Long[] timestamps = TimeUtils.convertDateStringToTimestamp(new String[]{item.getStarttimeAsString(),item.getEndtimeAsString()});
            event = new String[]{AppGlobal.getHandler().getActionById(AppGlobal.getHandler(), item.getActivityId()),String.valueOf(item.getActivityId()), String.valueOf(timestamps[0]*1000), String.valueOf(timestamps[1]*1000)};
            eventList.add(event);
        }
        //creates a CaseCreator object with the CSV file in an ArrayList
        CaseCreator creator = new CaseCreator(eventList);
        //splits the data into cases and adds a column for the case id for each entry
        creator.createCases();
        eventList = creator.getList();
        return eventList;
    }

}
