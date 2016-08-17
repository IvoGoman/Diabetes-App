package uni.mannheim.teamproject.diabetesplaner.DataMining;

/**
 * Created by leonidgunko on 17/08/16.
 */

import java.util.ArrayList;
import java.util.Calendar;

import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;


public class Evaluation {


    ArrayList<Integer> Creator(ArrayList<ActivityItem> Routine){
        ArrayList<Integer> result = new ArrayList<Integer>();

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

                result.add(activity.getActivityId());

                CurDate.setTime(CurDate.getTime() + 1 * 60 * 1000);
            }
        }
        return result;
    }


    float Accuracy(ArrayList<Integer> Predicted, ArrayList<Integer> Real ){
        int Acc=0;
        for (int i=0;i<Predicted.size();i++){
            if (Predicted.get(i)==Real.get(i)){
                Acc++;
            }
        }
        return (float)Acc/Predicted.size();
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