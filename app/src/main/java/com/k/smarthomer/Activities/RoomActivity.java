package com.k.smarthomer.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.style.CubeGrid;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.k.smarthomer.Adapters.HomeRecyclerViewAdapter;
import com.k.smarthomer.Adapters.RoomRecyclerViewAdapter;
import com.k.smarthomer.Database.FirebaseDatabaseInstance;
import com.k.smarthomer.Models.Room;
import com.k.smarthomer.R;

import java.util.ArrayList;

import static com.k.smarthomer.Activities.MainActivity.HOME_NAME;

public class RoomActivity extends AppCompatActivity {
    public static final String ROOM_ID_EXTRA = "Room ID";

    private static final String TAG = "Room Activity";

    private TextView homeNameTextView;
    private RecyclerView mRecyclerView;
    private RoomRecyclerViewAdapter mRoomRecyclerViewAdapter;
    private ImageView homeImage;
    private FloatingActionButton fab;
    private ProgressBar mProgressBar;

    private FirebaseAuth mAuth;

    private static String homeId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        homeId = getIntent().getStringExtra(MainActivity.HOME_ID);

        initViews();
        mAuth = FirebaseAuth.getInstance();
        checkUserLoggedIn();
        initAdapter();


        //testRoom();
        //testHome();
    }

    private void initViews() {
        homeNameTextView = findViewById(R.id.homeNameEditText);
        homeImage = findViewById(R.id.homeImageView);
        fab = findViewById(R.id.fabRoomActivity);

        String transitionName = getIntent().getStringExtra(MainActivity.ANIMATION_TRANSITION_IMAGE);
        homeImage.setTransitionName(transitionName);
        homeNameTextView.setTransitionName(transitionName + HomeRecyclerViewAdapter.RANDOM_TRANS_ID);
        homeNameTextView.setText(getIntent().getStringExtra(HOME_NAME));

        fab.setOnClickListener(view -> {
            showAddRoomDialog();
        });

        mProgressBar = findViewById(R.id.roomActivityProgressBar);
        // TODO fix progress bar to visible
        mProgressBar.setVisibility(View.GONE);
        CubeGrid doubleBounce = new CubeGrid();
        mProgressBar.setIndeterminateDrawable(doubleBounce);
    }

    private void showAddRoomDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_room_dialog);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        EditText editText = dialog.findViewById(R.id.roomNameEditText);
        dialog.findViewById(R.id.roomAddButton).setOnClickListener(view -> {
            String roomName = editText.getText().toString();
            if (roomName.isEmpty()) editText.setError("Must Enter Room Name");
            else {
                String roomId = FirebaseDatabaseInstance.getRoomsWithHomeId(homeId).push().getKey();
                Room room = new Room(roomId, roomName, null);
                FirebaseDatabaseInstance.getRoomsWithHomeId(homeId).child(roomId).setValue(room).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            showToast("Room Added Successfully");
                        } else {
                            showToast("Adding Room Failed");
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    private void showToast(String message) {
        Toast.makeText(RoomActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void initAdapter() {
        mRecyclerView = findViewById(R.id.roomRecyclerView);
        mRoomRecyclerViewAdapter = new RoomRecyclerViewAdapter();
        ArrayList<Room> rooms = new ArrayList<>();
        mRoomRecyclerViewAdapter.setData(rooms);
        mRecyclerView.setAdapter(mRoomRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseDatabaseInstance.getRoomsWithHomeId(homeId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mProgressBar.setVisibility(View.GONE);
                rooms.clear();
                for (DataSnapshot s : snapshot.getChildren()) {
                    rooms.add(s.getValue(Room.class));
                    mRoomRecyclerViewAdapter.setData(rooms);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkUserLoggedIn() {
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    public static void performClick(Room room, Context context) {
        Intent intent = new Intent((Activity) context, DeviceActivity.class);
        intent.putExtra(ROOM_ID_EXTRA, room.getRoomId());
        intent.putExtra(MainActivity.HOME_ID, homeId);
        context.startActivity(intent);
    }

    public static void performLongClick(Room room, Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).
                setTitle("Deleting Room").
                setMessage("Are you sure you want to remove " + room.getRoomName() + "?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseDatabaseInstance.getRoomWithId(homeId, room.getRoomId()).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Room Removed Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to Remove Room", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }).setNeutralButton("No", null).
                create();

        alertDialog.show();
    }
}