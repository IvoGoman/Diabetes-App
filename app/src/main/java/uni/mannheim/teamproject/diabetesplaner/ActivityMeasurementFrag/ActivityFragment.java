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

import java.util.ArrayList;

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
                // Create DirectoryChooserDialog and register a callback
                DirectoryChooserDialog directoryChooserDialog =
                        new DirectoryChooserDialog(getActivity(),
                                new DirectoryChooserDialog.ChosenDirectoryListener()
                                {
                                    @Override
                                    public void onChosenDir(String chosenDir)
                                    {
                                        m_chosenDir = chosenDir;
                                        FileList.add(chosenDir);
                                        ((AdapterView<ListAdapter>) lv).setAdapter(adapter);
                                        Toast.makeText(
                                                getActivity(), "Chosen directory: " +
                                                        chosenDir, Toast.LENGTH_LONG).show();
                                    }
                                });
                // Toggle new folder button enabling
                directoryChooserDialog.setNewFolderEnabled(m_newFolderEnabled);
                // Load directory chooser dialog for initial 'm_chosenDir' directory.
                // The registered callback will be called upon final directory selection.
                directoryChooserDialog.chooseDirectory(m_chosenDir);
                m_newFolderEnabled = ! m_newFolderEnabled;
            }
        });

        return inflaterView;
    }





}


