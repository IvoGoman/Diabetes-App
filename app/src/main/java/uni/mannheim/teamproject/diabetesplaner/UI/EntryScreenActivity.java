package uni.mannheim.teamproject.diabetesplaner.UI;


import android.Manifest;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

import uni.mannheim.teamproject.diabetesplaner.DataMining.Recommendation.BSInputRecommendation;
import uni.mannheim.teamproject.diabetesplaner.DataMining.Recommendation.Recommendation;
import uni.mannheim.teamproject.diabetesplaner.DataMining.Recommendation.RoutineRecommendation;
import uni.mannheim.teamproject.diabetesplaner.Database.DataBaseHandler;
import uni.mannheim.teamproject.diabetesplaner.Domain.DayHandler;
import uni.mannheim.teamproject.diabetesplaner.R;
import uni.mannheim.teamproject.diabetesplaner.TechnicalServices.GPS_Service.GPS_Service;
import uni.mannheim.teamproject.diabetesplaner.UI.ActivityMeasurementFrag.ActivityFragment;
import uni.mannheim.teamproject.diabetesplaner.UI.DailyRoutine.ActivityLimitDialog;
import uni.mannheim.teamproject.diabetesplaner.UI.DailyRoutine.AddDialog;
import uni.mannheim.teamproject.diabetesplaner.UI.DailyRoutine.DailyRoutineFragment;
import uni.mannheim.teamproject.diabetesplaner.UI.DailyRoutine.DailyRoutineView;
import uni.mannheim.teamproject.diabetesplaner.UI.DailyRoutine.EditDialog;
import uni.mannheim.teamproject.diabetesplaner.UI.DailyRoutine.InputDialog;
import uni.mannheim.teamproject.diabetesplaner.UI.DailyRoutine.MeasurementDialog;
import uni.mannheim.teamproject.diabetesplaner.UI.SettingsActivity.SettingsActivity;
import uni.mannheim.teamproject.diabetesplaner.UI.StatisticsFragment.ChartFragment;
import uni.mannheim.teamproject.diabetesplaner.UI.StatisticsFragment.StatisticsFragment;
import uni.mannheim.teamproject.diabetesplaner.Utility.AppGlobal;
import uni.mannheim.teamproject.diabetesplaner.Utility.TimeUtils;

/**
 * created by Stefan
 */
public class EntryScreenActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static Menu optionsMenu;
    public static NavigationView navigationView;
    private static String imagePath;
    public static RoutineRecommendation recServiceRoutine;
    public static BSInputRecommendation recServiceBS;

    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    public static final String TAG = EntryScreenActivity.class.getSimpleName();

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private Fragment fragment;
    private Intent recIntent;
    private MenuItem actualMenuItem;
    private boolean mBoundRoutineRec = false;
    private boolean mBoundBSRec = false;
    private MenuItem addMeasurements;
    private MenuItem editItem;
    private MenuItem addItem;
    private MenuItem deleteItem;
    private MenuItem addItemRoutine;
    private MenuItem chooseDateItem;

    /**
     * @param savedInstanceState
     * @author Stefan
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            //set the content view
            setContentView(R.layout.activity_main);

            //create the ActionBar
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
            setSupportActionBar(toolbar);

            //create the Navigation Drawer Layout
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();


            navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {

                    navigationView.removeOnLayoutChangeListener( this );

                    //Change of Username
                    TextView textView = (TextView) navigationView.findViewById(R.id.username);
                    DataBaseHandler database = AppGlobal.getHandler();
                    if (database.getUser(AppGlobal.getHandler(),database.getUserID(AppGlobal.getHandler()))!=null)
                    {
                        textView.setText(database.getUser(AppGlobal.getHandler(), database.getUserID(AppGlobal.getHandler()))[0] + " " +
                                database.getUser(AppGlobal.getHandler(), database.getUserID(AppGlobal.getHandler()))[1]);
                    }
                }
            });


            //create a DailyRoutineFragment (start page)
            fragment = new DailyRoutineFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame, fragment);
            ft.commit();

            //starts and binds to recommendation service
            startRec(Recommendation.ROUTINE_REC);
            startRec(Recommendation.BS_REC);


            // ATTENTION: This was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        }catch(Exception e)
        {
            e.getMessage();
        }
    }

    /**
     * @author Stefan
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if(DailyRoutineView.getSelectedActivities().size()>0){
            DailyRoutineView.deselectAll();
        }else{
            super.onBackPressed();
        }
    }

    /**
     *
     * @param menu
     * @return
     * @author Stefan
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //initialize all icons and set them to invisible
        addMeasurements = menu.findItem(R.id.addMeasurements_icon_action_bar_routine);
        addMeasurements.setVisible(false);
        addItem = menu.findItem(R.id.add_icon_action_bar);
        addItem.setVisible(false);
        deleteItem = menu.findItem(R.id.delete_icon_action_bar);
        deleteItem.setVisible(false);
        editItem = menu.findItem(R.id.edit_icon_action_bar_routine);
        editItem.setVisible(false);
        addItemRoutine = menu.findItem(R.id.add_icon_action_bar_routine);
        addItemRoutine.setVisible(false);
        chooseDateItem = menu.findItem(R.id.chooseDate_action_bar);
        chooseDateItem.setVisible(false);

        //handles the visibility of the action bar icons depending on the fragment
        if(fragment instanceof DailyRoutineFragment){
            addMeasurements.setVisible(true);
            addItemRoutine.setVisible(true);
        }else if(fragment instanceof ActivityFragment){

        }else if(fragment instanceof StatisticsFragment){
            chooseDateItem.setVisible(true);
        }else if(fragment instanceof HistoryFragment){
            addMeasurements.setVisible(true);
            addItemRoutine.setVisible(true);
        }

        super.onPrepareOptionsMenu(menu);
        return true;
    }

    /**
     *
     * @param menu
     * @return
     * @author Stefan
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionsMenu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.items_action_bar, menu);

        return true;
    }

    /**
     * handles ActionBar clicks
     * @param item
     * @return
     * @author Stefan
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //handles event when add button in the ActionBar on the ActivityInputFragment page was clicked
        switch (id){
            case R.id.add_icon_action_bar:
                //TODO: add activity log
                ActivityInputFragment.list.add("ActivityData.csv");

                ListAdapter mAdapter = new CustomListView(this, ActivityInputFragment.list);

                ActivityInputFragment.addListItem(mAdapter);

                return true;

            case R.id.delete_icon_action_bar:
                //Get the currently selected items and removes them
                if(fragment instanceof DailyRoutineFragment) {
                    DayHandler drHandler = ((DailyRoutineFragment) fragment).getDrHandler();
                    if(drHandler.getDailyRoutine().size() == 1 || getIndexesOfSelected(((DailyRoutineFragment) fragment)).size() == drHandler.getDailyRoutine().size()){
                        ActivityLimitDialog ald = new ActivityLimitDialog();
                        ald.show(getFragmentManager(), "editDialog");
                    }else {
                        //  drHandler.ic_delete(getIndexesOfSelected(((DailyRoutineFragment) fragment)));
                        ArrayList<Integer> selected = getIndexesOfSelected((DailyRoutineFragment) fragment);
                        for (int i = 0; i < selected.size(); i++) {
                            String start = TimeUtils.dateToDateTimeString(drHandler.getDailyRoutine().get(selected.get(i)).getStarttime());
                            String end = TimeUtils.dateToDateTimeString(drHandler.getDailyRoutine().get(selected.get(i)).getEndtime());
                            AppGlobal.getHandler().DeleteActivity(AppGlobal.getHandler(), start, end);
                        }
                        drHandler.update();

                    }
                }

                //do sth with the ic_delete icon
                return true;
            case R.id.edit_icon_action_bar_routine:

                EditDialog editDialog = new EditDialog();
                if(fragment instanceof DailyRoutineFragment){
                    editDialog.setDayHandler(((DailyRoutineFragment) fragment).getDrHandler());
                    editDialog.setActivityItem(DailyRoutineView.getSelectedActivities().get(0).getActivityItem());
                    editDialog.setSelected(getIndexesOfSelected(((DailyRoutineFragment) fragment)).get(0));
//                    editDialog.setActivity(DailyRoutineView.getSelectedActivities().get(0).getActivityID() - 1);
//                    editDialog.setStarttime(DailyRoutineView.getSelectedActivities().get(0).getStartTime());
//                    editDialog.setEndtime(DailyRoutineView.getSelectedActivities().get(0).getEndTime());
//                    editDialog.setImage(DailyRoutineView.getSelectedActivities().get(0).getImage());
//                    editDialog.setImageUri(DailyRoutineView.getSelectedActivities().get(0).getImageUri());
//                    editDialog.setMeal(DailyRoutineView.getSelectedActivities().get(0).getMeal());

                    editDialog.show(getFragmentManager(), "editDialog");
                }

                return true;

            case R.id.add_icon_action_bar_routine:
                //create an AddDialog
                AddDialog addDialog = new AddDialog();
                if(fragment instanceof DailyRoutineFragment) {
                    addDialog.setDayHandler(((DailyRoutineFragment) fragment).getDrHandler());
                    addDialog.setDate(((DailyRoutineFragment) fragment).getDrHandler().getDate());
                }
                addDialog.show(getFragmentManager(),"addDialog");
                return true;

            case R.id.addMeasurements_icon_action_bar_routine:
                MeasurementDialog measurementDialog = new MeasurementDialog();
                measurementDialog.show(getFragmentManager(),"MeasurementDialog");
                return true;

            case R.id.chooseDate_action_bar:
                int itemId = item.getItemId();
                //get the currently active fragment that is shown to the user
                Fragment active = null;
                FragmentManager fragmentManager = this.getSupportFragmentManager();
                List<Fragment> fragments = fragmentManager.getFragments();
                for (Fragment fragment : fragments) {
                    if (fragment != null && fragment.getUserVisibleHint())
                        active = fragment;
                }
                //update the chart of the fragment based on the TimeWindow selected
                if (!(active == null)) {
                    switch (itemId) {
                        case R.id.statistic_day:
//                    TODO:Update the charts by calling them by their ID
                            try{
                                ChartFragment chartFragment = (ChartFragment) active;
                                chartFragment.updateChart("DAY");
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            break;
                        case R.id.statistic_week:
//                    TODO: Update the charts by calling them by their ID
                            try{
                                ChartFragment chartFragment = (ChartFragment) active;
                                chartFragment.updateChart("WEEK");
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            break;
                        case R.id.statistic_month:
//                    TODO:Update the charts by calling them by their ID
                            try{
                                ChartFragment chartFragment = (ChartFragment) active;
                                chartFragment.updateChart("MONTH");
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            break;
                    }
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Returns the indexes of the items that were selected by the user
     * @return
     * @author Stefan
     */
    public ArrayList<Integer> getIndexesOfSelected(DailyRoutineFragment fragment){
        ArrayList<DailyRoutineView> items = fragment.getActivityList();
        ArrayList<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isSelected()) {
                indexes.add(i);
            }
        }
        return indexes;
    }

    /**
     * handles if a menu item in the navigation drawer was selected
     * @param item
     * @return
     * @author Stefan
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //TODO create backstack !
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        actualMenuItem = item;
        fragment = null;

        //set all action bar items to invisible
        addItem.setVisible(false);
        deleteItem.setVisible(false);
        editItem.setVisible(false);
        addItemRoutine.setVisible(false);
        addMeasurements.setVisible(false);
        chooseDateItem.setVisible(false);

        if (id == R.id.nav_daily_routine) {
            Toast.makeText(this, R.string.menu_item_daily_routine, Toast.LENGTH_SHORT).show();

            fragment = new DailyRoutineFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame, fragment);
            ft.commit();

            addMeasurements.setVisible(true);
            addItemRoutine.setVisible(true);

        }else if (id == R.id.nav_activity_measurement) {
            Toast.makeText(this, R.string.menu_item_activity, Toast.LENGTH_SHORT).show();
//            TEST
//            Log.d(TAG,"All Days: ");
//            ArrayList<ArrayList<ActivityItem>> list = AppGlobal.getHandler().getAllDays(PredictionFramework.WEEKDAYS);
//            for(int i=0; i<list.size(); i++){
//                Log.d(TAG, "New Day");
//                for(int j=0; j<list.get(i).size(); j++){
//                    Log.d(TAG, list.get(i).get(j).print());
//                }
//            }

            fragment = new ActivityFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame, fragment);
            ft.commit();
        }
        else if (id == R.id.nav_statistics) {
            Toast.makeText(this, R.string.menu_item_statistics, Toast.LENGTH_SHORT).show();


            fragment = new StatisticsFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame, fragment);
            ft.commit();

            chooseDateItem.setVisible(true);

        } else if (id == R.id.nav_history) {
            Toast.makeText(this, R.string.menu_item_history, Toast.LENGTH_SHORT).show();

            fragment = new HistoryFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame, fragment);
            ft.commit();

            addItemRoutine.setVisible(true);
            addMeasurements.setVisible(true);

        } else if (id == R.id.nav_settings) {
            Toast.makeText(this, R.string.menu_item_settings, Toast.LENGTH_SHORT).show();

            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);

        } else if (id == R.id.nav_share) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        System.out.println("in onStart");
        my_permissions();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "EntryScreen Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://uni.mannheim.teamproject.diabetesplaner/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);


    }

    public void my_permissions(){

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else {
            startGPS_Service();
        }

    }


    public void startGPS_Service(){
        System.out.println("in Start GPS Service");
        Intent myIntent = new Intent(this, GPS_Service.class);
        this.startService(myIntent);
    }

    /**
     * @author Stefan
     */
    @Override
    protected void onRestart() {
        startRec(Recommendation.ROUTINE_REC);
        startRec(Recommendation.BS_REC);
        super.onRestart();
    }

    @Override
    public void onStop() {
        super.onStop();

        //unbind the recommendation service
        stopRec(Recommendation.ROUTINE_REC);
        stopRec(Recommendation.BS_REC);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "EntryScreen Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://uni.mannheim.teamproject.diabetesplaner/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    /**
     * getter for ActionBar
     * @return optionsMenu (ActionBar)
     * @author Stefan
     */
    public static Menu getOptionsMenu(){
        return optionsMenu;
    }

    /**
     * returns URI of image captured
     * @return
     * @author Stefan
     */
    public static String getImagePath(){
        return imagePath;
    }



    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @author Stefan
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                imagePath = InputDialog.getImagePath();
                InputDialog.displayImageFromPath(imagePath);

                Toast.makeText(this, "Image saved to:\n" +
                        imagePath, Toast.LENGTH_SHORT).show();

            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
                imagePath = null;
            } else {
                // Image capture failed, advise user
                imagePath = null;
                Toast.makeText(this, R.string.image_failed, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(9999);

    }

    /**
     * create a service connection for the RoutineRecommendation
     * @author Stefan 05.07.2016
     */
    protected ServiceConnection mServiceConnRoutineRec = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.d(TAG, "onTaskServiceConnected");
            RoutineRecommendation.RecBinder recBinder = (RoutineRecommendation.RecBinder) binder;
            recServiceRoutine = (RoutineRecommendation)(recBinder.getService());
            Log.d("Rec", "Is really null?" + recBinder.getClass().getSimpleName());

            mBoundRoutineRec = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onTaskServiceDisconnected");
            mBoundRoutineRec = false;
        }
    };

    /**
     * create a service connection for the BSInputRecommendation
     * @author Stefan 09.07.2016
     */
    protected ServiceConnection mServiceConnBSRec = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.d(TAG, "onBSServiceConnected");
            RoutineRecommendation.RecBinder recBinder = (BSInputRecommendation.RecBinder) binder;
            recServiceBS = (BSInputRecommendation)(recBinder.getService());
            mBoundBSRec = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onBSServiceDisconnected");
            mBoundBSRec = false;
        }
    };


    /**
     * bind and start the recommendation service
     * @param recType specifies the type of recommendation to start
     * @author Stefan 05.07.2016
     */
    public void startRec(int recType) {
        switch (recType){
            case 0: {
                Intent rec = new Intent(this, RoutineRecommendation.class);
                bindService(rec, mServiceConnRoutineRec, Context.BIND_AUTO_CREATE);
                startService(rec);
                break;
            }
            case 1: {
                Intent rec = new Intent(this, BSInputRecommendation.class);
                bindService(rec, mServiceConnBSRec, Context.BIND_AUTO_CREATE);
                startService(rec);
                break;
            }
        }

    }

    /**
     * stop and unbind the recommendation service
     * @param recType specifies the type of recommendation to stop
     * @author Stefan 05.07.2016
     */
    public void stopRec(int recType) {
        switch (recType) {
            case 0: {
                stopService(new Intent(this, RoutineRecommendation.class));
                // Unbind from the service
                if (mBoundRoutineRec) {
                    unbindService(mServiceConnRoutineRec);
                    mBoundRoutineRec = false;
                }
                break;
            }
            case 1: {
                stopService(new Intent(this, BSInputRecommendation.class));
                // Unbind from the service
                if (mBoundBSRec) {
                    unbindService(mServiceConnBSRec);
                    mBoundBSRec = false;
                }
                break;
            }
        }
    }

    /**
     * returns the task recommendation service
     * @return
     * @author Stefan 05.07.2016
     */
    public RoutineRecommendation getRoutineRecommendationService(){
        return recServiceRoutine;
    }

    /**
     * returns the bloodsugar input recommendation service
     * @return
     * @author Stefan 09.07.2016
     */
    public BSInputRecommendation getBSInputRecommendationService(){
        return recServiceBS;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        System.out.println("in RequestPermissions");

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                    startGPS_Service();

                } else {

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