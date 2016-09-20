package uni.mannheim.teamproject.diabetesplaner.DataMining.SequentialPattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Stefan
 */
public class Sequence extends ArrayList{

	private float supCount = 0f;
	
	public Sequence(){
		super();
	}
	
	public Sequence(String str){
		super();
		this.add(str);
	}
	
	public Sequence(Sequence s1, Sequence s2){
		super();
		addSequence(s1);
		addSequence(s2);
	}
	
	public Sequence(Sequence s1, String str, boolean begin){
		super();
		if(begin){
			this.add(str);
			addSequence(s1);
		}else{
			addSequence(s1);
			this.add(str);
		}
	}
	
	public Sequence(ArrayList<String> list){
		super();
		for(int i=0; i<list.size(); i++){
			this.add(list.get(i));
		}
	}
	
	
	/**
	 * compares this Sequence with another Sequence
	 * @param o
	 * @return true if sequences are equal
	 * @author Stefan 19.07.2016
	 */
	@Override
	public boolean equals(Object o){
		Sequence s = (Sequence)o;
		if(this.size() == s.size()){
			for(int i=0; i<s.size(); i++){
				if(!get(i).equals(s.get(i))){
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public float getSubCount(){
		return supCount;
	}
	
	public void setSubCount(float supCount){
		this.supCount = supCount;
	}
	
	public void addSequence(Sequence s){
		for(int i=0; i<s.size();i++){
			this.add(s.get(i));
		}
	}
	
	/**
	 * Creates a subsequence starting with index begin and ending with index end
	 * @param begin
	 * @param end
	 * @return
	 * @author Stefan 19.07.2016
	 */
	public Sequence getSubSequence(int begin, int end){
		Sequence s = new Sequence();
		for(int i=0; i<this.size(); i++){
			if(i>= begin && i<=end-1){
				s.add(this.get(i));
			}
		}
		return s;
	}
	
	/**
	 * creates all subsequences 
	 * @return
	 * @author Stefan
	 */
	public ArrayList<Sequence> buildAllSubsequences() {
		ArrayList<Sequence> allSeqs = new ArrayList<>();
		
	    for (int from = 0; from < this.size(); from++) {
	        for (int to = from + 1; to <= this.size(); to++) {
	        	if(from < to){
	        		if(!(from == 0 && to == this.size())){
	        			allSeqs.add(getSubSequence(from, to));
	        		}
	        	}
	        }
	    }
		
		return allSeqs;
	}
	
	/**
	 * generates the powerset
	 * @param originalSet
	 * @return
	 * @author Stefan
	 */
	public static <T> Set<Set<T>> powerSet(Set<T> originalSet) {
	    Set<Set<T>> sets = new HashSet<Set<T>>();
	    if (originalSet.isEmpty()) {
	    	sets.add(new HashSet<T>());
	    	return sets;
	    }
	    List<T> list = new ArrayList<T>(originalSet);
	    T head = list.get(0);
	    Set<T> rest = new HashSet<T>(list.subList(1, list.size())); 
	    for (Set<T> set : powerSet(rest)) {
	    	Set<T> newSet = new HashSet<T>();
	    	newSet.add(head);
	    	newSet.addAll(set);
	    	sets.add(newSet);
	    	sets.add(set);
	    }		
	    return sets;
	}
	
	/**
	 * determines whether this is a subsequence of s
	 * @param s
	 * @return
	 * @author Stefan 19.07.2016
	 */
	public boolean isSubsequence(Sequence s){
		return Collections.indexOfSubList(s, this) > -1;
	}
	
	public String toString(){
		String str = "";
		str +="[";
		for(int i=0; i<this.size(); i++){
			if(i== this.size()-1){
				str += this.get(i);
			}else{
				str += this.get(i) + "; ";
			}
		}
		str += "]";
		return str;
	}
}
