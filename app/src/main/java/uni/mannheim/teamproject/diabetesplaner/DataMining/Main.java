package uni.mannheim.teamproject.diabetesplaner.DataMining;

/**
 * Created by Stefan
 */
public class Main {

	public static void main(String[] args) {
		CaseCreator creator = new CaseCreator();
		
		//create cases
//		String source = "C:\\Users\\Stefan\\Documents\\Uni Mannheim\\Master\\Team Project\\activityDataComplete.csv";
//		String dest = "C:\\Users\\Stefan\\Documents\\Uni Mannheim\\Master\\Team Project\\activityDataInMinutes.csv";
//		
//		creator.saveActivityWithCases(source,dest);
		
		//convert into minutes
//		ArrayList<String[]> list = creator.getActivityListWithCases(source);
//		
//		int starttimeIndex = creator.getStarttimeIndexInListWithCases();
//		int endtimeIndex = creator.getEndtimeIndexInListWithCases();
//		
//		MinuteSplitter split = new MinuteSplitter(list, starttimeIndex, endtimeIndex);
//		split.splitLogWithCasesIntoMinutes();
//
//		creator.write(split.getConvertedList(), dest);
		
		//convert rapidminer result for daily routine
		String source = "C:\\Users\\Stefan\\Documents\\Uni Mannheim\\Master\\Team Project\\firstDailyRoutine.csv";
		String dest = "C:\\Users\\Stefan\\Documents\\Uni Mannheim\\Master\\Team Project\\firstDailyRoutineAggregated.csv";
		DailyRoutineCreator daily = new DailyRoutineCreator(Util.read(source));
		daily.getDailyRoutine();
		//Util.write(daily.getDailyRoutine(),dest);
	}
}
