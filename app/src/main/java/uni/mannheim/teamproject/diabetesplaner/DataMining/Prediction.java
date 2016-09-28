package uni.mannheim.teamproject.diabetesplaner.DataMining;

import android.database.Cursor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;


/**
 * Created by leonidgunko on 10.02.16.
 */
public class Prediction {
    private static int StartHours,StartMinutes,EndHours,EndMinutes,CurHours,CurMinutes;
    private static String ActivityCur,ActivityBefore1,ActivityBefore2,ActivityBefore3, Location;


    private static String Start;
    private static String End;
    private static int Action;

    private static ArrayList<PeriodAction> PA1 = new ArrayList<PeriodAction>();

    public static class TimeAction{
        int Time;
        double Action;
        public TimeAction(int time, double action){
            this.Time = time;
            this.Action = action;
        }
    }
    public static class PeriodAction {
        public String Start;
        public String End;
        public int  Action;

        String GetStart(){
            return Start;
        }
        String GetEnd(){
            return End;
        }
        int GetAction(){
            return Action;
        }
        public void setStart(String start){
            Start = start;
        }
        public void setEnd(String end){
            End = end;
        }
        public void setAction(int action){
            Action = action;
        }
    }


    private static final String FILENAME = "output.arff";
    private static final String FILENAME1 = "output1.arff";

    public static BufferedReader readDataFile(String filename) {
        BufferedReader inputReader = null;

        try {
            inputReader = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException ex) {
            System.err.println("File not found: " + filename);
        }

        return inputReader;
    }

    /**
     * Created by leonidgunko
     * gets all possible activities for prediction
     */
    public static FastVector getActivities(){
        FastVector Activities = new FastVector();

        Cursor cursor2 = AppGlobal.getHandler().getAllRoutine();

        if (cursor2.moveToFirst()) {
            do {
                ActivityCur = cursor2.getString(cursor2.getColumnIndex("Activity")).replace(" ","") +"|"+ cursor2.getString(cursor2.getColumnIndex("SubActivity")).replace(" ","");
                if (!Activities.contains(ActivityCur)) {
                    Activities.addElement(ActivityCur);
                }
            }
            while (cursor2.moveToNext());
        }
        cursor2.close();
        return Activities;
    }

    /**
     * Created by leonidgunko
     * gets subactivity ID from activity|subactivity
     */
    public static int getSubactivityID(String ActivitySubActivity){
        String Activity = ActivitySubActivity.substring(0,ActivitySubActivity.indexOf("|"));
        String SubActivity = ActivitySubActivity.substring(ActivitySubActivity.indexOf("|")+1,ActivitySubActivity.length());

        HashMap<String,Integer> subActivities = AppGlobal.getHandler().getAllSubactivities(AppGlobal.getHandler().getActivityIDForPred(Activity));
        int subActivity = subActivities.get(SubActivity);
        return subActivity;
    }

    /**
     * Created by leonidgunko
     * gets all instances from db
     */
    public static Instances getInstances() throws ParseException {

        FastVector Activities = getActivities();
        Attribute Activity = new Attribute("Activity", Activities);
        Attribute Activity1 = new Attribute("Activity1", Activities);
        Attribute Time = new Attribute("Time");
        Attribute Dur = new Attribute("Duration");

        FastVector attinfo = new FastVector(4);
        attinfo.addElement(Time);
        attinfo.addElement(Dur);
        attinfo.addElement(Activity);
        attinfo.addElement(Activity1);

        Instances inst = new Instances("output", attinfo, 1);
        Instance newInstance = new Instance(4);
        newInstance.setDataset(inst);

        ActivityBefore1 = "Schlafen|Schlafen";

        Cursor cursor2 = AppGlobal.getHandler().getAllRoutine();
        int p = cursor2.getCount();
        //////////////////////////////////////////////
        if (cursor2.moveToFirst()) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            int duration = 0;
            do {
                java.util.Date Start = format.parse(cursor2.getString(cursor2.getColumnIndex("Start")));
                Calendar calendar = Calendar.getInstance();
                ActivityCur = cursor2.getString(cursor2.getColumnIndex("Activity")).replace(" ","") +"|"+ cursor2.getString(cursor2.getColumnIndex("SubActivity")).replace(" ","");
                java.util.Date End = format.parse(cursor2.getString(cursor2.getColumnIndex("End")));
                java.util.Date CurDate = Start;
                int num = 0;
                while (CurDate.before(End)) {
                    num += 1;
                    if (num == 2) {
                        duration = 0;
                    }
                    calendar.setTime(CurDate);

                    int minutesleftfrommidnight = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
                    duration += 1;

                    newInstance.setValue(0, (double) minutesleftfrommidnight);
                    newInstance.setValue(1, (double) duration);
                    newInstance.setValue(2, ActivityCur);
                    newInstance.setValue(3, ActivityBefore1);

                    inst.add(newInstance);

                    if (ActivityCur!=ActivityBefore1){
                        for (int i=0;i<4;i++){
                        inst.add(newInstance);
                        }
                    }

                    CurDate.setTime(CurDate.getTime() + 1 * 60 * 1000);
                    ActivityBefore1 = ActivityCur;
                }
            }
            while (cursor2.moveToNext());
        }
        // close cursor
        if (!cursor2.isClosed()) {
            cursor2.close();
        }
        return inst;
    }

    /**
     * Created by leonidgunko
     * gets instances from traininf activitylist
     */
    public static Instances getInstancesFromArray(ArrayList<ArrayList<ActivityItem>> train) throws ParseException {

        FastVector Activities = getActivities();
        Attribute Activity = new Attribute("Activity", Activities);
        Attribute Activity1 = new Attribute("Activity1", Activities);
        Attribute Time = new Attribute("Time");
        Attribute Dur = new Attribute("Duration");

        FastVector attinfo = new FastVector(4);
        attinfo.addElement(Time);
        attinfo.addElement(Dur);
        attinfo.addElement(Activity);
        attinfo.addElement(Activity1);

        Instances inst = new Instances("output", attinfo, 1);
        Instance newInstance = new Instance(4);
        newInstance.setDataset(inst);

        ActivityBefore1 = "Schlafen|Schlafen";
        if (Locale.getDefault().getLanguage().equals("en")) {
            ActivityBefore1 = "Sleeping|Sleeping";
        }

        ArrayList<ActivityItem>routine = new ArrayList<>();
        for (int i=1;i<train.size()+1;i++){
            try {
                ArrayList<ActivityItem> day = train.get(train.size() - i);
                for (ActivityItem ai : day) {
                    routine.add(ai);
                }
            }
            catch(Exception e){
                continue;
            }
        }

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            int duration = 0;
            for (ActivityItem ai: routine){
                java.util.Date Start = new Date();
                Start.setTime(ai.getStarttime().getTime());
                Calendar calendar = Calendar.getInstance();
                ActivityCur = AppGlobal.getHandler().getActionById(ai.getActivityId()).replace(" ","") +"|"+ AppGlobal.getHandler().getSubactivity(ai.getSubactivityId()).replace(" ","");
                java.util.Date End = new Date();
                End.setTime(ai.getEndtime().getTime());
                java.util.Date CurDate = Start;
                int num = 0;
                while (CurDate.before(End)) {
                    num += 1;
                    if (num == 2) {
                        duration = 0;
                    }
                    calendar.setTime(CurDate);

                    int minutesleftfrommidnight = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
                    duration += 1;

                    newInstance.setValue(0, (double) minutesleftfrommidnight);
                    newInstance.setValue(1, (double) duration);
                    newInstance.setValue(2, ActivityCur);
                    newInstance.setValue(3, ActivityBefore1);

                    inst.add(newInstance);
                    if (ActivityCur!=ActivityBefore1){
                        for (int i=0;i<1100;i++){
                            inst.add(newInstance);
                        }}

                    CurDate.setTime(CurDate.getTime() + 1 * 60 * 1000);
                    ActivityBefore1 = ActivityCur;
                }
            }
        return inst;
    }

    /**
     * Created by leonidgunko
     * predicts routine as array of timeaction
     */
    public static ArrayList<TimeAction> GetRoutineAsTimeAction(Instances inst) throws Exception {

        if (PA1.isEmpty() == false) {
            PA1.clear();
        }


        Instance newInstance = inst.firstInstance();

        inst.setClassIndex(inst.numAttributes() - 2);
        J48 tree = new J48();
        tree.buildClassifier(inst);

        ArrayList<TimeAction> TimeAction1 = new ArrayList<TimeAction>();
        int Duration = 1;
        double lastactionpr = inst.firstInstance().value(3);
        for (int i = 0; i < 1440; i += 1) {
            newInstance.setValue(0, i);
            newInstance.setValue(1, Duration);
            newInstance.setValue(3, lastactionpr);

            double curentaction = tree.classifyInstance(newInstance);

            if (lastactionpr == curentaction) {
                Duration += 1;
            } else {
                Duration = 1;
            }
            lastactionpr = curentaction;
            TimeAction T1 = new TimeAction(i, curentaction);
            TimeAction1.add(T1);
        }
        return TimeAction1;
    }


    /**
     * Created by leonidgunko
     * converts routine in timeaction to periodaction
     */
    public static ArrayList<PeriodAction> GetRoutineAsPA() throws Exception {
        Instances inst = getInstances();
        ArrayList<TimeAction> TimeAction1 = GetRoutineAsTimeAction(inst);
        for (int i =0;i<TimeAction1.size();i++) {
            int ind = (int)TimeAction1.get(i).Action;

            if (i==0){
                Action = ind;
                int Hours = (int) TimeAction1.get(i).Time / 60;
                int Minutes = TimeAction1.get(i).Time - Hours * 60;
                Start = Integer.toString(Hours) + ":" + Integer.toString(Minutes);
            }
            else{
                if (TimeAction1.get(i-1).Action != TimeAction1.get(i).Action){
                    Action = ind;
                    int Hours = (int) TimeAction1.get(i).Time / 60;
                    int Minutes = TimeAction1.get(i).Time - Hours * 60;
                    Start = Integer.toString(Hours) + ":" + Integer.toString(Minutes);
                }
                if (i<TimeAction1.size()-1) {
                    if (TimeAction1.get(i+1).Action != TimeAction1.get(i).Action){
                        int Hours = (int) TimeAction1.get(i).Time / 60;
                        int Minutes = TimeAction1.get(i).Time - Hours * 60;
                        End = Integer.toString(Hours) + ":" + Integer.toString(Minutes);
                        PeriodAction PA = new PeriodAction();
                        PA.Start = Start;
                        PA.Action = Action;
                        PA.End = End;
                        PA1.add(PA);
                    }
                }
                else{
                    int Hours = (int) TimeAction1.get(i).Time / 60;
                    int Minutes = TimeAction1.get(i).Time - Hours * 60;
                    End = Integer.toString(Hours) + ":" + Integer.toString(Minutes);
                    PeriodAction PA = new PeriodAction();
                    PA.Start = Start;
                    PA.Action = Action;
                    PA.End = End;
                    PA1.add(PA);
                }
            }
        }
        return PA1;
    }

    /**
     * Created by leonidgunko
     * predicts routine from training array as a PeriodAction array
     */
    public static ArrayList<PeriodAction> GetRoutineAsPA(ArrayList<ArrayList<ActivityItem>> train) throws Exception {
        Instances inst = getInstancesFromArray(train);
        ArrayList<TimeAction> TimeAction1 = GetRoutineAsTimeAction(inst);
        for (int i =0;i<TimeAction1.size();i++) {
            int ind = (int)TimeAction1.get(i).Action;

            if (i==0){
                Action = ind;
                int Hours = (int) TimeAction1.get(i).Time / 60;
                int Minutes = TimeAction1.get(i).Time - Hours * 60;
                Start = Integer.toString(Hours) + ":" + Integer.toString(Minutes);
            }
            else{
                if (TimeAction1.get(i-1).Action != TimeAction1.get(i).Action){
                    Action = ind;
                    int Hours = (int) TimeAction1.get(i).Time / 60;
                    int Minutes = TimeAction1.get(i).Time - Hours * 60;
                    Start = Integer.toString(Hours) + ":" + Integer.toString(Minutes);
                }
                if (i<TimeAction1.size()-1) {
                    if (TimeAction1.get(i+1).Action != TimeAction1.get(i).Action){
                        int Hours = (int) TimeAction1.get(i).Time / 60;
                        int Minutes = TimeAction1.get(i).Time - Hours * 60;
                        End = Integer.toString(Hours) + ":" + Integer.toString(Minutes);
                        PeriodAction PA = new PeriodAction();
                        PA.Start = Start;
                        PA.Action = Action;
                        PA.End = End;
                        PA1.add(PA);
                    }
                }
                else{
                    int Hours = (int) TimeAction1.get(i).Time / 60;
                    int Minutes = TimeAction1.get(i).Time - Hours * 60;
                    End = Integer.toString(Hours) + ":" + Integer.toString(Minutes);
                    PeriodAction PA = new PeriodAction();
                    PA.Start = Start;
                    PA.Action = Action;
                    PA.End = End;
                    PA1.add(PA);
                }
            }
        }
        return PA1;
    }


    /**
     * Created by leonidgunko
     * gets routine as Arraylist of ActivityItems form training array
     */
    public static ArrayList<ActivityItem> GetRoutineAsAI(ArrayList<ArrayList<ActivityItem>> train) throws Exception {
        ArrayList<ActivityItem> result= new ArrayList<>();
        ArrayList<PeriodAction> PA = GetRoutineAsPA(train);
        for (PeriodAction pa: PA){
            int activitySubactivityId = pa.GetAction();
            FastVector Activities = getActivities();
            String activitySubactivity = Activities.elementAt(activitySubactivityId).toString();
            int subActivityId = getSubactivityID(activitySubactivity);
            int activityId = AppGlobal.getHandler().getActivityIdbySubActicityId(subActivityId);
            String startTimeS = pa.GetStart();
            String endTimeS = pa.GetEnd();

            Calendar calendar = Calendar.getInstance();
            int Year = calendar.get(Calendar.YEAR);
            String Month = TimeUtils.formatMonthOrDay(calendar.get(Calendar.MONTH) + 1);
            String Day = TimeUtils.formatMonthOrDay(calendar.get(Calendar.DAY_OF_MONTH));
            String StartOfDay = String.valueOf(Year) + "-" + String.valueOf(Month) + "-" + String.valueOf(Day);
            String EndOfDay = String.valueOf(Year) + "-" + String.valueOf(Month) + "-" + String.valueOf(Day);

            if (startTimeS.charAt(1) == ':' && startTimeS.length()==3){
                String Start2 ="";
                Start2+="0"+startTimeS.charAt(0)+":0"+startTimeS.charAt(2);
                startTimeS=Start2;
            }
            if (startTimeS.charAt(1) == ':' && startTimeS.length()==4){
                String Start2 ="";
                Start2+="0"+startTimeS.charAt(0)+":"+startTimeS.charAt(2)+startTimeS.charAt(3);
                startTimeS=Start2;
            }
            if (startTimeS.charAt(2) == ':' && startTimeS.length()==4){
                String Start2 ="";
                Start2+="0"+startTimeS;
                startTimeS=Start2;
            }
            if (startTimeS.charAt(2) == ':' && startTimeS.length()==4){
                String Start2 ="";
                Start2+=startTimeS.charAt(0)+startTimeS.charAt(1)+":0"+startTimeS.charAt(3);
                startTimeS=Start2;
            }

            if (endTimeS.charAt(1) == ':' && endTimeS.length()==3){
                String End2 ="";
                End2+="0"+endTimeS.charAt(0)+":0"+endTimeS.charAt(2);
                endTimeS=End2;
            }
            if (endTimeS.charAt(1) == ':' && endTimeS.length()==4){
                String End2 ="";
                End2+="0"+endTimeS.charAt(0)+":"+endTimeS.charAt(2)+endTimeS.charAt(3);
                endTimeS=End2;
            }
            if (endTimeS.charAt(2) == ':' && endTimeS.length()==4){
                String End2 ="";
                End2+="0"+endTimeS;
                endTimeS=End2;
            }
            if (endTimeS.charAt(2) == ':' && endTimeS.length()==4){
                String End2 ="";
                End2+=endTimeS.charAt(0)+endTimeS.charAt(1)+":0"+endTimeS.charAt(3);
                endTimeS=End2;
            }


            startTimeS = StartOfDay.toString() + " " + startTimeS;
            endTimeS = EndOfDay.toString()  + " " + endTimeS;

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date startTime = format.parse(startTimeS);
            Date endTime = format.parse(endTimeS);
            ActivityItem aI = new ActivityItem(activityId,subActivityId,startTime,endTime);
            result.add(aI);
        }
        return result;
    }

    /**
     * Created by leonidgunko
     * predicts routine as arraylist of activity items from the whole routine
     */
    public static ArrayList<ActivityItem> GetRoutineAsAI() throws Exception {
        ArrayList<ActivityItem> result= new ArrayList<>();
        ArrayList<PeriodAction> PA = GetRoutineAsPA();
        for (PeriodAction pa: PA){
            int activitySubactivityId = pa.GetAction();
            FastVector Activities = getActivities();
            String activitySubactivity = Activities.elementAt(activitySubactivityId).toString();
            int subActivityId = getSubactivityID(activitySubactivity);
            int activityId = AppGlobal.getHandler().getActivityIdbySubActicityId(subActivityId);
            String startTimeS = pa.GetStart();
            String endTimeS = pa.GetEnd();

            Calendar calendar = Calendar.getInstance();
            int Year = calendar.get(Calendar.YEAR);
            String Month = TimeUtils.formatMonthOrDay(calendar.get(Calendar.MONTH) + 1);
            String Day = TimeUtils.formatMonthOrDay(calendar.get(Calendar.DAY_OF_MONTH));
            String StartOfDay = String.valueOf(Year) + "-" + String.valueOf(Month) + "-" + String.valueOf(Day);
            String EndOfDay = String.valueOf(Year) + "-" + String.valueOf(Month) + "-" + String.valueOf(Day);

            if (startTimeS.charAt(1) == ':' && startTimeS.length()==3){
                String Start2 ="";
                Start2+="0"+startTimeS.charAt(0)+":0"+startTimeS.charAt(2);
                startTimeS=Start2;
            }
            if (Start.charAt(1) == ':' && Start.length()==4){
                String Start2 ="";
                Start2+="0"+startTimeS.charAt(0)+":"+startTimeS.charAt(2)+startTimeS.charAt(3);
                startTimeS=Start2;
            }
            if (startTimeS.charAt(2) == ':' && startTimeS.length()==4){
                String Start2 ="";
                Start2+="0"+startTimeS;
                startTimeS=Start2;
            }
            if (startTimeS.charAt(2) == ':' && startTimeS.length()==4){
                String Start2 ="";
                Start2+=startTimeS.charAt(0)+startTimeS.charAt(1)+":0"+startTimeS.charAt(3);
                startTimeS=Start2;
            }

            if (endTimeS.charAt(1) == ':' && endTimeS.length()==3){
                String End2 ="";
                End2+="0"+endTimeS.charAt(0)+":0"+endTimeS.charAt(2);
                endTimeS=End2;
            }
            if (endTimeS.charAt(1) == ':' && endTimeS.length()==4){
                String End2 ="";
                End2+="0"+endTimeS.charAt(0)+":"+endTimeS.charAt(2)+endTimeS.charAt(3);
                endTimeS=End2;
            }
            if (endTimeS.charAt(2) == ':' && endTimeS.length()==4){
                String End2 ="";
                End2+="0"+endTimeS;
                endTimeS=End2;
            }
            if (endTimeS.charAt(2) == ':' && endTimeS.length()==4){
                String End2 ="";
                End2+=endTimeS.charAt(0)+endTimeS.charAt(1)+":0"+endTimeS.charAt(3);
                endTimeS=End2;
            }


            startTimeS = StartOfDay.toString() + " " + startTimeS;
            endTimeS = EndOfDay.toString()  + " " + endTimeS;

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date startTime = format.parse(startTimeS);
            Date endTime = format.parse(endTimeS);
            ActivityItem aI = new ActivityItem(activityId,subActivityId,startTime,endTime);
            result.add(aI);
        }
        return result;
    }

    /**
     * Created by leonidgunko
     * predicts routine as arraylist of periodaction
     */
    public static ArrayList<PeriodAction> GetRoutineAsPAforInserting() throws Exception {
        ArrayList<PeriodAction> result= new ArrayList<>();
        ArrayList<PeriodAction> PA = GetRoutineAsPA();
        for (PeriodAction pa: PA){
            int activitySubactivityId = pa.GetAction();
            FastVector Activities = getActivities();
            String activitySubactivity = Activities.elementAt(activitySubactivityId).toString();
            int subActivityId = getSubactivityID(activitySubactivity);
            int activityId = AppGlobal.getHandler().getActivityIdbySubActicityId(subActivityId);
            PeriodAction PAforInserting = new PeriodAction();
            PAforInserting.Action= subActivityId;
            PAforInserting.Start = pa.GetStart();
            PAforInserting.End = pa.GetEnd();
            result.add(PAforInserting);
        }
        return result;
    }

}

