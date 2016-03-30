package uni.mannheim.teamproject.diabetesplaner.DataMining;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Stefan
 * Class with utility functions
 */
public class Util {

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
	 * converts a timestamp String to a Calendar instance
	 * @param timestamp as String
	 * @return Calendar object
	 */
	public static Calendar getCalendar(String timestamp){
		Date date = new Date(Long.parseLong(timestamp));
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	/**
	 * converts a timestamp String to a Date instance
	 * @param timestamp as String
	 * @return Date object
	 */
	public static Date getDate(String timestamp){
		Date date = new Date(Long.parseLong(timestamp));
		return date;
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
	 * converts a string time into a timestamp
	 * @param time
	 * @return
	 */
	public static long stringToTimestamp(String time){
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

	public static String dateToDateTimeString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String dateString = sdf.format(date);
		return dateString;
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
					System.out.println(name + ": " + getDate(cell));
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
	 * converts a string time to a date time
	 * @param time a String time in format HH:mm
	 * @return time as a Date object
	 */
	public static Date getTime(String time){
		Date timeAsDate = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
			timeAsDate = dateFormat.parse(time);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return timeAsDate;
	}

	/**
	 * converts a time in HH:mm to String
	 * @param date
	 * @return
	 */
	public static String timeToString(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
		String tmp = dateFormat.format(date);
//		if(tmp.charAt(0) == '0'){
//			Log.d("Meins","hier");
//			StringBuilder sb = new StringBuilder(tmp);
//			sb.delete(0,1);
//			tmp = sb.toString();
//		}
//		Log.d("Meins",tmp);

		return tmp;
	}

	/**
	 * add or subtract minutes from a day time
	 * @param date the time
	 * @param minutes number of minutes to add/subtract
	 * @return edited date
	 */
	public static Date addMinuteFromDate(Date date, int minutes){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, minutes);
		return cal.getTime();
	}

	public static Date setTime(String dateString, String starttime) {
		Date date = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		dateString = dateString + " " + starttime;
		try {
			date = dateFormat.parse(dateString);

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return date;
	}

	public static String convertDateToDateString(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = dateFormat.format(date);

		return dateString;
	}

	/**
	 * Method which combines the current date of the day with the start and endtime from the dialog
	 *
	 * @param date
	 * @param time
	 * @return
	 */
	public static String combineDateAndTime(Date date, Date time) {
		Date newdate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = sdf.format(date);
		sdf = new SimpleDateFormat("HH:mm");
		String timeString = sdf.format(time);
		dateString += " " + timeString;
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			newdate = sdf.parse(dateString);

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return sdf.format(newdate);
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
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Save a file: path for use with ACTION_VIEW intents
		String mCurrentPhotoPath = "file:" + image.getAbsolutePath();
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

	/**
	 * Ivo Gosemann 18.03.2016
	 * Converting a "yyyy-MM-dd HH:mm" String into a "HH:mm" String
	 * @param dateValue
     * @return a String representing the time as "HH:mm"
     */
	public static String dateToTimeString(String dateValue) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = null;
		String timeString ="";
		try {
			date = sdf.parse(dateValue);
			sdf = new SimpleDateFormat("HH:mm");
			timeString = sdf.format(date);
			return timeString;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return timeString;
	}

	/**
	 * Ivo Gosemann 18.03.2016
	 * Methods returns the current date as a Date
	 * @return date in the format "yyyy-MM-dd HH:mm"
     */
	public static Date getCurrentDate() {
		Calendar calendar = Calendar.getInstance();
		return calendar.getTime();
	}

	public static int getDuration(Date starttime, Date endtime) {

		long duration = endtime.getTime()-starttime.getTime();
		long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
		long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
		/**int [] time = {(int)diffInHours, (int) diffInMinutes};
		return time;**/
		return (int)diffInMinutes;
	}


	/**
	 * @auther Stefan 30.03.2016
	 * edits the minute and hour of an existing date object
	 * @param date
	 * @param hour
	 * @param minute
	 * @return
	 */
	public static Date getDate(Date date, int hour, int minute) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}

	/**
	 * @author Stefan 30.03.2016
	 * returns time in format HH:mm if timeformat is 24h and in format KK:mm AM/PM if timeformat is 12h
	 * @param date
	 * @return String
	 */
	public static String getTimeInUserFormat(Date date, Context context){
		String time = "";
		if(DateFormat.is24HourFormat(context)){
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
			time = sdf.format(date);
		}else{
			SimpleDateFormat sdf = new SimpleDateFormat("KK:mm a");
			time = sdf.format(date);
		}
		return time;
	}
}
