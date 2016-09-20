package uni.mannheim.teamproject.diabetesplaner.DataMining.SequentialPattern;

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
	 * returns average time of an activity subactivity combination in milliseconds
	 * @param data
	 * @return
	 * @author Stefan 27.07.2016
	 */
	public static HashMap<String, Long> getAvgTime(ArrayList<ArrayList<ActivityItem>> data){
		HashMap<String, Long> sum = new HashMap<>();
		HashMap<String, Integer> count = new HashMap<>();
		for(int i=0; i<data.size(); i++) {
			for (int j = 0; j < data.get(i).size(); j++){
				ActivityItem item = data.get(i).get(j);
				long starttime = item.getStarttime().getTime();
				long endtime = item.getEndtime().getTime();

				long diff = endtime - starttime;

				Timestamp stamp = new Timestamp(starttime);
				Date date = new Date(stamp.getTime());
				SimpleDateFormat sdf = new SimpleDateFormat("a");
				String amPm = sdf.format(date);

				String key;
				if (amPm.equals("AM")) {
					key = item.getActivityId() + "_" + item.getSubactivityId() + "_AM";
				} else {
					key = item.getActivityId() + "_" + item.getSubactivityId() + "_PM";
				}


				if (!sum.containsKey(key)) {
					sum.put(key, diff);
					count.put(key, 1);
				} else {
					sum.put(key, sum.get(key) + diff);
					count.put(key, count.get(key) + 1);
				}
			}
		}

		for(Entry<String, Long> m : sum.entrySet()){
			sum.put(m.getKey(), (long)m.getValue()/count.get(m.getKey()));
		}

		return sum;
	}

	/**
	 * sums up a list
	 * @param list
	 * @return
	 * @author Stefan
     */
	public static int sumUp(ArrayList<Long> list){
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
