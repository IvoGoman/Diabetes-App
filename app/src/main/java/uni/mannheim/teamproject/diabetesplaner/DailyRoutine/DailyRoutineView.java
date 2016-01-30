package uni.mannheim.teamproject.diabetesplaner.DailyRoutine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import java.util.Calendar;

import uni.mannheim.teamproject.diabetesplaner.R;


public class DailyRoutineView extends View {

    public static final String TAG = DailyRoutineView.class.getSimpleName();
    private int endInMinOfDay;
    private int startInMinOfDay;
    private String durationAsString;
    private int activity = 0;
    private int subactivity;
    private String starttime;
    private String endtime;
    private int state;

    Paint borderPaint = new Paint();
    Paint upperPaint = new Paint();
    Paint lowerPaint = new Paint();
    Paint font = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
    Paint fontSubActivity = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
    Paint fontDur = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
    Paint done = new Paint();
    Paint open = new Paint();
    Paint upline = open;
    Paint downline = open;
    Paint dot = open;

    private int heightUpper = getpx(40);
    private int heightLower = getpx(30);
    private int offsetL1 = getpx(11);
    private int offsetL = getpx(10)+offsetL1;
    private int dotOffset = getpx(25);
    private int lineOffset = getpx(5);
    private int marginTop = getpx(8);
    private int radius = getpx(5);
    private int borderWidth = getpx(2);

    int innerColor = Color.parseColor("#f2f2f2");
    int innerColor2 = Color.parseColor("#d8d8d8");
    int borderColor = Color.parseColor("#7e7e7e");
    int textColor = Color.parseColor("#2a2a2a");
    int doneColor = Color.parseColor("#3f3f3f");
    int openColor = Color.parseColor("#939393");

    public DailyRoutineView(Context context) {
        super(context);
        initColors();
        initPaints();
    }
    public DailyRoutineView(Context context, int activity, int subactivity, String starttime, String endtime) {
        super(context);
        this.activity = activity;
        this.subactivity = subactivity;
        this.starttime = starttime;
        this.startInMinOfDay = getMinutesOfDay(starttime);
        this.endtime = endtime;
        this.endInMinOfDay = getMinutesOfDay(endtime);
        durationAsString = getDuration();
        setState(false);

        initColors();
        initPaints();
    }

    /**
     * inits special Colors
     */
    public void initColors(){
        innerColor = ContextCompat.getColor(this.getContext(), getColor(activity));
        Log.d(TAG, String.valueOf(innerColor));

        ColorDrawable cd = new ColorDrawable(innerColor);
        int col = cd.getColor();
        int alpha = cd.getAlpha();
        int red = Color.red(col);
        int green = Color.green(col);
        int blue = Color.blue(col);

        innerColor2 = Color.argb(alpha, red-10, green-10, blue-10);

    }

    /**
     * inits all Paints
     */
    public void initPaints(){
        borderPaint.setColor(textColor);
        borderPaint.setStrokeWidth(borderWidth);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setAntiAlias(true);

        upperPaint.setColor(innerColor);

        lowerPaint.setColor(innerColor2);

        font.setColor(Color.BLACK);
        font.setTextSize(getpx(20));

        fontSubActivity.setColor(Color.BLACK);
        fontSubActivity.setTextSize(getpx(14));

        fontDur.setColor(textColor);
        fontDur.setTextSize(getpx(11));
        //fontDur.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        done.setColor(doneColor);
        done.setStrokeWidth(getpx(3));

        open.setColor(openColor);
        open.setStrokeWidth(getpx(3));

    }

    //TODO: initialize all objects only once maybe in the constructor
    @Override
    public void onDraw(Canvas canvas) {

        //upper rectangle
        Rect front = new Rect(getLeft()+offsetL, 0, getRight()-getpx(8), heightUpper);
        canvas.drawRect(front, upperPaint);

        //lower rectangle
        Rect front2 = new Rect(getLeft()+offsetL, heightUpper, getRight()-getpx(8), heightLower+heightUpper);
        canvas.drawRect(front2, lowerPaint);

        //arrow-triangle
        Point a = new Point(getLeft()+offsetL1, dotOffset);
        Point b = new Point(getLeft()+offsetL, dotOffset-getpx(10));
        Point c = new Point(getLeft()+offsetL, dotOffset+getpx(10));

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(b.x, b.y);
        path.lineTo(c.x, c.y);
        path.lineTo(a.x, a.y);
        path.close();

        canvas.drawPath(path, upperPaint);

        //determine colors for state
        switch (state){
            case 1:
                upline = open;
                downline = open;
                dot = open;
                break;
            case 2:
                upline = done;
                downline = open;
                dot = done;
                //border
                Point p1 = new Point(getLeft()+offsetL, borderWidth/2);
                Point p2 = new Point(getRight()-getpx(8)-borderWidth/2, borderWidth/2);
                Point p3 = new Point(getRight()-getpx(8)-borderWidth/2, heightLower+heightUpper);
                Point p4 = new Point(getLeft()+offsetL, heightLower+heightUpper);

                Path border = new Path();
                border.moveTo(p1.x, p1.y);
                border.lineTo(p2.x, p2.y);
                border.lineTo(p3.x, p3.y);
                border.lineTo(p4.x, p4.y);
                border.lineTo(c.x, c.y);
                border.lineTo(a.x, a.y);
                border.lineTo(b.x, b.y);

                border.close();

                canvas.drawPath(border, borderPaint);
                break;
            case 3:
                upline = done;
                downline = done;
                dot = done;
                break;
        }
        //dot
        canvas.drawCircle(getLeft(), dotOffset, radius, dot);
        //upper done
        canvas.drawLine(getLeft(), getpx(0), getLeft(), dotOffset - radius - lineOffset, upline);
        //lower done
        canvas.drawLine(getLeft(), dotOffset + radius + lineOffset, getLeft(), heightLower + heightUpper + marginTop, downline);

        //activity text
        canvas.drawText(getActivity(activity), getLeft() + getpx(10) + offsetL, (front.height() / 2) + (font.getTextSize() / 2), font);
        //subactivity text
        canvas.drawText(getSubactivity(subactivity), getLeft() + getpx(20) + font.measureText(getActivity(activity)) + offsetL, (front.height() / 2) + (font.getTextSize() / 2), fontSubActivity);
        //duration text
        canvas.drawText("Duration: " + durationAsString, getRight() - getpx(20) - fontDur.measureText("Duration: " + durationAsString), (front.height() / 2) + (font.getTextSize() / 2), fontDur);
        //start text
        canvas.drawText("Start: " + starttime, getLeft() + getpx(10) + offsetL, (front2.height() / 2) + front.height() + fontDur.getTextSize() / 2, fontDur);
        //end text
        canvas.drawText("End: " + endtime, getRight() - getpx(20) - fontDur.measureText("End: " + endtime), (front2.height() / 2) + fontDur.getTextSize() / 2 + front.height(), fontDur);
    }

    /**
     * get the String to the activity id
     * TODO should be read from the database!
     * @param id activity id
     * @return name of activity
     */
    public String getActivity(int id){
        switch (id){
            case 1: return "Schlafen";
            case 2: return "Essen/Trinken";
            case 3: return "Körperpflege";
            case 4: return "Transportmittel benutzen";
            case 5: return "Entspannen";
            case 6: return "Fortbewegen (mit Gehilfe)";
            case 7: return "Medikamente einnehmen";
            case 8: return "Einkaufen";
            case 9: return "Hausarbeit";
            case 10: return "Essen zubereiten";
            case 11: return "Geselligkeit";
            case 12: return "Fortbewegen";
            case 13: return "Schreibtischarbeit";
            case 14: return "Sport";
            default: return "unknown activity";
        }
    }

    /**
     * takes start and endtime as String in HH:mm format and returns the duration
     * @return duration
     */
    public String getDuration(){
        int start = startInMinOfDay;
        int end = endInMinOfDay;

        int duration = end-start;
        Log.d(TAG, String.valueOf(duration));
        return getDurationAsString(duration);
    }

    /**
     * takes the duration in minutes as input and outputs it in the 2h 22min format
     * @param duration duration in minutes
     * @return duration
     */
    public String getDurationAsString(int duration){
        int min = duration%60;
        int h = duration/60;

        if(h>0) {
            return h + "h " + min + "min";
        }else{
            return min + "min";
        }
    }

    /**
     * returns subactivity TODO should be read from the database
     * @param id subactivity id
     * @return name of activity
     */
    public String getSubactivity(int id){
        switch (id){
            case 1: return "Joggen";
            case 2: return "Biken";
            case 3: return "Climbing";
            default: return "";
        }
    }

    /**
     * returns the px value to a dp value
     * @param dp dp value
     * @return px
     */
    public int getpx(int dp){
        return (int)(dp*getResources().getDisplayMetrics().density);
    }

    /**
     * returns the dp value for a px value
     * @param px px vlaue
     * @return dp
     */
    public int getdp(int px){
        return (int)(px/getResources().getDisplayMetrics().density);
    }

    /**
     * returns the position of the dot
     * @return point
     */
    public Point getDot(){
        return new Point(getLeft(),dotOffset);
    }

    /**
     * returns the total height of a activity item
     * @return total height
     */
    public int getTotalHeight(){
        return (int) (heightLower+heightUpper + marginTop);
    }

    /**
     * checks if actual time is inbetween
     * @param minutes minutes of the actual time
     * @param hour hours of the actual time
     * @return if this is the actual activity
     */
    private boolean isRunning(int minutes, int hour){
        int time = getMinutesOfDay(hour+":"+minutes);
        if(startInMinOfDay<=time && time<endInMinOfDay){
            return true;
        }else{
            return false;
        }
    }

    /**
     * checks if activity is finished
     * @param minutes minutes of actual time
     * @param hour hours of actual time
     * @return isFinished
     */
    public boolean isFinished(int minutes, int hour){
        int time = getMinutesOfDay(hour + ":" + minutes);
        if(endInMinOfDay<=time){
            return true;
        }else{
            return false;
        }
    }

    /**
     * checks if activity is remaining
     * @param minutes minutes of actual time
     * @param hour hours of actual time
     * @return isRemaining
     */
    public boolean isRemaining(int minutes, int hour){
        int time = getMinutesOfDay(hour + ":" + minutes);
        if(time<startInMinOfDay){
            return true;
        }else{
            return false;
        }
    }

    /**
     * sets the state of the activity
     * 1: is remaining
     * 2: is running
     * 3: is finished
     * @param isArchieved true if daily routine is archieved
     */
    public void setState(boolean isArchieved) {
        Calendar c = Calendar.getInstance();
        int minutes = c.get(Calendar.MINUTE);
        int hour = c.get(Calendar.HOUR_OF_DAY);

        if(isArchieved){
            state = 3;
        }else {
            if (isRemaining(minutes, hour)) {
                state = 1;
            } else if (isRunning(minutes, hour)) {
                state = 2;
            } else if (isFinished(minutes, hour)) {
                state = 3;
            }
        }
    }

    /**
     * takes the time in HH:mm and returns it in minutes of the day
     * @param time a time
     * @return minutes of the day
     */
    public int getMinutesOfDay(String time){
        String[] tmp = time.split(":");
        return Integer.parseInt(tmp[0])*60+Integer.parseInt(tmp[1]);
    }

    /**
     * returns the color for an activity
     * @param activityid id of activity
     * @return id of color in resources
     */
    public int getColor(int activityid){
        switch (activityid){
            case 1: return R.color.good;
            case 2: return R.color.bad;
            case 3: return R.color.no_influence;
            case 4: return R.color.potential_bad;
            case 5: return R.color.good;
            case 6: return R.color.good;
            case 7: return R.color.good;
            case 8: return R.color.no_influence;
            case 9: return R.color.no_influence;
            case 10: return R.color.no_influence;
            case 11: return R.color.good;
            case 12: return R.color.good;
            case 13: return R.color.potential_bad;
            case 14: return R.color.good;
            default: return R.color.no_influence;
        }
    }
}