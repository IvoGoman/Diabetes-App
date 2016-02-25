package uni.mannheim.teamproject.diabetesplaner;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

import uni.mannheim.teamproject.diabetesplaner.ActivityMeasurementFrag.ActivityMeasurementFragment;
import uni.mannheim.teamproject.diabetesplaner.Backend.DailyRoutineHandler;
import uni.mannheim.teamproject.diabetesplaner.DailyRoutine.AddDialog;
import uni.mannheim.teamproject.diabetesplaner.DailyRoutine.DailyRoutineFragment;
import uni.mannheim.teamproject.diabetesplaner.DailyRoutine.DailyRoutineView;
import uni.mannheim.teamproject.diabetesplaner.DailyRoutine.EditDialog;
import uni.mannheim.teamproject.diabetesplaner.SettingsActivity.SettingsActivity;
import uni.mannheim.teamproject.diabetesplaner.StatisticsFragment.StatisticsFragment;

public class EntryScreenActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static Menu optionsMenu;
    private static MenuItem actualMenuItem;
    public static NavigationView navigationView;
    public static TextView username;

    public static final String TAG = EntryScreenActivity.class.getSimpleName();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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


        //create a DailyRoutineFragment (start page)
        fragment = new DailyRoutineFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFrame, fragment);
        ft.commit();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


    }

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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //sets visibility of icons in the ActionBar
        //item for activity input
        MenuItem addItem = menu.findItem(R.id.add_icon_action_bar);
        addItem.setVisible(false);

        //items for daily routine
        MenuItem addItemRoutine = menu.findItem(R.id.add_icon_action_bar_routine);
        addItemRoutine.setVisible(true);
        MenuItem editItem = menu.findItem(R.id.edit_icon_action_bar_routine);
        editItem.setVisible(false);
        MenuItem deleteItem = menu.findItem(R.id.delete_icon_action_bar);
        deleteItem.setVisible(false);

        super.onPrepareOptionsMenu(menu);
        return true;
    }

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
                    DailyRoutineHandler drHandler = ((DailyRoutineFragment) fragment).getDrHandler();
                    drHandler.delete(getIndexesOfSelected());
                }

                //do sth with the delete icon
                return true;
            case R.id.edit_icon_action_bar_routine:

                EditDialog editDialog = new EditDialog();
                if(fragment instanceof DailyRoutineFragment){
                    editDialog.setDailyRoutineHandler(((DailyRoutineFragment) fragment).getDrHandler());
                    editDialog.setSelected(getIndexesOfSelected().get(0));
                    editDialog.setActivity(DailyRoutineView.getSelectedActivities().get(0).getActivityID() - 1);
                    editDialog.setStarttime(DailyRoutineView.getSelectedActivities().get(0).getStartTime());
                    editDialog.setEndtime(DailyRoutineView.getSelectedActivities().get(0).getEndTime());
                    editDialog.show(getFragmentManager(), "editDialog");
                }

                return true;

            case R.id.add_icon_action_bar_routine:
                //create an AddDialog
                AddDialog addDialog = new AddDialog();
                if(fragment instanceof DailyRoutineFragment) {
                    addDialog.setDailyRoutineHandler(((DailyRoutineFragment) fragment).getDrHandler());
                }
                addDialog.show(getFragmentManager(),"addDialog");
                return true;
        }



        return super.onOptionsItemSelected(item);
    }

    /**
     * Returns the indexes of the items that were selected by the user
     * @return
     */
    public ArrayList<Integer> getIndexesOfSelected(){
        ArrayList<DailyRoutineView> items = DailyRoutineFragment.getActivityList();
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
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //TODO create backstack !
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        actualMenuItem = item;
        fragment = null;

        //set all action items to invisible
        MenuItem addItem = optionsMenu.findItem(R.id.add_icon_action_bar);
        addItem.setVisible(false);
        MenuItem deleteItem = EntryScreenActivity.getOptionsMenu().findItem(R.id.delete_icon_action_bar);
        deleteItem.setVisible(false);
        MenuItem editItem = EntryScreenActivity.getOptionsMenu().findItem(R.id.edit_icon_action_bar_routine);
        editItem.setVisible(false);
        MenuItem addItemRoutine = EntryScreenActivity.getOptionsMenu().findItem(R.id.add_icon_action_bar_routine);
        addItemRoutine.setVisible(false);

        if (id == R.id.nav_daily_routine) {
            Toast.makeText(this, R.string.menu_item_daily_routine, Toast.LENGTH_SHORT).show();

            fragment = new DailyRoutineFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame, fragment);
            ft.commit();

            addItemRoutine.setVisible(true);

        } else if (id == R.id.nav_activity_input) {
            Toast.makeText(this, R.string.menu_item_activity_input, Toast.LENGTH_SHORT).show();

            fragment = new ActivityInputFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame, fragment);
            ft.commit();

            addItem.setVisible(true);

        }else if (id == R.id.nav_activity_measurement) {
            Toast.makeText(this, R.string.menu_item_activity_measurement, Toast.LENGTH_SHORT).show();

            fragment = new ActivityMeasurementFragment();
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

        } else if (id == R.id.nav_history) {
            Toast.makeText(this, R.string.menu_item_history, Toast.LENGTH_SHORT).show();

            fragment = new HistoryFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame, fragment);
            ft.commit();

            addItemRoutine.setVisible(true);

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

    @Override
    public void onStop() {
        super.onStop();

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
     */
    public static Menu getOptionsMenu(){
        return optionsMenu;
    }

}
