package uni.mannheim.teamproject.diabetesplaner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * created by Naira
 */
public class ActivityFragment extends Fragment implements AbsListView.OnItemClickListener {

    private static ListAdapter adapter;
    public static AbsListView lv;
    public static ArrayList<String> activityList;
    public static final String TAG = ActivityFragment.class.getSimpleName();
    public static String activities[] = new String[]{"jogging", "running", "sleeping"};
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    //public static ArrayList<String> list = new ArrayList<String>();
   // private static AbsListView mListView;
   // private ListAdapter mAdapter;
    private static View inflaterView;
    public static int index;


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
        adapter = new CustomListView(getActivity(), activityList);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflaterView = inflater.inflate(R.layout.fragment_activity, container, false);
        ImageButton b= (ImageButton) inflaterView.findViewById(R.id.imageButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // final String activities[] = new String[]{"jogging", "running", "sleeping"};
                activityList = new ArrayList<String>();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Select Activity");
                lv = (AbsListView) inflaterView.findViewById(R.id.listView);
                //adapter = new CustomListView(getActivity(), activityList);
                adapter = new CustomListView(getActivity(), activityList);
                builder.setItems(activities, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        index = which;
                        activityList.add(activities[which]);
                       // adapter = new CustomListView(getActivity(), activityList);
                        ((AdapterView<ListAdapter>) lv).setAdapter(adapter);

                    }
                });
                builder.show();
            }
        });

        return inflaterView;

    }

    public int getIndex() {
        return index;
    }

    public static void addListItem(ListAdapter adapter){
        inflaterView.findViewById(android.R.id.empty).setVisibility(View.INVISIBLE);
        ((AdapterView<ListAdapter>) lv).setAdapter(adapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            //mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }

    }

    public void setEmptyText(CharSequence emptyText) {
        View emptyView = lv.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }
}
