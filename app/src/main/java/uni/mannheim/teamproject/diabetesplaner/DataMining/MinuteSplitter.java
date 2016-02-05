package uni.mannheim.teamproject.diabetesplaner.DataMining;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
	 * @param starttimeIndex index in the string array of the starttime
	 * @param endtimeIndex index in the string array of the endtime
	 */
	public MinuteSplitter(ArrayList<String[]> list, int starttimeIndex, int endtimeIndex){
		this.list = list;
		this.starttimeIndex = starttimeIndex;
		this.endtimeIndex = endtimeIndex;
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
				System.out.println(starttime + " " + endtime + " " + iterations);
				
				for(int j=0; j<iterations; j++){
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
								System.out.println(cal.get(Calendar.MINUTE) + " " + cal.get(Calendar.HOUR));
								System.out.println(timestamp);
								tmp.add(String.valueOf(nrOfMinute));
							}else{
								tmp.add(list.get(i)[k]);
							}
						}
					}
					printActivity(Util.toArray(tmp));
					listFinal.add(Util.toArray(tmp));
				}
			}
		}
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

	/**
	 * converts a string time into a timestamp
	 * @param time
	 * @return
	 */
	private long stringToTimestamp(String time){
	    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy");
	    Date d = null;
		try {
			d = sdf.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    Calendar c = Calendar.getInstance();
	    c.setTime(d);
	    long time2 = c.getTimeInMillis()/1000;
	    return time2;
	}
}
