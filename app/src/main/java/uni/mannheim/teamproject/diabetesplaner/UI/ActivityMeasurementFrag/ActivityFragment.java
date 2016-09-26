package uni.mannheim.teamproject.diabetesplaner.UI.ActivityMeasurementFrag;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityInputHandler;
import uni.mannheim.teamproject.diabetesplaner.R;
import uni.mannheim.teamproject.diabetesplaner.UI.CustomListView;
import uni.mannheim.teamproject.diabetesplaner.UI.FileLoadingService;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;


/**
 * created by Naira
 */
public class ActivityFragment extends Fragment {

    private static final int MY_PERMISSIONS_READ_Storage = 0;
    public static AbsListView lv;
    public static ArrayList<String> FileList = new ArrayList<String>();
    private static View inflaterView;
    public static ListAdapter adapter;
    final DataBaseHandler DBHandler = AppGlobal.getHandler();
    private String mParam1;
    private String mParam2;
    private AppCompatActivity aca;
    private IntentFilter mStatusIntentFilter;

    // Defines a custom Intent action
    public static final String BROADCAST_ACTION =
            "com.example.android.threadsample.BROADCAST";
    // Defines the key for the status "extra" in an Intent
    public static final String EXTENDED_DATA_STATUS =
            "com.example.android.threadsample.STATUS";
    public static final String FILEPATH = "filepath";
    private RelativeLayout progressBar;

    public ActivityFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment ActivityFragment
     * @author Naira
     */
    public static ActivityFragment newInstance() {
        ActivityFragment fragment = new ActivityFragment();
        return fragment;
    }

    /**
     * Called when it is first created.
     * @param savedInstanceState
     * @author Naira
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aca = (AppCompatActivity) getActivity();
        aca.getSupportActionBar().setTitle("Activity");

        // The filter's action is BROADCAST_ACTION
        mStatusIntentFilter = new IntentFilter(
                ActivityFragment.BROADCAST_ACTION);
    }

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return inflaterView
     * @author Naira
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        inflaterView = inflater.inflate(R.layout.fragment_activity, container, false);

        //initializing variables to their corresponding layout elements
        final FloatingActionButton floatingButton = (FloatingActionButton) inflaterView.findViewById(R.id.add_button);
        lv = (ListView) inflaterView.findViewById(R.id.listView);

        progressBar = (RelativeLayout) inflaterView.findViewById(R.id.file_chooser_progress);
        progressBar.setVisibility(View.GONE);

        adapter = new CustomListView(getActivity(), FileList);

        //adjusts floating button location
        ViewTreeObserver viewTreeObserver = floatingButton.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        floatingButton.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        floatingButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    // not sure the above is equivalent, but that's beside the point for this example...
                    CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) floatingButton.getLayoutParams();
                    params.setMargins(0, 0, 16, 16); // (int left, int top, int right, int bottom)
                    floatingButton.setLayoutParams(params);
                }
            });
        }

        floatingButton.setOnClickListener(new View.OnClickListener() {
            /**
             * @param v
             */
            @Override
            public void onClick(View v) {
                //check if permission is given by the user (for Android version 6 or more)
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // No explanation needed, we can request the permission.
                    Log.d("filechooser permission", "No permission");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Log.d("filechooser permission", "Request permission");
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_READ_Storage);
                    }
                } else {
                    Log.d("filechooser permission", "has permission");
                    new FileChooser(getActivity()).setFileListener(new FileChooser.FileSelectedListener() {
                        @Override
                        public void fileSelected(final File file) {
                            String fileString = (String) file.toString();
                            String[] fileStringSplit = fileString.split("/");
                            String requiredSplitPart = fileStringSplit[fileStringSplit.length - 1];
                            if ((ActivityInputHandler.isFileFormatValid(fileString)) == true) {
                                ActivityFragment.FileList.add(requiredSplitPart);
                                Toast.makeText(getActivity(), "Chosen File:" + requiredSplitPart, Toast.LENGTH_LONG).show();
                                try {

                                    //-----START-SERVICE-CALL---------------------------------------------------
                                    progressBar.setVisibility(View.VISIBLE);
                                    lv.setVisibility(View.GONE);
                                    Intent mServiceIntent = new Intent(getActivity(), FileLoadingService.class);
                                    mServiceIntent.putExtra(FILEPATH, fileString);
                                    // Starts the IntentService
                                    getActivity().startService(mServiceIntent);

                                    // Instantiates a new ResponseReceiver
                                    ResponseReceiver mDownloadStateReceiver =
                                            new ResponseReceiver();
                                    // Registers the ResponseReceiver and its intent filters
                                    LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                                            mDownloadStateReceiver, mStatusIntentFilter);

                                    //------------END-SERVICE-CALL----------------------------------------------
                                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("PREDICTION_SERVICE_FILE", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("LAST_PREDICTION", "0");
                                    editor.commit();
                                } catch(Exception e){
                                    Toast.makeText(getActivity(), R.string.error_loading_data, Toast.LENGTH_LONG).show();
                                }
                                ((AdapterView<ListAdapter>) ActivityFragment.lv).setAdapter(ActivityFragment.adapter);
                            } else {
                                Toast.makeText(getActivity(), "File is not in the correct format", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).showDialog();
                }
            }
        });
        return inflaterView;
    }

    /**
     * permission pop up for read access from phone files
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @author Naira
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d("filechooser permission", "onRequestPermissionResult");
        switch (requestCode) {
            case MY_PERMISSIONS_READ_Storage: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("filechooser permission", "permission granted");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

//                    new FileChooser(getActivity()).setFileListener(new FileChooser.FileSelectedListener() {
//                        @Override
//                        public void fileSelected(final File file) {
//                            String fileString = (String) file.toString();
//                            String[] fileStringSplit = fileString.split("/");
//                            String requiredSplitPart = fileStringSplit[fileStringSplit.length - 1];
//                            if ((ActivityInputHndlr.isFileFormatValid(fileString)) == true) {
//                                FileList.add(requiredSplitPart);
//                                Toast.makeText(getActivity(), "Chosen File:" + requiredSplitPart, Toast.LENGTH_LONG).show();
//                                ActivityInputHndlr.loadIntoDatabase(fileString);
//                                ((AdapterView<ListAdapter>) lv).setAdapter(adapter);
//                            } else {
//                                Toast.makeText(getActivity(), "File is not in the correct format", Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    }).showDialog();
                } else {
                    Log.d("filechooser permission", "permission denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private class ResponseReceiver extends BroadcastReceiver {
        // Prevents instantiation
        private ResponseReceiver() {
        }

        // Called when the BroadcastReceiver gets an Intent it's registered to receive
//    @
        public void onReceive(Context context, Intent intent) {
            //do something
            progressBar.setVisibility(View.GONE);
            lv.setVisibility(View.VISIBLE);
        }
    }
}


