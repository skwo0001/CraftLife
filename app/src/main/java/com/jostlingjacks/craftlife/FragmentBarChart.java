package com.jostlingjacks.craftlife;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.common.collect.ArrayTable;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * This class is to show the BarChart fragment in the Stat page
 */

public class FragmentBarChart extends Fragment {

    View view;
    Context context;
    BarChart barChart;
    DataBaseHelper db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmentbarchart, container, false);
        context = view.getContext();
        db = new DataBaseHelper(context);

        barChart = (BarChart) view.findViewById(R.id.barchart);

        Spinner spinner = view.findViewById(R.id.spinnerbar);

        // get the email of the login user
        SharedPreferences userInfoSharedPreferences = this.getActivity().getSharedPreferences("CURRENT_USER_INFO", MODE_PRIVATE);
        final String emailAddress = userInfoSharedPreferences.getString("CURRENT_USER_EMAIL", "");

        //set up the barchart
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.bar_chart_spinner, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String text = parent.getItemAtPosition(position).toString();
                ArrayList<BarEntry> barEntry = new ArrayList<>();
                switch (text) {
                    case "The suggestion you liked":
                        float artLike = getNumberofOption(emailAddress, "Art", "1");
                        barEntry.add(new BarEntry(0, artLike));
                        float fountainLike = getNumberofOption(emailAddress, "Fountain", "1");
                        barEntry.add(new BarEntry(1, fountainLike));
                        float gardenLike = getNumberofOption(emailAddress, "Garden", "1");
                        barEntry.add(new BarEntry(2, gardenLike));
                        float galleryLike = getNumberofOption(emailAddress, "Gallery", "1");
                        barEntry.add(new BarEntry(3, galleryLike));
                        float monumentLike = getNumberofOption(emailAddress, "Monument", "1");
                        barEntry.add(new BarEntry(4, monumentLike));
                        float sportLike = getNumberofOption(emailAddress, "Facility", "1");
                        barEntry.add(new BarEntry(5, sportLike));
                        float theatreLike = getNumberofOption(emailAddress, "Theatre", "1");
                        barEntry.add(new BarEntry(6, theatreLike));

                        makeBarChart(barEntry);

                        break;
                    case "The suggestion you disliked":

                        float artDislike = getNumberofOption(emailAddress, "Art", "0");
                        barEntry.add(new BarEntry(0, artDislike));
                        float fountainDislike = getNumberofOption(emailAddress, "Fountain", "0");
                        barEntry.add(new BarEntry(1, fountainDislike));
                        float gardenDislike = getNumberofOption(emailAddress, "Garden", "0");
                        barEntry.add(new BarEntry(2, gardenDislike));
                        float galleryDislike = getNumberofOption(emailAddress, "Gallery", "0");
                        barEntry.add(new BarEntry(3, galleryDislike));
                        float monumentDislike = getNumberofOption(emailAddress, "Monument", "0");
                        barEntry.add(new BarEntry(4, monumentDislike));
                        float sportDislike = getNumberofOption(emailAddress, "Facility", "0");
                        barEntry.add(new BarEntry(5, sportDislike));
                        float theatreDislike = getNumberofOption(emailAddress, "Theatre", "0");
                        barEntry.add(new BarEntry(6, theatreDislike));

                        makeBarChart(barEntry);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }

    public void makeBarChart(ArrayList<BarEntry> barEntries) {

        BarDataSet barDataSet = new BarDataSet(barEntries, "Type of suggestion");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        String[] labels = new String[]{"Art","Fountain","Garden","Gallery","Monument","Sport","Theatre"};


        BarData data = new BarData(barDataSet);
        data.setBarWidth(0.6f);

        barChart.setData(data);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new XAisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);

        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.getDescription().setEnabled(false);

        barChart.invalidate();
    }

    //In here, it use the SQLite the access the user's data
    //It is able to connect to the api using Async Task
    public int getNumberofOption(String email, String title, String option) {
        Cursor cursor = db.getTypeByOption(email, title, option);
        return cursor.getCount();
    }

    public class XAisValueFormatter implements IAxisValueFormatter{
        private  String[] mValues;
        public XAisValueFormatter(String[] values){
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int)value];
        }
    }



}


