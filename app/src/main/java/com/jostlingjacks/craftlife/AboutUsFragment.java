package com.jostlingjacks.craftlife;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AboutUsFragment extends Fragment {

    View vAbtUs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vAbtUs = inflater.inflate(R.layout.fragment_aboutus, container, false);
        return vAbtUs;
    }
}
