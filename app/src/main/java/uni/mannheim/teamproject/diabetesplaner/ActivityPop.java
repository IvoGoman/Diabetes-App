package uni.mannheim.teamproject.diabetesplaner;

import android.os.Bundle;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import uni.mannheim.teamproject.diabetesplaner.ActivityMeasurementFrag.ActivityMeasurementFragment;

/**
 * created by Naira
 */
public class ActivityPop extends AppCompatActivity {

    String activityItem;
    ListView listView;
    public ArrayAdapter<String> mActivityAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_activity_window);

        // adjust the format of the popup
        DisplayMetrics dm= new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .4));

        //view the list on to the pop up
        populateListView();

        //set action bar title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Activities");
    }


   /* public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View inflaterView = inflater.inflate(R.layout.pop_activity_window, container, false);
        final String[] activities = {
                "ACTIVITIES",
                "Jogging",
                "Eating",
                "Sports",
                "Socializing",
        };

      /*  List<String> activityList = new ArrayList<String>(Arrays.asList(activities));
        final ArrayAdapter<String> mActivityAdapter =
                new ArrayAdapter<String>(
                        this,
                        R.layout.pop_activity_window,
                        R.id.activityList,
                        activityList);

        ListView listView = (ListView) findViewById(R.id.activityList);
        listView.setAdapter(mActivityAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Toast.makeText(ActivityPop.this, "activity added", Toast.LENGTH_SHORT).show();

            }
        });*/
     //   return inflaterView;

   // }

    private void populateListView(){
        final String[] activities = {
                "Jogging",
                "Eating",
                "Sports",
                "Socializing"
        };
         mActivityAdapter =
                new ArrayAdapter<String>(
                        this,
                        R.layout.activity_layout,
                        activities);
        listView = (ListView) findViewById(R.id.ActivitylistView);
        listView.setAdapter(mActivityAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                 activityItem= mActivityAdapter.getItem(position);
               // Toast.makeText(ActivityPop.this, "activity added", Toast.LENGTH_SHORT).show();
                Bundle bundle=new Bundle();
                bundle.putString("activityItem", activityItem);
                //set Fragmentclass Arguments
              //  ActivityMeasurementFragment.ActivityFragment fragobj=new ActivityMeasurementFragment.ActivityFragment();
               // fragobj.setArguments(bundle);
               }
        });

    }

    /*public String DoSomething() {
        final String[] activities = {
                "Jogging",
                "Eating",
                "Sports",
                "Socializing"
        };
        mActivityAdapter =
                new ArrayAdapter<String>(
                        this,
                        R.layout.activity_layout,
                        activities);
        listView = (ListView) findViewById(R.id.ActivitylistView);
        listView.setAdapter(mActivityAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                activityItem= mActivityAdapter.getItem(position);
                // Toast.makeText(ActivityPop.this, "activity added", Toast.LENGTH_SHORT).show();
            }
        });
        return activityItem;

    }*/






}

