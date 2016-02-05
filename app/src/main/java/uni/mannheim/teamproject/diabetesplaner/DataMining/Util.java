package uni.mannheim.teamproject.diabetesplaner.DataMining;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
/**
 * Created by Stefan
 * Class with utility functions
 */
public class Util {

	/**
	 * converts a ArrayList<String> to String array
	 * @param tmpList ArrayList<String>
	 * @return String[]
	 */
	public static String[] toArray(ArrayList<String> tmpList){
		String[] stockArr = new String[tmpList.size()];
		return tmpList.toArray(stockArr);
	}

	/**
	 * converts and ArrayList<ArrayList<String>> to an ArrayList <String[]>
	 * @param tmp ArrayList<ArrayList<String>>
	 * @return ArrayList<String[]>
	 */
	public static ArrayList<String[]> convertToArrayListStringArray(ArrayList<ArrayList<String>> tmp){
		ArrayList<String[]> str = new ArrayList<String[]>();
		for(int i=0; i<tmp.size(); i++){
			str.add(Util.toArray(tmp.get(i)));
		}
		return str;
	}

	/**
	 * Reads an CSV file and creates a Assumes that the activity data file is
	 * joined with the activities.
	 *
	 * @param filename CSV file path
	 * @return the CSV file as ArrayList<String[]>
	 */
	@SuppressWarnings("resource")
	public static ArrayList<String[]> read(String filename) {
		ArrayList<String[]> list = new ArrayList<String[]>();

		//System.out.println("Read data: ");
		// Build reader instance
		// Read data.csv
		// Default seperator is comma
		// Default quote character is double quote
		// Start reading from line number 2 (line numbers start from zero)
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(filename), ',', '"', 0);

			// Read CSV line by line and use the string array as you want
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				if (nextLine != null) {
					list.add(nextLine);

					// Verifying the read data here
					//System.out.println(Arrays.toString(nextLine));
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * Writes a CSV file.
	 *
	 * @param csvData
	 *            Data Array that contains the data to write
	 * @param saveAs
	 *            The filepath and name of the CSV file
	 */
	public static void write(ArrayList<String[]> csvData, String saveAs) {
		String csv = saveAs;
		CSVWriter writer;
		try {
			writer = new CSVWriter(new FileWriter(csv));

			for (int i = 0; i < csvData.size(); i++) {
				// Write the record to file
				writer.writeNext(csvData.get(i));
			}

			// close the writer
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * prints a String[]
	 * @param list
	 */
	public static void print(ArrayList<String[]> list){
		for(int i=0; i<list.size(); i++){
			for(int j=0; j<list.get(i).length; j++){

				if(j==0){
					System.out.print(list.get(i)[j] + " ");
					for(int k=0; k<20-list.get(i)[j].length();k++){
						System.out.print(" ");
					}
				}
				if(j==1){
					for(int k=0; k<9-list.get(i)[j].length();k++){
						System.out.print(" ");
					}
					System.out.print(list.get(i)[j] + " ");
				}
				if(j==2){
					if(i!=0){
						for(int k=0; k<8-list.get(i)[j].length();k++){
							System.out.print(" ");
						}
					}else{
						System.out.print(" ");
					}
					System.out.print(list.get(i)[j] + " ");

				}
			}
			System.out.println();
		}
	}
}
