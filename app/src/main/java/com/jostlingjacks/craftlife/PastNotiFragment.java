package com.jostlingjacks.craftlife;


import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import static android.content.Context.MODE_PRIVATE;

public class PastNotiFragment extends Fragment {

    View view;
    Context context;
    TabLayout tabLayout;
    DataBaseHelper db;
    private FragmentRegularHistory fragmentRegularHistory;
    private FragmentLocationHistory fragmentLocationHistory;
    private FragmentEventHistory fragmentEventHistory;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pastnoti, container,false);
        context = view.getContext();
        db = new DataBaseHelper(context);

        fragmentRegularHistory = new FragmentRegularHistory();
        fragmentLocationHistory = new FragmentLocationHistory();
        fragmentEventHistory = new FragmentEventHistory();
        replaceFragment(fragmentRegularHistory);

        tabLayout = (TabLayout) view.findViewById(R.id.tabHistory);
        tabLayout.addTab(tabLayout.newTab().setText("Daily"),true);
        tabLayout.addTab(tabLayout.newTab().setText("Art Location"));
        tabLayout.addTab(tabLayout.newTab().setText("Event"));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setCurrentTabFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        setHasOptionsMenu(true);
        return view;
    }

    private void setCurrentTabFragment(int tabPosition){
        switch (tabPosition)
        {
            case 0:
                replaceFragment(fragmentRegularHistory);
                break;
            case 1:
                replaceFragment(fragmentLocationHistory);
                break;
            case 2:
                replaceFragment(fragmentEventHistory);
                break;
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        getActivity().getMenuInflater().inflate(R.menu.refresh_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                Fragment fragment = new PastNotiFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame,fragment).commit();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }



}