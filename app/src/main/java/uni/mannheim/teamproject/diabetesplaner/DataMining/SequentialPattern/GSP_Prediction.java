package uni.mannheim.teamproject.diabetesplaner.DataMining.SequentialPattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import uni.mannheim.teamproject.diabetesplaner.Utility.Util;

/**
 * @author Stefan
 */
public class GSP_Prediction {

	public static void main(String[] args) {

//		ArrayList<String[]> data = Util.read("C:\\Users\\Stefan\\Documents\\Uni Mannheim\\Master\\Team Project\\activityDataCompleteWithCases.csv");
		ArrayList<String[]> data = Util.read("C:\\Users\\Stefan\\Documents\\Uni Mannheim\\Master\\Team Project\\Stefan_Data_27.07.16\\ActivityData_Cases.csv");

		GSP gsp = new GSP(data, 0, 2, 3, 4, 5);
		float minsup = gsp.getSupportXOccurance(1);
		HashMap<Sequence, Float> result = gsp.findFrequentPatterns(minsup);


		System.out.println("Frequent Sequences: ");
		for(Map.Entry<Sequence, Float> m : result.entrySet()){
			Sequence s = m.getKey();
			System.out.print(s.toString());
			System.out.print(" support: " + m.getValue());
			System.out.println();
		}

		String mostFreqStart = findMostFreqStartActivity(gsp.getCompleteSeqs());
		String mostFreqEnd = findMostFreqEndActivity(gsp.getCompleteSeqs());

		System.out.println("Most frequent start: " + mostFreqStart);
		System.out.println("Most frequent end: " + mostFreqEnd);


		ArrayList<String> drList = createDailyRoutine(mostFreqStart, mostFreqEnd, result);
		Sequence dailyRoutine = new Sequence(drList);

		HashMap<String, Long> avgTimes = GSP_Util.getAvgTime(data);

		for(Entry<String, Long> m: avgTimes.entrySet()){
			System.out.println(m.getKey() + " " + m.getValue());
		}

		System.out.println("Daily Routine: ");

		int sum = 0;
		for(int i=0; i<dailyRoutine.size(); i++){
			String act = (String)dailyRoutine.get(i);
			sum += (int)(avgTimes.get(act)/1000/60);
		}
		float ratio = 1440f/(float)sum;
		float s = 0;

		for(int i=0; i<dailyRoutine.size(); i++){
			String act = (String)dailyRoutine.get(i);
			//Tag: 1440
			float dur = (avgTimes.get(act)/1000/60)*ratio;
			int min = (int) (dur%60);
			int hour = (int) (dur/60);

			System.out.println(act + " duration: " + hour + " h " + min + " min ");
			s += ((avgTimes.get(act)/1000/60)*ratio);
		}
		System.out.println("Sum: " + s + " should match 1440");
		
	}
	
	public static ArrayList<String> createDailyRoutine(String mfStart, String mfEnd, HashMap<Sequence, Float> seqPat){
		ArrayList<String> dr = new ArrayList<>();
		String latest = mfStart;
		dr.add(mfStart);
		
		while(true){
			Sequence seq = null;
			for(Entry<Sequence, Float> m : seqPat.entrySet()){
				seq = m.getKey();
				if(seq.get(0).equals(latest)){
					for(int i=1; i<m.getKey().size(); i++){
						dr.add((String)seq.get(i));
					}					
					break;
				}
			}
			if(seq != null){
				seqPat.remove(seq);
			}
			latest = dr.get(dr.size()-1);
			if(latest.equals(mfEnd)){
				return dr;
			}
		}
	}
	
	/**
	 * finds most frequent start activity
	 * @param s
	 * @return
	 * @author Stefan
	 */
	public static String findMostFreqStartActivity(ArrayList<Sequence> s){
		HashMap<String, Integer> count = new HashMap<>();
		
		for(int i=0; i<s.size(); i++){
			String start = (String)s.get(i).get(0);
			if(count.containsKey(start)){
				count.put(start, count.get(start)+ 1);
			}else{
				count.put(start, 1);
			}
		}
		Entry<String, Integer> e = GSP_Util.getMaxValue(count);
		return e.getKey();
	}
	
	/**
	 * finds most frequent end activity
	 * @param s
	 * @return
	 * @author Stefan
	 */
	public static String findMostFreqEndActivity(ArrayList<Sequence> s){
		HashMap<String, Integer> count = new HashMap<>();
		
		for(int i=0; i<s.size(); i++){
			String start = (String)s.get(i).get(s.get(i).size()-1);
			if(count.containsKey(start)){
				count.put(start, count.get(start)+ 1);
			}else{
				count.put(start, 1);
			}
		}
		Entry<String, Integer> e = GSP_Util.getMaxValue(count);
		return e.getKey();
	}
}
