package uni.mannheim.teamproject.diabetesplaner.DataMining;

/**
 * Created by leonidgunko on 17/08/16.
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import uni.mannheim.teamproject.diabetesplaner.DataMining.SequentialPattern.GSP_Prediction;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;


public class Evaluation {

    public static float usageGsp(ArrayList<ArrayList<ActivityItem>> train){
        ArrayList<ArrayList<ActivityItem>>train1 = new ArrayList<ArrayList<ActivityItem>>(train.subList(0,train.size()/2));
        ArrayList<ArrayList<ActivityItem>>train2 = new ArrayList<ArrayList<ActivityItem>>(train.subList(train.size()/2,train.size()));
        ArrayList<ActivityItem> gsp1 = GSP_Prediction.makeGSPPrediction(train1, 0.2f);
        ArrayList<ActivityItem> gsp2 = GSP_Prediction.makeGSPPrediction(train2, 0.2f);
        float accuracy1 = AccuracyGsp(train1,gsp1);
        float accuracy2 = AccuracyGsp(train2,gsp2);
        return (accuracy1+accuracy2)/2;
     }

    public static float usageTree(ArrayList<ArrayList<ActivityItem>> train) throws Exception {
        ArrayList<ArrayList<ActivityItem>> train3 = new ArrayList<>();
        ArrayList<ArrayList<ActivityItem>>train1 = new ArrayList<ArrayList<ActivityItem>>(train.subList(0,train.size()/2));
        ArrayList<ArrayList<ActivityItem>>train2 = new ArrayList<ArrayList<ActivityItem>>(train.subList(train.size()/2,train.size()));

        ArrayList<ActivityItem> tree1 = Prediction.GetRoutineAsAI(train1);
        ArrayList<ActivityItem> tree2 = Prediction.GetRoutineAsAI(train2);
        float accuracy1 = AccuracyGsp(train1,tree1);
        float accuracy2 = AccuracyGsp(train2,tree2);
        return (accuracy1+accuracy2)/2;
    }

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
        java.util.Date start = new Date();
        start.setTime(activityItem.getStarttime().getTime());
        Calendar calendar = Calendar.getInstance();
        int activityCur = activityItem.getSubactivityId();
        java.util.Date end = new Date();
        end.setTime(activityItem.getEndtime().getTime());
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
            curDate.setTime(curDate.getTime() + 1 * 60 * 1000);
        }
        return result;
    }
}