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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity" ;
    private NotificationManager notificationManager,notificationManager2,notificationManager3;
    private LocationManager locationManager;
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendLiveRequest();
            }
        });

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

        //Hold 3 job services, 1 is for event, 1 is for regular and i is for art location
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

    @SuppressLint("StaticFieldLeak")
    public void sendLiveRequest() {
        new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Void... params) {
                JSONObject jsonObject = null;
                JSONObject jsonReply = null;
                try {
                    jsonObject = getLastLocation();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                int i = Tool.randomNumberGenerator(100);
                String jsonString = null;
                if (i % 2 == 0) {
                    jsonString = HTTPDataHandler.getLocationNotification(jsonObject);
                } else {
                    jsonString = HTTPDataHandler.getRegularNotification();
                }

                if (jsonString != "") {
                    try {
                        jsonReply = new JSONObject(jsonString.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                return jsonReply;
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected void onPostExecute(JSONObject jsonObject) {

                createNotification(jsonObject);
                Log.d("jsonObject", jsonObject.toString());

            }
        }.execute();
    }

    public JSONObject getLastLocation() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        Double[] latitudeAndLongtidue = new Double[2];
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return ;
        }
        Location lastLocation =  locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

        if (lastLocation == null){
            latitudeAndLongtidue[0] = -37.8770;
            latitudeAndLongtidue[1] = 145.0443;
        } else {
            latitudeAndLongtidue[0] = lastLocation.getLatitude();
            latitudeAndLongtidue[1] = lastLocation.getLongitude();
        }
        jsonObject.put("lat",latitudeAndLongtidue[0]);
        jsonObject.put("lon", latitudeAndLongtidue[1]);

        return  jsonObject;
    }

    //Using the JSONObject to create the notification and also store the data to sqlite
    public void createNotification(JSONObject jsonObject) {

        String type= null, title= null, description= null, lat= null, lon= null, address= null, time = null,url = null, subtype = null;
        int notification_id = 0;

        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID_1, "Regular Notification", NotificationManager.IMPORTANCE_HIGH);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);

        NotificationChannel notificationChannel2 = new NotificationChannel(CHANNEL_ID_2, "Art Location Notification", NotificationManager.IMPORTANCE_HIGH);
        notificationManager2 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager2.createNotificationChannel(notificationChannel2);

        try {
            Iterator<String> iterator = jsonObject.keys();
            String key = iterator.next();

            type = key;
            JSONObject object = jsonObject.getJSONObject(key);
            JSONObject suggestion = jsonObject.getJSONObject(key);
            title = suggestion.getString("title");
            if (key.contains("event")){
                time = suggestion.getString("time");
                url = suggestion.getString("url");
            }else {
                description = suggestion.getString("description");
            }
            if (key.contains("location")) {
                lat = suggestion.getString("lat");
                lon = suggestion.getString("lon");
                address = suggestion.getString("address");
                subtype = suggestion.getString("type");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Bundle bundle = new Bundle();
        bundle.putString("type",type);
        bundle.putString("title",title);
        bundle.putString("description",description);
        bundle.putString("lat",lat);
        bundle.putString("lon",lon);
        bundle.putString("address",address);
        bundle.putString("time",time);
        bundle.putString("subtype",subtype);
        bundle.putString("url",url);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf2 = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String formatedate = sdf2.format(calendar.getTime());

        SharedPreferences userInfoSharedPreferences = getSharedPreferences("REGISTER_PREFERENCES", MODE_PRIVATE);
        String emailAddress = userInfoSharedPreferences.getString("UserEmailAddress", "");

        if (!type.contains("regular")){
            if (!db.getLocationnEventRepeat(emailAddress,type,title)){
                db.addSuggestion(type,title,description,address,time,emailAddress,formatedate,null,lat,lon,subtype,url);
            } else {
                db.updateNoticeTime(emailAddress,type,title,formatedate);
            }
        } else {
            db.addSuggestion(type,title,description,address,time,emailAddress,formatedate,null,lat,lon,subtype,url);
        }

        Intent resultIntent;

        if (!type.contains("regular")) {
            resultIntent = new Intent(this, NotificationDetailActivity.class);
        } else {
            resultIntent = new Intent(this, NotificationRegularDetailActivity.class);
        }
        resultIntent.putExtras(bundle);


        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent yesAnswerIntent = new Intent(this, NotificationReceiver.class);
        yesAnswerIntent.putExtra("yesAction", "1");
        yesAnswerIntent.putExtra("email", emailAddress);
        yesAnswerIntent.putExtra("title", title);
        yesAnswerIntent.putExtra("description", description);
        yesAnswerIntent.putExtra("address", address);
        PendingIntent yesPendingIntent = PendingIntent.getBroadcast(this, 1, yesAnswerIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent noAnswerIntent = new Intent(this, NotificationReceiver.class);
        noAnswerIntent.putExtra("noAction", "0");
        noAnswerIntent.putExtra("email", emailAddress);
        noAnswerIntent.putExtra("title", title);
        noAnswerIntent.putExtra("description", description);
        noAnswerIntent.putExtra("address", address);
        PendingIntent noPendingIntent = PendingIntent.getBroadcast(this, 2, noAnswerIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // the button of adding information to the to do list.
        Intent addToToDoListIntent = new Intent(this, NotificationReceiver.class);
        addToToDoListIntent.putExtra("addToToDoList", "Go " + title.toLowerCase() + " at " + address);
        PendingIntent addToToDoListPendingIntent = PendingIntent.getBroadcast(this, 3, addToToDoListIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // now keeps the notification
        Notification notification;
        if (type.toLowerCase().equals("Regular".toLowerCase())) {
            notification_id = 1;
        }else if (type.toLowerCase().equals("location".toLowerCase())) {
            notification_id = 2;}

        if (type.contains("regular")) {
            notification = new NotificationCompat.Builder(this, CHANNEL_ID_1)
                    .setContentIntent(resultPendingIntent)
                    .setSmallIcon(R.drawable.app_logo)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setLargeIcon(this.resolveNotificationIcon(description.toLowerCase()))
                    .setColor(16757760)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(true)
                    .build();

            notificationManager.notify(notification_id, notification);

        } else if (type.contains("location")){
            notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                    .setContentIntent(resultPendingIntent)
                    .setSmallIcon(R.drawable.app_logo)
                    .setContentTitle(title)
                    .setContentText(description)
                    .addAction(R.drawable.ic_yes, "Okay, I'll go", yesPendingIntent)
                    .addAction(R.drawable.ic_no, "show me less", noPendingIntent)
                    .addAction(R.drawable.ic_no, "Add To To-do List", addToToDoListPendingIntent)
                    .setLargeIcon(this.resolveNotificationIcon(description.toLowerCase()))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setColor(16757760)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(true)
                    .build();

            notificationManager2.notify(notification_id, notification);

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

    private Bitmap resolveNotificationIcon(String title){
        if (title.toLowerCase().contains("rehydrate"))
        {
            return BitmapFactory.decodeResource(this.getResources(), R.drawable.drinkwater);
        } else if (title.toLowerCase().contains("walk")){
            return BitmapFactory.decodeResource(this.getResources(), R.drawable.walking);
        } else if (title.toLowerCase().contains("stand up")){
            return BitmapFactory.decodeResource(this.getResources(), R.drawable.coach);
        }else if (title.toLowerCase().contains("meditation")){
            return BitmapFactory.decodeResource(this.getResources(), R.drawable.meditation);
        } else if (title.toLowerCase().contains("window")){
            return BitmapFactory.decodeResource(this.getResources(), R.drawable.curtain);
        } else if (title.toLowerCase().contains("gallery"))
        {
            return BitmapFactory.decodeResource(this.getResources(), R.drawable.gallery);
        } else if (title.toLowerCase().contains("concert")){
            return BitmapFactory.decodeResource(this.getResources(), R.drawable.stage);
        }else if (title.toLowerCase().contains("art")){
            int i = Tool.randomNumberGenerator(100);
            if (i % 2 == 0){
                return BitmapFactory.decodeResource(this.getResources(), R.drawable.art);
            } else {
                return BitmapFactory.decodeResource(this.getResources(), R.drawable.painter);
            }
        } else if (title.toLowerCase().contains("fountain")){
            return BitmapFactory.decodeResource(this.getResources(), R.drawable.fountains_2);
        } else if (title.toLowerCase().contains("monument")){
            return BitmapFactory.decodeResource(this.getResources(), R.drawable.history);
        }else if (title.toLowerCase().contains("theatre")){
            return BitmapFactory.decodeResource(this.getResources(), R.drawable.theatre);
        }else if (title.toLowerCase().contains("garden")){
            return BitmapFactory.decodeResource(this.getResources(), R.drawable.park);
        }else if (title.toLowerCase().contains("facility")){
            return BitmapFactory.decodeResource(this.getResources(), R.drawable.exercise);
        }

        // maybe curtain is the default value
        return BitmapFactory.decodeResource(this.getResources(), R.drawable.curtain);
    }
}
