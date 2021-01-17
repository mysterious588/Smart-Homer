package com.k.smarthomer.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.k.smarthomer.Activities.RoomActivity;
import com.k.smarthomer.Models.Room;
import com.k.smarthomer.R;

import java.util.ArrayList;

public class RoomRecyclerViewAdapter extends RecyclerView.Adapter<RoomRecyclerViewAdapter.CardViewHolder> {
    private static ArrayList<Room> data;

    public ArrayList<Room> getData() {
        return data;
    }

    public void setData(ArrayList<Room> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView, detailsTextView;
        private ImageView imageView;
        private CardView mCardView;

        public CardViewHolder(View itemView) {
            super(itemView);
            this.titleTextView = itemView.findViewById(R.id.titleTextViewCardRoom);
            this.detailsTextView = itemView.findViewById(R.id.detailsTextViewRoom);
            this.imageView = itemView.findViewById(R.id.cardImageViewRoom);
            this.mCardView = itemView.findViewById(R.id.cardViewRoom);

            mCardView.setOnClickListener(view -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Room room = data.get(pos);
                    RoomActivity.performClick(room, itemView.getContext());
                }
            });

            mCardView.setOnLongClickListener(view -> {
                RoomActivity.performLongClick(data.get(getAdapterPosition()), mCardView.getContext());
                return false;
            });
        }
    }

    @NonNull
    @Override
    public RoomRecyclerViewAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomRecyclerViewAdapter.CardViewHolder holder, int position) {
        TextView titleTextView = holder.titleTextView;
        TextView detailsTextView = holder.detailsTextView;
        ImageView imageView = holder.imageView;

        Room room = data.get(position);

        titleTextView.setText(room.getRoomName());
        imageView.setImageResource(R.drawable.room_icon);
        try {
            detailsTextView.setText(room.getDeviceIds().size() + " Devices");
        } catch (Exception NullPointerException) {
            detailsTextView.setText("No Devices");
        }
    }

    @Override
    public int getItemCount() {
        if (data == null) return 0;
        else return data.size();
    }
}