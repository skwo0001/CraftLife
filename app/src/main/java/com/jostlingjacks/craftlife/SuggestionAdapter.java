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

/**
 * This class is have a adapter for the recycler view for the FragmentLocationHistory's recycler view
 **/

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private int cursorMovingTimes;
    private  boolean isCursorMoving;

    public SuggestionAdapter(Context context, Cursor cursor){
        mContext = context;
        mCursor = cursor;
        cursorMovingTimes = 0;
        isCursorMoving = false;
    }

    public class SuggestionViewHolder extends RecyclerView.ViewHolder{

        public TextView titleText, detailsText, addressText;
        public ImageView iconImg, choiceImg;
        LinearLayout linearLayout;

        public SuggestionViewHolder( View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleList);
            detailsText = itemView.findViewById(R.id.detailsList);
            addressText = itemView.findViewById(R.id.addressList);
            iconImg = itemView.findViewById(R.id.historyIcon);
            choiceImg = itemView.findViewById(R.id.choice);
            linearLayout = itemView.findViewById(R.id.LinearAdd);
        }
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.addresslistitem, viewGroup, false);

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
            suggestionViewHolder.detailsText.setText(detail);
            suggestionViewHolder.addressText.setText(address);

            if (type.toLowerCase().contains("art")){
                suggestionViewHolder.iconImg.setImageResource(R.drawable.placeholder_3);
            } else {
                suggestionViewHolder.iconImg.setImageResource(R.drawable.calendar4);
            }

            if (detail.toLowerCase().contains("gallery"))
            {
                suggestionViewHolder.iconImg.setImageResource(R.drawable.gallery);
            } else if (detail.toLowerCase().contains("concert")){
                suggestionViewHolder.iconImg.setImageResource(R.drawable.stage);
            }else if (detail.toLowerCase().contains("art")){
                suggestionViewHolder.iconImg.setImageResource(R.drawable.art);
            } else if (detail.toLowerCase().contains("fountain")){
                suggestionViewHolder.iconImg.setImageResource(R.drawable.fountains_2);
            } else if (detail.toLowerCase().contains("monument")){
                suggestionViewHolder.iconImg.setImageResource(R.drawable.history);
            }else if (detail.toLowerCase().contains("theatre")){
                suggestionViewHolder.iconImg.setImageResource(R.drawable.theatre);
            }else if (detail.toLowerCase().contains("garden")){
                suggestionViewHolder.iconImg.setImageResource(R.drawable.park);
            }else if (detail.toLowerCase().contains("facility") || detail.toLowerCase().contains("health")){
                suggestionViewHolder.iconImg.setImageResource(R.drawable.exercise);
            }

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
        if (i < 5 || i == 5) {
            cursorMovingTimes = i;
            return i;
        } else {
            cursorMovingTimes = 5;
            return 5;
        }
//        return mCursor.getCount();
    }


}
