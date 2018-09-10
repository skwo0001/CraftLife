package com.jostlingjacks.craftlife;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class SettingFragment extends Fragment{
    View vSet;
    Context context;
    LinearLayout regularCard, artLocation, eventCard;
    Button startSetting;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vSet = inflater.inflate(R.layout.setting_test, container,false);
        regularCard = (LinearLayout) vSet.findViewById(R.id.linearLayout);
        artLocation = (LinearLayout) vSet.findViewById(R.id.linearLayout2);
        eventCard = (LinearLayout) vSet.findViewById(R.id.linearLayout3);

        startSetting = (Button) vSet.findViewById(R.id.button);

        regularCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(context, SettingsActivity.class);
                startActivity(startIntent);
            }
        });

        artLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(context, SettingsActivity.class);
                startActivity(startIntent);
            }
        });

        eventCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(context, SettingsActivity.class);
                startActivity(startIntent);
            }
        });

        startSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(context, SettingsActivity.class);
                startActivity(startIntent);
            }
        });
        return vSet;
    }




}
