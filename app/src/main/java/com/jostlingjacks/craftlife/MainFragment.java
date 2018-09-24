package com.jostlingjacks.craftlife;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteTransactionListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends Fragment implements SQLiteTransactionListener {

    View vHome;
    Context context;
    private ConstraintLayout regularHome ,artLocationHome, eventHome;
    private TextView reg_detail, location_detail, event_detail;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DataBaseHelper db;

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

        //get the email from shared preference
        SharedPreferences userInfoSharedPreferences = this.getActivity().getSharedPreferences("REGISTER_PREFERENCES", MODE_PRIVATE);
        String emailAddress = userInfoSharedPreferences.getString("UserEmailAddress", "");

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

        String locationResult = showRegular(readRecentData(emailAddress,"Arts event"));
        if (locationResult != "")
        {
            location_detail.setText(locationResult);
        }
        String location = readRecentData(emailAddress, "Arts event");
        if (location != ""){
            String[] result = location.split("#");
            final String title = result[0];
            final String details = result[1];
            final String address = result[2];
            final String time = result[3];

            artLocationHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent notificationDetail = new Intent(context,NotificationDetailActivity.class);
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

//        eventHome = (ConstraintLayout) vHome.findViewById(R.id.event_home);
//        eventHome.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent notificationDetail = new Intent(context,NotificationDetailActivity.class);
//               Bundle bundle = new Bundle();
//                bundle.putString("title",title);
//               bundle.putString("description",details);
//               bundle.putString("address",address);
//               bundle.putString("time",time);
//               notificationDetail.putExtras(bundle);
//                startActivity(notificationDetail);
//            }
//        });

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
            s = ( c.getString(0))+ "#" + c.getString(1)+ "#" + c.getString(2)+ "#" + c.getString(3);
        }

        return  s;
    }

    public String showRegular(String s) {

        String show = "";
        if (s != "")
        {
            String[] result = s.split("#");
            String title = result[0];
            String details = result[1];
            String address = result[2];
            String time = result[3];

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
