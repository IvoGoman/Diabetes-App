package uni.mannheim.teamproject.diabetesplaner.DataMining.SequentialPattern;

import com.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

	public static ArrayList<String[]> read(String filename){
		ArrayList<String[]> tmp = new ArrayList<>();
	    CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(filename));
			//read first
			reader.readNext();
		    String [] nextLine;
		    while ((nextLine = reader.readNext()) != null) {
		       tmp.add(nextLine);
		    }
	    
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return tmp;
	}
	
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
	 * parses the sequence with activities and subactivities combined by String combinations to activity items
	 * @param s
	 * @return
	 * @author Stefan 27.07.2016
     */
	public static ArrayList<ActivityItem> parseSequence(Sequence s){
		ArrayList<ActivityItem> dailyRoutine = new ArrayList<>();
		for(int i=0; i<s.size(); i++){
			String event = (String) s.get(i);
			int activityID = Integer.parseInt(event.replaceAll("_.*",""));
			String tmp = event.replaceAll(".*_","");
			int subactivityID = 0;
			if(!tmp.equals("")) {
				subactivityID = Integer.parseInt(tmp);
			}

			String activity = ActivityItem.getActivityString(activityID);
			String subactivity = ActivityItem.getSubactivity(subactivityID);

			ActivityItem item = new ActivityItem(activityID, subactivityID);
			dailyRoutine.add(item);
		}
		return dailyRoutine;
	}

	/**
	 * returns average time of an activity subactivity combination
	 * @param data
	 * @return
	 * @author Stefan 27.07.2016
	 */
	public static HashMap<String, Long> getAvgTime(ArrayList<String[]> data){
		HashMap<String, Long> sum = new HashMap<>();
		HashMap<String, Integer> count = new HashMap<>();
		for(int i=0; i<data.size(); i++){
			long starttime = Long.parseLong(data.get(i)[4]);
			long endtime = Long.parseLong(data.get(i)[5]);

			long diff = endtime-starttime;

			String key = data.get(i)[2] + "_" + data.get(i)[3];

			if(!sum.containsKey(key)){
				sum.put(key, diff);
				count.put(key, 1);
			}else{
				sum.put(key, sum.get(key) + diff);
				count.put(key, count.get(key) + 1);
			}
		}

		for(Entry<String, Long> m : sum.entrySet()){
			sum.put(m.getKey(), (long)m.getValue()/count.get(m.getKey()));
		}

		return sum;
	}
}