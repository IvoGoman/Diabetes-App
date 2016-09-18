package uni.mannheim.teamproject.diabetesplaner.UI.ActivityMeasurementFrag;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import uni.mannheim.teamproject.diabetesplaner.Domain.ActivityInputHandler;
import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.UI.CustomListView;
import uni.mannheim.teamproject.diabetesplaner.R;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;


/**
 * created by Naira
 */
public class ActivityFragment extends Fragment  {

    private static View inflaterView;
    private static ListAdapter adapter;
    public static AbsListView lv;
    public static ArrayList<String> FileList = new ArrayList<String>();
    ActivityInputHandler ActivityInputHndlr = new ActivityInputHandler ();
    final DataBaseHandler DBHandler = AppGlobal.getHandler();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private AppCompatActivity aca;

    private static final int MY_PERMISSIONS_READ_Storage = 0;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ActivityFragment newInstance(String param1, String param2) {
        ActivityFragment fragment = new ActivityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        aca = (AppCompatActivity) getActivity();
        aca.getSupportActionBar().setTitle("Activity");


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        inflaterView = inflater.inflate(R.layout.fragment_activity, container, false);
        final FloatingActionButton floatingButton = (FloatingActionButton) inflaterView.findViewById(R.id.add_button);
        lv = (ListView) inflaterView.findViewById(R.id.listView);
        adapter = new CustomListView(getActivity(), FileList);

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


        floatingButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

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
                }
                else {
                    Log.d("filechooser permission", "has permission");
                    new FileChooser(getActivity()).setFileListener(new FileChooser.FileSelectedListener() {
                    @Override
                    public void fileSelected(final File file) {
                        String fileString = (String) file.toString();
                        String[] fileStringSplit = fileString.split("/");
                        String requiredSplitPart = fileStringSplit[fileStringSplit.length-1];
                        if ((ActivityInputHndlr.isFileFormatValid(fileString))== true){
                            FileList.add(requiredSplitPart);
                            Toast.makeText(getActivity(), "Chosen File:" + requiredSplitPart , Toast.LENGTH_LONG).show();
                            ActivityInputHndlr.loadIntoDatabase(fileString);
                            ((AdapterView<ListAdapter>) lv).setAdapter(adapter);
                        }else{
                            Toast.makeText(getActivity(), "File is not in the correct format", Toast.LENGTH_LONG).show();
                        }
                    }
                    }).showDialog();
                }

            }
            });


        return inflaterView;
    }

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


                    new FileChooser(getActivity()).setFileListener(new FileChooser.FileSelectedListener() {
                        @Override
                        public void fileSelected(final File file) {
                            String fileString = (String) file.toString();
                            String[] fileStringSplit = fileString.split("/");
                            String requiredSplitPart = fileStringSplit[fileStringSplit.length-1];
                            if ((ActivityInputHndlr.isFileFormatValid(fileString))== true){
                                FileList.add(requiredSplitPart);
                                Toast.makeText(getActivity(), "Chosen File:" + requiredSplitPart , Toast.LENGTH_LONG).show();
                                ActivityInputHndlr.loadIntoDatabase(fileString);
                                ((AdapterView<ListAdapter>) lv).setAdapter(adapter);
                            }else{
                                Toast.makeText(getActivity(), "File is not in the correct format", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).showDialog();


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




}


