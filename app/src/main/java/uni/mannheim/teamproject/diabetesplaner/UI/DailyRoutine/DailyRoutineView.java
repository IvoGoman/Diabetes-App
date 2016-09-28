package uni.mannheim.teamproject.diabetesplaner.UI.DailyRoutine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Vibrator;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.R;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.ColorUtils;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;
import uni.mannheim.teamproject.diabetesplaner.Utility.Util;

/**
 * Created by Stefan
 */
public class DailyRoutineView extends View implements View.OnLongClickListener, View.OnClickListener, View.OnTouchListener {

    public static final String TAG = DailyRoutineView.class.getSimpleName();
    private ActivityItem activityItem;
    private int endInMinOfDay;
    private int startInMinOfDay;
    private String durationAsString;
    private int activity = 0;
    private int subactivity;
    private Date startDate;
    private Date endDate;
    private String bloodsugarText;
    private String insulinText;
    private String meal;
    private int state;
    private boolean touched = false;

    Paint borderPaint = new Paint();
    Paint upperPaint = new Paint();
    Paint lowerPaint = new Paint();
    Paint arrowPaint = new Paint();
    TextPaint textPaint = new TextPaint();
    TextPaint textPaintSub = new TextPaint();
    Paint font = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
    Paint fontSubActivity = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
    Paint fontDur = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
    Paint done = new Paint();
    Paint open = new Paint();
    Paint upline = open;
    Paint downline = open;
    Paint dot = open;
    Paint highlightPaint = new Paint();

    private int heightUpper;
    private int heightLower = getpx(30);
    private int radius = getpx(5);
    private int offsetL1 = getpx(11) +radius;
    private int offsetL = getpx(10) + offsetL1;
    private int dotOffset = getpx(25);
    private int lineOffset = getpx(5);
    private int textPadding = getpx(7);
    private int marginTop = getpx(8);
    private int borderWidth = getpx(2);
    private static Context context;

    int innerColor = Color.parseColor("#f2f2f2");
    int innerColor2 = Color.parseColor("#d8d8d8");
    int borderColor = Color.parseColor("#7e7e7e");
    int textColor = Color.parseColor("#2a2a2a");
    int doneColor = Color.parseColor("#3f3f3f");
    int openColor = Color.parseColor("#939393");
    int highlightColor = Color.argb(100, 255, 255, 255);

    private static boolean selectable = false;
    private boolean isSelected = false;

    private static ArrayList<DailyRoutineView> selectedActivities = new ArrayList<DailyRoutineView>();

    private StaticLayout sl;
    private Rect actRect;
    private Rect front;
    private Rect front2;
    private StaticLayout slsub;
    private Rect actRectSub;
    private Point a;
    private Point b;
    private Point c;
    private Point p1;
    private Point p2;
    private Point p3;
    private Point p4;
    private Path arrow = new Path();
    private Path border = new Path();
    private Path hl = new Path();
    private static DailyRoutineView current;
    private Rect actRectBlood;
    private StaticLayout slBlood;
    private Rect actRectInsulin;
    private StaticLayout slInsulin;
    private Rect actRectMeal;
    private StaticLayout slMeal;
    private Bitmap mealImage;
    private String imagePath;
    private Rect actRectImage;
    private Rect src;
    private Rect dest;


//    public static int testActivity = 0;


    public DailyRoutineView(Context context) {
        super(context);
        initColors();
        initPaints();
    }

    /**
     * initializes the activityItem representation and its attributes
     * @param context
     * @param activityItem
     * @author Stefan
     */
    public DailyRoutineView(Context context, ActivityItem activityItem){
        super(context);
        this.context = context;
        this.activityItem = activityItem;

//        if(testActivity == 1){
//            activityItem.setSubactivityId(4);
//            activityItem.setMeal("Pizza");
//        }
//        if(testActivity == 2){
//            activityItem.setIntensity(ActivityItem.INTENSITY_HIGH);
//        }
//
//        testActivity++;

        this.activity = activityItem.getActivityId();
        this.subactivity = activityItem.getSubactivityId();
        this.meal = Util.getValidString(activityItem.getMeal());
        this.imagePath = Util.getValidString(activityItem.getImagePath());
        System.out.println("ImagePath: " + imagePath);
        if(this.imagePath != null && this.imagePath.length() > 1 && !this.imagePath.equals("null")) {
            this.mealImage = Util.getCompressedPic(imagePath);
        }
        this.startDate = activityItem.getStarttime();
        this.endDate = activityItem.getEndtime();

        init();

//        Log.d(TAG, "Activity: " + AppGlobal.getHandler().getActionById(AppGlobal.getHandler(),activityItem.getActivityId()));
//        Log.d(TAG, "Subactivity: " + activityItem.getSubactivityId());
//        Log.d(TAG, "Starttime: " + activityItem.getStarttimeAsString());
//        Log.d(TAG, "Endtime: " + activityItem.getEndtimeAsString());
//        Log.d(TAG, "Meal: " + activityItem.getMeal());
//        Log.d(TAG, "Image: " + activityItem.getImagePath());
//        Log.d(TAG, "Intensity: " + activityItem.getIntensity());
    }

    /**
     * inits additional combonents
     * @author Stefan
     */
    public void init(){
        this.startInMinOfDay = getMinutesOfDay(startDate);
        this.endInMinOfDay = getMinutesOfDay(endDate);
        durationAsString = getDuration();
        setState(false);
        this.setOnTouchListener(this);
        this.setOnLongClickListener(this);
        this.setOnClickListener(this);
        this.setLongClickable(true);
        this.setClickable(true);
        setWillNotDraw(false);

        initColors();
        initPaints();
    }

    /**
     * inits special Colors
     * @author Stefan
     */
    public void initColors() {
        innerColor = ColorUtils.getColor(activityItem, getContext());

        ColorDrawable cd = new ColorDrawable(innerColor);
        int col = cd.getColor();
        int alpha = cd.getAlpha();
        int red = Color.red(col);
        int green = Color.green(col);
        int blue = Color.blue(col);

        innerColor2 = Color.argb(alpha, red - 10, green - 10, blue - 10);

    }

    /**
     * inits all Paints
     * @author Stefan
     */
    public void initPaints() {
        borderPaint.setColor(textColor);
        borderPaint.setStrokeWidth(borderWidth);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setAntiAlias(true);

        upperPaint.setColor(innerColor);

        arrowPaint.setColor(upperPaint.getColor());
        arrowPaint.setStyle(Paint.Style.FILL);

        lowerPaint.setColor(innerColor2);

        font.setColor(Color.BLACK);
        font.setTextSize(getpx(20));

        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(getpx(20));
        //textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        textPaintSub.setColor(Color.BLACK);
        textPaintSub.setTextSize(getpx(14));

        fontSubActivity.setColor(Color.BLACK);
        fontSubActivity.setTextSize(getpx(14));

        fontDur.setColor(textColor);
        fontDur.setTextSize(getpx(11));

        done.setColor(doneColor);
        done.setStrokeWidth(getpx(3));

        open.setColor(openColor);
        open.setStrokeWidth(getpx(3));

        highlightPaint.setColor(highlightColor);
        highlightPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * measures the width and height before the actual view is drawn to determine the place the components claim
     * because the sizes depend on each other
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     * @author Stefan
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //get padding of parent
        ViewParent parent = getParent();
        int leftPadding = 0;
        int rightPadding = 0;

        if (parent instanceof ViewGroup) {
            leftPadding = ((ViewGroup) parent).getPaddingLeft();
            rightPadding = ((ViewGroup) parent).getPaddingRight();
        }

        //get width of screen
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int displayWidth = size.x;

        //set the desiredWidth to the display width - paddings of parent
        int desiredWidth = displayWidth - leftPadding - rightPadding;

        int desiredHeight = getpx(120);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;

        }
        else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);

        }
        else {
            //Be whatever you want
            width = desiredWidth;
        }

        desiredHeight = getDesiredHeight(width);

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        initComponents(width, height);

        setMeasuredDimension(width, height);
    }

    /**
     * creates the text fields and measures the height of the StaticLayouts
     * @param width
     * @return height of the View
     * @author Stefan
     */
    private int getDesiredHeight(int width){
        //width Duration: ... + padding to right border
        int mesDur = (int)fontDur.measureText(getResources().getString(R.string.duration)+": " + durationAsString)+textPadding;
        //width of canvas - textPadding to duration - width duration
        int xRight = width - 3*textPadding - mesDur;

        //initialize activity text
        actRect = new Rect(offsetL + textPadding, textPadding, xRight, 0);
        sl = new StaticLayout(ActivityItem.getActivityString(activity), textPaint, (int)actRect.width(), Layout.Alignment.ALIGN_NORMAL, 1, 1, false);
        heightUpper = sl.getHeight() + textPadding;
        int heightPrev = sl.getHeight();

        xRight = width-2*textPadding-offsetL;

        //measure subactivity if it exists
        if(!ActivityItem.getSubactivity(subactivity).equals("") && subactivity > AppGlobal.getHandler().getNumberOfActivities()) {
            //initialize subactivity text
            actRectSub = new Rect(0, heightPrev + textPadding, xRight, 0);
            slsub = new StaticLayout(ActivityItem.getSubactivity(subactivity), textPaintSub, (int) actRectSub.width(), Layout.Alignment.ALIGN_NORMAL, 1, 1, false);
            heightPrev = slsub.getHeight();

            //height of activity text field + height of subactivity text field + textpadding above and beyond
            heightUpper += slsub.getHeight() + textPadding;
        }

        //measure meal if it exists
        if(meal != null) {
            if(!meal.equals("") && !(meal.equals("null"))) {
                //initialize meal text
                actRectMeal = new Rect(0, heightPrev + textPadding, xRight, 0);
                slMeal = new StaticLayout(getMeal(), textPaintSub, (int) actRectMeal.width(), Layout.Alignment.ALIGN_NORMAL, 1, 1, false);
                heightPrev = slMeal.getHeight();

                //height + meal text field height
                heightUpper += slMeal.getHeight() + textPadding;
            }
        }

        //measure meal image if it exists
        if(mealImage != null) {
            src = new Rect(0,0,mealImage.getWidth(), mealImage.getHeight());
            Log.d(TAG, "width: " + mealImage.getWidth() + " height: " + mealImage.getHeight());
            float ratio = (float)mealImage.getWidth()/(float)mealImage.getHeight();
            Log.d(TAG, "ratio: " + ratio);
            int height = (int)((float)xRight/(float)ratio);

            Log.d(TAG, "xRight: " + xRight);
            Log.d(TAG, "ratio: " + ratio);
            Log.d(TAG, "height: " + height);

            dest = new Rect(0,heightPrev+textPadding, xRight, height+heightPrev+textPadding);
            Log.d(TAG, "Bitmap: " + mealImage.getWidth() + "x" + mealImage.getHeight());
            Log.d(TAG, "Rect: " + dest.width() + "x" + dest.height());

            heightPrev += dest.height() + textPadding;
            heightUpper += dest.height() + textPadding;
        }

//        Log.d(TAG, "Bloodsugar: " + bloodsugarText);

        //measure bloodsugarText if it exists
        if(bloodsugarText != null) {
            if(!bloodsugarText.equals("")) {
                //initialize bloodsugarText text
                actRectBlood = new Rect(0, heightPrev + textPadding, xRight, 0);
                slBlood = new StaticLayout(getBloodsugarText(), textPaintSub, (int) actRectBlood.width(), Layout.Alignment.ALIGN_NORMAL, 1, 1, false);
                heightPrev = slBlood.getHeight();

                //height + bloodsugarText text field height
                heightUpper += slBlood.getHeight() + textPadding;
            }
        }

        if(insulinText != null) {
            if(!insulinText.equals("")) {
                //initialize insulinText text
                actRectInsulin = new Rect(0, heightPrev + textPadding, xRight, 0);
                slInsulin = new StaticLayout(getInsulinText(), textPaintSub, (int) actRectInsulin.width(), Layout.Alignment.ALIGN_NORMAL, 1, 1, false);
                heightPrev = slInsulin.getHeight();

                //height + insulinText text field height
                heightUpper += slInsulin.getHeight() + textPadding;
            }
        }

        //padding bottom
        heightUpper += textPadding;
        return heightUpper + marginTop + heightLower;
    }

    /**
     * initializes the actual geometrical components that will be drawn
     * with the measures determined in onMeasure
     * @param width
     * @param height
     * @author Stefan
     */
    private void initComponents(int width, int height){
        //upper rectangle
        front = new Rect(0 + offsetL, 0, width, heightUpper);
        //lower rectangle
        front2 = new Rect(0 + offsetL, heightUpper, width, heightLower + heightUpper);

        //arrow-triangle
        a = new Point(0 + offsetL1, dotOffset);
        b = new Point(0 + offsetL, dotOffset - getpx(10));
        c = new Point(0 + offsetL, dotOffset + getpx(10));

        //arrow.setFillType(Path.FillType.EVEN_ODD);
        arrow.moveTo(b.x, b.y);
        arrow.lineTo(c.x, c.y);
        arrow.lineTo(a.x, a.y);
        arrow.close();

        //init border points (corner points of the overall rectangle)
        p1 = new Point(0 + offsetL, borderWidth / 2);
        p2 = new Point(width - borderWidth / 2, borderWidth / 2);
        p3 = new Point(width - borderWidth / 2, heightLower + heightUpper);
        p4 = new Point(0 + offsetL, heightLower + heightUpper);

        //border
        border.moveTo(p1.x, p1.y);
        border.lineTo(p2.x, p2.y);
        border.lineTo(p3.x, p3.y);
        border.lineTo(p4.x, p4.y);
        border.lineTo(c.x, c.y);
        border.lineTo(a.x, a.y);
        border.lineTo(b.x, b.y);
        border.close();

        //highlight clicked item
        hl.moveTo(p1.x, p1.y - borderWidth / 2);
        hl.lineTo(p2.x + borderWidth / 2, p2.y - borderWidth / 2);
        hl.lineTo(p3.x + borderWidth / 2, p3.y);
        hl.lineTo(p4.x, p4.y);
        hl.lineTo(c.x, c.y);
        hl.lineTo(a.x, a.y);
        hl.lineTo(b.x, b.y);
        hl.close();
    }

    /**
     * draw the components to canvas
     * @param canvas
     * @author Stefan
     */
    @Override
    public void onDraw(Canvas canvas) {

        //draw upper rectangle
        canvas.drawRect(front, upperPaint);

        //draw lower rectangle
        canvas.drawRect(front2, lowerPaint);

        //draw arrow
        canvas.drawPath(arrow, arrowPaint);

        if (touched) {
            canvas.drawPath(hl, highlightPaint);
        }

        //determine colors for state
        switch (state) {
            case 1:
                upline = open;
                downline = open;
                dot = open;
                break;
            case 2:
                upline = done;
                downline = open;
                dot = done;

                //draw border
                canvas.drawPath(border, borderPaint);
                break;
            case 3:
                upline = done;
                downline = done;
                dot = done;
                break;
        }
        //draw dot. dotOffset: offset from top
        canvas.drawCircle(radius, dotOffset, radius, dot);
        //draw upper line
        canvas.drawLine(radius, 0, radius, dotOffset - radius - lineOffset, upline);
        // draw lower line
        canvas.drawLine(radius, dotOffset + radius + lineOffset, radius, heightLower + heightUpper + marginTop, downline);


     //draw duration text
        canvas.drawText(getResources().getString(R.string.duration) + ": " + durationAsString, getWidth() - (int) fontDur.measureText(getResources().getString(R.string.duration) + ": " + durationAsString) - textPadding, sl.getHeight(), fontDur);
        //draw start text
        canvas.drawText(getResources().getString(R.string.start) + ": " + TimeUtils.getTimeInUserFormat(startDate, context), textPadding + offsetL, (front2.height() / 2) + front.height() + fontDur.getTextSize() / 2, fontDur);
        //draw end text
        canvas.drawText(getResources().getString(R.string.end) + ": " + TimeUtils.getTimeInUserFormat(endDate, context), getWidth() - textPadding - fontDur.measureText(getResources().getString(R.string.end) + ": " + TimeUtils.getTimeInUserFormat(endDate, context)), (front2.height() / 2) + fontDur.getTextSize() / 2 + front.height(), fontDur);

        //draw activity text
        canvas.translate(actRect.left, actRect.top);
        sl.draw(canvas);

        if(!ActivityItem.getSubactivity(subactivity).equals("") && subactivity > AppGlobal.getHandler().getNumberOfActivities()) {
            //draw subactivity text
            canvas.translate(actRectSub.left, actRectSub.top);
            slsub.draw(canvas);
        }

        if(meal != null) {
            if(!meal.equals("") && !(meal.equals("null"))) {
                //draw meal text
                canvas.translate(actRectMeal.left, actRectMeal.top);
                slMeal.draw(canvas);
            }
        }

        if(mealImage != null) {
            canvas.drawBitmap(mealImage, src, dest, null);
        }

        if(bloodsugarText != null) {
            if(!bloodsugarText.equals("")) {
                //draw bloodsugarText text
                canvas.translate(actRectBlood.left, actRectBlood.top);
                slBlood.draw(canvas);
            }
        }

        if(insulinText != null) {
            if(!insulinText.equals("")) {
                //draw insulinText text
                canvas.translate(actRectInsulin.left, actRectInsulin.top);
                slInsulin.draw(canvas);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //scrolls the scrollview to focus the running activity
        int[] loc = new int[2];
        if(DailyRoutineView.getCurrentRunning() != null) {
            DailyRoutineView.getCurrentRunning().getLocationOnScreen(loc);
            DailyRoutineFragment.getScrollView().scrollTo(0, loc[1]);
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * takes start and endtime as String in HH:mm format and returns the duration
     *
     * @return duration
     * @author Stefan
     */
    public String getDuration() {
        int start = startInMinOfDay;
        int end = endInMinOfDay;

        int duration = end - start;
        return getDurationAsString(duration);
    }

    /**
     * takes the duration in minutes as input and outputs it in the 2h 22min format
     *
     * @param duration duration in minutes
     * @return duration
     * @author Stefan
     */
    public String getDurationAsString(int duration) {
        int min = duration % 60;
        int h = duration / 60;

        if (h > 0) {
            return h + "h " + min + "min";
        } else {
            return min + "min";
        }
    }

    /**
     * returns the px value to a dp value
     *
     * @param dp dp value
     * @return px
     * @author Stefan
     */
    public int getpx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    /**
     * returns the dp value for a px value
     *
     * @param px px vlaue
     * @return dp
     * @author Stefan
     */
    public int getdp(int px) {
        return (int) (px / getResources().getDisplayMetrics().density);
    }

    /**
     * returns the position of the dot
     *
     * @return point
     * @author Stefan
     */
    public Point getDot() {
        return new Point(getLeft(), dotOffset);
    }

    /**
     * returns the total height of a activity item
     *
     * @return total height
     * @author Stefan
     */
    public int getTotalHeight() {
        return (int) (heightLower + heightUpper + marginTop);
    }

    /**
     * checks if actual time is inbetween
     * @return if this is the actual activity
     * @author Stefan
     */
    private boolean isRunning() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int time = getMinutesOfDay(date);
        if (startInMinOfDay <= time && time <= endInMinOfDay) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * checks if activity is finished
     *
     * @param date
     * @return isFinished
     * @author Stefan
     */
    public boolean isFinished(Date date) {
        int time = getMinutesOfDay(date);
        if (endInMinOfDay <= time) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * checks if activity is remaining
     *
     * @param date
     * @return isRemaining
     * @author Stefan
     */
    public boolean isRemaining(Date date) {
        int time = getMinutesOfDay(date);
        if (time < startInMinOfDay) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * sets the state of the activity
     * 1: is remaining
     * 2: is running
     * 3: is finished
     *
     * @param isArchieved true if daily routine is archieved
     * @author Stefan
     */
    public void setState(boolean isArchieved) {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        if (isArchieved) {
            state = 3;
        } else {
            if (isRemaining(date)) {
                state = 1;
            } else if (isRunning()) {
                state = 2;
                current = this;
            } else if (isFinished(date)) {
                state = 3;
            }
        }
    }

    /**
     * takes the time in HH:mm and returns it in minutes of the day
     *
     * @param time a time
     * @return minutes of the day
     *
     * @author Stefan
     */
    public int getMinutesOfDay(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);   // assigns calendar to given date
        int hour = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
        int min = calendar.get(Calendar.MINUTE);

        return hour*60+min;
    }

    /**
     * handles the onClick event, sets the ActionBar items according to the selection
     * @param v
     * @return
     * @author Stefan
     */
    @Override
    public boolean onLongClick(View v) {
        //makes the items selectable
        //TODO setActionBarItems does not work because no items selected here (get selected in onTouch)
        if(!selectable) {
            vibrate(this.context, 500);
            selectable = true;
            setActionBarItems();
            invalidate();
        }

        return true;
    }

    /**
     * performs a vibrate
     *
     * @param context context
     * @param millis  milliseconds to vibrate
     * @author Stefan
     */
    private void vibrate(Context context, int millis) {
        Vibrator vibr = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        vibr.vibrate(millis);
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * handles on touch event for interaction with daily routine
     * @param v
     * @param event
     * @return
     * @author Stefan
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //Log.d(TAG, String.valueOf("on touch " + selectable));

        //handles if an item was touched
        //getParent().requestDisallowInterceptTouchEvent(true);

        if (selectable) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (isSelected) {
                    selectedActivities.remove(this);
                    isSelected = false;
                    touched = false;
                } else {
                    selectedActivities.add(this);
                    isSelected = true;
                    touched = true;
                }

                if(selectedActivities.size()<1){
                    selectable = false;
                }
            }

        } else {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                touched = true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                touched = false;
            }
        }

        setActionBarItems();
        invalidate();

        //states if event was handled and no other handler should handle it
        //false because onLongClick() has to handle it too
        return false;
    }

    /**
     * sets the add, edit and remove icons in the action bar
     * depending on the selected activities
     * @author Stefan
     */
    public static void setActionBarItems() {
        if(selectedActivities.size()==1 || (selectedActivities.size()==0 && selectable)) {
            DailyRoutineFragment.setAddItemVisible(false);
            DailyRoutineFragment.setEditIconVisible(true);
            DailyRoutineFragment.setDeleteIconVisible(true);
        }else if(selectedActivities.size()<1){
            DailyRoutineFragment.setAddItemVisible(true);
            DailyRoutineFragment.setEditIconVisible(false);
            DailyRoutineFragment.setDeleteIconVisible(false);
        }else if(selectedActivities.size()>1){
            DailyRoutineFragment.setAddItemVisible(false);
            DailyRoutineFragment.setEditIconVisible(false);
            DailyRoutineFragment.setDeleteIconVisible(true);
        }
    }

    /**
     * returns current running view
     * @return
     */
    public static DailyRoutineView getCurrentRunning(){
        return current;
    }

    /**
     * return if items are selectable
     * @return isSelectable
     * @author Stefan
     */
    public static boolean isSelectable(){
        return selectable;
    }

    /**
     * sets items to isSelectable
     * @param isSelectable isSelectable
     * @author Stefan
     */
    public static void setSelectable(boolean isSelectable){
        selectable = isSelectable;
    }


    /**
     * true if this item is selected
     * @return boolean
     * @author Stefan
     */
    public boolean isSelected(){
        return isSelected;
    }

    /**
     * Sets an item to be selected or not.
     * ActionBar items and the list selectedActivities
     * @param isSelected
     * @author Stefan
     */
    public void setSelected(boolean isSelected) {
        Log.d(TAG,"deselected: " + ActivityItem.getActivityString(activity));

        this.isSelected = isSelected;
        if(!isSelected){
            if(selectedActivities.contains(this)) {
                touched = false;
                selectedActivities.remove(this);

                if(selectedActivities.size() == 0){
                    selectable = false;
                }
            }
        }else{
            if(!selectedActivities.contains(this)) {
                selectedActivities.add(this);
                touched = true;
                selectable = true;
            }
        }
        setActionBarItems();
        invalidate();
    }

    /**
     * deselects all items
     * @author Stefan
     */
    public static void deselectAll(){

        //run through the list from last to first item,
        //because last item will be removed and would shift the index if done in the other direction
        for(int i=selectedActivities.size()-1; i>=0; i--){
            //Log.d(TAG, selectedActivities.get(i).getActivity());
            selectedActivities.get(i).setSelected(false);
        }
    }
    /**
     * getter for the isSelected activity list
     * @return list with isSelected activities
     * @author Stefan
     */
    public static ArrayList<DailyRoutineView> getSelectedActivities(){
        return selectedActivities;
    }

    public void setActivity(int activity){
        this.activity = activity;
    }

    public static void setSelectedActivities(ArrayList<DailyRoutineView> drv) {
        selectedActivities = drv;
    }

    public static void clearSelectedActivities() {
        selectedActivities.clear();
    }

    /**
     * returns the bloodsugarlevel as string ("Time: level unit")
     * @return
     */
    public String getBloodsugarText() {
        return this.bloodsugarText;
    }

    /**
     * returns the insulinText as string
     * @return
     */
    public String getInsulinText() {
        return this.insulinText;
    }

    /**
     * set bloodsugarText string
     * @param bloodsugarText
     */
    public void setBloodsugarText(String bloodsugarText){
        this.bloodsugarText = bloodsugarText;
    }

    public void setInsulinText(String insulinText){
        this.insulinText = insulinText;
    }


    public void setSubactivity(int id){
        this.subactivity = id;
    }

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal){
        this.meal = meal;
    }

    /**
     * returns height of StaticLayout component
     * @param sl
     * @return 0 if sl ==null else height of sl
     * @author Stefan
     */
    public int getHeightComp(StaticLayout sl){
        if(sl == null){
            return 0;
        }else {
            return sl.getHeight();
        }
    }

    public Bitmap getImage() {
        return mealImage;
    }

    public String getImagePath(){
        return imagePath;
    }

    public ActivityItem getActivityItem(){
        return activityItem;
    }
}