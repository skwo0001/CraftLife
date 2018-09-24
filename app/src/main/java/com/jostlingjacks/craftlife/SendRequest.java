package com.jostlingjacks.craftlife;

//Using job service to do the background work

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;

import static com.jostlingjacks.craftlife.Channel.CHANNEL_ID;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SendRequest extends JobService {
    private static final String TAG = "JobService";
    private boolean jobCancelled = false;

    private  NotificationManager notificationManager;
    private LocationManager locationManager;
    private DataBaseHelper db;

    //Start the job
    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(JobParameters params) {
        db = new DataBaseHelper(this);
        try {
            Log.d("jsonObject", prepareCoordinatesAndTimeJSONObject(getLastLocation()).toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Send the JSON Object to the API
        new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Void... params) {
                JSONObject jsonObject = null;
                JSONObject jsonReply = null;
                try {
                    //get the jsonObject, date and location
                    jsonObject = prepareCoordinatesAndTimeJSONObject(getLastLocation());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Randomly send the request to different api
                int i = Tool.randomNumberGenerator(100);
                String jsonString = null;
                if (i % 2 == 0){
                    jsonString = HTTPDataHandler.getEventNotification(jsonObject);
                } else {
                    jsonString = HTTPDataHandler.getRegularNotification(jsonObject);
                }

                if (jsonString != "") {
                    try {
                        //To get the reply from the request and make it to JSONObject
                        jsonReply = new JSONObject(jsonString.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                return jsonReply ;}
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                createNotification(jsonObject);
                Log.d("jsonObject", jsonObject.toString());

            }
        }.execute();
        return true;
    }

    //Stop the job
    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }


    public Double[] getLastLocation() {
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

        return latitudeAndLongtidue;
    }

    private static JSONObject prepareCoordinatesAndTimeJSONObject(Double[] latitudeAndLongtidue) throws JSONException {
        //prepare json object
        JSONObject jsonObject = new JSONObject();
        //prepare time
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String formattedTime = sdf.format(calendar.getTime());
        //put time and coordinates into json object
        jsonObject.put("time", formattedTime);
        jsonObject.put("Latitude",latitudeAndLongtidue[0]);
        jsonObject.put("Longtitude", latitudeAndLongtidue[1]);

        return  jsonObject;
    }

    //Using the JSONObject to create the notification and also store the data to sqlite
    private void createNotification(JSONObject jsonObject) {

        String type= null, title= null, description= null, lat= null, lon= null, address= null, time = null;
        int notification_id;

        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Channel", NotificationManager.IMPORTANCE_HIGH);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);

        try {
            type = jsonObject.getString("type");
            title = jsonObject.getString("title");
            description = jsonObject.getString("description");
            lat = jsonObject.getString("lat");
            lon = jsonObject.getString("lon");
            address = jsonObject.getString("address");
            time = jsonObject.getString("time");
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

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf2 = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String formatedate = sdf2.format(calendar.getTime());

        SharedPreferences userInfoSharedPreferences = getSharedPreferences("REGISTER_PREFERENCES", MODE_PRIVATE);
        String emailAddress = userInfoSharedPreferences.getString("UserEmailAddress", "");

        db.addSuggestion(type, title,description,address,time,emailAddress,formatedate);

        Intent resultIntent;

        if (address != null) {
            resultIntent = new Intent(this, NotificationDetailActivity.class);
        } else {
            resultIntent = new Intent(this, NotificationRegularDetailActivity.class);
        }
        resultIntent.putExtras(bundle);


        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent yesAnswerIntent = new Intent(this, NotificationReceiver.class);
        yesAnswerIntent.putExtra("NOTIFICATION_ID_1", 1);
        PendingIntent yesPendingIntent = PendingIntent.getBroadcast(this, 1, yesAnswerIntent, PendingIntent.FLAG_ONE_SHOT);


        Intent noAnswerIntent = new Intent(this, NotificationReceiver.class);
        noAnswerIntent.putExtra("NOTIFICATION_ID_1", 1);
        PendingIntent noPendingIntent = PendingIntent.getBroadcast(this, 1, noAnswerIntent, PendingIntent.FLAG_ONE_SHOT);

        // the button of adding information to the to do list.
        Intent addToToDoListIntent = new Intent(this, NotificationReceiver.class);
        addToToDoListIntent.putExtra("addToToDoList", "Title: " + title + " \t" + "Description:  " + description + "\t Address: " +address);
        PendingIntent addToToDoListPendingIntent = PendingIntent.getBroadcast(this, 0, addToToDoListIntent, PendingIntent.FLAG_ONE_SHOT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentIntent(resultPendingIntent)
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle(title)
                .setContentText(description)
                .addAction(R.drawable.ic_yes, "Okay, I'll go", yesPendingIntent)
                .addAction(R.drawable.ic_no, "show me less", noPendingIntent)
                .addAction(R.drawable.ic_no, "Add To To-do List", addToToDoListPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                .setChannelId(CHANNEL_ID)
                .setColor(16757760)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .build();

        if (type == "Regular") {
            notification_id = 1;
        } else
            notification_id = 2;
        notificationManager.notify(notification_id, notification);
        notificationManager.cancel(notification_id);
    }
}