package uni.mannheim.teamproject.diabetesplaner.DataMining;

/**
 * Created by leonidgunko on 17/08/16.
 */

import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import uni.mannheim.teamproject.diabetesplaner.DataMining.SequentialPattern.GSP_Prediction;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;


public class Evaluation {

    ArrayList<Prediction.TimeAction> CreatorRealRoutine(ArrayList<ActivityItem> Routine){
        ArrayList<Prediction.TimeAction> result = new ArrayList<Prediction.TimeAction>();

        int duration=0;
        for (ActivityItem activity:Routine) {
            java.util.Date Start = activity.getStarttime();
            Calendar calendar = Calendar.getInstance();
            int ActivityCur = activity.getActivityId();
            java.util.Date End = activity.getEndtime();
            java.util.Date CurDate = Start;
            int num = 0;
            while (CurDate.before(End)) {
                num += 1;
                if (num == 2) {
                    duration = 0;
                }
                calendar.setTime(Start);

                int minutesleftfrommidnight = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
                duration += 1;

                result.add(new Prediction.TimeAction(minutesleftfrommidnight,(double)ActivityCur));

                CurDate.setTime(CurDate.getTime() + 1 * 60 * 1000);
            }
        }
        int i=0;
        while (result.get(i).Time!=0){
            result.remove(i);
            i++;
        }
        i = result.size()-1;
        while (result.get(i).Time!=1440){
            result.remove(i);
            i--;
        }
        return result;
    }

    ArrayList<Prediction.TimeAction> CreatorPredictedRoutine(ArrayList<ActivityItem> RoutinePredicted, ArrayList<ActivityItem> RoutineReal){
        int days = RoutineReal.size()/RoutinePredicted.size();

        while(RoutinePredicted.size()!=RoutineReal.size()) {
            RoutinePredicted.add(RoutinePredicted.get(RoutinePredicted.size()-1440));
        }

        ArrayList<Prediction.TimeAction> result = new ArrayList<Prediction.TimeAction>();
        int duration=0;
        for (ActivityItem activity:RoutinePredicted) {
            java.util.Date Start = activity.getStarttime();
            Calendar calendar = Calendar.getInstance();
            int ActivityCur = activity.getActivityId();
            java.util.Date End = activity.getEndtime();
            java.util.Date CurDate = Start;
            int num = 0;
            while (CurDate.before(End)) {
                num += 1;
                if (num == 2) {
                    duration = 0;
                }
                calendar.setTime(Start);

                int minutesleftfrommidnight = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
                duration += 1;

                result.add(new Prediction.TimeAction(minutesleftfrommidnight,(double)ActivityCur));

                CurDate.setTime(CurDate.getTime() + 1 * 60 * 1000);
            }
        }

        return result;
    }

    public static float usageGsp(ArrayList<ArrayList<ActivityItem>> train){
        ArrayList<ArrayList<ActivityItem>>train1 = new ArrayList<ArrayList<ActivityItem>>(train.subList(0,train.size()/2));
        ArrayList<ArrayList<ActivityItem>>train2 = new ArrayList<ArrayList<ActivityItem>>(train.subList(train.size()/2,train.size()));
        ArrayList<ActivityItem> gsp1 = GSP_Prediction.makeGSPPrediction(train1, 0.2f);
        ArrayList<ActivityItem> gsp2 = GSP_Prediction.makeGSPPrediction(train2, 0.2f);
        float accuracy1 = AccuracyGsp(train1,gsp1);
        float accuracy2 = AccuracyGsp(train2,gsp2);
        return (accuracy1+accuracy2)/2;
    }

    public static void usageTree() throws Exception {
        HashMap<Integer, List<Double>> hashReal = new HashMap<>();
        FastVector Activities = new FastVector();
        String ActivityCur;

        for (int i=0;i<1440;i++) {
            List<Double> l = new ArrayList<>();
            hashReal.put(i,l);
        }
        Instances train = Prediction.getInstances();

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

        Attribute Activity = new Attribute("Activity", Activities);
        Attribute Activity1 = new Attribute("Activity1", Activities);
        Attribute Time = new Attribute("Time");
        Attribute Dur = new Attribute("Duration");

        FastVector attinfo = new FastVector(4);
        attinfo.addElement(Time);
        attinfo.addElement(Dur);
        attinfo.addElement(Activity);
        attinfo.addElement(Activity1);
        Instances train1 = new Instances("output", attinfo, 1);
        for (int i=0;i<train.numInstances()/2;i++){
            train1.add(train.instance(i));
        }
        ArrayList<Prediction.TimeAction> Pred = Prediction.GetRoutineAsTimeAction(train1);
        for (int i=0;i<train1.numInstances();i++){
            int minute = (int)train.instance(i).value(0);
            double action = (int)train.instance(i).value(3);
            if (!hashReal.get(minute).contains(action)) {
                List<Double> actions = hashReal.get(minute);
                actions.add(action);
                hashReal.put(minute,actions);
            }
        }
        float accuracy1 = Accuracy(Pred,hashReal);

        HashMap<Integer, List<Double>> hashReal2 = new HashMap<>();
        Instances train2 = new Instances("output", attinfo, 1);
        for (int i=train.numInstances()/2;i<train.numInstances();i++){
            train2.add(train.instance(i));
        }
        ArrayList<Prediction.TimeAction> Pred2 = Prediction.GetRoutineAsTimeAction(train2);

        for (int i=0;i<1440;i++) {
            List<Double> l = new ArrayList<>();
            hashReal.put(i,l);
        }
        for (int i=0;i<train2.numInstances();i++){
            int minute = (int)train.instance(i).value(0);
            double action = (int)train.instance(i).value(3);
            if (!hashReal.get(minute).contains(action)) {
                List<Double> actions = hashReal.get(minute);
                actions.add(action);
                hashReal.put(minute,actions);
            }
        }
        float accuracy2 = Accuracy(Pred2,hashReal);
    }



//    public static void usage(){
//        //float f = Accuracy(rtd,gsp);
//        ArrayList<Prediction.TimeAction> prediction = new ArrayList<Prediction.TimeAction>();
//        try {
//            prediction = GetRoutineAsTimeAction();   //array of length 1440 with time,activity
//            ArrayList<Prediction.PeriodAction> pred = Prediction.GetRoutine1();
//            HashMap<Integer,List<String>> DayReal = new HashMap<>();
//            for (int minute=1;minute<1440;minute++){
//                ArrayList<String> a = new ArrayList<>();
//                DayReal.put(minute,a);
//            }
//            Cursor cursor2 = AppGlobal.getHandler().getAllRoutine();
//            int p = cursor2.getCount();
//            if (cursor2.moveToFirst()) {
//                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
//                int duration=0;
//                do {
//                    java.util.Date Start = format.parse(cursor2.getString(cursor2.getColumnIndex("Start")));
//                    Calendar calendar = Calendar.getInstance();
//                    String ActivityCur = cursor2.getString(cursor2.getColumnIndex("SubActivity"));
//                    java.util.Date End = format.parse(cursor2.getString(4));
//                    java.util.Date CurDate = Start;
//                    int num = 0;
//                    while (CurDate.before(End)) {
//                        num += 1;
//                        if (num == 2) {
//                            duration = 0;
//                        }
//                        calendar.setTime(Start);
//
//                        int minutesleftfrommidnight = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
//                        duration += 1;
//                        DayReal.get(minutesleftfrommidnight).add(ActivityCur);  //hashmap of length 1440 with time as a key,array of actions performed at this minute
//                        CurDate.setTime(CurDate.getTime() + 1 * 60 * 1000);
//                    }
//                }
//                while (cursor2.moveToNext());
//            }
//            // close cursor
//            if (!cursor2.isClosed()) {
//                cursor2.close();
//            }
//
//            Accuracy(prediction,DayReal);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    static float AccuracyGsp(ArrayList<ArrayList<ActivityItem>> train, ArrayList<ActivityItem> gsp){
        int acc=0;
        int count=0;
        HashMap<Integer,Integer> gspRes = new HashMap<>();
        HashMap<Integer,Integer> realRes = new HashMap<>();

        for (ActivityItem gsp1:gsp){
            HashMap<Integer,Integer> hashGsp = activityItemToHashMap(gsp1);
            for (Integer key: hashGsp.keySet()){
                gspRes.put(key,hashGsp.get(key));
            }
        }

        for (ArrayList<ActivityItem> day: train){
            for (ActivityItem activityItem: day){
                HashMap<Integer,Integer> hashReal = activityItemToHashMap(activityItem);
                for (Integer key: hashReal.keySet()){
                    realRes.put(key,hashReal.get(key));
                }
            }
            for (Integer key: realRes.keySet()){
                count++;
                if (realRes.get(key)==gspRes.get(key)){
                    acc++;
                }
            }
        }

        return (float)acc/count;
    }


    static float Accuracy(ArrayList<Prediction.TimeAction> prediction, HashMap<Integer,List<Double>> DayReal){
        int Acc=0;
        int count=0;
        for (int minute=0;minute<prediction.size();minute++){
            for (int i=0;i<DayReal.get(minute).size()-1;i++){
                    if (prediction.get(minute).Action==DayReal.get(minute).get(i)){
                        Acc++;
                    }
                count++;
            }

        }
        return (float)Acc/count;
    }

    ArrayList<Double> Precision(ArrayList<Integer> Actions, ArrayList<Integer> Predicted,ArrayList<Integer> Real ){
        ArrayList<Double> Precisions = new ArrayList<Double>(Actions.size());
        int Acc=0;
        ArrayList<Integer> predicted = new ArrayList(Actions.size());
        ArrayList<Integer> positivepredicted = new ArrayList(Actions.size());

        for (int i=0;i<predicted.size();i++){
            predicted.set(i, 0);
            positivepredicted.set(i, 0);
        }

        for (int i=0;i<Predicted.size();i++){
            for (int j=0;j<Actions.size();j++){
                if (Predicted.get(i)==Actions.get(j)){
                    if (Predicted.get(i)==Real.get(i)){
                        positivepredicted.set(j, positivepredicted.get(j)+1);
                        predicted.set(j, predicted.get(j)+1);
                    }
                    else{
                        predicted.set(j, predicted.get(j)+1);
                    }
                }
            }
        }

        for (int i=0;i<Precisions.size();i++){
            Precisions.set(i, (double)positivepredicted.get(i)/predicted.get(i));
        }

        return Precisions;
    }

    ArrayList<Double> Recall(ArrayList<Integer> Actions, ArrayList<Integer> Predicted,ArrayList<Integer> Real ){
        ArrayList<Double> Recalls = new ArrayList<Double>(Actions.size());
        int Acc=0;
        ArrayList<Integer> predicted = new ArrayList(Actions.size());
        ArrayList<Integer> real = new ArrayList(Actions.size());

        for (int i=0;i<predicted.size();i++){
            predicted.set(i, 0);
            real.set(i, 0);
        }

        for (int i=0;i<Real.size();i++){
            for (int j=0;j<Actions.size();j++){
                if (Real.get(i)==Actions.get(j)){
                    real.set(j, real.get(j)+1);
                }
                if (Predicted.get(i)==Actions.get(j)){
                    if (Predicted.get(i)==Real.get(j)){
                        predicted.set(j, predicted.get(j)+1);
                    }
                }
            }
        }

        for (int i=0;i<Recalls.size();i++){
            Recalls.set(i, (double)predicted.get(i)/real.get(i));
        }

        return Recalls;
    }

    Double Fmeasure(ArrayList<Double> Precisions, ArrayList<Double> Recalls){
        Double Fmeasure=(double) 0;
        for (int i=0;i<Precisions.size();i++){
            Fmeasure+=(2*Precisions.get(i)*Recalls.get(i))/(Precisions.get(i)+Recalls.get(i));
        }
        return Fmeasure/Precisions.size();
    }

    static HashMap<Integer,Integer> activityItemToHashMap(ActivityItem activityItem){
        HashMap<Integer,Integer> result = new HashMap<Integer,Integer>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        int duration = 0;
        java.util.Date start = activityItem.getStarttime();
        Calendar calendar = Calendar.getInstance();
        int activityCur = activityItem.getSubactivityId();
        java.util.Date end = activityItem.getEndtime();
        java.util.Date curDate = start;
        int num = 0;
        while (curDate.before(end)) {
            num += 1;
            if (num == 2) {
                duration = 0;
            }
            calendar.setTime(curDate);

            int minutesleftfrommidnight = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
            result.put(minutesleftfrommidnight,activityCur);
        }
        return result;
    }
}