package uni.mannheim.teamproject.diabetesplaner.Utility;

import android.content.Context;
import android.text.format.DateFormat;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import uni.mannheim.teamproject.diabetesplaner.R;

/**
 * Created by Stefan on 26.04.2016.
 */
public class TimeUtils {
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
     * converts a timestamp to a Calendar instance
     * @param timestamp as long
     * @return Calendar object
     * @author Stefan 09.07.2016
     */
    public static Calendar getCalendar(long timestamp){
        Date date = new Date(timestamp);
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
     * Methods converts a String in Format YYYY-MM-DD HH:MM:SS to a Data Object
     * @param dateString
     * @return
     */
    public static Date getDateFromString(String dateString){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    /**
     * @author Stefan 26.04.2016
     * converts a timestamp to a Date instance
     * @param timestamp as String
     * @return Date object
     */
    public static Date getDate(long timestamp){
        Date date = new Date(timestamp);
        return date;
    }

    /**
     * converts a timestamp long to a Datestring instance
     * @param timestamp as String
     * @return String object
     */
    public static String getTimeStampAsDateString(long timestamp){
        Date date = new Date(timestamp);
        String sDate = TimeUtils.dateToDateTimeString(date);
        return sDate;
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
//			sb.ic_delete(0,1);
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
     * @author Ivo Gosemann 18.03.2016
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
     * @author Ivo Gosemann 18.03.2016
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

    public static Date[] getDate(Date date, int minutes) {
        Date[] result = new Date[2];
        int hour, minute;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
//        Start Date is Date + 1 Minute;
        minute = cal.get(Calendar.MINUTE) + 1;
        cal.set(Calendar.MINUTE,minute);
        result[0] = cal.getTime();
//        End Date is Date + minutes
        hour = cal.get(Calendar.HOUR_OF_DAY) + minutes / 60;
        minute = cal.get(Calendar.MINUTE) + minutes % 60;
//        if minute is over 60 another hour is added
        if(minute > 59){
            hour += minute / 60;
            minute = minute % 60;
        }
        if(hour < 24 | (hour == 24 && minute == 0)) {
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minute);
        } else{
            cal.set(Calendar.HOUR_OF_DAY,23);
            cal.set(Calendar.MINUTE,59);
        }
        cal.set(Calendar.SECOND, 0);
        result[1] = cal.getTime();
        return result;
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

    /**
     * returns time in format HH:mm if timeformat is 24h and in format KK:mm AM/PM if timeformat is 12h
     * @param timestamp
     * @return String
     * @author Stefan 26.04.2016
     */
    public static String getTimeInUserFormat(long timestamp, Context context){
        return getTimeInUserFormat(getDate(timestamp), context);
    }

    /**
     * @author Ivo 08.04.2016
     * Convert a time window from a string array to a corresponding timestamp Long array
     * @param timeWindow array containing dates as string values
     * @return date array converted to long timestamp array
     */
    public static Long[] convertDateStringToTimestamp(String[] timeWindow) {
        Timestamp timestampStart = null, timestampEnd = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date parsedStart = dateFormat.parse(timeWindow[0]);
            Date parsedEnd = dateFormat.parse(timeWindow[1]);
            timestampStart = new java.sql.Timestamp(parsedStart.getTime());
            timestampEnd = new java.sql.Timestamp(parsedEnd.getTime());
        }catch(Exception e){//this generic but you can control another types of exception
            e.printStackTrace();
        }

        Long [] timeWindowLong = {timestampStart.getTime()/1000, timestampEnd.getTime()/1000};
        return timeWindowLong;
    }

    /**
     * @author Stefan 12.04.2016
     * checks if a timestamp is between a start and an end date
     * @param start
     * @param end
     * @param date
     * @return true if time is in between
     */
    public static boolean isTimeInbetween(Date start, Date end, Date date){
        if(start.compareTo(date) <= 0 && end.compareTo(date) >= 0){
            return true;
        }else{
            return false;
        }
    }

    /**
     * get the minute of a day by passing the timestamp
     * @param timestamp
     * @return minute of day
     * @author Stefan 09.07.2016
     */
    public static int getMinutesOfDay(long timestamp){

        Calendar calendar = getCalendar(timestamp);
        int hour = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
        int minute = calendar.get(Calendar.MINUTE);

        return hour*60 + minute;
    }

    /**
     * creates a timestamp out of the minOfDay. Takes the actual date for the timestamp since
     * the minOfDay does not specify information like day, month or year.
     * @param minOfDay
     * @return timestamp
     * @author Stefan 09.07.2015
     */
    public static long minutesOfDayToTimestamp(int minOfDay){
        int min = minOfDay%60;
        int hour = minOfDay/60;

        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.MINUTE, min);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        return cal.getTimeInMillis();
    }

    /**
     * get the day of the week
     * @param dayOfWeek integer value
     * @return day of the week as String
     * @author Stefan
     */
    public String getDayOfWeek(int dayOfWeek, Context c){
        switch (dayOfWeek){
            case 1: return c.getResources().getString(R.string.Sunday);
            case 2: return c.getResources().getString(R.string.Monday);
            case 3: return c.getResources().getString(R.string.Tuesday);
            case 4: return c.getResources().getString(R.string.Wednesday);
            case 5: return c.getResources().getString(R.string.Thursday);
            case 6: return c.getResources().getString(R.string.Friday);
            case 7: return c.getResources().getString(R.string.Saturday);
            default: return "";
        }
    }


    /**
     * Returns the date adapted to the phones date format
     * @return date as String
     * @author Stefan
     */
    public static String getDateAsString(){
        Date date = Calendar.getInstance(Locale.getDefault()).getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd.MM.yyyy");
        String dateString = dateFormat.format(date);

        return dateString;
    }

    /**
     * @param timestamp
     * @return 0 for AM or 1 for PM
     */

    public static int isAM(long timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Calendar calendar = TimeUtils.getCalendar(timestamp);
        int am_pm = calendar.get(Calendar.AM_PM);
        return am_pm;
    }
}
