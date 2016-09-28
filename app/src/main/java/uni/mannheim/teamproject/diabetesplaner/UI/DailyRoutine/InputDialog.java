package uni.mannheim.teamproject.diabetesplaner.UI.DailyRoutine;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityItem;
import uni.mannheim.teamproject.diabetesplaner.Domain.DayHandler;
import uni.mannheim.teamproject.diabetesplaner.R;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;
import uni.mannheim.teamproject.diabetesplaner.Utility.Util;

/**
 * Created by Stefan on 04.02.2016.
 */
public class InputDialog extends DialogFragment{
    private static final String TAG = InputDialog.class.getSimpleName();
    private int activity = 0;
    private int subactivity = 0;
    private String meal;
    private Date date;
    private Date startDate;
    private Date endDate;
    private Integer intensity;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;

    private TimePickerFragment timePickerFragmentStart;
    private TimePickerFragment timePickerFragmentEnd;
    private Button startTimeButton;
    private Button endTimeButton;
    private String selectedItem;
    private static String imagePath = null;

    private SeekBar intensityBar;
    private TableRow intensityText;
    private TextView intensityValue;
    private ActivityItem activityItem;
    private DayHandler drHandler;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static ImageView mealInputImage;
    private EditText mealInputText;
    private static Bitmap image;
    private TextView subactivityText;
    private Spinner subSpinner;

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
     * @author Stefan
     */
    public View getLayout(){
        LayoutInflater inflater = (LayoutInflater) getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.input_dialog, null);

        subactivityText = (TextView)v.findViewById(R.id.tv_subactivity);
        subSpinner = (Spinner)v.findViewById(R.id.add_dialog_spinner_sub);

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
                        switch(progress){
                            case 0: intensity = ActivityItem.INTENSITY_LOW;
                                break;
                            case 1: intensity = ActivityItem.INTENSITY_MEDIUM;
                                break;
                            case 2: intensity = ActivityItem.INTENSITY_HIGH;
                        }
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

        if(imagePath != null && !imagePath.equals("null")){
            displayImageFromPath(imagePath);
        }else{
            mealInputImage.setVisibility(View.GONE);
        }
        if(meal != null && !meal.equals("null")){
            mealInputText.setText(meal);
        }

        mealInput.setVisibility(View.GONE);
        mealInputText.setVisibility(View.GONE);
        mealInputCam.setVisibility(View.GONE);
        mealInputCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

//                    // Should we show an explanation?
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                            Manifest.permission.CAMERA)) {
//
//                        // Show an explanation to the user *asynchronously* -- don't block
//                        // this thread waiting for the user's response! After the user
//                        // sees the explanation, try again to request the permission.
//
//                    } else {

                        // No explanation needed, we can request the permission.
                    Log.d(TAG, "No permission");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Log.d(TAG, "Request permission");
                        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_CAMERA);
                    }

//                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                        // app-defined int constant. The callback method gets the
//                        // result of the request.
//                    }
                }else{
                    Log.d(TAG, "has permission");
                    //take the picture
                    dispatchTakePictureIntent();
                }
            }
        });

        //spinner menu with the activities
        Spinner spinner = (Spinner) v.findViewById(R.id.add_dialog_spinner);

        final DataBaseHandler dbHandler = AppGlobal.getHandler();
        ArrayList<String> actionsList = dbHandler.getAllActionsAsList();

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Util.toArray(actionsList));
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setSelection(activity);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapter, View view, int position, long id) {
                activity = ActivityItem.getActivityId(adapter.getItemAtPosition(position).toString());
                ArrayList<String> subactivities = dbHandler.GetSubActivities(activity);
                if(subactivities.size() > 1){
                    final ArrayAdapter<String> subAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Util.toArray(subactivities));
                    // Specify the layout to use when the list of choices appears
                    subAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    subSpinner.setAdapter(subAdapter);
                    //find subactivity index
                    String tmpSubact = dbHandler.getSubactivity(subactivity);
                    int subactivityIndex = 0;
                    for(int i=0; i<subactivities.size(); i++){
                        if(subactivities.get(i).equals(tmpSubact)){
                            subactivityIndex = i;
                        }
                    }
                    subSpinner.setSelection(subactivityIndex);
                    subSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> subAdapter, View view, int subPosition, long id) {
                            int tmp = subPosition;
                            subactivity = dbHandler.getSubactivityID(subAdapter.getItemAtPosition(subPosition).toString(), activity);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    subactivityText.setVisibility(View.VISIBLE);
                    subSpinner.setVisibility(View.VISIBLE);
                }else{
                    subactivity = 0;
                    subactivityText.setVisibility(View.GONE);
                    subSpinner.setVisibility(View.GONE);
                }

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
                String germanName = dbHandler.getGermanActivityName(activity);
                if(germanName.equals("Arbeiten") || germanName.equals("Sport")){
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
        startTimeButton.setText(TimeUtils.getTimeInUserFormat(startDate, getActivity()));
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
        endTimeButton.setText(TimeUtils.getTimeInUserFormat(endDate, getActivity()));
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
     * @author Stefan
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
     * sets the initial date
     * @param date
     * @author Stefan 30.03.2016
     */
    public void setDate(Date date){
        this.startDate = date;
        this.endDate = date;
        this.date = date;
    }

    /**
     * set the activity of the input dialog
     * @param activity activity id
     * @author Stefan
     */
    public void setActivity(int activity){
        this.activity = activity-1;
    }

    /**
     * set the subactivity of the input dialog
     * @param subactivity
     * @author Stefan 29.08.2016
     */
    public void setSubactivity(int subactivity){
        this.subactivity = subactivity;
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

    /**
     * sets the properties of the input dialog
     * @param activityItem
     * @author Stefan
     */
    public void setActivityItem(ActivityItem activityItem) {
        this.activityItem = activityItem;
        setActivity(activityItem.getActivityId());
        setSubactivity(activityItem.getSubactivityId());
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

    /**
     * returns intensity, if intensity == null it returns -1
     * @return
     * @author Stefan
     */
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
    public Integer getSelectedActivity() {
        return activity;
    }

    /**
     * returns id of selected subactivity, returns 0 if there is no subactivity
     * @return
     * @author Stefan 29.08.2016
     */
    public Integer getSelectedSubActivity(){
        return subactivity;
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
     * @author Stefan
     */
    public static void displayImageFromPath(String imagePath){
        InputDialog.imagePath = imagePath;
        mealInputImage.setVisibility(View.VISIBLE);
        Bitmap bitmap = Util.getCompressedPic(imagePath, mealInputImage.getWidth());
        mealInputImage.setImageBitmap(bitmap);
        image = bitmap;
    }

    public void setImagePath(String filepath){
        imagePath = Util.getValidString(filepath);
    }

    public static String getImagePath(){
        return Util.getValidString(imagePath);
    }

    public Bitmap getImage(){
        return image;
    }

    /**
     * sets the meal and replaces linebreaks with a space
     * @return
     * @author Stefan
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
     * @author Stefan
     */
    public void setStartDate(Date startDate) {
        if(startTimeButton != null) {
            startTimeButton.setText(TimeUtils.getTimeInUserFormat(startDate, getActivity()));
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
     * @author Stefan
     */
    public void setEndDate(Date endDate) {
        if(endTimeButton != null){
            endTimeButton.setText(TimeUtils.getTimeInUserFormat(endDate, getActivity()));
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
     * @author Stefan
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
                imagePath = Util.getValidString(photoFile.getPath());

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                getActivity().startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        }
    }

    /**
     * makes a picture visible in the gallery
     * @param mCurrentPhotoPath path of the picture taken
     * @author Stefan
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
     * @author Stefan
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
     * @author Stefan
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

    /**
     * callback method for asking for camera permission
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @author Stefan 08.07.2016
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "onRequestPermissionResult");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "permission granted");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    //take the picture
                    dispatchTakePictureIntent();

                } else {
                    Log.d(TAG, "permission denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}

