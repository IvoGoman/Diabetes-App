package uni.mannheim.teamproject.diabetesplaner.ActivityMeasurementFrag;


import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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

import uni.mannheim.teamproject.diabetesplaner.Backend.ActivityInputHandler;
import uni.mannheim.teamproject.diabetesplaner.Backend.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.CustomListView;
import uni.mannheim.teamproject.diabetesplaner.R;


/**
 * created by Naira
 */
public class ActivityFragment extends Fragment  {

    private static View inflaterView;
    private static ListAdapter adapter;
    public static AbsListView lv;
    public static ArrayList<String> FileList = new ArrayList<String>();
    ActivityInputHandler ActivityInputHndlr = new ActivityInputHandler ();
    final DataBaseHandler DBHandler = new DataBaseHandler(getContext());


    public ActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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
                new FileChooser(getActivity()).setFileListener(new FileChooser.FileSelectedListener() {
                    @Override
                    public void fileSelected(final File file) {
                        String fileString = (String) file.toString();
                        String[] fileStringSplit = fileString.split("/");
                        String requiredSplitPart = fileStringSplit[fileStringSplit.length-1];
                        if ((ActivityInputHndlr.isFileFormatValid(fileString))== true){
                            FileList.add(requiredSplitPart);
                            Toast.makeText(getActivity(), "Chosen File:" + requiredSplitPart , Toast.LENGTH_LONG).show();
                            ActivityInputHndlr.loadIntoDatabase(fileString,DBHandler);
                            ((AdapterView<ListAdapter>) lv).setAdapter(adapter);
                        }else{
                            Toast.makeText(getActivity(), "File is not in the correct format", Toast.LENGTH_LONG).show();
                        }
                    }
                    }).showDialog();

            }
            });

        return inflaterView;
    }





}


