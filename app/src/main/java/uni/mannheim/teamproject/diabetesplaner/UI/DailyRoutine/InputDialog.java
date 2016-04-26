package uni.mannheim.teamproject.diabetesplaner.UI.DailyRoutine;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Domain.DayHandler;
import uni.mannheim.teamproject.diabetesplaner.R;
import uni.mannheim.teamproject.diabetesplaner.Utility.Util;

/**
 * Created by Stefan on 04.02.2016.
 */
public class InputDialog extends DialogFragment{
    private static final String TAG = InputDialog.class.getSimpleName();
    private int activity = 0;
    private String meal;
    private Date date;
    private Date startDate;
    private Date endDate;
    private Integer intensity;

    private TimePickerFragment timePickerFragmentStart;
    private TimePickerFragment timePickerFragmentEnd;
    private Button startTimeButton;
    private Button endTimeButton;
    private String selectedItem;
    private static String imagePath;

    private SeekBar intensityBar;
    private TableRow intensityText;
    private TextView intensityValue;
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

        if(startDate == null){
            startDate = date;
        }
        if(endDate == null){
            endDate = date;
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

        //get the layout of the intensity bar, the value TextView and the intensity TableRow
        intensityText = (TableRow)v.findViewById(R.id.intensity_text);
        intensityBar = (SeekBar)v.findViewById(R.id.intensity_bar);
        intensityValue = (TextView)v.findViewById(R.id.intensity_value);

        intensityBar.setProgress(0);
        intensityBar.setMax(2);
        if(intensity != null){
            setIntensityText(intensity);
            intensityBar.setProgress(intensity);
        }

        intensityBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (progress >= 0 && progress <= intensityBar.getMax()) {
                        setIntensityText(progress);
                        seekBar.setSecondaryProgress(progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

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

                //if activity desk work or sport set the intensity bar to visible, else hide
                if(activity == 12 || activity == 13){
                    setIntensityVisible(true);
                }else{
                    setIntensityVisible(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //button for starttime TimePicker
        startTimeButton = (Button) v.findViewById(R.id.start_button);
        startTimeButton.setText(Util.getTimeInUserFormat(startDate, getActivity()));
        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerFragmentStart = new TimePickerFragment();
                timePickerFragmentStart.setInputDialog(InputDialog.this);
                timePickerFragmentStart.setStart(true);
                timePickerFragmentStart.setTime(startDate);
                timePickerFragmentStart.show(getFragmentManager(), "timePickerFragment");
            }
        });

        //button for endtime TimePicker
        endTimeButton = (Button) v.findViewById(R.id.end_button);
        endTimeButton.setText(Util.getTimeInUserFormat(endDate, getActivity()));
        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerFragmentEnd = new TimePickerFragment();
                timePickerFragmentEnd.setInputDialog(InputDialog.this);
                timePickerFragmentEnd.setStart(false);
                timePickerFragmentEnd.setTime(endDate);
                timePickerFragmentEnd.show(getFragmentManager(), "timePickerFragment");

            }
        });

        return v;
    }

    /**
     * checks if time is valid
     * @return
     */
    public boolean isTimeValid() {
        return startDate.before(endDate);
    }


//    /**
//     * Ivo Gosemann
//     * sets the date of the activity
//     *
//     * @param date
//     */
//    public void setDate(String date) {
//        this.date = date;
//    }

    /**
     * @author Stefan 30.03.2016
     * sets the initial date
     * @param date
     */
    public void setDate(Date date){
        this.startDate = date;
        this.endDate = date;
        this.date = date;
    }

//    /**
//     * Ivo Gosemann
//     * gets the date of the activity
//     *
//     * @return date
//     */
//    public String getDate() {
//        return date;
//    }


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
        setStartDate(activityItem.getStarttime());
        setEndDate(activityItem.getEndtime());

        setImage(activityItem.getMealImage());
        setImagePath(activityItem.getImagePath());
        setMeal(activityItem.getMeal());
        setIntensity(activityItem.getIntensity());
    }

    public void setIntensity(Integer intensity){
        this.intensity = intensity;
    }

    public int getIntensity(){
        if (this.intensity == null )
        {
            return -1;
        }
        else {
            return this.intensity;
        }
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
        return startDate;
    }

    /**
     * sets startDate and the corresponding start time button
     * @param startDate
     */
    public void setStartDate(Date startDate) {
        if(startTimeButton != null) {
            startTimeButton.setText(Util.getTimeInUserFormat(startDate, getActivity()));
        }
        if(endDate != null) {
            if (startDate.after(endDate)){
                setEndDate(startDate);
            }
        }
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    /**
     * sets endDate and the corresponding end time button
     * @param endDate
     */
    public void setEndDate(Date endDate) {
        if(endTimeButton != null){
            endTimeButton.setText(Util.getTimeInUserFormat(endDate, getActivity()));
        }
        if(startDate != null) {
            if (startDate.after(endDate)){
                setStartDate(endDate);
            }
        }
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

    /**
     * sets the intensity SeekBar and the intensity TextView to isVisible
     * @param isVisible true: visible, false: invisible
     */
    public void setIntensityVisible(boolean isVisible){
        if(isVisible){
            intensityBar.setVisibility(View.VISIBLE);
            intensityText.setVisibility(View.VISIBLE);
        }else{
            intensityBar.setVisibility(View.GONE);
            intensityText.setVisibility(View.GONE);
        }
    }

    /**
     * sets the intensity text with respect to the seek bar value
     * @param value
     */
    public void setIntensityText(int value){
        switch (value){
            case 0:
                intensityValue.setText(getResources().getString(R.string.low));
                break;
            case 1:
                intensityValue.setText(getResources().getString(R.string.medium));
                break;
            case 2:
                intensityValue.setText(getResources().getString(R.string.high));
                break;
        }
    }
}

