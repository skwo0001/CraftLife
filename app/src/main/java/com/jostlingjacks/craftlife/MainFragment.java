package com.jostlingjacks.craftlife;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainFragment extends Fragment {

    View vHome;
    Context context;
    private ConstraintLayout regularHome ,artLocationHome, eventHome;
    private TextView reg_detail, location_detail, event_detail;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        vHome = inflater.inflate(R.layout.fragment_home, container, false);
        context = vHome.getContext();
        /*regularHome = (ConstraintLayout) vHome.findViewById(R.id.regular_home);
        reg_detail = (TextView) vHome.findViewById(R.id.regular_home);
        location_detail = (TextView) vHome.findViewById(R.id.location_home);
        event_detail = (TextView) vHome.findViewById(R.id.event_home);

        regularHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notificationDetail = new Intent(context,NotificationDetailActivity.class);
                startActivity(notificationDetail);

            }
        });
        artLocationHome = (ConstraintLayout) vHome.findViewById(R.id.location_home);
        artLocationHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notificationDetail = new Intent(context,NotificationDetailActivity.class);
                startActivity(notificationDetail);
            }
        });
        eventHome = (ConstraintLayout) vHome.findViewById(R.id.event_home);
        eventHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notificationDetail = new Intent(context,NotificationDetailActivity.class);
                startActivity(notificationDetail);
            }
        });*/








        return vHome;
    }

}
