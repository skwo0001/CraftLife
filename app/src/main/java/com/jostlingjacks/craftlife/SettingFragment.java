package com.jostlingjacks.craftlife;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import java.util.prefs.Preferences;

public class SettingFragment extends PreferenceFragment {

    private View view;
    private Context context;
    private Button startButton;
    private ConstraintLayout regularCard ,artLocation, eventCard;
    private TextView reg_interval, location_interval, event_interval;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        //Find the value in shared preferences


        view = inflater.inflate(R.layout.fragment_setting, container,false);
        context = view.getContext();
        reg_interval = (TextView) view.findViewById(R.id.regular_setting);
        location_interval = (TextView) view.findViewById(R.id.location_setting);
        event_interval = (TextView) view.findViewById(R.id.event_setting);

        /**
         *  this loads the resources from pref_notification xml file...
         */
        ListView listViewPref_daily_suggestion_interval_values = (ListView) view.findViewById(R.id.pref_daily_suggestion_interval_values);
        ListView listViewPref_art_places_suggestion_interval_values = (ListView) view.findViewById(R.id.pref_art_places_suggestion_interval_values);

        startButton = (Button) view.findViewById(R.id.button_starttesting);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(context, SettingsActivity.class);
                startActivity(startIntent);
            }
        });

        /**
         * TODO: this is the daily suggestion card (fuck the mentors)
         */
        regularCard = (ConstraintLayout) view.findViewById(R.id.linearLayout);
        regularCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent startIntent = new Intent(context, SettingsActivity.class);
//                startActivity(startIntent);



                final PopupMenu popupMenu = new PopupMenu(context, regularCard);
                popupMenu.getMenuInflater().inflate(R.menu.daily_suggestion_interval, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Toast.makeText(context, "" + menuItem.getTitle(), Toast.LENGTH_LONG).show();

                        reg_interval.setText(menuItem.getTitle());

                        return true;
                    }
                });
            popupMenu.show();

            }
        });

        artLocation = (ConstraintLayout) view.findViewById(R.id.linearLayout2);
        artLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final PopupMenu popupMenu = new PopupMenu(context, artLocation);
                popupMenu.getMenuInflater().inflate(R.menu.art_location_suggestion_interval, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Toast.makeText(context, "" + menuItem.getTitle(), Toast.LENGTH_LONG).show();

                        location_interval.setText(menuItem.getTitle());

                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        eventCard = (ConstraintLayout) view.findViewById(R.id.linearLayout3);
        eventCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final PopupMenu popupMenu = new PopupMenu(context, regularCard);
                popupMenu.getMenuInflater().inflate(R.menu.event_suggestion_interval, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Toast.makeText(context, "" + menuItem.getTitle(), Toast.LENGTH_LONG).show();

                        event_interval.setText(menuItem.getTitle());
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        return view;

    }



}