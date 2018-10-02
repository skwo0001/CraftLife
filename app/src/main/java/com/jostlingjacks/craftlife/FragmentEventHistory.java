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

public class FragmentEventHistory extends Fragment {
    View view;
    Context context;
    RecyclerView recyclerView;
    TextView msg;
    private DataBaseHelper db;
    private EventSuggestionAdapter suggestionAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_event_history, null);
        context = view.getContext();

        recyclerView = (RecyclerView) view.findViewById(R.id.eventRV);
        msg = (TextView) view.findViewById(R.id.showEventMsg);
        msg.setVisibility(View.GONE);

        SharedPreferences userInfoSharedPreferences = this.getActivity().getSharedPreferences("REGISTER_PREFERENCES", MODE_PRIVATE);
        String emailAddress = userInfoSharedPreferences.getString("UserEmailAddress", "");

        db = new DataBaseHelper(context);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        Cursor cursor = getAllLocation(emailAddress, "event");

        int i = cursor.getCount();

        if (cursor.getCount() == 0){
            msg.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        else {
            suggestionAdapter = new EventSuggestionAdapter(context, getAllLocation(emailAddress, "event"));

            recyclerView.setAdapter(suggestionAdapter);

        }
        return view;
    }

    public Cursor getAllLocation(String email, String type){
        return db.getSuggestions(email,type);
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView = (RecyclerView) view.findViewById(R.id.eventRV);
        msg = (TextView) view.findViewById(R.id.showEventMsg);
        msg.setVisibility(View.GONE);

        SharedPreferences userInfoSharedPreferences = this.getActivity().getSharedPreferences("REGISTER_PREFERENCES", MODE_PRIVATE);
        String emailAddress = userInfoSharedPreferences.getString("UserEmailAddress", "");

        db = new DataBaseHelper(context);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        Cursor cursor = getAllLocation(emailAddress, "event");

        int i = cursor.getCount();

        if (cursor.getCount() == 0){
            msg.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        else {
            suggestionAdapter = new EventSuggestionAdapter(context, getAllLocation(emailAddress, "event"));

            recyclerView.setAdapter(suggestionAdapter);

        }
    }
}


