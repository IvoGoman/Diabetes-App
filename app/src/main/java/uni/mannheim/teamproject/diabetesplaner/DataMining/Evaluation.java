package uni.mannheim.teamproject.diabetesplaner.DataMining;

/**
 * Created by leonidgunko on 17/08/16.
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import uni.mannheim.teamproject.diabetesplaner.DataMining.SequentialPattern.GSP_Prediction;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.ProcessMining.HeuristicsMiner.HeuristicsMinerImplementation;


public class Evaluation {

    public static double usageGspK(ArrayList<ArrayList<ActivityItem>> train, int k){
        double accuracy =0;
        if (k>train.size()){
            k=train.size();
        }
        for (int i=0;i<k;i++) {
            ArrayList<ArrayList<ActivityItem>> train1 = new ArrayList<ArrayList<ActivityItem>>(train.subList(i * train.size() / k, (i + 1) * train.size() / k));
            ArrayList<ActivityItem> tree1 = GSP_Prediction.makeGSPPrediction(train1, 0.2f);
            double accuracy1 = Accuracy(train1, tree1);
            accuracy += accuracy1;
        }
        return accuracy/k;
     }

    public static double usageTreeK(ArrayList<ArrayList<ActivityItem>> train, int k) throws Exception {
        double accuracy =0;
        if (k>train.size()){
            k=train.size();
        }
        for (int i=0;i<k;i++){
            ArrayList<ArrayList<ActivityItem>>train1 = new ArrayList<ArrayList<ActivityItem>>(train.subList(i*train.size()/k,(i+1)*train.size()/k));
            ArrayList<ActivityItem> tree1 = Prediction.GetRoutineAsAI(train1);
            double accuracy1 = Accuracy(train1,tree1);
            accuracy+=accuracy1;
        }
        return accuracy/k;
    }

    public static double usageFMK(ArrayList<ArrayList<ActivityItem>> train, int k) throws Exception {
        double accuracy =0;
        if (k>train.size()){
            k=train.size();
        }
        for (int i=0;i<k;i++){
            ArrayList<ArrayList<ActivityItem>>train1 = new ArrayList<ArrayList<ActivityItem>>(train.subList(i*train.size()/k,(i+1)*train.size()/k));
            FuzzyModel model = new FuzzyModel(train1, false);
            ArrayList<ActivityItem> fM = model.makeFuzzyMinerPrediction();
            double accuracy1 = Accuracy(train1,fM);
            accuracy+=accuracy1;
        }
        return accuracy/k;
    }

    public static double usageHMK(ArrayList<ArrayList<ActivityItem>> train, int k) throws Exception {
        double accuracy =0;
        if (k>train.size()){
            k=train.size();
        }
        for (int i=0;i<k;i++){
            ArrayList<ArrayList<ActivityItem>>train1 = new ArrayList<ArrayList<ActivityItem>>(train.subList(i*train.size()/k,(i+1)*train.size()/k));
            HeuristicsMinerImplementation HMmodel = new HeuristicsMinerImplementation();
            ArrayList<ActivityItem> fM = HMmodel.runHeuristicsMiner(train1);
            double accuracy1 = Accuracy(train1,fM);
            accuracy+=accuracy1;
        }
        return accuracy/k;
    }

    static double Accuracy(ArrayList<ArrayList<ActivityItem>> train, ArrayList<ActivityItem> gsp){
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
        return (double)acc/count;
    }

    static double AccuracyFlow(ArrayList<ArrayList<ActivityItem>> train, ArrayList<ActivityItem> prediction){
        HashMap<Integer,ArrayList<Integer>> real = new HashMap<>(); //MAP<Activity, List of following activities>
        HashMap<Integer,ArrayList<Integer>> predicted = new HashMap<>();

        for (ArrayList<ActivityItem> day: train){
            for (int i=0;i<day.size()-1;i++){
                int idSubActivity = day.get(i).getSubactivityId();
                int idNextSubActivity = day.get(i+1).getSubactivityId();
                if (real.keySet().contains(day.get(i).getSubactivityId())){
                    real.get(idSubActivity).add(idNextSubActivity);
                }
                else{
                    real.put(idSubActivity,new ArrayList<Integer>());
                }
            }
        }

        for (int i=0;i<prediction.size()-1;i++){
            int idSubActivity = prediction.get(i).getSubactivityId();
            int idNextSubActivity = prediction.get(i+1).getSubactivityId();
            if (predicted.keySet().contains(prediction.get(i).getSubactivityId())){
                predicted.get(idSubActivity).add(idNextSubActivity);
            }
            else{
                predicted.put(idSubActivity,new ArrayList<Integer>());
                predicted.get(idSubActivity).add(idNextSubActivity);
            }
        }

        HashMap<Integer,HashMap<Integer,Double>> real1 = new HashMap<>(); //MAP<Activity, MAP<following activity, confidence>
        HashMap<Integer,HashMap<Integer,Double>> predicted1 = new HashMap<>();

        for (int subactivity:real.keySet()){
            real1.put(subactivity, new HashMap<Integer,Double>());
            for (int followingSubActivity:real.get(subactivity)){
                real1.get(subactivity).put(followingSubActivity,0.0);
            }
        }

        for (int subactivity:real.keySet()){
            for (int followingSubactivity:real.get(subactivity)){
                for (int followingSubactivity1:real1.get(subactivity).keySet()){
                    if (followingSubactivity == followingSubactivity1){
                        real1.get(subactivity).put(followingSubactivity,real1.get(subactivity).get(followingSubactivity) +1.0/real.get(subactivity).size());
                    }
                }
            }
        }

        for (int subactivity:predicted.keySet()){
            predicted1.put(subactivity, new HashMap<Integer,Double>());
            for (int followingSubActivity:predicted.get(subactivity)){
                predicted1.get(subactivity).put(followingSubActivity,0.0);
            }
        }

        for (int subactivity:predicted.keySet()){
            for (int followingSubactivity:predicted.get(subactivity)){
                for (int followingSubactivity1:predicted1.get(subactivity).keySet()){
                    if (followingSubactivity == followingSubactivity1){
                        predicted1.get(subactivity).put(followingSubactivity,predicted1.get(subactivity).get(followingSubactivity) +1.0/predicted1.get(subactivity).keySet().size());
                    }
                }
            }
        }

        double error =0.0;
        for (int int1:predicted1.keySet()) {
            HashMap<Integer,Double> following = predicted1.get(int1);   //Map of following activities with confidence
            for (int subactivity : following.keySet()){
                double confidencepred = predicted1.get(int1).get(subactivity);
                double confidencereal = real1.get(int1).get(subactivity);
                error += Math.abs(confidencepred-confidencereal);
            }
        }

        error = error/predicted1.keySet().size();
        return 1-error;
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