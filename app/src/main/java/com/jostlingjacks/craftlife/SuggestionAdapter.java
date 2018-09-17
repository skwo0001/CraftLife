package com.jostlingjacks.craftlife;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public SuggestionAdapter(Context context, Cursor cursor){
        mContext = context;
        mCursor = cursor;

    }

    public class SuggestionViewHolder extends RecyclerView.ViewHolder{

        public TextView titleText;
        public TextView detailsText;
        public ImageView iconImg;

        public SuggestionViewHolder( View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleList);
            detailsText = itemView.findViewById(R.id.detailsList);
            iconImg = itemView.findViewById(R.id.historyIcon);
        }
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.listitem, viewGroup, false);

        return new SuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder suggestionViewHolder, int i) {
        if (!mCursor.move(i)) {
            return;
        }

        String title = mCursor.getString(mCursor.getColumnIndex(DataBaseHelper.T2_COL_4));
        String detail = mCursor.getString(mCursor.getColumnIndex(DataBaseHelper.T2_COL_5));
        String type = mCursor.getString(mCursor.getColumnIndex(DataBaseHelper.T2_COL_3));

        suggestionViewHolder.titleText.setText(title);
        suggestionViewHolder.detailsText.setText(detail);

        if (type.toLowerCase().contains("regular")){
            suggestionViewHolder.iconImg.setImageResource(R.drawable.clock);
        } else if (type.toLowerCase().contains("art")){
            suggestionViewHolder.iconImg.setImageResource(R.drawable.placeholder_3);
        } else {
            suggestionViewHolder.iconImg.setImageResource(R.drawable.calendar4);
        }
    }


    @Override
    public int getItemCount() {

        if (mCursor.getCount() < 5) {
            return mCursor.getCount();
        } else {
            return 5;
        }
    }


}