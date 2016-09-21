package uni.mannheim.teamproject.diabetesplaner.DataMining.Preprocessing;

import java.util.ArrayList;

import uni.mannheim.teamproject.diabetesplaner.Utility.Util;

/**
 * Created by Stefan
 */
public class DailyRoutineCreator {
	private ArrayList<String[]> list;
	private Integer labelIndex;
	private Integer minuteIndex;

	/**
	 * Constructor
	 * @param list: predicted daily routine in minutes of the day
	 * @author Stefan
	 */
	public DailyRoutineCreator(ArrayList<String[]> list){
		this.list = list;
		this.labelIndex = getLabelIndex("prediction(name)");
		this.minuteIndex = getMinuteIndex("starttime");
	}
	
	/**
	 * finds the index of the label in the list 
	 * @param attributeName
	 * @return
	 * @author Stefan
	 */
	private Integer getLabelIndex(String attributeName){
		int index = 0;
		for(int i=0; i<list.get(0).length; i++){
			if(list.get(0)[i].equals(attributeName)){
				index = i;
			}
		}
		return index;
	}
	
	/**
	 * finds index of the time in minutes in the list
	 * @param attributeName
	 * @return
	 * @author Stefan
	 */
	private Integer getMinuteIndex(String attributeName){
		int index = 0;
		for(int i=0; i<list.get(0).length; i++){
			if(list.get(0)[i].equals(attributeName)){
				index = i;
			}
		}
		return index;
	}
	
	
	/**
	 * creates a list where every activity gets a time interval
	 * @return ArrayList<String[]>
	 * @author Stefan
	 */
	public ArrayList<String[]> getDailyRoutine(){
		ArrayList<ArrayList<String>> tmp = new ArrayList<ArrayList<String>>();
		boolean newActivity = false;
		String prevActivity = "";
		
		for(int i=0; i<list.size(); i++){
			ArrayList<String> row = new ArrayList<String>();
			if(i==0){
				row.add("activity");
				row.add("starttime");
				row.add("endtime");
				tmp.add(row);
			}else{
				if(i == 1 || !prevActivity.equals(list.get(i)[labelIndex])){
					row.add(list.get(i)[labelIndex]);
					row.add(convertToHHmm(list.get(i)[minuteIndex]));
					
					//add endtime previous
					if(i>1){
						tmp.get(tmp.size()-1).add(convertToHHmm(list.get(i)[minuteIndex]));
					}
					
					prevActivity = list.get(i)[labelIndex];
					tmp.add(row);
				}
				if(i == list.size()-1){
					tmp.get(tmp.size()-1).add("23:59");
				}
			}
		}
		
		Util.print(Util.convertToArrayListStringArray(tmp));
		
		return Util.convertToArrayListStringArray(tmp);
	}
	
	/**
	 * converts a string with minutes of a day (0-1439) to a HH:mm string
	 * @param minutesOfDay
	 * @return String
	 * @author Stefan
	 */
	private String convertToHHmm(String minutesOfDay){
		int minOfDay = (int)Float.parseFloat(minutesOfDay);
		int hours = minOfDay / 60; 
		int minutes = minOfDay % 60;
		
		String min = "";
		if(minutes < 10){
			min = "0"+minutes;
		}else{
			min += minutes;
		}
		
		return hours + ":" + min;
	}
}
