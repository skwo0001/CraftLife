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
import android.widget.TextView;

public class SettingsFragment extends Fragment {
    private View view;
    private Context context;
    private Button startButton;
    private TextView textView18;
    private TextView textView16;
    private TextView textView15;
    private TextView textView14;
    private TextView textView13;

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

        // some textViews should not be seen right now, but will make it for use later.
        textView18 = (TextView) view.findViewById(R.id.settingFragmentTextView18);
        textView18.setVisibility(View.INVISIBLE);

        textView16 = (TextView) view.findViewById(R.id.settingFragmentTextView16);
        textView16.setVisibility(View.INVISIBLE);

        textView15 = (TextView) view.findViewById(R.id.settingFragmentTextView15);
        textView15.setVisibility(View.INVISIBLE);

        textView14 = (TextView) view.findViewById(R.id.settingFragmentTextView14);
        textView14.setVisibility(View.INVISIBLE);

        textView13 = (TextView) view.findViewById(R.id.settingFragmentTextView13);
        textView13.setVisibility(View.INVISIBLE);


        return view;

    }
}
