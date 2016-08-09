package uni.mannheim.teamproject.diabetesplaner.Utility;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

//import org.deckfour.xes.model.XLog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import uni.mannheim.teamproject.diabetesplaner.R;

/**
 * Created by Stefan
 * Class with utility functions
 */
public class Util {

	public static final double ROUND_FACTOR = 10d;


	/**
	 * converts an ArrayList<String> to String array
	 * @param tmpList ArrayList<String>
	 * @return String[]
	 */
	public static String[] toArray(ArrayList<String> tmpList){
		String[] stockArr = new String[tmpList.size()];
		return tmpList.toArray(stockArr);
	}

	/**
	 * writes a ArrayList into another ArrayList
	 * @param source ArrayList
	 * @param dest ArrayList
	 */
	public static void writeListToList(ArrayList<String[]> source, ArrayList<String[]> dest){
		dest.clear();
		for(int i=0; i<source.size(); i++){
			dest.add(source.get(i));
		}
	}

	/**
	 * converts a String[] to an ArrayList<String>
	 * @param array String[]
	 * @return ArrayList<String>
	 */
	public static ArrayList<String> toArrayList(String[] array){
		ArrayList<String> list = new ArrayList<>();
		for(int i=0; i<array.length; i++){
			list.add(array[i]);
		}
		return list;
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
	 * creates a test data sheet with attributes "minuteOfDay" and "dayOfWeek"
	 * @param dest destination path
	 * @param dayOfWeek actual day
	 */
	public static void createTestData(String dest, int dayOfWeek){
		ArrayList<String[]> list = new ArrayList<>();
		//init first line
		String[] firstLine = new String[2];
		firstLine[0] = "minuteOfDay";
		firstLine[1] = "dayOfWeek";
		list.add(firstLine);

		for(int i=0; i<1440; i++){
			String[] tmp = new String[2];
			tmp[0] = String.valueOf(i);
			tmp[1] = String.valueOf(dayOfWeek);
			list.add(tmp);
		}
		write(list, dest);
	}

	/**
	 * finds column that is called "starttime"
	 * @param list
	 * @return
	 */
	public static Integer getStartTimeIndex(ArrayList<String[]> list){
		for(int i=0; i<list.get(0).length; i++){
			if(list.get(0)[i].equals("starttime")){
				return i;
			}
		}
		return null;
	}

	/**
	 * finds column that is called "endtime"
	 * @param list
	 * @return
	 */
	public static Integer getEndTimeIndex(ArrayList<String[]> list){
		for(int i=0; i<list.get(0).length; i++){
			if(list.get(0)[i].equals("endtime")){
				return i;
			}
		}
		return null;
	}

	/**
	 * prints a csv file and converts the start and end date from a timestamp to a readable date
	 * @param list
	 */
	public static void printList(ArrayList<String[]> list){
		int startIndex = getStartTimeIndex(list);
		int endIndex = getEndTimeIndex(list);

		for(int i=1; i<list.size(); i++){
			for(int j=0; j<list.get(i).length; j++){
				String name = list.get(0)[j];
				String cell = list.get(i)[j];
				if(j==startIndex || j== endIndex){
					System.out.println(name + ": " + TimeUtils.getDate(cell));
				}else {
					System.out.println(name + ": " + cell);
				}
			}
			System.out.println();
		}
	}

	/**
	 * prints the final list with the model
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

	/**
	 * creates a filepath
	 * @return
	 */
	public static File createImageFile(){
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES);
		File image = null;
		try {
			image = File.createTempFile(
					imageFileName,  /* prefix */
					".jpg",         /* suffix */
					storageDir      /* directory */
			);

			// Save a file: path for use with ACTION_VIEW intents
			String mCurrentPhotoPath = "file:" + image.getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}


		return image;
	}

	/**
	 * returns the uri of a bitmap
	 * @param inContext
	 * @param inImage
	 * @return
	 */
	public static Uri getImageUri(Context inContext, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
		return Uri.parse(path);
	}

	/**
	 * compresses a bitmap width to the screen width and adapts the height percentually
	 * @param mCurrentPhotoPath
	 * @return
	 */
	public static Bitmap getCompressedPic(String mCurrentPhotoPath) {
		return compressPic(mCurrentPhotoPath, 0);
	}

	/**
	 * compresses a bitmap width to the width parameter and adapts the height percentually
	 * @param mCurrentPhotoPath
	 * @return
	 */
	public static Bitmap getCompressedPic(String mCurrentPhotoPath, int width) {
		return compressPic(mCurrentPhotoPath, width);
	}


	/**
	 * @author Stefan
	 * compresses a pic from a path with a certain width.
	 * @param mCurrentPhotoPath
	 * @param width width to compress. If 0 width of screen is taken
	 * @return
	 */
	private static Bitmap compressPic(String mCurrentPhotoPath, int width){
		// Get the dimensions of the Screen
		DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
		float targetW = dm.widthPixels;
		float targetH = dm.heightPixels;
		if(width != 0) {
			targetW = width;
		}

		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		float photoW = bmOptions.outWidth;
		float photoH = bmOptions.outHeight;

		// Determine how much to scale down the image
		float scaleFactor = Math.min(photoW/targetW, photoH/targetH);

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = (int)scaleFactor;

		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		float factor = targetW/bitmap.getWidth();

		Bitmap image = Bitmap.createScaledBitmap(bitmap, (int)(targetW), (int)(photoH*factor), false);
		return image;
	}

//	public static XLog createXLog(){
//		ArrayList<String[]> list = AppGlobal.getHandler().getAllEvents(AppGlobal.getHandler());
//		//creates a CaseCreator object with the CSV file in an ArrayList
//		CaseCreator creator = new CaseCreator(list);
//		//splits the data into cases and adds a column for the case id for each entry
//		creator.createCases();
//		//adds a column with the day of the week
////		creator.addDayOfWeek();
////		Build the XLog from the List
//		LogBuilder builder = new LogBuilder();
//		builder.startLog("ActivityLog");
//		list = creator.getList();
//		String caseHelper = "default";
////		iterate through the event list with cases
//		for (int i=0;i<list.size();i++){
////			If it is the same case then fill the trace with events
//			if(list.get(i)[0].equals(caseHelper)){
////				Add Start Node of Activity
//				builder.addEvent(list.get(i)[2]);
//				builder.addAttribute("Activity",list.get(i)[2]);
//				builder.addAttribute("ID", list.get(i)[1]);
//				builder.addAttribute("time:timestamp", list.get(i)[3]);
//				builder.addAttribute("lifecyle:transition","start");
////				Add Complete Node of an Activity
//				builder.addEvent(list.get(i)[2]);
//				builder.addAttribute("Activity",list.get(i)[2]);
//				builder.addAttribute("ID", list.get(i)[1]);
//				builder.addAttribute("lifecyle:transition","complete");
//				builder.addAttribute("time:timestamp", list.get(i)[4]);
//			}else{
////				Add a new Trace to the Builder [this happens for every case]
//				builder.addTrace(list.get(i)[0]);
//			}
//			caseHelper = list.get(i)[0];
//		}
////		create the XLog Object
//		XLog log = builder.build();
//		return log;
//	}

	/***
	 * Converts mg/dl in mmol/l
	 * @param mg
	 * @return mmol/l
	 */
	public static double miligram_to_mol(double mg){

		return Math.round(mg * 0.0555*ROUND_FACTOR)/ROUND_FACTOR;
	}

	/***
	 * Converts mmol/l in mg/dl
	 * @param mmol
	 * @return mg/dl
	 */
	public static double mmol_to_milligram(double mmol){
		return Math.round(mmol * 18.0182*ROUND_FACTOR)/ROUND_FACTOR;
	}

	/***
	 * Converts HbA1c percentage to mg/dl
	 * @param percent
	 * @return mg/dl
	 */
	public static double percentage_to_mg(double percent){
		return Math.round((percent*33.3-86.0)*ROUND_FACTOR)/ROUND_FACTOR;
	}

	/***
	 * Converts mg/dl to HbA1c percentage
	 * @param mg
	 * @return percentage of HbA1c
	 */
	public static double mg_to_percentage(double mg){
		return Math.round(((mg+86.0)/33.3)*ROUND_FACTOR)/ROUND_FACTOR;
	}


	public static double Units_to_ml(double units){
		return units/100;
	}

	public static double ml_to_Units(double ml){
		return ml* 100;
	}

	// created by Naira, for the drop down animation of the measurement pop up in Entry screen
	/**
	 *
	 * @param ctx
	 * @param v
	 */
	public static void slide_down(Context ctx, View v){
		Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);
		if(a != null){
			a.reset();
			if(v != null){
				v.clearAnimation();
				v.startAnimation(a);
			}
		}
	}

	public static void slide_up(Context ctx, View v){
		Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);
		if(a != null){
			a.reset();
			if(v != null){
				v.clearAnimation();
				v.startAnimation(a);
			}
		}
	}
}
