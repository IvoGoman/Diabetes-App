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

import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;


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

    public void usage(){
        ArrayList<Prediction.TimeAction> prediction = new ArrayList<Prediction.TimeAction>();
        try {
            prediction = Prediction.GetRoutineAsTimeAction();   //array of length 1440 with time,activity
            HashMap<Integer,List<String>> DayReal = new HashMap<>();
            for (int minute=1;minute<1440;minute++){
                DayReal.put(minute,new ArrayList<String>());
            }
            Cursor cursor2 = AppGlobal.getHandler().getAllRoutine();
            int p = cursor2.getCount();
            if (cursor2.moveToFirst()) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                int duration=0;
                do {
                    java.util.Date Start = format.parse(cursor2.getString(3));
                    Calendar calendar = Calendar.getInstance();
                    String ActivityCur = cursor2.getString(1);
                    java.util.Date End = format.parse(cursor2.getString(4));
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
                        DayReal.get(minutesleftfrommidnight).add(ActivityCur);  //hashmap of length 1440 with time as a key,array of actions performed at this minute
                        CurDate.setTime(CurDate.getTime() + 1 * 60 * 1000);
                    }
                }
                while (cursor2.moveToNext());
            }
            // close cursor
            if (!cursor2.isClosed()) {
                cursor2.close();
            }

            Accuracy(prediction,DayReal);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    float Accuracy(ArrayList<Prediction.TimeAction> prediction, HashMap<Integer,List<String>> DayReal){
        int Acc=0;
        int count=0;
        for (int minute=0;minute<prediction.size();minute++){
            for (int i=0;i<DayReal.get(minute).size()-1;i++){
                    if (AppGlobal.getHandler().getSubactivity(( (int) prediction.get(minute).Action))==DayReal.get(minute).get(i)){
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
}