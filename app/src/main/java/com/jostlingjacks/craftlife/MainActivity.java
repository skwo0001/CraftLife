package com.jostlingjacks.craftlife;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

import static com.jostlingjacks.craftlife.Channel.CHANNEL_ID_1;
import static com.jostlingjacks.craftlife.Channel.CHANNEL_ID_2;
import static com.jostlingjacks.craftlife.Channel.CHANNEL_ID_3;

/**
 * This is the mainActivity that to start the background service and set up the application
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity" ;
    boolean doubleBackToExitPressedOnce = false;
    private DataBaseHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * the following code is for getting the settings from user settings.
         *
         */
        db = new DataBaseHelper(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.content_frame, new MainFragment()).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // get the user info shared preference.
        SharedPreferences userInfoSharedPreferences = getSharedPreferences("CURRENT_USER_INFO", MODE_PRIVATE);
        String emailAddress = userInfoSharedPreferences.getString("CURRENT_USER_EMAIL", "");

        // once the user logs in, the name should be displayed in the navigation drawer..
        this.showUserInfoInNaviHeader(navigationView, userInfoSharedPreferences);

        if (!db.getSettingRepeat(emailAddress,"regular")){
            db.addSetting(emailAddress,"regular","15 minutes");
        }
        if (!db.getSettingRepeat(emailAddress,"art location")) {
            db.addSetting(emailAddress, "art location", "1 hour");
        }
        if (!db.getSettingRepeat(emailAddress,"event")) {
            db.addSetting(emailAddress,"event","2 hour");
        }

        //Hold 3 job services, 1 is for event, 1 is for regular and 1 is for art location
        Cursor dailyNotificationIntervalcursor = db.getSetting(emailAddress,"regular");
        String dailyNotificationIntervalString =  dailyNotificationIntervalcursor.getString(0);
        int dailyNotificationInterval = 0;
        String exactint = "";
        if (!dailyNotificationIntervalString.toLowerCase().contains("off")){
            if (dailyNotificationIntervalString.toLowerCase().contains("minutes")){
                exactint = dailyNotificationIntervalString.substring(0,2);
                dailyNotificationInterval = Integer.parseInt(exactint);
            } else {
                exactint = dailyNotificationIntervalString.substring(0,1);
                dailyNotificationInterval = Integer.parseInt(exactint);
                dailyNotificationInterval = dailyNotificationInterval * 60 ;
            }
        }

        if (!runtime_permissions()) {
            ComponentName componentName = new ComponentName(this, SendRequest.class);
            PersistableBundle bundle = new PersistableBundle();
            bundle.putString("request","regular");
            JobInfo info = new JobInfo.Builder(123, componentName)
                    .setPersisted(true)
                    .setExtras(bundle)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setRequiresBatteryNotLow(true)
                    .setPeriodic(dailyNotificationInterval * 60 * 1000, dailyNotificationInterval * 60 * 1000)  //set the job work in schedule and the minimum is 15 mins for SDK 24 and above
                    //set the task will do whe  n the network is connected
                    .build();

            JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            int resultCode = scheduler.schedule(info);
            if (resultCode == JobScheduler.RESULT_SUCCESS) {
                Log.d(TAG, "Job scheduled");
            } else {
                Log.d(TAG, "Job scheduling failed");
            }
        }

        Cursor locationNotificationIntervalcursor = db.getSetting(emailAddress,"art location");
        String locationNotificationIntervalString =  locationNotificationIntervalcursor.getString(0);
        int locationNotificationInterval = 0;
        String exactLoint = "";
        if (!locationNotificationIntervalString.toLowerCase().contains("off")){
            exactLoint = locationNotificationIntervalString.substring(0,1);
            locationNotificationInterval = Integer.parseInt(exactLoint);
            locationNotificationInterval = locationNotificationInterval * 60 ;
        }

        if (!runtime_permissions()) {
            ComponentName componentName = new ComponentName(this, SendRequest.class);
            PersistableBundle bundle = new PersistableBundle();
            bundle.putString("request","art location");
            JobInfo info = new JobInfo.Builder(124, componentName)
                    .setPersisted(true)
                    .setExtras(bundle)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setRequiresBatteryNotLow(true)
                    .setPeriodic(locationNotificationInterval * 60 * 1000, locationNotificationInterval * 60 * 1000)  //set the job work in schedule and the minimum is 15 mins for SDK 24 and above
                    //set the task will do whe  n the network is connected
                    .build();

            JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            int resultCode = scheduler.schedule(info);
            if (resultCode == JobScheduler.RESULT_SUCCESS) {
                Log.d(TAG, "Job scheduled");
            } else {
                Log.d(TAG, "Job scheduling failed");
            }
        }

        Cursor eventNotificationIntervalcursor = db.getSetting(emailAddress,"event");
        String eventNotificationIntervalString =  eventNotificationIntervalcursor.getString(0);
        int eventNotificationInterval = 0;
        String exactEventint = "";
        if (!eventNotificationIntervalString.toLowerCase().contains("off")){
            exactLoint = eventNotificationIntervalString.substring(0,1);
            eventNotificationInterval = Integer.parseInt(exactLoint);
            eventNotificationInterval = eventNotificationInterval * 60 ;
        }

        if (!runtime_permissions()) {
            ComponentName componentName = new ComponentName(this, SendRequest.class);
            PersistableBundle bundle = new PersistableBundle();
            bundle.putString("request","event");
            JobInfo info = new JobInfo.Builder(125, componentName)
                    .setPersisted(true)
                    .setExtras(bundle)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setRequiresBatteryNotLow(true)
                    .setPeriodic(eventNotificationInterval * 60 * 1000,  eventNotificationInterval * 60 * 1000)  //set the job work in schedule and the minimum is 15 mins for SDK 24 and above
                    //set the task will do whe  n the network is connected
                    .build();

            JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            int resultCode = scheduler.schedule(info);
            if (resultCode == JobScheduler.RESULT_SUCCESS) {
                Log.d(TAG, "Job scheduled");
            } else {
                Log.d(TAG, "Job scheduling failed");
            }
        }
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

    @SuppressLint("StaticFieldLeak")
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout){

            SharedPreferences registerPreferences = getSharedPreferences("REGISTER_PREFERENCES", MODE_PRIVATE);
            SharedPreferences.Editor editor = registerPreferences.edit();
            // the key is, for example: email_address@outlook.com123456jhdata, data of new email address
            // and new password are stored by given each a new line.
            editor.putString("UserEmailAddress", "");
            editor.putString("UserPassword", "");
            editor.commit();

            Toast.makeText(getBaseContext(), "Logout Successful!", Toast.LENGTH_LONG).show();
            Intent main = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(main);
        } else {

            Fragment nextFragment = null;
            switch (id) {
                case R.id.nav_home:
                    nextFragment = new MainFragment();
                    break;
                case R.id.nav_pastnoti:
                    nextFragment = new PastNotiFragment();
                    break;
                case R.id.nav_stat:
                    nextFragment = new FragmentStat();
                    break;
                case R.id.nav_settings:
                    nextFragment = new SettingFragment();
                    break;
                case R.id.nav_aboutus:
                    nextFragment = new AboutUsFragment();
                    break;
                case R.id.nav_tutorial:
                    nextFragment = new TutorialFragment();
                    break;
                case R.id.nav_documentation:
                    nextFragment = new DocumentFragment();
                    break;
                case R.id.nav_to_do_list:
                    nextFragment = new ToDoListFragment();
                    break;
            }

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, nextFragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean runtime_permissions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.
                ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);

            return true;
        }
        return false;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

            } else {
                runtime_permissions();
            }
        }
    }
    private void showUserInfoInNaviHeader(NavigationView navigationView, SharedPreferences userInfoSharedPreferences){
        View headerView = navigationView.getHeaderView(0);
        TextView welcomeTextView = (TextView) headerView.findViewById(R.id.header_title_navigation_drawer_textview);
        welcomeTextView.setText("Welcome!");
        TextView navUsername = (TextView) headerView.findViewById(R.id.header_subtitle_navigation_drawer_textview);
        /**
         * TODO: the shared preferences can acutally put into a class variable.
         * TODO: Done!!!
         */
        String emailAddress = userInfoSharedPreferences.getString("CURRENT_USER_EMAIL", "");
        navUsername.setText(emailAddress);
    }

    private void logoutCurrentUser(){
        Intent main = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(main);
        finish();
    }


}
