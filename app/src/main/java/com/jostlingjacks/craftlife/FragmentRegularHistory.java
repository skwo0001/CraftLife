package com.jostlingjacks.craftlife;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;

public class FragmentRegularHistory extends Fragment{

    View view;
    Context context;
    RecyclerView recyclerView;
    TextView msg;
    private DataBaseHelper db;
    private RegularSuggestionAdapter regularSuggestionAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_regular_history, null);
        context = view.getContext();

        recyclerView = (RecyclerView) view.findViewById(R.id.regularRV);
        msg = (TextView) view.findViewById(R.id.showRegMsg);
        msg.setVisibility(View.GONE);

        SharedPreferences userInfoSharedPreferences = this.getActivity().getSharedPreferences("REGISTER_PREFERENCES", MODE_PRIVATE);
        String emailAddress = userInfoSharedPreferences.getString("UserEmailAddress", "");

        db = new DataBaseHelper(context);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        Cursor cursor = getAllDaily(emailAddress, "regular");

        if (cursor.getCount() == 0){
            msg.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        else {
            regularSuggestionAdapter = new RegularSuggestionAdapter(context, getAllDaily(emailAddress, "regular"));

            recyclerView.setAdapter(regularSuggestionAdapter);

        }
        return view;
    }

    public Cursor getAllDaily(String email, String type){
        return db.getSuggestions(email,type);
    }
}
