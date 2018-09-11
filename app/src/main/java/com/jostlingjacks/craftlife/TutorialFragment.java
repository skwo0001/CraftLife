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
import android.widget.Button;

public class TutorialFragment extends Fragment {
    private View vTut;
    private Context context;
    private Button start;
    private ConstraintLayout tut_video, tut_intro;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        vTut = inflater.inflate(R.layout.fragment_tutorial, container,false);
        context = vTut.getContext();

        tut_video = (ConstraintLayout) vTut.findViewById(R.id.startVideo);
        tut_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent start = new Intent(context, VideoActivity.class);
                startActivity(start);
            }
        });

        tut_intro = (ConstraintLayout) vTut.findViewById(R.id.startIntro);
        tut_intro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent start = new Intent(context, TutorialActivity.class);
                startActivity(start);
            }
        });

        return vTut;

    }
}
