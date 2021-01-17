package com.k.smarthomer.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.ybq.android.spinkit.style.CubeGrid;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.k.smarthomer.Database.FirebaseDatabaseInstance;
import com.k.smarthomer.Models.User;
import com.k.smarthomer.R;
import com.k.smarthomer.Utils.PasswordAuthentication;

public class RegisterActivity extends AppCompatActivity {
    private EditText firstNameEditText, secondNameEditText, emailEditText, passwordEditText;
    private Button signUpButton;
    private ProgressBar mProgressBar;

    private FirebaseAuth mAuth;
    private static final String TAG = "Register Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
        mAuth = FirebaseAuth.getInstance();
    }

    private void initViews() {
        firstNameEditText = findViewById(R.id.firstNameEditText);
        secondNameEditText = findViewById(R.id.secondNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signUpButton = findViewById(R.id.signUpButton);
        mProgressBar = findViewById(R.id.signUpProgressBar);
        CubeGrid doubleBounce = new CubeGrid();
        mProgressBar.setIndeterminateDrawable(doubleBounce);

        signUpButton.setOnClickListener(view -> signUp());
    }

    private void signUp() {
        String firstName, secondName, email, password;
        firstName = firstNameEditText.getText().toString();
        secondName = secondNameEditText.getText().toString();
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();

        if (firstName.isEmpty()) {
            firstNameEditText.setError("must set first name");
        } else if (secondName.isEmpty()) {
            secondNameEditText.setError("must set second name");
        } else if (email.isEmpty()) {
            emailEditText.setError("must set email");
        } else if (password.isEmpty()) {
            passwordEditText.setError("must set password");
        } else if (password.length() < 6) {
            passwordEditText.setError("password too short");
        } else {
            // passed all tests
            register(email, password, firstName, secondName);
        }
    }

    private void register(String email, String password, String firstName, String secondName) {
        mProgressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mProgressBar.setVisibility(View.GONE);
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();

                    String uid = user.getUid();

                    DatabaseReference database = FirebaseDatabaseInstance.getUsersDatabase();

                    PasswordAuthentication hashing = new PasswordAuthentication();
                    String hashedPassword = hashing.hash(password);

                    User userObj = new User(firstName, secondName, email, hashedPassword, null);

                    database.child(uid).setValue(userObj);
                    finish();
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));

                } else {
                    mProgressBar.setVisibility(View.GONE);
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
