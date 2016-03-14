package uni.mannheim.teamproject.diabetesplaner.DailyRoutine;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import uni.mannheim.teamproject.diabetesplaner.Backend.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Backend.DayHandler;
import uni.mannheim.teamproject.diabetesplaner.DataMining.Util;
import uni.mannheim.teamproject.diabetesplaner.R;

/**
 * Created by Stefan on 04.02.2016.
 */
public class InputDialog extends DialogFragment {
    private static final String TAG = InputDialog.class.getSimpleName();
    private String starttime;
    private String endtime;
    private int activity = 0;
    private String meal;
    private String date;
    private Date startDate;
    private Date endDate;

    private TimePickerFragment timePickerFragmentStart;
    private TimePickerFragment timePickerFragmentEnd;
    private Button startTimeButton;
    private Button endTimeButton;
    private String selectedItem;
    private static String imagePath;

    private ActivityItem activityItem;
    private DayHandler drHandler;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static ImageView mealInputImage;
    private EditText mealInputText;
    private static Bitmap image;

    public static void setImage(Bitmap image) {
        InputDialog.image = image;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //init times
        Date date = Calendar.getInstance(Locale.getDefault()).getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        if(starttime == null) {
            starttime = dateFormat.format(date);
        }
        if(endtime == null) {
            endtime = dateFormat.format(date);
        }
        super.onCreate(savedInstanceState);
    }

    /**
     * creates the layout and returns the view object that contains it
     * @return
     */
    public View getLayout(){
        LayoutInflater inflater = (LayoutInflater) getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.input_dialog, null);

        final TextView mealInput = (TextView) v.findViewById(R.id.meal_input_text);
        mealInputText = (EditText) v.findViewById(R.id.meal_input);
        final ImageButton mealInputCam = (ImageButton) v.findViewById(R.id.meal_input_cam);
        mealInputImage = (ImageView) v.findViewById(R.id.meal_image);

        if(imagePath != null){
            displayImageFromPath(imagePath);
        }else{
            mealInputImage.setVisibility(View.GONE);
        }
        if(meal != null){
            mealInputText.setText(meal);
        }

        mealInput.setVisibility(View.GONE);
        mealInputText.setVisibility(View.GONE);
        mealInputCam.setVisibility(View.GONE);
        mealInputCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //take the picture
                dispatchTakePictureIntent();

            }
        });

        //spinner menu with the activities
        Spinner spinner = (Spinner) v.findViewById(R.id.add_dialog_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.activity_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setSelection(activity);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapter, View view, int position, long id) {
                activity = ActivityItem.getActivityId(adapter.getItemAtPosition(position).toString());
                if(activity == 2){
                    mealInput.setVisibility(View.VISIBLE);
                    mealInputText.setVisibility(View.VISIBLE);
                    mealInputCam.setVisibility(View.VISIBLE);
                    mealInputImage.setVisibility(View.VISIBLE);

                }else{
                    mealInput.setVisibility(View.GONE);
                    mealInputText.setVisibility(View.GONE);
                    mealInputText.setText("");
                    mealInputCam.setVisibility(View.GONE);
                    mealInputImage.setVisibility(View.GONE);
                    imagePath = null;
                    image = null;
                    meal = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //button for starttime TimePicker
        startTimeButton = (Button) v.findViewById(R.id.start_button);
        startTimeButton.setText(starttime);
        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerFragmentStart = new TimePickerFragment();
                timePickerFragmentStart.setInputDialog(InputDialog.this);
                timePickerFragmentStart.setStart(true);
                timePickerFragmentStart.setTime(starttime);
                timePickerFragmentStart.show(getFragmentManager(), "timePicker");
            }
        });

        //button for endtime TimePicker
        endTimeButton = (Button) v.findViewById(R.id.end_button);
        endTimeButton.setText(endtime);
        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerFragmentEnd = new TimePickerFragment();
                timePickerFragmentEnd.setInputDialog(InputDialog.this);
                timePickerFragmentEnd.setStart(false);
                timePickerFragmentEnd.setTime(endtime);
                timePickerFragmentEnd.show(getFragmentManager(), "timePicker");

            }
        });

        return v;
    }

    /**
     * checks if time is valid
     * @return
     */
    public boolean isTimeValid() {
        Date starttime = null;
        Date endtime = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            starttime = dateFormat.parse(getStarttime());
            endtime = dateFormat.parse(getEndtime());
            return starttime.before(endtime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * returns the starttime
     * @return
     */
    public String getStarttime(){
        return starttime;
    }

    /**
     * returns the endtime
     * @return
     */
    public String getEndtime(){
        return endtime;
    }

    /**
     * sets the endtime that should be displayed in the endtime button
     * @param endtime
     */
    public void setEndtime(String endtime){
        if(endTimeButton != null){
            endTimeButton.setText(endtime);
        }
        if(starttime != null) {
            if (Util.getTime(starttime).after(Util.getTime(endtime))){
                setStarttime(endtime);
            }
        }
        this.endtime = endtime;
    }

    /**
     * sets the starttime that should be displayed in the starttime button
     * @param starttime
     */
    public void setStarttime(String starttime){
        if(startTimeButton != null) {
            startTimeButton.setText(starttime);
        }
        if(endtime != null) {
            if (Util.getTime(starttime).after(Util.getTime(endtime))){
                setEndtime(starttime);
            }
        }
        this.starttime = starttime;
    }

    /**
     * Ivo Gosemann
     * sets the date of the activity
     *
     * @param date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Ivo Gosemann
     * gets the date of the activity
     *
     * @return date
     */
    public String getDate() {
        return date;
    }


    /**
     * set the activity of the input dialog
     * @param activity activity id
     */
    public void setActivity(int activity){
        this.activity = activity;
    }

    public int getChosenActivity(){
        return activity;
    }

    /**
     * returns the TimePickerFragment
     * @return
     */
    public TimePickerFragment getTimePickerFragmentStart(){
        return timePickerFragmentStart;
    }

    /**
     * returns the TimePickerFragment
     * @return
     */
    public TimePickerFragment getTimePickerFragmentEnd(){
        return timePickerFragmentStart;
    }

    public Button getEndtimeButton(){
        return endTimeButton;
    }

    public Button getStarttimeButton() {
        return startTimeButton;
    }

    public void setActivityItem(ActivityItem activityItem) {
        this.activityItem = activityItem;
        setActivity(activityItem.getActivityId() - 1);
        setStarttime(activityItem.getStarttimeAsString());
        setEndtime(activityItem.getEndtimeAsString());
        setImage(activityItem.getMealImage());
        setImagePath(activityItem.getImagePath());
        setMeal(activityItem.getMeal());
    }

    /**
     * returns id of selected activity
     * @return
     */
    public Integer getSelectedItem() {
        return activity;
    }

    public void setDayHandler(DayHandler drHandler){
        this.drHandler = drHandler;
    }

    public DayHandler getDrHandler(){
        return drHandler;
    }

    /**
     * displays the image form a path, sets the ImageView of the meal visible and the imagesource
     * @param imagePath path of image
     */
    public static void displayImageFromPath(String imagePath){
        InputDialog.imagePath = imagePath;
        mealInputImage.setVisibility(View.VISIBLE);
        Bitmap bitmap = Util.getCompressedPic(imagePath, mealInputImage.getWidth());
        mealInputImage.setImageBitmap(bitmap);
        image = bitmap;
    }

    public void setImagePath(String filepath){
        imagePath = filepath;
    }

    public static String getImagePath(){
        return imagePath;
    }

    public Bitmap getImage(){
        return image;
    }

    /**
     * sets the meal and replaces linebreaks with a space
     * @return
     */
    public String getMeal(){
        String text = String.valueOf(mealInputText.getText());
        text = text.replaceAll("\n+", " ").replaceAll("\r+", " ");
        return text;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public Date getStartDate() {
        startDate = Util.setTime(date, starttime);
        return startDate;
    }

    public void setStartDate(Date startDate) {


        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * creates a file and takes a photo
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = Util.createImageFile();
            // Continue only if the File was successfully created
            if (photoFile != null) {
                //make picture visible in gallery
                galleryAddPic(photoFile.getPath());
                imagePath = photoFile.getPath();

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                getActivity().startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        }
    }

    /**
     * makes a picture visible in the gallery
     * @param mCurrentPhotoPath path of the picture taken
     */
    private void galleryAddPic(String mCurrentPhotoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }


}

