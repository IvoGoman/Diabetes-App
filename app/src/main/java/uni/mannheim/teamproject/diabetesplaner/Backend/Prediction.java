package uni.mannheim.teamproject.diabetesplaner.Backend;

import android.content.Context;
import android.database.Cursor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import uni.mannheim.teamproject.diabetesplaner.Backend.DataBaseHandler;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;


/**
 * Created by leonidgunko on 10.02.16.
 */
public class Prediction {
    private static int StartHours,StartMinutes,EndHours,EndMinutes,CurHours,CurMinutes;
    private static String Activity,Activity1,Activity2,Activity3, Location;

    private static String Start;
    private static String End;
    private static String Action;

    private static ArrayList<PeriodAction> PA1 = new ArrayList<PeriodAction>();

    private static class TimeAction{
        int Time;
        double Action;
    }
    public static class PeriodAction {
        String Start;
        String End;
        String Action;

        String GetStart(){
            return Start;
        }
        String GetEnd(){
            return End;
        }
        String GetAction(){
            return Action;
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

    public ArrayList<PeriodAction> GetRoutine1(DataBaseHandler helper) throws Exception {
        // Creates ARFF file for the instances to be saved to
Context c = AppGlobal.getcontext();

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
                    output.write(String.valueOf(minutesleftfrommidnight) + ',' + String.valueOf(duration) + ',' + Activity + ',' + Activity1  + "\n");

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
        BufferedReader datafile = readDataFile(pat + "/files/output.arff");

        Instances data = new Instances(datafile);
        data.setClassIndex(data.numAttributes() - 2);

        J48 tree = new J48();

        tree.buildClassifier(data);

        ArrayList<TimeAction>TimeAction1 = new ArrayList<TimeAction>();
        Duration = 10;
/*!!!!*/OutputStreamWriter output2 = new OutputStreamWriter(c.openFileOutput("output3", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING));
        double lastactionpr = 0.0;
        for (int i = 0; i< 1440; i+=10){
            //Instance inst1 = new Instance(2);
            data.instance(0).setValue(0,i);
            data.instance(0).setValue(1,Duration);
            data.instance(0).setValue(3, lastactionpr);

            //inst1.setValue(0,i);
            double curentaction = tree.classifyInstance(data.instance(0));

/*!!!!*/    output2.write(String.valueOf(i) + ',' + String.valueOf(Duration) + ',' +  String.valueOf(lastactionpr) + ',' + String.valueOf(lastactionpr) + "\n");


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
                            /*!!!!*/output2.write(PA.Start + ',' + PA.End + ',' + PA.Action + "; \n");
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
                        /*!!!!*/output2.write(PA.Start + ',' + PA.End + ',' + PA.Action + "; \n");

                }
            }
        }


        /*!!!!*/output2.close();

        return PA1;
    }


    public static ArrayList<PeriodAction> GetRoutine(DataBaseHandler helper, Context c, String path) throws Exception {
        // Creates ARFF file for the instances to be saved to

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
        output.write("@ATTRIBUTE Action" + Actions + "\n");
//        output.write("@ATTRIBUTE Action1" + Actions + "\n");
//        output.write("@ATTRIBUTE Action2" + Actions + "\n");
//        output.write("@ATTRIBUTE Location" + Locations + "\n");
//        output.write("@ATTRIBUTE Action3" + Actions + "\n");
        output.write("\n");
        output.write("@DATA \n");

        Cursor cursor2 = helper.getAllRoutine(helper);
        if (cursor2.moveToFirst()) {
            do {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                java.util.Date Start = format.parse(cursor2.getString(3));
                Calendar calendar = Calendar.getInstance();
                //calendar.setTime(Start);
                //StartHours = calendar.get(Calendar.HOUR_OF_DAY);
                //StartMinutes = calendar.get(Calendar.MINUTE);
                Activity = cursor2.getString(1);
                //Location = cursor2.getString(2);
                java.util.Date End = format.parse(cursor2.getString(4));
                //calendar.setTime(End);
                //EndHours = calendar.get(Calendar.HOUR_OF_DAY);
                //EndMinutes = calendar.get(Calendar.MINUTE);
                //CurHours = StartHours;
                //CurMinutes = StartMinutes;
                java.util.Date CurDate = Start;
                while (CurDate.before(End))
                {
                    calendar.setTime(Start);

                    int minutesleftfrommidnight = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
                    //String S = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))+ ':' + String.valueOf(calendar.get(Calendar.MINUTE)) + ',' + Activity + "\n";
                    output.write(String.valueOf(minutesleftfrommidnight) + ',' + Activity + "\n");
                    CurDate.setTime(CurDate.getTime() + 1*60*1000);
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
        BufferedReader datafile = readDataFile(pat + "/files/output.arff");

        Instances data = new Instances(datafile);
        data.setClassIndex(data.numAttributes() - 1);

        J48 tree = new J48();

        tree.buildClassifier(data);

        ArrayList<TimeAction>TimeAction1 = new ArrayList<TimeAction>();

        for (int i = 0; i< 1440; i++){
            //Instance inst1 = new Instance(2);
            data.instance(0).setValue(0,i);
            //inst1.setValue(0,i);
            double nextaction = tree.classifyInstance(data.instance(0));
            //double nextaction = tree.classifyInstance(data.instance(i + 1));
            //String time = String.valueOf(data.instance(i + 1).value(0));
            //int time = (int)data.instance(0).value(0);
            TimeAction T1 = new TimeAction();
            T1.Action = nextaction;
            T1.Time = i;
            TimeAction1.add(T1);
        }

        ArrayList<PeriodAction> PA1 = new ArrayList<PeriodAction>();
        for (int i =0;i<TimeAction1.size();i++) {
            int ind = (int)TimeAction1.get(i).Action;

            if (i==0){
                Action = data.attribute(1).value(ind);
                //PA.Action = data.attribute(1).value(ind);
                int Hours = (int) TimeAction1.get(i).Time / 60;
                int Minutes = TimeAction1.get(i).Time - Hours * 60;
                Start = Integer.toString(Hours) + ":" + Integer.toString(Minutes);
                //PA.Start = Integer.toString(Hours) + ":" + Integer.toString(Minutes);
                //PA1.add(PA);
            }
            else{
                if (TimeAction1.get(i-1).Action != TimeAction1.get(i).Action){
                    //PA.Action = data.attribute(1).value(ind);
                    Action = data.attribute(1).value(ind);
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
                        //PA1.get(i).End = Integer.toString(Hours) + ":" + Integer.toString(Minutes);
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



    public static int GetNextAction(DataBaseHandler helper, Context c, String path) throws Exception {
        // Creates ARFF file for the instances to be saved to

        OutputStreamWriter output = new OutputStreamWriter(c.openFileOutput(FILENAME1, Context.MODE_ENABLE_WRITE_AHEAD_LOGGING));

        String Actions = "{";
        String Locations = "{";
        Cursor cursor = helper.getAllLocations(helper);
        Cursor cursor1 = helper.getAllActions(helper);

        if (cursor.moveToFirst()) {
            do {
                Locations += cursor.getString(3) + ",";
            }
            while (cursor.moveToNext());
            Locations = Locations.substring(0, Locations.length()-1);
            Locations +="}";
        }
        // close cursor
        if (!cursor.isClosed()) {
            cursor.close();
        }
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
        output.write("@RELATION output1 \n");
        output.write("\n");
        output.write("@ATTRIBUTE TimeStamp numeric \n");
        output.write("@ATTRIBUTE Action3" + Actions + "\n");
        output.write("@ATTRIBUTE Action2" + Actions + "\n");
        output.write("@ATTRIBUTE Action1" + Actions + "\n");
        output.write("@ATTRIBUTE Location" + Locations + "\n");
        output.write("@ATTRIBUTE Action" + Actions + "\n");
        output.write("\n");
        output.write("@DATA \n");

        Cursor cursor2 = helper.getAllRoutine(helper);
        if (cursor2.moveToFirst()) {
            int num = 0;
            do {
                //if (cursor2.isLast()){
                //    break;
                //}
                num +=1;
                switch (num)
                {
                    case 1: Activity3 = cursor2.getString(1);
                        break;
                    case 2: Activity2 = cursor2.getString(1);;
                        break;
                    case 3: Activity1 = cursor2.getString(1);;
                        break;
                    default:

                        Activity3 = Activity2;
                        Activity2 = Activity1;
                        Activity1 = Activity;
                        Activity = cursor2.getString(1);
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                        java.util.Date Start = format.parse(cursor2.getString(3));
                        Calendar calendar = Calendar.getInstance();
                        Location = cursor2.getString(2);
                        java.util.Date End = format.parse(cursor2.getString(4));
                        java.util.Date CurDate = Start;
                        while (CurDate.before(End))
                        {
                            calendar.setTime(Start);

                            int minutesleftfrommidnight = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
                            //String S = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))+ ':' + String.valueOf(calendar.get(Calendar.MINUTE)) + ',' + Activity + "\n";
                            output.write(String.valueOf(minutesleftfrommidnight) + ',' + Activity3 + ',' + Activity2 + ','
                                    + Activity1 + ',' + Location + ',' + Activity +"\n");
                            CurDate.setTime(CurDate.getTime() + 1*60*1000);
                        }
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
        BufferedReader datafile = readDataFile(pat + "/files/output1.arff");

        Instances data = new Instances(datafile);
        data.setClassIndex(data.numAttributes() - 1);

        J48 tree = new J48();

        tree.buildClassifier(data);

        Instance inst = data.firstInstance();
        data.delete();

        Calendar calendar = Calendar.getInstance();
        int minutesleftfrommidnight = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        cursor2 = helper.getAllRoutine(helper);
        if (cursor2.moveToLast()){
            inst.setValue(0, minutesleftfrommidnight);
            inst.setValue(3, cursor2.getString(1));
            inst.setValue(4, cursor2.getString(2));
            cursor2.moveToPrevious();
            inst.setValue(2, cursor2.getString(1));
            cursor2.moveToPrevious();
            inst.setValue(1,cursor2.getString(1));
        }
        double nextaction = tree.classifyInstance(inst);
        return (int)nextaction;
    }


}

