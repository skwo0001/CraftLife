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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

import static com.jostlingjacks.craftlife.Channel.CHANNEL_ID_3;
import static com.jostlingjacks.craftlife.Channel.CHANNEL_ID_1;
import static com.jostlingjacks.craftlife.Channel.CHANNEL_ID_2;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

public class SendRequest extends JobService {
    private static final String TAG = "JobService";
    private boolean jobCancelled = false;

    private  NotificationManager notificationManager,notificationManager2,notificationManager3;
    private LocationManager locationManager;
    private DataBaseHelper db;

    //Start the job
    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(JobParameters params) {
        db = new DataBaseHelper(this);
        final String request = params.getExtras().getString("request");
        try {
            Log.d("jsonObject",getLastLocation().toString());

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
                    jsonObject = getLastLocation();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String jsonString = null;

                if (request.contains("regular")){
                    jsonString = HTTPDataHandler.getRegularNotification();
                } else if (request.contains("art location")){
                    jsonString = HTTPDataHandler.getLocationNotification(jsonObject);
                } else {
                    //for event
                    jsonString = HTTPDataHandler.getEventNotification(jsonObject);
                }

                if (jsonString != "") {
                    try {
                        //To get the reply from the request and make it to JSONObject
                        jsonReply = new JSONObject(jsonString.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                return jsonReply ;
            }
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                if (jsonObject != null) {
                    createNotification(jsonObject);
                    Log.d("jsonObject", jsonObject.toString());
                }

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

        String type= null, title= null, description= null, lat= null, lon= null, address= null, time = null, url = null, subtype = null;
        int notification_id;

        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID_1, "Regular Notification", NotificationManager.IMPORTANCE_HIGH);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);

        NotificationChannel notificationChannel2 = new NotificationChannel(CHANNEL_ID_2, "Art Location Notification", NotificationManager.IMPORTANCE_HIGH);
        notificationManager2 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager2.createNotificationChannel(notificationChannel2);

        NotificationChannel notificationChannel3 = new NotificationChannel(CHANNEL_ID_3, "Event Notification", NotificationManager.IMPORTANCE_HIGH);
        notificationManager3 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager3.createNotificationChannel(notificationChannel3);



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

        db.addSuggestion(type,title,description,address,time,emailAddress,formatedate,null,lat,lon,subtype,url);
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
            notification_id = 2;
        }else //add notification_id 3 for events
            notification_id = 3;

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

        } else if (type.contains("event")){
            notification = new NotificationCompat.Builder(this, CHANNEL_ID_3)
                    .setContentIntent(resultPendingIntent)
                    .setSmallIcon(R.drawable.app_logo)
                    .setContentTitle(title)
                    .setContentText("When : " + time)
                    .addAction(R.drawable.ic_yes, "Okay, I'll go", yesPendingIntent)
                    .addAction(R.drawable.ic_no, "show me less", noPendingIntent)
                    .addAction(R.drawable.ic_no, "Add To To-do List", addToToDoListPendingIntent)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.app_logo))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setColor(16757760)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(true)
                    .build();

            notificationManager3.notify(notification_id, notification);
        }

    }

    /**
     * this method appears twices!!
     * @param title this is the title of the notification
     * @return returns the bitmap and used by the setLargeIcon() by Notification Builder...
     */
    private Bitmap resolveNotificationIcon(String title){
        if (title.toLowerCase().contains("water"))
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