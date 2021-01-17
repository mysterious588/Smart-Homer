package com.k.smarthomer.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.k.smarthomer.Activities.MainActivity;
import com.k.smarthomer.Models.Home;
import com.k.smarthomer.R;

import java.util.ArrayList;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.CardViewHolder> {
    public static final int RANDOM_TRANS_ID = 58;
    private static ArrayList<Home> data;

    private static final int TEXT_ID_RANDOM = 5445;

    private static final String TAG = "Home Recycler View";

    public ArrayList<Home> getData() {
        return data;
    }

    public void setData(ArrayList<Home> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView, detailsTextView;
        private ImageView imageView;
        private CardView mCardView;


        public CardViewHolder(View itemView) {
            super(itemView);
            this.titleTextView = itemView.findViewById(R.id.titleTextViewCard);
            this.detailsTextView = itemView.findViewById(R.id.detailsTextView);
            this.imageView = itemView.findViewById(R.id.cardImageView);
            this.mCardView = itemView.findViewById(R.id.card_view);

            mCardView.setOnClickListener(view -> {
                int pos = CardViewHolder.this.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Log.d(TAG, "performing click...");
                    Home home = data.get(pos);
                    MainActivity.performClick(home, itemView.getContext(), imageView.getId(), titleTextView.getId());
                } else {
                    Log.d(TAG, "clicked nothing");
                }
            });

            mCardView.setOnLongClickListener(view -> {
                MainActivity.performLongClick(mCardView.getContext(), data.get(CardViewHolder.this.getAdapterPosition()));
                return false;
            });
        }
    }

    @NonNull
    @Override
    public HomeRecyclerViewAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeRecyclerViewAdapter.CardViewHolder holder, int position) {
        TextView titleTextView = holder.titleTextView;
        TextView detailsTextView = holder.detailsTextView;
        ImageView imageView = holder.imageView;

        Home home = data.get(position);

        titleTextView.setText(home.getHomeName());
        imageView.setImageResource(R.drawable.home_icon);
        try {
            detailsTextView.setText(home.getRooms().size() + " Rooms");
        }catch (Exception NullPointerException){
            detailsTextView.setText("No Rooms");
        }

        imageView.setId(position);
        titleTextView.setId(position + TEXT_ID_RANDOM);
    }

    @Override
    public int getItemCount() {
        if (data == null) return 0;
        else return data.size();
    }
}
