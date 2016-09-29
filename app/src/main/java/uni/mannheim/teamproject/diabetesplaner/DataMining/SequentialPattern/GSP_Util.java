package uni.mannheim.teamproject.diabetesplaner.DataMining.SequentialPattern;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;

/**
 * @author Stefan
 */
public class GSP_Util {

	/**
	 * prints HashMap to console
	 * @param map
	 * @author Stefan
	 */
	public static void printMap(HashMap<Sequence, Float> map){
		for(Entry<Sequence, Float> m : map.entrySet()){
			System.out.println(m.getKey().toString() + ": " + m.getValue());
		}
	}

	/**
	 * prints HashMap to console
	 * @param map
	 * @author Stefan
	 */
	public static void printMapBoolean(HashMap<Sequence, Boolean> map){
		for(Entry<Sequence, Boolean> m : map.entrySet()){
			System.out.println(m.getKey().toString() + ": " + m.getValue());
		}
	}

	/**
	 * prints ArrayList to console
	 * @param list
	 * @author Stefan
	 */
	public static void printList(ArrayList<Sequence> list){
		for(int i=0; i<list.size(); i++){
			System.out.println(list.get(i));
		}
	}


	/**
	 * Sorts a HashMap by value (desc or asc)
	 * @param map
	 * @param desc if true sorts in descending otherwise in ascending order
	 * @return
	 * @author Stefan
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, final boolean desc) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				if(desc){
					return (o2.getValue()).compareTo(o1.getValue());
				}else{
					return (o1.getValue()).compareTo(o2.getValue());
				}
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list)
		{
			result.put( entry.getKey(), entry.getValue() );
		}
		return result;
	}


	/**
	 * returns the max value in a HashMap
	 * @param map
	 * @return
	 * @author Stefan
	 */
	public static Entry<String, Integer> getMaxValue(HashMap<String, Integer> map ){
		Map.Entry<String, Integer> maxEntry = null;

		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
				maxEntry = entry;
			}
		}
		return maxEntry;
	}

	/**
	 * Returns average time of an activity subactivity combination.
	 * Excludes outliers with Interquartile Range if removeOutliers is set.
	 * @param data
	 * @param removeOutliers true if outlier detection should be performed, false otherwise
	 * @return
	 * @author Stefan 27.07.2016
	 * edited 21.09.2016: outlier detection added
	 */
	public static HashMap<String, Long> getAvgTime(ArrayList<ArrayList<ActivityItem>> data, boolean removeOutliers){
		HashMap<String, Long> sum = new HashMap<>();
		HashMap<String, Integer> count = new HashMap<>();

		//list that collects values
		HashMap<String, ArrayList<Long>> values = new HashMap<>();
		ArrayList<String[]> distr = new ArrayList<>();
		int distrCount = 0;
		for(int i=0; i<data.size(); i++){
			for(int j=0; j<data.get(i).size(); j++) {
				ActivityItem item = data.get(i).get(j);
				long starttime = item.getStarttime().getTime();
				long endtime = item.getEndtime().getTime();

				long diff = endtime - starttime;

				Timestamp stamp = new Timestamp(starttime);
				Date date = new Date(stamp.getTime());
				SimpleDateFormat sdf = new SimpleDateFormat("a");
				String amPm = sdf.format(date);

				String key;
				if (amPm.equals("AM") || amPm.equals("vorm.")) {
					key = item.getActivityId() + "_" + item.getSubactivityId() + "_AM";
				} else {
					key = item.getActivityId() + "_" + item.getSubactivityId() + "_PM";
				}

				//fill list <name, list with durations>
				if (!values.containsKey(key)) {
					ArrayList<Long> valI = new ArrayList<Long>();
					valI.add(diff);
					values.put(key, valI);
				} else {
					ArrayList<Long> valI = values.get(key);
					valI.add(diff);
					values.put(key, valI);
				}
			}
		}

		if(removeOutliers){
			//perform outlier detection
			for(Entry<String, ArrayList<Long>> v : values.entrySet()){
				String key = v.getKey();
				ArrayList<Long> value = v.getValue();
				//only remove outliers if the activity occurs very often i.e. >= 90% of number of logged days
				// (Activity can occure more than once a day)
				if(value.size() >= (float)values.size()*0.9f){
					v.setValue(removeOutlierWithInterquartileRange(value));
//					v.setValue(removeOutlierWithMAD(value, 4));
				}
			}
		}

		//calculates the average
		for(Entry<String, ArrayList<Long>> v : values.entrySet()){
			String key = v.getKey();
			ArrayList<Long> value = v.getValue();

			sum.put(key, (long)sumUpList(value)/value.size());
		}

		return sum;
	}

	/**
	 * removes outliers outside the Interquartile Range
	 * @param list
	 * @return
	 * @author Stefan 21.09.2016
	 */
	public static ArrayList<Long> removeOutlierWithInterquartileRange(ArrayList<Long> list){
		DescriptiveStatistics stats = new DescriptiveStatistics();
		for(int i=0; i<list.size(); i++){
			stats.addValue(list.get(i));
		}
		double median = stats.getPercentile(50);
		double iqr = stats.getPercentile(75) - stats.getPercentile(25);

		double upperBound = median+1.5d*iqr;
		double lowerBound = median-1.5d*iqr;

		for(int i=0; i<list.size(); i++){
			if(!(list.get(i) >= lowerBound && list.get(i) <= upperBound)){
				list.remove(i);
				i--;
			}
		}
		return list;
	}

	/**
	 * outlier detection based on distance to median aboslut deviation
	 * @param list
	 * @param k median +/- k*MAD
     * @return list with outliers removed
	 * @author Stefan 29.09.2016
     */
	public static ArrayList<Long> removeOutlierWithMAD(ArrayList<Long> list, int k){
		DescriptiveStatistics stats = new DescriptiveStatistics();
		for(int i=0; i<list.size(); i++){
			stats.addValue(list.get(i));
		}

		double median = stats.getPercentile(50);

		stats.clear();
		for(int i=0; i<list.size(); i++){
			stats.addValue(Math.abs(list.get(i)-median));
		}
		double mad = stats.getPercentile(50);

		for(int i=0; i<list.size(); i++){
			Long l = list.get(i);
			double lower = median - k*mad;
			double upper = median + k*mad;
			if(!(lower <= l && l <= upper)){
				list.remove(i);
				i--;
			}
		}
		return list;
	}

	/**
	 * sums up a list
	 * @param list
	 * @return
	 * @author Stefan
     */
	public static int sumUpList(ArrayList<Long> list){
		int sum = 0;
		for(int i=0 ;i<list.size(); i++){
			sum += list.get(i);
		}
		return sum;
	}

	/**
	 * finds maximum value in a list
	 * @param list
	 * @return
	 * @author Stefan
     */
	public static int findMax(ArrayList<Float> list){
		float max = Float.MIN_VALUE;
		int index = 0;
		for(int i=0 ;i<list.size(); i++){
			if(max<list.get(i)){
				max = list.get(i);
				index = i;
			}
		}
		return index;
	}
}
