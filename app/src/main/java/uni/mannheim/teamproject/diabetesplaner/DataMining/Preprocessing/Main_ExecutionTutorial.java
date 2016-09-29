package uni.mannheim.teamproject.diabetesplaner.DataMining.Preprocessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import uni.mannheim.teamproject.diabetesplaner.DataMining.SequentialPattern.GSP_Util;
import uni.mannheim.teamproject.diabetesplaner.Utility.Util;

/**
 * Created by Stefan
 */
public class Main_ExecutionTutorial {

	/**
	 * execute to test how the loading csv files works. Can run independent from the app.
	 * @param args
	 * @author Stefan
     */
	public static void main(String[] args) {

		//initialize source and destination paths
		//------------------replace with your filepaths-------------------------------------------------------
		String source = "C:\\Users\\Stefan\\Documents\\Uni Mannheim\\Master\\Team Project\\Stefan_Data_27.07.16\\SDC_ActivityData.csv";
		String dest = "C:\\Users\\Stefan\\Documents\\Uni Mannheim\\Master\\Team Project\\Stefan_Data_27.07.16\\ActivityData_Cases.csv";
		String destTestData = "C:\\Users\\Stefan\\Documents\\Uni Mannheim\\Master\\Team Project\\Stefan_Data_27.07.16\\test.csv";


		//reads the csv from the source path
		ArrayList<String[]> list = Util.read(source);

//		for(int i=0; i<list.size(); i++){
//			System.out.println(list.get(i)[0]);
//		}

		//creates a CaseCreator object with the CSV file in an ArrayList
		CaseCreator creator = new CaseCreator(list);
		//splits the data into cases and adds a column for the case id for each entry
		creator.createCases();
		//adds a column with the day of the week
		creator.addDayOfWeek();
		//print statistics of the csv:
//		printActivityDistribution(creator.getList());
//		printAvgDurations(creator.getList(),2,3,4,5);
//		printDistribution(creator.getList());
//		Util.printList(list);
//		System.out.println("-------------------------------------------------");
		//first case of the complete list is csv header (causes type conversation errors if not removed)
		creator.removeFirstCase(true);
		creator.shiftSameBorderTime();
		//merges two consecutive activities which are the same
		creator.mergeConsecutiveSameActivity(true);
		//removes activities where endtime is before starttime
		creator.removeActivitiesWithEndBeforeStarttime();

		creator.removeFirstCase(true);
		creator.removeLastCase();

//		for(int i=0; i<creator.getList().size(); i++){
//			for(int j=0; j<creator.getList().get(i).length; j++){
//				if(j == creator.getStarttimeIndex() || j == creator.getEndtimeIndex()) {
//					System.out.println(new Date(Long.parseLong(creator.getList().get(i)[j])));
//				}else {
//					System.out.println(creator.getList().get(i)[j]);
//				}
//			}
//			System.out.println();
//		}

//		Util.printList(creator.getList());

//		//creates a MinuteSplitter object with the SCV file in an ArrayList
//		MinuteSplitter splitter = new MinuteSplitter(list);
//		//splits the list into minutes
//		splitter.splitLogWithCasesIntoMinutes();
//		writes the list to a CSV file that is stored at dest
		Util.write(creator.getList(), dest);

		//creates a test data sheet with the actual day and minute of day as an attribute
/*		Calendar cal = Calendar.getInstance();
		Util.createTestData(destTestData, cal.get(Calendar.DAY_OF_WEEK));*/
		
		//convert rapidminer result for daily routine
/*		String source = "C:\\Users\\Stefan\\Documents\\Uni Mannheim\\Master\\Team Project\\firstDailyRoutine.csv";
		String dest = "C:\\Users\\Stefan\\Documents\\Uni Mannheim\\Master\\Team Project\\firstDailyRoutineAggregated.csv";
		DailyRoutineCreator daily = new DailyRoutineCreator(Util.read(source));
		daily.getDailyRoutine();
		Util.write(daily.getDailyRoutine(),dest);*/
	}

	/**
	 * prints day distributions in .csv
	 * @param list2
	 * @author Stefan
     */
	public static void printDistribution(ArrayList<String[]> list2) {
		int mon = 0;
		int tues = 0;
		int wed = 0;
		int thur = 0;
		int fri = 0;
		int sat = 0;
		int sun = 0;

		int prev = 0;

		for (int i = 1; i < list2.size(); i++) {
			int day = Integer.valueOf(list2.get(i)[list2.get(i).length - 1]);
			int caseId = Integer.valueOf(list2.get(i)[0]);
			if (i == 1) {
				switch (day) {
					case 1:
						sun++;
						break;
					case 2:
						mon++;
						break;
					case 3:
						tues++;
						break;
					case 4:
						wed++;
						break;
					case 5:
						thur++;
						break;
					case 6:
						fri++;
						break;
					case 7:
						sat++;
						break;
				}
				prev = caseId;
			} else {
				if (caseId != prev) {
					switch (day) {
						case 1:
							sun++;
							break;
						case 2:
							mon++;
							break;
						case 3:
							tues++;
							break;
						case 4:
							wed++;
							break;
						case 5:
							thur++;
							break;
						case 6:
							fri++;
							break;
						case 7:
							sat++;
							break;
					}
					prev = caseId;
				}
			}
		}

		System.out.println("Mondays: " + mon);
		System.out.println("Tuesdays: " + tues);
		System.out.println("Wednesdays: " + wed);
		System.out.println("Thursdays: " + thur);
		System.out.println("Fridays: " + fri);
		System.out.println("Saturdays: " + sat);
		System.out.println("Sundays: " + sun);
		System.out.println("Weekdays: " + (mon + tues + wed + thur + fri));
		System.out.println("Weekends: " + (sat + sun));
	}

	/**
	 * prints activity distribution in .csv
	 * @param list2
	 * @author Stefan
     */
	public static void printActivityDistribution(ArrayList<String[]> list2) {
		int mon = 0;
		int tues = 0;
		int wed = 0;
		int thur = 0;
		int fri = 0;
		int sat = 0;
		int sun = 0;

		int prev = 0;

		for (int i = 1; i < list2.size(); i++) {
			int day = Integer.valueOf(list2.get(i)[list2.get(i).length - 1]);
			switch (day) {
				case 1:
					sun++;
					break;
				case 2:
					mon++;
					break;
				case 3:
					tues++;
					break;
				case 4:
					wed++;
					break;
				case 5:
					thur++;
					break;
				case 6:
					fri++;
					break;
				case 7:
					sat++;
					break;
			}
		}

		System.out.println("Activities Mondays: " + mon);
		System.out.println("Activities Tuesdays: " + tues);
		System.out.println("Activities Wednesdays: " + wed);
		System.out.println("Activities Thursdays: " + thur);
		System.out.println("Activities Fridays: " + fri);
		System.out.println("Activities Saturdays: " + sat);
		System.out.println("Activities Sundays: " + sun);
		System.out.println("Activities Weekdays: " + (mon + tues + wed + thur + fri));
		System.out.println("Weekends: " + (sat + sun));
	}

	/**
	 * print average and total durations of activities in .csv
	 * @param list2
	 * @param iAct
	 * @param iSub
	 * @param iStart
     * @param iEnd
	 * @author Stefan
     */
	public static void printAvgDurations(ArrayList<String[]> list2, int iAct, int iSub, int iStart, int iEnd){
		HashMap<String, Long> map = new HashMap<>();
		HashMap<String, Integer>counts = new HashMap<>();
		for(int i=1; i<list2.size(); i++){
			String[] arr = list2.get(i);
			String key = arr[iAct]+"_"+arr[iSub];
			Long dur = Long.parseLong(arr[iEnd])-Long.parseLong(arr[iStart]);
			if(map.get(key) != null){
				map.put(key, (map.get(key) + dur));
				counts.put(key, (counts.get(key)+ 1));
			}else{
				map.put(key, dur);
				counts.put(key, 1);
			}
		}

		HashMap<String, Double> avgDur = new HashMap<>();
		for(Map.Entry<String, Long> entry : map.entrySet()){
			String key = entry.getKey();
			Double value = (double) entry.getValue();
			avgDur.put(key, value/((double)counts.get(key)));
		}

		Map<String, Long> newMap = GSP_Util.sortByValue(map, true);
		for (Map.Entry<String, Long> entry : newMap.entrySet()) {
			String key = entry.getKey();
			Long value = entry.getValue()/(1000*60);
			int hours = (int) (value/60);
			int mins = (int) (value%60);

			Double avg = avgDur.get(key)/(1000*60);
			int avgH = (int) (avg/60);
			int avgMin = (int) (avg%60);
			System.out.println(key + ": \t" + hours + "h " + mins + "min \t" + "Average duration: " + avgH + "h " + avgMin + "min");
		}
	}
}
