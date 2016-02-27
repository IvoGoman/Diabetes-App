package uni.mannheim.teamproject.diabetesplaner.ActivityMeasurementFrag;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
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
        ImageButton dirChooserButton = (ImageButton) inflaterView.findViewById(R.id.add_button);
        lv = (ListView) inflaterView.findViewById(R.id.listView);
        adapter = new CustomListView(getActivity(), FileList);


        dirChooserButton.setOnClickListener(new View.OnClickListener()
        {
            private String m_chosenDir = "";
            private boolean m_newFolderEnabled = true;

            @Override
            public void onClick(View v)
            {
                new FileChooser(getActivity()).setFileListener(new FileChooser.FileSelectedListener() {
                    @Override
                    public void fileSelected(final File file) {
                        String s = (String) file.toString();
                        String[] parts = s.split("/");
                        String part1 = parts[parts.length-1];
                        if ((ActivityInputHndlr.isFileFormatValid(s))== true){
                            FileList.add(part1);
                            ActivityInputHndlr.loadIntoDatabase(part1,DBHandler);
                            ((AdapterView<ListAdapter>) lv).setAdapter(adapter);
                        }
                    }
                    }).showDialog();

            }
            });

        return inflaterView;
    }





}


