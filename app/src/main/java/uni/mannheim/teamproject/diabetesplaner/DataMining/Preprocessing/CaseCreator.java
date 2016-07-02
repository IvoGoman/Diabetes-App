package uni.mannheim.teamproject.diabetesplaner.DataMining.Preprocessing;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;
import uni.mannheim.teamproject.diabetesplaner.Utility.Util;

/**
 * Created by Stefan
 */
public class CaseCreator {

	private int starttimeIndex = 0;
	private int endtimeIndex = 0;
	private ArrayList<String[]> list;

	/**
	 * Constructor
	 * @param list CSV file as ArrayList<String[]>
	 */
	public CaseCreator(ArrayList<String[]> list){
		this.list = list;
	}

	/**
	 * inits the indexes of start and endtime
	 * @param array first line of csv file
	 */
	private void initStartAndEndtimeIndex(String[]array){
		for (int j = 0; j < array.length; j++) {

			if (array[j].equals("starttime")) {
				starttimeIndex = j;
			} else if (array[j].equals("endtime")) {
				endtimeIndex = j;
			}
		}
	}

	/**
	 * creates the cases and splits an activity going over two days
	 * TODO bug: does not work from last to first day of two years
	 * does not handle logging mistakes like an activity that goes accidental over many days
	 * or a break of some days!
	 */
	public void createCases() {
		ArrayList<String[]> list2 = new ArrayList<String[]>();

		int caseCounter = 1;
		int eventID = 1;

		Calendar calActual = Calendar.getInstance();
		Calendar calPrev = Calendar.getInstance();
		
		
		// finds 00:00
		for (int i = 0; i < list.size(); i++) {
			String[] tmp = list.get(i);
			if (i == 0) {			
				initStartAndEndtimeIndex(tmp);
				list2.add(insertCase(list.get(i), 0, true));
			}else if (i == 1) {
				//init date with starttime of first entry
				Date date = new Date(Long.parseLong(tmp[starttimeIndex]));
				calActual.setTime(date);
				
				list2.add(insertCase(list.get(i),caseCounter, false));
				list2.get(list2.size()-1)[1]=String.valueOf(eventID);
				eventID++;
			} else{
				calPrev.setTime(calActual.getTime());
				//init starttime of current activity
				Date date = new Date(Long.parseLong(tmp[starttimeIndex]));
				calActual.setTime(date);
								
				int dayActual = calActual.get(Calendar.DAY_OF_YEAR);
				int dayPrev = calPrev.get(Calendar.DAY_OF_YEAR);

				
				//System.out.println("Previous: " + calPrev.getTime() + " Actual: " + calActual.getTime());
				//if days follow each other
				if (dayActual == dayPrev+1) {					
					//remove last activity in list, which is the last activity of the day. 
					//It gets split and added in the next steps.
					list2.remove(list2.size()-1);	
					eventID--;
					
					//add last activity of day
					String[] end = editEndOfDay(list.get(i-1), calPrev, starttimeIndex, endtimeIndex);			
					String[] copyEnd = new String[end.length];
					for(int k=0; k<copyEnd.length; k++){
						copyEnd[k] = end[k];
					}
					list2.add(insertCase(copyEnd, caseCounter, false));	
					list2.get(list2.size()-1)[1]=String.valueOf(eventID);
					eventID++;
					
					caseCounter++;
					
					//add first activity of day
					String[] begin = editBeginOfDay(list.get(i-1), calActual, starttimeIndex, endtimeIndex);		
					list2.add(insertCase(begin, caseCounter, false));
					list2.get(list2.size()-1)[1]=String.valueOf(eventID);
					eventID++;

				}
				list2.add(insertCase(list.get(i),caseCounter, false));
				list2.get(list2.size()-1)[1]=String.valueOf(eventID);
				eventID++;
			}
		}
		Util.writeListToList(list2, list);
	}

	/**
	 * Adds day of week to the CSV file. Should be called after cases are created!
	 * Sunday = 1
	 * Monday = 2
	 * Tuesday = 3
	 * Wednesday = 4
	 * Thursday = 5
	 * Friday = 6
	 * Saturday = 7
	 */
	public void addDayOfWeek(){
		initStartAndEndtimeIndex(list.get(0));

		for(int i=0; i<list.size(); i++){
			ArrayList<String> item = Util.toArrayList(list.get(i));
			if(i==0){
				item.add("dayOfWeek");
			}else{
				Calendar cal = TimeUtils.getCalendar(item.get(starttimeIndex));
				int day = cal.get(Calendar.DAY_OF_WEEK);
				item.add(String.valueOf(day));
			}
			list.set(i, Util.toArray(item));
		}
	}

	/**
	 * prints a single activity log entry
	 * @param activity
	 */
	private void printActivity(String[] activity){
		for(int j=0; j<activity.length; j++){

			if(j==0){
				System.out.println("CaseID: " + activity[j] + " ");
			}else if(j==1){
				System.out.println("EventID: " + activity[j] + " ");
			}else if(j==2){
				System.out.println("ActivityID: " + activity[j] + " ");
			}else if(j==3){
				System.out.println("SubactivityID: " + activity[j] + " ");
			}else if(j==4){
				Date date = new Date(Long.parseLong(activity[j]));
				System.out.println("Starttime: " + date);
			}else if(j==5){
				Date date = new Date(Long.parseLong(activity[j]));
				System.out.println("Endtime: " + date);					
			}
		}
	}
	
	/**
	 * Makes the last activity log of the day end in 23:59:59 and returns the activity log entry
	 * @param calPrev
	 * @param starttimeIndex
	 * @param endtimeIndex
	 * @return last activity of day edited
	 */
	private String[] editEndOfDay(String[] lastActivityPreviousDay, Calendar calPrev, int starttimeIndex, int endtimeIndex){
		//change endtime of last activity of day
		Calendar last = Calendar.getInstance();
		last.setTime(calPrev.getTime());
		last.set(last.get(Calendar.YEAR), last.get(Calendar.MONTH), last.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
		String[] endOfDay = lastActivityPreviousDay;
		endOfDay[endtimeIndex] = String.valueOf(last.getTimeInMillis());
		
		return endOfDay;
	}
	
	/**
	 * Creates the activity for the beginning of the day 
	 * @param calActual
	 * @param starttimeIndex
	 * @param endtimeIndex
	 * @return first activity of the day
	 */
	private String[] editBeginOfDay(String[] lastActiviyPreviousDay, Calendar calActual, int starttimeIndex, int endtimeIndex){
		//copy last activity and let it start at 00:00
		Calendar startNextEvent = Calendar.getInstance();
		startNextEvent.setTime(calActual.getTime());
		
		Calendar startBeginOfDay = Calendar.getInstance();		
		Calendar endBeginOfDay = Calendar.getInstance();
		
		startBeginOfDay.set(startNextEvent.get(Calendar.YEAR), startNextEvent.get(Calendar.MONTH), startNextEvent.get(Calendar.DAY_OF_MONTH), 00, 00, 00);
		endBeginOfDay.set(startNextEvent.get(Calendar.YEAR), startNextEvent.get(Calendar.MONTH), startNextEvent.get(Calendar.DAY_OF_MONTH), startNextEvent.get(Calendar.HOUR), startNextEvent.get(Calendar.MINUTE), startNextEvent.get(Calendar.SECOND)-1);					
		String[] beginOfDay = lastActiviyPreviousDay;
		beginOfDay[starttimeIndex] = String.valueOf(startBeginOfDay.getTimeInMillis());
		beginOfDay[endtimeIndex] = String.valueOf(endBeginOfDay.getTimeInMillis());
		
		return beginOfDay;
	}
	
	/**
	 * inserts caseID into a single event log entry
	 * @param activity
	 * @param caseID
	 */
	private String[] insertCase(String[] activity, int caseID, boolean initial){
//		TODO: Remove setting boolean to false and withCase to "CaseID" when solution is found
		String[] withCase = new String[activity.length+1];
		for(int i=0; i<withCase.length; i++){
			if(i==0){
				if(initial){
					withCase[i] = "1";
				}else{
					withCase[i] = String.valueOf(caseID);
				}
			}else{
				withCase[i] = activity[i-1];
			}
		}
		
		return withCase;
	}
	
	/**
	 * It splits the activity into days and creates a unique case id for every day.
	 * @param source Path to an activity log in csv format
	 * @return ArrayList that contains the activity list divided into cases
	 */
	public ArrayList<String[]> getActivityListWithCases(String source){
		ArrayList<String[]> list = Util.read(source);
		createCases();
		return list;
	}
	
	/**
	 * It splits the activity into days and creates a unique case id for every day.
	 * Also another column is created and filled with the day of the weak
	 * @param source Path to an activity log in csv format
	 * @param target Path for saving the resulting csv file
	 */
	public void saveActivityWithCases(String source, String target){
		ArrayList<String[]> list = Util.read(source);

		createCases();
		addDayOfWeek();
		
//		for(int i=1; i<list2.size();i++){
//			printActivity(list2.get(i));
//			System.out.println();
//		}
		
		Util.write(list, target);
	}

	/**
	 * @return list
	 */
	public ArrayList<String[]> getList(){
		return this.list;
	}

	/**
	 * 	 * Merges two activities following each other that have the same activity and subactivity into one activity
	 * should be.
	 * Has to be applied after case creation !!
	 * @param withSubactivity if true the subactivity is taken into account for comparison
     */
	public void mergeConsecutiveSameActivity(boolean withSubactivity){
		String prevActivity = "";
		String prevSubactivity = "";
		String prevCaseID = "";

		ArrayList<Boolean> sameAsBefore = new ArrayList<>();
		//find duplicates
		for(int i=0; i<list.size();i++){
			//initialize filed in arraylist
			sameAsBefore.add(false);
			//first activity cannot be a duplicate
			if(i==0){
				prevCaseID = list.get(i)[0];
				prevActivity = list.get(i)[2];
				prevSubactivity = list.get(i)[3];

			}
			//same case as previous activity
			else if(list.get(i)[0].equals(prevCaseID)){
				//subactivity is taken into acocunt
				if (prevActivity.equals(list.get(i)[2]) && prevSubactivity.equals(list.get(i)[3]) && withSubactivity) {
					sameAsBefore.add(true);

					prevCaseID = list.get(i)[0];
					prevActivity = list.get(i)[2];
					prevSubactivity = list.get(i)[3];
				//subactivity not taken into account
				}else if(prevActivity.equals(list.get(i)[2])&& !withSubactivity){
					sameAsBefore.add(true);

					prevCaseID = list.get(i)[0];
					prevActivity = list.get(i)[2];
				}else{
					prevCaseID = list.get(i)[0];
					prevActivity = list.get(i)[2];
					prevSubactivity = list.get(i)[3];

				}
			}
			//not the same case as the previous activity
			else{
				prevCaseID = list.get(i)[0];
				prevActivity = list.get(i)[2];
				prevSubactivity = list.get(i)[3];
			}
		}
		ArrayList<String[]> resultList = new ArrayList<>();
		//merge duplicates
		for(int i=sameAsBefore.size()-1; i>=0; i--){
			if(sameAsBefore.get(i)){
				String[] actual = list.get(i);
				String[] prev = list.get(i-1);

				//set starttime of previous to actual
				list.get(i-1)[endtimeIndex] = list.get(i)[endtimeIndex];
				//remove previous from list
				list.remove(i);
			}
		}
	}
}
