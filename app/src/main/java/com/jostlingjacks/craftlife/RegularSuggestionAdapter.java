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
 * This class is have a adapter for the recycler view for the FragmentRegularHistory's recycler view
 **/

public class RegularSuggestionAdapter extends RecyclerView.Adapter<RegularSuggestionAdapter.SuggestionViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public RegularSuggestionAdapter(Context context, Cursor cursor){
        mContext = context;
        mCursor = cursor;
    }

    public class SuggestionViewHolder extends RecyclerView.ViewHolder{

        TextView titleText;
        TextView detailsText;
        ImageView iconImg;
        LinearLayout linearLayout;

        public SuggestionViewHolder( View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleList);
            detailsText = itemView.findViewById(R.id.detailsList);
            iconImg = itemView.findViewById(R.id.historyIcon);
            linearLayout = itemView.findViewById(R.id.LinearReg);
        }
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.regularlistitem, viewGroup, false);

        return new SuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder suggestionViewHolder, int i) {

        final String title = mCursor.getString(mCursor.getColumnIndex(DataBaseHelper.T2_COL_4));
        final String detail = mCursor.getString(mCursor.getColumnIndex(DataBaseHelper.T2_COL_5));

        if (title != null){
            suggestionViewHolder.titleText.setText(title);
            suggestionViewHolder.detailsText.setText(detail);

            if (title.toLowerCase().contains("water"))
            {
                suggestionViewHolder.iconImg.setImageResource(R.drawable.drinkwater);
            } else if (title.toLowerCase().contains("walk")){
                suggestionViewHolder.iconImg.setImageResource(R.drawable.walking);
            } else if (title.toLowerCase().contains("stand up")){
                suggestionViewHolder.iconImg.setImageResource(R.drawable.coach);
            }else if (title.toLowerCase().contains("meditation")){
                suggestionViewHolder.iconImg.setImageResource(R.drawable.meditation);
            } else if (title.toLowerCase().contains("eyes")){
                suggestionViewHolder.iconImg.setImageResource(R.drawable.curtain);
            }
        }

        suggestionViewHolder.linearLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent notificationDetail = new Intent(mContext,NotificationRegularDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("title",title);
                bundle.putString("description",detail);
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
