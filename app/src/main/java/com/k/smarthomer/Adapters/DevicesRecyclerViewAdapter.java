package com.k.smarthomer.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.k.smarthomer.Activities.DeviceActivity;
import com.k.smarthomer.Models.Devices.Device;
import com.k.smarthomer.R;

import java.util.ArrayList;

public class DevicesRecyclerViewAdapter extends RecyclerView.Adapter<DevicesRecyclerViewAdapter.CardViewHolder> {
    private static ArrayList<Device> data;

    public ArrayList<Device> getData() {
        return data;
    }


    public void setData(ArrayList<Device> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView, detailsTextView;
        private ImageView imageView;
        CardView cardView;

        public CardViewHolder(View itemView) {
            super(itemView);
            this.titleTextView = itemView.findViewById(R.id.titleTextViewCardRoom);
            this.detailsTextView = itemView.findViewById(R.id.detailsTextViewRoom);
            this.imageView = itemView.findViewById(R.id.cardImageViewRoom);
            this.cardView = itemView.findViewById(R.id.cardViewRoom);

            cardView.setOnClickListener(view -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Device device = data.get(pos);
                    DeviceActivity.showDialog(device, itemView.getContext());
                }
            });

            cardView.setOnLongClickListener(view -> {
                DeviceActivity.performLongClick(data.get(getAdapterPosition()), cardView.getContext(), getAdapterPosition());
                return false;
            });
        }
    }

    @NonNull
    @Override
    public DevicesRecyclerViewAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_card, parent, false);
        return new DevicesRecyclerViewAdapter.CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DevicesRecyclerViewAdapter.CardViewHolder holder, int position) {
        TextView titleTextView = holder.titleTextView;
        TextView detailsTextView = holder.detailsTextView;
        ImageView imageView = holder.imageView;

        Device device = data.get(position);

        switch (device.getDeviceType()) {
            case Device.AIR_CONDITIONER:
                imageView.setBackgroundResource(R.drawable.air_conditioner);
                break;
            case Device.BOILER:
                imageView.setBackgroundResource(R.drawable.kettle);
                break;
            case Device.FAN:
                imageView.setBackgroundResource(R.drawable.fan);
                break;
            case Device.SWITCH:
                imageView.setImageResource(R.drawable.plug);
                break;
            case Device.TEMPERATURE_SENSOR:
                imageView.setImageResource(R.drawable.temperature);
                break;
        }

        titleTextView.setText(device.getDeviceName());
        String details = device.getConnectionState() == Device.CONNECTION_OK ? "Connected" : "Disconnected";
        detailsTextView.setText(details);
    }

    @Override
    public int getItemCount() {
        if (data == null) return 0;
        else return data.size();
    }
}
