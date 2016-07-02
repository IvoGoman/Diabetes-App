package uni.mannheim.teamproject.diabetesplaner.DataMining.Preprocessing;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import uni.mannheim.teamproject.diabetesplaner.Utility.Util;

/**
 * Created by Stefan
 */
public class MinuteSplitter {
	
	private ArrayList<String[]> list = new ArrayList<String[]>();
	private ArrayList<String[]> listFinal = new ArrayList<String[]>();
	private int starttimeIndex;
	private int endtimeIndex;

	/**
	 * takes an eventlog and splits it into minutes
	 * @param list list with each row in the csv file as String array and devided into cases
	 */
	public MinuteSplitter(ArrayList<String[]> list){
		this.list = list;
		initStartAndEndtimeIndex(list.get(0));
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

	private void convertToMinutes(){
		for(int i=1; i<list.size();i++){
			for(int j=0; j<list.get(i).length; j++){
				if(j== starttimeIndex || j == endtimeIndex){
					list.get(i)[j] = String.valueOf(Long.parseLong(list.get(i)[j])/60);
				}
			}
		}
	}

	/**
	 * splits the cases into minutes
	 */
	public void splitLogWithCasesIntoMinutes(){
		//convertToMinutes();
		for(int i=0; i<list.size(); i++){
			
			if(i==0){
				ArrayList<String> tmp = new ArrayList<String>();

				for(int j=0; j<list.get(i).length; j++){
					if(!(j == endtimeIndex)){
						tmp.add(list.get(i)[j]);
					}
				}
				listFinal.add(Util.toArray(tmp));


			}else{
				Timestamp starttime = new Timestamp(Long.parseLong(list.get(i)[starttimeIndex]));
				Timestamp endtime = new Timestamp(Long.parseLong(list.get(i)[endtimeIndex]));
				long iterations = (endtime.getTime()-starttime.getTime())/1000/60;
				//System.out.println(starttime + " " + endtime + " " + iterations);
				
				for(int j=0; j<iterations; j++){
					//list for minutes
					ArrayList<String> tmp = new ArrayList<String>();
					for(int k=0; k<list.get(i).length; k++){
						if(!(k == endtimeIndex)){
							if(k == starttimeIndex){
								Timestamp timestamp = new Timestamp(Long.parseLong(list.get(i)[k]));
								timestamp.setTime(timestamp.getTime() + j*60*1000);

							    String srg = new SimpleDateFormat("HH").format(timestamp);
								Calendar cal = Calendar.getInstance();
								cal.setTime(timestamp);
								int nrOfMinute = cal.get(Calendar.MINUTE)+Integer.parseInt(srg)*60;
								//System.out.println(cal.get(Calendar.MINUTE) + " " + cal.get(Calendar.HOUR));
								//System.out.println(timestamp);
								tmp.add(String.valueOf(nrOfMinute));
							}else{
								tmp.add(list.get(i)[k]);
							}
						}
					}
					//printActivity(Util.toArray(tmp));
					listFinal.add(Util.toArray(tmp));
				}
			}
		}
		Util.writeListToList(listFinal, list);
	}

	/**
	 * returns the converted list
	 * list has to be converted before!
	 * @return
	 */
	public ArrayList<String[]>getConvertedList(){
		return listFinal;
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
			}else if(j==starttimeIndex){
				//Timestamp date = new Timestamp(Long.parseLong(activity[j]));
				System.out.println("Starttime: " + activity[j]);
			}
		}
	}
}
