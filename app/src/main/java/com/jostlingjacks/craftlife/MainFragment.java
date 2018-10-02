package com.jostlingjacks.craftlife;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteTransactionListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends Fragment implements SQLiteTransactionListener {

    View vHome;
    Context context;
    private ConstraintLayout regularHome ,artLocationHome, eventHome;
    private TextView reg_detail, location_detail, event_detail;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DataBaseHelper db;

    @SuppressLint("StaticFieldLeak")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        vHome = inflater.inflate(R.layout.fragment_home, container, false);
        context = vHome.getContext();
        db = new DataBaseHelper(context);


        reg_detail = (TextView) vHome.findViewById(R.id.regular_detail);
        location_detail = (TextView) vHome.findViewById(R.id.location_detail);
        event_detail = (TextView) vHome.findViewById(R.id.event_detail);

        regularHome = (ConstraintLayout) vHome.findViewById(R.id.regular_home);
        artLocationHome = (ConstraintLayout) vHome.findViewById(R.id.location_home);
        eventHome = (ConstraintLayout) vHome.findViewById(R.id.event_home);

        //get the email from shared preference
        SharedPreferences userInfoSharedPreferences = this.getActivity().getSharedPreferences("CURRENT_USER_INFO", MODE_PRIVATE);
        String emailAddress = userInfoSharedPreferences.getString("CURRENT_USER_EMAIL", "");
        final String token = userInfoSharedPreferences.getString(emailAddress+"AuthToken", "");
        new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Void... params) {
                JSONObject jsonReply = null;
                String jsonString = null;
                jsonString = HTTPDataHandler.getHistories(token);
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
                    Log.d("jsonObject", jsonObject.toString());
                }

            }
        }.execute();

        String regResult = showRegular(readRecentData(emailAddress,"regular"));
        if (regResult != "")
        {
            reg_detail.setText(regResult);
        }
        // get the result to show the latest notification
        String regular = readRecentData(emailAddress, "regular");
        if (regular != ""){
            String[] result = regular.split("#");
            final String title = result[0];
            final String details = result[1];
            final String address = result[2];
            final String time = result[3];

            regularHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent notificationDetail = new Intent(context,NotificationRegularDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("title",title);
                    bundle.putString("description",details);
                    bundle.putString("address",address);
                    bundle.putString("time",time);
                    notificationDetail.putExtras(bundle);
                    startActivity(notificationDetail);
                }
            });
        }

        String locationResult = showRegular(readRecentData(emailAddress,"location"));
        if (locationResult != "")
        {
            location_detail.setText(locationResult);
        }
        String location = readRecentData(emailAddress, "location");
        if (location != ""){
            String[] result = location.split("#");
            final String title = result[0];
            final String details = result[1];
            final String address = result[2];
            final String time = result[3];
            final String url = result[4];

            artLocationHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent notificationDetail = new Intent(context,NotificationDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("title",title);
                    bundle.putString("description",details);
                    bundle.putString("address",address);
                    bundle.putString("time",time);
                    bundle.putString("url",url);
                    notificationDetail.putExtras(bundle);
                    startActivity(notificationDetail);

                }
            });
        }


        String eventResult = showRegular(readRecentData(emailAddress,"event"));
        if (eventResult != "")
        {
            event_detail.setText(eventResult);
        }
        String event = readRecentData(emailAddress, "event");
        if (event != ""){
            String[] result = event.split("#");
            final String title = result[0];
            final String details = result[1];
            final String address = result[2];
            final String time = result[3];
            final String url = result[4];

            eventHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent notificationDetail = new Intent(context,NotificationDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("title",title);
                    bundle.putString("description",details);
                    bundle.putString("address",address);
                    bundle.putString("time",time);
                    bundle.putString("url",url);
                    notificationDetail.putExtras(bundle);
                    startActivity(notificationDetail);

                }
            });
        }
        setHasOptionsMenu(true);
        return vHome;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        getActivity().getMenuInflater().inflate(R.menu.refresh_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                Fragment fragment = new MainFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame,fragment).commit();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    public String readRecentData(String email, String type){
        Cursor c = db.getRecentRegularSuggestion(email,type);
        String s = "";
        if (c.moveToLast()){
            //return title,details,address and time
            s = ( c.getString(0))+ "#" + c.getString(1)+ "#" + c.getString(2)+ "#" + c.getString(3)+ "#" +c.getString(4);
        }

        return  s;
    }

    public String showRegular(String s) {

        String show = "";
        if (s != "")
        {
            String[] result = s.split("#");
            String title = result[0];
            show = "Type: " + title ;
        }else{
            show = s;
        }
            return show;
    }

    @Override
    public void onBegin() {
        reg_detail.setText("it is been updated");
        location_detail.setText("Updated");
        event_detail.setText("Updated");
    }

    @Override
    public void onCommit() {
        reg_detail.setText("it is been updated");
        location_detail.setText("Updated");
        event_detail.setText("Updated");
    }

    @Override
    public void onRollback() {
        Log.e("MainFragment", "Error");
    }









}
