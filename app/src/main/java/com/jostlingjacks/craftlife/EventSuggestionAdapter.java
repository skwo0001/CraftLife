package com.jostlingjacks.craftlife;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EventSuggestionAdapter extends RecyclerView.Adapter<EventSuggestionAdapter.SuggestionViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public EventSuggestionAdapter(Context context, Cursor cursor){
        mContext = context;
        mCursor = cursor;

    }

    public class SuggestionViewHolder extends RecyclerView.ViewHolder{

        public TextView titleText, timeText, urlText;
        public ImageView choiceImg;
        LinearLayout linearLayout;

        public SuggestionViewHolder( View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleEvent);
            timeText = itemView.findViewById(R.id.timeList);
            urlText = itemView.findViewById(R.id.urlList);
            choiceImg = itemView.findViewById(R.id.choiceEvent);
            linearLayout = itemView.findViewById(R.id.LinearEvent);
        }
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.eventlistitem, viewGroup, false);

        return new SuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder suggestionViewHolder, int i) {

        final String notificationid = mCursor.getString(mCursor.getColumnIndex(DataBaseHelper.T2_COL_1));
        final String title = mCursor.getString(mCursor.getColumnIndex(DataBaseHelper.T2_COL_4));
        final String detail = mCursor.getString(mCursor.getColumnIndex(DataBaseHelper.T2_COL_5));
        final String address = mCursor.getString(mCursor.getColumnIndex(DataBaseHelper.T2_COL_6));
        final String time = mCursor.getString(mCursor.getColumnIndex(DataBaseHelper.T2_COL_7));
        final String type = mCursor.getString(mCursor.getColumnIndex(DataBaseHelper.T2_COL_3));
        String respond = mCursor.getString(mCursor.getColumnIndex(DataBaseHelper.T2_COL_9));
        final String url = mCursor.getString(mCursor.getColumnIndex(DataBaseHelper.T2_COL_13));

        suggestionViewHolder.titleText.setText(title);
        suggestionViewHolder.timeText.setText(time);
        suggestionViewHolder.urlText.setText(url);

        if (respond == null){
            suggestionViewHolder.choiceImg.setImageResource(R.drawable.null12);
        } else if (respond.contains("0")){
            suggestionViewHolder.choiceImg.setImageResource(R.drawable.dislike);
        } else if (respond.contains("1")){
            suggestionViewHolder.choiceImg.setImageResource(R.drawable.good);
        }

        suggestionViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notificationDetail = new Intent(mContext,NotificationDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type",type);
                bundle.putString("id",notificationid);
                bundle.putString("title",title);
                bundle.putString("description",detail);
                bundle.putString("address",address);
                bundle.putString("time",time);
                bundle.putString("url",url);
                notificationDetail.putExtras(bundle);
                mContext.startActivity(notificationDetail);
            }
        });
        if (!mCursor.moveToNext()) {
            return;
        }
    }


    @Override
    public int getItemCount() {

        int i = mCursor.getCount();
        if (mCursor.getCount() < 5) {
            return mCursor.getCount();
        } else {
            return 5;
        }
    }


}
