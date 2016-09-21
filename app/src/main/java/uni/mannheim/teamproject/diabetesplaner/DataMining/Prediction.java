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

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
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

    /*

    public ArrayList<PeriodAction> GetRoutine1() throws Exception {
        // Creates ARFF file for the instances to be saved to
        Context c = AppGlobal.getcontext();
        DataBaseHandler helper = AppGlobal.getHandler();
        String S =c.getApplicationInfo().dataDir + "/files/" + FILENAME;
        OutputStreamWriter output = new OutputStreamWriter(c.openFileOutput(FILENAME, Context.MODE_ENABLE_WRITE_AHEAD_LOGGING));

        String Actions = "{";

        Cursor cursor1 = helper.getAllActions(helper);

        if (cursor1.moveToFirst()) {
            do {
                Actions += cursor1.getString(1) + ",";
            }
            while (cursor1.moveToNext());
            Actions = Actions.substring(0, Actions.length()-1);
            Actions +="}";
        }
        // close cursor
        if (!cursor1.isClosed()) {
            cursor1.close();
        }

        // Create the layout for the file
        output.write("@RELATION output \n");
        output.write("\n");
        output.write("@ATTRIBUTE TimeStamp numeric \n");
        output.write("@ATTRIBUTE Duration numeric \n");
        Actions = Actions.replace(" ", "");
        output.write("@ATTRIBUTE Action" + Actions + "\n");
        output.write("@ATTRIBUTE Action1" + Actions + "\n");

//        output.write("@ATTRIBUTE Location" + Locations + "\n");
//        output.write("@ATTRIBUTE Action3" + Actions + "\n");
        output.write("\n");
        output.write("@DATA \n");

        int Duration = 0;
        Cursor cursor2 = helper.getAllRoutine(helper);
        Activity1 = "Schlafen";
        int p = cursor2.getCount();

        if (cursor2.moveToFirst()) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            //java.util.Date Start1 = format.parse(cursor2.getString(3));
            int duration=0;
            do {
                java.util.Date Start = format.parse(cursor2.getString(3));
                Calendar calendar = Calendar.getInstance();
                Activity = cursor2.getString(1).replace(" ", "");
                java.util.Date End = format.parse(cursor2.getString(4));
                java.util.Date CurDate = Start;
                int num =0;
                while (CurDate.before(End)) {
                    num+=1;
                    if (num == 2 ){
                        duration = 0;
                    }
                    calendar.setTime(Start);

                    int minutesleftfrommidnight = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
                    duration += 10;
                    //Start1 = format.parse(cursor2.getString(3));
                    //String S = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))+ ':' + String.valueOf(calendar.get(Calendar.MINUTE)) + ',' + Activity + "\n";
                    output.write(String.valueOf(minutesleftfrommidnight) + ',' + String.valueOf(duration) + ',' + Activity + ',' + Activity1  + "\n");
                    output.write(String.valueOf(minutesleftfrommidnight) + ',' + String.valueOf(duration) + ',' + Activity + ',' + Activity1  + "\n");
                    output.write(String.valueOf(minutesleftfrommidnight) + ',' + String.valueOf(duration) + ',' + Activity + ',' + Activity1  + "\n");
                    //output.write(String.valueOf(minutesleftfrommidnight) + ',' + String.valueOf(duration) + ',' + Activity + ',' + Activity1  + "\n");

                    CurDate.setTime(CurDate.getTime() + 10 * 60 * 1000);
                    Activity1 = Activity;
                }


            }
            while (cursor2.moveToNext());
            output.close();
        }
        // close cursor
        if (!cursor2.isClosed()) {
            cursor2.close();
        }

        String pat = c.getApplicationInfo().dataDir;
        String pat1;
        //pat1 = c.getApplicationInfo().publicSourceDir;
        pat1 = c.getApplicationInfo().packageName; //
        //pat1 = c.getPackageResourcePath();
        //pat1 = c.getFileStreamPath();

        //pat1 = c.getApplicationInfo().sourceDir;
        String s = "data/data/"+ pat1 + "/files/output.arff";
        BufferedReader datafile = readDataFile("data/data/"+ pat1 + "/files/output.arff");

        Instances data = new Instances(datafile);
        data.setClassIndex(data.numAttributes() - 2);

        J48 tree = new J48();

        tree.buildClassifier(data);

        ArrayList<TimeAction>TimeAction1 = new ArrayList<TimeAction>();
        Duration = 10;
        OutputStreamWriter output2 = new OutputStreamWriter(c.openFileOutput("output3", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING));
        double lastactionpr = 0.0;
        for (int i = 0; i< 1440; i+=10){
            //Instance inst1 = new Instance(2);
            data.instance(0).setValue(0,i);
            data.instance(0).setValue(1,Duration);
            data.instance(0).setValue(3, lastactionpr);

            //inst1.setValue(0,i);
            double curentaction = tree.classifyInstance(data.instance(0));

            output2.write(String.valueOf(i) + ',' + String.valueOf(Duration) + ',' +  String.valueOf(lastactionpr) + ',' + String.valueOf(lastactionpr) + "\n");


            if (lastactionpr ==curentaction){
                Duration +=10;
            }
            else{
                Duration = 10;
            }
            lastactionpr = curentaction;
            //double nextaction = tree.classifyInstance(data.instance(i + 1));
            //String time = String.valueOf(data.instance(i + 1).value(0));
            //int time = (int)data.instance(0).value(0);
            TimeAction T1 = new TimeAction();
            T1.Action = curentaction;
            T1.Time = i;
            TimeAction1.add(T1);
        }


//        ArrayList<PeriodAction> PA1 = new ArrayList<PeriodAction>();
        for (int i =0;i<TimeAction1.size();i++) {
            int ind = (int)TimeAction1.get(i).Action;

            if (i==0){
                Action = data.attribute(2).value(ind);
                int Hours = (int) TimeAction1.get(i).Time / 60;
                int Minutes = TimeAction1.get(i).Time - Hours * 60;
                Start = Integer.toString(Hours) + ":" + Integer.toString(Minutes);
            }
            else{
                if (TimeAction1.get(i-1).Action != TimeAction1.get(i).Action){
                    Action = data.attribute(2).value(ind);
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
                        output2.write(PA.Start + ',' + PA.End + ',' + PA.Action + "; \n");
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
                    output2.write(PA.Start + ',' + PA.End + ',' + PA.Action + "; \n");

                }
            }
        }
        output2.close();
        return PA1;
    }

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

    public static int getSubactivityID(String ActivitySubActivity){
        String Activity = ActivitySubActivity.substring(0,ActivitySubActivity.indexOf("|"));
        String SubActivity = ActivitySubActivity.substring(ActivitySubActivity.indexOf("|")+1,ActivitySubActivity.length());

        HashMap<String,Integer> subActivities = AppGlobal.getHandler().getAllSubactivities(AppGlobal.getHandler().getActivityIDForPred(Activity));
        int subActivity = subActivities.get(SubActivity);
        return subActivity;
    }


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
                        for (int i=0;i<100;i++){
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

        ArrayList<ActivityItem>routine = new ArrayList<>();
        for (ArrayList<ActivityItem> day:train){
            for(ActivityItem ai:day){
                routine.add(ai);
            }
        }

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            int duration = 0;
            for (ActivityItem ai: routine){
                java.util.Date Start = ai.getStarttime();
                Calendar calendar = Calendar.getInstance();
                ActivityCur = AppGlobal.getHandler().getActionById(ai.getActivityId()).replace(" ","") +"|"+ AppGlobal.getHandler().getSubactivity(ai.getSubactivityId()).replace(" ","");
                java.util.Date End = ai.getEndtime();
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
                    for (int i=0;i<300;i++){
                        if (ActivityCur!=ActivityBefore1){
                            inst.add(newInstance);
                        }}

                    CurDate.setTime(CurDate.getTime() + 1 * 60 * 1000);
                    ActivityBefore1 = ActivityCur;
                }
            }
        return inst;
    }

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
            String Month = DataBaseHandler.formatMonthOrDay(calendar.get(Calendar.MONTH) + 1);
            String Day = DataBaseHandler.formatMonthOrDay(calendar.get(Calendar.DAY_OF_MONTH));
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
                Start2+=startTimeS+"0";
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
                End2+=endTimeS+"0";
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
            String Month = DataBaseHandler.formatMonthOrDay(calendar.get(Calendar.MONTH) + 1);
            String Day = DataBaseHandler.formatMonthOrDay(calendar.get(Calendar.DAY_OF_MONTH));
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
                Start2+=startTimeS+"0";
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
                End2+=endTimeS+"0";
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

