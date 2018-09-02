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

public class SettingsFragment extends Fragment {
    private View view;
    private Context context;
    private Button startButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_settings, container,false);
        context = view.getContext();

        startButton = (Button) view.findViewById(R.id.startSettingActivityButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(context, SettingsActivity.class);
                startActivity(startIntent);
            }
        });


        return view;

    }
}
