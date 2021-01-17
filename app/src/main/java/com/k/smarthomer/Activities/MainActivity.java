package com.k.smarthomer.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.style.CubeGrid;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.k.smarthomer.Adapters.HomeRecyclerViewAdapter;
import com.k.smarthomer.Database.FirebaseDatabaseInstance;
import com.k.smarthomer.Models.Home;
import com.k.smarthomer.Models.Room;
import com.k.smarthomer.R;

import java.util.ArrayList;
import java.util.HashMap;

import static com.k.smarthomer.Database.FirebaseDatabaseInstance.HOMES;
import static com.k.smarthomer.Database.FirebaseDatabaseInstance.ROOMS;
import static com.k.smarthomer.Database.FirebaseDatabaseInstance.getHomesDatabase;
import static com.k.smarthomer.Database.FirebaseDatabaseInstance.getHomesWithUserId;
import static com.k.smarthomer.Database.FirebaseDatabaseInstance.getRoomsWithHomeId;
import static com.k.smarthomer.Database.FirebaseDatabaseInstance.getRootDatabase;
import static com.k.smarthomer.Database.FirebaseDatabaseInstance.getUserById;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main Activity";
    public static final String ANIMATION_TRANSITION_IMAGE = "IMAGE_ANIMATION";
    public static final String HOME_ID = "Home id";
    public static final String HOME_NAME = "Home Name";

    private FloatingActionButton fab;
    private RecyclerView mRecyclerView;
    private HomeRecyclerViewAdapter mHomeRecyclerViewAdapter;
    private ProgressBar mProgressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        checkUserLoggedIn();
        //testHome();
        //testRoom();
    }

    private void initViews() {
        mProgressBar = findViewById(R.id.progressBarMainActivity);
        CubeGrid doubleBounce = new CubeGrid();
        mProgressBar.setIndeterminateDrawable(doubleBounce);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> startAddDialog());
    }

    private void initAdapter() {
        mRecyclerView = findViewById(R.id.homeRecyclerView);
        mHomeRecyclerViewAdapter = new HomeRecyclerViewAdapter();
        ArrayList<Home> homes = new ArrayList<>();

        mHomeRecyclerViewAdapter.setData(homes);
        mRecyclerView.setAdapter(mHomeRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getHomesWithUserId(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mProgressBar.setVisibility(View.GONE);
                homes.clear();
                for (DataSnapshot s : snapshot.getChildren()) {
                    getHomesDatabase().child(s.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (homes != null) {
                                for (int i = 0; i < homes.size(); i++) {
                                    if (s.getKey().equals(homes.get(i).getHomeId())) {
                                        homes.remove(i);
                                    }
                                }
                            }
                            homes.add(snapshot.getValue(Home.class));

                            mHomeRecyclerViewAdapter.setData(homes);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            showToast(error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void startAddDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_dialog);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();

        View homeCard = dialog.findViewById(R.id.homeCard);
        homeCard.setOnClickListener(view -> {
            LayoutInflater li = LayoutInflater.from(MainActivity.this);
            LinearLayout v = (LinearLayout) li.inflate(R.layout.add_home_dialog, null);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(16, 0, 16, 8);

            dialog.setContentView(v);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.findViewById(R.id.homeAddButton).setOnClickListener(view1 -> {
                Log.d(TAG, "add button clicked!");
                EditText homeIdEditText = dialog.findViewById(R.id.homeIdEditText);
                EditText homePinEditText = dialog.findViewById(R.id.homePinEditText);

                String id = homeIdEditText.getText().toString();
                String pin = homePinEditText.getText().toString();
                if (id.isEmpty()) homeIdEditText.setError("Must Enter Id");
                else if (pin.isEmpty()) homePinEditText.setError("Must Enter Pin");
                else {
                    addHomeToUser(id, pin);
                }
            });

            dialog.findViewById(R.id.homeCreateButton).setOnClickListener(view12 -> {
                dialog.setContentView(R.layout.create_home_dialog);
                dialog.findViewById(R.id.createHomeButtonFinal).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText homeNameEditText = dialog.findViewById(R.id.homeNameCreateEditText);
                        EditText homePinEditText = dialog.findViewById(R.id.homePinCreateEditText);

                        String name = homeNameEditText.getText().toString();
                        String pin = homePinEditText.getText().toString();
                        if (name.isEmpty()) homeNameEditText.setError("Must Enter Name");
                        else if (pin.isEmpty()) homePinEditText.setError("Must Enter Pin");
                        else {
                            String id = FirebaseDatabaseInstance.getHomesDatabase().push().getKey();
                            Home home = new Home();
                            home.setHomeId(id);
                            home.setHomeName(name);
                            home.setPinNumber(pin);
                            FirebaseDatabaseInstance.getHomesDatabase().child(id).setValue(home);
                            FirebaseDatabaseInstance.getHomesWithUserId(mAuth.getUid()).child(id).setValue(id).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    showToast("Home Successfully Created");
                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                });
            });
        });
    }

    private void addHomeToUser(String id, String pin) {
        getHomesDatabase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(id)) {
                    if (snapshot.child(id).getValue(Home.class).getPinNumber().equals(pin)) {
                        getUserById(mAuth.getUid()).child(HOMES).child(id).setValue(id).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) showToast("Home Added Successfully!");
                            }
                        });
                    } else {
                        showToast("PIN doesn't math");
                    }
                } else {
                    showToast("Home doesn't exist in our database, please contact the support");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void checkUserLoggedIn() {
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        } else{
            initViews();
            initAdapter();
            //DatabaseDummies.createDevices();
        }
    }

    private void testRoom() {
        DatabaseReference databaseReference = getRootDatabase();
        ArrayList<String> dummyIds = new ArrayList<>();
        dummyIds.add("0");
        dummyIds.add("1");
        Room room = new Room("05454", "test room", dummyIds);
        getRoomsWithHomeId("800").child(room.getRoomId()).setValue(room, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                databaseReference.child(ROOMS).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Room r = snapshot.getValue(Room.class);
                        Log.d(TAG, "Adding rooms data...");
                        Log.d(TAG, snapshot.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }

    private void testHome() {
        DatabaseReference databaseReference = getRootDatabase();

        ArrayList<String> dummyId = new ArrayList<>();
        dummyId.add("454d45");
        dummyId.add("ffafa");

        Room room = new Room("05454", "Living Room", dummyId);
        Room room2 = new Room("5254", "Khaled's Room", dummyId);

        HashMap<String, Room> map = new HashMap();
        map.put("05454", room);
        map.put("5254", room2);

        Home home = new Home("54", "508", "home sweet home", map);
        databaseReference.child(HOMES).child(home.getHomeId()).setValue(home, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                databaseReference.child(HOMES).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }

    public static void performClick(Home home, Context context, int imageId, int textViewId) {
        Log.d(TAG, home.getHomeName() + " clicked");

        ImageView imageView = ((Activity) context).findViewById(imageId);
        imageView.setTransitionName(home.getHomeId());

        TextView textView = ((Activity) context).findViewById(textViewId);
        textView.setTransitionName(home.getHomeId() + HomeRecyclerViewAdapter.RANDOM_TRANS_ID);

        Pair<View, String> p1 = Pair.create(imageView, ViewCompat.getTransitionName(imageView));
        Pair<View, String> p2 = Pair.create(textView, ViewCompat.getTransitionName(textView));

        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation((Activity) context, p1, p2);

        Intent intent = new Intent(context, RoomActivity.class);
        intent.putExtra(HOME_NAME, home.getHomeName());
        intent.putExtra(ANIMATION_TRANSITION_IMAGE, home.getHomeId());
        intent.putExtra(HOME_ID, home.getHomeId());

        context.startActivity(intent, options.toBundle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);

        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logoutOption:
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                break;
        }
        return true;
    }

    public static void performLongClick(Context context, Home home) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).
                setTitle("Deleting Home").
                setMessage("Are you sure you want to remove " + home.getHomeName() + "?").setPositiveButton("Yes", (dialogInterface, i) -> {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    FirebaseDatabaseInstance.getHomesWithUserId(uid).child(home.getHomeId()).removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Home Removed Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to Remove Home", Toast.LENGTH_SHORT).show();

                        }
                    });
                }).setNeutralButton("No", null).
                create();

        alertDialog.show();
    }

}