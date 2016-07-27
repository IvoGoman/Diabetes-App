package uni.mannheim.teamproject.diabetesplaner.DataMining.Preprocessing;

import java.util.ArrayList;

import uni.mannheim.teamproject.diabetesplaner.Utility.Util;

/**
 * Created by Stefan
 */
public class Main_ExecutionTutorial {

	public static void main(String[] args) {

		//initialize source and destination paths
		//------------------replace with your filepaths-------------------------------------------------------
		String source = "C:\\Users\\Stefan\\Documents\\Uni Mannheim\\Master\\Team Project\\Stefan_Data_27.07.16\\SDC_ActivityData.csv";
		String dest = "C:\\Users\\Stefan\\Documents\\Uni Mannheim\\Master\\Team Project\\Stefan_Data_27.07.16\\ActivityData_Cases.csv";
		String destTestData = "C:\\Users\\Stefan\\Documents\\Uni Mannheim\\Master\\Team Project\\activityDataTestData2.csv";


		//reads the csv from the source path
		ArrayList<String[]> list = Util.read(source);

		for(int i=0; i<list.size(); i++){
			System.out.println(list.get(i)[0]);
		}

		//creates a CaseCreator object with the CSV file in an ArrayList
		CaseCreator creator = new CaseCreator(list);
		//splits the data into cases and adds a column for the case id for each entry
		creator.createCases();
		//adds a column with the day of the week
		creator.addDayOfWeek();
		Util.printList(list);
		System.out.println("-------------------------------------------------");
		//merges two consecutive activities which are the same
		creator.mergeConsecutiveSameActivity(true);
		creator.removeFirstCase(true);
		creator.removeLastCase();

		for(int i=0; i<creator.getList().size(); i++){
			for(int j=0; j<creator.getList().get(i).length; j++){
				System.out.println(creator.getList().get(i)[j]);
			}
			System.out.println();
		}

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
}
