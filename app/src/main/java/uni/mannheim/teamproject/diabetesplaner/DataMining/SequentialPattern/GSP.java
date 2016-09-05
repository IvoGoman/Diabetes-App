package uni.mannheim.teamproject.diabetesplaner.DataMining.SequentialPattern;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import uni.mannheim.teamproject.diabetesplaner.Utility.Util;

/**
 * @author Stefan
 */
public class GSP {

	private ArrayList<String[]> data;
	private ArrayList<ArrayList<String[]>> cases = new ArrayList<>();
	private HashMap<Sequence, Integer> count = new HashMap<>();
	private HashMap<Sequence, Float> freqSubSeq = new HashMap<>();
	ArrayList<Sequence> newFreqKSeq = new ArrayList<>();
	private ArrayList<Sequence> completeSeqs = new ArrayList<>();
	private HashMap<Sequence, Float> freqSubSeqKminus1 = new HashMap<>();

	public static int iCaseId;
	public static int iActivity;
	public static int iSubactivity;
	public static int iStarttime;
	public static int iEndtime;
	private int k = 1;

	/**
	 *
	 * @param data
	 * @param indexCaseId index of caseId
	 * @param indexActivity index of activity
	 * @param indexSubactivity index of subactivity
	 */
	public GSP(ArrayList<String[]> data, int indexCaseId, int indexActivity, int indexSubactivity, int indexStart, int indexEnd){
		this.data = data;
		this.iCaseId = indexCaseId;
		this.iActivity = indexActivity;
		this.iSubactivity = indexSubactivity;
		this.iStarttime = indexStart;
		this.iEndtime = indexEnd;

		createListWithCases();

	}

	/**
	 * returns the support for an event that occurs x times
	 * @param x number of occurances
	 * @return support
	 * @author Stefan
	 */
	public float getSupportXOccurance(int x){
		return ((float)x)/((float)cases.size());
	}

	/**
	 * creates a list that contains for each caseID an own list with the data
	 * @author Stefan 19.07.2016
	 */
	public void createListWithCases(){
		int currCase = 0;
		ArrayList<String[]> tmpList = new ArrayList<>();
		for(int i=0; i<data.size(); i++){
			if(i==0){
				currCase = Integer.valueOf(data.get(i)[iCaseId]);
				tmpList.add(data.get(i));
			}else{
				int tmpCase = Integer.valueOf(data.get(i)[iCaseId]);
				if(tmpCase > currCase){
					currCase = tmpCase;
					cases.add(tmpList);
					tmpList = new ArrayList<>();
					tmpList.add(data.get(i));
				}else{
					tmpList.add(data.get(i));
				}
			}
		}
	}

	/**
	 * applies the GSP algorithm
	 * @param minsup
	 * @return
	 */
	public HashMap<Sequence, Float> findFrequentPatterns(float minsup){
		HashMap<Sequence, Float> freqPat = new HashMap<>();

		//1. Step: find frequent 1-Sequences
		findFrequentOneSequences(minsup);

//		for(int i=0; i<completeSeqs.size(); i++){
//			System.out.println(completeSeqs.get(i).toString());
//		}

		//2. Step: until no new frequent subsequences are found
		while(true){
			k++;
			System.out.println("k: " + k);
			//1. candidate generation
			generateCandidates();

//			for(int i=0; i<newFreqKSeq.size(); i++){
//				System.out.println("k = " + k + ": " +newFreqKSeq.get(i));
//			}

			//2. apriori pruning
			pruneCandidates();

//			for(int i=0; i<newFreqKSeq.size(); i++){
//				System.out.println("k = " + k + " pruned: " +newFreqKSeq.get(i));
//			}

			//3. support count and elimination
			findFrequentKSequences(minsup);


			if(newSeqs()){
				HashMap<Sequence, Float> result = new HashMap<>();
				for(Entry<Sequence, Float> m : freqSubSeq.entrySet()){
					if(m.getKey().size() > 1){
						result.put(m.getKey(), m.getValue());
					}
				}

				return (HashMap<Sequence, Float>) GSP_Util.sortByValue(result, true);
			}
		}
	}


	/**
	 * returns a HashMap<Item,Support> with all frequent one sequences
	 * @param minsup
	 * @return
	 */
	public void findFrequentOneSequences(float minsup){
		for(int i=0; i<cases.size(); i++){
			HashMap<String, Boolean> present = new HashMap<>();
			//set present items
			Sequence tmp = new Sequence();
			for(int j=0; j<cases.get(i).size(); j++){
				long starttime = Long.parseLong(cases.get(i).get(j)[iStarttime]);
				Timestamp stamp = new Timestamp(starttime);
				Date date = new Date(stamp.getTime());
				SimpleDateFormat sdf = new SimpleDateFormat("a");
				String amPm = sdf.format(date);

				String item;

				//starttime is in the morning or eventing
				if(amPm.equals("AM")){
					item = cases.get(i).get(j)[iActivity] + "_" + cases.get(i).get(j)[iSubactivity] + "_" + "AM";
				}else{
					item = cases.get(i).get(j)[iActivity] + "_" + cases.get(i).get(j)[iSubactivity] + "_" + "PM";
				}

				tmp.add(item);
				present.put(item, true);
			}
			completeSeqs.add(tmp);

			//add present items of this iteration to the total item count
			for (Map.Entry<String, Boolean> entry : present.entrySet()){
				Sequence seq = new Sequence(entry.getKey());
				Boolean value = entry.getValue();
				if(!count.containsKey(seq)){
					count.put(seq, 1);
				}else{
					count.put(seq, count.get(seq)+1);
				}
			}
		}
		//calc support, remove infrequent
		HashMap<Sequence, Float> freqOneSeq = new HashMap<>();
		int nrOfCases = cases.size();
		for (Map.Entry<Sequence, Integer> entry : count.entrySet()){
			Sequence key = entry.getKey();
			Integer value = entry.getValue();
			if(Float.valueOf(value)/nrOfCases >= minsup){
				freqOneSeq.put(key, Float.valueOf(value)/nrOfCases);
			}else{
				freqOneSeq.remove(key);
			}
		}

		this.freqSubSeq = freqOneSeq;
	}

	/**
	 * generates new candidate sequences
	 * @author Stefan 19.07.2016
	 */
	public void generateCandidates(){

		for(Map.Entry<Sequence, Float> entry1 : freqSubSeq.entrySet()){
			Sequence s1 = entry1.getKey();
			for(Map.Entry<Sequence, Float> entry2 : freqSubSeq.entrySet()){
				Sequence s2 = entry2.getKey();
				if(!s1.equals(s2)){
					//base case
					if(s1.size() == 1 && s2.size() == 1){
						newFreqKSeq.add(new Sequence(s1,s2));
						newFreqKSeq.add(new Sequence(s2,s1));
					}
					//general case
					else{
						if(s1.getSubSequence(1, s1.size()-1).equals(s2.getSubSequence(0, s1.size()-2))){
							Sequence newSeq = new Sequence(s1, (String)(s2.get(s2.size()-1)), false);
							newFreqKSeq.add(newSeq);

						}else if(s1.getSubSequence(0, s1.size()-2).equals(s2.getSubSequence(1, s1.size()-1))){
							Sequence newSeq = new Sequence(s2, (String)(s1.get(s1.size()-1)), false);
							newFreqKSeq.add(newSeq);
						}
					}
				}
			}
		}

		//adds the sequences from the k-1th pass
		for(Map.Entry<Sequence, Float> map : freqSubSeq.entrySet()){
			Sequence s = map.getKey();
			newFreqKSeq.add(s);
		}
	}

	/**
	 * prune sequences which contain infrequent k-1 sequences 
	 * @author Stefan 19.07.2016
	 */
	public void pruneCandidates(){
		ArrayList<Sequence> tmp = new ArrayList<Sequence>();

//		for(Entry<Sequence, Float> m : freqSubSeq.entrySet()){
//			System.out.println(m.getKey().toString() + ": " + m.getValue());
//		}

		for(int i=0; i<newFreqKSeq.size(); i++){
			boolean inFreqSubSeq = false;
			ArrayList<Sequence>seq = newFreqKSeq.get(i).buildAllSubsequences();


			//have a look if one of the subsequences is not contained in the k-1 frequent sequences
			for(int j=0; j<seq.size(); j++){
//				System.out.println(seq.get(j) + " contained in freqSubSeq: " + freqSubSeq.containsKey(seq.get(j)));
				if(!freqSubSeq.containsKey(seq.get(j))){
					inFreqSubSeq = true;;				}

			}

			if(!inFreqSubSeq){
				tmp.add(newFreqKSeq.get(i));
			}
		}

		newFreqKSeq = tmp;
	}

	/**
	 * Calculates the support and removes infrequent sequences
	 * @param minsup
	 * @author Stefan 19.07.2016
	 */
	public void findFrequentKSequences(float minsup){
		for(Entry<Sequence, Float> m : freqSubSeq.entrySet()){
			freqSubSeqKminus1.put(m.getKey(), m.getValue());
		}

		freqSubSeq = new HashMap<>();
		//count occurances
		for(int i=0; i<completeSeqs.size(); i++){
			HashMap<Sequence, Boolean> isSS = new HashMap<>();
			for(int j=0; j<newFreqKSeq.size(); j++){
				if(newFreqKSeq.get(j).isSubsequence(completeSeqs.get(i))){
					isSS.put(newFreqKSeq.get(j), true);
				}
			}

			for(Map.Entry<Sequence, Boolean> m : isSS.entrySet()){
				Sequence s = m.getKey();
				boolean value = m.getValue();
				if(value){
					if(!freqSubSeq.containsKey(s)){
						freqSubSeq.put(s, 1f);
					}else{
						freqSubSeq.put(s, freqSubSeq.get(s)+1);
					}
				}
			}
		}


		ArrayList<Sequence> al = new ArrayList<>();

		//calc support, remove infrequent sequences
		for(Map.Entry<Sequence, Float> m : freqSubSeq.entrySet()){
			float value = m.getValue();
			float support = value/cases.size();

			if(support >= minsup){
				m.setValue(support);
			}else{
				al.add(m.getKey());
			}
		}

		for(int i=0; i<al.size(); i++){
			freqSubSeq.remove(al.get(i));
		}
	}

	/**
	 * checks if there was a new sequence found in the i+1st run
	 * @return boolean
	 * @author Stefan 25.07.2016
	 */
	private boolean newSeqs() {
		for(Entry<Sequence, Float> m2 : freqSubSeq.entrySet()){
			if(!freqSubSeqKminus1.containsKey(m2.getKey())){
				return true;
			}
		}
		return false;
	}

	/**
	 * returns an arraylist with sequences where a sequence is one day
	 * @return
	 * @author Stefan 25.07.2016
	 */
	public ArrayList<Sequence> getCompleteSeqs() {
		return completeSeqs;
	}
}
