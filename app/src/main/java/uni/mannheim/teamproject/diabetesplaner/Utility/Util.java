package uni.mannheim.teamproject.diabetesplaner.Utility;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Domain.MeasureItem;
import uni.mannheim.teamproject.diabetesplaner.R;

//import org.deckfour.xes.model.XLog;

/**
 * Created by Stefan
 * Class with utility functions
 */
public class Util {

    public static final double ROUND_FACTOR = 10d;

    /**
     * converts a bloodsugar level into mol
     *
     * @param value
     * @param unit
     * @return
     * @author Stefan 08.09.2016
     */
    public static double convertBSToMol(double value, String unit) {
        switch (unit) {
            case "%":
                return Util.miligram_to_mol(Util.percentage_to_mg(value));

            case "mmol/l":
                return value;

            case "mg/dl":
                return Util.miligram_to_mol(value);
            default:
                return 0;
        }
    }

    /**
     * Converts Bloodsugar to Percent
     * @param value
     * @param unit
     * @return bloodsugar in percent
     */
    public static double convertBSToPercent(double value, String unit) {
        switch (unit) {
            case "%":
                return value;

            case "mmol/l":
                return Util.mg_to_percentage(Util.mmol_to_milligram(value));

            case "mg/dl":
                return Util.mg_to_percentage(value);
            default:
                return 0;
        }
    }

    /**
     * converts a bloodsugar level into mg/dl
     *
     * @param value
     * @param unit
     * @return
     * @author Stefan 08.09.2016
     */
    public static double convertBSToMG(double value, String unit) {
        switch (unit) {
            case MeasureItem.UNIT_PERCENT:
                return Util.percentage_to_mg(value);

            case MeasureItem.UNIT_MMOL:
                return Util.mmol_to_milligram(value);

            case MeasureItem.UNIT_MG:
                return value;
            default:
                return 0;
        }
    }

    /**
     * converts an ArrayList<String> to String array
     *
     * @param tmpList ArrayList<String>
     * @return String[]
     * @author Stefan
     */
    public static String[] toArray(ArrayList<String> tmpList) {
        String[] stockArr = new String[tmpList.size()];
        return tmpList.toArray(stockArr);
    }

    /**
     * writes a ArrayList into another ArrayList
     *
     * @param source ArrayList
     * @param dest   ArrayList
     * @author Stefan
     */
    public static void writeListToList(ArrayList<String[]> source, ArrayList<String[]> dest) {
        dest.clear();
        for (int i = 0; i < source.size(); i++) {
            dest.add(source.get(i));
        }
    }

    /**
     * converts a String[] to an ArrayList<String>
     *
     * @param array String[]
     * @return ArrayList<String>
     * @author Stefan
     */
    public static ArrayList<String> toArrayList(String[] array) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            list.add(array[i]);
        }
        return list;
    }

    /**
     * converts and ArrayList<ArrayList<String>> to an ArrayList <String[]>
     *
     * @param tmp ArrayList<ArrayList<String>>
     * @return ArrayList<String[]>
     * @author Stefan
     */
    public static ArrayList<String[]> convertToArrayListStringArray(ArrayList<ArrayList<String>> tmp) {
        ArrayList<String[]> str = new ArrayList<String[]>();
        for (int i = 0; i < tmp.size(); i++) {
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
     * @param csvData Data Array that contains the data to write
     * @param saveAs  The filepath and name of the CSV file
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
     *
     * @param dest      destination path
     * @param dayOfWeek actual day
     * @author Stefan
     */
    public static void createTestData(String dest, int dayOfWeek) {
        ArrayList<String[]> list = new ArrayList<>();
        //init first line
        String[] firstLine = new String[2];
        firstLine[0] = "minuteOfDay";
        firstLine[1] = "dayOfWeek";
        list.add(firstLine);

        for (int i = 0; i < 1440; i++) {
            String[] tmp = new String[2];
            tmp[0] = String.valueOf(i);
            tmp[1] = String.valueOf(dayOfWeek);
            list.add(tmp);
        }
        write(list, dest);
    }

    /**
     * finds column that is called "starttime"
     *
     * @param list
     * @return
     * @author Stefan
     */
    public static Integer getStartTimeIndex(ArrayList<String[]> list) {
        for (int i = 0; i < list.get(0).length; i++) {
            if (list.get(0)[i].equals("starttime")) {
                return i;
            }
        }
        return null;
    }

    /**
     * finds column that is called "endtime"
     *
     * @param list
     * @return
     * @author Stefan
     */
    public static Integer getEndTimeIndex(ArrayList<String[]> list) {
        for (int i = 0; i < list.get(0).length; i++) {
            if (list.get(0)[i].equals("endtime")) {
                return i;
            }
        }
        return null;
    }

    /**
     * prints a csv file and converts the start and end date from a timestamp to a readable date
     *
     * @param list
     * @author Stefan
     */
    public static void printList(ArrayList<String[]> list) {
        int startIndex = getStartTimeIndex(list);
        int endIndex = getEndTimeIndex(list);

        for (int i = 1; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).length; j++) {
                String name = list.get(0)[j];
                String cell = list.get(i)[j];
                if (j == startIndex || j == endIndex) {
                    System.out.println(name + ": " + TimeUtils.getDate(cell));
                } else {
                    System.out.println(name + ": " + cell);
                }
            }
            System.out.println();
        }
    }

    /**
     * prints the final list with the model
     *
     * @param list
     * @author Stefan
     */
    public static void print(ArrayList<String[]> list) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).length; j++) {

                if (j == 0) {
                    System.out.print(list.get(i)[j] + " ");
                    for (int k = 0; k < 20 - list.get(i)[j].length(); k++) {
                        System.out.print(" ");
                    }
                }
                if (j == 1) {
                    for (int k = 0; k < 9 - list.get(i)[j].length(); k++) {
                        System.out.print(" ");
                    }
                    System.out.print(list.get(i)[j] + " ");
                }
                if (j == 2) {
                    if (i != 0) {
                        for (int k = 0; k < 8 - list.get(i)[j].length(); k++) {
                            System.out.print(" ");
                        }
                    } else {
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
     *
     * @return
     * @author Stefan
     */
    public static File createImageFile() {
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
     *
     * @param inContext
     * @param inImage
     * @return
     * @author Stefan
     */
    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    /**
     * compresses a bitmap width to the screen width and adapts the height percentually
     *
     * @param mCurrentPhotoPath
     * @return
     * @author Stefan
     */
    public static Bitmap getCompressedPic(String mCurrentPhotoPath) {
        return compressPic(mCurrentPhotoPath, 0);
    }

    /**
     * compresses a bitmap width to the width parameter and adapts the height percentually
     *
     * @param mCurrentPhotoPath
     * @return
     * @author Stefan
     */
    public static Bitmap getCompressedPic(String mCurrentPhotoPath, int width) {
        return compressPic(mCurrentPhotoPath, width);
    }


    /**
     * compresses a pic from a path with a certain width.
     *
     * @param mCurrentPhotoPath
     * @param width             width to compress. If 0 width of screen is taken
     * @return
     * @author Stefan
     */
    private static Bitmap compressPic(String mCurrentPhotoPath, int width) {
        // Get the dimensions of the Screen
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        float targetW = dm.widthPixels;
        float targetH = dm.heightPixels;
        if (width != 0) {
            targetW = width;
        }

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        float photoW = bmOptions.outWidth;
        float photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        float scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = (int) scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        float factor = targetW / bitmap.getWidth();

        Bitmap image = Bitmap.createScaledBitmap(bitmap, (int) (targetW), (int) (photoH * factor), false);
        return image;
    }


    /***
     * Converts mg/dl in mmol/l
     *
     * @param mg
     * @return mmol/l
     * @author Stefan
     */
    public static double miligram_to_mol(double mg) {

        return Math.round(mg * 0.0555 * ROUND_FACTOR) / ROUND_FACTOR;
    }

    /***
     * Converts mmol/l in mg/dl
     *
     * @param mmol
     * @return mg/dl
     * @author Stefan
     */
    public static double mmol_to_milligram(double mmol) {
        return Math.round(mmol * 18.0182 * ROUND_FACTOR) / ROUND_FACTOR;
    }

    /***
     * Converts HbA1c percentage to mg/dl
     *
     * @param percent
     * @return mg/dl
     * @author Stefan
     */
    public static double percentage_to_mg(double percent) {
        return Math.round((percent * 33.3 - 86.0) * ROUND_FACTOR) / ROUND_FACTOR;
    }

    /***
     * Converts mg/dl to HbA1c percentage
     *
     * @param mg
     * @return percentage of HbA1c
     * @author Stefan
     */
    public static double mg_to_percentage(double mg) {
        return Math.round(((mg + 86.0) / 33.3) * ROUND_FACTOR) / ROUND_FACTOR;
    }

    /**
     * units to ml/cc conversion
     * @param units
     * @return ml
     * @author Naira
     */
    public static double Units_to_ml(double units) {
        return units / 100;
    }

    /**
     * ml/cc to units conversion
     * @param ml
     * @return units
     * @author Naira
     */
    public static double ml_to_Units(double ml) {
        return ml * 100;
    }

    /**
     * drop down animation of the measurement dialog
     * @param ctx
     * @param v
     * @author Naira
     */
    public static void slide_down(Context ctx, View v) {
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);
        if (a != null) {
            a.reset();
            if (v != null) {
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    public static void slide_up(Context ctx, View v) {
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);
        if (a != null) {
            a.reset();
            if (v != null) {
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    /**
     * reads the activities CSV
     *
     * @return String[id, activity name, superactivitiy id]
     * @author Stefan 09.09.2016
     */
    public static ArrayList<String[]> readActivities(String filename, Context c) {
        ArrayList<String[]> list = new ArrayList<String[]>();
        CSVReader reader;
        try {
            reader = new CSVReader(new InputStreamReader(c.getAssets().open(filename)));

            String[] nextLine;
            reader.readNext();
            while ((nextLine = reader.readNext()) != null) {
                if(nextLine.length == 4) {
//                    Log.d("tag","activity: " +  nextLine[0] + ", " + nextLine[1] + ", " + nextLine[2]);
                    list.add(nextLine);
                }
            }
        } catch (IOException e) {
            Log.e("readActivities", filename + ": "+e+"");
            e.printStackTrace();
        }
        return list;
    }

    /**
     * reads the subactivities CSV
     *
     * @return String[id, activityId, name]
     * @author Stefan 09.09.2016
     */
    public static ArrayList<String[]> readSubActivities(String filename, Context c) {
        ArrayList<String[]> list = new ArrayList<String[]>();
        CSVReader reader;
        try {
            reader = new CSVReader(new InputStreamReader(c.getAssets().open(filename)));

            String[] nextLine;
            reader.readNext();
            while ((nextLine = reader.readNext()) != null) {
                if(nextLine.length == 4) {
//                    Log.d("tag", "subactivitiy: " + nextLine[0] + ", " + nextLine[1] + ", " + nextLine[2]);
                    list.add(nextLine);
                }
            }
        } catch (IOException e) {
            Log.e("readSubActivities",filename + ": "+ e+"");
            e.printStackTrace();
        }
        return list;
    }

    /**
     * checks if String is null, "null" or a valid String
     * @param tmp
     * @return null or valid String
     * @author Stefan 09.09.2016
     */
    public static String getValidString(String tmp){
        if(tmp == null){
            return null;
        }else{
            if(tmp.equals("null")){
                return null;
            }else if(tmp.equals("")){
                return null;
            }else{
                return tmp;
            }
        }
    }

    /**
     * checks whether a day should be retrieved as training data or not.
     * Checks if day is complete and if day is not the actual day (= day to predict)
     * @param day ArrayList that represents one day
     * @return true if day is complete, false otherwise
     * @author Stefan 13.09.2016
     */
    public static boolean checkDay(ArrayList<ActivityItem> day){
        boolean complete = isDayComplete(day);
//        boolean complete = true;
        boolean isToday = isToday(day);

        return complete && !isToday;
    }

    /**
     * checks if the day is today
     * @param day
     * @return
     * @author Stefan 15.09.2016
     */
    public static boolean isToday(ArrayList<ActivityItem> day){
        if(day.size()>0){
            ActivityItem first = day.get(0);
            return TimeUtils.isSameDay(first.getStarttime(), new Date());
        }
        return false;
    }

    /**
     * checks whether a day is complete or not. In a complete day there is an activity specified for every single minute.
     * @param day ArrayList that represents one day
     * @return true if day is complete, false otherwise
     * @author Stefan 13.09.2016
     */
    public static boolean isDayComplete(ArrayList<ActivityItem> day){
        if(day.size()>0) {
            //check starttime of first
            if (TimeUtils.getMinutesOfDay(day.get(0).getStarttime().getTime()) != 0){
                return false;
            }
            //check endtime of last
            if(TimeUtils.getMinutesOfDay(day.get(day.size()-1).getEndtime().getTime()) != 1439){
                return false;
            }else{
                int prevEnd = 0;
                for(int i=0; i<day.size();i++){
                    if(i==0){
                        prevEnd = TimeUtils.getMinutesOfDay(day.get(i).getEndtime().getTime());
                    }else{
                        prevEnd = TimeUtils.getMinutesOfDay(day.get(i-1).getEndtime().getTime());
                        int currStart = TimeUtils.getMinutesOfDay(day.get(i).getStarttime().getTime());
                        if(currStart != (prevEnd+1)){
                            return false;
                        }
                    }
                }
                return true;
            }
        }else {
            return false;
        }
    }

    /**
     * returns the activity that is at the specified minute
     * @param day list that contains a daily routine
     * @param minute the  minute of interest
     * @return
     * @author Stefan 13.09.2016
     */
    public static ActivityItem getActivityAtMinute(ArrayList<ActivityItem> day, int minute){
        for(int i=0; i<day.size(); i++){
            ActivityItem item = day.get(i);
            int start = TimeUtils.getMinutesOfDay(item.getStarttime().getTime());
            int end = TimeUtils.getMinutesOfDay(item.getEndtime().getTime());

            if(start <= minute && minute <= end){
                return item;
            }
        }
        return null;
    }
}
