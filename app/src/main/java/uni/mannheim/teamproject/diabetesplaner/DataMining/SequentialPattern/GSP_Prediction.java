package uni.mannheim.teamproject.diabetesplaner.DataMining.SequentialPattern;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;

/**
 * @author Stefan
 */
public class GSP_Prediction {

	private static GSP gsp;
	private static float minsup;

	/**
	 * Does the actual GSP prediction.
	 * First of all the GSP is executed to find the frequent sequential patterns,
	 * then the path from the most frequent start activity of a day to the most frequent end activity of a day is created.
	 * Times are based on the average times (AM and PM) and normalized to sum to one day.
	 * Finally the result is converted into ActivityItems and returned
	 * @param train training data in ArrayList<ArrayList<ActivityItem>> format
	 * @param minsup minsup that should be used
     * @return daily routine in ArrayList<ActivityItem> format
	 * @author Stefan 06.09.2016
     */
	public static ArrayList<ActivityItem> makeGSPPrediction(ArrayList<ArrayList<ActivityItem>> train, float minsup){
		GSP_Prediction.minsup = minsup;
		ArrayList<ActivityItem> aiRoutine = new ArrayList<>();

		try {
			gsp = new GSP(train);
			HashMap<Sequence, Float> result = gsp.findFrequentPatterns(minsup);

			//get most frequent start and end activities
			String mostFreqStart = GSP_Prediction.findMostFreqStartActivity(gsp.getCompleteSeqs());
			String mostFreqEnd = GSP_Prediction.findMostFreqEndActivity(gsp.getCompleteSeqs());

			//create a path from start of a day to the end of the day
			ArrayList<String> drList = GSP_Prediction.createDailyRoutine(mostFreqStart, mostFreqEnd, result);
			Sequence dailyRoutine = new Sequence(drList);

			HashMap<String, Long> avgTimes = GSP_Util.getAvgTime(train, true);

//			for (Map.Entry<String, Long> entry :avgTimes.entrySet()) {
//				String key = entry.getKey();
//				Long value = entry.getValue();
//				Log.d("GSP", key + ": " + value);
//			}

			//create list with the average times for each activity
			//sum up times
			int sum = 0;
			ArrayList<Long> times = new ArrayList<>();
			for (int i = 0; i < drList.size(); i++) {
				Long avgTime = avgTimes.get(drList.get(i));
				times.add(avgTime);
				sum += (int)(avgTime/1000/60);
			}

			//normalize times to fit a day
			float ratio = 1440f / (float) (sum);

			ArrayList<Float> mods = new ArrayList<>();
//		ArrayList<Float> floatTimes = new ArrayList<>();
			for (int i = 0; i < times.size(); i++) {
				times.set(i, (long)((times.get(i) / 1000 / 60) * ratio));
				mods.add(((((float)times.get(i) / 1000f / 60f) * ratio)) % 1f);
			}

			while (GSP_Util.sumUpList(times) != 1440) {
				int maxIndex = GSP_Util.findMax(mods);
				times.set(maxIndex, times.get(maxIndex) + 1);
				mods.set(maxIndex, 0f);
			}

			//create the actual daily routine where an activity is represented as an ActivityItem
			int minOfDay = 0;
			for (int i = 0; i < drList.size(); i++) {
				String[] params = drList.get(i).split("_");
				Integer activityId = Integer.parseInt(params[0]);
				Integer subactivityId = null;
				try {
					subactivityId = Integer.parseInt(params[1]);
				} catch (NumberFormatException e) {
					subactivityId = null;
				}
				int duration = times.get(i) != null ? times.get(i).intValue() : 0;

				Date starttime = TimeUtils.getDate(TimeUtils.minutesOfDayToTimestamp(minOfDay));
				Date endtime = TimeUtils.getDate(TimeUtils.minutesOfDayToTimestamp(minOfDay + duration - 1));
				minOfDay += duration;

				ActivityItem item = new ActivityItem(activityId, subactivityId, starttime, endtime);
				aiRoutine.add(item);
			}

		}catch(Exception e){
			e.printStackTrace();
			Log.e("GSP_Prediction", "makeGSPPrediction(): " + e.getLocalizedMessage());
			throw e;
		}
		return aiRoutine;
	}


	/**
	 * runs through the map with sequences and selects the path with the maximum support
	 * @param mfStart
	 * @param mfEnd
	 * @param seqPat
	 * @return arrayList with activities in the right order
	 * @author Stefan
	 */
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

			if(seqPat.size() == 0){
				minsup = minsup-0.05f;
				if(minsup >= 0) {
					seqPat = gsp.findFrequentPatterns(minsup);
					dr = new ArrayList<>();
					dr.add(mfStart);
					latest = mfStart;
					seq = null;
				}
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
