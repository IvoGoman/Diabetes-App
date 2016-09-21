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
    private ArrayList<String[]> cases;

    public CustomXLog(ArrayList<ActivityItem> items) {
        this.items = items;
        this.eventList = this.convertActivityItemToStringArray(this.items);
        this.cases = this.retrieveCases(this.eventList);
        this.createXLog(this.cases);
    }

    public ArrayList<String[]> getEventList() {
        return eventList;
    }

    public ArrayList<String[]> getCases() {
        return cases;
    }

    public XLog getXLog() {
        return this.xLog;
    }

    private void createXLog(ArrayList<String[]> eventList) {
//		Build the XLog from the List
        LogBuilder builder = new LogBuilder();
        builder.startLog("ActivityLog");
        String caseHelper = eventList.get(1)[0];
        builder.addTrace(eventList.get(1)[0]);

        this.createInitialEvent(builder, eventList.get(1));
//		iterate through the event list with cases
//        TODO:do not reduce the full amount of activities
        for (int i = 1; i < eventList.size(); i++) {
//			If it is the same case then fill the trace with events
            if (eventList.get(i)[0].equals(caseHelper)) {
//				Add Start Node of Activity
                builder.addEvent(eventList.get(i)[2]);
                builder.addAttribute("Activity", eventList.get(i)[2]);
                builder.addAttribute("lifecycle:transition", "Start");
                builder.addAttribute(new XAttributeTimestampImpl("time:timestamp", new Date(Long.valueOf(eventList.get(i)[3]))));
//				Add Complete Node of an Activity
                builder.addEvent(eventList.get(i)[2]);
                builder.addAttribute("Activity", eventList.get(i)[2]);
                builder.addAttribute("lifecycle:transition", "Complete");
                builder.addAttribute(new XAttributeTimestampImpl("time:timestamp", new Date(Long.valueOf(eventList.get(i)[4]))));
            } else {
//				Add a new Trace to the Builder [this happens for every case]
                this.createEndEvent(builder, eventList.get(i));
                builder.addTrace(eventList.get(i)[0]);
                this.createInitialEvent(builder, eventList.get(i));
            }
            caseHelper = eventList.get(i)[0];
        }
//		create the XLog Object

        this.xLog = builder.build();
    }

    private void createInitialEvent(LogBuilder builder, String[] event) {
        builder.addEvent("9990");
        builder.addAttribute("Activity", "Start");
        builder.addAttribute("lifecycle:transition", "Start");
        builder.addAttribute(new XAttributeTimestampImpl("time:timestamp", new Date(Long.valueOf(event[3]))));
        builder.addEvent("9990");
        builder.addAttribute("Activity", "Start");
        builder.addAttribute("lifecycle:transition", "Complete");
        builder.addAttribute(new XAttributeTimestampImpl("time:timestamp", new Date(Long.valueOf(event[3]))));

    }

    private void createEndEvent(LogBuilder builder, String[] event) {
        builder.addEvent("9991");
        builder.addAttribute("Activity", "End");
        builder.addAttribute("lifecycle:transition", "Start");
        builder.addAttribute(new XAttributeTimestampImpl("time:timestamp", new Date(Long.valueOf(event[3]))));
        builder.addEvent("9991");
        builder.addAttribute("Activity", "End");
        builder.addAttribute("lifecycle:transition", "Complete");
        builder.addAttribute(new XAttributeTimestampImpl("time:timestamp", new Date(Long.valueOf(event[3]))));

    }

    /**
     * Create the cases from the ArrayList of ActivityItems queried
     *
     * @param items ArrayList of Activity Items
     * @return returns the ActivityItems converted into String Arrays
     */
    private ArrayList<String[]> convertActivityItemToStringArray(ArrayList<ActivityItem> items) {
        ArrayList<String[]> eventList = new ArrayList<>();
        String[] event;
        event = new String[]{"", "", "starttime", "endtime"};
        eventList.add(event);
        String id;
        int isAM;
        for (ActivityItem item : items) {
            id = String.valueOf(item.getActivityId());
            Long[] timestamps = TimeUtils.convertDateStringToTimestamp(new String[]{item.getStarttimeAsString(), item.getEndtimeAsString()});
            isAM = TimeUtils.isAM(timestamps[0] * 1000);
            event = new String[]{AppGlobal.getHandler().getActionById(item.getActivityId()), id + isAM, String.valueOf(timestamps[0] * 1000), String.valueOf(timestamps[1] * 1000)};
            eventList.add(event);
        }
        return eventList;
    }

    /**
     * Create the cases based on the list of events
     *
     * @param eventList List of Events/Activities in StringArray format
     * @return ArrayList of StringArrays of Events with Cases
     */
    public ArrayList<String[]> retrieveCases(ArrayList<String[]> eventList) {
        //creates a CaseCreator object with the CSV file in an ArrayList
        ArrayList<String[]> cases = (ArrayList<String[]>) eventList.clone();
        CaseCreator creator = new CaseCreator(cases);
        //splits the data into cases and adds a column for the case id for each entry
        creator.createCases();
        cases = creator.getList();
        return cases;
    }
}
