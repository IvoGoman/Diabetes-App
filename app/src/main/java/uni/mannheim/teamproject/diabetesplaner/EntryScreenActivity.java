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
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

import uni.mannheim.teamproject.diabetesplaner.DailyRoutine.DailyRoutineFragment;
import uni.mannheim.teamproject.diabetesplaner.DailyRoutine.DailyRoutineView;
import uni.mannheim.teamproject.diabetesplaner.SettingsActivity.SettingsActivity;
import uni.mannheim.teamproject.diabetesplaner.SettingsActivity.SettingsFragment;
import uni.mannheim.teamproject.diabetesplaner.StatisticsFragment.StatisticsFragment;

public class EntryScreenActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static Menu optionsMenu;
    private static MenuItem actualMenuItem;
    public static NavigationView navigationView;
    public static TextView username;

    public static final String TAG = SettingsFragment.class.getSimpleName();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

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
        Fragment fragment = new DailyRoutineFragment();
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
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //sets visibility of icons in the ActionBar
        MenuItem addItem = menu.findItem(R.id.add_icon_action_bar);
        addItem.setVisible(false);
        MenuItem deleteItem = menu.findItem(R.id.delete_icon_action_bar);
        deleteItem.setVisible(false);
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        this.optionsMenu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_item_action_bar, menu);
        getMenuInflater().inflate(R.menu.delete_icon_action_bar, menu);
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
                ArrayList<DailyRoutineView> dailyRoutine = DailyRoutineFragment.getActivityList();
                LinearLayout linearLayout = DailyRoutineFragment.getLinearLayout();
                for(int i=0; i<dailyRoutine.size();i++){
                    if(dailyRoutine.get(i).isSelected()){
                        linearLayout.removeView(dailyRoutine.get(i));
                        //TODO handle the remove event within the database
                    }
                }

                //do sth with the delete icon
                return true;
        }


        return super.onOptionsItemSelected(item);
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
        Fragment fragment = null;

        MenuItem addItem = optionsMenu.findItem(R.id.add_icon_action_bar);
        addItem.setVisible(false);

        if (id == R.id.nav_daily_routine) {
            Toast.makeText(this, R.string.menu_item_daily_routine, Toast.LENGTH_SHORT).show();

            fragment = new DailyRoutineFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame, fragment);
            ft.commit();

        } else if (id == R.id.nav_activity_input) {
            Toast.makeText(this, R.string.menu_item_activity_input, Toast.LENGTH_SHORT).show();

            fragment = new ActivityInputFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame, fragment);
            ft.commit();

            addItem.setVisible(true);

        } else if (id == R.id.nav_statistics) {
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
