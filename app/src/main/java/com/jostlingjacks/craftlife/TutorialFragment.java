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

public class TutorialFragment extends Fragment {
    private View vTut;
    private Context context;
    private Button start;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        vTut = inflater.inflate(R.layout.fragment_tutorial, container,false);
        context = vTut.getContext();

        start = (Button) vTut.findViewById(R.id.startActivity);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent start = new Intent(context, TutorialActivity.class);
                startActivity(start);
            }
        });


        return vTut;

    }
}
