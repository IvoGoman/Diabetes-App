package uni.mannheim.teamproject.diabetesplaner.Utility;

import java.util.ArrayList;

/**
 * Created by Stefan
 */
public class Main {

	public static void main(String[] args) {

		//initialize source and destination paths
		//------------------replace with your filepaths-------------------------------------------------------
		String source = "C:\\Users\\Stefan\\Documents\\Uni Mannheim\\Master\\Team Project\\activityDataComplete.csv";
		String dest = "C:\\Users\\Stefan\\Documents\\Uni Mannheim\\Master\\Team Project\\activityDataCasesDayOfWeek.csv";
		String destTestData = "C:\\Users\\Stefan\\Documents\\Uni Mannheim\\Master\\Team Project\\activityDataTestData.csv";


		//reads the csv from the source path
		ArrayList<String[]> list = Util.read(source);
		//creates a CaseCreator object with the CSV file in an ArrayList
		CaseCreator creator = new CaseCreator(list);
		//splits the data into cases and adds a column for the case id for each entry
		creator.createCases();
		//adds a column with the day of the week
		creator.addDayOfWeek();

		//Util.printList(list);

		//creates a MinuteSplitter object with the SCV file in an ArrayList
		MinuteSplitter splitter = new MinuteSplitter(list);
		//splits the list into minutes
		splitter.splitLogWithCasesIntoMinutes();
		//writes the list to a CSV file that is stored at dest
		Util.write(list, dest);

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
}
