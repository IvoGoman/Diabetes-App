package uni.mannheim.teamproject.diabetesplaner.DataMining.Preprocessing;

import org.deckfour.xes.model.XLog;

import java.util.ArrayList;

import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;

/**
 * Created by Ivo on 10.07.2016.
 */
public class CustomXLog {
    public static XLog createXLog(){
        ArrayList<String[]> list = AppGlobal.getHandler().getAllEvents(AppGlobal.getHandler());
        //creates a CaseCreator object with the CSV file in an ArrayList
        CaseCreator creator = new CaseCreator(list);
        //splits the data into cases and adds a column for the case id for each entry
        creator.createCases();
        //adds a column with the day of the week
//		creator.addDayOfWeek();
//		Build the XLog from the List
        LogBuilder builder = new LogBuilder();
        builder.startLog("ActivityLog");
        list = creator.getList();
        String caseHelper = "default";
//		iterate through the event list with cases
        for (int i=0;i<list.size();i++){
//			If it is the same case then fill the trace with events
            if(list.get(i)[0].equals(caseHelper)){
//				Add Start Node of Activity
                builder.addEvent(list.get(i)[2]);
                builder.addAttribute("Activity",list.get(i)[2]);
                builder.addAttribute("ID", list.get(i)[1]);
                builder.addAttribute("lifecyle:transition","Start");
                builder.addAttribute("time:timestamp", list.get(i)[3]);
//				Add Complete Node of an Activity
                builder.addEvent(list.get(i)[2]);
                builder.addAttribute("Activity",list.get(i)[2]);
                builder.addAttribute("ID", list.get(i)[1]);
                builder.addAttribute("lifecyle:transition","Complete");
                builder.addAttribute("time:timestamp", list.get(i)[4]);
            }else{
//				Add a new Trace to the Builder [this happens for every case]
                builder.addTrace(list.get(i)[0]);
            }
            caseHelper = list.get(i)[0];
        }
//		create the XLog Object
        XLog log = builder.build();
        return log;
    }

}
