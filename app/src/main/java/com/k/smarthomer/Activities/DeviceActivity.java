package com.k.smarthomer.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.style.CubeGrid;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.k.smarthomer.Adapters.DevicesRecyclerViewAdapter;
import com.k.smarthomer.Database.FirebaseDatabaseInstance;
import com.k.smarthomer.Models.Devices.AirConditioner;
import com.k.smarthomer.Models.Devices.Boiler;
import com.k.smarthomer.Models.Devices.Device;
import com.k.smarthomer.Models.Devices.Fan;
import com.k.smarthomer.Models.Devices.Switch;
import com.k.smarthomer.Models.Devices.TemperatureSensor;
import com.k.smarthomer.Models.Room;
import com.k.smarthomer.R;

import java.util.ArrayList;

public class DeviceActivity extends AppCompatActivity {

    private static final String TAG = "Device Activity";

    private RecyclerView mRecyclerView;
    private static DevicesRecyclerViewAdapter mDevicesRecyclerViewAdapter;
    private FloatingActionButton fab;

    private ProgressBar mProgressBar;

    private static String homeId, roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        initViews();
        initAdapter();
    }

    private void initViews() {
        fab = findViewById(R.id.fabDeviceActivity);
        fab.setOnClickListener(view -> {
            showAddDeviceDialog();
        });
        mRecyclerView = findViewById(R.id.deviceRecyclerView);
        mProgressBar = findViewById(R.id.deviceActivityProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        CubeGrid doubleBounce = new CubeGrid();
        mProgressBar.setIndeterminateDrawable(doubleBounce);
    }

    private void showToast(String message) {
        Toast.makeText(DeviceActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void showAddDeviceDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_device_dialog);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
        EditText deviceIdEditText = dialog.findViewById(R.id.deviceIdEditText);
        EditText devicePinEditText = dialog.findViewById(R.id.devicePinEditText);

        dialog.findViewById(R.id.deviceAddButton).setOnClickListener(view -> {


            String deviceId = deviceIdEditText.getText().toString();
            String devicePin = devicePinEditText.getText().toString();

            if (deviceId.isEmpty()) deviceIdEditText.setError("Must Enter Device ID");
            else if (devicePin.isEmpty()) devicePinEditText.setError("Must Enter Device PIN");
            else {
                FirebaseDatabaseInstance.getDeviceWithDeviceId(deviceId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            Log.v(TAG, snapshot.toString());
                            if (snapshot.child("devicePin").getValue(String.class).equals(devicePin)) {
                                FirebaseDatabaseInstance.getRoomWithId(homeId, roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Room room = snapshot.getValue(Room.class);
                                        ArrayList<String> deviceIds = room.getDeviceIds();
                                        boolean deviceExists = false;
                                        if (deviceIds != null) {
                                            for (String id : deviceIds) {
                                                if (id.equals(deviceId)) {
                                                    deviceExists = true;
                                                }
                                            }
                                        } else {
                                            deviceIds = new ArrayList<>();
                                        }
                                        if (!deviceExists) {
                                            deviceIds.add(deviceId);
                                            room.setDeviceIds(deviceIds);
                                            FirebaseDatabaseInstance.getRoomWithId(homeId, roomId).setValue(room).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        showToast("Device Added Successfully");
                                                        dialog.dismiss();
                                                    } else {
                                                        showToast("Adding Device Failed");
                                                    }
                                                }
                                            });
                                        } else {
                                            showToast("Device Already Exists!");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            } else {
                                showToast("Wrong PIN");
                                devicePinEditText.setError("Wrong PIN");
                            }
                        } else {
                            deviceIdEditText.setError("Wrong ID");
                            showToast("Device doesn't exist");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });
            }
        });


    }

    private void initAdapter() {
        mDevicesRecyclerViewAdapter = new DevicesRecyclerViewAdapter();

        ArrayList<Device> devices = new ArrayList<>();

        homeId = getIntent().getStringExtra(MainActivity.HOME_ID);
        roomId = getIntent().getStringExtra(RoomActivity.ROOM_ID_EXTRA);

        FirebaseDatabaseInstance.getDeviceIdsWithHomeId(homeId, roomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mProgressBar.setVisibility(View.GONE);
                for (DataSnapshot s : snapshot.getChildren()) {
                    Log.v(TAG, "Found device with ID:" + s.getValue(String.class));
                    FirebaseDatabaseInstance.getDeviceWithDeviceId(s.getValue(String.class)).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Log.v(TAG, snapshot.toString());
                            int type = snapshot.child("deviceType").getValue(Integer.class);
                            String deviceId = snapshot.child("deviceId").getValue(String.class);

                            for (int i = 0; i < devices.size(); i++) {
                                if (devices.get(i).getDeviceId().equals(deviceId)) {
                                    devices.remove(i);
                                    break;
                                }
                            }

                            switch (type) {
                                case Device.FAN:
                                    devices.add(snapshot.getValue(Fan.class));
                                    break;
                                case Device.AIR_CONDITIONER:
                                    devices.add(snapshot.getValue(AirConditioner.class));
                                    break;
                                case Device.BOILER:
                                    devices.add(snapshot.getValue(Boiler.class));
                                    break;
                                case Device.SWITCH:
                                    devices.add(snapshot.getValue(Switch.class));
                                    break;
                                case Device.TEMPERATURE_SENSOR:
                                    devices.add(snapshot.getValue(TemperatureSensor.class));
                                    break;
                                default:
                                    break;
                            }
                            mDevicesRecyclerViewAdapter.setData(devices);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mDevicesRecyclerViewAdapter.setData(devices);
        mRecyclerView.setAdapter(mDevicesRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new

                LinearLayoutManager(this));
    }

    public static void showDialog(Device device, Context context) {

        switch (device.getDeviceType()) {
            case Device.FAN:
                showFanDialog((Fan) device, context);
                break;
            case Device.AIR_CONDITIONER:
                showAirConditionerDialog((AirConditioner) device, context);
                break;
            case Device.BOILER:
                showBoilerDialog((Boiler) device, context);
                break;
            case Device.SWITCH:
                showSwitchDialog((Switch) device, context);
                break;
            case Device.TEMPERATURE_SENSOR:
                showTemperatureDialog((TemperatureSensor) device, context);
                break;
            default:
                break;
        }
    }

    private static void showFanDialog(Fan fan, Context context) {
        Dialog dialog = new Dialog(context);

        LinearLayout view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.show_device, null);

        MaterialButton start = new MaterialButton(context);
        start.setText("Start");
        MaterialButton stop = new MaterialButton(context);
        stop.setText("Stop");


        TextView powerTextView = new TextView(context);
        powerTextView.setTextSize(16);

        SeekBar seekBar = new SeekBar(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekBar.setMin(0);
        }
        seekBar.setMax(100);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                fan.setPower(i);
                FirebaseDatabaseInstance.getDeviceWithDeviceId(fan.getDeviceId()).setValue(fan);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar.setProgress(fan.getPower());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        powerTextView.setLayoutParams(layoutParams);

        view.addView(powerTextView);
        view.addView(seekBar);
        view.addView(start);
        view.addView(stop);

        dialog.setContentView(view);
        dialog.show();

        ImageView imageView = dialog.findViewById(R.id.showDeviceImageView);
        imageView.setBackgroundResource(R.drawable.fan);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        FirebaseDatabaseInstance.getDeviceWithDeviceId(fan.getDeviceId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Fan data = snapshot.getValue(Fan.class);
                if (dialog.isShowing()) {
                    TextView connectionStateTextView = dialog.findViewById(R.id.connectionStateTextView);
                    TextView stateTextView = dialog.findViewById(R.id.stateTextView);

                    powerTextView.setText("Power: " + fan.getPower() + "%");

                    String state = fan.getState() ? "running" : "off";
                    stateTextView.setText(state);

                    if (data.getConnectionState() == Device.CONNECTION_OK) {
                        connectionStateTextView.setTextColor(Color.GREEN);
                    } else {
                        connectionStateTextView.setTextColor(Color.RED);
                    }
                    connectionStateTextView.setText(Device.stateToString(data.getConnectionState()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        start.setOnClickListener(view12 -> {
            Log.d(TAG, "Starting fan");
            fan.setState(true);
            FirebaseDatabaseInstance.getDeviceWithDeviceId(fan.getDeviceId()).setValue(fan);
        });

        stop.setOnClickListener(view1 -> {
            fan.setState(false);
            Log.d(TAG, fan.toString());
            FirebaseDatabaseInstance.getDeviceWithDeviceId(fan.getDeviceId()).setValue(fan);
        });
    }

    private static void showAirConditionerDialog(AirConditioner airConditioner, Context context) {
        Dialog dialog = new Dialog(context);

        LinearLayout view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.show_device, null);

        MaterialButton start = new MaterialButton(context);
        start.setText("Start");
        MaterialButton stop = new MaterialButton(context);
        stop.setText("Stop");

        TextView temperatureTextView = new TextView(context);
        temperatureTextView.setTextSize(16);

        SeekBar seekBar = new SeekBar(context);
        seekBar.setMax(30);
        seekBar.setMin(17);

        seekBar.setProgress(airConditioner.getTemperature());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                airConditioner.setTemperature(i);
                FirebaseDatabaseInstance.getDeviceWithDeviceId(airConditioner.getDeviceId()).setValue(airConditioner);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        temperatureTextView.setLayoutParams(layoutParams);

        view.addView(temperatureTextView);
        view.addView(seekBar);
        view.addView(start);
        view.addView(stop);

        dialog.setContentView(view);
        dialog.show();

        ImageView imageView = dialog.findViewById(R.id.showDeviceImageView);
        imageView.setBackgroundResource(R.drawable.air_conditioner);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        FirebaseDatabaseInstance.getDeviceWithDeviceId(airConditioner.getDeviceId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Fan data = snapshot.getValue(Fan.class);
                if (dialog.isShowing()) {
                    TextView connectionStateTextView = dialog.findViewById(R.id.connectionStateTextView);
                    TextView stateTextView = dialog.findViewById(R.id.stateTextView);

                    String state = airConditioner.getState() ? "running" : "off";
                    stateTextView.setText(state);

                    temperatureTextView.setText(airConditioner.getTemperature() + "°C");


                    if (data.getConnectionState() == Device.CONNECTION_OK) {
                        connectionStateTextView.setTextColor(Color.GREEN);
                    } else {
                        connectionStateTextView.setTextColor(Color.RED);
                    }
                    connectionStateTextView.setText(Device.stateToString(data.getConnectionState()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        start.setOnClickListener(view12 -> {
            Log.d(TAG, "Starting air conditioner");
            airConditioner.setState(true);
            FirebaseDatabaseInstance.getDeviceWithDeviceId(airConditioner.getDeviceId()).setValue(airConditioner);
        });

        stop.setOnClickListener(view1 -> {
            airConditioner.setState(false);
            Log.d(TAG, airConditioner.toString());
            FirebaseDatabaseInstance.getDeviceWithDeviceId(airConditioner.getDeviceId()).setValue(airConditioner);
        });
    }

    private static void showBoilerDialog(Boiler boiler, Context context) {
        Dialog dialog = new Dialog(context);

        LinearLayout view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.show_device, null);


        MaterialTextView waterAvailable = new MaterialTextView(context);
        waterAvailable.setTextSize(18);

        MaterialTextView waterHeated = new MaterialTextView(context);
        waterAvailable.setTextSize(18);

        LinearLayout.LayoutParams paramWater = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        paramWater.gravity = Gravity.CENTER;

        waterAvailable.setLayoutParams(paramWater);
        waterAvailable.setTextSize(18);

        waterHeated.setLayoutParams(paramWater);
        waterHeated.setTextSize(18);

        MaterialButton start = new MaterialButton(context);
        start.setText("Turn On");
        MaterialButton stop = new MaterialButton(context);
        stop.setText("Turn Off");

        view.addView(waterAvailable);
        view.addView(start);
        view.addView(stop);

        dialog.setContentView(view);
        dialog.show();

        ImageView imageView = dialog.findViewById(R.id.showDeviceImageView);
        imageView.setBackgroundResource(R.drawable.kettle);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        FirebaseDatabaseInstance.getDeviceWithDeviceId(boiler.getDeviceId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Fan data = snapshot.getValue(Fan.class);
                if (dialog.isShowing()) {
                    TextView connectionStateTextView = dialog.findViewById(R.id.connectionStateTextView);
                    TextView stateTextView = dialog.findViewById(R.id.stateTextView);

                    String state = boiler.getState() ? "running" : "off";
                    stateTextView.setText(state);

                    if (data.getConnectionState() == Device.CONNECTION_OK) {
                        connectionStateTextView.setTextColor(Color.GREEN);
                    } else {
                        connectionStateTextView.setTextColor(Color.RED);
                    }
                    connectionStateTextView.setText(Device.stateToString(data.getConnectionState()));

                    if (boiler.isWaterAvailable()) {
                        waterAvailable.setTextColor(Color.GREEN);
                        waterAvailable.setText("Water is available");
                    } else {
                        waterAvailable.setTextColor(Color.RED);
                        waterAvailable.setText("Water isn't present");
                    }

                    if (boiler.isHeatedUp()) {
                        waterHeated.setTextColor(Color.GREEN);
                        waterHeated.setText("Water is heated up");
                    } else {
                        waterHeated.setTextColor(Color.RED);
                        waterHeated.setText("Water isn't heated up");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        start.setOnClickListener(view12 -> {
            if (boiler.isWaterAvailable()) {
                Log.d(TAG, "Starting Boiler");
                boiler.setState(true);
                FirebaseDatabaseInstance.getDeviceWithDeviceId(boiler.getDeviceId()).setValue(boiler);
            } else {
                Toast.makeText(context, "Please fill in water", Toast.LENGTH_SHORT).show();
            }
        });

        stop.setOnClickListener(view1 -> {
            boiler.setState(false);
            Log.d(TAG, boiler.toString());
            FirebaseDatabaseInstance.getDeviceWithDeviceId(boiler.getDeviceId()).setValue(boiler);
        });
    }

    private static void showSwitchDialog(Switch device, Context context) {
        Dialog dialog = new Dialog(context);

        LinearLayout view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.show_device, null);

        MaterialButton start = new MaterialButton(context);
        start.setText("Switch On");
        MaterialButton stop = new MaterialButton(context);
        stop.setText("Switch Off");

        view.addView(start);
        view.addView(stop);

        dialog.setContentView(view);
        dialog.show();

        ImageView imageView = dialog.findViewById(R.id.showDeviceImageView);
        imageView.setBackgroundResource(R.drawable.plug);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        FirebaseDatabaseInstance.getDeviceWithDeviceId(device.getDeviceId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Fan data = snapshot.getValue(Fan.class);
                if (dialog.isShowing()) {
                    TextView connectionStateTextView = dialog.findViewById(R.id.connectionStateTextView);
                    TextView stateTextView = dialog.findViewById(R.id.stateTextView);

                    String state = device.getState() ? "running" : "off";
                    stateTextView.setText(state);

                    if (data.getConnectionState() == Device.CONNECTION_OK) {
                        connectionStateTextView.setTextColor(Color.GREEN);
                    } else {
                        connectionStateTextView.setTextColor(Color.RED);
                    }
                    connectionStateTextView.setText(Device.stateToString(data.getConnectionState()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        start.setOnClickListener(view12 -> {
            Log.d(TAG, "Starting switch");
            device.setState(true);
            FirebaseDatabaseInstance.getDeviceWithDeviceId(device.getDeviceId()).setValue(device);
        });

        stop.setOnClickListener(view1 -> {
            device.setState(false);
            Log.d(TAG, device.toString());
            FirebaseDatabaseInstance.getDeviceWithDeviceId(device.getDeviceId()).setValue(device);
        });
    }

    private static void showTemperatureDialog(TemperatureSensor temperatureSensor, Context context) {
        Dialog dialog = new Dialog(context);

        LinearLayout view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.show_device, null);

        MaterialButton start = new MaterialButton(context);
        start.setText("Start");
        MaterialButton stop = new MaterialButton(context);
        stop.setText("Stop");

        LinearLayout.LayoutParams paramsText = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        paramsText.gravity = Gravity.CENTER;

        MaterialTextView currentTemperature = new MaterialTextView(context);
        currentTemperature.setTextSize(18);
        currentTemperature.setLayoutParams(paramsText);

        view.addView(currentTemperature);
        view.addView(start);
        view.addView(stop);

        dialog.setContentView(view);
        dialog.show();

        ImageView imageView = dialog.findViewById(R.id.showDeviceImageView);
        imageView.setBackgroundResource(R.drawable.temperature);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        FirebaseDatabaseInstance.getDeviceWithDeviceId(temperatureSensor.getDeviceId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Fan data = snapshot.getValue(Fan.class);
                if (dialog.isShowing()) {
                    TextView connectionStateTextView = dialog.findViewById(R.id.connectionStateTextView);
                    TextView stateTextView = dialog.findViewById(R.id.stateTextView);

                    String state = temperatureSensor.getState() ? "running" : "off";
                    stateTextView.setText(state);

                    if (data.getConnectionState() == Device.CONNECTION_OK) {
                        connectionStateTextView.setTextColor(Color.GREEN);
                    } else {
                        connectionStateTextView.setTextColor(Color.RED);
                    }
                    connectionStateTextView.setText(Device.stateToString(data.getConnectionState()));
                    currentTemperature.setText("Current Temperature is: " + temperatureSensor.getTemperature() + "°C");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        start.setOnClickListener(view12 -> {
            Log.d(TAG, "Starting temperature sensor");
            temperatureSensor.setState(true);
            FirebaseDatabaseInstance.getDeviceWithDeviceId(temperatureSensor.getDeviceId()).setValue(temperatureSensor);
        });

        stop.setOnClickListener(view1 -> {
            temperatureSensor.setState(false);
            Log.d(TAG, temperatureSensor.toString());
            FirebaseDatabaseInstance.getDeviceWithDeviceId(temperatureSensor.getDeviceId()).setValue(temperatureSensor);
        });
    }

    public static void performLongClick(Device device, Context context, int pos) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).
                setTitle("Deleting Home").
                setMessage("Are you sure you want to remove " + device.getDeviceName() + "?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseDatabaseInstance.getRoomWithId(homeId, roomId).child("deviceIds").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot s : snapshot.getChildren()) {
                            Log.v(TAG, s.getValue(String.class));
                            if (s.getValue(String.class).equals(device.getDeviceId())) {
                                Log.v(TAG, s.getValue(String.class));
                                FirebaseDatabaseInstance.getRoomWithId(homeId, roomId).child("deviceIds").child(s.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(context, "Device Removed Successfully", Toast.LENGTH_SHORT).show();
                                            mDevicesRecyclerViewAdapter.notifyItemRemoved(pos);
                                        } else {
                                            Toast.makeText(context, "Failed to Remove Device", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }).setNeutralButton("No", null).
                create();

        alertDialog.show();

    }
}