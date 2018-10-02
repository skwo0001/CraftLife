package com.jostlingjacks.craftlife;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import static android.content.Context.MODE_PRIVATE;

public class FragmentStat extends Fragment {

    View view;
    Context context;
    TabLayout tabLayout;
    DataBaseHelper db;
    private FragmentPieChart fragmentPieChart;
    private FragmentBarChart fragmentBarChart;
    private FragmentMapCollection fragmentMapCollection;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_stat, container, false);
        context = view.getContext();
        db = new DataBaseHelper(context);
        SharedPreferences userInfoSharedPreferences = this.getActivity().getSharedPreferences("REGISTER_PREFERENCES", MODE_PRIVATE);
        String emailAddress = userInfoSharedPreferences.getString("UserEmailAddress", "");

        fragmentPieChart = new FragmentPieChart();
        fragmentBarChart = new FragmentBarChart();
        fragmentMapCollection = new FragmentMapCollection();

        replaceFragment(fragmentPieChart);

        tabLayout = (TabLayout) view.findViewById(R.id.tabHistory);
        tabLayout.addTab(tabLayout.newTab().setText("Sort By Type"), true);
        tabLayout.addTab(tabLayout.newTab().setText("Sort By Response"));
        tabLayout.addTab(tabLayout.newTab().setText("Place Collection"));

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

    private void setCurrentTabFragment(int tabPosition) {
        switch (tabPosition) {
            case 0:
                replaceFragment(fragmentPieChart);
                break;
            case 1:
                replaceFragment(fragmentBarChart);
                break;
            case 2:
                replaceFragment(fragmentMapCollection);
                break;
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame2_container, fragment);
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
                Fragment fragment = new FragmentStat();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }
}


