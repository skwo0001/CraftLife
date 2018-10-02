package com.jostlingjacks.craftlife;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class FragmentPieChart extends Fragment {

    View view;
    Context context;
    PieChart pieChart;
    DataBaseHelper db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmentpiechart, container, false);
        context = view.getContext();
        db = new DataBaseHelper(context);

        SharedPreferences userInfoSharedPreferences = this.getActivity().getSharedPreferences("REGISTER_PREFERENCES", MODE_PRIVATE);
        final String emailAddress = userInfoSharedPreferences.getString("UserEmailAddress", "");

        pieChart = (PieChart)view.findViewById(R.id.piechart);
        Spinner spinner = view.findViewById(R.id.spinnerpie);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.pie_chart_spinner,R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String text = parent.getItemAtPosition(position).toString();
                int like, dislike, nothing;
                switch (text){
                    case "Overall":
                        like = getAllNumberofOption(emailAddress,"1");
                        dislike = getAllNumberofOption(emailAddress,"0");
                        nothing = getAllNull(emailAddress);

                        createChart(text,like,dislike,nothing);
                        break;
                    case "Art":
                        like = getNumberofOption(emailAddress,text,"1");
                        dislike = getNumberofOption(emailAddress,text,"0");
                        nothing = getNumberofNull(emailAddress,text);

                        createChart(text,like,dislike,nothing);
                        break;
                    case "Monument":
                        like = getNumberofOption(emailAddress,text,"1");
                        dislike = getNumberofOption(emailAddress,text,"0");
                        nothing = getNumberofNull(emailAddress,text);

                        createChart(text,like,dislike,nothing);
                        break;
                    case "Theatre":
                        like = getNumberofOption(emailAddress,text,"1");
                        dislike = getNumberofOption(emailAddress,text,"0");
                        nothing = getNumberofNull(emailAddress,text);

                        createChart(text,like,dislike,nothing);
                        break;
                    case "Gallery":
                        like = getNumberofOption(emailAddress,text,"1");
                        dislike = getNumberofOption(emailAddress,text,"0");
                        nothing = getNumberofNull(emailAddress,text);

                        createChart(text,like,dislike,nothing);
                        break;
                    case "Sport":
                        like = getNumberofOption(emailAddress,"Facility","1");
                        dislike = getNumberofOption(emailAddress,"Facility","0");
                        nothing = getNumberofNull(emailAddress,"Facility");

                        createChart(text,like,dislike,nothing);
                        break;
                    case "Fountain":
                        like = getNumberofOption(emailAddress,text,"1");
                        dislike = getNumberofOption(emailAddress,text,"0");
                        nothing = getNumberofNull(emailAddress,text);

                        createChart(text,like,dislike,nothing);
                        break;
                    case "Garden":
                        like = getNumberofOption(emailAddress,text,"1");
                        dislike = getNumberofOption(emailAddress,text,"0");
                        nothing = getNumberofNull(emailAddress,text);

                        createChart(text,like,dislike,nothing);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    private void createChart(String item, int like, int dislike, int nothing) {

        pieChart.setCenterText(item);
        pieChart.setCenterTextSize(22);
        pieChart.setRotationEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);

        ArrayList<PieEntry> yValues = new ArrayList<>();
        yValues.add(new PieEntry(like,"Like"));
        yValues.add(new PieEntry(dislike,"Dislike"));
        yValues.add(new PieEntry(nothing,"No response"));


        PieDataSet dataSet = new PieDataSet(yValues,"");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData data = new PieData(dataSet);
        dataSet.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(15f);
        dataSet.setValueTextColor(Color.YELLOW);
        pieChart.setData(data);
        pieChart.invalidate();
    }

    public int getNumberofOption(String email, String title, String option){
        Cursor cursor = db.getTypeByOption(email,title,option);
        return cursor.getCount();
    }

    public int getAllNumberofOption(String email,String option){
        Cursor cursor = db.getAllByOption(email,option);
        return cursor.getCount();
    }

    public int getAllNull(String email){
        Cursor cursor = db.getAllNull(email);
        return cursor.getCount();
    }

    public int getNumberofNull(String email, String title){
        Cursor cursor = db.getTypeNull(email,title);
        return cursor.getCount();
    }


}
